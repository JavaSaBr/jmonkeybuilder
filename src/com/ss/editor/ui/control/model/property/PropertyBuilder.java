package com.ss.editor.ui.control.model.property;

import com.jme3.asset.AssetManager;
import com.jme3.asset.MaterialKey;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingSphere;
import com.jme3.bounding.BoundingVolume;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.Editor;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.css.CSSIds;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import rlib.ui.util.FXUtils;
import rlib.util.StringUtils;

import static com.ss.editor.util.EditorUtil.clipNumber;

/**
 * Реализация конструктора контролов дял свойст части модели.
 *
 * @author Ronn
 */
public class PropertyBuilder {

    private static final Editor EDITOR = Editor.getInstance();

    public static final Insets SPLIT_LINE_OFFSET = new Insets(14, 0, 14, 0);

    public static final Function<BoundingVolume, String> BOUNDING_VOLUME_TO_STRING = boundingVolume -> {

        if (boundingVolume instanceof BoundingSphere) {
            final BoundingSphere boundingSphere = (BoundingSphere) boundingVolume;
            return Messages.BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_SPHERE + ": " + "[" + Messages.BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_SPHERE_RADIUS + "=" + boundingSphere.getRadius() + "]";
        } else if (boundingVolume instanceof BoundingBox) {

            final BoundingBox boundingBox = (BoundingBox) boundingVolume;

            final float xExtent = clipNumber(boundingBox.getXExtent(), 100);
            final float yExtent = clipNumber(boundingBox.getYExtent(), 100);
            final float zExtent = clipNumber(boundingBox.getZExtent(), 100);

            return Messages.BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_BOX + ": " + "[x=" + xExtent + ", y=" + yExtent + ", z=" + zExtent + "]";
        }

        return StringUtils.EMPTY;
    };

    public static final BiConsumer<Geometry, MaterialKey> MATERIAL_APPLY_HANDLER = (geometry, materialKey) -> {

        final AssetManager assetManager = EDITOR.getAssetManager();

        if (materialKey == null) {

            final Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            material.setColor("Color", ColorRGBA.Gray);

            geometry.setMaterial(material);

        } else {

            assetManager.clearCache();

            final Material material = assetManager.loadAsset(materialKey);
            geometry.setMaterial(material);
        }
    };

    public static final Function<Geometry, MaterialKey> MATERIAL_SYNC_HANDLER = geometry -> {
        final Material material = geometry.getMaterial();
        return (MaterialKey) material.getKey();
    };

