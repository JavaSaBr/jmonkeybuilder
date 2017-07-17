package com.ss.editor.ui.control.model.property.builder.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.ss.editor.extension.property.EditableProperty;
import com.ss.editor.extension.property.EditablePropertyType;
import com.ss.editor.extension.scene.control.EditableControl;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.control.*;
import com.ss.editor.ui.control.property.AbstractPropertyControl;
import com.ss.editor.ui.control.property.builder.impl.AbstractPropertyBuilder;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.ClassUtils;
import com.ss.rlib.util.array.Array;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The property builder to build property controls of editable controls.
 *
 * @author JavaSaBr
 */
public class EditableControlPropertyBuilder extends AbstractPropertyBuilder<ModelChangeConsumer> {

    @NotNull
    private static final EditableControlPropertyBuilder INSTANCE = new EditableControlPropertyBuilder();

    /**
     * Gets instance.
     *
     * @return the instance
     */
    @NotNull
    public static EditableControlPropertyBuilder getInstance() {
        return INSTANCE;
    }

    private EditableControlPropertyBuilder() {
        super(ModelChangeConsumer.class);
    }

    @Override
    protected void buildForImpl(@NotNull final Object object, @Nullable final Object parent, @NotNull final VBox container,
                                @NotNull final ModelChangeConsumer changeConsumer) {

        if (!(object instanceof EditableControl)) return;

        final EditableControl control = (EditableControl) object;

        final Array<EditableProperty<?, ?>> editableProperties = control.getEditableProperties();
        if (editableProperties.isEmpty()) return;

        for (final EditableProperty<?, ?> editableProperty : editableProperties) {

            final EditablePropertyType type = editableProperty.getType();

            switch (type) {
                case BOOLEAN: {

                    final EditableProperty<Boolean, ?> property = cast(editableProperty);
                    final Boolean value = notNull(property.getValue(), "Boolean value can't be null.");

                    final BooleanModelPropertyControl<EditableProperty<Boolean, ?>> propertyControl =
                            new BooleanModelPropertyControl<>(value, property.getName(), changeConsumer);

                    addControl(container, property, propertyControl);
                    break;
                }
                case FLOAT: {

                    final EditableProperty<Float, ?> property = cast(editableProperty);
                    final Float value = notNull(property.getValue(), "Float value can't be null.");

                    final FloatModelPropertyControl<EditableProperty<Float, ?>> propertyControl =
                            new FloatModelPropertyControl<>(value, property.getName(), changeConsumer);

                    final float scrollPower = propertyControl.getScrollPower();
                    final float mod = property.getScrollPower();

                    propertyControl.setScrollPower(scrollPower * mod);
                    propertyControl.setMinMax(property.getMinValue(), property.getMaxValue());

                    addControl(container, property, propertyControl);
                    break;
                }
                case COLOR: {

                    final EditableProperty<ColorRGBA, ?> property = cast(editableProperty);
                    final ColorRGBA color = property.getValue();

                    final ColorModelPropertyControl<EditableProperty<ColorRGBA, ?>> propertyControl =
                            new ColorModelPropertyControl<>(color, property.getName(), changeConsumer);

                    addControl(container, property, propertyControl);
                    break;
                }
                case INTEGER: {

                    final EditableProperty<Integer, ?> property = cast(editableProperty);
                    final Integer value = notNull(property.getValue(), "Integer value can't be null.");

                    final IntegerModelPropertyControl<EditableProperty<Integer, ?>> propertyControl =
                            new IntegerModelPropertyControl<>(value, property.getName(), changeConsumer);

                    addControl(container, property, propertyControl);
                    break;
                }
                case STRING: {

                    final EditableProperty<String, ?> property = cast(editableProperty);
                    final String value = property.getValue();

                    final StringModelPropertyControl<EditableProperty<String, ?>> propertyControl =
                            new StringModelPropertyControl<>(value, property.getName(), changeConsumer);

                    addControl(container, property, propertyControl);
                    break;
                }
                case VECTOR_2F: {

                    final EditableProperty<Vector2f, ?> property = cast(editableProperty);
                    final Vector2f value = notNull(property.getValue(), "Vector2f value can't be null.");

                    final Vector2fModelPropertyControl<EditableProperty<Vector2f, ?>> propertyControl =
                            new Vector2fModelPropertyControl<>(value, property.getName(), changeConsumer);

                    addControl(container, property, propertyControl);
                    break;
                }
                case VECTOR_3F: {

                    final EditableProperty<Vector3f, ?> property = cast(editableProperty);
                    final Vector3f value = notNull(property.getValue(), "Vector3f value can't be null.");

                    final Vector3fModelPropertyControl<EditableProperty<Vector3f, ?>> propertyControl =
                            new Vector3fModelPropertyControl<>(value, property.getName(), changeConsumer);

                    addControl(container, property, propertyControl);
                    break;
                }
                case ENUM: {

                    final EditableProperty<Enum<?>, ?> property = cast(editableProperty);
                    final Enum<?> value = notNull(property.getValue(), "Enum value can't be null.");

                    final EnumControlPropertyControl<Enum<?>, EditableProperty<Enum<?>, ?>> propertyControl =
                            new EnumControlPropertyControl<>(value, property.getName(), changeConsumer);

                    addControl(container, property, propertyControl);
                    break;
                }
                default: {
                    break;
                }
            }
        }
    }

    /**
     * Add control.
     *
     * @param <T>             the type parameter
     * @param container       the container
     * @param property        the property
     * @param propertyControl the property control
     */
    protected <T> void addControl(final @NotNull VBox container, @NotNull final EditableProperty<T, ?> property,
                                  @NotNull final AbstractPropertyControl<ModelChangeConsumer, EditableProperty<T, ?>, T> propertyControl) {

        propertyControl.setApplyHandler(EditableProperty::setValue);
        propertyControl.setSyncHandler(EditableProperty::getValue);
        propertyControl.setEditObject(property);

        FXUtils.addToPane(propertyControl, container);
    }

    @NotNull
    private <T> EditableProperty<T, ?> cast(@NotNull final EditableProperty<?, ?> property) {
        return ClassUtils.unsafeCast(property);
    }
}
