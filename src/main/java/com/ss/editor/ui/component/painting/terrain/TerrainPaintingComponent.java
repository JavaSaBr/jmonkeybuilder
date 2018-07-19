package com.ss.editor.ui.component.painting.terrain;

import static com.ss.editor.extension.property.EditablePropertyType.BOOLEAN;
import static com.ss.editor.extension.property.EditablePropertyType.FLOAT;
import static com.ss.editor.ui.component.painting.PaintingComponentContainer.FIELD_PERCENT;
import static com.ss.editor.ui.component.painting.PaintingComponentContainer.LABEL_PERCENT;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import static com.ss.rlib.common.util.array.ArrayFactory.toArray;
import com.jme3.scene.Node;
import com.jme3.terrain.Terrain;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.control.painting.terrain.*;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.painting.PaintingComponentContainer;
import com.ss.editor.ui.component.painting.property.PaintingPropertyDefinition;
import com.ss.editor.ui.component.painting.property.PropertiesBasedPaintingComponent;
import com.ss.editor.ui.component.painting.terrain.paint.TextureLayerSettings;
import com.ss.editor.ui.control.property.operation.PropertyOperation;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.util.MaterialUtils;
import com.ss.editor.util.NodeUtils;
import com.ss.rlib.common.util.StringUtils;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import com.ss.rlib.common.util.dictionary.DictionaryFactory;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
import com.ss.rlib.fx.control.input.FloatTextField;
import com.ss.rlib.fx.util.FxControlUtils;
import com.ss.rlib.fx.util.FxUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The painting component to edit terrain.
 *
 * @author JavaSaBr
 */
