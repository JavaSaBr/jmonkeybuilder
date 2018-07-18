package com.ss.editor.ui.control.property.builder.impl;

import com.jme3.audio.AudioKey;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.math.Vector2f;
import com.jme3.post.Filter;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.property.EditableProperty;
import com.ss.editor.extension.scene.SceneLayer;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.control.property.impl.*;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of property builder for editable objects.
 *
 * @author JavaSaBr
 */
public class EditableModelObjectPropertyBuilder extends EditableObjectPropertyBuilder<ModelChangeConsumer> {

    protected EditableModelObjectPropertyBuilder(@NotNull Class<? extends ModelChangeConsumer> type) {
        super(type);
    }

    @Override
    @FxThread
    protected void buildFor(
            @NotNull VBox container,
            @NotNull ModelChangeConsumer changeConsumer,
            @NotNull EditableProperty<?, ?> description
    ) {

        super.buildFor(container, changeConsumer, description);

        var type = description.getType();

        switch (type) {
            case DIRECTION_LIGHT_FROM_SCENE: {

                EditableProperty<DirectionalLight, ?> property = cast(description);

                var value = property.getValue();
                var propertyControl = new LightElementModelPropertyControl<DirectionalLight, EditableProperty<DirectionalLight, ?>>(
                        DirectionalLight.class, value, property.getName(), changeConsumer);

                addControl(container, property, propertyControl);
                break;
            }
            case AMBIENT_LIGHT_FROM_SCENE: {

                EditableProperty<AmbientLight, ?> property = cast(description);

                var value = property.getValue();
                var propertyControl = new LightElementModelPropertyControl<AmbientLight, EditableProperty<AmbientLight, ?>>(
                        AmbientLight.class, value, property.getName(), changeConsumer);

                addControl(container, property, propertyControl);
                break;
            }
            case POINT_LIGHT_FROM_SCENE: {

                EditableProperty<PointLight, ?> property = cast(description);

                var value = property.getValue();
                var propertyControl = new LightElementModelPropertyControl<PointLight, EditableProperty<PointLight, ?>>(
                        PointLight.class, value, property.getName(), changeConsumer);

                addControl(container, property, propertyControl);
                break;
            }
            case LIGHT_FROM_SCENE: {

                EditableProperty<Light, ?> property = cast(description);

                var value = property.getValue();
                var propertyControl = new LightElementModelPropertyControl<Light, EditableProperty<Light, ?>>(
                        Light.class, value, property.getName(), changeConsumer);

                addControl(container, property, propertyControl);
                break;
            }
            case SPATIAL_FROM_SCENE: {

                EditableProperty<Spatial, ?> property = cast(description);

                var value = property.getValue();
                var propertyControl = new SpatialElementModelPropertyControl<Spatial, EditableProperty<Spatial, ?>>(
                        Spatial.class, value, property.getName(), changeConsumer);

                addControl(container, property, propertyControl);
                break;
            }
            case NODE_FROM_SCENE: {

                EditableProperty<Node, ?> property = cast(description);

                var value = property.getValue();
                var propertyControl = new SpatialElementModelPropertyControl<Node, EditableProperty<Node, ?>>(
                        Node.class, value, property.getName(), changeConsumer);

                addControl(container, property, propertyControl);
                break;
            }
            case AUDIO_KEY: {

                EditableProperty<AudioKey, ?> property = cast(description);

                var value = property.getValue();
                var propertyControl = new AudioKeyPropertyControl<ModelChangeConsumer, EditableProperty<AudioKey, ?>>(
                        value, property.getName(), changeConsumer);

                addControl(container, property, propertyControl);
                break;
            }
        }

        if (!(changeConsumer instanceof SceneChangeConsumer)) {
            return;
        }

        var consumer = (SceneChangeConsumer) changeConsumer;

        switch (type) {
            case FILTER_FROM_SCENE: {

                EditableProperty<Filter, ?> property = cast(description);

                var value = property.getValue();
                var propertyControl = new FilterElementModelPropertyControl<EditableProperty<Filter, ?>>(
                        value, property.getName(), consumer);

                addControl(container, property, propertyControl);
                break;
            }
            case SCENE_LAYER: {

                EditableProperty<SceneLayer, ?> property = cast(description);

                var value = property.getValue();
                var propertyControl = new LayerModelPropertyControl<EditableProperty<SceneLayer, ?>>(value,
                        property.getName(), consumer);

                addControl(container, property, propertyControl);
                break;
            }
        }
    }
}
