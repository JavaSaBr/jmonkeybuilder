package com.ss.editor.ui.control.property.builder.impl;

import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.property.EditableProperty;
import com.ss.editor.extension.property.SimpleProperty;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.builder.PropertyBuilder;
import com.ss.editor.ui.control.property.impl.DefaultSinglePropertyControl;
import com.ss.editor.ui.control.property.impl.Vector3fPropertyControl;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.fx.util.FXUtils;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ss.editor.extension.property.EditablePropertyType.ENUM;
import static com.ss.editor.extension.property.EditablePropertyType.READ_ONLY_STRING;
import static com.ss.editor.extension.property.EditablePropertyType.VECTOR_3F;

/**
 * The implementation of the {@link PropertyBuilder} to build property controls for primitive objects objects.
 *
 * @author JavaSaBr
 */
public class PrimitivePropertyBuilder extends EditableModelObjectPropertyBuilder {

    private static final Array<Class<?>> SUPPORTED_TYPES = Array.of(
            Vector3f.class,
            VertexBuffer.class,
            Buffer.class
    );

    private static final PropertyBuilder INSTANCE = new PrimitivePropertyBuilder();

    @FromAnyThread
    public static @NotNull PropertyBuilder getInstance() {
        return INSTANCE;
    }

    private PrimitivePropertyBuilder() {
        super(ModelChangeConsumer.class);
    }

    @Override
    protected @Nullable List<EditableProperty<?, ?>> getProperties(
            @NotNull Object object,
            @Nullable Object parent,
            @NotNull ModelChangeConsumer changeConsumer
    ) {

        if (!SUPPORTED_TYPES.anyMatch(object, Class::isInstance)) {
            return null;
        }

        var properties = new ArrayList<EditableProperty<?, ?>>();

        if (object instanceof Vector3f) {

            properties.add(new SimpleProperty<>(VECTOR_3F, Messages.MODEL_PROPERTY_VALUE, (Vector3f) object,
                    Vector3f::clone, Vector3f::set));

        } else if (object instanceof VertexBuffer) {

            var vertexBuffer = (VertexBuffer) object;
            var data = vertexBuffer.getData();

            if (data == null) {
                return null;
            }

            properties.add(new SimpleProperty<>(READ_ONLY_STRING, Messages.MODEL_PROPERTY_TYPE,
                    vertexBuffer.getBufferType(), Enum::name));
            properties.add(new SimpleProperty<>(READ_ONLY_STRING, Messages.MODEL_PROPERTY_FORMAT,
                    vertexBuffer.getFormat(), Enum::name));
            properties.add(new SimpleProperty<>(READ_ONLY_STRING, Messages.MODEL_PROPERTY_USAGE,
                    vertexBuffer.getUsage(), Enum::name));
            properties.add(new SimpleProperty<>(READ_ONLY_STRING, Messages.MODEL_PROPERTY_UNIQ_ID,
                    vertexBuffer.getUniqueId(), String::valueOf));
            properties.add(new SimpleProperty<>(READ_ONLY_STRING, Messages.MODEL_PROPERTY_BASE_INSTANCE_COUNT,
                    vertexBuffer.getBaseInstanceCount(), String::valueOf));
            properties.add(new SimpleProperty<>(READ_ONLY_STRING, Messages.MODEL_PROPERTY_INSTANCE_SPAN,
                    vertexBuffer.getInstanceSpan(), String::valueOf));
            properties.add(new SimpleProperty<>(READ_ONLY_STRING, Messages.MODEL_PROPERTY_NUM_COMPONENTS,
                    vertexBuffer.getNumComponents(), String::valueOf));
            properties.add(new SimpleProperty<>(READ_ONLY_STRING, Messages.MODEL_PROPERTY_NUM_ELEMENTS,
                    vertexBuffer.getNumElements(), String::valueOf));
            properties.add(new SimpleProperty<>(READ_ONLY_STRING, Messages.MODEL_PROPERTY_OFFSET,
                    vertexBuffer.getOffset(), String::valueOf));
            properties.add(new SimpleProperty<>(READ_ONLY_STRING, Messages.MODEL_PROPERTY_STRIDE,
                    vertexBuffer.getStride(), String::valueOf));

        } else if (object instanceof Buffer) {

            var buffer = (Buffer) object;

            properties.add(new SimpleProperty<>(READ_ONLY_STRING, Messages.MODEL_PROPERTY_CAPACITY,
                    buffer.capacity(), String::valueOf));
        }

        return properties;
    }

}
