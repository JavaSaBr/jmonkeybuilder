package com.ss.editor.test.external;

import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.*;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

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

    /*private BufferObject ssbo;
    private BufferObject ssbo2;
    private BufferObject ubo;
    @Override
    public void simpleInitApp() {
        super.simpleInitApp();

        getFlyByCamera().setEnabled(false);
        getInputManager().setCursorVisible(true);

        final Box box = new Box(1, 1, 1);

        Geometry geometry = new Geometry("Geom", box);

        ssbo = new BufferObject(3);
        ssbo.setFieldValue("light_1", ColorRGBA.Red);
        ssbo.setFieldValue("light_2", ColorRGBA.Green);

        ubo = new BufferObject(2);
        ubo.setFieldValue("light_1", ColorRGBA.Yellow);
        ubo.setFieldValue("light_2", ColorRGBA.Red);

        final Matrix3f matrix3f = new Matrix3f();
        matrix3f.setColumn(0, new Vector3f(0, 0, 1));
        matrix3f.setColumn(1, new Vector3f(0, 1, 0));
        matrix3f.setColumn(2, new Vector3f(0, 1, 0));

        final Matrix4f matrix4f = new Matrix4f();
        matrix4f.setColumn(1, new float[]{0, 1, 0, 0});

        ssbo2 = new BufferObject(4);
        ssbo2.setFieldValue("index", 1);
        ssbo2.setFieldValue("colors", new ColorRGBA[]{ColorRGBA.Green, ColorRGBA.Red, ColorRGBA.Blue});
        ssbo2.setFieldValue("alp", 1F);
        ssbo2.setFieldValue("matrix3", matrix3f);
        ssbo2.setFieldValue("matrix4", matrix4f);
        ssbo2.setFieldValue("positions", new Vector3f[]{Vector3f.UNIT_X, new Vector3f(0.3F, 0.3f, 0.3F), Vector3f.UNIT_Z});
        ssbo2.setFieldValue("index2", 1);
        ssbo2.setFieldValue("matrixes", new Matrix3f[] {new Matrix3f(), matrix3f});
        ssbo2.setFieldValue("vector2", new Vector2f(0, 0));
        ssbo2.setFieldValue("fvalue", 0.4F);

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

        final Material materialSSBO2 = new Material(assetManager, "MatDefs/UnshadedSSBO2.j3md");
        materialSSBO2.setShaderStorageBufferObject("TestSSBO", ssbo2);
        materialSSBO2.setColor("Color", ColorRGBA.DarkGray);

        geometry = geometry.clone();
        geometry.setMaterial(materialSSBO2);
        geometry.setLocalTranslation(-3, 0, 0);

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

        ssbo.setFieldValue("light_2", randomColor);

        randomColor = new ColorRGBA(random.nextFloat(),
                random.nextFloat(),
                random.nextFloat(),
                1F);

        ubo.setFieldValue("light_2", randomColor);
    }
*/
}
