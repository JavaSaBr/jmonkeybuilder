package com.ss.builder.fx.component.split.pane;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.config.EditorConfig;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.config.EditorConfig;
import com.ss.builder.fx.component.tab.TabToolComponent;
import com.ss.rlib.fx.util.ObservableUtils;
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

    public GlobalBottomToolSplitPane(@NotNull Scene scene) {
        super(scene, EditorConfig.getInstance());
        setOrientation(Orientation.VERTICAL);
    }

    @Override
    @FxThread
    protected void addElements(@NotNull TabToolComponent toolComponent, @NotNull Node other) {
        getItems().setAll(other, toolComponent);
    }

    @Override
    @FxThread
    protected void addListeners(@NotNull TabToolComponent toolComponent) {
        ObservableUtils.onChange(toolComponent.heightProperty(), this::handleToolChanged);
    }

    @Override
    @FxThread
    protected void bindToScene() {
        ObservableUtils.onChange(scene.heightProperty(),
                number -> handleSceneChanged(getSceneSize()));
    }

    @Override
    @FxThread
    protected boolean loadCollapsed() {
        return getConfig().isGlobalBottomToolCollapsed();
    }

    @Override
    @FxThread
    protected int loadSize() {
        return getConfig().getGlobalBottomToolHeight();
    }

    @Override
    @FxThread
    protected void storeCollapsed(boolean collapsed) {
        getConfig().setGlobalBottomToolCollapsed(collapsed);
    }

    @Override
    @FxThread
    protected void storeSize(int size) {
        getConfig().setGlobalBottomToolHeight(size);
    }

    @Override
    @FxThread
    protected double getCollapsedPosition() {
        return 1;
    }

    @Override
    @FxThread
    protected double getSceneSize() {
        var height = scene.getHeight();
        return Double.compare(height, 0D) == 0 ? scene.getHeight() : height;
    }

    @Override
    @FxThread
    protected double getExpandPosition(double toolSize, double sceneSize) {
        return 1D - super.getExpandPosition(toolSize, sceneSize);
    }
}