public class TerrainPaintingComponent extends
        PropertiesBasedPaintingComponent<Node, TerrainPaintingStateWithEditorTool, TerrainToolControl> {

    public static final String TERRAIN_PARAM = "terrainParam";

    private static final String CATEGORY_RAISE_LOWER = "RaiseLower";
    private static final String CATEGORY_ROUGH = "Rough";
    private static final String CATEGORY_SLOPE = "Slope";
    private static final String CATEGORY_LEVEL = "Level";
    private static final String CATEGORY_SMOOTH = "Smooth";
    private static final String CATEGORY_PAINT = "Paint";

    private static final String PROPERTY_SLOPE_SMOOTHLY = "slope.smoothly";
    private static final String PROPERTY_SLOPE_LIMITED = "slope.limited";
    private static final String PROPERTY_LEVEL_SMOOTHLY = "level.smoothly";
    private static final String PROPERTY_LEVEL_USE_MARKER = "level.useMarker";
    private static final String PROPERTY_LEVEL_VALUE = "level.level";
    private static final String PROPERTY_ROUGH_ROUGHNESS = "rough.roughness";
    private static final String PROPERTY_ROUGH_FREQUENCY = "rough.frequency";
    private static final String PROPERTY_ROUGH_LACUNARITY = "rough.lacunarity";
    private static final String PROPERTY_ROUGH_OCTAVES = "rough.octaves";
    private static final String PROPERTY_ROUGH_SCALE = "rough.scale";

    @NotNull
    private static final Function<Integer, String> LAYER_TO_SCALE_NAME = layer -> "DiffuseMap_" + layer + "_scale";

    @NotNull
    private static final Function<Integer, String> LAYER_TO_ALPHA_NAME = layer -> {

        final int alphaIdx = layer / 4; // 4 = rgba = 4 textures

        if (alphaIdx == 0) {
            return "AlphaMap";
        } else if (alphaIdx == 1) {
            return "AlphaMap_1";
        } else if (alphaIdx == 2) {
            return "AlphaMap_2";
        } else {
            return null;
        }
    };

    @NotNull
    private static final Function<Integer, String> LAYER_TO_DIFFUSE_NAME = layer -> {
        if (layer == 0) {
            return "DiffuseMap";
        } else {
            return "DiffuseMap_" + layer;
        }
    };

    @NotNull
    private static final Function<Integer, String> LAYER_TO_NORMAL_NAME = layer -> {
        if (layer == 0) {
            return "NormalMap";
        } else {
            return "NormalMap_" + layer;
        }
    };

    /**
     * The list of all tool controls.
     */
    @NotNull
    private final Array<TerrainToolControl> toolControls;

    /**
     * The list of all toggle buttons.
     */
    @NotNull
    private final Array<ToggleButton> toggleButtons;

    /**
     * The map with mapping toggle button to terrain control.
     */
    @NotNull
    private final ObjectDictionary<ToggleButton, TerrainToolControl> buttonToControl;

    /**
     * The map with mapping toggle button to properties category.
     */
    @NotNull
    private final ObjectDictionary<ToggleButton, String> buttonToCategory;


    /**
     * The control to raise/lowe terrain.
     */
    @NotNull
    private final RaiseLowerTerrainToolControl raiseLowerToolControl;

    /**
     * The control to smooth terrain.
     */
    @NotNull
    private final SmoothTerrainToolControl smoothToolControl;

    /**
     * The control to make rough surface terrain.
     */
    @NotNull
    private final RoughTerrainToolControl roughToolControl;

    /**
     * The control to make some levels terrain.
     */
    @NotNull
    private final LevelTerrainToolControl levelToolControl;

    /**
     * The control to make slopes on terrain.
     */
    @NotNull
    private final SlopeTerrainToolControl slopeToolControl;

    /**
     * The control to paint on terrain.
     */
    @NotNull
    private final PaintTerrainToolControl paintToolControl;

    /**
     * The button to raise/lower terrain.
     */
    @Nullable
    private ToggleButton raiseLowerButton;

    /**
     * The button to smooth terrain.
     */
    @Nullable
    private ToggleButton smoothButton;

    /**
     * The button to make rough terrain.
     */
    @Nullable
    private ToggleButton roughButton;

    /**
     * The button to change height of terrain by level.
     */
    @Nullable
    private ToggleButton levelButton;

    /**
     * The button to make slopes on terrain.
     */
    @Nullable
    private ToggleButton slopeButton;

    /**
     * The button to paint on terrain.
     */
    @Nullable
    private ToggleButton paintButton;

    /**
     * The settings of painting control.
     */
    @Nullable
    private GridPane paintControlSettings;

    /**
     * The settings of texture layers.
     */
    @Nullable
    private TextureLayerSettings textureLayerSettings;

    /**
     * The box to use tri-planar.
     */
    @Nullable
    private CheckBox triPlanarCheckBox;

    /**
     * The shininess field.
     */
    @Nullable
    private FloatTextField shininessField;

    public TerrainPaintingComponent(@NotNull PaintingComponentContainer container) {
        super(container);

        this.buttonToControl = DictionaryFactory.newObjectDictionary();
        this.buttonToCategory = DictionaryFactory.newObjectDictionary();
        this.raiseLowerToolControl = new RaiseLowerTerrainToolControl(this);
        this.smoothToolControl = new SmoothTerrainToolControl(this);
        this.roughToolControl = new RoughTerrainToolControl(this);
        this.levelToolControl = new LevelTerrainToolControl(this);
        this.slopeToolControl = new SlopeTerrainToolControl(this);
        this.paintToolControl = new PaintTerrainToolControl(this);
        this.toolControls = ArrayFactory.newArray(TerrainToolControl.class);
        this.toggleButtons = ArrayFactory.newArray(ToggleButton.class);
        this.toolControls.addAll(toArray(raiseLowerToolControl, smoothToolControl, roughToolControl,
                levelToolControl, slopeToolControl, paintToolControl));
        this.toggleButtons.addAll(toArray(raiseLowerButton, smoothButton, roughButton, levelButton,
                slopeButton, paintButton));

        var raiseLowerButton = getRaiseLowerButton();
        var slopeButton = getSlopeButton();
        var levelButton = getLevelButton();
        var roughButton = getRoughButton();
        var smoothButton = getSmoothButton();
        var paintButton = getPaintButton();

        buttonToControl.put(raiseLowerButton, raiseLowerToolControl);
        buttonToControl.put(smoothButton, smoothToolControl);
        buttonToControl.put(roughButton, roughToolControl);
        buttonToControl.put(levelButton, levelToolControl);
        buttonToControl.put(slopeButton, slopeToolControl);
        buttonToControl.put(paintButton, paintToolControl);
        buttonToCategory.put(raiseLowerButton, CATEGORY_RAISE_LOWER);
        buttonToCategory.put(slopeButton, CATEGORY_SLOPE);
        buttonToCategory.put(levelButton, CATEGORY_LEVEL);
        buttonToCategory.put(roughButton, CATEGORY_ROUGH);
        buttonToCategory.put(smoothButton, CATEGORY_SMOOTH);
        buttonToCategory.put(paintButton, CATEGORY_PAINT);

        raiseLowerButton.setSelected(true);

        setToolControl(raiseLowerToolControl);
        showCategory(CATEGORY_RAISE_LOWER);

        FxUtils.addClass(this, CssClasses.PROCESSING_COMPONENT_TERRAIN_EDITOR);
    }

    @Override
    @FromAnyThread
    protected @NotNull Class<TerrainPaintingStateWithEditorTool> getStateType() {
        return TerrainPaintingStateWithEditorTool.class;
    }

    @Override
    @FromAnyThread
    protected @NotNull Supplier<TerrainPaintingStateWithEditorTool> getStateConstructor() {
        return TerrainPaintingStateWithEditorTool::new;
    }

    @Override
    protected @NotNull Array<PaintingPropertyDefinition> getPaintingProperties() {

        var result = ArrayFactory.<PaintingPropertyDefinition>newArray(PaintingPropertyDefinition.class);

        result.add(new PaintingPropertyDefinition(CATEGORY_SLOPE, BOOLEAN, Messages.MODEL_PROPERTY_SMOOTHLY,
                PROPERTY_SLOPE_SMOOTHLY, false));
        result.add(new PaintingPropertyDefinition(CATEGORY_SLOPE, BOOLEAN, Messages.MODEL_PROPERTY_LIMITED,
                PROPERTY_SLOPE_LIMITED, false));

        result.add(new PaintingPropertyDefinition(CATEGORY_ROUGH, FLOAT, Messages.MODEL_PROPERTY_ROUGHNESS,
                PROPERTY_ROUGH_ROUGHNESS, 0F, 0F, Integer.MAX_VALUE));
        result.add(new PaintingPropertyDefinition(CATEGORY_ROUGH, FLOAT, Messages.MODEL_PROPERTY_FREQUENCY,
                PROPERTY_ROUGH_FREQUENCY, 0F, 0.1F, Integer.MAX_VALUE));
        result.add(new PaintingPropertyDefinition(CATEGORY_ROUGH, FLOAT, Messages.MODEL_PROPERTY_LACUNARITY,
                PROPERTY_ROUGH_LACUNARITY, 0F, 1.1F, Integer.MAX_VALUE));
        result.add(new PaintingPropertyDefinition(CATEGORY_ROUGH, FLOAT, Messages.MODEL_PROPERTY_OCTAVES,
                PROPERTY_ROUGH_OCTAVES, 0F, 0F, Integer.MAX_VALUE));
        result.add(new PaintingPropertyDefinition(CATEGORY_ROUGH, FLOAT, Messages.MODEL_PROPERTY_MIN_SCALE,
                PROPERTY_ROUGH_SCALE, 0F, 0F, Integer.MAX_VALUE));

        result.add(new PaintingPropertyDefinition(CATEGORY_LEVEL, FLOAT, Messages.MODEL_PROPERTY_LEVEL,
                PROPERTY_LEVEL_VALUE, 1F, 0F, Integer.MAX_VALUE));
        result.add(new PaintingPropertyDefinition(CATEGORY_LEVEL, BOOLEAN, Messages.MODEL_PROPERTY_SMOOTHLY,
                PROPERTY_LEVEL_SMOOTHLY, false));
        result.add(new PaintingPropertyDefinition(CATEGORY_LEVEL, BOOLEAN, Messages.MODEL_PROPERTY_USE_MARKER,
                PROPERTY_LEVEL_USE_MARKER, false));

        return result;
    }

    /**
     * Get the list of all tool controls.
     *
     * @return the list of all tool controls.
     */
    @FxThread
    private @NotNull Array<TerrainToolControl> getToolControls() {
        return toolControls;
    }

    /**
     * Get the list of all toggle buttons.
     *
     * @return the list of all toggle buttons.
     */
    @FxThread
    private @NotNull Array<ToggleButton> getToggleButtons() {
        return toggleButtons;
    }

    /**
     * Get the map with mapping toggle button to terrain control.
     *
     * @return the map with mapping toggle button to terrain control.
     */
    @FxThread
    private @NotNull ObjectDictionary<ToggleButton, TerrainToolControl> getButtonToControl() {
        return buttonToControl;
    }

    /**
     * Get the map with mapping toggle button to properties category.
     *
     * @return the map with mapping toggle button to properties category.
     */
    @FxThread
    private @NotNull ObjectDictionary<ToggleButton, String> getButtonToCategory() {
        return buttonToCategory;
    }

    /**
     * Get the button to change height of terrain by level.
     *
     * @return the button to change height of terrain by level.
     */
    @FxThread
    private @NotNull ToggleButton getLevelButton() {
        return notNull(levelButton);
    }

    /**
     * Get the button to paint on terrain.
     *
     * @return the button to paint on terrain.
     */
    @FxThread
    private @NotNull ToggleButton getPaintButton() {
        return notNull(paintButton);
    }

    /**
     * Get the button to make slopes on terrain.
     *
     * @return the button to make slopes on terrain.
     */
    @FxThread
    private @NotNull ToggleButton getSlopeButton() {
        return notNull(slopeButton);
    }

    /**
     * Get the button to make rough terrain.
     *
     * @return the button to make rough terrain.
     */
    @FxThread
    private @NotNull ToggleButton getRoughButton() {
        return notNull(roughButton);
    }

    /**
     * Get the button to smooth terrain.
     *
     * @return the button to smooth terrain.
     */
    @FxThread
    private @NotNull ToggleButton getSmoothButton() {
        return notNull(smoothButton);
    }

    /**
     * Get the button to raise/lower terrain.
     *
     * @return the button to raise/lower terrain.
     */
    @FxThread
    private @NotNull ToggleButton getRaiseLowerButton() {
        return notNull(raiseLowerButton);
    }

    @Override
    @FxThread
    protected void createComponents() {

        raiseLowerButton = new ToggleButton(StringUtils.EMPTY, new ImageView(Icons.TERRAIN_UP_32));
        raiseLowerButton.setOnAction(event -> switchMode((ToggleButton) event.getSource()));

        smoothButton = new ToggleButton(StringUtils.EMPTY, new ImageView(Icons.TERRAIN_SMOOTH_32));
        smoothButton.setOnAction(event -> switchMode((ToggleButton) event.getSource()));

        roughButton = new ToggleButton(StringUtils.EMPTY, new ImageView(Icons.TERRAIN_ROUGH_32));
        roughButton.setOnAction(event -> switchMode((ToggleButton) event.getSource()));

        levelButton = new ToggleButton(StringUtils.EMPTY, new ImageView(Icons.TERRAIN_LEVEL_32));
        levelButton.setOnAction(event -> switchMode((ToggleButton) event.getSource()));

        slopeButton = new ToggleButton(StringUtils.EMPTY, new ImageView(Icons.TERRAIN_SLOPE_32));
        slopeButton.setOnAction(event -> switchMode((ToggleButton) event.getSource()));

        paintButton = new ToggleButton(StringUtils.EMPTY, new ImageView(Icons.TERRAIN_PAINT_32));
        paintButton.setOnAction(event -> switchMode((ToggleButton) event.getSource()));

        var buttonsContainer = new GridPane();
        buttonsContainer.setAlignment(Pos.CENTER);
        buttonsContainer.setPadding(new Insets(2, 4, 2, 4));
        buttonsContainer.add(raiseLowerButton, 0, 0);
        buttonsContainer.add(smoothButton, 1, 0);
        buttonsContainer.add(roughButton, 2, 0);
        buttonsContainer.add(levelButton, 3, 0);
        buttonsContainer.add(slopeButton, 4, 0);
        buttonsContainer.add(paintButton, 5, 0);
        buttonsContainer.prefWidthProperty().bind(widthProperty());

        FxUtils.addChild(this, buttonsContainer);

        super.createComponents();

        createPaintControlSettings();

        FxUtils.addClass(raiseLowerButton, smoothButton, roughButton, CssClasses.MEDIUM_TOGGLE_BUTTON)
                .addClass(levelButton, slopeButton, paintButton, CssClasses.MEDIUM_TOGGLE_BUTTON)
                .addClass(buttonsContainer, CssClasses.DEF_GRID_PANE);
    }

    @Override
    protected void readState(@NotNull TerrainPaintingStateWithEditorTool state, @NotNull VarTable vars) {
        super.readState(state, vars);

        vars.set(PROPERTY_SLOPE_LIMITED, state.isSlopeLimited());
        vars.set(PROPERTY_SLOPE_SMOOTHLY, state.isSlopeSmoothly());
        vars.set(PROPERTY_LEVEL_VALUE, state.getLevelValue());
        vars.set(PROPERTY_LEVEL_SMOOTHLY, state.isLevelSmoothly());
        vars.set(PROPERTY_LEVEL_USE_MARKER, state.isLevelUseMarker());
    }

    @Override
    protected void syncValues(@NotNull VarTable vars, @NotNull TerrainPaintingStateWithEditorTool state) {

        var slopeLimited = vars.getBoolean(PROPERTY_SLOPE_LIMITED);
        var slopeSmoothly = vars.getBoolean(PROPERTY_SLOPE_SMOOTHLY);
        var levelUseMarker = vars.getBoolean(PROPERTY_LEVEL_USE_MARKER);
        var levelSmoothly = vars.getBoolean(PROPERTY_LEVEL_SMOOTHLY);
        var levelValue = vars.getFloat(PROPERTY_LEVEL_VALUE);
        var roughFrequency = vars.getFloat(PROPERTY_ROUGH_FREQUENCY);
        var roughLacunarity = vars.getFloat(PROPERTY_ROUGH_LACUNARITY);
        var roughRoughness = vars.getFloat(PROPERTY_ROUGH_ROUGHNESS);
        var roughScale = vars.getFloat(PROPERTY_ROUGH_SCALE);
        var roughOctaves = vars.getFloat(PROPERTY_ROUGH_OCTAVES);

        state.setSlopeLimited(slopeLimited);
        state.setSlopeSmoothly(slopeSmoothly);
        state.setLevelSmoothly(levelSmoothly);
        state.setLevelUseMarker(levelUseMarker);
        state.setLevelValue(levelValue);
        state.setRoughtFrequency(roughFrequency);
        state.setRoughtLacunarity(roughLacunarity);
        state.setRoughtScale(roughScale);
        state.setRoughtOctaves(roughOctaves);
        state.setRoughtRoughness(roughRoughness);

        super.syncValues(vars, state);
    }

    @Override
    @JmeThread
    protected void syncValues(
            @NotNull TerrainPaintingStateWithEditorTool state,
            @NotNull TerrainToolControl toolControl) {

        var brushPower = state.getBrushPower();
        var brushSize = state.getBrushSize();

        var toolControls = getToolControls();
        toolControls.forEach(brushSize, TerrainToolControl::setBrushSize);
        toolControls.forEach(brushPower, TerrainToolControl::setBrushPower);

        var slopeToolControl = getSlopeToolControl();
        slopeToolControl.setLock(state.isSlopeLimited());
        slopeToolControl.setPrecision(state.isSlopeSmoothly());

        var levelToolControl = getLevelToolControl();
        levelToolControl.setLevel(state.getLevelValue());
        levelToolControl.setPrecision(state.isLevelSmoothly());
        levelToolControl.setUseMarker(state.isLevelUseMarker());

        var roughToolControl = getRoughToolControl();
        roughToolControl.setFrequency(state.getRoughtFrequency());
        roughToolControl.setLacunarity(state.getRoughtLacunarity());
        roughToolControl.setOctaves(state.getRoughtOctaves());
        roughToolControl.setScale(state.getRoughtScale());
        roughToolControl.setRoughness(state.getRoughtRoughness());

        super.syncValues(state, toolControl);
    }

    /**
     * Create settings of paint control.
     */
    @FxThread
    private void createPaintControlSettings() {

        var triPlanarLabelLabel = new Label(Messages.MODEL_PROPERTY_TRI_PLANAR + ":");
        triPlanarLabelLabel.prefWidthProperty()
                .bind(widthProperty().multiply(LABEL_PERCENT));

        triPlanarCheckBox = new CheckBox();
        triPlanarCheckBox.prefWidthProperty()
                .bind(widthProperty().multiply(FIELD_PERCENT));

        var shininessLabel = new Label(Messages.MODEL_PROPERTY_SHININESS + ":");
        shininessLabel.prefWidthProperty().bind(widthProperty().multiply(LABEL_PERCENT));

        shininessField = new FloatTextField();
        shininessField.setMinMax(0F, (float) Integer.MAX_VALUE);
        shininessField.prefWidthProperty()
                .bind(widthProperty().multiply(FIELD_PERCENT));

        FxControlUtils.onSelectedChange(triPlanarCheckBox, this::changePaintControlTriPlanar);
        FxControlUtils.onValueChange(shininessField, this::changePaintControlShininess);

        textureLayerSettings = new TextureLayerSettings(this);

        paintControlSettings = new GridPane();
        paintControlSettings.add(shininessLabel, 0, 0);
        paintControlSettings.add(shininessField, 1, 0);
        paintControlSettings.add(triPlanarLabelLabel, 0, 1);
        paintControlSettings.add(triPlanarCheckBox, 1, 1);
        paintControlSettings.add(textureLayerSettings, 0, 2, 2, 1);

        FxUtils.addClass(paintControlSettings, CssClasses.DEF_GRID_PANE)
                .addClass(shininessLabel, triPlanarLabelLabel, CssClasses.ABSTRACT_PARAM_CONTROL_PARAM_NAME_SINGLE_ROW)
                .addClass(triPlanarCheckBox, CssClasses.PROPERTY_CONTROL_CHECK_BOX)
                .addClass(shininessField, CssClasses.PROPERTY_CONTROL_COMBO_BOX);
    }

    /**
     * Get the control to make some levels terrain.
     *
     * @return the control to make some levels terrain.
     */
    @FxThread
    private @NotNull LevelTerrainToolControl getLevelToolControl() {
        return levelToolControl;
    }

    /**
     * Get the control to make slopes on terrain.
     *
     * @return the control to make slopes on terrain.
     */
    @FxThread
    private @NotNull SlopeTerrainToolControl getSlopeToolControl() {
        return slopeToolControl;
    }

    /**
     * Get the control to make rough surface terrain.
     *
     * @return the control to make rough surface terrain.
     */
    @FxThread
    private @NotNull RoughTerrainToolControl getRoughToolControl() {
        return roughToolControl;
    }

    /**
     * Get the paint tool control.
     *
     * @return the control to paint textures.
     */
    @FxThread
    public @NotNull PaintTerrainToolControl getPaintToolControl() {
        return paintToolControl;
    }

    /**
     * Change the shininess value.
     */
    @FromAnyThread
    private void changePaintControlShininess(@NotNull Float newValue) {

        if (isIgnoreListeners()) {
            return;
        }

        var paintedObject = notNull(getPaintedObject());
        var material = NodeUtils.findMateial(paintedObject, mat -> mat.getParam("Shininess") != null);
        var param = material == null ? null : material.getParam("Shininess");
        var shininess = param == null ? 0F : (float) param.getValue();

        var operation = new PropertyOperation<ChangeConsumer, Node, Float>(paintedObject, TERRAIN_PARAM,
                newValue, shininess);

        operation.setApplyHandler((terrainQuad, value) ->
                NodeUtils.visitMaterials(terrainQuad, mat ->
                        MaterialUtils.safeSet(mat, "Shininess", value)));

        getChangeConsumer().execute(operation);
    }

    /**
     * Change using tri-planar textures.
     */
    @FromAnyThread
    private void changePaintControlTriPlanar(@NotNull Boolean newValue) {

        if (isIgnoreListeners()) {
            return;
        }

        var paintedObject = notNull(getPaintedObject());
        var operation = new PropertyOperation<ChangeConsumer, Node, Boolean>(paintedObject, TERRAIN_PARAM,
                newValue, !newValue);

        operation.setApplyHandler((terrainQuad, value) ->
                NodeUtils.visitMaterials(terrainQuad, mat ->
                        MaterialUtils.safeSet(mat, "useTriPlanarMapping", value)));

        getChangeConsumer().execute(operation);
    }

    /**
     * Get the box to use tri-planar.
     *
     * @return the box to use tri-planar.
     */
    @FxThread
    protected @NotNull CheckBox getTriPlanarCheckBox() {
        return notNull(triPlanarCheckBox);
    }

    /**
     * Get the the shininess field.
     *
     * @return the shininess field.
     */
    @FxThread
    protected @NotNull FloatTextField getShininessField() {
        return notNull(shininessField);
    }

    /**
     * Get the settings of painting control.
     *
     * @return the settings of painting control.
     */
    @FxThread
    private @NotNull TextureLayerSettings getTextureLayerSettings() {
        return notNull(textureLayerSettings);
    }

    /**
     * Get the paint control settings.
     *
     * @return the paint control settings.
     */
    @FxThread
    private @NotNull GridPane getPaintControlSettings() {
        return notNull(paintControlSettings);
    }

    /**
     * Switch editing mode.
     */
    @FxThread
    private void switchMode(@NotNull ToggleButton source) {

        if (!source.isSelected()) {
            source.setSelected(true);
            return;
        }

        getToggleButtons().forEach(source,
                (button, arg) -> button != arg,
                (toDeselect, arg) -> toDeselect.setSelected(false));

        var category = notNull(getButtonToCategory().get(source));

        showCategory(category);

        if (CATEGORY_PAINT.equals(category)) {
            FxUtils.addChild(this, getPaintControlSettings());
        } else {
            FxUtils.removeChild(this, getPaintControlSettings());
        }

        var toolControl = getButtonToControl().get(source);

        setToolControl(toolControl);

        if (!isShowed()) {
            return;
        }

        EXECUTOR_MANAGER.addJmeTask(() -> {
            var cursorNode = getCursorNode();
            cursorNode.removeControl(TerrainToolControl.class);
            cursorNode.addControl(toolControl);
        });
    }

    @Override
    @FxThread
    public void startPainting(@NotNull Object object) {
        super.startPainting(object);

        refreshProperties();

        var settings = getTextureLayerSettings();
        var paintButton = getPaintButton();

        if (object instanceof Terrain) {

            var terrain = (Terrain) object;
            var material = terrain.getMaterial();
            var materialDef = material.getMaterialDef();

            if (materialDef.getAssetName().equals("Common/MatDefs/Terrain/TerrainLighting.j3md")) {
                settings.setLayerToScaleName(LAYER_TO_SCALE_NAME);
                settings.setLayerToAlphaName(LAYER_TO_ALPHA_NAME);
                settings.setLayerToDiffuseName(LAYER_TO_DIFFUSE_NAME);
                settings.setLayerToNormalName(LAYER_TO_NORMAL_NAME);
                settings.setMaxLevels(12);
            }

            settings.refresh();
            settings.setVisible(true);
            paintButton.setVisible(true);

        } else {

            settings.setVisible(false);
            paintButton.setVisible(false);

            if (paintButton.isSelected()) {

                var raiseLowerButton = getRaiseLowerButton();
                raiseLowerButton.setSelected(true);

                switchMode(raiseLowerButton);
            }
        }
    }

    /**
     * Refresh terrain properties.
     */
    @FxThread
    private void refreshProperties() {
        setIgnoreListeners(true);
        try {

            var paintedObject = notNull(getPaintedObject());
            var material = NodeUtils.findMateial(paintedObject, mat -> mat.getParam("Shininess") != null);
            var shininessParam = material == null ? null : material.getParam("Shininess");
            var triPlanarMappingParam = material == null ? null : material.getParam("useTriPlanarMapping");
            var shininess = shininessParam == null ? 0F : (float) shininessParam.getValue();
            var triPlanarMapping = triPlanarMappingParam != null && (boolean) triPlanarMappingParam.getValue();

            getShininessField().setValue(shininess);
            getTriPlanarCheckBox().setSelected(triPlanarMapping);

        } finally {
            setIgnoreListeners(false);
        }
    }

    @Override
    @FxThread
    public void notifyChangeProperty(@NotNull Object object, @NotNull String propertyName) {
        refreshProperties();

        if (getPaintedObject() instanceof Terrain) {
            getTextureLayerSettings().notifyChangeProperty();
        }
    }

    @Override
    @FxThread
    public boolean isSupport(@NotNull Object object) {
        return object instanceof Node &&
                NodeUtils.findSpatial((Node) object, TerrainQuad.class::isInstance) != null;
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return Messages.PAINTING_COMPONENT_TERRAIN_EDITOR;
    }

    @Override
    public @NotNull Image getIcon() {
        return Icons.TERRAIN_16;
    }
}
