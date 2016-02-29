package com.ss.editor.manager;

import com.ss.editor.executor.EditorTaskExecutor;
import com.ss.editor.executor.impl.BackgroundEditorTaskExecutor;
import com.ss.editor.executor.impl.EditorThreadExecutor;
import com.ss.editor.executor.impl.FXEditorTaskExecutor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import rlib.concurrent.atomic.AtomicInteger;
import rlib.logging.Logger;
import rlib.logging.LoggerManager;

/**
 * Реализация менеджера по исполнению различных задач.
 *
 * @author Ronn
 */
public class ExecutorManager {

    private static final Logger LOGGER = LoggerManager.getLogger(ExecutorManager.class);

    private static final Runtime RUNTIME = Runtime.getRuntime();

    private static final int PROP_BACKGROUND_TASK_EXECUTORS = RUNTIME.availableProcessors();

    private static ExecutorManager instance;

    public static ExecutorManager getInstance() {

        if (instance == null) {
            instance = new ExecutorManager();
        }

        return instance;
    }

    /**
     * Сервс для выполнения задач по расписанию.
     */
    private final ScheduledExecutorService scheduledExecutorService;

    /**
     * Список исполнителей фоновых.
     */
    private final EditorTaskExecutor[] backgroundTaskExecutors;

    /**
     * Исполнитель задач в основном потоке.
     */
    private final EditorThreadExecutor editorThreadExecutor;

    /**
     * Исполнитель задач в потоке JavaFX.
     */
    private final EditorTaskExecutor fxEditorTaskExecutor;

    /**
     * Индекс следующего исполнителя фоновых задач.
     */
    private final AtomicInteger nextBackgroundTaskExecutor;

    private ExecutorManager() {

        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        this.backgroundTaskExecutors = new EditorTaskExecutor[PROP_BACKGROUND_TASK_EXECUTORS];

        for (int i = 0, length = backgroundTaskExecutors.length; i < length; i++) {
            backgroundTaskExecutors[i] = new BackgroundEditorTaskExecutor(i + 1);
        }

        this.editorThreadExecutor = EditorThreadExecutor.getInstance();
        this.fxEditorTaskExecutor = new FXEditorTaskExecutor();

        this.nextBackgroundTaskExecutor = new AtomicInteger(0);

        LOGGER.info("initialized.");
    }

    /**
     * Добавление на обработку фоновой задачи.
     *
     * @param task фоновая задача.
     */
    public void addBackgroundTask(final Runnable task) {

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
     * Добавление на обработку задачи по обновлению FX UI.
     *
     * @param task задача.
     */
    public void addFXTask(final Runnable task) {
        final EditorTaskExecutor executor = getFxTaskExecutor();
        executor.execute(task);
    }

    /**
     * Добавление на обработку задачи для выполнения в основном потоке.
     *
     * @param task задача.
     */
    public void addEditorThreadTask(final Runnable task) {
        final EditorThreadExecutor executor = getEditorThreadExecutor();
        executor.addToExecute(task);
    }

    /**
     * @return список исполнителей фоновых.
     */
    protected EditorTaskExecutor[] getBackgroundTaskExecutors() {
        return backgroundTaskExecutors;
    }

    /**
     * @return исполнитель задач по обновлению FX UI.
     */
    protected EditorTaskExecutor getFxTaskExecutor() {
        return fxEditorTaskExecutor;
    }

    /**
     * @return индекс следующего исполнителя фоновых задач.
     */
    protected AtomicInteger getNextBackgroundTaskExecutor() {
        return nextBackgroundTaskExecutor;
    }

    /**
     * @return исполнитель задач в основном потоке.
     */
    public EditorThreadExecutor getEditorThreadExecutor() {
        return editorThreadExecutor;
    }

    /**
     * Отправить на выполнение задачу.
     *
     * @param runnable выполняемая задача.
     * @param timeout  задержка перед выполнением.
     */
    public void schedule(final Runnable runnable, final long timeout) {
        scheduledExecutorService.schedule(runnable, timeout, TimeUnit.MILLISECONDS);
    }
}
