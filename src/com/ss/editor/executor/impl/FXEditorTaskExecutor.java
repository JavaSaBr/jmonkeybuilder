package com.ss.editor.executor.impl;

import com.sun.javafx.application.PlatformImpl;

import org.sample.client.SampleGame;
import org.sample.client.game.task.GameTask;
import org.sample.client.util.LocalObjects;

import java.util.concurrent.atomic.AtomicBoolean;

import rlib.concurrent.util.ConcurrentUtils;
import rlib.util.array.Array;

/**
 * Реализация исполнителья игровых задач по обновлению FX UI.
 *
 * @author Ronn
 */
public class FXEditorTaskExecutor extends AbstractEditorTaskExecutor {

    private static final int EXECUTE_LIMIT = 300;

    /**
     * Контейнер локальных объектов для потока JavaFX.
     */
    private final LocalObjects fxLocal;

    /**
     * Задча по выполнению подзадач в потоке JavaFX.
     */
    private final Runnable fxTask = () -> doExecute(getExecute(), getExecuted(), getFxLocal(), SampleGame.getInstance());

    public FXEditorTaskExecutor() {
        setName(FXEditorTaskExecutor.class.getSimpleName());
        setPriority(NORM_PRIORITY);

        this.fxLocal = new LocalObjects();

        start();
    }

    /**
     * Процесс обновления состояния задач.
     */
    protected void doExecute(final Array<GameTask> execute, final Array<GameTask> executed, final LocalObjects local, final SampleGame game) {

        final GameTask[] array = execute.array();

        for (int i = 0, length = execute.size(); i < length; ) {

            final long time = System.currentTimeMillis();

            try {

                final long currentTime = SampleGame.getCurrentTime();

                for (int count = 0, limit = EXECUTE_LIMIT; count < limit && i < length; count++, i++) {

                    final GameTask task = array[i];

                    if (task.execute(local, currentTime)) {
                        executed.add(task);
                    }
                }

            } catch (final Exception e) {
                LOGGER.warning(e);
            }
        }
    }

    @Override
    public void execute(final GameTask gameTask) {
        lock();
        try {

            final Array<GameTask> waitTasks = getWaitTasks();
            waitTasks.slowRemove(gameTask);
            waitTasks.add(gameTask);

            final AtomicBoolean wait = getWait();

            if (wait.get()) {
                synchronized (wait) {
                    if (wait.compareAndSet(true, false)) {
                        ConcurrentUtils.notifyAllInSynchronize(wait);
                    }
                }
            }

        } finally {
            unlock();
        }
    }

    /**
     * @return контейнер локальных объектов для потока JavaFX.
     */
    public LocalObjects getFxLocal() {
        return fxLocal;
    }

    @Override
    public void run() {

        final Array<GameTask> execute = getExecute();
        final Array<GameTask> executed = getExecuted();
        final Array<GameTask> waitTasks = getWaitTasks();

        final AtomicBoolean wait = getWait();

        while (true) {

            executed.clear();
            execute.clear();

            lock();
            try {

                if (waitTasks.isEmpty()) {
                    wait.getAndSet(true);
                } else {
                    execute.addAll(waitTasks);
                }

            } finally {
                unlock();
            }

            if (wait.get()) {
                synchronized (wait) {
                    if (wait.get()) {
                        ConcurrentUtils.waitInSynchronize(wait);
                    }
                }
            }

            if (execute.isEmpty()) {
                continue;
            }

            // обновление состояния задач
            PlatformImpl.runAndWait(fxTask);

            if (executed.isEmpty()) {
                continue;
            }

            lock();
            try {
                waitTasks.removeAll(executed);
            } finally {
                unlock();
            }

            executed.forEach(FINISH_TASK_FUNC);
        }
    }
}
