package com.ss.editor.ui.control.model.property.builder;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.builder.impl.GeometryPropertyBuilder;
import com.ss.editor.ui.control.model.property.builder.impl.LightPropertyBuilder;
import com.ss.editor.ui.control.model.property.builder.impl.ParticlesEmissionPropertyBuilder;
import com.ss.editor.ui.control.model.property.builder.impl.SpatialPropertyBuilder;

import org.jetbrains.annotations.NotNull;

import javafx.scene.layout.VBox;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The builder for building controls of settings for different nodes of a model.
 *
 * @author JavaSaBr
 */
public class PropertyBuilderFactory {

    private static final Array<PropertyBuilder> BUILDERS = ArrayFactory.newArray(PropertyBuilder.class);

    static {
        BUILDERS.add(ParticlesEmissionPropertyBuilder.getInstance());
        BUILDERS.add(GeometryPropertyBuilder.getInstance());
        BUILDERS.add(LightPropertyBuilder.getInstance());
        BUILDERS.add(SpatialPropertyBuilder.getInstance());
    }

    /**
     * Build properties controls for the object to the container.
     *
     * @param object              the object for building property controls.
     * @param container           the container for containing these controls.
     * @param modelChangeConsumer the consumer for working between controls and editor.
     */
    public static void buildFor(@NotNull final Object object, @NotNull final VBox container, @NotNull final ModelChangeConsumer modelChangeConsumer) {
        for (final PropertyBuilder builder : BUILDERS) {
            builder.buildFor(object, container, modelChangeConsumer);
        }
    }
}
