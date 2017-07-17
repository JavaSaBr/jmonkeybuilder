package com.ss.editor.ui.control.model.property.builder.impl;

import com.jme3.effect.shapes.*;
import com.jme3.math.Vector3f;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.control.FloatModelPropertyControl;
import com.ss.editor.ui.control.model.property.control.Vector3fModelPropertyControl;
import com.ss.editor.ui.control.property.builder.PropertyBuilder;
import com.ss.editor.ui.control.property.builder.impl.AbstractPropertyBuilder;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link PropertyBuilder} to build property controls for {@link EmitterShape}.
 *
 * @author JavaSaBr
 */
public class EmitterShapePropertyBuilder extends AbstractPropertyBuilder<ModelChangeConsumer> {

    @NotNull
    private static final PropertyBuilder INSTANCE = new EmitterShapePropertyBuilder();

    /**
     * Gets instance.
     *
     * @return the instance
     */
    @NotNull
    public static PropertyBuilder getInstance() {
        return INSTANCE;
    }

    private EmitterShapePropertyBuilder() {
        super(ModelChangeConsumer.class);
    }

    @Override
    protected void buildForImpl(@NotNull final Object object, @Nullable final Object parent,
                                @NotNull final VBox container, @NotNull final ModelChangeConsumer changeConsumer) {

        if (!(object instanceof EmitterShape)) return;

        final EmitterShape shape = (EmitterShape) object;

        if (shape instanceof EmitterPointShape) {
            createControls(container, changeConsumer, (EmitterPointShape) object);
        } else if (shape instanceof EmitterBoxShape) {
            createControls(container, changeConsumer, (EmitterBoxShape) object);
        } else if (shape instanceof EmitterSphereShape) {
            createControls(container, changeConsumer, (EmitterSphereShape) object);
        }
    }

    /**
     * Create controls.
     *
     * @param container      the container
     * @param changeConsumer the change consumer
     * @param shape          the shape
     */
    private void createControls(@NotNull final VBox container, @NotNull final ModelChangeConsumer changeConsumer,
                                @NotNull final EmitterPointShape shape) {

        final Vector3f point = shape.getPoint();

        final Vector3fModelPropertyControl<EmitterPointShape> pointControl =
                new Vector3fModelPropertyControl<>(point, Messages.MODEL_PROPERTY_POINT, changeConsumer);

        pointControl.setSyncHandler(EmitterPointShape::getPoint);
        pointControl.setApplyHandler(EmitterPointShape::setPoint);
        pointControl.setEditObject(shape);

        FXUtils.addToPane(pointControl, container);
    }

    /**
     * Create controls.
     *
     * @param container      the container
     * @param changeConsumer the change consumer
     * @param shape          the shape
     */
    private void createControls(@NotNull final VBox container, @NotNull final ModelChangeConsumer changeConsumer,
                                @NotNull final EmitterBoxShape shape) {

        final Vector3f length = shape.getLen();
        final Vector3f min = shape.getMin();

        final Vector3fModelPropertyControl<EmitterBoxShape> lengthControl =
                new Vector3fModelPropertyControl<>(length, Messages.MODEL_PROPERTY_LENGTH, changeConsumer);

        lengthControl.setSyncHandler(EmitterBoxShape::getLen);
        lengthControl.setApplyHandler(EmitterBoxShape::setLen);
        lengthControl.setEditObject(shape);

        final Vector3fModelPropertyControl<EmitterBoxShape> minControl =
                new Vector3fModelPropertyControl<>(min, Messages.MODEL_PROPERTY_MIN, changeConsumer);

        minControl.setSyncHandler(EmitterBoxShape::getMin);
        minControl.setApplyHandler(EmitterBoxShape::setMin);
        minControl.setEditObject(shape);

        FXUtils.addToPane(lengthControl, container);
        FXUtils.addToPane(minControl, container);
    }

    /**
     * Create controls.
     *
     * @param container      the container
     * @param changeConsumer the change consumer
     * @param shape          the shape
     */
    private void createControls(@NotNull final VBox container, @NotNull final ModelChangeConsumer changeConsumer,
                                @NotNull final EmitterSphereShape shape) {

        final Vector3f center = shape.getCenter();
        final float radius = shape.getRadius();

        final FloatModelPropertyControl<EmitterSphereShape> radiusControl =
                new FloatModelPropertyControl<>(radius, Messages.MODEL_PROPERTY_RADIUS, changeConsumer);

        radiusControl.setSyncHandler(EmitterSphereShape::getRadius);
        radiusControl.setApplyHandler(EmitterSphereShape::setRadius);
        radiusControl.setEditObject(shape);

        final Vector3fModelPropertyControl<EmitterSphereShape> centerControl =
                new Vector3fModelPropertyControl<>(center, Messages.MODEL_PROPERTY_CENTER, changeConsumer);

        centerControl.setSyncHandler(EmitterSphereShape::getCenter);
        centerControl.setApplyHandler(EmitterSphereShape::setCenter);
        centerControl.setEditObject(shape);

        FXUtils.addToPane(centerControl, container);
        buildSplitLine(container);
        FXUtils.addToPane(radiusControl, container);
    }
}
