package com.ss.editor.ui.control.model.property.builder.impl;

import com.jme3.math.Vector3f;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.control.Vector3fModelPropertyControl;
import com.ss.editor.ui.control.property.builder.PropertyBuilder;
import com.ss.editor.ui.control.property.builder.impl.AbstractPropertyBuilder;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rlib.ui.util.FXUtils;

/**
 * The implementation of the {@link PropertyBuilder} to build property controls for primitive objects objects.
 *
 * @author JavaSaBr
 */
public class PrimitivePropertyBuilder extends AbstractPropertyBuilder<ModelChangeConsumer> {

    @NotNull
    private static final PropertyBuilder INSTANCE = new PrimitivePropertyBuilder();

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
        }
    }
}
