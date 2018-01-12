package com.ss.editor.plugin.api.editor.part3d;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.util.SkyFactory;
import com.ss.editor.plugin.api.editor.Advanced3DFileEditor;
import org.jetbrains.annotations.NotNull;

/**
 * The advanced implementation of 3D part of an editor with PBR Light probe and Studio Sky.
 *
 * @author JavaSaBr
 */
public class AdvancedPBRWithStudioSky3DEditorState<T extends Advanced3DFileEditor> extends AdvancedPBR3DEditorState<T> {

    public AdvancedPBRWithStudioSky3DEditorState(@NotNull final T fileEditor) {
        super(fileEditor);

        final AssetManager assetManager = JME_APPLICATION.getAssetManager();

        final Geometry sky = (Geometry) SkyFactory.createSky(assetManager, "graphics/textures/sky/studio.hdr",
                SkyFactory.EnvMapType.EquirectMap);

        final Node stateNode = getStateNode();
        stateNode.attachChild(sky);
    }
}
