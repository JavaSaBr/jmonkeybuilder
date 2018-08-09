package com.ss.editor.util;

import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.controls.Trigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import org.jetbrains.annotations.NotNull;

/**
 * The class with utility methods for the jME things.
 *
 * @author JavaSaBr
 */
public class JmeUtils {

    /**
     * Add mapping to the input manager.
     *
     * @param inputManager the input manager.
     * @param name         the mapping name.
     * @param trigger      the trigger.
     */
    @JmeThread
    public static void addMapping(@NotNull InputManager inputManager, @NotNull String name, @NotNull Trigger trigger) {
        if (!inputManager.hasMapping(name)) {
            inputManager.addMapping(name, trigger);
        }
    }

    /**
     * Add mapping to the input manager.
     *
     * @param inputManager the input manager.
     * @param name         the mapping name.
     * @param triggers     the triggers.
     */
    @JmeThread
    public static void addMapping(
            @NotNull InputManager inputManager,
            @NotNull String name,
            @NotNull Trigger... triggers
    ) {
        if (!inputManager.hasMapping(name)) {
            inputManager.addMapping(name, triggers);
        }
    }

    /**
     * Create a material with the color.
     */
    @FromAnyThread
    public static @NotNull Material coloredWireframeMaterial(
            @NotNull ColorRGBA color,
            @NotNull AssetManager assetManagers
    ) {

        var material = new Material(assetManagers, "Common/MatDefs/Misc/Unshaded.j3md");
        material.getAdditionalRenderState().setWireframe(true);
        material.setColor("Color", color);

        return material;
    }

    /**
     * Get position os the location on the camera.
     *
     * @param location the location.
     * @param camera the camera.
     * @return the position on the camera.
     */
    @JmeThread
    public static @NotNull Vector3f getPositionOnCamera(@NotNull Vector3f location, @NotNull Camera camera) {

        var local = LocalObjects.get();

        var cameraLocation = camera.getLocation();
        var resultPosition = location.subtract(cameraLocation, local.nextVector())
                .normalizeLocal()
                .multLocal(camera.getFrustumNear() + 0.5f);

        return cameraLocation.add(resultPosition, local.nextVector());
    }
}
