package com.ss.editor.control.transform;

import static com.ss.rlib.util.ObjectUtils.notNull;
import static java.lang.Math.abs;
import com.jme3.input.InputManager;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.ss.editor.Editor;
import com.ss.editor.config.Config;
import com.ss.editor.control.transform.EditorTransformSupport.PickedAxis;
import com.ss.editor.control.transform.EditorTransformSupport.TransformationMode;
import com.ss.editor.util.GeomUtils;
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

    @NotNull
    private static final Editor EDITOR = Editor.getInstance();

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
        final InputManager inputManager = EDITOR.getInputManager();
        final Transform transform = notNull(editorControl.getTransformCenter());

        // cursor position and selected position vectors
        final Vector2f cursorPos = inputManager.getCursorPosition();
        final Vector3f transformOnScreen = camera.getScreenCoordinates(transform.getTranslation(), local.nextVector());
        final Vector2f selectedCoords = local.nextVector2f().set(transformOnScreen.getX(), transformOnScreen.getY());

        //set new deltaVector if it's not set (scale tool stores position of a cursor)
        if (editorControl.getDeltaVector() == null) {
            editorControl.setDeltaVector(new Vector3f(cursorPos.getX(), cursorPos.getY(), 0));
        }

        // Picked vector
        final Spatial toTransform = notNull(editorControl.getToTransform());
        final PickedAxis pickedAxis = editorControl.getPickedAxis();
        final Vector3f pickedVec = getPickedVector(toTransform, pickedAxis);

        // scale according to distance
        final Vector3f deltaVector = editorControl.getDeltaVector();
        final Vector2f delta2d = local.nextVector2f().set(deltaVector.getX(), deltaVector.getY());
        final Vector3f baseScale = local.nextVector().set(transform.getScale()); // default scale

        // scale object
        float disCursor = cursorPos.distance(selectedCoords);
        float disDelta = delta2d.distance(selectedCoords);
        float scaleValue = cursorPos.distance(delta2d) * 0.007f;

        if (disCursor > disDelta) {
             baseScale.addLocal(pickedVec.mult(scaleValue));
        } else {
            scaleValue = Math.min(scaleValue, 0.999f); // remove negateve values
            baseScale.subtractLocal(pickedVec.mult((scaleValue)));
        }

        toTransform.setLocalScale(baseScale);
        editorControl.notifyTransformed(toTransform);
    }

    @NotNull
    private Vector3f getPickedVector(@NotNull final Spatial spatial, @NotNull final PickedAxis pickedAxis) {

        final LocalObjects local = LocalObjects.get();
        final EditorTransformSupport editorControl = getEditorControl();
        final TransformationMode transformationMode = editorControl.getTransformationMode();
        final Quaternion rotation = transformationMode.getScaleRotation(spatial, editorControl.getCamera());

        final Vector3f result = local.nextVector();

        if (pickedAxis == PickedAxis.Y) {
            GeomUtils.getUp(rotation, result);
        } else if (pickedAxis == PickedAxis.Z) {
            GeomUtils.getDirection(rotation, result);
        } else {
            GeomUtils.getLeft(rotation, result);
        }

        result.set(abs(result.getX()), abs(result.getY()), abs(result.getZ()));

        if (Config.DEV_TRANSFORMS_DEBUG) {
            System.out.println("target scale rotation " + rotation + ", result vector " + result);
        }

        return result;
    }

    @Override
    protected void controlUpdate(final float tpf) {
    }

    @Override
    protected void controlRender(@NotNull final RenderManager renderManager, @NotNull final ViewPort viewPort) {
    }
}
