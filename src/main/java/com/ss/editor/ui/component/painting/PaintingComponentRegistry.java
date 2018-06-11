package com.ss.editor.ui.component.painting;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.component.painting.spawn.SpawnPaintingComponent;
import com.ss.editor.ui.component.painting.terrain.TerrainPaintingComponent;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayCollectors;
import com.ss.rlib.common.util.array.ArrayFactory;
import com.ss.rlib.common.util.array.ConcurrentArray;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
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
    private final ConcurrentArray<Constructor> constructors;

    private PaintingComponentRegistry() {
        this.constructors = ConcurrentArray.of(Constructor.class);
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
        constructors.runInWriteLock(constructor, Collection::add);
    }

    /**
     * Create all available painting components.
     *
     * @param container painting component's container.
     * @return all available painting components.
     */
    @FxThread
    public @NotNull Array<PaintingComponent> createComponents(@NotNull PaintingComponentContainer container) {
        var stamp = constructors.readLock();
        try {

            return constructors.stream()
                .map(constructor -> constructor.apply(container))
                .collect(ArrayCollectors.toArray(PaintingComponent.class));

        } finally {
            constructors.readUnlock(stamp);
        }
    }
}
