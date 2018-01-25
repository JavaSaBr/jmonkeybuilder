package com.ss.editor.ui.component.painting;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.component.painting.terrain.TerrainEditingComponent;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * The registry of all painting components.
 *
 * @author JavaSaBr
 */
public class PaintingComponentRegistry {

    private static final PaintingComponentRegistry INSTANCE = new PaintingComponentRegistry();

    @FromAnyThread
    public static @NotNull PaintingComponentRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * The list of painting component's constructors.
     */
    @NotNull
    private final Array<Function<PaintingComponentContainer, PaintingComponent>> constructors;

    private PaintingComponentRegistry() {
        this.constructors = ArrayFactory.newArray(Function.class);
        register(TerrainEditingComponent::new);
    }

    /**
     * Register the new painting component's constructor.
     *
     * @param constructor the new painting component's constructor.
     */
    @FxThread
    public void register(@NotNull final Function<PaintingComponentContainer, PaintingComponent> constructor) {
        this.constructors.add(constructor);
    }

    /**
     * Create all available painting components.
     *
     * @param container painting component's container.
     * @return all available painting components.
     */
    @FxThread
    public @NotNull Array<PaintingComponent> createComponents(@NotNull final PaintingComponentContainer container) {
        final Array<PaintingComponent> result = ArrayFactory.newArray(PaintingComponent.class);
        constructors.forEach(result, container, (constructor, components, cont) -> components.add(constructor.apply(cont)));
        return result;
    }
}
