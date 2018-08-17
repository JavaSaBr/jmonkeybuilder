package com.ss.editor.part3d.editor.control.impl;

import static com.ss.editor.part3d.editor.EditableSceneEditor3dPart.PROP_IS_EDITING;
import static com.ss.editor.part3d.editor.impl.scene.AbstractSceneEditor3dPart.KEY_SHAPE_CENTER;
import static com.ss.editor.part3d.editor.impl.scene.AbstractSceneEditor3dPart.KEY_SHAPE_INIT_SCALE;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.app.Application;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingSphere;
import com.jme3.effect.ParticleEmitter;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.WireBox;
import com.jme3.scene.debug.WireSphere;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.scene.*;
import com.ss.editor.part3d.editor.EditableSceneEditor3dPart;
import com.ss.editor.part3d.editor.ExtendableEditor3dPart;
import com.ss.editor.part3d.editor.control.impl.BaseInputEditor3dPartControl;
import com.ss.editor.util.JmeUtils;
import com.ss.editor.util.NodeUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The control to implement painting support on the scene editor 3d part.
 *
 * @author JavaSaBr
 */
public class SelectionSupportEditor3dPartControl<T extends EditableSceneEditor3dPart & ExtendableEditor3dPart>
        extends BaseInputEditor3dPartControl<T> {

    private static final ObjectDictionary<String, Trigger> TRIGGERS =
            ObjectDictionary.ofType(String.class, Trigger.class);

    private static final ColorRGBA SELECTION_COLOR = new ColorRGBA(1F, 170 / 255F, 64 / 255F, 1F);

    private static final String MOUSE_RIGHT_CLICK = "jMB.selectionSupportEditor.mouseRightClick";

    private static final String[] MAPPINGS;

    static {
        TRIGGERS.put(MOUSE_RIGHT_CLICK, new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        MAPPINGS = TRIGGERS.keyArray(String.class)
                .toArray(String.class);
    }

    /**
     * The selection models of selected models.
     */
    @NotNull
    private final ObjectDictionary<Spatial, Geometry> selectionShape;

    /**
     * The array of selected models.
     */
    @NotNull
    private final Array<Spatial> selected;

    /**
     * Material for selection.
     */
    @Nullable
    private Material selectionMaterial;

    /**
     * The flag of visibility selection.
     */
    private boolean showSelection;

    public SelectionSupportEditor3dPartControl(@NotNull T editor3dPart) {
        super(editor3dPart);

        this.selected = Array.ofType(Spatial.class);
        this.selectionShape = ObjectDictionary.ofType(Spatial.class, Geometry.class);

        actionHandlers.put(MOUSE_RIGHT_CLICK, (isPressed, tpf) -> {
            if (!isPressed && !editor3dPart.getBooleanProperty(PROP_IS_EDITING)) {
                processSelect();
            }
        });
    }

    @Override
    @JmeThread
    public void register(@NotNull InputManager inputManager) {
        TRIGGERS.forEach(inputManager, JmeUtils::addMapping);
        inputManager.addListener(getActionListener(), MAPPINGS);
    }

    @Override
    @JmeThread
    public void initialize(@NotNull Application application) {

        if (selectionMaterial == null) {
            selectionMaterial = JmeUtils.coloredWireframeMaterial(SELECTION_COLOR, application.getAssetManager());
        }
    }

    /**
     * Get the material of selection.
     *
     * @return the material of selection.
     */
    @FromAnyThread
    private @NotNull Material getSelectionMaterial() {
        return notNull(selectionMaterial);
    }

    /**
     * Select the objects.
     *
     * @param objects the objects.
     */
    @FromAnyThread
    public void select(@NotNull Array<Spatial> objects) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> selectInJme(objects));
    }

    /**
     * Select the object.
     *
     * @param object the object.
     */
    @FromAnyThread
    public void select(@NotNull Spatial object) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> selectInJme(Array.of(object)));
    }

    /**
     * Select the objects in the jMe thread.
     *
     * @param objects the objects.
     */
    @JmeThread
    private void selectInJme(@NotNull Array<Spatial> objects) {

        if (objects.isEmpty()) {
            selected.forEach(this, (spatial, ed) -> ed.removeFromSelection(spatial));
            selected.clear();
        } else {

            for (var iterator = selected.iterator(); iterator.hasNext(); ) {

                var spatial = iterator.next();
                if (objects.contains(spatial)) {
                    continue;
                }

                removeFromSelection(spatial);
                iterator.fastRemove();
            }

            for (var spatial : objects) {
                if (!selected.contains(spatial)) {
                    addToSelection(spatial);
                }
            }
        }

        //updateToTransform();
    }

    /**
     * Add the spatial to selection.
     */
    @JmeThread
    private void addToSelection(@NotNull Spatial spatial) {

        if (spatial instanceof VisibleOnlyWhenSelected) {
            spatial.setCullHint(Spatial.CullHint.Dynamic);
        }

        selected.add(spatial);

        if (spatial instanceof NoSelection) {
            return;
        }

        Geometry shape;

        if (spatial instanceof ParticleEmitter) {
            shape = buildBoxSelection(spatial);
        } else if (spatial instanceof Geometry) {
            shape = buildGeometrySelection((Geometry) spatial);
        } else {
            shape = buildBoxSelection(spatial);
        }

        if (shape == null) {
            return;
        }

        if (isShowSelection()) {
            //toolNode.attachChild(shape);
        }

        selectionShape.put(spatial, shape);
    }

    /**
     * Remove the spatial from the selection.
     */
    @JmeThread
    private void removeFromSelection(@NotNull Spatial spatial) {
        //setTransformCenter(null);
        //setToTransform(null);

        var shape = selectionShape.remove(spatial);

        if (shape != null) {
            shape.removeFromParent();
        }

        selected.fastRemove(spatial);

        if (spatial instanceof VisibleOnlyWhenSelected) {
            spatial.setCullHint(Spatial.CullHint.Always);
        }
    }

    /**
     * Build the selection box for the spatial.
     */
    @JmeThread
    private Geometry buildBoxSelection(@NotNull Spatial spatial) {

        NodeUtils.updateWorldBound(spatial);

        var bound = spatial.getWorldBound();

        if (bound instanceof BoundingBox) {

            var boundingBox = (BoundingBox) bound;
            var center = boundingBox.getCenter().subtract(spatial.getWorldTranslation());
            var initScale = spatial.getLocalScale().clone();

            var geometry = WireBox.makeGeometry(boundingBox);
            geometry.setName("SelectionShape");
            geometry.setMaterial(getSelectionMaterial());
            geometry.setUserData(KEY_SHAPE_CENTER, center);
            geometry.setUserData(KEY_SHAPE_INIT_SCALE, initScale);

            var position = geometry.getLocalTranslation();
            position.addLocal(center);

            geometry.setLocalTranslation(position);

            return geometry;

        } else if (bound instanceof BoundingSphere) {

            var boundingSphere = (BoundingSphere) bound;

            var wire = new WireSphere();
            wire.fromBoundingSphere(boundingSphere);

            var geometry = new Geometry("SelectionShape", wire);
            geometry.setMaterial(getSelectionMaterial());
            geometry.setLocalTranslation(spatial.getWorldTranslation());

            return geometry;
        }

        var geometry = WireBox.makeGeometry(new BoundingBox(Vector3f.ZERO, 1, 1, 1));
        geometry.setName("SelectionShape");
        geometry.setMaterial(getSelectionMaterial());
        geometry.setLocalTranslation(spatial.getWorldTranslation());

        return geometry;
    }

    /**
     * Build selection grid for the geometry.
     */
    @JmeThread
    private Geometry buildGeometrySelection(@NotNull Geometry geom) {

        var mesh = geom.getMesh();
        if (mesh == null) {
            return null;
        }

        var geometry = new Geometry("SelectionShape", mesh);
        geometry.setMaterial(getSelectionMaterial());
        geometry.setLocalTransform(geom.getWorldTransform());

        return geometry;
    }


    @Override
    @JmeThread
    public void postCameraUpdate(float tpf) {

        selected.forEach(this, (spatial, editor3dPart) -> {

            if (spatial instanceof EditorLightNode) {
                spatial = ((EditorLightNode) spatial).getModel();
            } else if (spatial instanceof EditorAudioNode) {
                spatial = ((EditorAudioNode) spatial).getModel();
            } else if (spatial instanceof EditorPresentableNode) {
                spatial = ((EditorPresentableNode) spatial).getModel();
            }

            if (spatial == null) {
                return;
            }

            //editor3dPart.updateTransformNode(spatial.getWorldTransform());

            var selectionShape = editor3dPart.selectionShape;
            var shape = selectionShape.get(spatial);
            if (shape == null) {
                return;
            }

            var position = shape.getLocalTranslation();
            position.set(spatial.getWorldTranslation());

            var center = shape.<Vector3f>getUserData(KEY_SHAPE_CENTER);
            var initScale = shape.<Vector3f>getUserData(KEY_SHAPE_INIT_SCALE);

            if (center != null) {

                if (!initScale.equals(spatial.getLocalScale())) {

                    initScale.set(spatial.getLocalScale());

                    NodeUtils.updateWorldBound(spatial);

                    var bound = (BoundingBox) spatial.getWorldBound();
                    bound.getCenter().subtract(spatial.getWorldTranslation(), center);

                    var mesh = (WireBox) shape.getMesh();
                    mesh.updatePositions(bound.getXExtent(), bound.getYExtent(), bound.getZExtent());
                }

                position.addLocal(center);

            } else {
                shape.setLocalRotation(spatial.getWorldRotation());
                shape.setLocalScale(spatial.getWorldScale());
            }

            shape.setLocalTranslation(position);
        });
    }

    /**
     * Set true if need to show a selection grid.
     *
     * @param showSelection true if need to show a selection grid.
     */
    @JmeThread
    private void setShowSelection(final boolean showSelection) {
        this.showSelection = showSelection;
    }

    /**
     * Return true if a selection grid is showed.
     *
     * @return true if a selection grid is showed.
     */
    @JmeThread
    private boolean isShowSelection() {
        return showSelection;
    }

    /**
     * Update showing state of selection grid.
     *
     * @param showSelection true if need to show selection grid.
     */
    @FromAnyThread
    public void updateShowSelection(boolean showSelection) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> updateShowSelectionInJme(showSelection));
    }

    /**
     * Update showing state of selection grid in jME thread.
     *
     * @param showSelection true if need to show selection grid.
     */
    @JmeThread
    private void updateShowSelectionInJme(boolean showSelection) {

        if (isShowSelection() == showSelection) {
            return;
        }

        if (showSelection && !selectionShape.isEmpty()) {
           // selectionShape.forEach(toolNode::attachChild);
        } else if (!showSelection && !selectionShape.isEmpty()) {
            //selectionShape.forEach(toolNode::detachChild);
        }

        setShowSelection(showSelection);
    }

    /**
     * Handling a click in the area of the editor.
     */
    @JmeThread
    private void processSelect() {

        /*var anyGeometry = GeomUtils.getGeometryFromCursor(modelNode, getCamera());
        var currentModel = notNull(getCurrentModel());

        Object toSelect = anyGeometry == null ? null : findToSelect(anyGeometry);

        if (toSelect == null && anyGeometry != null) {
            var modelGeometry = GeomUtils.getGeometryFromCursor(currentModel, getCamera());
            toSelect = modelGeometry == null ? null : findToSelect(modelGeometry);
        }

        var result = toSelect;

        ExecutorManager.getInstance()
                .addFxTask(() -> notifySelected(result));*/
    }


    /**
     * Find to select object.
     *
     * @param object the object
     * @return the object
     */
    @JmeThread
    protected @Nullable Object findToSelect(@NotNull Object object) {

       /* for (var finder : SELECTION_FINDERS.getExtensions()) {
            var spatial = finder.find(object);
            if (spatial != null && spatial.isVisible()) {
                return spatial;
            }
        }

        if (object instanceof Geometry) {

            var spatial = (Spatial) object;
            var parent = NodeUtils.findParent(spatial, 2);

            var lightNode = parent == null ? null : getLightNode(parent);

            if (lightNode != null) {
                return lightNode;
            }

            var audioNode = parent == null ? null : getAudioNode(parent);

            if (audioNode != null) {
                return audioNode;
            }

            parent = NodeUtils.findParent(spatial, AssetLinkNode.class::isInstance);

            if (parent != null) {
                return parent;
            }

            parent = NodeUtils.findParent(spatial,
                    p -> Boolean.TRUE.equals(p.getUserData(KEY_LOADED_MODEL)));

            if (parent != null) {
                return parent;
            }
        }

        if (object instanceof Spatial) {

            var spatial = (Spatial) object;

            if (!spatial.isVisible()) {
                return null;
            } else if (findParent(spatial, sp -> !sp.isVisible()) != null) {
                return null;
            } else if (findParent(spatial, sp -> sp == getCurrentModel()) == null) {
                return null;
            }
        }

        return object;*/
       return null;
    }

    @FxThread
    private void notifySelected(@Nullable Object object) {
        //fileEditor.notifySelected(object);
    }
}
