package com.ss.builder.fx.control.property.builder.impl;

import static com.ss.editor.extension.property.EditablePropertyType.FLOAT;
import static com.ss.editor.extension.property.EditablePropertyType.VECTOR_3F;
import com.jme3.effect.shapes.EmitterBoxShape;
import com.jme3.effect.shapes.EmitterPointShape;
import com.jme3.effect.shapes.EmitterShape;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.editor.extension.property.EditableProperty;
import com.ss.editor.extension.property.SeparatorProperty;
import com.ss.editor.extension.property.SimpleProperty;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.fx.control.property.builder.PropertyBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * The implementation of the {@link PropertyBuilder} to build property controls for {@link EmitterShape}.
 *
 * @author JavaSaBr
 */
public class EmitterShapePropertyBuilder extends EditableModelObjectPropertyBuilder {

    private static final PropertyBuilder INSTANCE = new EmitterShapePropertyBuilder();

    @FromAnyThread
    public static @NotNull PropertyBuilder getInstance() {
        return INSTANCE;
    }

    private EmitterShapePropertyBuilder() {
        super(ModelChangeConsumer.class);
    }

    @Override
    @FxThread
    protected @Nullable List<EditableProperty<?, ?>> getProperties(@NotNull Object object) {

        if (!(object instanceof EmitterShape)) {
            return null;
        }

        var shape = (EmitterShape) object;
        var properties = new ArrayList<EditableProperty<?, ?>>();

        if (shape instanceof EmitterPointShape) {

            properties.add(new SimpleProperty<>(VECTOR_3F, Messages.MODEL_PROPERTY_POINT, (EmitterPointShape) shape,
                    EmitterPointShape::getPoint, EmitterPointShape::setPoint));

        } else if (shape instanceof EmitterBoxShape) {

            properties.add(new SimpleProperty<>(VECTOR_3F, Messages.MODEL_PROPERTY_LENGTH, (EmitterBoxShape) shape,
                    EmitterBoxShape::getLen, EmitterBoxShape::setLen));
            properties.add(new SimpleProperty<>(VECTOR_3F, Messages.MODEL_PROPERTY_MIN, (EmitterBoxShape) shape,
                    EmitterBoxShape::getMin, EmitterBoxShape::setMin));

        } else if (shape instanceof EmitterSphereShape) {

            properties.add(new SimpleProperty<>(FLOAT, Messages.MODEL_PROPERTY_RADIUS, (EmitterSphereShape) shape,
                    EmitterSphereShape::getRadius, EmitterSphereShape::setRadius));

            properties.add(SeparatorProperty.getInstance());

            properties.add(new SimpleProperty<>(VECTOR_3F, Messages.MODEL_PROPERTY_CENTER, (EmitterSphereShape) shape,
                    EmitterSphereShape::getCenter, EmitterSphereShape::setCenter));
        }

        return properties;
    }
}
