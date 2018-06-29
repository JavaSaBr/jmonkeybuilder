package com.ss.editor.ui.control.property.builder;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.builder.impl.*;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.plugin.extension.ExtensionPoint;
import com.ss.rlib.common.plugin.extension.ExtensionPointManager;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The factory to build property controls for an object.
 *
 * @author JavaSaBr
 */
public class PropertyBuilderRegistry {

    private static final Logger LOGGER = LoggerManager.getLogger(PropertyBuilderRegistry.class);

    /**
     * @see PropertyBuilder
     */
    public static final String EP_BUILDERS = "PropertyBuilderRegistry#builders";

    /**
     * @see PropertyBuilderFilter
     */
    public static final String EP_FILTERS = "PropertyBuilderRegistry#filters";

    private static final ExtensionPoint<PropertyBuilder> PROPERTY_BUILDERS =
            ExtensionPointManager.register(EP_BUILDERS);

    private static final ExtensionPoint<PropertyBuilderFilter> PROPERTY_BUILDER_FILTERS =
            ExtensionPointManager.register(EP_FILTERS);

    private static final PropertyBuilderRegistry INSTANCE = new PropertyBuilderRegistry();

    @FromAnyThread
    public static @NotNull PropertyBuilderRegistry getInstance() {
        return INSTANCE;
    }

    private PropertyBuilderRegistry() {

        PROPERTY_BUILDERS.register(AudioNodePropertyBuilder.getInstance())
            .register(ParticleEmitterPropertyBuilder.getInstance())
            .register(GeometryPropertyBuilder.getInstance())
            .register(LightPropertyBuilder.getInstance())
            .register(SpatialPropertyBuilder.getInstance())
            .register(SceneAppStatePropertyBuilder.getInstance())
            .register(SceneFilterPropertyBuilder.getInstance())
            .register(DefaultControlPropertyBuilder.getInstance())
            .register(EditableControlPropertyBuilder.getInstance())
            .register(CollisionShapePropertyBuilder.getInstance())
            .register(PrimitivePropertyBuilder.getInstance())
            .register(MeshPropertyBuilder.getInstance())
            .register(MaterialPropertyBuilder.getInstance())
            .register(ParticleInfluencerPropertyBuilder.getInstance())
            .register(EmitterShapePropertyBuilder.getInstance())
            .register(MaterialSettingsPropertyBuilder.getInstance());

        LOGGER.info("initialized.");
    }

    /**
     * Build properties controls for the object to the container.
     *
     * @param object         the object to build property controls.
     * @param parent         the parent of the object.
     * @param container      the container for containing these controls.
     * @param changeConsumer the consumer to work between controls and editor.
     */
    @FxThread
    public void buildFor(
            @NotNull Object object,
            @Nullable Object parent,
            @NotNull VBox container,
            @NotNull ChangeConsumer changeConsumer
    ) {

        var filters = PROPERTY_BUILDER_FILTERS.getExtensions();

        for (var builder : PROPERTY_BUILDERS.getExtensions()) {

            boolean needSkip = false;

            for (var filter : filters) {
                if (filter.skip(builder, object, parent)) {
                    needSkip = true;
                    break;
                }
            }

            if (needSkip) {
                continue;
            }

            builder.buildFor(object, parent, container, changeConsumer);
        }
    }
}
