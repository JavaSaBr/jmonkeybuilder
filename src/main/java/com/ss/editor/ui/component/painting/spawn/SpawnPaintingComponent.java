package com.ss.editor.ui.component.painting.spawn;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.control.painting.spawn.SpawnToolControl;
import com.ss.editor.control.painting.spawn.SpawnToolControl.SpawnMethod;
import com.ss.editor.extension.property.EditablePropertyType;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.painting.PaintingComponentContainer;
import com.ss.editor.ui.component.painting.property.PaintingPropertyDefinition;
import com.ss.editor.ui.component.painting.property.PropertiesBasedPaintingComponent;
import com.ss.editor.util.NodeUtils;
import com.ss.rlib.util.VarTable;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * The component to spawn models.
 *
 * @author JavaSaBr
 */
public class SpawnPaintingComponent extends PropertiesBasedPaintingComponent<Node, SpawnPaintingStateWithEditorTool, SpawnToolControl> {

    private static final String CATEGORY_DEFAULT = "Default";

    private static final String PROPERTY_MODEL = "model";
    private static final String PROPERTY_METHOD = "method";

    private static final int AVAILABLE_MODELS = 10;

    public SpawnPaintingComponent(@NotNull final PaintingComponentContainer container) {
        super(container);
        setToolControl(new SpawnToolControl(this));
        showCategory(CATEGORY_DEFAULT);
    }

    @Override
    @FxThread
    protected @NotNull Array<PaintingPropertyDefinition> getPaintingProperties() {

        final Array<PaintingPropertyDefinition> result = ArrayFactory.newArray(PaintingPropertyDefinition.class);
        result.add(new PaintingPropertyDefinition(CATEGORY_DEFAULT, EditablePropertyType.ENUM,
                "Method", PROPERTY_METHOD, SpawnMethod.BATCHED));

        for (int i = 1; i <= AVAILABLE_MODELS; i++) {
            result.add(new PaintingPropertyDefinition(CATEGORY_DEFAULT, EditablePropertyType.SPATIAL_FROM_ASSET_FOLDER,
                    "Model " + "#" + i, PROPERTY_MODEL + "_" + i, null));
        }

        return result;
    }

    @Override
    protected void syncValues() {
        super.syncValues();

        final VarTable vars = getVars();
        final SpawnToolControl toolControl = getToolControl();

        final Array<Spatial> examples = ArrayFactory.newArray(Spatial.class);

        for (int i = 1; i <= AVAILABLE_MODELS; i++) {

            final String id = PROPERTY_MODEL + "_" + i;
            if (!vars.has(id)) {
                continue;
            }

            examples.add(vars.get(id));
        }

        toolControl.setMethod(vars.get(PROPERTY_METHOD));

        EXECUTOR_MANAGER.addJmeTask(() -> toolControl.updateExamples(examples));
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
