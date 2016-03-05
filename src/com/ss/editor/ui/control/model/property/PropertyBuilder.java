package com.ss.editor.ui.control.model.property;

import com.jme3.material.Material;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.Messages;
import com.ss.editor.ui.css.CSSIds;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import rlib.ui.util.FXUtils;

/**
 * Реализация конструктора контролов дял свойст части модели.
 *
 * @author Ronn
 */
public class PropertyBuilder {

    public static final Insets SPLIT_LINE_OFFSET = new Insets(14, 0, 14, 0);

    /**
     * Построесть список свойств для указанного объекта.
     *
     * @param object        объект для которого строятся свойства.
     * @param container     контейнер контролов.
     * @param changeHandler обработчик внесения изменений.
     */
    public static void buildFor(final Object object, final VBox container, final Runnable changeHandler) {

        if (object instanceof Mesh) {
            //TODO
        }

        if (object instanceof Geometry) {

            final Geometry geometry = (Geometry) object;
            final Material material = geometry.getMaterial();

            final ModelPropertyControl<Material> materialControl = new MaterialModelPropertyEditor(changeHandler, material, Messages.MODEL_PROPERTY_MATERIAL);
            materialControl.setApplyHandler(geometry::setMaterial);

            final Line splitLine = createSplitLine(container);

            FXUtils.addToPane(materialControl, container);
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

            final ModelPropertyControl<Spatial.CullHint> cullHintControl = new CullHintModelPropertyControl(changeHandler, cullHint, Messages.MODEL_PROPERTY_CULL_HINT);
            cullHintControl.setApplyHandler(spatial::setCullHint);

            final ModelPropertyControl<RenderQueue.ShadowMode> shadowModeControl = new ShadowModeModelPropertyControl(changeHandler, shadowMode, Messages.MODEL_PROPERTY_SHADOW_MODE);
            shadowModeControl.setApplyHandler(spatial::setShadowMode);

            final ModelPropertyControl<RenderQueue.Bucket> queueBucketControl = new QueueBucketModelPropertyControl(changeHandler, queueBucket, Messages.MODEL_PROPERTY_QUEUE_BUCKET);
            queueBucketControl.setApplyHandler(spatial::setQueueBucket);

            final ModelPropertyControl<Vector3f> locationControl = new Vector3fModelPropertyControl(changeHandler, location, Messages.MODEL_PROPERTY_LOCATION);
            locationControl.setApplyHandler(spatial::setLocalTranslation);

            final ModelPropertyControl<Vector3f> scaleControl = new Vector3fModelPropertyControl(changeHandler, scale, Messages.MODEL_PROPERTY_SCALE);
            scaleControl.setApplyHandler(spatial::setLocalScale);

            final ModelPropertyControl<Quaternion> rotationControl = new QuaternionModelPropertyControl(changeHandler, rotation, Messages.MODEL_PROPERTY_ROTATION);
            rotationControl.setApplyHandler(spatial::setLocalRotation);

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
