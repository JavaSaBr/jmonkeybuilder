package com.ss.editor.ui.control.property.builder;

import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.app.state.property.builder.impl.AppStatePropertyBuilder;
import com.ss.editor.ui.control.filter.property.builder.impl.FilterPropertyBuilder;
import com.ss.editor.ui.control.model.property.builder.impl.*;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The factory to build property controls for an object.
 *
 * @author JavaSaBr
 */
public class PropertyBuilderRegistry {

    @NotNull
    private static final PropertyBuilderRegistry INSTANCE = new PropertyBuilderRegistry();

    public static @NotNull PropertyBuilderRegistry getInstance() {
        return INSTANCE;
    }

    @NotNull
    private final Array<PropertyBuilder> builders;

    private PropertyBuilderRegistry() {
        builders = ArrayFactory.newArray(PropertyBuilder.class);
        builders.add(AudioNodePropertyBuilder.getInstance());
        builders.add(ParticleEmitterPropertyBuilder.getInstance());
        builders.add(GeometryPropertyBuilder.getInstance());
        builders.add(LightPropertyBuilder.getInstance());
        builders.add(SpatialPropertyBuilder.getInstance());
        builders.add(AppStatePropertyBuilder.getInstance());
        builders.add(FilterPropertyBuilder.getInstance());
        builders.add(DefaultControlPropertyBuilder.getInstance());
        builders.add(EditableControlPropertyBuilder.getInstance());
        builders.add(CollisionShapePropertyBuilder.getInstance());
        builders.add(PrimitivePropertyBuilder.getInstance());
        builders.add(MeshPropertyBuilder.getInstance());
        builders.add(MaterialPropertyBuilder.getInstance());
        builders.add(ParticleInfluencerPropertyBuilder.getInstance());
        builders.add(EmitterShapePropertyBuilder.getInstance());
        builders.add(Toneg0dParticleInfluencerPropertyBuilder.getInstance());
    }
    /**
     * Register a new property builder.
     *
     * @param builder the property builder.
     */
    public void register(@NotNull final PropertyBuilder builder) {
        builders.add(builder);
    }

    /**
     * Build properties controls for the object to the container.
     *
     * @param object         the object to build property controls.
     * @param parent         the parent of the object.
     * @param container      the container for containing these controls.
     * @param changeConsumer the consumer to work between controls and editor.
     */
    public void buildFor(@NotNull final Object object, @Nullable final Object parent, @NotNull final VBox container,
                         @NotNull final ChangeConsumer changeConsumer) {

        for (final PropertyBuilder builder : builders) {
            builder.buildFor(object, parent, container, changeConsumer);
        }
    }
}
