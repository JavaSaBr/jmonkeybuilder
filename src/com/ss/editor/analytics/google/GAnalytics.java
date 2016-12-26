package com.ss.editor.analytics.google;

import static org.apache.http.impl.client.HttpClients.createMinimal;
import static rlib.util.StringUtils.isEmpty;

import com.ss.editor.EditorThread;

import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import rlib.concurrent.util.ConcurrentUtils;
import rlib.logging.Logger;
import rlib.logging.LoggerManager;
import rlib.util.StringUtils;
import rlib.util.linkedlist.LinkedList;
import rlib.util.linkedlist.LinkedListFactory;

/**
 * @author JavaSaBr
 */
public class GAnalytics extends EditorThread {

    private static final Logger LOGGER = LoggerManager.getLogger(GAnalytics.class);

    public static final String PARAM_PROTOCOL_VERSION = "v";
    public static final String PARAM_TRACKING_ID = "tid";
    public static final String PARAM_CLIENT_ID = "cid";
    public static final String PARAM_HIT_TYPE = "t";
    public static final String PARAM_DATA_SOURCE = "ds";
    public static final String PARAM_QUEUE_TIME = "qt";
    public static final String PARAM_CACHE_BUSTER = "z";
    public static final String PARAM_USER_ID = "uid";
    public static final String PARAM_SESSION_CONTROL = "sc";
    public static final String PARAM_SCREEN_RESOLUTION = "sr";
    public static final String PARAM_USER_LANGUAGE = "ul";
    public static final String PARAM_APPLICATION_VERSION = "av";
    public static final String PARAM_EVENT_CATEGORY = "ec";
    public static final String PARAM_EVENT_ACTION = "ea";
    public static final String PARAM_EVENT_LABEL = "el";
    public static final String PARAM_EVENT_VALUE = "ev";
    public static final String PARAM_USER_TIMING_CATEGORY = "utc";
    public static final String PARAM_USER_TIMING_VAR_NAME = "utv";
    public static final String PARAM_USER_TIMING_TIME = "utt";
    public static final String PARAM_USER_TIMING_LABEL = "utl";
    public static final String PARAM_EXCEPTION_DESCRIPTION = "exd";
    public static final String PARAM_IS_EXCEPTION_FATAL = "exf";
    public static final String PARAM_CUSTOM_DIMENSION = "cd";

    public static final String FIELD_OS = PARAM_CUSTOM_DIMENSION + "1";
    public static final String FIELD_GRAPHICS_ADAPTER = PARAM_CUSTOM_DIMENSION + "2";

    public static final String PROP_ANALYTICS_HOST = "http://www.google-analytics.com/collect";
    public static final String PROP_TRACKING_ID = "UA-89459340-1";
    public static final String PROP_CLIENT_ID = "89459340";

    private static final GAnalytics INSTANCE = new GAnalytics();

    public static GAnalytics getInstance() {
        return INSTANCE;
    }

    public static void waitForSend() {
        final GAnalytics instance = getInstance();
        final AtomicInteger progressCount = instance.progressCount;
        if(progressCount.get() < 1) return;
        ConcurrentUtils.wait(progressCount, 2000);
    }

    public static void sendEvent(@NotNull final String category, @NotNull final String action) {
        sendEvent(category, action, null, null);
    }

    public static void sendEvent(@NotNull final String category, @NotNull final String action, @Nullable final String label) {
        sendEvent(category, action, label, null);
    }

    public static void sendEvent(@NotNull final String category, @NotNull final String action, @Nullable final String label, @Nullable final String value) {

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_EVENT_CATEGORY, category);
        parameters.put(PARAM_EVENT_ACTION, action);

        if(!isEmpty(label)) parameters.put(PARAM_EVENT_LABEL, label);
        if(!isEmpty(value)) parameters.put(PARAM_EVENT_VALUE, value);

