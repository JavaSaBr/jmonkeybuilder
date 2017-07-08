package com.ss.editor.executor.impl;

import static java.lang.Math.min;
import com.ss.rlib.util.array.Array;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link EditorThreadExecutor} for executing task in the background.
 *
 * @author JavaSaBr
 */
public class BackgroundEditorTaskExecutor extends AbstractEditorTaskExecutor {

    /**
     * The runtime of this process.
     */
    private static final Runtime RUNTIME = Runtime.getRuntime();

    /**
     * The max count of task to execute using the same time.
     */
    private static final int PROP_MAXIMUM_UPDATE = 500 / RUNTIME.availableProcessors();

    /**
     * The max count of task to execute in the one iteration.
     */
    private static final int PROP_EXECUTE_LIMIT = 5;

    /**
     * Instantiates a new Background editor task executor.
     *
     * @param order the order
     */
    public BackgroundEditorTaskExecutor(final int order) {
        setName(BackgroundEditorTaskExecutor.class.getSimpleName() + "_" + order);
        setPriority(NORM_PRIORITY - 2);
        start();
    }

    @Override
    protected void doExecute(@NotNull final Array<Runnable> execute, @NotNull final Array<Runnable> executed) {

        final Runnable[] array = execute.array();

        for (int i = 0, length = min(execute.size(), PROP_MAXIMUM_UPDATE); i < length; ) {
            for (int count = 0; count < PROP_EXECUTE_LIMIT && i < length; count++, i++) {

                final Runnable task = array[i];
                task.run();

                executed.add(task);
            }
        }
    }
}
