package com.ss.editor.ui.control.property.builder.impl;

import static com.ss.editor.extension.property.EditablePropertyType.READ_ONLY_STRING;
import com.jme3.bullet.collision.shapes.*;
import com.jme3.bullet.collision.shapes.infos.ChildCollisionShape;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.property.EditableProperty;
import com.ss.editor.extension.property.SimpleProperty;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.builder.PropertyBuilder;
import com.ss.editor.ui.control.property.impl.DefaultSinglePropertyControl;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.fx.util.FXUtils;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * The implementation of the {@link PropertyBuilder} to build property controls for {@link CollisionShape} objects.
 *
 * @author JavaSaBr
 */
public class CollisionShapePropertyBuilder extends EditableModelObjectPropertyBuilder {

    private static final Array<Class<?>> SUPPORTED_TYPES = Array.of(
            ChildCollisionShape.class,
            CollisionShape.class
    );

    private static final PropertyBuilder INSTANCE = new CollisionShapePropertyBuilder();

    @FromAnyThread
    public static @NotNull PropertyBuilder getInstance() {
        return INSTANCE;
    }

    private CollisionShapePropertyBuilder() {
        super(ModelChangeConsumer.class);
    }

    @Override
    @FxThread
    protected @Nullable List<EditableProperty<?, ?>> getProperties(@NotNull Object object) {

        if (!SUPPORTED_TYPES.anyMatch(object, Class::isInstance)) {
            return null;
        }

        var properties = new ArrayList<EditableProperty<?, ?>>();

        if (object instanceof ChildCollisionShape) {

            var shape = (ChildCollisionShape) object;

            properties.add(new SimpleProperty<>(READ_ONLY_STRING, Messages.MODEL_PROPERTY_LOCATION, shape,
                    collisionShape -> String.valueOf(collisionShape.location)));
            properties.add(new SimpleProperty<>(READ_ONLY_STRING, Messages.MODEL_PROPERTY_ROTATION, shape,
                    collisionShape -> String.valueOf(new Quaternion().fromRotationMatrix(collisionShape.rotation))));
        }

        if (object instanceof BoxCollisionShape) {

            properties.add(new SimpleProperty<>(READ_ONLY_STRING, Messages.MODEL_PROPERTY_HALF_EXTENTS,
                    (BoxCollisionShape) object, shape -> String.valueOf(shape.getHalfExtents())));

        } else if (object instanceof SphereCollisionShape) {

            properties.add(new SimpleProperty<>(READ_ONLY_STRING, Messages.MODEL_PROPERTY_RADIUS,
                    (SphereCollisionShape) object, shape -> String.valueOf(shape.getRadius())));

        } else if (object instanceof CapsuleCollisionShape) {

            var shape = (CapsuleCollisionShape) object;

            properties.add(new SimpleProperty<>(READ_ONLY_STRING, Messages.MODEL_PROPERTY_RADIUS, shape,
                    collisionShape -> String.valueOf(collisionShape.getRadius())));
            properties.add(new SimpleProperty<>(READ_ONLY_STRING, Messages.MODEL_PROPERTY_HEIGHT, shape,
                    collisionShape -> String.valueOf(collisionShape.getHeight())));
            properties.add(new SimpleProperty<>(READ_ONLY_STRING, Messages.MODEL_PROPERTY_AXIS, shape,
                    collisionShape -> String.valueOf(collisionShape.getAxis())));

        } else if (object instanceof ConeCollisionShape) {

            var shape = (ConeCollisionShape) object;

            properties.add(new SimpleProperty<>(READ_ONLY_STRING, Messages.MODEL_PROPERTY_RADIUS, shape,
                    collisionShape -> String.valueOf(collisionShape.getRadius())));
            properties.add(new SimpleProperty<>(READ_ONLY_STRING, Messages.MODEL_PROPERTY_HEIGHT, shape,
                    collisionShape -> String.valueOf(collisionShape.getHeight())));

        } else if (object instanceof CylinderCollisionShape) {

            var shape = (CylinderCollisionShape) object;

            properties.add(new SimpleProperty<>(READ_ONLY_STRING, Messages.MODEL_PROPERTY_HALF_EXTENTS, shape,
                    collisionShape -> String.valueOf(collisionShape.getHalfExtents())));
            properties.add(new SimpleProperty<>(READ_ONLY_STRING, Messages.MODEL_PROPERTY_AXIS, shape,
                    collisionShape -> String.valueOf(collisionShape.getAxis())));
        }

        if (object instanceof CollisionShape) {

            var shape = (CollisionShape) object;

            properties.add(new SimpleProperty<>(READ_ONLY_STRING, Messages.MODEL_PROPERTY_SCALE, shape,
                    collisionShape -> String.valueOf(collisionShape.getScale())));
            properties.add(new SimpleProperty<>(READ_ONLY_STRING, Messages.MODEL_PROPERTY_OBJECT_ID, shape,
                    collisionShape -> String.valueOf(collisionShape.getObjectId())));
            properties.add(new SimpleProperty<>(READ_ONLY_STRING, Messages.MODEL_PROPERTY_MARGIN, shape,
                    collisionShape -> String.valueOf(collisionShape.getMargin())));
        }

        return properties;
    }
}
