package com.ss.editor.executor.impl;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.common.concurrent.util.ConcurrentUtils;
import com.ss.rlib.common.util.array.Array;
import javafx.application.Platform;
import org.jetbrains.annotations.NotNull;

/**
 * The executor to execute tasks in the FX UI Thread.
 *
 * @author JavaSaBr
 */
public class FxEditorTaskExecutor extends AbstractEditorTaskExecutor {

    private static final int EXECUTE_LIMIT = 300;

    /**
     * The task for executing editor tasks in the FX UI Thread.
     */
    @NotNull
    private final Runnable fxTask = () -> doExecute(execute, executed);

    public FxEditorTaskExecutor() {
        setName(FxEditorTaskExecutor.class.getSimpleName());
        setPriority(NORM_PRIORITY);
        try {
            Platform.startup(this::start);
        } catch (IllegalStateException e) {
            start();
        }
    }

    @Override
    @FxThread
    protected void doExecute(@NotNull Array<Runnable> execute, @NotNull Array<Runnable> executed) {

        var array = execute.array();

        for (int i = 0, length = execute.size(); i < length; ) {
            try {

                for (int count = 0; count < EXECUTE_LIMIT && i < length; count++, i++) {

                    var task = array[i];
                    try {
                        task.run();
                    } catch (Exception e) {
                        EditorUtil.handleException(LOGGER, this, e);
                    }

                    executed.add(task);
                }

            } catch (Exception e) {
                LOGGER.warning(e);
            }
        }

        ConcurrentUtils.notifyAll(fxTask);
    }

    @Override
    public void run() {
        while (true) {

            executed.clear();
            execute.clear();

            synchronized (waitTasks) {
                execute.addAll(waitTasks);
            }

            if (execute.isEmpty() && wait.compareAndSet(false, true)) {
                synchronized (wait) {
                    if (wait.get()) {
                        ConcurrentUtils.waitInSynchronize(wait);
                    }
                }
            }

            if (execute.isEmpty()) {
                continue;
            }

            executeInFxUiThread();

            if (executed.isEmpty()) {
                continue;
            }

            synchronized (waitTasks) {
                waitTasks.removeAll(executed);
            }
        }
    }

    @FromAnyThread
    private void executeInFxUiThread() {
        synchronized (fxTask) {
            Platform.runLater(fxTask);
            ConcurrentUtils.waitInSynchronize(fxTask);
        }
    }
}
