package com.ss.editor.executor.impl;

import com.sun.javafx.application.PlatformImpl;

import rlib.concurrent.util.ConcurrentUtils;
import rlib.concurrent.util.ThreadUtils;
import rlib.util.array.Array;

/**
 * The implementation of the {@link EditorThreadExecutor} for executing task in the FX UI Thread.
 *
 * @author Ronn
 */
public class FXEditorTaskExecutor extends AbstractEditorTaskExecutor {

    private static final int EXECUTE_LIMIT = 300;

    /**
     * The task for executing editor tasks in the FX UI Thread.
     */
    private final Runnable fxTask = () -> doExecute(execute, executed);

    public FXEditorTaskExecutor() {
        setName(FXEditorTaskExecutor.class.getSimpleName());
        setPriority(NORM_PRIORITY);
        PlatformImpl.startup(this::start);
    }

    @Override
    protected void doExecute(final Array<Runnable> execute, final Array<Runnable> executed) {

        final Runnable[] array = execute.array();

        for (int i = 0, length = execute.size(); i < length; ) {
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
    public void run() {
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

            if (execute.isEmpty()) continue;
            executeInFXUIThread();
            if (executed.isEmpty()) continue;

            lock();
            try {
                waitTasks.removeAll(executed);
            } finally {
                unlock();
            }
        }
    }

    private void executeInFXUIThread() {
        while (true) {
            try {
                PlatformImpl.runAndWait(fxTask);
                break;
            } catch (final IllegalStateException e) {
                LOGGER.warning(this, e);
                ThreadUtils.sleep(1000);
            }
        }
    }
}
