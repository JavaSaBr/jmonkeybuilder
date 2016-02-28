package com.ss.editor.ui.control.material;

import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.util.UIUtils;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import rlib.ui.util.FXUtils;

import static com.ss.editor.Messages.COLOR_MATERIAL_PARAM_CONTROL_REMOVE;
import static java.lang.Math.min;

/**
 * Реализация контрола для выбора цвета.
 *
 * @author Ronn
 */
public class ColorMaterialParamControl extends MaterialParamControl {

    public static final Insets ELEMENT_OFFSET = new Insets(0, 0, 0, 3);

    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * Контрол для выбора цвета.
     */
    private ColorPicker colorPicker;

    public ColorMaterialParamControl(final Runnable changeHandler, final Material material, final String parameterName) {
        super(changeHandler, material, parameterName);
    }

    @Override
    protected void createComponents() {
        super.createComponents();

        colorPicker = new ColorPicker();
        colorPicker.valueProperty().addListener((observable, oldValue, newValue) -> processChange(newValue));

        final Button removeButton = new Button();
        removeButton.setId(CSSIds.MATERIAL_PARAM_CONTROL_BUTTON);
        removeButton.setTooltip(new Tooltip(COLOR_MATERIAL_PARAM_CONTROL_REMOVE));
        removeButton.setGraphic(new ImageView(Icons.REMOVE_18));
        removeButton.setOnAction(event -> processRemove());
        removeButton.disableProperty().bind(colorPicker.valueProperty().isNull());

        FXUtils.addToPane(colorPicker, this);
        FXUtils.addToPane(removeButton, this);

        HBox.setMargin(colorPicker, ELEMENT_OFFSET);
        HBox.setMargin(removeButton, ELEMENT_OFFSET);

        FXUtils.addClassTo(colorPicker, CSSClasses.MAIN_FONT_13);
        FXUtils.addClassTo(removeButton, CSSClasses.TOOLBAR_BUTTON);
    }

    /**
     * Процесс обновления цвета.
     */
    private void processChange(final Color newValue) {

        if(isIgnoreListeners()) {
            return;
        } else if(newValue == null) {
            processRemove();
            return;
        }

        EXECUTOR_MANAGER.addEditorThreadTask(() -> processChangeImpl(newValue));
    }

    /**
     * Процесс изменения цвета.
     */
    private void processChangeImpl(final Color newValue) {

        final ColorRGBA colorRGBA = UIUtils.convertColor(newValue);

        final Material material = getMaterial();
        material.setColor(getParameterName(), colorRGBA);

        EXECUTOR_MANAGER.addFXTask(() -> {
            changed();
            setIgnoreListeners(true);
            reload();
            setIgnoreListeners(false);
        });
    }

    /**
     * Удаление цвета.
     */
    private void processRemove() {
        EXECUTOR_MANAGER.addEditorThreadTask(this::removeColorImpl);
    }

    /**
     * Процесс удаления увета.
     */
    private void removeColorImpl() {

        final Material material = getMaterial();
        material.clearParam(getParameterName());

        EXECUTOR_MANAGER.addFXTask(() -> {
            changed();
            setIgnoreListeners(true);
            reload();
            setIgnoreListeners(false);
        });
    }

    @Override
    protected void reload() {
        super.reload();

        final Material material = getMaterial();
        final MatParam param = material.getParam(getParameterName());

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
