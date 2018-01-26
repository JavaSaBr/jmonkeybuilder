package com.ss.editor.control.transform;

import static com.ss.rlib.util.ObjectUtils.notNull;
import static java.lang.Math.abs;
import com.jme3.input.InputManager;
import com.jme3.math.Transform;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.config.Config;
import com.ss.editor.control.transform.EditorTransformSupport.TransformationMode;
import com.ss.editor.util.EditorUtil;
import com.ss.editor.util.LocalObjects;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the scaling control.
 *
 * @author JavaSaBr
 */
public class ScaleToolControl extends AbstractTransformControl {

    @NotNull
    private static final String NODE_SCALE_X = "scale_x";

    @NotNull
    private static final String NODE_SCALE_Y = "scale_y";

    @NotNull
    private static final String NODE_SCALE_Z = "scale_z";

    /**
     * Instantiates a new Scale tool control.
     *
     * @param editorControl the editor control
     */
    public ScaleToolControl(@NotNull final EditorTransformSupport editorControl) {
        super(editorControl);
    }

    @NotNull
    @Override
    protected String getNodeY() {
        return NODE_SCALE_Y;
    }

    @NotNull
    @Override
    protected String getNodeX() {
        return NODE_SCALE_X;
    }

    @NotNull
    @Override
    protected String getNodeZ() {
        return NODE_SCALE_Z;
    }

    @Override
    public void processTransform() {

        final EditorTransformSupport editorControl = getEditorControl();

        final LocalObjects local = LocalObjects.get();
        final Camera camera = editorControl.getCamera();
        final InputManager inputManager = EditorUtil.getInputManager();
        final Transform transform = notNull(editorControl.getTransformCenter());

        // cursor position and selected position vectors
        final Vector2f cursorPos = inputManager.getCursorPosition();
        final Vector3f transformOnScreen = camera.getScreenCoordinates(transform.getTranslation(), local.nextVector());
        final Vector2f selectedCoords = local.nextVector(transformOnScreen.getX(), transformOnScreen.getY());

        // set new deltaVector if it's not set (scale tool stores position of a cursor)
        if (Float.isNaN(editorControl.getTransformDeltaX())) {
            editorControl.setTransformDeltaX(cursorPos.getX());
            editorControl.setTransformDeltaY(cursorPos.getY());
        }

        final Node parentNode = getParentNode();
        final Node childNode = getChildNode();

        // Picked vector
        final Spatial toTransform = notNull(editorControl.getToTransform());
        final TransformationMode transformationMode = editorControl.getTransformationMode();
        transformationMode.prepareToScale(parentNode, childNode, transform, camera);

        // scale according to distance
        final Vector3f deltaVector = local.nextVector(editorControl.getTransformDeltaX(), editorControl.getTransformDeltaY(), 0F);
        final Vector2f delta2d = local.nextVector(deltaVector.getX(), deltaVector.getY());
        final Vector3f baseScale = local.nextVector(transform.getScale()); // default scale
        final Vector3f pickedVector = local.nextVector(transformationMode.getScaleAxis(transform, editorControl.getPickedAxis(), camera));
        pickedVector.setX(abs(pickedVector.getX()));
        pickedVector.setY(abs(pickedVector.getY()));
        pickedVector.setZ(abs(pickedVector.getZ()));

        if (Config.DEV_TRANSFORMS_DEBUG) {
            System.out.println("Base scale " + baseScale + ", pickedVector " + pickedVector);
        }

        // scale object
        float disCursor = cursorPos.distance(selectedCoords);
        float disDelta = delta2d.distance(selectedCoords);
        float scaleValue = (float) (cursorPos.distance(delta2d) * 0.01f * Math.sqrt(baseScale.length()));

        if (disCursor > disDelta) {
            baseScale.addLocal(pickedVector.mult(scaleValue, local.nextVector()));
        } else {
            scaleValue = Math.min(scaleValue, 0.999f); // remove negateve values
            baseScale.subtractLocal(pickedVector.mult(scaleValue, local.nextVector()));
        }

        parentNode.setLocalScale(baseScale);

        if (Config.DEV_TRANSFORMS_DEBUG) {
            System.out.println("New scale " + baseScale + ", result world " + childNode.getWorldScale());
        }

        parentNode.setLocalScale(baseScale);
        toTransform.setLocalScale(childNode.getWorldScale());

        editorControl.notifyTransformed(toTransform);
    }


    @Override
    protected void controlUpdate(final float tpf) {
    }

    @Override
    protected void controlRender(@NotNull final RenderManager renderManager, @NotNull final ViewPort viewPort) {
    }
}
