package com.ss.editor.ui.component.painting.terrain.paint;

import static com.ss.editor.ui.component.painting.PaintingComponentContainer.FIELD_PERCENT;
import static com.ss.editor.ui.component.painting.PaintingComponentContainer.LABEL_PERCENT;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.control.choose.NamedChooseTextureControl;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.fx.control.input.FloatTextField;
import com.ss.rlib.fx.util.FXUtils;
import com.ss.rlib.fx.util.FxControlUtils;
import com.ss.rlib.fx.util.FxUtils;
import javafx.beans.binding.DoubleBinding;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    public TextureLayerCell(@NotNull DoubleBinding prefWidth, @NotNull DoubleBinding maxWidth) {

        settingContainer = new GridPane();
        settingContainer.prefWidthProperty().bind(prefWidth);
        settingContainer.maxWidthProperty().bind(maxWidth);

        layerField = new Label();
        layerField.prefWidthProperty()
                .bind(settingContainer.widthProperty());

        diffuseTextureControl = new NamedChooseTextureControl("Diffuse");
        diffuseTextureControl.setChangeHandler(this::updateDiffuse);
        diffuseTextureControl.setControlWidthPercent(PropertyControl.CONTROL_WIDTH_PERCENT_2);

        normalTextureControl = new NamedChooseTextureControl("Normal");
        normalTextureControl.setChangeHandler(this::updateNormal);
        normalTextureControl.setControlWidthPercent(PropertyControl.CONTROL_WIDTH_PERCENT_2);

        var scaleLabel = new Label(Messages.MODEL_PROPERTY_SCALE + ":");
        scaleLabel.prefWidthProperty()
                .bind(settingContainer.widthProperty().multiply(LABEL_PERCENT));

        scaleField = new FloatTextField();
        scaleField.setMinMax(0.0001F, Integer.MAX_VALUE);
        scaleField.setScrollPower(3F);
        scaleField.prefWidthProperty()
                .bind(settingContainer.widthProperty().multiply(FIELD_PERCENT));

        settingContainer.add(layerField, 0, 0, 2, 1);
        settingContainer.add(diffuseTextureControl, 0, 1, 2, 1);
        settingContainer.add(normalTextureControl, 0, 2, 2, 1);
        settingContainer.add(scaleLabel, 0, 3);
        settingContainer.add(scaleField, 1, 3);

        FxControlUtils.onValueChange(scaleField, this::updateScale);

        FxUtils.addClass(settingContainer, CssClasses.DEF_GRID_PANE)
                .addClass(layerField, CssClasses.ABSTRACT_PARAM_CONTROL_PARAM_NAME)
                .addClass(scaleLabel, CssClasses.ABSTRACT_PARAM_CONTROL_PARAM_NAME_SINGLE_ROW)
                .addClass(scaleField, CssClasses.PROPERTY_CONTROL_COMBO_BOX)
                .addClass(this, CssClasses.PROCESSING_COMPONENT_TERRAIN_EDITOR_TEXTURE_LAYER);
    }

    /**
     * Get the texture scale field.
     *
     * @return the texture scale field.
     */
    @FromAnyThread
    private @NotNull FloatTextField getScaleField() {
        return scaleField;
    }

    /**
     * Get the diffuse texture chooser.
     *
     * @return the diffuse texture chooser.
     */
    @FromAnyThread
    private @NotNull NamedChooseTextureControl getDiffuseTextureControl() {
        return diffuseTextureControl;
    }

    /**
     * Get the normal texture chooser.
     *
     * @return the normal texture chooser.
     */
    @FromAnyThread
    private @NotNull NamedChooseTextureControl getNormalTextureControl() {
        return normalTextureControl;
    }

    /**
     * Get the layer field.
     *
     * @return the layer field.
     */
    @FromAnyThread
    private @NotNull Label getLayerField() {
        return layerField;
    }

    /**
     * Get the settings container.
     *
     * @return the settings container.
     */
    @FromAnyThread
    private @NotNull GridPane getSettingContainer() {
        return settingContainer;
    }

    /**
     * Set true if need to ignore listeners.
     *
     * @param ignoreListeners true if need to ignore listeners.
     */
    @FxThread
    private void setIgnoreListeners(boolean ignoreListeners) {
        this.ignoreListeners = ignoreListeners;
    }

    /**
     * Return true if need to ignore listeners.
     *
     * @return true if need to ignore listeners.
     */
    @FxThread
    private boolean isIgnoreListeners() {
        return ignoreListeners;
    }

    /**
     * Update texture scale.
     */
    @FxThread
    private void updateScale(@NotNull Float newValue) {
        if (!isIgnoreListeners()) {
            getItem().setScale(newValue);
        }
    }

    /**
     * Update a normal texture.
     */
    @FxThread
    private void updateNormal() {

        if (isIgnoreListeners()) {
            return;
        }

        getItem().setNormalFile(getNormalTextureControl()
                .getTextureFile());
    }

    /**
     * Update a diffuse texture.
     */
    @FxThread
    private void updateDiffuse() {

        if (isIgnoreListeners()) {
            return;
        }

        getItem().setDiffuseFile(getDiffuseTextureControl()
                .getTextureFile());
    }

    @Override
    @FxThread
    protected void updateItem(@Nullable TextureLayer item, boolean empty) {
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
    @FxThread
    protected void refresh() {

        var item = getItem();

        if (item == null) {
            return;
        }

        setIgnoreListeners(true);
        try {

            getScaleField().setValue(item.getScale());
            getNormalTextureControl().setTextureFile(item.getNormalFile());
            getDiffuseTextureControl().setTextureFile(item.getDiffuseFile());
            getLayerField().setText(Messages.MODEL_PROPERTY_LAYER + " #" + (item.getLayer() + 1));

        } finally {
            setIgnoreListeners(false);
        }
    }
}
