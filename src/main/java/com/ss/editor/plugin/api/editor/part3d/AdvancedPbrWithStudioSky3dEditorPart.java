package com.ss.editor.plugin.api.editor.part3d;

import com.jme3.scene.Geometry;
import com.jme3.util.SkyFactory;
import com.ss.editor.plugin.api.editor.Advanced3dFileEditor;
import com.ss.editor.util.EditorUtil;
import org.jetbrains.annotations.NotNull;

/**
 * The advanced implementation of 3D part of an editor with PBR Light probe and Studio Sky.
 *
 * @author JavaSaBr
 */
public class AdvancedPbrWithStudioSky3dEditorPart<T extends Advanced3dFileEditor> extends AdvancedPbr3dEditorPart<T> {

    public AdvancedPbrWithStudioSky3dEditorPart(@NotNull T fileEditor) {
        super(fileEditor);

        var assetManager = EditorUtil.getAssetManager();
        var sky = (Geometry) SkyFactory.createSky(assetManager, "graphics/textures/sky/studio.hdr",
                SkyFactory.EnvMapType.EquirectMap);

        stateNode.attachChild(sky);
    }
}