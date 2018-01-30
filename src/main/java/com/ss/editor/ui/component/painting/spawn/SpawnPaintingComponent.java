package com.ss.editor.ui.component.painting.spawn;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.control.painting.spawn.SpawnToolControl;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.painting.PaintingComponentContainer;
import com.ss.editor.ui.component.painting.impl.AbstractPaintingComponent;
import com.ss.editor.util.NodeUtils;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * The component to spawn models.
 *
 * @author JavaSaBr
 */
public class SpawnPaintingComponent extends AbstractPaintingComponent<Node, SpawnPaintingStateWithEditorTool, SpawnToolControl> {

    public SpawnPaintingComponent(@NotNull final PaintingComponentContainer container) {
        super(container);
        setToolControl(new SpawnToolControl(this));
    }

    @Override
    @FxThread
    protected void createComponents() {
        super.createComponents();
        FXUtils.addToPane(createBrushSettings(), this);
    }

    @Override
    @FxThread
    public void startPainting(@NotNull final Object object) {
        super.startPainting(object);
    }

    @Override
    @FromAnyThread
    protected @NotNull Supplier<SpawnPaintingStateWithEditorTool> getStateConstructor() {
        return SpawnPaintingStateWithEditorTool::new;
    }

    @Override
    @FromAnyThread
    protected @NotNull Class<SpawnPaintingStateWithEditorTool> getStateType() {
        return SpawnPaintingStateWithEditorTool.class;
    }

    @Override
    @FxThread
    public void stopPainting() {

    }

    @Override
    @FxThread
    public boolean isSupport(@NotNull final Object object) {
        return object instanceof Node &&
                NodeUtils.findGeometry((Spatial) object) != null;
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return "Spawn";
    }

    @Override
    @FxThread
    public @Nullable Image getIcon() {
        return Icons.SCENE_16;
    }

    @Override
    @FxThread
    public void notifyChangeProperty(@NotNull final Object object, @NotNull final String propertyName) {

    }
}
