package com.ss.editor.ui.component.split.pane;

import com.ss.editor.config.EditorConfig;
import com.ss.editor.ui.component.tab.TabToolComponent;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link SplitPane} for the {@link GlobalBottomToolSplitPane}.
 *
 * @author JavaSaBr
 */
public class GlobalBottomToolSplitPane extends TabToolSplitPane<EditorConfig> {

    public GlobalBottomToolSplitPane(@NotNull final Scene scene) {
        super(scene, EditorConfig.getInstance());
        setOrientation(Orientation.VERTICAL);
    }

    @Override
    protected void addElements(final @NotNull TabToolComponent toolComponent, final @NotNull Node other) {
        getItems().setAll(other, toolComponent);
    }

    @Override
    protected void addListeners(@NotNull final TabToolComponent toolComponent) {
        toolComponent.heightProperty().addListener((observable, oldValue, newValue) -> handleToolChanged(newValue));
    }

    @Override
    protected boolean loadCollapsed() {
        return getConfig().isGlobalBottomToolCollapsed();
    }

    @Override
    protected int loadWidth() {
        return getConfig().getGlobalBottomToolWidth();
    }

    @Override
    protected void saveCollapsed(final boolean collapsed) {
        getConfig().setGlobalBottomToolCollapsed(collapsed);
    }

    @Override
    protected void saveWidth(final int width) {
        getConfig().setGlobalBottomToolWidth(width);
    }

    @Override
    protected double getCollapsedPosition() {
        return 1;
    }

    @Override
    protected double getSceneWidth() {
        final double height = scene.getHeight();
        return height == 0 ? scene.getHeight() : height;
    }

    @Override
    protected double getExpandPosition(final double toolWidth, final double sceneWidth) {
        return 1D - super.getExpandPosition(toolWidth, sceneWidth);
    }
}
