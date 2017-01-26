package com.ss.editor.ui.control.model.property.builder.impl;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.control.model.property.control.BooleanModelPropertyControl;
import com.ss.editor.ui.control.model.property.control.ColorModelPropertyControl;
import com.ss.editor.ui.control.model.property.control.EnumControlPropertyControl;
import com.ss.editor.ui.control.model.property.control.FloatModelPropertyControl;
import com.ss.editor.ui.control.model.property.control.IntegerModelPropertyControl;
import com.ss.editor.ui.control.model.property.control.Vector2fModelPropertyControl;
import com.ss.editor.ui.control.model.property.control.Vector3fModelPropertyControl;
import com.ss.editor.ui.control.property.AbstractPropertyControl;
import com.ss.editor.ui.control.property.builder.impl.AbstractPropertyBuilder;
import com.ss.extension.property.EditableProperty;
import com.ss.extension.property.EditablePropertyType;
import com.ss.extension.scene.control.EditableControl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;
import rlib.util.ClassUtils;
import rlib.util.array.Array;

/**
 * The property builder to build property controls of editable controls.
 *
 * @author JavaSaBr
 */
public class EditableControlPropertyBuilder extends AbstractPropertyBuilder<SceneChangeConsumer> {

    private static final EditableControlPropertyBuilder INSTANCE = new EditableControlPropertyBuilder();

    public static EditableControlPropertyBuilder getInstance() {
        return INSTANCE;
    }

    protected EditableControlPropertyBuilder() {
        super(SceneChangeConsumer.class);
    }

    @Override
    protected void buildForImpl(@NotNull final Object object, @Nullable final Object parent, @NotNull final VBox container,
                                @NotNull final SceneChangeConsumer changeConsumer) {

        if (!(object instanceof EditableControl)) return;

        final EditableControl control = (EditableControl) object;

        final Array<EditableProperty<?, ?>> editableProperties = control.getEditableProperties();
        if (editableProperties.isEmpty()) return;

        for (final EditableProperty<?, ?> editableProperty : editableProperties) {

            final EditablePropertyType type = editableProperty.getType();

            switch (type) {
                case BOOLEAN: {

                    final EditableProperty<Boolean, ?> property = cast(editableProperty);
                    final Boolean value = Objects.requireNonNull(property.getValue(), "Boolean value can't be null.");

                    final BooleanModelPropertyControl<EditableProperty<Boolean, ?>> propertyControl =
                            new BooleanModelPropertyControl<>(value, property.getName(), changeConsumer);

                    addControl(container, property, propertyControl);
                    break;
                }
                case FLOAT: {

                    final EditableProperty<Float, ?> property = cast(editableProperty);
                    final Float value = Objects.requireNonNull(property.getValue(), "Float value can't be null.");

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
                    final ColorRGBA color = Objects.requireNonNull(property.getValue(), "Color value can't be null.");

                    final ColorModelPropertyControl<EditableProperty<ColorRGBA, ?>> propertyControl =
                            new ColorModelPropertyControl<>(color, property.getName(), changeConsumer);

                    addControl(container, property, propertyControl);
                    break;
                }
                case INTEGER: {

                    final EditableProperty<Integer, ?> property = cast(editableProperty);
                    final Integer value = Objects.requireNonNull(property.getValue(), "Integer value can't be null.");

                    final IntegerModelPropertyControl<EditableProperty<Integer, ?>> propertyControl =
                            new IntegerModelPropertyControl<>(value, property.getName(), changeConsumer);

                    addControl(container, property, propertyControl);
                    break;
                }
                case VECTOR_2F: {

                    final EditableProperty<Vector2f, ?> property = cast(editableProperty);
                    final Vector2f value = Objects.requireNonNull(property.getValue(), "Vector2f value can't be null.");

                    final Vector2fModelPropertyControl<EditableProperty<Vector2f, ?>> propertyControl =
                            new Vector2fModelPropertyControl<>(value, property.getName(), changeConsumer);

                    addControl(container, property, propertyControl);
                    break;
                }
                case VECTOR_3F: {

                    final EditableProperty<Vector3f, ?> property = cast(editableProperty);
                    final Vector3f value = Objects.requireNonNull(property.getValue(), "Vector3f value can't be null.");

                    final Vector3fModelPropertyControl<EditableProperty<Vector3f, ?>> propertyControl =
                            new Vector3fModelPropertyControl<>(value, property.getName(), changeConsumer);

                    addControl(container, property, propertyControl);
                    break;
                }
                case ENUM: {

                    final EditableProperty<Enum<?>, ?> property = cast(editableProperty);
                    final Enum<?> value = Objects.requireNonNull(property.getValue(), "Enum value can't be null.");

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

    protected <T> void addControl(final @NotNull VBox container, @NotNull final EditableProperty<T, ?> property,
                                  @NotNull final AbstractPropertyControl<ModelChangeConsumer, EditableProperty<T, ?>, T> propertyControl) {

        propertyControl.setApplyHandler(EditableProperty::setValue);
        propertyControl.setSyncHandler(EditableProperty::getValue);
        propertyControl.setEditObject(property);

        FXUtils.addToPane(propertyControl, container);
    }

    private <T> EditableProperty<T, ?> cast(@NotNull final EditableProperty<?, ?> property) {
        return ClassUtils.unsafeCast(property);
    }
}
