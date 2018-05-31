package com.ss.editor.manager;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.executor.EditorTaskExecutor;
import com.ss.editor.executor.impl.BackgroundEditorTaskExecutor;
import com.ss.editor.executor.impl.FxEditorTaskExecutor;
import com.ss.editor.executor.impl.JmeThreadExecutor;
import com.ss.editor.util.TimeTracker;
import com.ss.rlib.common.concurrent.atomic.AtomicInteger;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.manager.InitializeManager;
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

    private static final Logger LOGGER = LoggerManager.getLogger(ExecutorManager.class);

    private static final Runtime RUNTIME = Runtime.getRuntime();

    private static final int PROP_BACKGROUND_TASK_EXECUTORS = 8;

    @Nullable
    private static ExecutorManager instance;

    public static @NotNull ExecutorManager getInstance() {
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
        InitializeManager.valid(getClass());

        TimeTracker.getStartupTracker(TimeTracker.STARTPUL_LEVEL_5)
                .start();

        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        this.backgroundTaskExecutors = new EditorTaskExecutor[PROP_BACKGROUND_TASK_EXECUTORS];

        for (int i = 0, length = backgroundTaskExecutors.length; i < length; i++) {
            backgroundTaskExecutors[i] = new BackgroundEditorTaskExecutor(i + 1);
        }

        this.jmeTasksExecutor = JmeThreadExecutor.getInstance();
        this.fxEditorTaskExecutor = new FxEditorTaskExecutor();

        this.nextBackgroundTaskExecutor = new AtomicInteger(0);

        TimeTracker.getStartupTracker(TimeTracker.STARTPUL_LEVEL_5)
                .finish(() -> "Initialized ExecutorManager");

        LOGGER.info("initialized.");
    }

    /**
     * Add a new background task.
     *
     * @param task the background task.
     */
    @FromAnyThread
    public void addBackgroundTask(@NotNull final Runnable task) {

        var executors = getBackgroundTaskExecutors();
        var nextTaskExecutor = getNextBackgroundTaskExecutor();

        var index = nextTaskExecutor.incrementAndGet();

        if (index < executors.length) {
            executors[index].execute(task);
        } else {
            nextTaskExecutor.set(0);
            executors[0].execute(task);
        }
    }

    /**
     * Add the new task to be executed in the JavaFX thread.
     *
     * @param task the task.
     */
    @FromAnyThread
    public void addFxTask(@NotNull Runnable task) {
        getFxTaskExecutor().execute(task);
    }

    /**
     * Add a new editor task.
     *
     * @param task the editor task.
     */
    @FromAnyThread
    public void addJmeTask(@NotNull Runnable task) {
        getJmeTasksExecutor().addToExecute(task);
    }

    /**
     * Get the list of background tasks executors.
     *
     * @return the list of background tasks executors.
     */
    @FromAnyThread
    private @NotNull EditorTaskExecutor[] getBackgroundTaskExecutors() {
        return backgroundTaskExecutors;
    }

    /**
     * Get the executor of javaFX tasks.
     *
     * @return the executor of javaFX tasks.
     */
    @FromAnyThread
    private @NotNull EditorTaskExecutor getFxTaskExecutor() {
        return fxEditorTaskExecutor;
    }

    /**
     * Get the index of a next background executor.
     *
     * @return the index of a next background executor.
     */
    @FromAnyThread
    private @NotNull AtomicInteger getNextBackgroundTaskExecutor() {
        return nextBackgroundTaskExecutor;
    }

    /**
     * Get the executor of jME tasks.
     *
     * @return the executor of jME tasks.
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
    public void schedule(@NotNull Runnable runnable, long timeout) {
        scheduledExecutorService.schedule(runnable, timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * Add a scheduled task.
     *
     * @param runnable the scheduled task.
     * @param delay    the delay.
     */
    @FromAnyThread
    public void scheduleAtFixedRate(@NotNull Runnable runnable, long delay) {
        scheduledExecutorService.scheduleAtFixedRate(runnable, delay, delay, TimeUnit.MILLISECONDS);
    }
}
