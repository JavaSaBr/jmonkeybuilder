package com.ss.editor.ui.component.painting.spawn;

import static com.ss.editor.extension.property.EditablePropertyType.*;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.control.painting.spawn.SpawnToolControl;
import com.ss.editor.control.painting.spawn.SpawnToolControl.SpawnMethod;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.painting.PaintingComponentContainer;
import com.ss.editor.ui.component.painting.property.PaintingPropertyDefinition;
import com.ss.editor.ui.component.painting.property.PropertiesBasedPaintingComponent;
import com.ss.editor.util.EditorUtil;
import com.ss.editor.util.NodeUtils;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * The component to spawn models.
 *
 * @author JavaSaBr
 */
public class SpawnPaintingComponent extends
        PropertiesBasedPaintingComponent<Node, SpawnPaintingStateWithEditorTool, SpawnToolControl> {

    private static final SpawnMethod[] SPAWN_METHODS = SpawnMethod.values();

    private static final String CATEGORY_DEFAULT = "Default";

    private static final String PROPERTY_MODEL = "model";
    private static final String PROPERTY_METHOD = "method";
    private static final String PROPERTY_MIN_SCALE = "minScale";
    private static final String PROPERTY_MAX_SCALE = "maxScale";
    private static final String PROPERTY_PADDING = "padding";

    public static final int AVAILABLE_MODELS = 10;

    public SpawnPaintingComponent(@NotNull PaintingComponentContainer container) {
        super(container);
        setToolControl(new SpawnToolControl(this));
        showCategory(CATEGORY_DEFAULT);
    }

    @Override
    @FxThread
    protected @NotNull Array<PaintingPropertyDefinition> getPaintingProperties() {

        var result = ArrayFactory.<PaintingPropertyDefinition>newArray(PaintingPropertyDefinition.class);
        result.add(new PaintingPropertyDefinition(CATEGORY_DEFAULT, ENUM,
                Messages.MODEL_PROPERTY_METHOD, PROPERTY_METHOD, SpawnMethod.BATCH));
        result.add(new PaintingPropertyDefinition(CATEGORY_DEFAULT, VECTOR_3F,
                Messages.MODEL_PROPERTY_MIN_SCALE, PROPERTY_MIN_SCALE, new Vector3f(1F, 1F, 1F)));
        result.add(new PaintingPropertyDefinition(CATEGORY_DEFAULT, VECTOR_3F,
                Messages.MODEL_PROPERTY_MAX_SCALE, PROPERTY_MAX_SCALE, new Vector3f(1F, 1F, 1F)));
        result.add(new PaintingPropertyDefinition(CATEGORY_DEFAULT, VECTOR_3F,
                Messages.MODEL_PROPERTY_PADDING, PROPERTY_PADDING, new Vector3f(1F, 1F, 1F)));

        for (int i = 1; i <= AVAILABLE_MODELS; i++) {
            result.add(new PaintingPropertyDefinition(CATEGORY_DEFAULT, SPATIAL_FROM_ASSET_FOLDER,
                    Messages.MODEL_PROPERTY_MODEL + " " + "#" + i, PROPERTY_MODEL + "_" + i, null));
        }

        return result;
    }

    @Override
    @FxThread
    protected void syncValues(@NotNull VarTable vars, @NotNull SpawnPaintingStateWithEditorTool state) {

        var toolControl = getToolControl();

        var examples = ArrayFactory.<Spatial>newArray(Spatial.class);
        var selectedModels = new String[AVAILABLE_MODELS];

        for (var i = 1; i <= AVAILABLE_MODELS; i++) {

            var id = PROPERTY_MODEL + "_" + i;
            if (!vars.has(id)) {
                selectedModels[i - 1] = null;
                continue;
            }

            var spatial = vars.get(id, Spatial.class);

            examples.add(spatial);

            selectedModels[i - 1] = spatial.getKey()
                    .getName();
        }

        var method = vars.getEnum(PROPERTY_METHOD, SpawnMethod.class);
        var minScale = vars.get(PROPERTY_MIN_SCALE, Vector3f.class);
        var maxScale = vars.get(PROPERTY_MAX_SCALE, Vector3f.class);
        var padding = vars.get(PROPERTY_PADDING, Vector3f.class);

        state.setMethod(method.ordinal());
        state.setMinScale(minScale);
        state.setMaxScale(maxScale);
        state.setPadding(padding);
        state.setSelectedModels(selectedModels);

        super.syncValues(vars, state);

        EXECUTOR_MANAGER.addJmeTask(() ->
                toolControl.updateExamples(examples));
    }

    @Override
    @JmeThread
    protected void syncValues(@NotNull SpawnPaintingStateWithEditorTool state, @NotNull SpawnToolControl toolControl) {
        super.syncValues(state, toolControl);
        toolControl.setMethod(SpawnMethod.valueOf(state.getMethod()));
        toolControl.setMinScale(state.getMinScale());
        toolControl.setMaxScale(state.getMaxScale());
        toolControl.setPadding(state.getPadding());
    }

    @Override
    protected void readState(@NotNull SpawnPaintingStateWithEditorTool state, @NotNull VarTable vars) {
        super.readState(state, vars);

        var method = state.getMethod();
        var selectedModels = state.getSelectedModels();

        vars.set(PROPERTY_METHOD, SPAWN_METHODS[method]);
        vars.set(PROPERTY_MIN_SCALE, state.getMinScale());
        vars.set(PROPERTY_MAX_SCALE, state.getMaxScale());
        vars.set(PROPERTY_PADDING, state.getPadding());

        var assetManager = EditorUtil.getAssetManager();

        for (int i = 1; i <= AVAILABLE_MODELS; i++) {

            var selectedModel = selectedModels[i - 1];
            if (selectedModel == null) {
                continue;
            }

            try {
                vars.set(PROPERTY_MODEL + "_" + i, assetManager.loadModel(selectedModel));
            } catch (AssetNotFoundException e) {
                LOGGER.warning(this, e);
            }
        }
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
    public boolean isSupport(@NotNull Object object) {
        return object instanceof Node &&
                NodeUtils.findGeometry((Spatial) object) != null;
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return Messages.PAINTING_COMPONENT_SPAWN_MODELS;
    }

    @Override
    @FxThread
    public @Nullable Image getIcon() {
        return Icons.FOREST_16;
    }
}
