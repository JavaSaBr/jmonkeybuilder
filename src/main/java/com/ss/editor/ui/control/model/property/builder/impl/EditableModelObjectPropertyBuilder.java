package com.ss.editor.ui.control.model.property.builder.impl;

import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.extension.property.EditableProperty;
import com.ss.editor.extension.property.EditablePropertyType;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.control.DirectionLightElementModelPropertyControl;
import com.ss.editor.ui.control.model.property.control.NodeElementModelPropertyControl;
import com.ss.editor.ui.control.model.property.control.PointLightElementModelPropertyControl;
import com.ss.editor.ui.control.model.property.control.SpatialElementModelPropertyControl;
import com.ss.editor.ui.control.property.builder.impl.EditableObjectPropertyBuilder;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of property builder for editable objects.
 *
 * @author JavaSaBr
 */
public class EditableModelObjectPropertyBuilder extends EditableObjectPropertyBuilder<ModelChangeConsumer> {

    protected EditableModelObjectPropertyBuilder(@NotNull final Class<? extends ModelChangeConsumer> type) {
        super(type);
    }

    @Override
    protected void buildFor(@NotNull final VBox container, @NotNull final ModelChangeConsumer changeConsumer,
                            @NotNull final EditableProperty<?, ?> description) {
        super.buildFor(container, changeConsumer, description);

        final EditablePropertyType type = description.getType();

        switch (type) {
            case DIRECTION_LIGHT_FROM_SCENE: {

                final EditableProperty<DirectionalLight, ?> property = cast(description);
                final DirectionalLight value = property.getValue();

                final DirectionLightElementModelPropertyControl<EditableProperty<DirectionalLight, ?>> propertyControl =
                        new DirectionLightElementModelPropertyControl<>(value, property.getName(), changeConsumer);

                addControl(container, property, propertyControl);
                break;
            }
            case POINT_LIGHT_FROM_SCENE: {

                final EditableProperty<PointLight, ?> property = cast(description);
                final PointLight value = property.getValue();

                final PointLightElementModelPropertyControl<EditableProperty<PointLight, ?>> propertyControl =
                        new PointLightElementModelPropertyControl<>(value, property.getName(), changeConsumer);

                addControl(container, property, propertyControl);
                break;
            }
            case SPATIAL_FROM_SCENE: {

                final EditableProperty<Spatial, ?> property = cast(description);
                final Spatial value = property.getValue();

                final SpatialElementModelPropertyControl<EditableProperty<Spatial, ?>> propertyControl =
                        new SpatialElementModelPropertyControl<>(value, property.getName(), changeConsumer);

                addControl(container, property, propertyControl);
                break;
            }
            case NODE_FROM_SCENE: {

                final EditableProperty<Node, ?> property = cast(description);
                final Node value = property.getValue();

                final NodeElementModelPropertyControl<EditableProperty<Node, ?>> propertyControl =
                        new NodeElementModelPropertyControl<>(value, property.getName(), changeConsumer);

                addControl(container, property, propertyControl);
                break;
            }
        }
    }
}
