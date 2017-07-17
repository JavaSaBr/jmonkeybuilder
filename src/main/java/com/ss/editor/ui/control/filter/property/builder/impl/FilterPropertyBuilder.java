package com.ss.editor.ui.control.filter.property.builder.impl;

import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.control.filter.property.control.*;
import com.ss.editor.ui.control.property.AbstractPropertyControl;
import com.ss.editor.ui.control.property.builder.impl.AbstractPropertyBuilder;
import com.ss.editor.extension.property.EditableProperty;
import com.ss.editor.extension.property.EditablePropertyType;
import com.ss.editor.extension.scene.filter.EditableSceneFilter;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.ClassUtils;
import com.ss.rlib.util.array.Array;

import java.util.Objects;

/**
 * The iproperty builder to build property controls of editable scene app states.
 *
 * @author JavaSaBr
 */
public class FilterPropertyBuilder extends AbstractPropertyBuilder<SceneChangeConsumer> {

    @NotNull
    private static final FilterPropertyBuilder INSTANCE = new FilterPropertyBuilder();

    /**
     * Gets instance.
     *
     * @return the instance
     */
    @NotNull
    public static FilterPropertyBuilder getInstance() {
        return INSTANCE;
    }

    /**
     * Instantiates a new Filter property builder.
     */
    protected FilterPropertyBuilder() {
        super(SceneChangeConsumer.class);
    }

    @Override
    protected void buildForImpl(@NotNull final Object object, @Nullable final Object parent, @NotNull final VBox container,
                                @NotNull final SceneChangeConsumer changeConsumer) {

        if (!(object instanceof EditableSceneFilter)) return;

        final EditableSceneFilter<?> sceneFilter = (EditableSceneFilter) object;

        final Array<EditableProperty<?, ?>> editableProperties = sceneFilter.getEditableProperties();
        if (editableProperties.isEmpty()) return;

        for (final EditableProperty<?, ?> editableProperty : editableProperties) {

            final EditablePropertyType type = editableProperty.getType();

            switch (type) {
                case BOOLEAN: {

                    final EditableProperty<Boolean, ?> property = cast(editableProperty);
                    final Boolean value = Objects.requireNonNull(property.getValue(), "Boolean value can't be null.");

                    final BooleanFilterPropertyControl<EditableProperty<Boolean, ?>> propertyControl =
                            new BooleanFilterPropertyControl<>(value, property.getName(), changeConsumer);

                    addControl(container, property, propertyControl);
                    break;
                }
                case FLOAT: {

                    final EditableProperty<Float, ?> property = cast(editableProperty);
                    final Float value = Objects.requireNonNull(property.getValue(), "Float value can't be null.");

                    final FloatFilterPropertyControl<EditableProperty<Float, ?>> propertyControl =
                            new FloatFilterPropertyControl<>(value, property.getName(), changeConsumer);

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

                    final ColorFilterPropertyControl<EditableProperty<ColorRGBA, ?>> propertyControl =
                            new ColorFilterPropertyControl<>(color, property.getName(), changeConsumer);

                    addControl(container, property, propertyControl);
                    break;
                }
                case INTEGER: {

                    final EditableProperty<Integer, ?> property = cast(editableProperty);
                    final Integer value = Objects.requireNonNull(property.getValue(), "Integer value can't be null.");

                    final IntegerFilterPropertyControl<EditableProperty<Integer, ?>> propertyControl =
                            new IntegerFilterPropertyControl<>(value, property.getName(), changeConsumer);

                    addControl(container, property, propertyControl);
                    break;
                }
                case STRING: {

                    final EditableProperty<String, ?> property = cast(editableProperty);
                    final String value = property.getValue();

                    final StringFilterPropertyControl<EditableProperty<String, ?>> propertyControl =
                            new StringFilterPropertyControl<>(value, property.getName(), changeConsumer);

                    addControl(container, property, propertyControl);
                    break;
                }
                case VECTOR_2F: {

                    final EditableProperty<Vector2f, ?> property = cast(editableProperty);
                    final Vector2f value = Objects.requireNonNull(property.getValue(), "Vector2f value can't be null.");

                    final Vector2fFilterPropertyControl<EditableProperty<Vector2f, ?>> propertyControl =
                            new Vector2fFilterPropertyControl<>(value, property.getName(), changeConsumer);

                    addControl(container, property, propertyControl);
                    break;
                }
                case VECTOR_3F: {

                    final EditableProperty<Vector3f, ?> property = cast(editableProperty);
                    final Vector3f value = Objects.requireNonNull(property.getValue(), "Vector3f value can't be null.");

                    final Vector3fFilterPropertyControl<EditableProperty<Vector3f, ?>> propertyControl =
                            new Vector3fFilterPropertyControl<>(value, property.getName(), changeConsumer);

                    addControl(container, property, propertyControl);
                    break;
                }
                case ENUM: {

                    final EditableProperty<Enum<?>, ?> property = cast(editableProperty);
                    final Enum<?> value = Objects.requireNonNull(property.getValue(), "Enum value can't be null.");

                    final EnumFilterPropertyControl<Enum<?>, EditableProperty<Enum<?>, ?>> propertyControl =
                            new EnumFilterPropertyControl<>(value, property.getName(), changeConsumer);

                    addControl(container, property, propertyControl);
                    break;
                }
                case DIRECTION_LIGHT_FROM_SCENE: {

                    final EditableProperty<DirectionalLight, ?> property = cast(editableProperty);
                    final DirectionalLight value = property.getValue();

                    final DirectionLightElementFilterPropertyControl<EditableProperty<DirectionalLight, ?>> propertyControl =
                            new DirectionLightElementFilterPropertyControl<>(value, property.getName(), changeConsumer);

                    addControl(container, property, propertyControl);
                    break;
                }
                case POINT_LIGHT_FROM_SCENE: {

                    final EditableProperty<PointLight, ?> property = cast(editableProperty);
                    final PointLight value = property.getValue();

                    final PointLightElementFilterPropertyControl<EditableProperty<PointLight, ?>> propertyControl =
                            new PointLightElementFilterPropertyControl<>(value, property.getName(), changeConsumer);

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
                                  @NotNull final AbstractPropertyControl<SceneChangeConsumer, EditableProperty<T, ?>, T> propertyControl) {

        propertyControl.setApplyHandler(EditableProperty::setValue);
        propertyControl.setSyncHandler(EditableProperty::getValue);
        propertyControl.setEditObject(property);

        FXUtils.addToPane(propertyControl, container);
    }

    private <T> EditableProperty<T, ?> cast(@NotNull final EditableProperty<?, ?> property) {
        return ClassUtils.unsafeCast(property);
    }
}