        send(HitType.EVENT, parameters);
    }

    /**
     * Send an exception.
     *
     * @param exception the exception.
     * @param fatal true if the exception is fatal.
     */
    public static void sendException(@NotNull final Throwable exception, final boolean fatal) {

        final StringWriter writer = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(writer);

        exception.printStackTrace(printWriter);

        final String localizedMessage = exception.getLocalizedMessage();
        final String stackTrace = writer.toString();

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_EXCEPTION_DESCRIPTION, localizedMessage + ":\n" + stackTrace);
        parameters.put(PARAM_IS_EXCEPTION_FATAL, fatal);

        send(HitType.EXCEPTION, parameters);
    }

    private static void send(final HitType hitType, final Map<String, Object> parameters) {
        parameters.put(PARAM_HIT_TYPE, hitType.toString());
        send(parameters);
    }

    private static void send(final Map<String, Object> parameters) {
        getInstance().addTask(() -> doSend(parameters));
    }

    private static void doSend(final Map<String, Object> parameters) {

        String os = null;
        String graphicsAdapter = null;

        final GAnalytics instance = getInstance();
        final AtomicInteger progressCount = instance.progressCount;

        System.out.println("start sending " + parameters);

        try(final CloseableHttpClient httpClient = createMinimal()) {

            parameters.put(PARAM_PROTOCOL_VERSION, "1");
            parameters.put(PARAM_TRACKING_ID, PROP_TRACKING_ID);
            parameters.put(PARAM_CLIENT_ID, PROP_CLIENT_ID);

            if(!StringUtils.isEmpty(os)) parameters.put(FIELD_OS, os);
            if(!StringUtils.isEmpty(graphicsAdapter)) parameters.put(FIELD_GRAPHICS_ADAPTER, graphicsAdapter);

            final String stringParameters = buildParameters(parameters);
            final byte[] byteParameters = stringParameters.getBytes("UTF-8");

            final HttpPost post = new HttpPost(PROP_ANALYTICS_HOST);
            post.setEntity(new ByteArrayEntity(byteParameters));

            final CloseableHttpResponse response = httpClient.execute(post);
            final StatusLine statusLine = response.getStatusLine();

            if (statusLine.getStatusCode() != 200) {
                LOGGER.warning("failed analytics request: " + response);
            }

            System.out.println("finished sending.");

        } catch (final IOException e) {
            LOGGER.warning(e);
        } finally {
            progressCount.decrementAndGet();
            ConcurrentUtils.notifyAll(progressCount);
        }
    }

    private static String buildParameters(final Map<String, Object> parameters) {

        final StringBuilder builder = new StringBuilder();

        parameters.forEach((key, value) -> appendParam(builder, key, value));

        return builder.toString();
    }

    private static void appendParam(final StringBuilder builder, final String key, final Object value) {
        if(value == null) return;
        else if(builder.length() > 1) {
            builder.append('&');
        }

        builder.append(key).append('=');

        if(value instanceof String) {
            try {
                builder.append(URLEncoder.encode((String) value, "UTF-8"));
            } catch (final UnsupportedEncodingException e) {
                builder.append((String) value);
            }
        }
    }

    /**
     * The queue with tasks.
     */
    @NotNull
    private final LinkedList<Runnable> queue;

    /**
     * The count of sending.
     */
    private final AtomicInteger progressCount;

    public GAnalytics() {
        setName("GAnalytics Thread");
        this.queue = LinkedListFactory.newLinkedList(Runnable.class);
        this.progressCount = new AtomicInteger();
        start();
    }

    /**
     * Add a new task for executing.
     *
     * @param runnable a new task.
     */
    private void addTask(@NotNull final Runnable runnable) {
        progressCount.incrementAndGet();
        synchronized (queue) {
            queue.add(runnable);
            ConcurrentUtils.notifyAllInSynchronize(queue);
        }
    }

    @Override
    public void run() {
        for(;;) {

            Runnable next = null;

            synchronized (queue) {
                next = queue.pollFirst();
                if(next == null) {
                    ConcurrentUtils.waitInSynchronize(queue);
                    continue;
                }
            }

            try {
                next.run();
            } catch (final Exception e) {
                LOGGER.warning(e.getMessage(), e);
            }
        }
    }
}
