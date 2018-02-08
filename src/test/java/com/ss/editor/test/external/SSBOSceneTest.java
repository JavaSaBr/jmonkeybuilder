package com.ss.editor.test.external;

import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.shader.ShaderStorageBufferObject;
import com.jme3.shader.VarType;
import com.jme3.util.BufferUtils;

import java.nio.ByteBuffer;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The simple test of j3s scenes.
 *
 * @author JavaSaBr
 */
public class SSBOSceneTest extends BaseExternalTest {

    public static void main(String[] args) {
        run(SSBOSceneTest.class);
    }

    private ShaderStorageBufferObject ssbo;

    @Override
    public void simpleInitApp() {
        super.simpleInitApp();

        getFlyByCamera().setEnabled(false);
        getInputManager().setCursorVisible(true);

        final Box box = new Box(1, 1, 1);
        final Geometry geometry = new Geometry("Geom", box);

        final ByteBuffer data = BufferUtils.createByteBuffer(32);
        data.putFloat(0).putFloat(0)
                .putFloat(0)
                .putFloat(0)
                .putFloat(1)
                .putFloat(0)
                .putFloat(0)
                .putFloat(1);

        ssbo = new ShaderStorageBufferObject();
        ssbo.setBinding(2);
        ssbo.setupData(data);

        final Material material = new Material(assetManager, "MatDefs/Unshaded.j3md");
        material.setParam("TestSSBO", VarType.ShaderStorageBufferObject, ssbo);
        material.setColor("Color", ColorRGBA.Blue);

        geometry.setMaterial(material);

        rootNode.attachChild(geometry);

        getFlyByCamera().setMoveSpeed(5);
        getInputManager().addMapping("mouse", new KeyTrigger(KeyInput.KEY_SPACE));
        getInputManager().addListener((ActionListener) (name, isPressed, tpf) -> {
            if (isPressed) {
                getFlyByCamera().setEnabled(!getFlyByCamera().isEnabled());
            }
        }, "mouse");
    }

    @Override
    public void simpleUpdate(final float tpf) {
        super.simpleUpdate(tpf);

        final ThreadLocalRandom random = ThreadLocalRandom.current();

        final ByteBuffer data = ssbo.getData();
        data.clear();
        data.putFloat(0).putFloat(0)
                .putFloat(0)
                .putFloat(0)
                .putFloat(random.nextFloat())
                .putFloat(random.nextFloat())
                .putFloat(random.nextFloat())
                .putFloat(1);

        ssbo.updateData(data);
    }
}
