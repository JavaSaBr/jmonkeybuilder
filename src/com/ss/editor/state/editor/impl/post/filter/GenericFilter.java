package com.ss.editor.state.editor.impl.post.filter;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.post.Filter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;

/**
 * Реализация общего пост фильтра для тестирования эффектов.
 *
 * @author Ronn
 */
public class GenericFilter extends Filter {

    /**
     * Тестируемый материал.
     */
    private final Material material;

    /**
     * Нужена ли фильтру текстура глубины.
     */
    private final boolean requiresDepthTexture;

    /**
     * Нужена ли филбтру текстура сцены.
     */
    private final boolean requiresSceneTexture;

    public GenericFilter(final Material material, boolean requiresDepthTexture, boolean requiresSceneTexture) {
        this.material = material;
        this.requiresDepthTexture = requiresDepthTexture;
        this.requiresSceneTexture = requiresSceneTexture;
    }

    @Override
    protected void initFilter(final AssetManager manager, final RenderManager renderManager, final ViewPort vp, final int w, final int h) {
    }

    @Override
    protected Material getMaterial() {
        return material;
    }

    @Override
    protected boolean isRequiresDepthTexture() {
        return requiresDepthTexture;
    }

    @Override
    protected boolean isRequiresSceneTexture() {
        return requiresSceneTexture;
    }
}
