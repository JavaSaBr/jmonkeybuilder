package com.ss.editor.ui.control.model.property;

import static java.util.Objects.requireNonNull;

import com.jme3.light.Light;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.operation.LightPropertyOperation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import rlib.ui.control.input.FloatTextField;
import rlib.util.array.ArrayFactory;

/**
 * The implementation of the {@link ModelPropertyControl} for editing direction's vector of the {@link Light}.
 *
 * @author JavaSaBr
 */
public class DirectionLightPropertyControl<T extends Light> extends AbstractVector3fModelPropertyControl<T> {

    public DirectionLightPropertyControl(@NotNull final Vector3f element, @NotNull final String paramName, @NotNull final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
    }

    @Override
    protected void updateVector(@Nullable final KeyEvent event) {
        if (isIgnoreListener() || (event != null && event.getCode() != KeyCode.ENTER)) return;

        final FloatTextField xField = getXField();
        final FloatTextField yFiled = getYFiled();
        final FloatTextField zField = getZField();

        final float x = xField.getValue();
        final float y = yFiled.getValue();
        final float z = zField.getValue();

        final Quaternion rotation = new Quaternion();
        rotation.fromAngles(ArrayFactory.toFloatArray(x, y, z));

        final Vector3f oldValue = requireNonNull(getPropertyValue());
        final Vector3f newValue = new Vector3f(x, y, z);
        newValue.normalizeLocal();

        changed(newValue, oldValue.clone());
    }

    @Override
    protected void changed(@Nullable final Vector3f newValue, @Nullable final Vector3f oldValue) {

        final T editObject = getEditObject();

        final LightPropertyOperation<T, Vector3f> operation =
                new LightPropertyOperation<>(editObject, getPropertyName(), newValue, oldValue);

        operation.setApplyHandler(getApplyHandler());

        final ModelChangeConsumer modelChangeConsumer = getModelChangeConsumer();
        modelChangeConsumer.execute(operation);
    }
}
