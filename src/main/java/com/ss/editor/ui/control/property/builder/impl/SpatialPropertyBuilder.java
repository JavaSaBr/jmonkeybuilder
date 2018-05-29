package com.ss.editor.ui.control.property.builder.impl;

import static com.ss.editor.extension.property.EditablePropertyType.*;
import static com.ss.editor.part3d.editor.impl.scene.AbstractSceneEditor3DPart.KEY_LOADED_MODEL;
import static com.ss.rlib.common.util.ObjectUtils.ifNull;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.property.*;
import com.ss.editor.extension.scene.SceneLayer;
import com.ss.editor.extension.scene.SceneNode;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.builder.PropertyBuilder;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * The implementation of the {@link PropertyBuilder} to build property controls for {@link Spatial} objects.
 *
 * @author JavaSaBr
 */
public class SpatialPropertyBuilder extends EditableModelObjectPropertyBuilder {

    public static final int PRIORITY = 1;

    private static final PropertyBuilder INSTANCE = new SpatialPropertyBuilder();

    private static final Getter<Spatial, SceneLayer> LAYER_GETTER =
            spatial -> ifNull(SceneLayer.getLayer(spatial), SceneLayer.NO_LAYER);

    private static final Setter<Spatial, SceneLayer> LAYER_SETTER =
            (spatial, layer) -> SceneLayer.setLayer(layer, spatial);

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

        if (!(object instanceof Spatial)) {
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
        properties.add(new SimpleProperty<>(SCENE_LAYER, Messages.MODEL_PROPERTY_LAYER, spatial, LAYER_GETTER, LAYER_SETTER));

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
