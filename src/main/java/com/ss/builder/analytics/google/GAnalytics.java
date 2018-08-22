package com.ss.builder.analytics.google;

import static com.ss.editor.config.DefaultSettingsProvider.Defaults.PREF_DEFAULT_ANALYTICS_GOOGLE;
import static com.ss.editor.config.DefaultSettingsProvider.Preferences.PREF_ANALYTICS_GOOGLE;
import static com.ss.rlib.common.util.StringUtils.isEmpty;
import static org.apache.http.impl.client.HttpClients.createMinimal;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.editor.EditorThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.config.Config;
import com.ss.editor.config.EditorConfig;
import com.ss.rlib.common.concurrent.util.ConcurrentUtils;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.util.StringUtils;
import com.ss.rlib.common.util.Utils;
import com.ss.rlib.common.util.linkedlist.LinkedList;
import com.ss.rlib.common.util.linkedlist.LinkedListFactory;
import com.ss.rlib.common.util.os.OperatingSystem;
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
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The implementation of a client to work with Google Analytics.
 *
 * @author JavaSaBr
 */
public class GAnalytics extends EditorThread {

    @NotNull
    private static final Logger LOGGER = LoggerManager.getLogger(GAnalytics.class);

    private static final String PARAM_PROTOCOL_VERSION = "v";
    private static final String PARAM_TRACKING_ID = "tid";
    private static final String PARAM_CLIENT_ID = "cid";
    private static final String PARAM_HIT_TYPE = "t";
    private static final String PARAM_EVENT_CATEGORY = "ec";
    private static final String PARAM_EVENT_ACTION = "ea";
    private static final String PARAM_EVENT_LABEL = "el";
    private static final String PARAM_PAGE_VIEW_LOCATION = "dl";
    private static final String PARAM_PAGE_VIEW_TITLE = "dt";
    private static final String PARAM_PAGE_VIEW_PAGE = "dp";
    private static final String PARAM_USER_TIMING_CATEGORY = "utc";
    private static final String PARAM_USER_TIMING_VAR_NAME = "utv";
    private static final String PARAM_USER_TIMING_TIME = "utt";
    private static final String PARAM_USER_TIMING_LABEL = "utl";
    private static final String PARAM_EXCEPTION_DESCRIPTION = "exd";
    private static final String PARAM_IS_EXCEPTION_FATAL = "exf";
    private static final String PARAM_CUSTOM_DIMENSION = "cd";

    private static final String FIELD_OS = PARAM_CUSTOM_DIMENSION + "1";
    private static final String FIELD_APP_VERSION = PARAM_CUSTOM_DIMENSION + "6";
    private static final String FIELD_LOCALE = PARAM_CUSTOM_DIMENSION + "3";
    private static final String FIELD_USER_ID = PARAM_CUSTOM_DIMENSION + "5";
    private static final String FIELD_JAVA_VERSION = PARAM_CUSTOM_DIMENSION + "7";

    private static final String PROP_ANALYTICS_HOST = "http://www.google-analytics.com/collect";
    private static final String PROP_TRACKING_ID = "UA-89459340-1";
    private static final String PROP_CLIENT_ID = "89459340";

    @NotNull
    private static final EditorConfig EDITOR_CONFIG = EditorConfig.getInstance();

    @NotNull
    private static final GAnalytics INSTANCE = new GAnalytics();

    @FromAnyThread
    public static @NotNull GAnalytics getInstance() {
        return INSTANCE;
    }

    /**
     * Wait for sending max 2 sec.
     */
    @FromAnyThread
    public static void waitForSend() {

        if (!EDITOR_CONFIG.getBoolean(PREF_ANALYTICS_GOOGLE, PREF_DEFAULT_ANALYTICS_GOOGLE)) {
            return;
        }

        final GAnalytics instance = getInstance();
        final AtomicInteger progressCount = instance.progressCount;

        if (progressCount.get() < 1) {
            return;
        }

        ConcurrentUtils.wait(progressCount, 2000);
    }

