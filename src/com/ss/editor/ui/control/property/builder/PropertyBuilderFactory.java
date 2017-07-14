package com.ss.editor.ui.control.property.builder;

import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.app.state.property.builder.impl.AppStatePropertyBuilder;
import com.ss.editor.ui.control.filter.property.builder.impl.FilterPropertyBuilder;
import com.ss.editor.ui.control.model.property.builder.impl.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.layout.VBox;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;

/**
 * The factory to build property controls to edit an object.
 *
 * @author JavaSaBr
 */
public class PropertyBuilderFactory {

    @NotNull
    private static final Array<PropertyBuilder> BUILDERS = ArrayFactory.newArray(PropertyBuilder.class);

    static {
        BUILDERS.add(AudioNodePropertyBuilder.getInstance());
        BUILDERS.add(Toneg0dParticleEmitterPropertyBuilder.getInstance());
        BUILDERS.add(GeometryPropertyBuilder.getInstance());
        BUILDERS.add(LightPropertyBuilder.getInstance());
        BUILDERS.add(SpatialPropertyBuilder.getInstance());
        BUILDERS.add(AppStatePropertyBuilder.getInstance());
        BUILDERS.add(FilterPropertyBuilder.getInstance());
        BUILDERS.add(DefaultControlPropertyBuilder.getInstance());
        BUILDERS.add(EditableControlPropertyBuilder.getInstance());
        BUILDERS.add(CollisionShapePropertyBuilder.getInstance());
        BUILDERS.add(PrimitivePropertyBuilder.getInstance());
        BUILDERS.add(MeshPropertyBuilder.getInstance());
        BUILDERS.add(ParticleInfluencerPropertyBuilder.getInstance());
        BUILDERS.add(EmitterShapePropertyBuilder.getInstance());
        BUILDERS.add(Toneg0dParticleInfluencerPropertyBuilder.getInstance());
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