    /**
     * Построесть список свойств для указанного объекта.
     *
     * @param object        объект для которого строятся свойства.
     * @param container     контейнер контролов.
     * @param changeHandler обработчик внесения изменений.
     */
    public static void buildFor(final Object object, final VBox container, final Consumer<EditorOperation> changeHandler, final ModelChangeConsumer modelChangeConsumer) {

        if (object instanceof Mesh) {
            //TODO
        }

        if (object instanceof Geometry) {

            final Geometry geometry = (Geometry) object;
            final Material material = geometry.getMaterial();
            final MaterialKey materialKey = (MaterialKey) material.getKey();
            final BoundingVolume modelBound = geometry.getModelBound();

            final ModelPropertyControl<Geometry, MaterialKey> materialControl = new MaterialModelPropertyEditor(changeHandler, materialKey, Messages.MODEL_PROPERTY_MATERIAL, modelChangeConsumer);
            materialControl.setApplyHandler(MATERIAL_APPLY_HANDLER);
            materialControl.setSyncHandler(MATERIAL_SYNC_HANDLER);
            materialControl.setEditObject(geometry);

            final DefaultModelPropertyControl<BoundingVolume> boundingVolumeControl = new DefaultModelPropertyControl<>(changeHandler, modelBound, Messages.BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_NAME, modelChangeConsumer);
            boundingVolumeControl.setToStringFunction(BOUNDING_VOLUME_TO_STRING);
            boundingVolumeControl.reload();
            boundingVolumeControl.setEditObject(geometry);

            final Line splitLine = createSplitLine(container);

            FXUtils.addToPane(materialControl, container);
            FXUtils.addToPane(boundingVolumeControl, container);
            FXUtils.addToPane(splitLine, container);

            VBox.setMargin(splitLine, SPLIT_LINE_OFFSET);
        }

        if (object instanceof Node) {
            //TODO
        }

        if (object instanceof Spatial) {

            final Spatial spatial = (Spatial) object;

            final Spatial.CullHint cullHint = spatial.getLocalCullHint();
            final RenderQueue.ShadowMode shadowMode = spatial.getLocalShadowMode();
            final RenderQueue.Bucket queueBucket = spatial.getLocalQueueBucket();
            final Vector3f location = spatial.getLocalTranslation().clone();
            final Vector3f scale = spatial.getLocalScale().clone();

            final Quaternion rotation = spatial.getLocalRotation().clone();

            final ModelPropertyControl<Spatial, Spatial.CullHint> cullHintControl = new CullHintModelPropertyControl(changeHandler, cullHint, Messages.MODEL_PROPERTY_CULL_HINT, modelChangeConsumer);
            cullHintControl.setApplyHandler(Spatial::setCullHint);
            cullHintControl.setSyncHandler(Spatial::getCullHint);
            cullHintControl.setEditObject(spatial);

            final ModelPropertyControl<Spatial, RenderQueue.ShadowMode> shadowModeControl = new ShadowModeModelPropertyControl(changeHandler, shadowMode, Messages.MODEL_PROPERTY_SHADOW_MODE, modelChangeConsumer);
            shadowModeControl.setApplyHandler(Spatial::setShadowMode);
            shadowModeControl.setSyncHandler(Spatial::getShadowMode);
            shadowModeControl.setEditObject(spatial);

            final ModelPropertyControl<Spatial, RenderQueue.Bucket> queueBucketControl = new QueueBucketModelPropertyControl(changeHandler, queueBucket, Messages.MODEL_PROPERTY_QUEUE_BUCKET, modelChangeConsumer);
            queueBucketControl.setApplyHandler(Spatial::setQueueBucket);
            queueBucketControl.setSyncHandler(Spatial::getQueueBucket);
            queueBucketControl.setEditObject(spatial);

            final ModelPropertyControl<Spatial, Vector3f> locationControl = new Vector3fModelPropertyControl(changeHandler, location, Messages.MODEL_PROPERTY_LOCATION, modelChangeConsumer);
            locationControl.setApplyHandler(Spatial::setLocalTranslation);
            locationControl.setSyncHandler(Spatial::getLocalTranslation);
            locationControl.setEditObject(spatial);

            final ModelPropertyControl<Spatial, Vector3f> scaleControl = new Vector3fModelPropertyControl(changeHandler, scale, Messages.MODEL_PROPERTY_SCALE, modelChangeConsumer);
            scaleControl.setApplyHandler(Spatial::setLocalScale);
            scaleControl.setSyncHandler(Spatial::getLocalScale);
            scaleControl.setEditObject(spatial);

            final ModelPropertyControl<Spatial, Quaternion> rotationControl = new QuaternionModelPropertyControl(changeHandler, rotation, Messages.MODEL_PROPERTY_ROTATION, modelChangeConsumer);
            rotationControl.setApplyHandler(Spatial::setLocalRotation);
            rotationControl.setSyncHandler(Spatial::getLocalRotation);
            rotationControl.setEditObject(spatial);

            final Line splitLine = createSplitLine(container);

            FXUtils.addToPane(cullHintControl, container);
            FXUtils.addToPane(shadowModeControl, container);
            FXUtils.addToPane(queueBucketControl, container);
            FXUtils.addToPane(splitLine, container);
            FXUtils.addToPane(locationControl, container);
            FXUtils.addToPane(scaleControl, container);
            FXUtils.addToPane(rotationControl, container);

            VBox.setMargin(splitLine, SPLIT_LINE_OFFSET);
        }
    }

    /**
     * Создать линию-разделителя.
     */
    private static Line createSplitLine(final VBox container) {

        final Line line = new Line();
        line.setId(CSSIds.MODEL_PARAM_CONTROL_SPLIT_LINE);
        line.setStartX(0);
        line.endXProperty().bind(container.widthProperty().subtract(70));

        return line;
    }
}
