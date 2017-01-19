package com.ss.editor.ui.control.property.builder;

import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.app.state.property.builder.impl.AppStatePropertyBuilder;
import com.ss.editor.ui.control.model.property.builder.impl.AudioNodePropertyBuilder;
import com.ss.editor.ui.control.model.property.builder.impl.GenericPropertyBuilder;
import com.ss.editor.ui.control.model.property.builder.impl.GeometryPropertyBuilder;
import com.ss.editor.ui.control.model.property.builder.impl.LightPropertyBuilder;
import com.ss.editor.ui.control.model.property.builder.impl.ParticleInfluencerPropertyBuilder;
import com.ss.editor.ui.control.model.property.builder.impl.ParticlesPropertyBuilder;
import com.ss.editor.ui.control.model.property.builder.impl.SpatialPropertyBuilder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.layout.VBox;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The builder to build controls to edit properties of objects.
 *
 * @author JavaSaBr
 */
public class PropertyBuilderFactory {

    private static final Array<PropertyBuilder> BUILDERS = ArrayFactory.newArray(PropertyBuilder.class);

    static {
        BUILDERS.add(AudioNodePropertyBuilder.getInstance());
        BUILDERS.add(ParticleInfluencerPropertyBuilder.getInstance());
        BUILDERS.add(ParticlesPropertyBuilder.getInstance());
        BUILDERS.add(GeometryPropertyBuilder.getInstance());
        BUILDERS.add(LightPropertyBuilder.getInstance());
        BUILDERS.add(SpatialPropertyBuilder.getInstance());
        BUILDERS.add(GenericPropertyBuilder.getInstance());
        BUILDERS.add(AppStatePropertyBuilder.getInstance());
    }

    /**
     * Build properties controls for the object to the container.
     *
     * @param object         the object to build property controls.
     * @param parent         the parent of the object.
     * @param container      the container for containing these controls.
     * @param changeConsumer the consumer to work between controls and editor.
     */
    public static void buildFor(@NotNull final Object object, @Nullable final Object parent,
                                @NotNull final VBox container, @NotNull final ChangeConsumer changeConsumer) {

        for (final PropertyBuilder builder : BUILDERS) {
            builder.buildFor(object, parent, container, changeConsumer);
        }
    }
}
