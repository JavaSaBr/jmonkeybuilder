package com.ss.extension.scene.filter.impl;

import org.jetbrains.annotations.NotNull;

/**
 * The editable implementation of scene bloom filter.
 *
 * @author JavaSaBr
 */
public class EditableSceneBloomFilter extends EditableBloomFilter {

    public EditableSceneBloomFilter() {
        super(GlowMode.Scene);
    }

    @NotNull
    @Override
    public String getName() {
        return "Scene bloom filter";
    }
}
