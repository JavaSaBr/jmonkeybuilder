package com.ss.editor.executor.impl;

import com.ss.editor.annotation.FXThread;
import com.ss.editor.util.EditorUtil;
import com.sun.javafx.application.PlatformImpl;

import org.jetbrains.annotations.NotNull;

import rlib.concurrent.util.ConcurrentUtils;
import rlib.concurrent.util.ThreadUtils;
import rlib.util.array.Array;

/**
 * The executor to execute tasks in the FX UI Thread.
 *
 * @author JavaSaBr
 */
public class FXEditorTaskExecutor extends AbstractEditorTaskExecutor {

    private static final int EXECUTE_LIMIT = 300;

    /**
     * The task for executing editor tasks in the FX UI Thread.
     */
    @NotNull
    private final Runnable fxTask = () -> doExecute(execute, executed);

    public FXEditorTaskExecutor() {
        setName(FXEditorTaskExecutor.class.getSimpleName());
        setPriority(NORM_PRIORITY);
        PlatformImpl.startup(this::start);
    }

    @Override
    @FXThread
    protected void doExecute(@NotNull final Array<Runnable> execute, @NotNull final Array<Runnable> executed) {

        final Runnable[] array = execute.array();

        for (int i = 0, length = execute.size(); i < length; ) {
            try {

                for (int count = 0, limit = EXECUTE_LIMIT; count < limit && i < length; count++, i++) {

                    final Runnable task = array[i];
                    try {
                        task.run();
                    } catch (final Exception e) {
                        EditorUtil.handleException(LOGGER, this, e);
                    }

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
