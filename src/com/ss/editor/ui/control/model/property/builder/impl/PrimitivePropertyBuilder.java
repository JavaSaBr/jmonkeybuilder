package com.ss.editor.ui.control.model.property.builder.impl;

import com.jme3.math.Vector3f;
import com.jme3.scene.VertexBuffer;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.control.DefaultModelSinglePropertyControl;
import com.ss.editor.ui.control.model.property.control.Vector3fModelPropertyControl;
import com.ss.editor.ui.control.property.builder.PropertyBuilder;
import com.ss.editor.ui.control.property.builder.impl.AbstractPropertyBuilder;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.Buffer;

/**
 * The implementation of the {@link PropertyBuilder} to build property controls for primitive objects objects.
 *
 * @author JavaSaBr
 */
public class PrimitivePropertyBuilder extends AbstractPropertyBuilder<ModelChangeConsumer> {

    @NotNull
    private static final PropertyBuilder INSTANCE = new PrimitivePropertyBuilder();

    /**
     * Gets instance.
     *
     * @return the instance
     */
    @NotNull
    public static PropertyBuilder getInstance() {
        return INSTANCE;
    }

    private PrimitivePropertyBuilder() {
        super(ModelChangeConsumer.class);
    }

    @Override
    protected void buildForImpl(@NotNull final Object object, @Nullable final Object parent,
                                @NotNull final VBox container, @NotNull final ModelChangeConsumer changeConsumer) {

        if (object instanceof Vector3f) {

            final Vector3f position = (Vector3f) object;
            final Vector3f value = position.clone();

            final Vector3fModelPropertyControl<Vector3f> control =
                    new Vector3fModelPropertyControl<>(value, Messages.MODEL_PROPERTY_VALUE, changeConsumer);
            control.setApplyHandler(Vector3f::set);
            control.setSyncHandler(Vector3f::clone);
            control.setEditObject(position);

            FXUtils.addToPane(control, container);

        } else if (object instanceof VertexBuffer) {

            final VertexBuffer vertexBuffer = (VertexBuffer) object;
            final Buffer data = vertexBuffer.getData();
            if (data == null) return;

            final VertexBuffer.Type bufferType = vertexBuffer.getBufferType();
            final VertexBuffer.Format format = vertexBuffer.getFormat();
            final VertexBuffer.Usage usage = vertexBuffer.getUsage();

            final long uniqueId = vertexBuffer.getUniqueId();

            final int baseInstanceCount = vertexBuffer.getBaseInstanceCount();
            final int instanceSpan = vertexBuffer.getInstanceSpan();
            final int numComponents = vertexBuffer.getNumComponents();
            final int numElements = vertexBuffer.getNumElements();
            final int offset = vertexBuffer.getOffset();
            final int stride = vertexBuffer.getStride();

            final DefaultModelSinglePropertyControl<VertexBuffer, VertexBuffer.Type> bufferTypeControl =
                    new DefaultModelSinglePropertyControl<>(bufferType, Messages.MODEL_PROPERTY_TYPE, changeConsumer);

            bufferTypeControl.setSyncHandler(VertexBuffer::getBufferType);
            bufferTypeControl.setToStringFunction(Enum::name);
            bufferTypeControl.setEditObject(vertexBuffer);

            final DefaultModelSinglePropertyControl<VertexBuffer, VertexBuffer.Format> formatControl =
                    new DefaultModelSinglePropertyControl<>(format, Messages.MODEL_PROPERTY_FORMAT, changeConsumer);

            formatControl.setSyncHandler(VertexBuffer::getFormat);
            formatControl.setToStringFunction(Enum::name);
            formatControl.setEditObject(vertexBuffer);

            final DefaultModelSinglePropertyControl<VertexBuffer, VertexBuffer.Usage> usageControl =
                    new DefaultModelSinglePropertyControl<>(usage, Messages.MODEL_PROPERTY_USAGE, changeConsumer);

            usageControl.setSyncHandler(VertexBuffer::getUsage);
            usageControl.setToStringFunction(Enum::name);
            usageControl.setEditObject(vertexBuffer);

            final DefaultModelSinglePropertyControl<VertexBuffer, Long> uniqIdControl =
                    new DefaultModelSinglePropertyControl<>(uniqueId, Messages.MODEL_PROPERTY_UNIQ_ID, changeConsumer);

            uniqIdControl.setSyncHandler(VertexBuffer::getUniqueId);
            uniqIdControl.setToStringFunction(value -> Long.toString(value));
            uniqIdControl.setEditObject(vertexBuffer);

            final DefaultModelSinglePropertyControl<VertexBuffer, Integer> baseInstanceCountControl =
                    new DefaultModelSinglePropertyControl<>(baseInstanceCount, Messages.MODEL_PROPERTY_BASE_INSTANCE_COUNT, changeConsumer);

            baseInstanceCountControl.setSyncHandler(VertexBuffer::getBaseInstanceCount);
            baseInstanceCountControl.setToStringFunction(value -> Integer.toString(value));
            baseInstanceCountControl.setEditObject(vertexBuffer);

            final DefaultModelSinglePropertyControl<VertexBuffer, Integer> instanceSpanControl =
                    new DefaultModelSinglePropertyControl<>(instanceSpan, Messages.MODEL_PROPERTY_INSTANCE_SPAN, changeConsumer);

            instanceSpanControl.setSyncHandler(VertexBuffer::getBaseInstanceCount);
            instanceSpanControl.setToStringFunction(value -> Integer.toString(value));
            instanceSpanControl.setEditObject(vertexBuffer);

            final DefaultModelSinglePropertyControl<VertexBuffer, Integer> numComponentsControl =
                    new DefaultModelSinglePropertyControl<>(numComponents, Messages.MODEL_PROPERTY_NUM_COMPONENTS, changeConsumer);

            numComponentsControl.setSyncHandler(VertexBuffer::getNumComponents);
            numComponentsControl.setToStringFunction(value -> Integer.toString(value));
            numComponentsControl.setEditObject(vertexBuffer);

            final DefaultModelSinglePropertyControl<VertexBuffer, Integer> numElementsControl =
                    new DefaultModelSinglePropertyControl<>(numElements, Messages.MODEL_PROPERTY_NUM_ELEMENTS, changeConsumer);

            numElementsControl.setSyncHandler(VertexBuffer::getNumElements);
            numElementsControl.setToStringFunction(value -> Integer.toString(value));
            numElementsControl.setEditObject(vertexBuffer);

            final DefaultModelSinglePropertyControl<VertexBuffer, Integer> offsetControl =
                    new DefaultModelSinglePropertyControl<>(offset, Messages.MODEL_PROPERTY_OFFSET, changeConsumer);

            offsetControl.setSyncHandler(VertexBuffer::getOffset);
            offsetControl.setToStringFunction(value -> Integer.toString(value));
            offsetControl.setEditObject(vertexBuffer);

            final DefaultModelSinglePropertyControl<VertexBuffer, Integer> strideControl =
                    new DefaultModelSinglePropertyControl<>(stride, Messages.MODEL_PROPERTY_STRIDE, changeConsumer);

            strideControl.setSyncHandler(VertexBuffer::getStride);
            strideControl.setToStringFunction(value -> Integer.toString(value));
            strideControl.setEditObject(vertexBuffer);

            FXUtils.addToPane(bufferTypeControl, container);
            FXUtils.addToPane(formatControl, container);
            FXUtils.addToPane(usageControl, container);
            FXUtils.addToPane(uniqIdControl, container);
            FXUtils.addToPane(baseInstanceCountControl, container);
            FXUtils.addToPane(instanceSpanControl, container);
            FXUtils.addToPane(numComponentsControl, container);
            FXUtils.addToPane(numElementsControl, container);
            FXUtils.addToPane(offsetControl, container);
            FXUtils.addToPane(strideControl, container);

        } else if (object instanceof Buffer) {

            final Buffer buffer = (Buffer) object;
            final int capacity = buffer.capacity();

            final DefaultModelSinglePropertyControl<Buffer, Integer> capacityControl =
                    new DefaultModelSinglePropertyControl<>(capacity, Messages.MODEL_PROPERTY_CAPACITY, changeConsumer);

            capacityControl.setSyncHandler(Buffer::capacity);
            capacityControl.setToStringFunction(integer -> Integer.toString(integer));
            capacityControl.setEditObject(buffer);

            FXUtils.addToPane(capacityControl, container);
        }
    }
}
