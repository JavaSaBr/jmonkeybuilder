package com.ss.editor.ui.control.material;

import static com.ss.editor.Messages.COLOR_MATERIAL_PARAM_CONTROL_REMOVE;
import static com.ss.rlib.util.ObjectUtils.notNull;
import static java.lang.Math.min;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.material.operation.ColorMaterialParamOperation;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.util.DynamicIconSupport;
import com.ss.editor.ui.util.UIUtils;
import com.ss.rlib.ui.util.FXUtils;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * The implementation of a control for editing colors properties.
 *
 * @author JavaSaBr
 */
public class ColorMaterialParamControl extends MaterialParamControl {

    @NotNull
    private static final Insets ELEMENT_OFFSET = new Insets(0, 0, 0, 3);

    /**
     * The color picker.
     */
    @Nullable
    private ColorPicker colorPicker;

    /**
     * Instantiates a new Color material param control.
     *
     * @param changeHandler the change handler
     * @param material      the material
     * @param parameterName the parameter name
     */
    public ColorMaterialParamControl(@NotNull final Consumer<EditorOperation> changeHandler,
                                     @NotNull final Material material,
                                     @NotNull final String parameterName) {
        super(changeHandler, material, parameterName);
    }

    @Override
    protected void createComponents() {
        super.createComponents();

        colorPicker = new ColorPicker();
        colorPicker.valueProperty().addListener((observable, oldValue, newValue) -> processChange(newValue));
        colorPicker.prefWidthProperty().bind(widthProperty().multiply(CONTROL_PERCENT_WIDTH));

        final Button removeButton = new Button();
        removeButton.setTooltip(new Tooltip(COLOR_MATERIAL_PARAM_CONTROL_REMOVE));
        removeButton.setGraphic(new ImageView(Icons.REMOVE_12));
        removeButton.setOnAction(event -> processRemove());
        removeButton.disableProperty().bind(colorPicker.valueProperty().isNull());

        FXUtils.addToPane(colorPicker, this);
        FXUtils.addToPane(removeButton, this);

        FXUtils.addClassesTo(colorPicker, CSSClasses.MATERIAL_FILE_EDITOR_PARAM_CONTROL_COLOR_PICKER);
        FXUtils.addClassesTo(removeButton, CSSClasses.FLAT_BUTTON,
                CSSClasses.MATERIAL_FILE_EDITOR_PARAM_CONTROL_BUTTON);

        DynamicIconSupport.addSupport(removeButton);

        HBox.setMargin(colorPicker, ELEMENT_OFFSET);
        HBox.setMargin(removeButton, ELEMENT_OFFSET);
    }

    /**
     * @return the color picker.
     */
    @NotNull
    private ColorPicker getColorPicker() {
        return notNull(colorPicker);
    }

    /**
     * Update a color.
     */
    private void processChange(@Nullable final Color newValue) {

        if (isIgnoreListeners()) {
            return;
        } else if (newValue == null) {
            processRemove();
            return;
        }

        final ColorRGBA colorRGBA = UIUtils.convertColor(newValue);

        final String parameterName = getParameterName();
        final Material material = getMaterial();
        final MatParam param = material.getParam(parameterName);
        final ColorRGBA oldValue = param == null ? null : (ColorRGBA) param.getValue();

        execute(new ColorMaterialParamOperation(parameterName, colorRGBA, oldValue));
    }

    /**
     * Remove a color.
     */
    private void processRemove() {

        final String parameterName = getParameterName();
        final Material material = getMaterial();
        final MatParam param = material.getParam(parameterName);
        final ColorRGBA oldValue = param == null ? null : (ColorRGBA) param.getValue();

        execute(new ColorMaterialParamOperation(parameterName, null, oldValue));
    }

    @Override
    public void reload() {
        super.reload();

        final Material material = getMaterial();
        final MatParam param = material.getParam(getParameterName());
        final ColorPicker colorPicker = getColorPicker();

        if (param == null) {
            colorPicker.setValue(null);
            return;
        }

        final ColorRGBA color = (ColorRGBA) param.getValue();

        final float red = min(color.getRed(), 1F);
        final float green = min(color.getGreen(), 1F);
        final float blue = min(color.getBlue(), 1F);
        final float alpha = min(color.getAlpha(), 1F);

        colorPicker.setValue(new Color(red, green, blue, alpha));
    }
}
