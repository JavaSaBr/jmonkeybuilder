package com.ss.editor.ui.component.painting.terrain.paint;

import static com.ss.editor.ui.component.painting.PaintingComponentContainer.FIELD_PERCENT;
import static com.ss.editor.ui.component.painting.PaintingComponentContainer.LABEL_PERCENT;
import com.ss.editor.Messages;
import com.ss.editor.ui.control.choose.NamedChooseTextureControl;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.fx.control.input.FloatTextField;
import com.ss.rlib.fx.util.FXUtils;
import javafx.beans.binding.DoubleBinding;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The implementation of a list cell to edit/show a texture layer.
 *
 * @author JavaSaBr
 */
public class TextureLayerCell extends ListCell<TextureLayer> {

    /**
     * The diffuse texture chooser.
     */
    @NotNull
    private final NamedChooseTextureControl diffuseTextureControl;

    /**
     * The normal texture chooser.
     */
    @NotNull
    private final NamedChooseTextureControl normalTextureControl;

    /**
     * The texture scale field.
     */
    @NotNull
    private final FloatTextField scaleField;

    /**
     * The layer field.
     */
    @NotNull
    private final Label layerField;

    /**
     * The settings container.
     */
    @NotNull
    private final GridPane settingContainer;

    /**
     * The flag of ignoring listeners.
     */
    private boolean ignoreListeners;

    /**
     * Instantiates a new Texture layer cell.
     *  @param prefWidth the pref width
     * @param maxWidth  the max width
     */
    public TextureLayerCell(final DoubleBinding prefWidth,
                            final DoubleBinding maxWidth) {

        settingContainer = new GridPane();
        settingContainer.prefWidthProperty().bind(prefWidth);
        settingContainer.maxWidthProperty().bind(maxWidth);

        layerField = new Label();
        layerField.prefWidthProperty().bind(settingContainer.widthProperty());

        diffuseTextureControl = new NamedChooseTextureControl("Diffuse");
        diffuseTextureControl.setChangeHandler(this::updateDiffuse);
        diffuseTextureControl.setControlWidthPercent(PropertyControl.CONTROL_WIDTH_PERCENT_2);

        normalTextureControl = new NamedChooseTextureControl("Normal");
        normalTextureControl.setChangeHandler(this::updateNormal);
        normalTextureControl.setControlWidthPercent(PropertyControl.CONTROL_WIDTH_PERCENT_2);

        final Label scaleLabel = new Label(Messages.MODEL_PROPERTY_SCALE + ":");
        scaleLabel.prefWidthProperty().bind(settingContainer.widthProperty().multiply(LABEL_PERCENT));

        scaleField = new FloatTextField();
        scaleField.setMinMax(0.0001F, (float) Integer.MAX_VALUE);
        scaleField.setScrollPower(3F);
        scaleField.addChangeListener((observable, oldValue, newValue) -> updateScale(newValue));
        scaleField.prefWidthProperty().bind(settingContainer.widthProperty().multiply(FIELD_PERCENT));

        settingContainer.add(layerField, 0, 0, 2, 1);
        settingContainer.add(diffuseTextureControl, 0, 1, 2, 1);
        settingContainer.add(normalTextureControl, 0, 2, 2, 1);
        settingContainer.add(scaleLabel, 0, 3);
        settingContainer.add(scaleField, 1, 3);

        FXUtils.addClassTo(settingContainer, CssClasses.DEF_GRID_PANE);
        FXUtils.addClassTo(layerField, CssClasses.ABSTRACT_PARAM_CONTROL_PARAM_NAME);
        FXUtils.addClassTo(scaleLabel, CssClasses.ABSTRACT_PARAM_CONTROL_PARAM_NAME_SINGLE_ROW);
        FXUtils.addClassTo(scaleField, CssClasses.PROPERTY_CONTROL_COMBO_BOX);
        FXUtils.addClassTo(this, CssClasses.PROCESSING_COMPONENT_TERRAIN_EDITOR_TEXTURE_LAYER);
    }

    /**
     * @return the texture scale field.
     */
    @NotNull
    private FloatTextField getScaleField() {
        return scaleField;
    }

    /**
     * @return the diffuse texture chooser.
     */
    @NotNull
    private NamedChooseTextureControl getDiffuseTextureControl() {
        return diffuseTextureControl;
    }

    /**
     * @return the normal texture chooser.
     */
    @NotNull
    private NamedChooseTextureControl getNormalTextureControl() {
        return normalTextureControl;
    }

    /**
     * @return the layer field.
     */
    @NotNull
    private Label getLayerField() {
        return layerField;
    }

    /**
     * @return the settings container.
     */
    @NotNull
    private GridPane getSettingContainer() {
        return settingContainer;
    }

    /**
     * @param ignoreListeners true if need to ignore listeners.
     */
    private void setIgnoreListeners(final boolean ignoreListeners) {
        this.ignoreListeners = ignoreListeners;
    }

    /**
     * @return true if need to ignore listeners.
     */
    private boolean isIgnoreListeners() {
        return ignoreListeners;
    }

    /**
     * Update texture scale.
     */
    private void updateScale(@NotNull final Float newValue) {
        if (isIgnoreListeners()) return;
        getItem().setScale(newValue);

    }

    /**
     * Update a normal texture.
     */
    private void updateNormal() {
        if (isIgnoreListeners()) return;
        final NamedChooseTextureControl normalTextureControl = getNormalTextureControl();
        final Path textureFile = normalTextureControl.getTextureFile();
        getItem().setNormalFile(textureFile);
    }

    /**
     * Update a diffuse texture.
     */
    private void updateDiffuse() {
        if (isIgnoreListeners()) return;
        final NamedChooseTextureControl diffuseTextureControl = getDiffuseTextureControl();
        final Path textureFile = diffuseTextureControl.getTextureFile();
        getItem().setDiffuseFile(textureFile);
    }

    @Override
    protected void updateItem(final TextureLayer item, final boolean empty) {
        super.updateItem(item, empty);

        setText("");

        if (item == null) {
            setGraphic(null);
            return;
        }

        refresh();
        setGraphic(getSettingContainer());
    }

    /**
     * Refresh this cell.
     */
    protected void refresh() {

        final TextureLayer item = getItem();
        if (item == null) return;

        setIgnoreListeners(true);
        try {

            final FloatTextField scaleField = getScaleField();
            scaleField.setValue(item.getScale());

            final NamedChooseTextureControl normalTextureControl = getNormalTextureControl();
            normalTextureControl.setTextureFile(item.getNormalFile());

            final NamedChooseTextureControl diffuseTextureControl = getDiffuseTextureControl();
            diffuseTextureControl.setTextureFile(item.getDiffuseFile());

            final Label layerField = getLayerField();
            layerField.setText(Messages.MODEL_PROPERTY_LAYER + " #" + (item.getLayer() + 1));

        } finally {
            setIgnoreListeners(false);
        }
    }
}
