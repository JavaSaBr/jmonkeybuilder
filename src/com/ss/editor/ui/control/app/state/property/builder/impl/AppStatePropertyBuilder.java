package com.ss.editor.ui.control.app.state.property.builder.impl;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.control.app.state.property.control.*;
import com.ss.editor.ui.control.property.AbstractPropertyControl;
import com.ss.editor.ui.control.property.builder.impl.AbstractPropertyBuilder;
import com.ss.editor.extension.property.EditableProperty;
import com.ss.editor.extension.property.EditablePropertyType;
import com.ss.editor.extension.scene.app.state.EditableSceneAppState;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.ClassUtils;
import com.ss.rlib.util.array.Array;

import java.util.Objects;

/**
 * The property builder to build property controls of editable scene app states.
 *
 * @author JavaSaBr
 */
public class AppStatePropertyBuilder extends AbstractPropertyBuilder<SceneChangeConsumer> {

    @NotNull
    private static final AppStatePropertyBuilder INSTANCE = new AppStatePropertyBuilder();

    /**
     * Gets instance.
     *
     * @return the instance
     */
    @NotNull
    public static AppStatePropertyBuilder getInstance() {
        return INSTANCE;
    }

    /**
     * Instantiates a new App state property builder.
     */
    protected AppStatePropertyBuilder() {
        super(SceneChangeConsumer.class);
    }

    @Override
    protected void buildForImpl(@NotNull final Object object, @Nullable final Object parent, @NotNull final VBox container,
                                @NotNull final SceneChangeConsumer changeConsumer) {

        if (!(object instanceof EditableSceneAppState)) return;

        final EditableSceneAppState appState = (EditableSceneAppState) object;

        final Array<EditableProperty<?, ?>> editableProperties = appState.getEditableProperties();
        if (editableProperties.isEmpty()) return;

        for (final EditableProperty<?, ?> editableProperty : editableProperties) {

            final EditablePropertyType type = editableProperty.getType();

            switch (type) {
                case BOOLEAN: {

                    final EditableProperty<Boolean, ?> property = cast(editableProperty);
                    final Boolean value = Objects.requireNonNull(property.getValue(), "Boolean value can't be null.");

                    final BooleanAppStatePropertyControl<EditableProperty<Boolean, ?>> propertyControl =
                            new BooleanAppStatePropertyControl<>(value, property.getName(), changeConsumer);

                    addControl(container, property, propertyControl);
                    break;
                }
                case FLOAT: {

                    final EditableProperty<Float, ?> property = cast(editableProperty);
                    final Float value = Objects.requireNonNull(property.getValue(), "Float value can't be null.");

                    final FloatAppStatePropertyControl<EditableProperty<Float, ?>> propertyControl =
                            new FloatAppStatePropertyControl<>(value, property.getName(), changeConsumer);

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

                    final ColorAppStatePropertyControl<EditableProperty<ColorRGBA, ?>> propertyControl =
                            new ColorAppStatePropertyControl<>(color, property.getName(), changeConsumer);

                    addControl(container, property, propertyControl);
                    break;
                }
                case INTEGER: {

                    final EditableProperty<Integer, ?> property = cast(editableProperty);
                    final Integer value = Objects.requireNonNull(property.getValue(), "Integer value can't be null.");

                    final IntegerAppStatePropertyControl<EditableProperty<Integer, ?>> propertyControl =
                            new IntegerAppStatePropertyControl<>(value, property.getName(), changeConsumer);

                    addControl(container, property, propertyControl);
                    break;
                }
                case STRING: {

                    final EditableProperty<String, ?> property = cast(editableProperty);
                    final String value = property.getValue();

                    final StringAppStatePropertyControl<EditableProperty<String, ?>> propertyControl =
                            new StringAppStatePropertyControl<>(value, property.getName(), changeConsumer);

                    addControl(container, property, propertyControl);
                    break;
                }
                case VECTOR_2F: {

                    final EditableProperty<Vector2f, ?> property = cast(editableProperty);
                    final Vector2f value = Objects.requireNonNull(property.getValue(), "Vector2f value can't be null.");

                    final Vector2fAppStatePropertyControl<EditableProperty<Vector2f, ?>> propertyControl =
                            new Vector2fAppStatePropertyControl<>(value, property.getName(), changeConsumer);

                    addControl(container, property, propertyControl);
                    break;
                }
                case VECTOR_3F: {

                    final EditableProperty<Vector3f, ?> property = cast(editableProperty);
                    final Vector3f value = Objects.requireNonNull(property.getValue(), "Vector3f value can't be null.");

                    final Vector3fAppStatePropertyControl<EditableProperty<Vector3f, ?>> propertyControl =
                            new Vector3fAppStatePropertyControl<>(value, property.getName(), changeConsumer);

                    addControl(container, property, propertyControl);
                    break;
                }
                case ENUM: {

                    final EditableProperty<Enum<?>, ?> property = cast(editableProperty);
                    final Enum<?> value = Objects.requireNonNull(property.getValue(), "Enum value can't be null.");

                    final EnumAppStatePropertyControl<Enum<?>, EditableProperty<Enum<?>, ?>> propertyControl =
                            new EnumAppStatePropertyControl<>(value, property.getName(), changeConsumer);

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
