package com.ss.builder.fx.component.split.pane;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.editor.state.EditorToolConfig;
import com.ss.builder.fx.component.tab.GlobalLeftToolComponent;
import com.ss.builder.fx.component.tab.TabToolComponent;
import com.ss.rlib.fx.util.ObservableUtils;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link SplitPane} for the {@link GlobalLeftToolComponent}.
 *
 * @author JavaSaBr
 */
public class EditorToolSplitPane extends TabToolSplitPane<EditorToolConfig> {

    /**
     * The root of the editor.
     */
    @NotNull
    private final Region root;

    public EditorToolSplitPane(@NotNull Scene scene, @NotNull Region root) {
        super(scene, null);
        this.root = root;
    }

    @Override
    @FxThread
    public void initFor(@NotNull TabToolComponent toolComponent, @NotNull Node another) {
        super.initFor(toolComponent, another);
        ObservableUtils.onChange(root.widthProperty(),
                () -> handleSceneChanged(getSceneSize()));
    }

    @Override
    @FxThread
    protected void handleSceneChanged(@NotNull Number newSize) {
        super.handleSceneChanged(newSize);
        EXECUTOR_MANAGER.addFxTask(this::requestLayout);
    }

    @Override
    @FxThread
    protected void bindToScene() {
    }

    @Override
    @FxThread
    protected void addElements(@NotNull TabToolComponent toolComponent, @NotNull Node other) {
        getItems().setAll(other, toolComponent);
    }

    @Override
    @FxThread
    protected boolean loadCollapsed() {
        return getConfig().isToolCollapsed();
    }

    @Override
    @FxThread
    protected int loadSize() {
        return getConfig().getToolWidth();
    }

    @Override
    @FxThread
    protected void storeCollapsed(boolean collapsed) {
        getConfig().setToolCollapsed(collapsed);
    }

    @Override
    @FxThread
    protected void storeSize(int size) {
        getConfig().setToolWidth(size);
    }

    @Override
    @FxThread
    protected double getCollapsedPosition() {
        return 1;
    }

    @Override
    @FxThread
    protected double getSceneSize() {
        var width = root.getWidth();
        return Double.compare(width, 0D) == 0 ? scene.getWidth() : width;
    }

    @Override
    @FxThread
    protected double getExpandPosition(double toolSize, double sceneSize) {
        return 1D - super.getExpandPosition(toolSize, sceneSize);
    }
}
