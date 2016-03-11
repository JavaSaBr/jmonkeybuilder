package com.ss.editor.executor.impl;

import com.sun.javafx.application.PlatformImpl;

import java.util.concurrent.atomic.AtomicBoolean;

import rlib.concurrent.util.ConcurrentUtils;
import rlib.concurrent.util.ThreadUtils;
import rlib.util.array.Array;

/**
 * Реализация исполнителья задач по обновлению FX UI.
 *
 * @author Ronn
 */
public class FXEditorTaskExecutor extends AbstractEditorTaskExecutor {

    private static final int EXECUTE_LIMIT = 300;

    /**
     * Задча по выполнению подзадач в потоке JavaFX.
     */
    private final Runnable fxTask = () -> doExecute(getExecute(), getExecuted());

    public FXEditorTaskExecutor() {
        setName(FXEditorTaskExecutor.class.getSimpleName());
        setPriority(NORM_PRIORITY);
        start();
    }

    /**
     * Процесс обновления состояния задач.
     */
    protected void doExecute(final Array<Runnable> execute, final Array<Runnable> executed) {

        final Runnable[] array = execute.array();

        for (int i = 0, length = execute.size(); i < length; ) {

            final long time = System.currentTimeMillis();

            try {

                for (int count = 0, limit = EXECUTE_LIMIT; count < limit && i < length; count++, i++) {

                    final Runnable task = array[i];
                    task.run();

                    executed.add(task);
                }

            } catch (final Exception e) {
                LOGGER.warning(e);
            }
        }
    }

    @Override
    public void execute(final Runnable task) {
        lock();
        try {

            final Array<Runnable> waitTasks = getWaitTasks();
            waitTasks.slowRemove(task);
            waitTasks.add(task);

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

    @Override
    public void run() {

        final Array<Runnable> execute = getExecute();
        final Array<Runnable> executed = getExecuted();
        final Array<Runnable> waitTasks = getWaitTasks();

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
            while (true) {
                try {
                    PlatformImpl.runAndWait(fxTask);
                    break;
                } catch (final IllegalStateException e) {
                    LOGGER.warning(this, e);
                    ThreadUtils.sleep(1000);
                }
            }

            if (executed.isEmpty()) {
                continue;
            }

            lock();
            try {
                waitTasks.removeAll(executed);
            } finally {
                unlock();
            }
        }
    }
}
