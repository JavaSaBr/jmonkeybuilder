package com.ss.editor.ui.component.split.pane;

import com.ss.editor.config.EditorConfig;
import com.ss.editor.ui.component.tab.GlobalLeftToolComponent;

import org.jetbrains.annotations.NotNull;

import javafx.scene.Scene;
import javafx.scene.control.SplitPane;

/**
 * The implementation of the {@link SplitPane} for the {@link GlobalLeftToolComponent}.
 *
 * @author JavaSaBr
 */
public class GlobalLeftToolSplitPane extends TabToolSplitPane<EditorConfig> {

    /**
     * Instantiates a new Global left tool split pane.
     *
     * @param scene the scene
     */
    public GlobalLeftToolSplitPane(@NotNull final Scene scene) {
        super(scene, EditorConfig.getInstance());
    }

    @Override
    protected boolean loadCollapsed() {
        return getConfig().isGlobalLeftToolCollapsed();
    }

    @Override
    protected int loadSize() {
        return getConfig().getGlobalLeftToolWidth();
    }

    @Override
    protected void saveCollapsed(final boolean collapsed) {
        getConfig().setGlobalLeftToolCollapsed(collapsed);
    }

    @Override
    protected void saveSize(final int size) {
        getConfig().setGlobalLeftToolWidth(size);
    }
}
