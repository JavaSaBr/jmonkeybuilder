package com.ss.editor.ui.component.painting;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.component.painting.spawn.SpawnPaintingComponent;
import com.ss.editor.ui.component.painting.terrain.TerrainPaintingComponent;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayCollectors;
import com.ss.rlib.common.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * The registry of all painting components.
 *
 * @author JavaSaBr
 */
public class PaintingComponentRegistry {

    private static final PaintingComponentRegistry INSTANCE = new PaintingComponentRegistry();

    interface Constructor extends Function<PaintingComponentContainer, PaintingComponent> {

        @Override
        @NotNull PaintingComponent apply(@NotNull PaintingComponentContainer container);
    }

    @FromAnyThread
    public static @NotNull PaintingComponentRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * The list of painting component's constructors.
     */
    @NotNull
    private final Array<Constructor> constructors;

    private PaintingComponentRegistry() {
        this.constructors = ArrayFactory.newCopyOnModifyArray(Constructor.class);
        register(TerrainPaintingComponent::new);
        register(SpawnPaintingComponent::new);
    }

    /**
     * Register the new painting component's constructor.
     *
     * @param constructor the new painting component's constructor.
     */
    @FxThread
    public void register(@NotNull Constructor constructor) {
        constructors.add(constructor);
    }

    /**
     * Create all available painting components.
     *
     * @param container painting component's container.
     * @return all available painting components.
     */
    @FxThread
    public @NotNull Array<PaintingComponent> createComponents(@NotNull PaintingComponentContainer container) {
        return constructors.stream()
                .map(constructor -> constructor.apply(container))
                .collect(ArrayCollectors.toArray(PaintingComponent.class));
    }
}
