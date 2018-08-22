package com.ss.builder.util;

import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.manager.ExecutorManager;
import com.ss.builder.fx.util.UiUtils;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.editor.extension.integration.EditorEnvironment;
import com.ss.builder.manager.ExecutorManager;
import com.ss.builder.fx.util.UiUtils;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of {@link EditorEnvironment}.
 *
 * @author JavaSaBr
 */
public class JmbEditorEnvoriment implements EditorEnvironment {

    private static final JmbEditorEnvoriment INSTANCE = new JmbEditorEnvoriment();

    public static @NotNull JmbEditorEnvoriment getInstance() {
        return INSTANCE;
    }

    @Override
    @FromAnyThread
    public void notifyStartLoading() {
        ExecutorManager.getInstance()
                .addFxTask(UiUtils::incrementLoading);
    }

    @Override
    @FromAnyThread
    public void notifyEndLoading() {
        ExecutorManager.getInstance()
                .addFxTask(UiUtils::decrementLoading);
    }
}
