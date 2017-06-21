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

    /**
     * Instantiates a new Global bottom tool split pane.
     *
     * @param scene the scene
     */
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
    protected void bindToScene() {
        scene.heightProperty()
                .addListener((observableValue, oldValue, newValue) -> handleSceneChanged(getSceneSize()));
    }

    @Override
    protected boolean loadCollapsed() {
        return getConfig().isGlobalBottomToolCollapsed();
    }

    @Override
    protected int loadSize() {
        return getConfig().getGlobalBottomToolHeight();
    }

    @Override
    protected void saveCollapsed(final boolean collapsed) {
        getConfig().setGlobalBottomToolCollapsed(collapsed);
    }

    @Override
    protected void saveSize(final int size) {
        getConfig().setGlobalBottomToolHeight(size);
    }

    @Override
    protected double getCollapsedPosition() {
        return 1;
    }

    @Override
    protected double getSceneSize() {
        final double height = scene.getHeight();
        return height == 0 ? scene.getHeight() : height;
    }

    @Override
    protected double getExpandPosition(final double toolSize, final double sceneSize) {
        return 1D - super.getExpandPosition(toolSize, sceneSize);
    }
}
