package com.ss.builder.fx.control.property.builder.impl;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.math.*;
import com.jme3.texture.Texture2D;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.builder.util.EditorUtils;
import com.ss.builder.annotation.FxThread;
import com.ss.editor.extension.property.EditableProperty;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.builder.fx.control.property.PropertyControl;
import com.ss.builder.fx.control.property.impl.*;
import com.ss.builder.util.EditorUtils;
import com.ss.rlib.common.util.ClassUtils;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The implementation of property builder for editable objects.
 *
 * @param <C> the type of {@link ChangeConsumer}.
 * @author JavaSaBr
 */
public class EditableObjectPropertyBuilder<C extends ChangeConsumer> extends AbstractPropertyBuilder<C> {

    protected EditableObjectPropertyBuilder(@NotNull Class<? extends C> type) {
        super(type);
    }

    @Override
    @FxThread
    protected void buildForImpl(
            @NotNull Object object,
            @Nullable Object parent,
            @NotNull VBox container,
            @NotNull C changeConsumer
    ) {

        var properties = getProperties(object, parent, changeConsumer);

        if (properties == null || properties.isEmpty()) {
            return;
        }

        for (var description : properties) {
            buildFor(container, changeConsumer, description);
        }
    }

    @FxThread
    protected void buildFor(
            @NotNull VBox container,
            @NotNull C changeConsumer,
            @NotNull EditableProperty<?, ?> description
    ) {

        var type = description.getType();

        switch (type) {
            case BOOLEAN: {

                EditableProperty<Boolean, ?> property = cast(description);
                var currentValue = property.getValue();

                var propertyControl = new BooleanPropertyControl<C, EditableProperty<Boolean, ?>>(currentValue,
                        property.getName(), changeConsumer);

                addControl(container, property, propertyControl);
                break;
            }
            case FLOAT: {

                EditableProperty<Float, ?> property = cast(description);
                var currentValue = property.getValue();

                var propertyControl = new FloatPropertyControl<C, EditableProperty<Float, ?>>(currentValue,
                        property.getName(), changeConsumer);

                var scrollPower = propertyControl.getScrollPower();
                var mod = property.getScrollPower();

                propertyControl.setScrollPower(scrollPower * mod);
                propertyControl.setMinMax(property.getMinValue(), property.getMaxValue());

                addControl(container, property, propertyControl);
                break;
            }
            case COLOR: {

                EditableProperty<ColorRGBA, ?> property = cast(description);
                var undefine = description.getValue();

                final ColorRGBA color;

                // for some cases with materials
                if (undefine instanceof Vector4f) {
                    final Vector4f vector4f = (Vector4f) undefine;
                    color = new ColorRGBA(vector4f.getX(), vector4f.getY(), vector4f.getZ(), vector4f.getW());
                } else {
                    color = (ColorRGBA) undefine;
                }

                var propertyControl = new ColorPropertyControl<C, EditableProperty<ColorRGBA, ?>>(color,
                        property.getName(), changeConsumer);

                addControl(container, property, propertyControl);
                break;
            }
            case INTEGER: {

                EditableProperty<Integer, ?> property = cast(description);
                var currentValue = property.getValue();

                var propertyControl = new IntegerPropertyControl<C, EditableProperty<Integer, ?>>(currentValue,
                        property.getName(), changeConsumer);

                addControl(container, property, propertyControl);
                break;
            }
            case STRING: {

                EditableProperty<String, ?> property = cast(description);
                var currentValue = property.getValue();

                var propertyControl = new StringPropertyControl<C, EditableProperty<String, ?>>(currentValue,
                        property.getName(), changeConsumer);

                addControl(container, property, propertyControl);
                break;
            }
            case READ_ONLY_STRING: {

                EditableProperty<Object, ?> property = cast(description);
                var currentValue = property.getValue();

                var propertyControl = new DefaultSinglePropertyControl<C, EditableProperty<Object, ?>, Object>(
                        currentValue, property.getName(), changeConsumer);

                addControl(container, property, propertyControl);
                break;
            }
            case VECTOR_2F: {

                EditableProperty<Vector2f, ?> property = cast(description);
                var currentValue = property.getValue();
                var propertyControl = new Vector2fPropertyControl<C, EditableProperty<Vector2f, ?>>(currentValue,
                        property.getName(), changeConsumer);

                var scrollPower = propertyControl.getScrollPower();
                var mod = property.getScrollPower();

                propertyControl.setMinMax(property.getMinValue(), property.getMaxValue());
                propertyControl.setScrollPower(scrollPower * mod);

                addControl(container, property, propertyControl);
                break;
            }
            case MIN_MAX_2F: {

                EditableProperty<Vector2f, ?> property = cast(description);

                var value = property.getValue();
                var propertyControl = new MinMaxPropertyControl<C, EditableProperty<Vector2f, ?>>(
                        value, property.getName(), changeConsumer);


                var scrollPower = propertyControl.getScrollPower();
                var mod = property.getScrollPower();

                propertyControl.setMinMax(property.getMinValue(), property.getMaxValue());
                propertyControl.setScrollPower(scrollPower * mod);

                addControl(container, property, propertyControl);
                break;
            }
            case VECTOR_3F: {

                EditableProperty<Vector3f, ?> property = cast(description);
                var currentValue = property.getValue();

                var propertyControl = new Vector3fPropertyControl<C, EditableProperty<Vector3f, ?>>(currentValue,
                        property.getName(), changeConsumer);

                addControl(container, property, propertyControl);
                break;
            }
            case QUATERNION: {

                EditableProperty<Quaternion, ?> property = cast(description);
                var currentValue = property.getValue();

                var propertyControl = new QuaternionPropertyControl<C, EditableProperty<Quaternion, ?>>(
                        currentValue, property.getName(), changeConsumer);

                addControl(container, property, propertyControl);
                break;
            }
            case ENUM: {

                EditableProperty<Enum<?>, ?> property = cast(description);
                var value = notNull(property.getValue(), "Enum value can't be null.");
                var availableValues = EditorUtils.getAvailableValues(value);

                var propertyControl = new EnumPropertyControl<C, EditableProperty<Enum<?>, ?>, Enum<?>>(value,
                        property.getName(), changeConsumer, availableValues);

                addControl(container, property, propertyControl);
                break;
            }
            case TEXTURE_2D: {

                EditableProperty<Texture2D, ?> property = cast(description);
                var value = property.getValue();

                var propertyControl = new Texture2dPropertyControl<C, EditableProperty<Texture2D, ?>>(value,
                        property.getName(), changeConsumer);

                addControl(container, property, propertyControl);
                break;
            }
            case SEPARATOR: {
                buildSplitLine(container);
                break;
            }
            default: {
                break;
            }
        }
    }

