package com.ss.editor.ui.control.model.property.builder.impl;

import com.jme3.bullet.collision.shapes.*;
import com.jme3.bullet.collision.shapes.infos.ChildCollisionShape;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.builder.PropertyBuilder;
import com.ss.editor.ui.control.property.builder.impl.AbstractPropertyBuilder;
import com.ss.editor.ui.control.property.impl.DefaultSinglePropertyControl;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link PropertyBuilder} to build property controls for {@link CollisionShape} objects.
 *
 * @author JavaSaBr
 */
public class CollisionShapePropertyBuilder extends AbstractPropertyBuilder<ModelChangeConsumer> {

    @NotNull
    private static final PropertyBuilder INSTANCE = new CollisionShapePropertyBuilder();

    /**
     * Get the single instance.
     *
     * @return the single instance
     */
    @FromAnyThread
    public static @NotNull PropertyBuilder getInstance() {
        return INSTANCE;
    }

    private CollisionShapePropertyBuilder() {
        super(ModelChangeConsumer.class);
    }

    @Override
    @FXThread
    protected void buildForImpl(@NotNull final Object object, @Nullable final Object parent,
                                @NotNull final VBox container, @NotNull final ModelChangeConsumer changeConsumer) {

        if (object instanceof ChildCollisionShape) {
            build((ChildCollisionShape) object, container, changeConsumer);
        }

        if (!(object instanceof CollisionShape)) return;

        if (object instanceof BoxCollisionShape) {
            build((BoxCollisionShape) object, container, changeConsumer);
        } else if (object instanceof SphereCollisionShape) {
            build((SphereCollisionShape) object, container, changeConsumer);
        } else if (object instanceof CapsuleCollisionShape) {
            build((CapsuleCollisionShape) object, container, changeConsumer);
        } else if (object instanceof ConeCollisionShape) {
            build((ConeCollisionShape) object, container, changeConsumer);
        } else if (object instanceof CylinderCollisionShape) {
            build((CylinderCollisionShape) object, container, changeConsumer);
        }

        build((CollisionShape) object, container, changeConsumer);
    }

    @FXThread
    private void build(@NotNull final CylinderCollisionShape shape, @NotNull final VBox container,
                       @NotNull final ModelChangeConsumer changeConsumer) {

        final Vector3f halfExtents = shape.getHalfExtents();
        final float margin = shape.getMargin();
        final int axis = shape.getAxis();

        final DefaultSinglePropertyControl<ModelChangeConsumer, CylinderCollisionShape, Vector3f> halfExtentsControl =
                new DefaultSinglePropertyControl<>(halfExtents, Messages.MODEL_PROPERTY_HALF_EXTENTS, changeConsumer);

        halfExtentsControl.setSyncHandler(CylinderCollisionShape::getHalfExtents);
        halfExtentsControl.setToStringFunction(Vector3f::toString);
        halfExtentsControl.setEditObject(shape);

        final DefaultSinglePropertyControl<ModelChangeConsumer, CylinderCollisionShape, Float> marginControl =
                new DefaultSinglePropertyControl<>(margin, Messages.MODEL_PROPERTY_MARGIN, changeConsumer);

        marginControl.setSyncHandler(CylinderCollisionShape::getMargin);
        marginControl.setToStringFunction(value -> Float.toString(value));
        marginControl.setEditObject(shape);

        final DefaultSinglePropertyControl<ModelChangeConsumer, CylinderCollisionShape, Integer> axisControl =
                new DefaultSinglePropertyControl<>(axis, Messages.MODEL_PROPERTY_AXIS, changeConsumer);

        axisControl.setSyncHandler(CylinderCollisionShape::getAxis);
        axisControl.setToStringFunction(value -> Integer.toString(value));
        axisControl.setEditObject(shape);

        FXUtils.addToPane(halfExtentsControl, container);
        FXUtils.addToPane(marginControl, container);
        FXUtils.addToPane(axisControl, container);
    }

    private void build(@NotNull final ConeCollisionShape shape, @NotNull final VBox container,
                       @NotNull final ModelChangeConsumer changeConsumer) {

        final float radius = shape.getRadius();
        final float height = shape.getHeight();

        final DefaultSinglePropertyControl<ModelChangeConsumer, ConeCollisionShape, Float> radiusControl =
                new DefaultSinglePropertyControl<>(radius, Messages.MODEL_PROPERTY_RADIUS, changeConsumer);

        radiusControl.setSyncHandler(ConeCollisionShape::getRadius);
        radiusControl.setToStringFunction(value -> Float.toString(value));
        radiusControl.setEditObject(shape);

        final DefaultSinglePropertyControl<ModelChangeConsumer, ConeCollisionShape, Float> heightControl =
                new DefaultSinglePropertyControl<>(height, Messages.MODEL_PROPERTY_HEIGHT, changeConsumer);

        heightControl.setSyncHandler(ConeCollisionShape::getHeight);
        heightControl.setToStringFunction(value -> Float.toString(value));
        heightControl.setEditObject(shape);

        FXUtils.addToPane(radiusControl, container);
        FXUtils.addToPane(heightControl, container);
    }

