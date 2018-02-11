package com.ss.editor.test.external;

import static com.jme3.shader.BufferObjectField.field;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.shader.BufferObject;
import com.jme3.shader.VarType;

import java.util.concurrent.ThreadLocalRandom;

/**
 * The simple test of j3s scenes.
 *
 * @author JavaSaBr
 */
public class BOSceneTest extends BaseExternalTest {

    public static void main(String[] args) {
        run(BOSceneTest.class);
    }

    private BufferObject ssbo;

    private BufferObject ubo;

    @Override
    public void simpleInitApp() {
        super.simpleInitApp();

        getFlyByCamera().setEnabled(false);
        getInputManager().setCursorVisible(true);

        final Box box = new Box(1, 1, 1);

        Geometry geometry = new Geometry("Geom", box);

        ssbo = new BufferObject(3,
                field("light_1", VarType.Vector4),
                field("light_2", VarType.Vector4)
        );
        ssbo.setValue("light_1", ColorRGBA.Red);
        ssbo.setValue("light_2", ColorRGBA.Green);

        ubo = new BufferObject(2,
                field("light_1", VarType.Vector4),
                field("light_2", VarType.Vector4)
        );
        ubo.setValue("light_1", ColorRGBA.Yellow);
        ubo.setValue("light_2", ColorRGBA.Red);

        final Material materialSSBO = new Material(assetManager, "MatDefs/UnshadedSSBO.j3md");
        materialSSBO.setShaderStorageBufferObject("TestSSBO", ssbo);
        materialSSBO.setColor("Color", ColorRGBA.DarkGray);

        geometry.setMaterial(materialSSBO);
        geometry.setLocalTranslation(1, 0, 0);

        rootNode.attachChild(geometry);

        final Material materialUBO = new Material(assetManager, "MatDefs/UnshadedUBO.j3md");
        materialUBO.setUniformBufferObject("TestUBO", ubo);
        materialUBO.setColor("Color", ColorRGBA.DarkGray);

        geometry = geometry.clone();
        geometry.setMaterial(materialUBO);
        geometry.setLocalTranslation(-1, 0, 0);

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

        ColorRGBA randomColor = new ColorRGBA(random.nextFloat(),
                random.nextFloat(),
                random.nextFloat(),
                1F);

        ssbo.setValue("light_2", randomColor);

        randomColor = new ColorRGBA(random.nextFloat(),
                random.nextFloat(),
                random.nextFloat(),
                1F);

        ubo.setValue("light_2", randomColor);
    }
}
