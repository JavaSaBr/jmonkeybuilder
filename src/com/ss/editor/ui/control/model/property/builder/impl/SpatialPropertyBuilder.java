package com.ss.editor.ui.control.model.property.builder.impl;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.builder.PropertyBuilder;
import com.ss.editor.ui.control.model.property.control.EnumModelPropertyControl;
import com.ss.editor.ui.control.model.property.control.QuaternionModelPropertyControl;
import com.ss.editor.ui.control.model.property.control.Vector3fModelPropertyControl;
import com.ss.extension.scene.SceneLayer;
import com.ss.extension.scene.SceneNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;

/**
 * The implementation of the {@link PropertyBuilder} for building property controls for {@link Spatial} objects.
 *
 * @author JavaSaBr
 */
public class SpatialPropertyBuilder extends AbstractPropertyBuilder {

    private static final CullHint[] CULL_HINTS = CullHint.values();
    private static final ShadowMode[] SHADOW_MODES = ShadowMode.values();
    private static final Bucket[] BUCKETS = Bucket.values();

    private static final PropertyBuilder INSTANCE = new SpatialPropertyBuilder();

    public static PropertyBuilder getInstance() {
        return INSTANCE;
    }

    @Override
    public void buildFor(@NotNull final Object object, @Nullable final Object parent, @NotNull final VBox container,
                         @NotNull final ModelChangeConsumer modelChangeConsumer) {

        if (!(object instanceof Spatial)) return;

        final Spatial spatial = (Spatial) object;
        final CullHint cullHint = spatial.getLocalCullHint();
        final ShadowMode shadowMode = spatial.getLocalShadowMode();
        final Bucket queueBucket = spatial.getLocalQueueBucket();

        final EnumModelPropertyControl<Spatial, CullHint> cullHintControl =
                new EnumModelPropertyControl<>(cullHint, Messages.MODEL_PROPERTY_CULL_HINT, modelChangeConsumer, CULL_HINTS);
        cullHintControl.setApplyHandler(Spatial::setCullHint);
        cullHintControl.setSyncHandler(Spatial::getLocalCullHint);
        cullHintControl.setEditObject(spatial);

        final EnumModelPropertyControl<Spatial, ShadowMode> shadowModeControl =
                new EnumModelPropertyControl<>(shadowMode, Messages.MODEL_PROPERTY_SHADOW_MODE, modelChangeConsumer, SHADOW_MODES);
        shadowModeControl.setApplyHandler(Spatial::setShadowMode);
        shadowModeControl.setSyncHandler(Spatial::getLocalShadowMode);
        shadowModeControl.setEditObject(spatial);

        final EnumModelPropertyControl<Spatial, Bucket> queueBucketControl =
                new EnumModelPropertyControl<>(queueBucket, Messages.MODEL_PROPERTY_QUEUE_BUCKET, modelChangeConsumer, BUCKETS);
        queueBucketControl.setApplyHandler(Spatial::setQueueBucket);
        queueBucketControl.setSyncHandler(Spatial::getLocalQueueBucket);
        queueBucketControl.setEditObject(spatial);

        FXUtils.addToPane(cullHintControl, container);
        FXUtils.addToPane(shadowModeControl, container);
        FXUtils.addToPane(queueBucketControl, container);

        if (!canEditTransformation(spatial)) return;

        final Vector3f location = spatial.getLocalTranslation().clone();
        final Vector3f scale = spatial.getLocalScale().clone();
        final Quaternion rotation = spatial.getLocalRotation().clone();

        final Vector3fModelPropertyControl<Spatial> locationControl =
                new Vector3fModelPropertyControl<>(location, Messages.MODEL_PROPERTY_LOCATION, modelChangeConsumer);
        locationControl.setApplyHandler(Spatial::setLocalTranslation);
        locationControl.setSyncHandler(Spatial::getLocalTranslation);
        locationControl.setEditObject(spatial);

        final Vector3fModelPropertyControl<Spatial> scaleControl =
                new Vector3fModelPropertyControl<>(scale, Messages.MODEL_PROPERTY_SCALE, modelChangeConsumer);
        scaleControl.setApplyHandler(Spatial::setLocalScale);
        scaleControl.setSyncHandler(Spatial::getLocalScale);
        scaleControl.setEditObject(spatial);

        final QuaternionModelPropertyControl rotationControl =
                new QuaternionModelPropertyControl(rotation, Messages.MODEL_PROPERTY_ROTATION, modelChangeConsumer);
        rotationControl.setApplyHandler(Spatial::setLocalRotation);
        rotationControl.setSyncHandler(Spatial::getLocalRotation);
        rotationControl.setEditObject(spatial);

        addSplitLine(container);

        FXUtils.addToPane(locationControl, container);
        FXUtils.addToPane(scaleControl, container);
        FXUtils.addToPane(rotationControl, container);
    }

    private boolean canEditTransformation(@NotNull final Spatial spatial) {
        return !(spatial instanceof SceneNode || spatial instanceof SceneLayer);
    }
}
