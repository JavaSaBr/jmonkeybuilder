package com.ss.editor.ui.control.property.builder.impl;

import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.property.EditableProperty;
import com.ss.editor.extension.property.EditablePropertyType;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.impl.LightElementModelPropertyControl;
import com.ss.editor.ui.control.property.impl.SpatialElementModelPropertyControl;
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
    @FxThread
    protected void buildFor(@NotNull final VBox container, @NotNull final ModelChangeConsumer changeConsumer,
                            @NotNull final EditableProperty<?, ?> description) {
        super.buildFor(container, changeConsumer, description);

        final EditablePropertyType type = description.getType();

        switch (type) {
            case DIRECTION_LIGHT_FROM_SCENE: {

                final EditableProperty<DirectionalLight, ?> property = cast(description);
                final DirectionalLight value = property.getValue();

                final LightElementModelPropertyControl<DirectionalLight, EditableProperty<DirectionalLight, ?>> propertyControl =
                        new LightElementModelPropertyControl<>(DirectionalLight.class, value, property.getName(), changeConsumer);

                addControl(container, property, propertyControl);
                break;
            }
            case POINT_LIGHT_FROM_SCENE: {

                final EditableProperty<PointLight, ?> property = cast(description);
                final PointLight value = property.getValue();

                final LightElementModelPropertyControl<PointLight, EditableProperty<PointLight, ?>> propertyControl =
                        new LightElementModelPropertyControl<>(PointLight.class, value, property.getName(), changeConsumer);

                addControl(container, property, propertyControl);
                break;
            }
            case LIGHT_FROM_SCENE: {

                final EditableProperty<Light, ?> property = cast(description);
                final Light value = property.getValue();

                final LightElementModelPropertyControl<Light, EditableProperty<Light, ?>> propertyControl =
                        new LightElementModelPropertyControl<>(Light.class, value, property.getName(), changeConsumer);

                addControl(container, property, propertyControl);
                break;
            }
            case SPATIAL_FROM_SCENE: {

                final EditableProperty<Spatial, ?> property = cast(description);
                final Spatial value = property.getValue();

                final SpatialElementModelPropertyControl<Spatial, EditableProperty<Spatial, ?>> propertyControl =
                        new SpatialElementModelPropertyControl<>(Spatial.class, value, property.getName(), changeConsumer);

                addControl(container, property, propertyControl);
                break;
            }
            case NODE_FROM_SCENE: {

                final EditableProperty<Node, ?> property = cast(description);
                final Node value = property.getValue();

                final SpatialElementModelPropertyControl<Node, EditableProperty<Node, ?>> propertyControl =
                        new SpatialElementModelPropertyControl<>(Node.class, value, property.getName(), changeConsumer);

                addControl(container, property, propertyControl);
                break;
            }
        }
    }
}
