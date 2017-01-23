package com.ss.extension.scene.renderer;

import com.jme3.asset.AssetManager;
import com.jme3.renderer.Camera;
import com.jme3.shadow.AbstractShadowRenderer;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.util.clone.Cloner;
import com.jme3.util.clone.JmeCloneable;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * The editable implementation of {@link DirectionalLightShadowRenderer}.
 *
 * @author JavaSaBr
 */
public class EditableDirectionalLightShadowRenderer extends DirectionalLightShadowRenderer implements Cloneable, JmeCloneable {

    private static final Method INIT_METHOD;

    static {
        try {
            INIT_METHOD = AbstractShadowRenderer.class.getDeclaredMethod("init", AssetManager.class, int.class, int.class);
            INIT_METHOD.setAccessible(true);
        } catch (final NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public EditableDirectionalLightShadowRenderer() {
        displayDebug();
    }

    public EditableDirectionalLightShadowRenderer(final AssetManager assetManager, final int shadowMapSize, final int nbSplits) {
        super(assetManager, shadowMapSize, nbSplits);
        displayDebug();
    }

    @Override
    protected void updateShadowCams(@NotNull final Camera viewCam) {
        if (getLight() == null) return;
        super.updateShadowCams(viewCam);
    }

    @Override
    public Object jmeClone() {
        try {
            return super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public void cloneFields(@NotNull final Cloner cloner, @NotNull final Object original) {
        try {
            INIT_METHOD.invoke(this, assetManager, nbShadowMaps, (int) shadowMapSize);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
