package com.ss.editor.ui.control.model.property.builder.impl;

import com.ss.editor.control.scene.ControlEditableGenericObjectFactory;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.control.generic.BooleanGenericPropertyControl;
import com.ss.editor.ui.control.model.property.generic.EditableGenericObject;
import com.ss.editor.ui.control.model.property.generic.EditableGenericObjectFactory;
import com.ss.editor.ui.control.model.property.generic.EditableProperty;
import com.ss.editor.ui.control.model.property.generic.EditablePropertyType;
import com.ss.editor.ui.control.property.AbstractPropertyControl;
import com.ss.editor.ui.control.property.builder.PropertyBuilder;
import com.ss.editor.ui.control.property.builder.impl.AbstractPropertyBuilder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;
import rlib.util.ClassUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The implementation of the {@link PropertyBuilder} for building property controls for editable objects.
 *
 * @author JavaSaBr
 */
public class GenericPropertyBuilder extends AbstractPropertyBuilder<ModelChangeConsumer> {

    private static final Array<EditableGenericObjectFactory> FACTORIES = ArrayFactory.newArray(EditableGenericObjectFactory.class);

    static {
        FACTORIES.add(ControlEditableGenericObjectFactory.getInstance());
    }

    private static final PropertyBuilder INSTANCE = new GenericPropertyBuilder();

    public static PropertyBuilder getInstance() {
        return INSTANCE;
    }

    public GenericPropertyBuilder() {
        super(ModelChangeConsumer.class);
    }

    @Override
    protected void buildForImpl(@NotNull final Object object, @Nullable final Object parent, @NotNull final VBox container,
                                @NotNull final ModelChangeConsumer changeConsumer) {

        EditableGenericObject genericObject = object instanceof EditableGenericObject ?
                (EditableGenericObject) object : null;

        if (genericObject == null) {
            for (final EditableGenericObjectFactory factory : FACTORIES) {
                genericObject = factory.make(object);
                if (genericObject != null) break;
            }
        }

        if (genericObject == null) return;

        final Array<EditableProperty<?, ?>> editableProperties = genericObject.getEditableProperties();
        if (editableProperties.isEmpty()) return;

        for (final EditableProperty<?, ?> editableProperty : editableProperties) {

            final EditablePropertyType type = editableProperty.getType();

            switch (type) {
                case BOOLEAN: {

                    final EditableProperty<Boolean, ?> property = cast(editableProperty);
                    final Boolean value = Objects.requireNonNull(property.getValue(), "Boolean value can't be null.");

                    final BooleanGenericPropertyControl propertyControl =
                            new BooleanGenericPropertyControl(value, property.getName(), changeConsumer);

                    addControl(container, property, propertyControl);
                    break;
                }
                default:
                    break;
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

    private <T> EditableProperty<T, ?> cast(final EditableProperty<?, ?> property) {
        return ClassUtils.unsafeCast(property);
    }
}
