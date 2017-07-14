package com.ss.editor.ui.component.editing.terrain;

import static com.ss.rlib.util.ObjectUtils.notNull;
import static com.ss.rlib.util.array.ArrayFactory.toArray;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.jme3.scene.Node;
import com.jme3.terrain.Terrain;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.container.impl.AbstractProcessingComponent;
import com.ss.editor.ui.component.editing.EditingComponent;
import com.ss.editor.ui.component.editing.EditingComponentContainer;
import com.ss.editor.ui.component.editing.terrain.control.*;
import com.ss.editor.ui.component.editing.terrain.paint.TextureLayerSettings;
import com.ss.editor.ui.control.model.property.operation.ModelPropertyOperation;
import com.ss.editor.ui.control.property.AbstractPropertyControl;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.util.NodeUtils;
import com.ss.rlib.ui.control.input.FloatTextField;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import com.ss.rlib.util.dictionary.DictionaryFactory;
import com.ss.rlib.util.dictionary.ObjectDictionary;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * The implementation of a terrain editor.
 *
 * @author JavaSaBr
 */
public class TerrainEditingComponent extends AbstractProcessingComponent<TerrainQuad, EditingComponentContainer>
        implements EditingComponent {

    /**
     * The constant LABEL_PERCENT.
     */
    public static final double LABEL_PERCENT = 1D - AbstractPropertyControl.CONTROL_WIDTH_PERCENT_2;
    /**
     * The constant FIELD_PERCENT.
     */
    public static final double FIELD_PERCENT = AbstractPropertyControl.CONTROL_WIDTH_PERCENT_2;

    /**
     * The constant TERRAIN_PARAM.
     */
    @NotNull
    public static final String TERRAIN_PARAM = "terrainParam";

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
     * The map with mapping toggle button to its settings.
     */
    @NotNull
    private final ObjectDictionary<ToggleButton, Pane> buttonToSettings;

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
     * The brush size field.
     */
    @Nullable
    private FloatTextField brushSizeField;

    /**
     * The brush power field.
     */
    @Nullable
    private FloatTextField brushPowerField;

    /**
     * The container of control settings.
     */
    @Nullable
    private VBox controlSettings;

    /**
     * The settings of level control.
     */
    @Nullable
    private GridPane levelControlSettings;

    /**
     * The setting of using smoothly changing of terrain height.
     */
    @Nullable
    private CheckBox levelControlSmoothly;

    /**
     * The setting of using marker to detect a level.
     */
    @Nullable
    private CheckBox levelControlUseMarker;

    /**
     * The setting of target level.
     */
    @Nullable
    private FloatTextField levelControlLevelField;

    /**
     * The settings of rough control.
     */
    @Nullable
    private GridPane roughControlSettings;

    /**
     * The settings of roughness.
     */
    @Nullable
    private FloatTextField roughControlRoughnessField;

    /**
     * The settings of frequency.
     */
    @Nullable
    private FloatTextField roughControlFrequencyField;

    /**
     * The settings of lacunarity.
     */
    @Nullable
    private FloatTextField roughControlLacunarityField;

    /**
     * The settings of octaves.
     */
    @Nullable
    private FloatTextField roughControlOctavesField;

    /**
     * The settings of scale.
     */
    @Nullable
    private FloatTextField roughControlScaleField;

    /**
     * The settings of slope control.
     */
    @Nullable
    private GridPane slopeControlSettings;

    /**
     * The setting of using smoothly changing of terrain height.
     */
    @Nullable
    private CheckBox slopeControlSmoothly;

    /**
     * The setting of using limited between markers.
     */
    @Nullable
    private CheckBox slopeControlLimited;

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

    /**
     * The current tool control.
     */
    @Nullable
    private TerrainToolControl toolControl;

    /**
     * The flag of ignoring listeners.
     */
    private boolean ignoreListeners;

    /**
     * Instantiates a new Terrain editing component.
     */
    public TerrainEditingComponent() {
        this.buttonToControl = DictionaryFactory.newObjectDictionary();
        this.buttonToSettings = DictionaryFactory.newObjectDictionary();
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

        final ToggleButton raiseLowerButton = getRaiseLowerButton();

        buttonToControl.put(raiseLowerButton, raiseLowerToolControl);
        buttonToControl.put(getSmoothButton(), smoothToolControl);
        buttonToControl.put(getRoughButton(), roughToolControl);
        buttonToControl.put(getLevelButton(), levelToolControl);
        buttonToControl.put(getSlopeButton(), slopeToolControl);
        buttonToControl.put(getPaintButton(), paintToolControl);
        buttonToSettings.put(getSlopeButton(), slopeControlSettings);
        buttonToSettings.put(getLevelButton(), levelControlSettings);
        buttonToSettings.put(getRoughButton(), roughControlSettings);
        buttonToSettings.put(getPaintButton(), paintControlSettings);

        getLevelControlLevelField().setValue(1);
        getLevelControlUseMarker().setSelected(false);
        getLevelControlSmoothly().setSelected(true);
        getSlopeControlLimited().setSelected(true);
        getSlopeControlSmoothly().setSelected(true);
        getRoughControlFrequencyField().setValue(0.2f);
        getRoughControlLacunarityField().setValue(2.12f);
        getRoughControlOctavesField().setValue(8);
        getRoughControlRoughnessField().setValue(1.2f);
        getRoughControlScaleField().setValue(1.0f);

        raiseLowerButton.setSelected(true);

        getBrushSizeField().setValue(1);
        getBrushPowerField().setValue(1);

        setToolControl(raiseLowerToolControl);

        FXUtils.addClassTo(this, CSSClasses.PROCESSING_COMPONENT_TERRAIN_EDITOR);
    }

    /**
     * @return the list of all tool controls.
     */
    @NotNull
    private Array<TerrainToolControl> getToolControls() {
        return toolControls;
    }

    /**
     * @return the list of all toggle buttons.
     */
    @NotNull
    private Array<ToggleButton> getToggleButtons() {
        return toggleButtons;
    }

    /**
     * @return the map with mapping toggle button to terrain control.
     */
    @NotNull
    private ObjectDictionary<ToggleButton, TerrainToolControl> getButtonToControl() {
        return buttonToControl;
    }

    /**
     * @return the map with mapping toggle button to its settings.
     */
    @NotNull
    private ObjectDictionary<ToggleButton, Pane> getButtonToSettings() {
        return buttonToSettings;
    }

    /**
     * @return the current tool control.
     */
    @Nullable
    private TerrainToolControl getToolControl() {
        return toolControl;
    }

    /**
     * @param toolControl the current tool control.
     */
    private void setToolControl(@Nullable final TerrainToolControl toolControl) {
        this.toolControl = toolControl;
    }

    /**
     * @return the container of control settings.
     */
    @NotNull
    private VBox getControlSettings() {
        return notNull(controlSettings);
    }

    /**
     * @return the button to change height of terrain by level.
     */
    @NotNull
    private ToggleButton getLevelButton() {
        return notNull(levelButton);
    }

    /**
     * @return the button to paint on terrain.
     */
    @NotNull
    private ToggleButton getPaintButton() {
        return notNull(paintButton);
    }

    /**
     * @return the button to make slopes on terrain.
     */
    @NotNull
    private ToggleButton getSlopeButton() {
        return notNull(slopeButton);
    }

    /**
     * @return the button to make rough terrain.
     */
    @NotNull
    private ToggleButton getRoughButton() {
        return notNull(roughButton);
    }

    /**
     * @return the button to smooth terrain.
     */
    @NotNull
    private ToggleButton getSmoothButton() {
        return notNull(smoothButton);
    }

    /**
     * @return The button to raise/lower terrain.
     */
    @NotNull
    private ToggleButton getRaiseLowerButton() {
        return notNull(raiseLowerButton);
    }

    /**
     * @return the brush power field.
     */
    @NotNull
    private FloatTextField getBrushPowerField() {
        return notNull(brushPowerField);
    }

    /**
     * @return the brush size field.
     */
    @NotNull
    private FloatTextField getBrushSizeField() {
        return notNull(brushSizeField);
    }

    @Override
    protected void createComponents() {
        super.createComponents();

        raiseLowerButton = new ToggleButton(StringUtils.EMPTY, new ImageView(Icons.TERRAIN_UP_32));
        raiseLowerButton.setOnAction(this::switchMode);

        smoothButton = new ToggleButton(StringUtils.EMPTY, new ImageView(Icons.TERRAIN_SMOOTH_32));
        smoothButton.setOnAction(this::switchMode);

        roughButton = new ToggleButton(StringUtils.EMPTY, new ImageView(Icons.TERRAIN_ROUGH_32));
        roughButton.setOnAction(this::switchMode);

        levelButton = new ToggleButton(StringUtils.EMPTY, new ImageView(Icons.TERRAIN_LEVEL_32));
        levelButton.setOnAction(this::switchMode);

        slopeButton = new ToggleButton(StringUtils.EMPTY, new ImageView(Icons.TERRAIN_SLOPE_32));
        slopeButton.setOnAction(this::switchMode);

        paintButton = new ToggleButton(StringUtils.EMPTY, new ImageView(Icons.TERRAIN_PAINT_32));
        paintButton.setOnAction(this::switchMode);

        final GridPane buttonsContainer = new GridPane();
        buttonsContainer.setAlignment(Pos.CENTER);
        buttonsContainer.setPadding(new Insets(2, 4, 2, 4));
        buttonsContainer.add(raiseLowerButton, 0, 0);
        buttonsContainer.add(smoothButton, 1, 0);
        buttonsContainer.add(roughButton, 2, 0);
        buttonsContainer.add(levelButton, 3, 0);
        buttonsContainer.add(slopeButton, 4, 0);
        buttonsContainer.add(paintButton, 5, 0);
        buttonsContainer.prefWidthProperty().bind(widthProperty());

        FXUtils.addClassTo(buttonsContainer, CSSClasses.DEF_GRID_PANE);

        final Label brushSizeLabel = new Label(Messages.EDITING_COMPONENT_BRUSH_SIZE + ":");
        brushSizeLabel.prefWidthProperty().bind(widthProperty().multiply(LABEL_PERCENT));

        brushSizeField = new FloatTextField();
        brushSizeField.prefWidthProperty().bind(widthProperty().multiply(FIELD_PERCENT));
        brushSizeField.setMinMax(0.0001F, Integer.MAX_VALUE);
        brushSizeField.addChangeListener((observable, oldValue, newValue) -> changeBrushSize(newValue));

        final Label brushPowerLabel = new Label(Messages.EDITING_COMPONENT_BRUSH_POWER + ":");
        brushPowerLabel.prefWidthProperty().bind(widthProperty().multiply(LABEL_PERCENT));

        brushPowerField = new FloatTextField();
        brushPowerField.prefWidthProperty().bind(widthProperty().multiply(FIELD_PERCENT));
        brushPowerField.setScrollPower(3F);
        brushPowerField.setMinMax(0.0001F, Integer.MAX_VALUE);
        brushPowerField.addChangeListener((observable, oldValue, newValue) -> changeBrushPower(newValue));

        final GridPane brushSettingsContainer = new GridPane();
        brushSettingsContainer.add(brushSizeLabel, 0, 0);
        brushSettingsContainer.add(brushSizeField, 1, 0);
        brushSettingsContainer.add(brushPowerLabel, 0, 1);
        brushSettingsContainer.add(brushPowerField, 1, 1);

        controlSettings = new VBox();
        controlSettings.prefWidthProperty().bind(widthProperty());

        FXUtils.addToPane(buttonsContainer, this);
        FXUtils.addToPane(brushSettingsContainer, this);
        FXUtils.addToPane(controlSettings, this);

        createLevelControlSettings();
        createSlopeControlSettings();
        createRoughControlSettings();
        createPaintControlSettings();

        FXUtils.addClassesTo(raiseLowerButton, smoothButton, roughButton, levelButton, slopeButton, paintButton,
                CSSClasses.MEDIUM_TOGGLE_BUTTON);

        FXUtils.addClassTo(brushSettingsContainer, CSSClasses.DEF_GRID_PANE);
        FXUtils.addClassTo(controlSettings, CSSClasses.DEF_VBOX);
        FXUtils.addClassTo(brushSizeLabel, brushPowerLabel, CSSClasses.ABSTRACT_PARAM_CONTROL_PARAM_NAME_SINGLE_ROW);
        FXUtils.addClassTo(brushSizeField, brushPowerField, CSSClasses.ABSTRACT_PARAM_CONTROL_COMBO_BOX);
    }

    /**
     * Create settings of slope control.
     */
    private void createSlopeControlSettings() {

        final Label smoothlyLabel = new Label(Messages.EDITING_COMPONENT_SMOOTHLY + ":");
        smoothlyLabel.prefWidthProperty().bind(widthProperty().multiply(LABEL_PERCENT));

        slopeControlSmoothly = new CheckBox();
        slopeControlSmoothly.prefWidthProperty().bind(widthProperty().multiply(FIELD_PERCENT));
        slopeControlSmoothly.selectedProperty().addListener((observable, oldValue, newValue) -> changeSlopeControlSmoothly(newValue));

        final Label limitedLabel = new Label(Messages.EDITING_COMPONENT_LIMITED + ":");
        limitedLabel.prefWidthProperty().bind(widthProperty().multiply(LABEL_PERCENT));

        slopeControlLimited = new CheckBox();
        slopeControlLimited.prefWidthProperty().bind(widthProperty().multiply(FIELD_PERCENT));
        slopeControlLimited.selectedProperty().addListener((observable, oldValue, newValue) -> changeSlopeControlLimited(newValue));

        slopeControlSettings = new GridPane();
        slopeControlSettings.add(smoothlyLabel, 0, 0);
        slopeControlSettings.add(slopeControlSmoothly, 1, 0);
        slopeControlSettings.add(limitedLabel, 0, 1);
        slopeControlSettings.add(slopeControlLimited, 1, 1);

        FXUtils.addClassTo(slopeControlSettings, CSSClasses.DEF_GRID_PANE);
        FXUtils.addClassTo(smoothlyLabel, limitedLabel, CSSClasses.ABSTRACT_PARAM_CONTROL_PARAM_NAME_SINGLE_ROW);
        FXUtils.addClassTo(slopeControlSmoothly, slopeControlLimited, CSSClasses.ABSTRACT_PARAM_CONTROL_CHECK_BOX);
    }

    /**
     * Create settings of level control.
     */
    private void createLevelControlSettings() {

        final Label smoothlyLabel = new Label(Messages.EDITING_COMPONENT_SMOOTHLY + ":");
        smoothlyLabel.prefWidthProperty().bind(widthProperty().multiply(LABEL_PERCENT));

        levelControlSmoothly = new CheckBox();
        levelControlSmoothly.prefWidthProperty().bind(widthProperty().multiply(FIELD_PERCENT));
        levelControlSmoothly.selectedProperty().addListener((observable, oldValue, newValue) -> changeLevelControlSmoothly(newValue));

        final Label useMarkerLabel = new Label(Messages.EDITING_COMPONENT_USE_MARKER + ":");
        useMarkerLabel.prefWidthProperty().bind(widthProperty().multiply(LABEL_PERCENT));

        levelControlUseMarker = new CheckBox();
        levelControlUseMarker.prefWidthProperty().bind(widthProperty().multiply(FIELD_PERCENT));
        levelControlUseMarker.selectedProperty().addListener((observable, oldValue, newValue) -> changeLevelControlUseMarker(newValue));

        final Label levelLabel = new Label(Messages.EDITING_COMPONENT_LEVEL + ":");
        levelLabel.prefWidthProperty().bind(widthProperty().multiply(LABEL_PERCENT));

        levelControlLevelField = new FloatTextField();
        levelControlLevelField.prefWidthProperty().bind(widthProperty().multiply(FIELD_PERCENT));
        levelControlLevelField.setMinMax(0F, Integer.MAX_VALUE);
        levelControlLevelField.addChangeListener((observable, oldValue, newValue) -> changeLevelControlLevel(newValue));
        levelControlLevelField.disableProperty().bind(levelControlUseMarker.selectedProperty());

        levelControlSettings = new GridPane();
        levelControlSettings.add(smoothlyLabel, 0, 0);
        levelControlSettings.add(levelControlSmoothly, 1, 0);
        levelControlSettings.add(useMarkerLabel, 0, 1);
        levelControlSettings.add(levelControlUseMarker, 1, 1);
        levelControlSettings.add(levelLabel, 0, 2);
        levelControlSettings.add(levelControlLevelField, 1, 2);

        FXUtils.addClassTo(smoothlyLabel, useMarkerLabel, levelLabel,
                CSSClasses.ABSTRACT_PARAM_CONTROL_PARAM_NAME_SINGLE_ROW);

        FXUtils.addClassesTo(levelControlSettings, CSSClasses.DEF_GRID_PANE);
        FXUtils.addClassTo(levelControlLevelField, CSSClasses.ABSTRACT_PARAM_CONTROL_COMBO_BOX);
        FXUtils.addClassTo(levelControlSmoothly, levelControlUseMarker, CSSClasses.ABSTRACT_PARAM_CONTROL_CHECK_BOX);
    }

    /**
     * Create settings of rough control.
     */
    private void createRoughControlSettings() {

        final Label roughnessLabel = new Label(Messages.EDITING_COMPONENT_ROUGHNESS + ":");
        roughnessLabel.prefWidthProperty().bind(widthProperty().multiply(LABEL_PERCENT));

        roughControlRoughnessField = new FloatTextField();
        roughControlRoughnessField.prefWidthProperty().bind(widthProperty().multiply(FIELD_PERCENT));
        roughControlRoughnessField.setMinMax(0F, Integer.MAX_VALUE);
        roughControlRoughnessField.addChangeListener((observable, oldValue, newValue) -> changeRoughControlRoughness(newValue));

        final Label frequencyLabel = new Label(Messages.EDITING_COMPONENT_FREQUENCY + ":");
        frequencyLabel.prefWidthProperty().bind(widthProperty().multiply(LABEL_PERCENT));

        roughControlFrequencyField = new FloatTextField();
        roughControlFrequencyField.prefWidthProperty().bind(widthProperty().multiply(FIELD_PERCENT));
        roughControlFrequencyField.setMinMax(0.1F, Integer.MAX_VALUE);
        roughControlFrequencyField.addChangeListener((observable, oldValue, newValue) -> changeRoughControlFrequency(newValue));

        final Label lacunarityLabel = new Label(Messages.EDITING_COMPONENT_LACUNARITY + ":");
        lacunarityLabel.prefWidthProperty().bind(widthProperty().multiply(LABEL_PERCENT));

        roughControlLacunarityField = new FloatTextField();
        roughControlLacunarityField.prefWidthProperty().bind(widthProperty().multiply(FIELD_PERCENT));
        roughControlLacunarityField.setMinMax(1.1F, Integer.MAX_VALUE);
        roughControlLacunarityField.addChangeListener((observable, oldValue, newValue) -> changeRoughControlLacunarity(newValue));

        final Label octavesLabel = new Label(Messages.EDITING_COMPONENT_OCTAVES + ":");
        octavesLabel.prefWidthProperty().bind(widthProperty().multiply(LABEL_PERCENT));

        roughControlOctavesField = new FloatTextField();
        roughControlOctavesField.prefWidthProperty().bind(widthProperty().multiply(FIELD_PERCENT));
        roughControlOctavesField.setMinMax(0F, Integer.MAX_VALUE);
        roughControlOctavesField.addChangeListener((observable, oldValue, newValue) -> changeRoughControlOctaves(newValue));

        final Label scaleLabel = new Label(Messages.EDITING_COMPONENT_SCALE + ":");
        scaleLabel.prefWidthProperty().bind(widthProperty().multiply(LABEL_PERCENT));

        roughControlScaleField = new FloatTextField();
        roughControlScaleField.prefWidthProperty().bind(widthProperty().multiply(FIELD_PERCENT));
        roughControlScaleField.setMinMax(0F, Integer.MAX_VALUE);
        roughControlScaleField.addChangeListener((observable, oldValue, newValue) -> changeRoughControlScale(newValue));

        roughControlSettings = new GridPane();
        roughControlSettings.add(roughnessLabel, 0, 0);
        roughControlSettings.add(roughControlRoughnessField, 1, 0);
        roughControlSettings.add(frequencyLabel, 0, 1);
        roughControlSettings.add(roughControlFrequencyField, 1, 1);
        roughControlSettings.add(lacunarityLabel, 0, 2);
        roughControlSettings.add(roughControlLacunarityField, 1, 2);
        roughControlSettings.add(octavesLabel, 0, 3);
        roughControlSettings.add(roughControlOctavesField, 1, 3);
        roughControlSettings.add(scaleLabel, 0, 4);
        roughControlSettings.add(roughControlScaleField, 1, 4);

        FXUtils.addClassTo(roughnessLabel, frequencyLabel, lacunarityLabel, octavesLabel, scaleLabel,
                CSSClasses.ABSTRACT_PARAM_CONTROL_PARAM_NAME_SINGLE_ROW);


        FXUtils.addClassesTo(roughControlSettings, CSSClasses.DEF_GRID_PANE);
        FXUtils.addClassTo(roughControlRoughnessField, roughControlFrequencyField, roughControlLacunarityField,
                roughControlOctavesField, roughControlScaleField, CSSClasses.ABSTRACT_PARAM_CONTROL_COMBO_BOX);
    }

    /**
     * Create settings of paint control.
     */
    private void createPaintControlSettings() {

        final Label triPlanarLabelLabel = new Label(Messages.EDITING_COMPONENT_TRI_PLANAR + ":");
        triPlanarLabelLabel.prefWidthProperty().bind(widthProperty().multiply(LABEL_PERCENT));

        triPlanarCheckBox = new CheckBox();
        triPlanarCheckBox.prefWidthProperty().bind(widthProperty().multiply(FIELD_PERCENT));
        triPlanarCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> changePaintControlTriPlanar(newValue));

        final Label shininessLabel = new Label(Messages.EDITING_COMPONENT_SHININESS + ":");
        shininessLabel.prefWidthProperty().bind(widthProperty().multiply(LABEL_PERCENT));

        shininessField = new FloatTextField();
        shininessField.prefWidthProperty().bind(widthProperty().multiply(FIELD_PERCENT));
        shininessField.setMinMax(0F, Integer.MAX_VALUE);
        shininessField.addChangeListener((observable, oldValue, newValue) -> changePaintControlShininess(newValue));

        textureLayerSettings = new TextureLayerSettings(this);

        paintControlSettings = new GridPane();
        paintControlSettings.add(shininessLabel, 0, 0);
        paintControlSettings.add(shininessField, 1, 0);
        paintControlSettings.add(triPlanarLabelLabel, 0, 1);
        paintControlSettings.add(triPlanarCheckBox, 1, 1);
        paintControlSettings.add(textureLayerSettings, 0, 2, 2, 1);

        FXUtils.addClassesTo(paintControlSettings, CSSClasses.DEF_GRID_PANE);
        FXUtils.addClassTo(shininessLabel, triPlanarLabelLabel, CSSClasses.ABSTRACT_PARAM_CONTROL_PARAM_NAME_SINGLE_ROW);
        FXUtils.addClassTo(triPlanarCheckBox, CSSClasses.ABSTRACT_PARAM_CONTROL_CHECK_BOX);
        FXUtils.addClassTo(shininessField, CSSClasses.ABSTRACT_PARAM_CONTROL_COMBO_BOX);
    }

    /**
     * @return the control to make some levels terrain.
     */
    @NotNull
    private LevelTerrainToolControl getLevelToolControl() {
        return levelToolControl;
    }

    /**
     * @return the control to make slopes on terrain.
     */
    @NotNull
    private SlopeTerrainToolControl getSlopeToolControl() {
        return slopeToolControl;
    }

    /**
     * @return the control to make rough surface terrain.
     */
    @NotNull
    private RoughTerrainToolControl getRoughToolControl() {
        return roughToolControl;
    }

    /**
     * Gets paint tool control.
     *
     * @return the control to paint textures.
     */
    @NotNull
    public PaintTerrainToolControl getPaintToolControl() {
        return paintToolControl;
    }

    /**
     * Change the shininess value.
     */
    @FromAnyThread
    private void changePaintControlShininess(@NotNull final Float newValue) {
        if (isIgnoreListeners()) return;

        final TerrainQuad processedObject = getProcessedObject();
        final Material mat = processedObject.getMaterial();
        final MatParam param = mat.getParam("Shininess");
        final float shininess = param == null ? 0F : (float) param.getValue();

        final ModelPropertyOperation<TerrainQuad, Float> operation =
                new ModelPropertyOperation<>(processedObject, TERRAIN_PARAM, newValue, shininess);

        operation.setApplyHandler((terrainQuad, value) -> {
            NodeUtils.visitGeometry(terrainQuad, geometry -> {
                final Material material = geometry.getMaterial();
                material.setFloat("Shininess", value);
            });
        });

        final ModelChangeConsumer changeConsumer = getChangeConsumer();
        changeConsumer.execute(operation);
    }

    /**
     * Change using tri-planar textures.
     */
    @FromAnyThread
    private void changePaintControlTriPlanar(@NotNull final Boolean newValue) {
        if (isIgnoreListeners()) return;

        final TerrainQuad processedObject = getProcessedObject();

        final ModelPropertyOperation<TerrainQuad, Boolean> operation =
                new ModelPropertyOperation<>(processedObject, TERRAIN_PARAM, newValue, !newValue);

        operation.setApplyHandler((terrainQuad, value) -> {
            NodeUtils.visitGeometry(terrainQuad, geometry -> {
                final Material material = geometry.getMaterial();
                material.setBoolean("useTriPlanarMapping", value);
            });
        });

        final ModelChangeConsumer changeConsumer = getChangeConsumer();
        changeConsumer.execute(operation);
    }

    /**
     * Change using smoothly editing.
     */
    @FromAnyThread
    private void changeLevelControlSmoothly(@NotNull final Boolean newValue) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> getLevelToolControl().setPrecision(!newValue));
    }

    /**
     * Change using marker for level control.
     */
    @FromAnyThread
    private void changeLevelControlUseMarker(@NotNull final Boolean newValue) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> getLevelToolControl().setUseMarker(newValue));
    }

    /**
     * Change a level of a level control.
     */
    @FromAnyThread
    private void changeLevelControlLevel(@NotNull final Float newLevel) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> getLevelToolControl().setLevel(newLevel));
    }

    /**
     * Change using smoothly editing.
     */
    @FromAnyThread
    private void changeSlopeControlSmoothly(@NotNull final Boolean newValue) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> getSlopeToolControl().setPrecision(!newValue));
    }

    /**
     * Change using limited editing.
     */
    @FromAnyThread
    private void changeSlopeControlLimited(@NotNull final Boolean newValue) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> getSlopeToolControl().setLock(newValue));
    }

    /**
     * Change scale of a rough control.
     */
    @FromAnyThread
    private void changeRoughControlScale(@NotNull final Float newScale) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> getRoughToolControl().setScale(newScale));
    }

    /**
     * Change frequency of a rough control.
     */
    @FromAnyThread
    private void changeRoughControlFrequency(@NotNull final Float newFrequency) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> getRoughToolControl().setFrequency(newFrequency));
    }

    /**
     * Change lacunarity of a rough control.
     */
    @FromAnyThread
    private void changeRoughControlLacunarity(@NotNull final Float newLacunarity) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> getRoughToolControl().setLacunarity(newLacunarity));
    }

    /**
     * Change octaves of a rough control.
     */
    @FromAnyThread
    private void changeRoughControlOctaves(@NotNull final Float newOctaves) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> getRoughToolControl().setOctaves(newOctaves));
    }

    /**
     * Change roughness of a rough control.
     */
    @FromAnyThread
    private void changeRoughControlRoughness(@NotNull final Float newRoughness) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> getRoughToolControl().setRoughness(newRoughness));
    }

    /**
     * Change brush sizes.
     */
    @FromAnyThread
    private void changeBrushSize(@NotNull final Float size) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            final Array<TerrainToolControl> toolControls = getToolControls();
            toolControls.forEach(size, TerrainToolControl::setBrushSize);
        });
    }

    /**
     * Change brush powers.
     */
    @FromAnyThread
    private void changeBrushPower(@NotNull final Float power) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            final Array<TerrainToolControl> toolControls = getToolControls();
            toolControls.forEach(power, TerrainToolControl::setBrushPower);
        });
    }

    /**
     * Gets tri planar check box.
     *
     * @return the box to use tri-planar.
     */
    @NotNull
    protected CheckBox getTriPlanarCheckBox() {
        return notNull(triPlanarCheckBox);
    }

    /**
     * Gets shininess field.
     *
     * @return the shininess field.
     */
    @NotNull
    protected FloatTextField getShininessField() {
        return notNull(shininessField);
    }

    /**
     * @return the setting of using smoothly changing of terrain height.
     */
    @NotNull
    private CheckBox getLevelControlSmoothly() {
        return notNull(levelControlSmoothly);
    }

    /**
     * @return the setting of using marker to detect a level.
     */
    @NotNull
    private CheckBox getLevelControlUseMarker() {
        return notNull(levelControlUseMarker);
    }

    /**
     * @return the setting of target level.
     */
    @NotNull
    private FloatTextField getLevelControlLevelField() {
        return notNull(levelControlLevelField);
    }

    /**
     * @return the setting of using limited between markers.
     */
    @NotNull
    private CheckBox getSlopeControlLimited() {
        return notNull(slopeControlLimited);
    }

    /**
     * @return the setting of using smoothly changing of terrain height.
     */
    @NotNull
    private CheckBox getSlopeControlSmoothly() {
        return notNull(slopeControlSmoothly);
    }

    /**
     * @return the settings of frequency.
     */
    @NotNull
    private FloatTextField getRoughControlFrequencyField() {
        return notNull(roughControlFrequencyField);
    }

    /**
     * @return the settings of lacunarity.
     */
    @NotNull
    private FloatTextField getRoughControlLacunarityField() {
        return notNull(roughControlLacunarityField);
    }

    /**
     * @return the settings of octaves.
     */
    @NotNull
    private FloatTextField getRoughControlOctavesField() {
        return notNull(roughControlOctavesField);
    }

    /**
     * @return the settings of roughness.
     */
    @NotNull
    private FloatTextField getRoughControlRoughnessField() {
        return notNull(roughControlRoughnessField);
    }

    /**
     * @return the settings of scale.
     */
    @NotNull
    private FloatTextField getRoughControlScaleField() {
        return notNull(roughControlScaleField);
    }

    /**
     * @return the settings of painting control.
     */
    @NotNull
    private TextureLayerSettings getTextureLayerSettings() {
        return notNull(textureLayerSettings);
    }

    /**
     * Switch editing mode.
     */
    private void switchMode(@NotNull final ActionEvent event) {

        final ToggleButton source = (ToggleButton) event.getSource();

        if (!source.isSelected()) {
            source.setSelected(true);
            return;
        }

        getToggleButtons().forEach(source, (button, arg) -> button !=
                arg, (toggleButton, arg) -> toggleButton.setSelected(false));

        final ObjectDictionary<ToggleButton, Pane> buttonToSettings = getButtonToSettings();
        final Pane settings = buttonToSettings.get(source);

        final VBox controlSettings = getControlSettings();
        final ObservableList<javafx.scene.Node> children = controlSettings.getChildren();
        children.clear();

        if (settings != null) {
            children.add(settings);
        }

        final ObjectDictionary<ToggleButton, TerrainToolControl> buttonToControl = getButtonToControl();
        final TerrainToolControl toolControl = buttonToControl.get(source);

        setToolControl(toolControl);

        if (!isShowed()) return;

        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            final Node cursorNode = getCursorNode();
            cursorNode.removeControl(TerrainToolControl.class);
            cursorNode.addControl(toolControl);
        });
    }

    @Override
    public void startProcessing(@NotNull final Object object) {
        super.startProcessing(object);

        final TextureLayerSettings settings = getTextureLayerSettings();

        final Terrain terrain = (Terrain) object;
        final Material material = terrain.getMaterial();
        final MaterialDef materialDef = material.getMaterialDef();

        if (materialDef.getAssetName().equals("Common/MatDefs/Terrain/TerrainLighting.j3md")) {
            settings.setLayerToScaleName(LAYER_TO_SCALE_NAME);
            settings.setLayerToAlphaName(LAYER_TO_ALPHA_NAME);
            settings.setLayerToDiffuseName(LAYER_TO_DIFFUSE_NAME);
            settings.setLayerToNormalName(LAYER_TO_NORMAL_NAME);
            settings.setMaxLevels(12);
        }

        refreshProperties();

        settings.refresh();
    }

    /**
     * Refresh terrain properties.
     */
    private void refreshProperties() {
        setIgnoreListeners(true);
        try {

            final Terrain terrain = getProcessedObject();
            final Material material = terrain.getMaterial();
            final FloatTextField shininessField = getShininessField();
            final CheckBox triPlanarCheckBox = getTriPlanarCheckBox();
            final MatParam shininess = material.getParam("Shininess");
            final MatParam useTriPlanarMapping = material.getParam("useTriPlanarMapping");

            shininessField.setValue(shininess == null ? 0F : (float) shininess.getValue());
            triPlanarCheckBox.setSelected(useTriPlanarMapping != null && (boolean) useTriPlanarMapping.getValue());

        } finally {
            setIgnoreListeners(false);
        }
    }

    @Override
    public void notifyChangeProperty(@NotNull final Object object, @NotNull final String propertyName) {
        refreshProperties();

        final TextureLayerSettings textureLayerSettings = getTextureLayerSettings();
        textureLayerSettings.notifyChangeProperty();
    }

    @Override
    public boolean isSupport(@NotNull final Object object) {
        return object instanceof TerrainQuad;
    }

    @Override
    public void notifyShowed() {
        super.notifyShowed();
        EXECUTOR_MANAGER.addEditorThreadTask(() -> getCursorNode().addControl(getToolControl()));
    }

    @Override
    public void notifyHided() {
        super.notifyHided();
        EXECUTOR_MANAGER.addEditorThreadTask(() -> getCursorNode().removeControl(TerrainToolControl.class));
    }

    /**
     * @param ignoreListeners the flag of ignoring listeners.
     */
    private void setIgnoreListeners(final boolean ignoreListeners) {
        this.ignoreListeners = ignoreListeners;
    }

    /**
     * @return the flag of ignoring listeners.
     */
    private boolean isIgnoreListeners() {
        return ignoreListeners;
    }

    @NotNull
    @Override
    public String getName() {
        return "Terrain editor";
    }
}
