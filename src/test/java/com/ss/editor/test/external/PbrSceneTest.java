package com.ss.editor.test.external;

import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.ss.editor.extension.loader.SceneLoader;

/**
 * The simple test of j3s scenes.
 *
 * @author JavaSaBr
 */
public class PbrSceneTest extends BaseExternalTest {

    public static void main(String[] args) {
        run(PbrSceneTest.class);
    }

    @Override
    public void simpleInitApp() {
        super.simpleInitApp();
        SceneLoader.install(this, postProcessor);
        rootNode.attachChild(assetManager.loadModel("Scene/TestPbrScene.j3s"));
        getFlyByCamera().setMoveSpeed(5);
        getInputManager().addMapping("mouse", new KeyTrigger(KeyInput.KEY_SPACE));
        getInputManager().addListener(new ActionListener() {
            @Override
            public void onAction(final String name, final boolean isPressed, final float tpf) {
                if (isPressed) {
                    getFlyByCamera().setEnabled(!getFlyByCamera().isEnabled());
                }
            }
        }, "mouse");
    }
}
