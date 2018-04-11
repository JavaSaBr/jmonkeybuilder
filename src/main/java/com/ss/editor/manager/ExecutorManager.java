package com.ss.editor.manager;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.executor.EditorTaskExecutor;
import com.ss.editor.executor.impl.BackgroundEditorTaskExecutor;
import com.ss.editor.executor.impl.FxEditorTaskExecutor;
import com.ss.editor.executor.impl.JmeThreadExecutor;
import com.ss.rlib.concurrent.atomic.AtomicInteger;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The class to manage executing some tasks in the some threads.
 *
 * @author JavaSaBr
 */
public class ExecutorManager {

    @NotNull
    private static final Logger LOGGER = LoggerManager.getLogger(ExecutorManager.class);

    @NotNull
    private static final Runtime RUNTIME = Runtime.getRuntime();

    private static final int PROP_BACKGROUND_TASK_EXECUTORS = RUNTIME.availableProcessors();

    @Nullable
    private static ExecutorManager instance;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    @NotNull
    public static ExecutorManager getInstance() {
        if (instance == null) instance = new ExecutorManager();
        return instance;
    }

    /**
     * The service to execute tasks using schedule.
     */
    @NotNull
    private final ScheduledExecutorService scheduledExecutorService;

    /**
     * The list of background tasks executors.
     */
    @NotNull
    private final EditorTaskExecutor[] backgroundTaskExecutors;

    /**
     * The executor of editor tasks.
     */
    @NotNull
    private final JmeThreadExecutor jmeTasksExecutor;

    /**
     * The executor of javaFX tasks.
     */
    @NotNull
    private final EditorTaskExecutor fxEditorTaskExecutor;

    /**
     * The index of a next background executor.
     */
    @NotNull
    private final AtomicInteger nextBackgroundTaskExecutor;

    private ExecutorManager() {

        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        this.backgroundTaskExecutors = new EditorTaskExecutor[PROP_BACKGROUND_TASK_EXECUTORS];

        for (int i = 0, length = backgroundTaskExecutors.length; i < length; i++) {
            backgroundTaskExecutors[i] = new BackgroundEditorTaskExecutor(i + 1);
        }

        this.jmeTasksExecutor = JmeThreadExecutor.getInstance();
        this.fxEditorTaskExecutor = new FxEditorTaskExecutor();

        this.nextBackgroundTaskExecutor = new AtomicInteger(0);

        LOGGER.debug("initialized.");
    }

    /**
     * Add a new background task.
     *
     * @param task the background task.
     */
    @FromAnyThread
    public void addBackgroundTask(@NotNull final Runnable task) {

        final EditorTaskExecutor[] executors = getBackgroundTaskExecutors();
        final AtomicInteger nextTaskExecutor = getNextBackgroundTaskExecutor();

        final int index = nextTaskExecutor.incrementAndGet();

        if (index < executors.length) {
            executors[index].execute(task);
        } else {
            nextTaskExecutor.set(0);
            executors[0].execute(task);
        }
    }

    /**
     * Add a new javaFX task.
     *
     * @param task the javaFX task.
     */
    @FromAnyThread
    public void addFxTask(@NotNull final Runnable task) {
        final EditorTaskExecutor executor = getFxTaskExecutor();
        executor.execute(task);
    }

    /**
     * Add a new editor task.
     *
     * @param task the editor task.
     */
    @FromAnyThread
    public void addJmeTask(@NotNull final Runnable task) {
        final JmeThreadExecutor executor = getJmeTasksExecutor();
        executor.addToExecute(task);
    }

    /**
     * @return the list of background tasks executors.
     */
    @FromAnyThread
    private @NotNull EditorTaskExecutor[] getBackgroundTaskExecutors() {
        return backgroundTaskExecutors;
    }

    /**
     * @return the executor of javaFX tasks.
     */
    @FromAnyThread
    private @NotNull EditorTaskExecutor getFxTaskExecutor() {
        return fxEditorTaskExecutor;
    }

    /**
     * @return the index of a next background executor.
     */
    @FromAnyThread
    private @NotNull AtomicInteger getNextBackgroundTaskExecutor() {
        return nextBackgroundTaskExecutor;
    }

    /**
     * @return the executor of editor tasks.
     */
    @FromAnyThread
    private @NotNull JmeThreadExecutor getJmeTasksExecutor() {
        return jmeTasksExecutor;
    }

    /**
     * Add a scheduled task.
     *
     * @param runnable the scheduled task.
     * @param timeout  the timeout.
     */
    @FromAnyThread
    public void schedule(@NotNull final Runnable runnable, final long timeout) {
        scheduledExecutorService.schedule(runnable, timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * Add a scheduled task.
     *
     * @param runnable the scheduled task.
     * @param delay    the delay.
     */
    @FromAnyThread
    public void scheduleAtFixedRate(@NotNull final Runnable runnable, final long delay) {
        scheduledExecutorService.scheduleAtFixedRate(runnable, delay, delay, TimeUnit.MILLISECONDS);
    }
}