    @FXThread
    private void build(@NotNull final CapsuleCollisionShape shape, @NotNull final VBox container,
                       @NotNull final ModelChangeConsumer changeConsumer) {

        final float radius = shape.getRadius();
        final float height = shape.getHeight();

        final int axis = shape.getAxis();

        final DefaultSinglePropertyControl<ModelChangeConsumer, CapsuleCollisionShape, Float> radiusControl =
                new DefaultSinglePropertyControl<>(radius, Messages.MODEL_PROPERTY_RADIUS, changeConsumer);

        radiusControl.setSyncHandler(CapsuleCollisionShape::getRadius);
        radiusControl.setToStringFunction(value -> Float.toString(value));
        radiusControl.setEditObject(shape);

        final DefaultSinglePropertyControl<ModelChangeConsumer, CapsuleCollisionShape, Float> heightControl =
                new DefaultSinglePropertyControl<>(height, Messages.MODEL_PROPERTY_HEIGHT, changeConsumer);

        heightControl.setSyncHandler(CapsuleCollisionShape::getHeight);
        heightControl.setToStringFunction(value -> Float.toString(value));
        heightControl.setEditObject(shape);

        final DefaultSinglePropertyControl<ModelChangeConsumer, CapsuleCollisionShape, Integer> axisControl =
                new DefaultSinglePropertyControl<>(axis, Messages.MODEL_PROPERTY_AXIS, changeConsumer);

        axisControl.setSyncHandler(CapsuleCollisionShape::getAxis);
        axisControl.setToStringFunction(value -> Integer.toString(value));
        axisControl.setEditObject(shape);

        FXUtils.addToPane(radiusControl, container);
        FXUtils.addToPane(heightControl, container);
        FXUtils.addToPane(axisControl, container);
    }

    @FXThread
    private void build(@NotNull final SphereCollisionShape shape, @NotNull final VBox container,
                       @NotNull final ModelChangeConsumer changeConsumer) {

        final float radius = shape.getRadius();

        final DefaultSinglePropertyControl<ModelChangeConsumer, SphereCollisionShape, Float> radiusControl =
                new DefaultSinglePropertyControl<>(radius, Messages.MODEL_PROPERTY_RADIUS, changeConsumer);

        radiusControl.setSyncHandler(SphereCollisionShape::getRadius);
        radiusControl.setToStringFunction(value -> Float.toString(value));
        radiusControl.setEditObject(shape);

        FXUtils.addToPane(radiusControl, container);
    }

    @FXThread
    private void build(@NotNull final BoxCollisionShape shape, @NotNull final VBox container,
                       @NotNull final ModelChangeConsumer changeConsumer) {

        final Vector3f halfExtents = shape.getHalfExtents();

        final DefaultSinglePropertyControl<ModelChangeConsumer, BoxCollisionShape, Vector3f> halfExtentsControl =
                new DefaultSinglePropertyControl<>(halfExtents, Messages.MODEL_PROPERTY_HALF_EXTENTS, changeConsumer);

        halfExtentsControl.setSyncHandler(BoxCollisionShape::getHalfExtents);
        halfExtentsControl.setToStringFunction(Vector3f::toString);
        halfExtentsControl.setEditObject(shape);

        FXUtils.addToPane(halfExtentsControl, container);
    }

    @FXThread
    private void build(@NotNull final CollisionShape shape, @NotNull final VBox container,
                       @NotNull final ModelChangeConsumer changeConsumer) {

        final Vector3f scale = shape.getScale();
        final long objectId = shape.getObjectId();
        final float margin = shape.getMargin();

        final DefaultSinglePropertyControl<ModelChangeConsumer, CollisionShape, Vector3f> scaleControl =
                new DefaultSinglePropertyControl<>(scale, Messages.MODEL_PROPERTY_SCALE, changeConsumer);

        scaleControl.setSyncHandler(CollisionShape::getScale);
        scaleControl.setToStringFunction(Vector3f::toString);
        scaleControl.setEditObject(shape);

        final DefaultSinglePropertyControl<ModelChangeConsumer, CollisionShape, Long> objectIdControl =
                new DefaultSinglePropertyControl<>(objectId, Messages.MODEL_PROPERTY_OBJECT_ID, changeConsumer);

        objectIdControl.setSyncHandler(CollisionShape::getObjectId);
        objectIdControl.setToStringFunction(String::valueOf);
        objectIdControl.setEditObject(shape);

        final DefaultSinglePropertyControl<ModelChangeConsumer, CollisionShape, Float> marginControl =
                new DefaultSinglePropertyControl<>(margin, Messages.MODEL_PROPERTY_MARGIN, changeConsumer);

        marginControl.setSyncHandler(CollisionShape::getMargin);
        marginControl.setToStringFunction(String::valueOf);
        marginControl.setEditObject(shape);

        FXUtils.addToPane(scaleControl, container);
        FXUtils.addToPane(marginControl, container);
        FXUtils.addToPane(objectIdControl, container);
    }

    @FXThread
    private void build(@NotNull final ChildCollisionShape shape, @NotNull final VBox container,
                       @NotNull final ModelChangeConsumer changeConsumer) {

        final Vector3f location = shape.location;
        final Matrix3f rotation = shape.rotation;

        final DefaultSinglePropertyControl<ModelChangeConsumer, ChildCollisionShape, Vector3f> locationControl =
                new DefaultSinglePropertyControl<>(location, Messages.MODEL_PROPERTY_LOCATION, changeConsumer);

        locationControl.setSyncHandler(collisionShape -> collisionShape.location);
        locationControl.setToStringFunction(Vector3f::toString);
        locationControl.setEditObject(shape);

        final DefaultSinglePropertyControl<ModelChangeConsumer, ChildCollisionShape, Matrix3f> rotationControl =
                new DefaultSinglePropertyControl<>(rotation, Messages.MODEL_PROPERTY_ROTATION, changeConsumer);

        rotationControl.setSyncHandler(collisionShape -> collisionShape.rotation);
        rotationControl.setToStringFunction(matrix3f -> new Quaternion().fromRotationMatrix(matrix3f).toString());
        rotationControl.setEditObject(shape);
        rotationControl.reload();

        FXUtils.addToPane(locationControl, container);
        FXUtils.addToPane(rotationControl, container);
    }
}
