package com.ss.editor.test.external;

import com.ss.editor.extension.loader.SceneLoader;

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
    }
}
