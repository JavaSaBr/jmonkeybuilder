package com.ss.editor.test.external;

import com.jme3.app.SimpleApplication;
import com.jme3.material.TechniqueDef;
import com.jme3.math.ColorRGBA;
import com.jme3.post.FilterPostProcessor;
import com.jme3.system.AppSettings;
import com.ss.rlib.util.ClassUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The base external test.
 *
 * @author JavaSaBr
 */
public class BaseExternalTest extends SimpleApplication {

    protected static @NotNull AppSettings getAppSettings() {
        final AppSettings settings = new AppSettings(true);
        settings.setHeight(768);
        settings.setWidth(1024);
        settings.setGammaCorrection(true);
        settings.setRenderer(AppSettings.LWJGL_OPENGL33);
        return settings;
    }

    protected static void run(@NotNull Class<? extends BaseExternalTest> type) {
        final BaseExternalTest test = ClassUtils.newInstance(type);
        test.setSettings(getAppSettings());
        test.setShowSettings(false);
        test.start();
    }

    /**
     * The post filter processor.
     */
    @Nullable
    protected FilterPostProcessor postProcessor;

    @Override
    public void simpleInitApp() {
        renderManager.setPreferredLightMode(TechniqueDef.LightMode.SinglePass);
        renderManager.setSinglePassLightBatchSize(15);

        postProcessor = new FilterPostProcessor(assetManager);
        postProcessor.initialize(renderManager, viewPort);
        viewPort.addProcessor(postProcessor);

        flyCam.setDragToRotate(false);

        viewPort.setBackgroundColor(new ColorRGBA(50 / 255F, 50 / 255F, 50 / 255F, 1F));
        cam.setFrustumPerspective(55, (float) cam.getWidth() / cam.getHeight(), 1f, Integer.MAX_VALUE);
    }
}
