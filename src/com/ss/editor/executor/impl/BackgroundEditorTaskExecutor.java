package com.ss.editor.executor.impl;

import java.util.concurrent.atomic.AtomicBoolean;

import rlib.concurrent.util.ConcurrentUtils;
import rlib.util.array.Array;

import static java.lang.Math.min;

/**
 * Реализация исполнителья фоновых задач.
 *
 * @author Ronn
 */
public class BackgroundEditorTaskExecutor extends AbstractEditorTaskExecutor {

    /**
     * Рантайм.
     */
    private static final Runtime RUNTIME = Runtime.getRuntime();

    /**
     * Максимум обновляемых задач за проход.
     */
    private static final int PROP_MAXIMUM_UPDATE = 500 / RUNTIME.availableProcessors();

    /**
     * Лимит выполнений задач за одну фазу.
     */
    private static final int PROP_EXECUTE_LIMIT = 5;

    public BackgroundEditorTaskExecutor(final int order) {
        setName(BackgroundEditorTaskExecutor.class.getSimpleName() + "_" + order);
        setPriority(NORM_PRIORITY - 2);
        start();
    }

    /**
     * Процесс обновления состояния задач.
     */
    protected void doUpdate(final Array<Runnable> execute, final Array<Runnable> executed) {

        final Runnable[] array = execute.array();

        for (int i = 0, length = min(execute.size(), PROP_MAXIMUM_UPDATE); i < length; ) {

            for (int count = 0, limit = PROP_EXECUTE_LIMIT; count < limit && i < length; count++, i++) {

                final Runnable task = array[i];
                task.run();

                executed.add(task);
            }
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

            if (execute.isEmpty()) continue;

            // обновление состояния задач
            doUpdate(execute, executed);

            if (executed.isEmpty()) continue;

            lock();
            try {
                waitTasks.removeAll(executed);
            } finally {
                unlock();
            }
        }
    }
}
