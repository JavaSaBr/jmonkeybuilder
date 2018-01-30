package com.ss.editor.ui.component.painting.spawn;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
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
import com.ss.editor.util.EditorUtil;
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
    private static final String PROPERTY_SCALE = "scale";

    public static final int AVAILABLE_MODELS = 10;

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
        result.add(new PaintingPropertyDefinition(CATEGORY_DEFAULT, EditablePropertyType.VECTOR_3F,
                "Scale", PROPERTY_SCALE, new Vector3f(1F, 1F, 1F)));

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
        final String[] selectedModels = new String[AVAILABLE_MODELS];

        for (int i = 1; i <= AVAILABLE_MODELS; i++) {

            final String id = PROPERTY_MODEL + "_" + i;
            if (!vars.has(id)) {
                selectedModels[i - 1] = null;
                continue;
            }

            final Spatial spatial = vars.get(id);
            examples.add(spatial);
            selectedModels[i - 1] = spatial.getKey().getName();
        }

        final SpawnMethod method = vars.get(PROPERTY_METHOD);
        final Vector3f scale = vars.get(PROPERTY_SCALE);

        final SpawnPaintingStateWithEditorTool state = getState();
        state.setMethod(method.ordinal());
        state.setScale(scale);
        state.setSelectedModels(selectedModels);

        EXECUTOR_MANAGER.addJmeTask(() -> {
            toolControl.setMethod(method);
            toolControl.setScale(scale);
            toolControl.updateExamples(examples);
        });
    }

    @Override
    @FxThread
    protected void readState(@NotNull final SpawnPaintingStateWithEditorTool state) {

        final int method = state.getMethod();
        final String[] selectedModels = state.getSelectedModels();

        final VarTable vars = getVars();
        vars.set(PROPERTY_METHOD, SpawnMethod.values()[method]);
        vars.set(PROPERTY_SCALE, state.getScale());

        final AssetManager assetManager = EditorUtil.getAssetManager();

        for (int i = 1; i <= AVAILABLE_MODELS; i++) {
            final String selectedModel = selectedModels[i - 1];
            if (selectedModel == null) {
                continue;
            }
            vars.set(PROPERTY_MODEL + "_" + i, assetManager.loadModel(selectedModel));
        }

        super.readState(state);
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
