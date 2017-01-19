package com.ss.editor.ui.control.app.state.property.builder;

import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.control.property.AbstractPropertyControl;
import com.ss.extension.scene.app.state.EditableSceneAppState;
import com.ss.extension.scene.app.state.property.EditableProperty;
import com.ss.extension.scene.app.state.property.EditablePropertyType;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;
import rlib.util.ClassUtils;
import rlib.util.array.Array;

/**
 * The iproperty builder to build property controls of editavle scene app states.
 *
 * @author JavaSaBr
 */
public class PropertyBuilder {

    private static final PropertyBuilder INSTANCE = new PropertyBuilder();

    public static PropertyBuilder getInstance() {
        return INSTANCE;
    }

    public void buildFor(@NotNull final EditableSceneAppState appState, @NotNull final VBox container,
                         @NotNull final SceneChangeConsumer changeConsumer) {

        final Array<EditableProperty<?, ?>> editableProperties = appState.getEditableProperties();
        if (editableProperties.isEmpty()) return;

        for (final EditableProperty<?, ?> editableProperty : editableProperties) {

            final EditablePropertyType type = editableProperty.getType();

            switch (type) {
                case BOOLEAN: {

                    final EditableProperty<Boolean, ?> property = cast(editableProperty);
                    final Boolean value = Objects.requireNonNull(property.getValue(), "Boolean value can't be null.");

                    //  final BooleanGenericPropertyControl propertyControl =
                    //         new BooleanGenericPropertyControl(value, property.getName(), changeConsumer);

                    // addControl(container, property, propertyControl);
                    break;
                }
                default:
                    break;
            }
        }
    }

    protected <T> void addControl(final @NotNull VBox container, @NotNull final EditableProperty<T, ?> property,
                                  @NotNull final AbstractPropertyControl<SceneChangeConsumer, EditableProperty<T, ?>, T> propertyControl) {

        propertyControl.setApplyHandler(EditableProperty::setValue);
        propertyControl.setSyncHandler(EditableProperty::getValue);
        propertyControl.setEditObject(property);

        FXUtils.addToPane(propertyControl, container);
    }

    private <T> EditableProperty<T, ?> cast(final EditableProperty<?, ?> property) {
        return ClassUtils.unsafeCast(property);
    }
}
