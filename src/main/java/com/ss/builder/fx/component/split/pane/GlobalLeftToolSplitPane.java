package com.ss.builder.fx.component.split.pane;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.config.EditorConfig;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.config.EditorConfig;
import com.ss.builder.fx.component.tab.GlobalLeftToolComponent;

import org.jetbrains.annotations.NotNull;

import javafx.scene.Scene;
import javafx.scene.control.SplitPane;

/**
 * The implementation of the {@link SplitPane} for the {@link GlobalLeftToolComponent}.
 *
 * @author JavaSaBr
 */
public class GlobalLeftToolSplitPane extends TabToolSplitPane<EditorConfig> {

    public GlobalLeftToolSplitPane(@NotNull Scene scene) {
        super(scene, EditorConfig.getInstance());
    }

    @Override
    @FxThread
    protected boolean loadCollapsed() {
        return getConfig().isGlobalLeftToolCollapsed();
    }

    @Override
    @FxThread
    protected int loadSize() {
        return getConfig().getGlobalLeftToolWidth();
    }

    @Override
    @FxThread
    protected void storeCollapsed(boolean collapsed) {
        getConfig().setGlobalLeftToolCollapsed(collapsed);
    }

    @Override
    @FxThread
    protected void storeSize(int size) {
        getConfig().setGlobalLeftToolWidth(size);
    }
}
