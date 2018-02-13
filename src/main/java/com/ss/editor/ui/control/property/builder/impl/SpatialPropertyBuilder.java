package com.ss.editor.ui.control.property.builder.impl;

import static com.ss.editor.part3d.editor.impl.scene.AbstractSceneEditor3DPart.KEY_LOADED_MODEL;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.extension.scene.SceneLayer;
import com.ss.editor.extension.scene.SceneNode;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.control.property.impl.LayerModelPropertyControl;
import com.ss.editor.ui.control.property.builder.PropertyBuilder;
import com.ss.editor.ui.control.property.impl.*;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * The implementation of the {@link PropertyBuilder} to build property controls for {@link Spatial} objects.
 *
 * @author JavaSaBr
 */
public class SpatialPropertyBuilder extends AbstractPropertyBuilder<ModelChangeConsumer> {

    @NotNull
    private static final CullHint[] CULL_HINTS = CullHint.values();

    @NotNull
    private static final ShadowMode[] SHADOW_MODES = ShadowMode.values();

    @NotNull
    private static final Bucket[] BUCKETS = Bucket.values();

    @NotNull
    private static final PropertyBuilder INSTANCE = new SpatialPropertyBuilder();

    /**
     * Get the single instance.
     *
     * @return the single instance.
     */
    @FromAnyThread
    public static @NotNull PropertyBuilder getInstance() {
        return INSTANCE;
    }

    private SpatialPropertyBuilder() {
        super(ModelChangeConsumer.class);
    }

