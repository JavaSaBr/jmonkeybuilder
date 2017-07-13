package com.ss.editor.ui.control.material;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.ui.control.material.operation.IntegerMaterialParamOperation;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.rlib.ui.control.input.IntegerTextField;
import com.ss.rlib.ui.util.FXUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * The base implementation of control for editing integer material parameter.
 *
 * @author JavaSaBr
 */
public class IntegerMaterialParamControl extends MaterialParamControl {

    /**
     * The integer field.
     */
    @Nullable
    private IntegerTextField integerField;

    /**
     * Instantiates a new Integer material param control.
     *
     * @param changeHandler the change handler
     * @param material      the material
     * @param parameterName the parameter name
     */
    public IntegerMaterialParamControl(final Consumer<EditorOperation> changeHandler, final Material material, final String parameterName) {
        super(changeHandler, material, parameterName);
    }

    @Override
    protected void createComponents() {
        super.createComponents();

        integerField = new IntegerTextField();
        integerField.addChangeListener((observable, oldValue, newValue) -> processChange(newValue));
        integerField.prefWidthProperty().bind(widthProperty().multiply(CONTROL_PERCENT_WIDTH2));

        FXUtils.addToPane(integerField, this);
        FXUtils.addClassTo(integerField, CSSClasses.MATERIAL_FILE_EDITOR_PARAM_CONTROL_SPINNER);
    }

    @Override
    protected double getLabelPercentWidth() {
        return LABEL_PERCENT_WIDTH2;
    }

    /**
     * Update a value.
     */
    private void processChange(@NotNull final Integer newValue) {
        if (isIgnoreListeners()) return;

        final String parameterName = getParameterName();
        final Material material = getMaterial();
        final MatParam param = material.getParam(parameterName);
        final Integer oldValue = param == null ? null : (Integer) param.getValue();

        execute(new IntegerMaterialParamOperation(parameterName, newValue, oldValue));
    }

    /**
     * @return The integer field.
     */
    @NotNull
    private IntegerTextField getIntegerField() {
        return notNull(integerField);
    }

    @Override
    public void reload() {
        super.reload();

        final Material material = getMaterial();
        final MatParam param = material.getParam(getParameterName());
        final IntegerTextField integerField = getIntegerField();

        if (param == null) {
            integerField.setValue(0);
            return;
        }

        integerField.setValue((Integer) param.getValue());
        integerField.positionCaret(integerField.getText().length());
    }
}