    /**
     * Add the control.
     *
     * @param <T>             the type parameter.
     * @param container       the container.
     * @param property        the property.
     * @param propertyControl the property control.
     */
    @FxThread
    protected <T> void addControl(
            @NotNull VBox container,
            @NotNull EditableProperty<T, ?> property,
            @NotNull PropertyControl<? extends C, EditableProperty<T, ?>, T> propertyControl
    ) {

        propertyControl.setApplyHandler(EditableProperty::setValue);
        propertyControl.setSyncHandler(EditableProperty::getValue);
        propertyControl.setEditObject(property);
        propertyControl.setDisable(property.isReadOnly());

        FxUtils.addChild(container, propertyControl);
    }

    /**
     * Get the list of editable properties of the object.
     *
     * @param object         the editable object.
     * @param parent         the parent.
     * @param changeConsumer the change consumer.
     * @return the list of properties or null.
     */
    @FxThread
    protected @Nullable List<EditableProperty<?, ?>> getProperties(
            @NotNull Object object,
            @Nullable Object parent,
            @NotNull C changeConsumer
    ) {
        return getProperties(object);
    }

    /**
     * Get the list of editable properties of the object.
     *
     * @param object the editable object.
     * @return the list of properties or null.
     */
    @FxThread
    protected @Nullable List<EditableProperty<?, ?>> getProperties(@NotNull Object object) {
        return null;
    }

    @FxThread
    protected @NotNull <T> EditableProperty<T, ?> cast(@NotNull EditableProperty<?, ?> property) {
        return ClassUtils.unsafeCast(property);
    }
}