    @Override
    @FxThread
    protected void buildForImpl(@NotNull final Object object, @Nullable final Object parent, @NotNull final VBox container,
                                @NotNull final ModelChangeConsumer changeConsumer) {

        if (!(object instanceof Spatial)) {
            return;
        }

        final Spatial spatial = (Spatial) object;
        final CullHint cullHint = spatial.getLocalCullHint();
        final ShadowMode shadowMode = spatial.getLocalShadowMode();
        final Bucket queueBucket = spatial.getLocalQueueBucket();

        if (changeConsumer instanceof SceneChangeConsumer) {

            final SceneLayer layer = SceneLayer.getLayer(spatial);
            final LayerModelPropertyControl propertyControl = new LayerModelPropertyControl(layer, (SceneChangeConsumer) changeConsumer);
            propertyControl.setEditObject(spatial);

            FXUtils.addToPane(propertyControl, container);
        }

        final EnumPropertyControl<ModelChangeConsumer, Spatial, CullHint> cullHintControl =
                new EnumPropertyControl<>(cullHint, Messages.MODEL_PROPERTY_CULL_HINT, changeConsumer, CULL_HINTS);
        cullHintControl.setApplyHandler(Spatial::setCullHint);
        cullHintControl.setSyncHandler(Spatial::getLocalCullHint);
        cullHintControl.setEditObject(spatial);

        final EnumPropertyControl<ModelChangeConsumer, Spatial, ShadowMode> shadowModeControl =
                new EnumPropertyControl<>(shadowMode, Messages.MODEL_PROPERTY_SHADOW_MODE, changeConsumer, SHADOW_MODES);
        shadowModeControl.setApplyHandler(Spatial::setShadowMode);
        shadowModeControl.setSyncHandler(Spatial::getLocalShadowMode);
        shadowModeControl.setEditObject(spatial);

        final EnumPropertyControl<ModelChangeConsumer, Spatial, Bucket> queueBucketControl =
                new EnumPropertyControl<>(queueBucket, Messages.MODEL_PROPERTY_QUEUE_BUCKET, changeConsumer, BUCKETS);
        queueBucketControl.setApplyHandler(Spatial::setQueueBucket);
        queueBucketControl.setSyncHandler(Spatial::getLocalQueueBucket);
        queueBucketControl.setEditObject(spatial);

        FXUtils.addToPane(cullHintControl, container);
        FXUtils.addToPane(shadowModeControl, container);
        FXUtils.addToPane(queueBucketControl, container);

        if (canEditTransformation(spatial)) {

            buildSplitLine(container);

            final Vector3f location = spatial.getLocalTranslation().clone();
            final Vector3f scale = spatial.getLocalScale().clone();
            final Quaternion rotation = spatial.getLocalRotation().clone();

            final Vector3FPropertyControl<ModelChangeConsumer, Spatial> locationControl =
                    new Vector3FPropertyControl<>(location, Messages.MODEL_PROPERTY_LOCATION, changeConsumer);
            locationControl.setApplyHandler(Spatial::setLocalTranslation);
            locationControl.setSyncHandler(Spatial::getLocalTranslation);

            locationControl.setEditObject(spatial);

            final Vector3FPropertyControl<ModelChangeConsumer, Spatial> scaleControl =
                    new Vector3FPropertyControl<>(scale, Messages.MODEL_PROPERTY_SCALE, changeConsumer);
            scaleControl.setApplyHandler(Spatial::setLocalScale);
            scaleControl.setSyncHandler(Spatial::getLocalScale);
            scaleControl.setEditObject(spatial);

            final QuaternionPropertyControl<ModelChangeConsumer, Spatial> rotationControl =
                    new QuaternionPropertyControl<>(rotation, Messages.MODEL_PROPERTY_ROTATION, changeConsumer);
            rotationControl.setApplyHandler(Spatial::setLocalRotation);
            rotationControl.setSyncHandler(Spatial::getLocalRotation);
            rotationControl.setEditObject(spatial);

            FXUtils.addToPane(locationControl, container);
            FXUtils.addToPane(scaleControl, container);
            FXUtils.addToPane(rotationControl, container);
        }

        final Collection<String> userDataKeys = spatial.getUserDataKeys();
        if (userDataKeys.isEmpty()) {
            return;
        }

        int count = 0;

        for (final String key : userDataKeys) {
            if (isNeedSkip(key)) continue;
            count++;
        }

        if(count < 1) {
            return;
        }

        buildSplitLine(container);

        final Array<String> sortedKeys = ArrayFactory.newSortedArray(String.class);
        sortedKeys.addAll(userDataKeys);

        for (final String key : sortedKeys) {
            if (isNeedSkip(key)) continue;

            final Object data = spatial.getUserData(key);

            if (data instanceof Float) {

                final Float value = (Float) data;

                final FloatPropertyControl<ModelChangeConsumer, Spatial> control =
                        new FloatPropertyControl<>(value, key, changeConsumer);
                control.setApplyHandler((sp, newValue) -> sp.setUserData(key, newValue));
                control.setSyncHandler(sp -> sp.getUserData(key));
                control.setEditObject(spatial);

                FXUtils.addToPane(control, container);

            } else if (data instanceof Integer) {

                final Integer value = (Integer) data;

                final IntegerPropertyControl<ModelChangeConsumer, Spatial> control =
                        new IntegerPropertyControl<>(value, key, changeConsumer);
                control.setApplyHandler((sp, newValue) -> sp.setUserData(key, newValue));
                control.setSyncHandler(sp -> sp.getUserData(key));
                control.setEditObject(spatial);

                FXUtils.addToPane(control, container);

            } else if (data instanceof Boolean) {

                final Boolean value = (Boolean) data;

                final BooleanPropertyControl<ModelChangeConsumer, Spatial> control =
                        new BooleanPropertyControl<>(value, key, changeConsumer);
                control.setApplyHandler((sp, newValue) -> sp.setUserData(key, newValue));
                control.setSyncHandler(sp -> sp.getUserData(key));
                control.setEditObject(spatial);

                FXUtils.addToPane(control, container);

            } else if (data instanceof Vector3f) {

                final Vector3f value = (Vector3f) data;

                final Vector3FPropertyControl<ModelChangeConsumer, Spatial> control =
                        new Vector3FPropertyControl<>(value, key, changeConsumer);
                control.setApplyHandler((sp, newValue) -> sp.setUserData(key, newValue));
                control.setSyncHandler(sp -> sp.getUserData(key));
                control.setEditObject(spatial);

                FXUtils.addToPane(control, container);

            } else if (data instanceof Vector2f) {

                final Vector2f value = (Vector2f) data;

                final Vector2FPropertyControl<ModelChangeConsumer, Spatial> control =
                        new Vector2FPropertyControl<>(value, key, changeConsumer);
                control.setApplyHandler((sp, newValue) -> sp.setUserData(key, newValue));
                control.setSyncHandler(sp -> sp.getUserData(key));
                control.setEditObject(spatial);

                FXUtils.addToPane(control, container);

            } else if (data instanceof ColorRGBA) {

                final ColorRGBA value = (ColorRGBA) data;

                final ColorPropertyControl<ModelChangeConsumer, Spatial> control =
                        new ColorPropertyControl<>(value, key, changeConsumer);
                control.setApplyHandler((sp, newValue) -> sp.setUserData(key, newValue));
                control.setSyncHandler(sp -> sp.getUserData(key));
                control.setEditObject(spatial);

                FXUtils.addToPane(control, container);

            } else if (data instanceof String) {

                final String value = (String) data;

                final StringPropertyControl<ModelChangeConsumer, Spatial> control =
                        new StringPropertyControl<>(value, key, changeConsumer);
                control.setApplyHandler((sp, newValue) -> sp.setUserData(key, newValue));
                control.setSyncHandler(sp -> sp.getUserData(key));
                control.setEditObject(spatial);

                FXUtils.addToPane(control, container);

            } else {

                final DefaultSinglePropertyControl<ModelChangeConsumer, Spatial, Object> control =
                        new DefaultSinglePropertyControl<>(data, key, changeConsumer);
                control.setSyncHandler(sp -> sp.getUserData(key));
                control.setEditObject(spatial);

                FXUtils.addToPane(control, container);
            }
        }
    }

    @FxThread
    private boolean isNeedSkip(@NotNull final String key) {
        return SceneLayer.KEY.equals(key) || KEY_LOADED_MODEL.equals(key);
    }

    @FxThread
    private boolean canEditTransformation(@NotNull final Spatial spatial) {
        return !(spatial instanceof SceneNode || spatial instanceof SceneLayer);
    }
}
