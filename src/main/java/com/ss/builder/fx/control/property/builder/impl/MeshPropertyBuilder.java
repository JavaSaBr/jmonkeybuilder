package com.ss.builder.fx.control.property.builder.impl;

import com.jme3.scene.Mesh;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.editor.extension.property.EditableProperty;
import com.ss.editor.extension.property.SimpleProperty;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.fx.control.property.builder.PropertyBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.ss.editor.extension.property.EditablePropertyType.ENUM;
import static com.ss.editor.extension.property.EditablePropertyType.READ_ONLY_STRING;

/**
 * The implementation of the {@link PropertyBuilder} to build property controls for {@link Mesh} objects.
 *
 * @author JavaSaBr
 */
public class MeshPropertyBuilder extends EditableModelObjectPropertyBuilder {

    private static final PropertyBuilder INSTANCE = new MeshPropertyBuilder();

    @FromAnyThread
    public static @NotNull PropertyBuilder getInstance() {
        return INSTANCE;
    }

    private MeshPropertyBuilder() {
        super(ModelChangeConsumer.class);
    }

    @Override
    @FxThread
    protected @Nullable List<EditableProperty<?, ?>> getProperties(@NotNull Object object) {

        if (!(object instanceof Mesh)) {
            return null;
        }

        var mesh = (Mesh) object;
        var properties = new ArrayList<EditableProperty<?, ?>>();

        properties.add(new SimpleProperty<>(READ_ONLY_STRING, Messages.MODEL_PROPERTY_ID, mesh,
                Mesh::getId));
        properties.add(new SimpleProperty<>(READ_ONLY_STRING, Messages.MODEL_PROPERTY_INSTANCE_COUNT, mesh,
                Mesh::getInstanceCount));
        properties.add(new SimpleProperty<>(READ_ONLY_STRING, Messages.MODEL_PROPERTY_INSTANCE_COUNT, mesh,
                Mesh::getInstanceCount));
        properties.add(new SimpleProperty<>(READ_ONLY_STRING, Messages.MODEL_PROPERTY_VERTEX_COUNT, mesh,
                Mesh::getVertexCount));
        properties.add(new SimpleProperty<>(READ_ONLY_STRING, Messages.MODEL_PROPERTY_TRIANGLE_COUNT, mesh,
                Mesh::getTriangleCount));
        properties.add(new SimpleProperty<>(READ_ONLY_STRING, Messages.MODEL_PROPERTY_NUM_LOD_LEVELS, mesh,
                Mesh::getNumLodLevels));
        properties.add(new SimpleProperty<>(ENUM, Messages.MODEL_PROPERTY_MODE, mesh,
                Mesh::getMode, Mesh::setMode));

        return properties;
    }
}
