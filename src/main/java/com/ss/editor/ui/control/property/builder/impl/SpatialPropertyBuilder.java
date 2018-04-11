package com.ss.editor.ui.control.property.builder.impl;

import static com.ss.editor.part3d.editor.impl.scene.AbstractSceneEditor3DPart.KEY_LOADED_MODEL;
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

/**
 * The implementation of the {@link PropertyBuilder} to build property controls for {@link Spatial} objects.
 *
 * @author JavaSaBr
 */
public class SpatialPropertyBuilder extends AbstractPropertyBuilder<ModelChangeConsumer> {

    public static final int PRIORITY = 1;

    @NotNull
    private static final CullHint[] CULL_HINTS = CullHint.values();

    @NotNull
    private static final ShadowMode[] SHADOW_MODES = ShadowMode.values();

    @NotNull
    private static final Bucket[] BUCKETS = Bucket.values();

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
    @FxThread
    protected void buildForImpl(
            @NotNull Object object,
            @Nullable Object parent,
            @NotNull VBox container,
            @NotNull ModelChangeConsumer changeConsumer
    ) {

        if (!(object instanceof Spatial)) {
            return;
        }

        var spatial = (Spatial) object;
        var cullHint = spatial.getLocalCullHint();
        var shadowMode = spatial.getLocalShadowMode();
        var queueBucket = spatial.getLocalQueueBucket();

        if (changeConsumer instanceof SceneChangeConsumer) {

            var layer = SceneLayer.getLayer(spatial);
            var propertyControl = new LayerModelPropertyControl(layer, (SceneChangeConsumer) changeConsumer);
            propertyControl.setEditObject(spatial);

            FXUtils.addToPane(propertyControl, container);
        }

        EnumPropertyControl<ModelChangeConsumer, Spatial, CullHint> cullHintControl =
                new EnumPropertyControl<>(cullHint, Messages.MODEL_PROPERTY_CULL_HINT, changeConsumer, CULL_HINTS);
        cullHintControl.setApplyHandler(Spatial::setCullHint);
        cullHintControl.setSyncHandler(Spatial::getLocalCullHint);
        cullHintControl.setEditObject(spatial);

        EnumPropertyControl<ModelChangeConsumer, Spatial, ShadowMode> shadowModeControl =
                new EnumPropertyControl<>(shadowMode, Messages.MODEL_PROPERTY_SHADOW_MODE, changeConsumer, SHADOW_MODES);
        shadowModeControl.setApplyHandler(Spatial::setShadowMode);
        shadowModeControl.setSyncHandler(Spatial::getLocalShadowMode);
        shadowModeControl.setEditObject(spatial);

        EnumPropertyControl<ModelChangeConsumer, Spatial, Bucket> queueBucketControl =
                new EnumPropertyControl<>(queueBucket, Messages.MODEL_PROPERTY_QUEUE_BUCKET, changeConsumer, BUCKETS);
        queueBucketControl.setApplyHandler(Spatial::setQueueBucket);
        queueBucketControl.setSyncHandler(Spatial::getLocalQueueBucket);
        queueBucketControl.setEditObject(spatial);

        FXUtils.addToPane(cullHintControl, container);
        FXUtils.addToPane(shadowModeControl, container);
        FXUtils.addToPane(queueBucketControl, container);

        if (canEditTransformation(spatial)) {

            buildSplitLine(container);

            var location = spatial.getLocalTranslation().clone();
            var scale = spatial.getLocalScale().clone();
            var rotation = spatial.getLocalRotation().clone();

            Vector3FPropertyControl<ModelChangeConsumer, Spatial> locationControl =
                    new Vector3FPropertyControl<>(location, Messages.MODEL_PROPERTY_LOCATION, changeConsumer);
            locationControl.setApplyHandler(Spatial::setLocalTranslation);
            locationControl.setSyncHandler(Spatial::getLocalTranslation);
            locationControl.setEditObject(spatial);

            Vector3FPropertyControl<ModelChangeConsumer, Spatial> scaleControl =
                    new Vector3FPropertyControl<>(scale, Messages.MODEL_PROPERTY_SCALE, changeConsumer);
            scaleControl.setApplyHandler(Spatial::setLocalScale);
            scaleControl.setSyncHandler(Spatial::getLocalScale);
            scaleControl.setEditObject(spatial);

            QuaternionPropertyControl<ModelChangeConsumer, Spatial> rotationControl =
                    new QuaternionPropertyControl<>(rotation, Messages.MODEL_PROPERTY_ROTATION, changeConsumer);
            rotationControl.setApplyHandler(Spatial::setLocalRotation);
            rotationControl.setSyncHandler(Spatial::getLocalRotation);
            rotationControl.setEditObject(spatial);

            FXUtils.addToPane(locationControl, container);
            FXUtils.addToPane(scaleControl, container);
            FXUtils.addToPane(rotationControl, container);
        }

        var userDataKeys = spatial.getUserDataKeys();
        if (userDataKeys.isEmpty()) {
            return;
        }

        var count = 0;

        for (var key : userDataKeys) {
            if (isNeedSkip(key)) continue;
            count++;
        }

        if (count < 1) {
            return;
        }

        buildSplitLine(container);

        final Array<String> sortedKeys = ArrayFactory.newSortedArray(String.class);
        sortedKeys.addAll(userDataKeys);

        for (var key : sortedKeys) {

            if (isNeedSkip(key)) {
                continue;
            }

            var data = spatial.getUserData(key);

            if (data instanceof Float) {

                var value = (Float) data;

                var control = new FloatPropertyControl<ModelChangeConsumer, Spatial>(value, key, changeConsumer);
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
