package com.ss.editor.ui.component.split.pane;

import com.ss.editor.config.EditorConfig;
import com.ss.editor.ui.component.tab.GlobalToolComponent;

import org.jetbrains.annotations.NotNull;

import javafx.scene.Scene;
import javafx.scene.control.SplitPane;

/**
 * The implementation of the {@link SplitPane} for the {@link GlobalToolComponent}.
 *
 * @author JavaSaBr
 */
public class GlobalToolSplitPane extends TabToolSplitPane<EditorConfig> {

    public GlobalToolSplitPane(@NotNull final Scene scene) {
        super(scene, EditorConfig.getInstance());
    }

    @Override
    protected boolean loadCollapsed() {
        return config.isGlobalToolCollapsed();
    }

    @Override
    protected int loadWidth() {
        return config.getGlobalToolWidth();
    }

    @Override
    protected void saveCollapsed(final boolean collapsed) {
        config.setGlobalToolCollapsed(collapsed);
    }

    @Override
    protected void saveWidth(final int width) {
        config.setGlobalToolWidth(width);
    }
}
