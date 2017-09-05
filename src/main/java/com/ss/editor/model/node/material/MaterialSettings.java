package com.ss.editor.model.node.material;

import com.jme3.material.Material;
import org.jetbrains.annotations.NotNull;

/**
 * The base class of material settings.
 *
 * @author JavaSaBr
 */
public class MaterialSettings {

    @NotNull
    private final Material material;

    public MaterialSettings(@NotNull final Material material) {
        this.material = material;
    }

    /**
     * @return the material.
     */
    public  @NotNull Material getMaterial() {
        return material;
    }
}
