package com.ss.editor.ui.component.painting;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.component.painting.spawn.SpawnPaintingComponent;
import com.ss.editor.ui.component.painting.terrain.TerrainPaintingComponent;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.plugin.extension.ExtensionPoint;
import com.ss.rlib.common.plugin.extension.ExtensionPointManager;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayCollectors;
import org.jetbrains.annotations.NotNull;

/**
 * The registry of all painting components.
 *
 * @author JavaSaBr
 */
public class PaintingComponentRegistry {

    private static final Logger LOGGER = LoggerManager.getLogger(PaintingComponentRegistry.class);

    /**
     * @see ComponentConstructor
     */
    public static final String EP_CONSTRUCTORS = "PaintingComponentRegistry#constructors";

    private static final ExtensionPoint<ComponentConstructor> CONSTRUCTORS =
            ExtensionPointManager.register(EP_CONSTRUCTORS);

    private static final PaintingComponentRegistry INSTANCE = new PaintingComponentRegistry();

    @FromAnyThread
    public static @NotNull PaintingComponentRegistry getInstance() {
        return INSTANCE;
    }

    private PaintingComponentRegistry() {

        CONSTRUCTORS.register(TerrainPaintingComponent::new)
                .register(SpawnPaintingComponent::new);

        LOGGER.info("initialized.");
    }


    /**
     * Create all available painting components.
     *
     * @param container painting component's container.
     * @return all available painting components.
     */
    @FxThread
    public @NotNull Array<PaintingComponent> createComponents(@NotNull PaintingComponentContainer container) {
        return CONSTRUCTORS.getExtensions()
                .stream()
                .map(constructor -> constructor.create(container))
                .collect(ArrayCollectors.toArray(PaintingComponent.class));
    }
}
