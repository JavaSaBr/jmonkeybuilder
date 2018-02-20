package com.ss.editor.model.node.material;

import com.jme3.material.Material;
import com.ss.editor.annotation.FromAnyThread;
import org.jetbrains.annotations.NotNull;

/**
 * The base class of material settings.
 *
 * @author JavaSaBr
 */
public class MaterialSettings {

    /**
     * The material.
     */
    @NotNull
    private final Material material;

    public MaterialSettings(@NotNull final Material material) {
        this.material = material;
    }

    /**
     * Get the material.
     *
     * @return the material.
     */
    @FromAnyThread
    public @NotNull Material getMaterial() {
        return material;
    }
}