    /**
     * Send the event.
     *
     * @param category the category.
     * @param action   the action.
     */
    @FromAnyThread
    public static void sendEvent(@NotNull final String category, @NotNull final String action) {
        if (EDITOR_CONFIG.getBoolean(PREF_ANALYTICS_GOOGLE, PREF_DEFAULT_ANALYTICS_GOOGLE)) {
            sendEvent(category, action, null);
        }
    }

    /**
     * Send the event.
     *
     * @param category the category.
     * @param action   the action.
     * @param label    the label.
     */
    @FromAnyThread
    public static void sendEvent(@NotNull final String category, @NotNull final String action,
                                 @Nullable final String label) {

        if (!EDITOR_CONFIG.getBoolean(PREF_ANALYTICS_GOOGLE, PREF_DEFAULT_ANALYTICS_GOOGLE)) {
            return;
        }

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_EVENT_CATEGORY, category);
        parameters.put(PARAM_EVENT_ACTION, action);

        if (!isEmpty(label)) {
            parameters.put(PARAM_EVENT_LABEL, label);
        }

        send(HitType.EVENT, parameters);
    }

    /**
     * Send the event with ignoring disabling GA.
     *
     * @param category the category.
     * @param action   the action.
     * @param label    the label.
     */
    @FromAnyThread
    public static void forceSendEvent(@NotNull final String category, @NotNull final String action,
                                      @Nullable final String label) {

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_EVENT_CATEGORY, category);
        parameters.put(PARAM_EVENT_ACTION, action);

        if (!isEmpty(label)) {
            parameters.put(PARAM_EVENT_LABEL, label);
        }

        send(HitType.EVENT, parameters);
    }

    /**
     * Send the exception.
     *
     * @param exception the exception.
     * @param fatal     true if the exception is fatal.
     */
    @FromAnyThread
    public static void sendException(@NotNull final Throwable exception, final boolean fatal) {

        if (!EDITOR_CONFIG.getBoolean(PREF_ANALYTICS_GOOGLE, PREF_DEFAULT_ANALYTICS_GOOGLE)) {
            return;
        }

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

    /**
     * Send the page view event.
     *
     * @param title    the title.
     * @param location the location.
     * @param page     the page.
     */
    @FromAnyThread
    public static void sendPageView(@NotNull final String title, @Nullable final String location,
                                    @Nullable final String page) {

        if (!EDITOR_CONFIG.getBoolean(PREF_ANALYTICS_GOOGLE, PREF_DEFAULT_ANALYTICS_GOOGLE)) {
            return;
        }

        final Map<String, Object> parameters = new HashMap<>();
        if (!isEmpty(title)) parameters.put(PARAM_PAGE_VIEW_TITLE, title);
        if (!isEmpty(location)) parameters.put(PARAM_PAGE_VIEW_LOCATION, location);
        if (!isEmpty(page)) parameters.put(PARAM_PAGE_VIEW_PAGE, page);

        send(HitType.PAGE_VIEW, parameters);
    }

    /**
     * Send the timing stats.
     *
     * @param timingCategory the category.
     * @param timingVar      the variable.
     * @param timingValue    the value.
     * @param timingLabel    the label.
     */
    @FromAnyThread
    public static void sendTiming(@NotNull final String timingCategory, @NotNull final String timingVar,
                                  final int timingValue, @Nullable final String timingLabel) {

        if (!EDITOR_CONFIG.getBoolean(PREF_ANALYTICS_GOOGLE, PREF_DEFAULT_ANALYTICS_GOOGLE)) {
            return;
        }

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_USER_TIMING_CATEGORY, timingCategory);
        parameters.put(PARAM_USER_TIMING_VAR_NAME, timingVar);
        parameters.put(PARAM_USER_TIMING_TIME, timingValue);

        if (!isEmpty(timingLabel)) {
            parameters.put(PARAM_USER_TIMING_LABEL, timingLabel);
        }

        send(HitType.TIMING, parameters);
    }

    /**
     * Send the analytic event.
     *
     * @param hitType    the hit type.
     * @param parameters the parameters.
     */
    private static void send(@NotNull final HitType hitType, @NotNull final Map<String, Object> parameters) {
        parameters.put(PARAM_HIT_TYPE, hitType.toString());
        send(parameters);
    }

    /**
     * Send the analytic event.
     *
     * @param parameters the parameters.
     */
    private static void send(@NotNull final Map<String, Object> parameters) {
        getInstance().addTask(() -> doSend(parameters));
    }

    /**
     * Process of sending the analytic event.
     *
     * @param parameters the parameters.
     */
    private static void doSend(@NotNull final Map<String, Object> parameters) {

        final OperatingSystem operatingSystem = Config.getOperatingSystem();

        final String distribution = operatingSystem.getDistribution();
        final String os = StringUtils.isEmpty(distribution) ? operatingSystem.getName() + " " + operatingSystem.getVersion() : distribution;
        final String appVersion = Config.STRING_VERSION;
        final String language = Locale.getDefault().toString();
        final String userId = Utils.getUserName();
        final String javaVersion = System.getProperty("java.vm.name", "unknown") + " " +
                System.getProperty("java.version", "unknown");

        final GAnalytics instance = getInstance();
        final AtomicInteger progressCount = instance.progressCount;

        try (final CloseableHttpClient httpClient = createMinimal()) {

            parameters.put(PARAM_PROTOCOL_VERSION, "1");
            parameters.put(PARAM_TRACKING_ID, PROP_TRACKING_ID);
            parameters.put(PARAM_CLIENT_ID, PROP_CLIENT_ID);

            if (EDITOR_CONFIG.getBoolean(PREF_ANALYTICS_GOOGLE, PREF_DEFAULT_ANALYTICS_GOOGLE)) {
                if (!StringUtils.isEmpty(userId)) parameters.put(FIELD_USER_ID, userId);
            }

            if (!StringUtils.isEmpty(os)) parameters.put(FIELD_OS, os);
            if (!StringUtils.isEmpty(appVersion)) parameters.put(FIELD_APP_VERSION, appVersion);
            if (!StringUtils.isEmpty(language)) parameters.put(FIELD_LOCALE, language);
            if (!StringUtils.isEmpty(javaVersion)) parameters.put(FIELD_JAVA_VERSION, javaVersion);

            final String stringParameters = buildParameters(parameters);
            final byte[] byteParameters = stringParameters.getBytes("UTF-8");

            final HttpPost post = new HttpPost(PROP_ANALYTICS_HOST);
            post.setEntity(new ByteArrayEntity(byteParameters));

            final CloseableHttpResponse response = httpClient.execute(post);
            final StatusLine statusLine = response.getStatusLine();

            if (statusLine.getStatusCode() != 200) {
                LOGGER.warning("failed analytics request: " + response);
            }

        } catch (final IOException e) {
            // ignore the exception
        } finally {
            progressCount.decrementAndGet();
            ConcurrentUtils.notifyAll(progressCount);
        }
    }

    private static @NotNull String buildParameters(@NotNull final Map<String, Object> parameters) {
        final StringBuilder builder = new StringBuilder();
        parameters.forEach((key, value) -> appendParam(builder, key, value));
        return builder.toString();
    }

    private static void appendParam(@NotNull final StringBuilder builder,
                                    @NotNull final String key,
                                    @Nullable final Object value) {

        if (value == null) return;
        else if (builder.length() > 1) {
            builder.append('&');
        }

        builder.append(key).append('=');

        if (value instanceof String) {
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
    @NotNull
    private final AtomicInteger progressCount;

    private GAnalytics() {
        setName("GAnalytics Thread");
        this.queue = LinkedListFactory.newLinkedList(Runnable.class);
        this.progressCount = new AtomicInteger();
        start();
    }

    /**
     * Add the new task to execute.
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
        while(true) {

            Runnable next;

            synchronized (queue) {
                next = queue.pollFirst();
                if (next == null) {
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
