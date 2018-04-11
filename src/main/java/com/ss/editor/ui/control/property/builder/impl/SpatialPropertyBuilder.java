package com.ss.editor.ui.control.property.builder.impl;

import static com.ss.editor.extension.property.EditablePropertyType.*;
import static com.ss.editor.part3d.editor.impl.scene.AbstractSceneEditor3DPart.KEY_LOADED_MODEL;

import com.jme3.bullet.objects.VehicleWheel;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.property.EditableProperty;
import com.ss.editor.extension.property.EditablePropertyType;
import com.ss.editor.extension.property.SeparatorProperty;
import com.ss.editor.extension.property.SimpleProperty;
import com.ss.editor.extension.scene.SceneLayer;
import com.ss.editor.extension.scene.SceneNode;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.control.property.builder.PropertyBuilder;
import com.ss.editor.ui.control.property.impl.*;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The implementation of the {@link PropertyBuilder} to build property controls for {@link Spatial} objects.
 *
 * @author JavaSaBr
 */
public class SpatialPropertyBuilder extends EditableModelObjectPropertyBuilder {

    public static final int PRIORITY = 1;

    @NotNull
    private static final PropertyBuilder INSTANCE = new SpatialPropertyBuilder();

    @FromAnyThread
    public static @NotNull PropertyBuilder getInstance() {
        return INSTANCE;
    }

    private SpatialPropertyBuilder() {
        super(ModelChangeConsumer.class);
    }

    @Override
    protected @Nullable List<EditableProperty<?, ?>> getProperties(
            @NotNull Object object,
            @Nullable Object parent,
            @NotNull ModelChangeConsumer changeConsumer
    ) {

        if(!(object instanceof Spatial)) {
            return null;
        }

        var properties = new ArrayList<EditableProperty<?, ?>>();
        var spatial = (Spatial) object;

        properties.add(new SimpleProperty<>(ENUM, Messages.MODEL_PROPERTY_CULL_HINT, spatial,
            Spatial::getCullHint, Spatial::setCullHint));
        properties.add(new SimpleProperty<>(ENUM, Messages.MODEL_PROPERTY_SHADOW_MODE, spatial,
            Spatial::getShadowMode, Spatial::setShadowMode));
        properties.add(new SimpleProperty<>(ENUM, Messages.MODEL_PROPERTY_QUEUE_BUCKET, spatial,
            Spatial::getLocalQueueBucket, Spatial::setQueueBucket));

        if (canEditTransformation(spatial)) {

            properties.add(SeparatorProperty.getInstance());

            properties.add(new SimpleProperty<>(VECTOR_3F, Messages.MODEL_PROPERTY_LOCATION, spatial,
                Spatial::getLocalTranslation, Spatial::setLocalTranslation));
            properties.add(new SimpleProperty<>(VECTOR_3F, Messages.MODEL_PROPERTY_SCALE, spatial,
                Spatial::getLocalScale, Spatial::setLocalScale));
            properties.add(new SimpleProperty<>(QUATERNION, Messages.MODEL_PROPERTY_ROTATION, spatial,
                Spatial::getLocalRotation, Spatial::setLocalRotation));

        }

        var userDataKeys = spatial.getUserDataKeys();
        if (userDataKeys.isEmpty()) {
            return properties;
        }

        var count = userDataKeys.stream()
            .filter(s -> !isNeedSkip(s))
            .count();

        if (count < 1) {
            return properties;
        }

        properties.add(SeparatorProperty.getInstance());

        final Array<String> sortedKeys = ArrayFactory.newSortedArray(String.class);
        sortedKeys.addAll(userDataKeys);

        for (var key : sortedKeys) {

            if (isNeedSkip(key)) {
                continue;
            }

            var data = spatial.getUserData(key);

            properties.add(new SimpleProperty<>(getUserDataType(data), key, spatial,
                sp -> sp.getUserData(key), (sp, val) -> sp.setUserData(key, val)));
        }

        return properties;
    }

    private @NotNull EditablePropertyType getUserDataType(@NotNull Object value) {

        if (value instanceof Float) {
            return EditablePropertyType.FLOAT;
        } else if (value instanceof Integer) {
            return EditablePropertyType.INTEGER;
        } else if (value instanceof Boolean) {
            return EditablePropertyType.BOOLEAN;
        } else if (value instanceof Vector3f) {
            return EditablePropertyType.VECTOR_3F;
        } else if (value instanceof Vector2f) {
            return EditablePropertyType.VECTOR_2F;
        } else if (value instanceof ColorRGBA) {
            return EditablePropertyType.COLOR;
        } else if (value instanceof String) {
            return EditablePropertyType.STRING;
        }

        return EditablePropertyType.READ_ONLY_STRING;
    }

    @Override
    @FxThread
    protected void buildForImpl(
            @NotNull Object object,
            @Nullable Object parent,
            @NotNull VBox container,
            @NotNull ModelChangeConsumer changeConsumer
    ) {
        super.buildForImpl(object, parent, container, changeConsumer);

        if (!(object instanceof Spatial)) {
            return;
        }

        var spatial = (Spatial) object;

        if (changeConsumer instanceof SceneChangeConsumer) {

            // TODO add an editable property type
            var layer = SceneLayer.getLayer(spatial);
            var propertyControl = new LayerModelPropertyControl(layer, (SceneChangeConsumer) changeConsumer);
            propertyControl.setEditObject(spatial);

            FXUtils.addToPane(propertyControl, container);
        }
    }

    @FxThread
    private boolean isNeedSkip(@NotNull String key) {
        return SceneLayer.KEY.equals(key) || KEY_LOADED_MODEL.equals(key);
    }

    @FxThread
    private boolean canEditTransformation(@NotNull Spatial spatial) {
        return !(spatial instanceof SceneNode || spatial instanceof SceneLayer);
    }

    @Override
    public int getPriority() {
        return PRIORITY;
    }
}
