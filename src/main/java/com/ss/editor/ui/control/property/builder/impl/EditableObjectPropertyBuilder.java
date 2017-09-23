package com.ss.editor.ui.control.property.builder.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.texture.Texture2D;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.extension.property.EditableProperty;
import com.ss.editor.extension.property.EditablePropertyType;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.control.property.impl.*;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.ClassUtils;
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

    protected EditableObjectPropertyBuilder(@NotNull final Class<? extends C> type) {
        super(type);
    }

    @Override
    @FXThread
    protected void buildForImpl(@NotNull final Object object, @Nullable final Object parent,
                                @NotNull final VBox container, @NotNull final C changeConsumer) {

        final List<EditableProperty<?, ?>> properties = getProperties(object);
        if (properties == null || properties.isEmpty()) return;

        for (final EditableProperty<?, ?> description : properties) {
            buildFor(container, changeConsumer, description);
        }
    }

    @FXThread
    protected void buildFor(@NotNull final VBox container, @NotNull final C changeConsumer,
                            @NotNull final EditableProperty<?, ?> description) {

        final EditablePropertyType type = description.getType();

        switch (type) {
            case BOOLEAN: {

                final EditableProperty<Boolean, ?> property = cast(description);
                final Boolean currentValue = property.getValue();

                final BooleanPropertyControl<C, EditableProperty<Boolean, ?>> propertyControl =
                        new BooleanPropertyControl<>(currentValue, property.getName(), changeConsumer);

                addControl(container, property, propertyControl);
                break;
            }
            case FLOAT: {

                final EditableProperty<Float, ?> property = cast(description);
                final Float currentValue = property.getValue();

                final FloatPropertyControl<C, EditableProperty<Float, ?>> propertyControl =
                        new FloatPropertyControl<>(currentValue, property.getName(), changeConsumer);

                final float scrollPower = propertyControl.getScrollPower();
                final float mod = property.getScrollPower();

                propertyControl.setScrollPower(scrollPower * mod);
                propertyControl.setMinMax(property.getMinValue(), property.getMaxValue());

                addControl(container, property, propertyControl);
                break;
            }
            case COLOR: {

                final EditableProperty<ColorRGBA, ?> property = cast(description);
                final Object undefine = description.getValue();
                final ColorRGBA color;

                // for some cases with materials
                if (undefine instanceof Vector4f) {
                    final Vector4f vector4f = (Vector4f) undefine;
                    color = new ColorRGBA(vector4f.getX(), vector4f.getY(), vector4f.getZ(), vector4f.getW());
                } else {
                    color = (ColorRGBA) undefine;
                }

                final ColorPropertyControl<C, EditableProperty<ColorRGBA, ?>> propertyControl =
                        new ColorPropertyControl<>(color, property.getName(), changeConsumer);

                addControl(container, property, propertyControl);
                break;
            }
            case INTEGER: {

                final EditableProperty<Integer, ?> property = cast(description);
                final Integer currentValue = property.getValue();

                final IntegerPropertyControl<C, EditableProperty<Integer, ?>> propertyControl =
                        new IntegerPropertyControl<>(currentValue, property.getName(), changeConsumer);

                addControl(container, property, propertyControl);
                break;
            }
            case STRING: {

                final EditableProperty<String, ?> property = cast(description);
                final String currentValue = property.getValue();

                final StringPropertyControl<C, EditableProperty<String, ?>> propertyControl =
                        new StringPropertyControl<>(currentValue, property.getName(), changeConsumer);

                addControl(container, property, propertyControl);
                break;
            }
            case VECTOR_2F: {

                final EditableProperty<Vector2f, ?> property = cast(description);
                final Vector2f currentValue = property.getValue();

                final Vector2FPropertyControl<C, EditableProperty<Vector2f, ?>> propertyControl =
                        new Vector2FPropertyControl<>(currentValue, property.getName(), changeConsumer);

                addControl(container, property, propertyControl);
                break;
            }
            case VECTOR_3F: {

                final EditableProperty<Vector3f, ?> property = cast(description);
                final Vector3f currentValue = property.getValue();

                final Vector3FPropertyControl<C, EditableProperty<Vector3f, ?>> propertyControl =
                        new Vector3FPropertyControl<>(currentValue, property.getName(), changeConsumer);

                addControl(container, property, propertyControl);
                break;
            }
            case ENUM: {

                final EditableProperty<Enum<?>, ?> property = cast(description);
                final Enum<?> value = notNull(property.getValue(), "Enum value can't be null.");
                final Enum<?>[] availableValues = EditorUtil.getAvailableValues(value);

                final EnumPropertyControl<C, EditableProperty<Enum<?>, ?>, Enum<?>> propertyControl =
                        new EnumPropertyControl<>(value, property.getName(), changeConsumer, availableValues);

                addControl(container, property, propertyControl);
                break;
            }
            case TEXTURE_2D: {

                final EditableProperty<Texture2D, ?> property = cast(description);
                final Texture2D value = property.getValue();

                final Texture2DPropertyControl<C, EditableProperty<Texture2D, ?>> propertyControl =
                        new Texture2DPropertyControl<>(value, property.getName(), changeConsumer);

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
     * @param <T>             the type parameter
     * @param container       the container
     * @param property        the property
     * @param propertyControl the property control
     */
    @FXThread
    protected <T> void addControl(@NotNull final VBox container, @NotNull final EditableProperty<T, ?> property,
                                  @NotNull final PropertyControl<C, @NotNull EditableProperty<T, ?>, T> propertyControl) {

        propertyControl.setApplyHandler(EditableProperty::setValue);
        propertyControl.setSyncHandler(EditableProperty::getValue);
        propertyControl.setEditObject(property);

        FXUtils.addToPane(propertyControl, container);
    }

    /**
     * Get the list of editable properties of the object.
     *
     * @param object the editable object.
     * @return the list of properties or null.
     */
    @FXThread
    protected @Nullable List<EditableProperty<?, ?>> getProperties(final @NotNull Object object) {
        return null;
    }

    @FXThread
    protected @NotNull <T> EditableProperty<T, ?> cast(@NotNull final EditableProperty<?, ?> property) {
        return ClassUtils.unsafeCast(property);
    }
}
