package com.ss.editor.ui.control.material;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.ui.control.material.operation.BooleanMaterialParamOperation;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.control.CheckBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * The implementation of a control for editing boolean properties.
 *
 * @author JavaSaBr
 */
public class BooleanMaterialParamControl extends MaterialParamControl {

    /**
     * The check box.
     */
    @Nullable
    private CheckBox checkBox;

    /**
     * Instantiates a new Boolean material param control.
     *
     * @param changeHandler the change handler
     * @param material      the material
     * @param parameterName the parameter name
     */
    public BooleanMaterialParamControl(@NotNull final Consumer<EditorOperation> changeHandler,
                                       @NotNull final Material material, @NotNull final String parameterName) {
        super(changeHandler, material, parameterName);
    }

    @Override
    protected void createComponents() {
        super.createComponents();

        checkBox = new CheckBox();
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> processChange(newValue));
        checkBox.prefWidthProperty().bind(widthProperty().multiply(CONTROL_PERCENT_WIDTH2));

        FXUtils.addToPane(checkBox, this);
        FXUtils.addClassTo(checkBox, CSSClasses.MATERIAL_FILE_EDITOR_PARAM_CONTROL_CHECKBOX);
    }

    @Override
    protected double getLabelPercentWidth() {
        return LABEL_PERCENT_WIDTH2;
    }

    /**
     * @return the check box.
     */
    @NotNull
    private CheckBox getCheckBox() {
        return notNull(checkBox);
    }

    /**
     * Update a value.
     */
    private void processChange(@NotNull final Boolean newValue) {
        if (isIgnoreListeners()) return;

        final String parameterName = getParameterName();
        final Material material = getMaterial();
        final MatParam param = material.getParam(parameterName);
        final Boolean oldValue = param == null ? null : (Boolean) param.getValue();

        execute(new BooleanMaterialParamOperation(parameterName, newValue, oldValue));
    }

    @Override
    public void reload() {
        super.reload();

        final Material material = getMaterial();
        final MatParam param = material.getParam(getParameterName());
        final CheckBox checkBox = getCheckBox();

        if (param == null) {
            checkBox.setSelected(false);
            return;
        }

        checkBox.setSelected((Boolean) param.getValue());
    }
}
