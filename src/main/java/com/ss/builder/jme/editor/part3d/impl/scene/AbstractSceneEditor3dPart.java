package com.ss.builder.jme.editor.part3d.impl.scene;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.app.state.AppState;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.audio.AudioNode;
import com.jme3.bounding.BoundingBox;
import com.jme3.light.Light;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RendererException;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Quad;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.jme.editor.part3d.EditableSceneEditor3dPart;
import com.ss.builder.jme.editor.part3d.control.impl.CameraEditor3dPartControl;
import com.ss.builder.jme.editor.part3d.control.impl.SelectionSupportEditor3dPartControl;
import com.ss.builder.jme.editor.part3d.control.impl.TransformationSupportEditor3dPartControl;
import com.ss.builder.jme.editor.part3d.impl.scene.handler.ApplyScaleToPhysicsControlsHandler;
import com.ss.builder.jme.editor.part3d.impl.scene.handler.DisableControlsTransformationHandler;
import com.ss.builder.jme.editor.part3d.impl.scene.handler.PhysicsControlTransformationHandler;
import com.ss.builder.jme.editor.part3d.impl.scene.handler.ReactivatePhysicsControlsTransformationHandler;
import com.ss.builder.manager.ExecutorManager;
import com.ss.builder.plugin.api.editor.part3d.Advanced3dFileEditor3dEditorPart;
import com.ss.builder.util.EditorUtils;
import com.ss.builder.util.JmeUtils;
import com.ss.editor.extension.property.EditableProperty;
import com.ss.editor.extension.scene.ScenePresentable;
import com.ss.builder.model.scene.EditorAudioNode;
import com.ss.builder.model.scene.EditorLightNode;
import com.ss.builder.model.scene.EditorPresentableNode;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.jme.editor.part3d.control.impl.AbstractSceneEditorHotKeys3dPartControl;
import com.ss.builder.jme.editor.part3d.control.impl.PaintingSupportEditor3dPartControl;
import com.ss.builder.editor.impl.scene.AbstractSceneFileEditor;
import com.ss.builder.util.GeomUtils;
import com.ss.builder.util.NodeUtils;
import com.ss.rlib.common.geom.util.AngleUtils;
import com.ss.rlib.common.plugin.extension.ExtensionPoint;
import com.ss.rlib.common.plugin.extension.ExtensionPointManager;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import com.ss.rlib.common.util.dictionary.DictionaryFactory;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * The base implementation of the {@link AppState} for the editor.
 *
 * @param <T> the type of file scene editor.
 * @param <M> the type of edited spatial.
 * @author JavaSaBr
 */
public abstract class AbstractSceneEditor3dPart<T extends AbstractSceneFileEditor & ModelChangeConsumer, M extends Spatial>
        extends Advanced3dFileEditor3dEditorPart<T> implements EditableSceneEditor3dPart {

    /**
     * @see SelectionFinder
     */
    public static final String EP_SELECTION_FINDER = "AbstractSceneEditor3dPart#selectionFinder";

    /**
     * @see TransformationHandler
     */
    public static final String EP_PRE_TRANSFORM_HANDLER = "AbstractSceneEditor3dPart#preTransfromHandler";

    /**
     * @see TransformationHandler
     */
    public static final String EP_POST_TRANSFORM_HANDLER = "AbstractSceneEditor3dPart#postTransformHandler";

    public static final String KEY_LOADED_MODEL = "jMB.sceneEditor.loadedModel";
    public static final String KEY_IGNORE_RAY_CAST = "jMB.sceneEditor.ignoreRayCast";
    public static final String KEY_MODEL_NODE = "jMB.sceneEditor.modelNode";
    public static final String KEY_SHAPE_CENTER = "jMB.sceneEditor.shapeCenter";
    public static final String KEY_SHAPE_INIT_SCALE = "jMB.sceneEditor.initScale";

    private static final float H_ROTATION = AngleUtils.degreeToRadians(45);
    private static final float V_ROTATION = AngleUtils.degreeToRadians(15);

    /**
     * The table with models to present lights on a scene.
     */
    private static final ObjectDictionary<Light.Type, Node> LIGHT_MODEL_TABLE;

    private static final Node AUDIO_NODE_MODEL;

    static {

        var assetManager = EditorUtils.getAssetManager();

        AUDIO_NODE_MODEL = (Node) assetManager.loadModel("graphics/models/speaker/speaker.j3o");

        LIGHT_MODEL_TABLE = DictionaryFactory.newObjectDictionary();
        LIGHT_MODEL_TABLE.put(Light.Type.Point, (Node) assetManager.loadModel("graphics/models/light/point_light.j3o"));
        LIGHT_MODEL_TABLE.put(Light.Type.Directional, (Node) assetManager.loadModel("graphics/models/light/direction_light.j3o"));
        LIGHT_MODEL_TABLE.put(Light.Type.Spot, (Node) assetManager.loadModel("graphics/models/light/spot_light.j3o"));
    }

    @FunctionalInterface
    public interface SelectionFinder {

        @JmeThread
        @Nullable Spatial find(@NotNull Object object);
    }

    @FunctionalInterface
    public interface TransformationHandler {

        @JmeThread
        void handle(@NotNull Spatial object);
    }

    private static final ExtensionPoint<SelectionFinder> SELECTION_FINDERS =
            ExtensionPointManager.register(EP_SELECTION_FINDER);

    private static final ExtensionPoint<TransformationHandler> PRE_TRANSFORM_HANDLERS =
            ExtensionPointManager.register(EP_PRE_TRANSFORM_HANDLER);

    private static final ExtensionPoint<TransformationHandler> POST_TRANSFORM_HANDLERS =
            ExtensionPointManager.register(EP_POST_TRANSFORM_HANDLER);

    static {

        // default handlers
        var disableControlsHandler = new DisableControlsTransformationHandler();
        var applyScaleToPhysicsControlsHandler = new ApplyScaleToPhysicsControlsHandler();

        PRE_TRANSFORM_HANDLERS.register(disableControlsHandler::onPreTransform)
                .register(applyScaleToPhysicsControlsHandler::onPreTransform);

        POST_TRANSFORM_HANDLERS.register(disableControlsHandler::onPostTransform)
                .register(new ReactivatePhysicsControlsTransformationHandler())
                .register(new PhysicsControlTransformationHandler())
                .register(applyScaleToPhysicsControlsHandler::onPostTransform);
    }

    /**
     * The map with cached light nodes.
     */
    @NotNull
    private final ObjectDictionary<Light, EditorLightNode> cachedLights;

    /**
     * The map with cached audio nodes.
     */
    @NotNull
    private final ObjectDictionary<AudioNode, EditorAudioNode> cachedAudioNodes;

    /**
     * The map with cached presentable objects.
     */
    @NotNull
    private final ObjectDictionary<ScenePresentable, EditorPresentableNode> cachedPresentableObjects;

    /**
     * The array of light nodes.
     */
    @NotNull
    private final Array<EditorLightNode> lightNodes;

    /**
     * The array of audio nodes.
     */
    @NotNull
    private final Array<EditorAudioNode> audioNodes;

    /**
     * The array of scene presentable nodes.
     */
    @NotNull
    private final Array<EditorPresentableNode> presentableNodes;

    /**
     * The node for the placement of controls.
     */
    @NotNull
    protected final Node toolNode;

    /**
     * The node for the placement of models.
     */
    @NotNull
    protected final Node modelNode;

    /**
     * The node for the placement of lights.
     */
    @NotNull
    protected final Node lightNode;

    /**
     * The node for the placement of audio nodes.
     */
    @NotNull
    protected final Node audioNode;

    /**
     * The node for the placement of presentable nodes.
     */
    @NotNull
    private final Node presentableNode;

    /**
     * Current display model.
     */
    @Nullable
    private M currentModel;

    /**
     * Grid of the scene.
     */
    @Nullable
    private Node grid;

    /**
     * The flag of visibility grid.
     */
    private boolean showGrid;

    public AbstractSceneEditor3dPart(@NotNull T fileEditor) {
        super(fileEditor);
        this.cachedLights = DictionaryFactory.newObjectDictionary();
        this.cachedAudioNodes = DictionaryFactory.newObjectDictionary();
        this.cachedPresentableObjects = DictionaryFactory.newObjectDictionary();
        this.modelNode = new Node("TreeNode");
        this.modelNode.setUserData(KEY_MODEL_NODE, true);
        this.toolNode = new Node("ToolNode");
        this.lightNodes = ArrayFactory.newArray(EditorLightNode.class);
        this.audioNodes = ArrayFactory.newArray(EditorAudioNode.class);
        this.presentableNodes = ArrayFactory.newArray(EditorPresentableNode.class);
        this.lightNode = new Node("Lights");
        this.audioNode = new Node("Audio nodes");
        this.presentableNode = new Node("Presentable nodes");

        controls.add(new AbstractSceneEditorHotKeys3dPartControl(this));
        controls.add(new PaintingSupportEditor3dPartControl<>(this));
        controls.add(new TransformationSupportEditor3dPartControl<>(this));
        controls.add(new SelectionSupportEditor3dPartControl<>(this));

        var cameraControl = requireControl(CameraEditor3dPartControl.class);
        cameraControl.setDefaultHorizontalRotation(H_ROTATION);
        cameraControl.setDefaultVerticalRotation(V_ROTATION);

        modelNode.attachChild(lightNode);
        modelNode.attachChild(audioNode);
        modelNode.attachChild(presentableNode);

        createToolElements();
        setShowGrid(true);
    }

    /**
     * Create tool elements.
     */
    @FromAnyThread
    private void createToolElements() {
        grid = createGrid();
        toolNode.attachChild(grid);
    }

    @FromAnyThread
    private @NotNull Node createGrid() {

        var gridNode = new Node("GridNode");

        var gridColor = new ColorRGBA(0.4f, 0.4f, 0.4f, 0.5f);
        var xColor = new ColorRGBA(1.0f, 0.1f, 0.1f, 0.5f);
        var zColor = new ColorRGBA(0.1f, 1.0f, 0.1f, 0.5f);

        var gridMaterial = JmeUtils.coloredWireframeMaterial(gridColor, EditorUtils.getAssetManager());
        gridMaterial.getAdditionalRenderState()
                .setBlendMode(RenderState.BlendMode.Alpha);

        var xMaterial = JmeUtils.coloredWireframeMaterial(xColor, EditorUtils.getAssetManager());
        xMaterial.getAdditionalRenderState()
                .setLineWidth(5);

        var zMaterial = JmeUtils.coloredWireframeMaterial(zColor, EditorUtils.getAssetManager());
        zMaterial.getAdditionalRenderState()
                .setLineWidth(5);

        var gridSize = getGridSize();

        var grid = new Geometry("grid", new Grid(gridSize, gridSize, 1.0f));
        grid.setMaterial(gridMaterial);
        grid.setQueueBucket(RenderQueue.Bucket.Transparent);
        grid.setShadowMode(RenderQueue.ShadowMode.Off);
        grid.setCullHint(CullHint.Never);
        grid.setLocalTranslation(gridSize / 2 * -1, 0, gridSize / 2 * -1);

        var quad = new Quad(gridSize, gridSize);
        var gridCollision = new Geometry("collision", quad);
        gridCollision.setMaterial(JmeUtils.coloredWireframeMaterial(gridColor, EditorUtils.getAssetManager()));
        gridCollision.setQueueBucket(RenderQueue.Bucket.Transparent);
        gridCollision.setShadowMode(RenderQueue.ShadowMode.Off);
        gridCollision.setCullHint(CullHint.Always);
        gridCollision.setLocalTranslation(gridSize / 2 * -1, 0, gridSize / 2 * -1);
        gridCollision.setLocalRotation(new Quaternion().fromAngles(AngleUtils.degreeToRadians(90), 0, 0));

        gridNode.attachChild(grid);
        gridNode.attachChild(gridCollision);

        // Red line for X axis
        var xAxis = new Line(new Vector3f(-gridSize / 2, 0f, 0f), new Vector3f(gridSize / 2 - 1, 0f, 0f));

        var gxAxis = new Geometry("XAxis", xAxis);
        gxAxis.setModelBound(new BoundingBox());
        gxAxis.setShadowMode(RenderQueue.ShadowMode.Off);
        gxAxis.setCullHint(CullHint.Never);
        gxAxis.setMaterial(xMaterial);

        gridNode.attachChild(gxAxis);

        // Blue line for Z axis
        var zAxis = new Line(new Vector3f(0f, 0f, -gridSize / 2), new Vector3f(0f, 0f, gridSize / 2 - 1));

        var gzAxis = new Geometry("ZAxis", zAxis);
        gzAxis.setModelBound(new BoundingBox());
        gzAxis.setShadowMode(RenderQueue.ShadowMode.Off);
        gzAxis.setCullHint(CullHint.Never);
        gzAxis.setMaterial(zMaterial);

        gridNode.attachChild(gzAxis);

        return gridNode;
    }

    @Override
    @JmeThread
    public @NotNull Node getToolNode() {
        return toolNode;
    }

    /**
     * Get the grid size.
     *
     * @return the grid size.
     */
    @FromAnyThread
    protected int getGridSize() {
        return 20;
    }

    /**
     * Get the grid.
     *
     * @return the grid.
     */
    @FromAnyThread
    private @NotNull Node getGrid() {
        return notNull(grid);
    }

    /*@Override
    protected void postCameraUpdate(float tpf) {
        super.postCameraUpdate(tpf);

        lightNodes.forEach(EditorLightNode::updateModel);
        audioNodes.forEach(EditorAudioNode::updateModel);
        presentableNodes.forEach(EditorPresentableNode::updateModel);
    }
*/

    /**
     * Set true if need to show a grid.
     *
     * @param showGrid true if need to show a grid.
     */
    @JmeThread
    private void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
    }

    /**
     * Return true if the grid is showed.
     *
     * @return true if the grid is showed.
     */
    @JmeThread
    private boolean isShowGrid() {
        return showGrid;
    }

    /**
     * Get a geometry on a scene for a position on a screen.
     *
     * @param screenX the x position on screen.
     * @param screenY the y position on screen.
     * @return the position on a scene.
     */
    @JmeThread
    public @Nullable Geometry getGeometryByScreenPos(float screenX, float screenY) {
        return GeomUtils.getGeometryFromScreenPos(notNull(getCurrentModel()), getCamera(), screenX, screenY);
    }

    /**
     * Get a position on a scene for a cursor position.
     *
     * @param screenX the x position on screen.
     * @param screenY the y position on screen.
     * @return the position on a scene.
     */
    @JmeThread
    public @NotNull Vector3f getScenePosByScreenPos(float screenX, float screenY) {

        var camera = getCamera();
        var currentModel = notNull(getCurrentModel());

        var modelPoint = GeomUtils.getContactPointFromScreenPos(currentModel, camera, screenX, screenY);
        var gridPoint = GeomUtils.getContactPointFromScreenPos(getGrid(), camera, screenX, screenY);

        if (modelPoint == null) {
            return gridPoint == null ? Vector3f.ZERO : gridPoint;
        } else if (gridPoint == null) {
            return modelPoint;
        }

        var distance = modelPoint.distance(camera.getLocation());

        if (gridPoint.distance(camera.getLocation()) < distance) {
            return gridPoint;
        } else {
            return modelPoint;
        }
    }

    /**
     * Get a normal on a scene for a cursor position.
     *
     * @param screenX the x position on screen.
     * @param screenY the y position on screen.
     * @return the normal on the current scene or null.
     */
    @JmeThread
    public @Nullable Vector3f getSceneNormalByScreenPos(float screenX, float screenY) {
        var camera = getCamera();
        var currentModel = notNull(getCurrentModel());
        return GeomUtils.getContactNormalFromScreenPos(currentModel, camera, screenX, screenY);
    }

    /**
     * Update the state of showing a scene's grid.
     *
     * @param showGrid true if need to show scene's grid.
     */
    @FromAnyThread
    public void updateShowGrid(boolean showGrid) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> updateShowGridInJme(showGrid));
    }

    /**
     * Update the state of showing a scene's grid in jME thread.
     *
     * @param showGrid true if need to show scene's grid.
     */
    @JmeThread
    private void updateShowGridInJme(boolean showGrid) {

        if (isShowGrid() == showGrid) {
            return;
        }

        var grid = getGrid();

        if (showGrid) {
            toolNode.attachChild(grid);
        } else {
            toolNode.detachChild(grid);
        }

        setShowGrid(showGrid);
    }

    /**
     * Notify about an attempt to change the property from jME thread.
     *
     * @param object the object.
     * @param name   the property name.
     */
    @JmeThread
    public void notifyPropertyPreChanged(@NotNull Object object, @NotNull String name) {
        if (object instanceof Spatial) {
            if (isTransformationProperty(name)) {
                PRE_TRANSFORM_HANDLERS.getExtensions()
                        .forEach(handler -> handler.handle((Spatial) object));
            }
        }
    }

    /**
     * Notify about property changes.
     *
     * @param object the object with changes.
     * @param name   the property name.
     */
    @JmeThread
    public void notifyPropertyChanged(@NotNull Object object, @NotNull String name) {

        if (object instanceof EditableProperty) {
            object = ((EditableProperty<?, ?>) object).getObject();
        }

        if (object instanceof AudioNode) {

            getAudioNodeOpt((AudioNode) object)
                    .ifPresent(EditorAudioNode::sync);

        } else if (object instanceof Light) {

            getLightNodeOpt((Light) object)
                    .ifPresent(EditorLightNode::sync);

        } else if (object instanceof ScenePresentable) {

            getPresentableNodeOpt((ScenePresentable) object).stream()
                    .peek(EditorPresentableNode::sync)
                    .forEach(EditorPresentableNode::updateGeometry);
        }

        if (object instanceof Spatial) {
            if (isTransformationProperty(name)) {
                var transformed = object;
                POST_TRANSFORM_HANDLERS.getExtensions()
                        .forEach(handler -> handler.handle((Spatial) transformed));
            }
        }
    }

    protected boolean isTransformationProperty(@NotNull String name) {
        return Messages.MODEL_PROPERTY_LOCATION.equals(name) ||
            Messages.MODEL_PROPERTY_SCALE.equals(name) ||
            Messages.MODEL_PROPERTY_ROTATION.equals(name) ||
            Messages.MODEL_PROPERTY_TRANSFORMATION.equals(name);
    }

    /**
     * Show the model in this editor.
     *
     * @param model the model.
     */
    @FromAnyThread
    public void openModel(@NotNull M model) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> openModelInJme(model));
    }

    /**
     * Show the model in this editor in jME thread.
     *
     * @param model the model.
     */
    @JmeThread
    private void openModelInJme(@NotNull M model) {

        var currentModel = getCurrentModel();

        if (currentModel != null) {
            detachPrevModel(modelNode, currentModel);
        }

        NodeUtils.visitGeometry(model, geometry -> {

            var renderManager = EditorUtils.getRenderManager();
            try {
                renderManager.preloadScene(geometry);
            } catch (final RendererException | AssetNotFoundException | UnsupportedOperationException e) {
                EditorUtils.handleException(LOGGER, this,
                        new RuntimeException("Found invalid material in the geometry: [" + geometry.getName() + "]. " +
                                "The material will be removed from the geometry.", e));
                geometry.setMaterial(EditorUtils.getDefaultMaterial());
            }
        });

        PRE_TRANSFORM_HANDLERS.getExtensions()
                .forEach(handler -> handler.handle(model));

        attachModel(model, modelNode);

        POST_TRANSFORM_HANDLERS.getExtensions()
                .forEach(handler -> handler.handle(model));

        setCurrentModel(model);
    }

    @JmeThread
    protected void detachPrevModel(@NotNull Node modelNode, @Nullable M currentModel) {
        if (currentModel != null) {
            modelNode.detachChild(currentModel);
        }
    }

    @JmeThread
    protected void attachModel(@NotNull M model, @NotNull Node modelNode) {
        modelNode.attachChild(model);
    }

    /**
     * Set the current model.
     *
     * @param currentModel the current model.
     */
    @JmeThread
    private void setCurrentModel(@Nullable M currentModel) {
        this.currentModel = currentModel;
    }

    /**
     * Get the current model.
     *
     * @return the current model.
     */
    @FromAnyThread
    public @Nullable M getCurrentModel() {
        return currentModel;
    }

    /**
     * Add the light to this editor.
     *
     * @param light the light.
     */
    @FromAnyThread
    public void addLight(@NotNull Light light) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> addLightInJme(light));
    }

    /**
     * Add the light to this editor in jME thread.
     *
     * @param light the light.
     */
    @JmeThread
    private void addLightInJme(@NotNull Light light) {

        var node = LIGHT_MODEL_TABLE.get(light.getType());

        if (node == null) {
            return;
        }

        var camera = EditorUtils.getGlobalCamera();

        var lightModel = notNull(cachedLights.get(light, () -> {

            var model = (Node) node.clone();
            model.setLocalScale(0.03F);

            var result = new EditorLightNode(camera);
            result.setModel(model);

            var geometry = NodeUtils.findGeometry(model, "White");

            if (geometry == null) {
                LOGGER.warning(this, "not found geometry for the node " + geometry);
                return null;
            }

            var material = geometry.getMaterial();
            material.setColor("Color", light.getColor());

            return result;
        }));

        lightModel.setLight(light);
        lightModel.sync();

        lightNode.attachChild(lightModel);
        lightNode.attachChild(lightModel.getModel());

        lightNodes.add(lightModel);
    }

    /**
     * Move editor's camera to the location.
     *
     * @param location the location.
     */
    @FromAnyThread
    public void moveCameraTo(@NotNull Vector3f location) {
       // ExecutorManager.getInstance()
        //        .addJmeTask(() -> getNodeForCamera().setLocalTranslation(location));
    }

    /**
     * Look at the spatial.
     *
     * @param spatial the spatial.
     */
    @FromAnyThread
    public void cameraLookAt(@NotNull Spatial spatial) {

      /*  var executorManager = ExecutorManager.getInstance();
        executorManager.addJmeTask(() -> {

            var editorCamera = notNull(getEditorCamera());
            var local = LocalObjects.get();

            float distance;

            var worldBound = spatial.getWorldBound();

            if (worldBound != null) {

                distance = worldBound.getVolume();

                if (worldBound instanceof BoundingBox) {
                    final BoundingBox boundingBox = (BoundingBox) worldBound;
                    distance = boundingBox.getXExtent();
                    distance = Math.max(distance, boundingBox.getYExtent());
                    distance = Math.max(distance, boundingBox.getZExtent());
                    distance *= 2F;
                } else if (worldBound instanceof BoundingSphere) {
                    distance = ((BoundingSphere) worldBound).getRadius() * 2F;
                }

            } else {

               distance = getCamera().getLocation()
                       .distance(spatial.getWorldTranslation());
            }

            editorCamera.setTargetDistance(distance);

            var position = local.nextVector()
                    .set(spatial.getWorldTranslation());

            getNodeForCamera().setLocalTranslation(position);
        });*/
    }

    /**
     * Remove the light from this editor.
     *
     * @param light the light.
     */
    @FromAnyThread
    public void removeLight(@NotNull Light light) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> removeLightInJme(light));
    }

    /**
     * Remove the light from this editor in jME thread.
     *
     * @param light the light.
     */
    @JmeThread
    private void removeLightInJme(@NotNull Light light) {

        var node = LIGHT_MODEL_TABLE.get(light.getType());

        if (node == null) {
            return;
        }

        var lightModel = cachedLights.get(light);
        if (lightModel == null) {
            return;
        }

        lightModel.setLight(null);

        lightNode.detachChild(lightModel);
        lightNode.detachChild(notNull(lightModel.getModel()));

        lightNodes.fastRemove(lightModel);
    }

    /**
     * Add the audio node to this editor.
     *
     * @param audioNode the audio node.
     */
    @FromAnyThread
    public void addAudioNode(@NotNull AudioNode audioNode) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> addAudioNodeInJme(audioNode));
    }

    /**
     * Add the audio node to this editor in jME thread.
     *
     * @param audio the audio node.
     */
    @JmeThread
    private void addAudioNodeInJme(@NotNull AudioNode audio) {

        var audioModel = notNull(cachedAudioNodes.get(audio, () -> {

            var model = (Node) AUDIO_NODE_MODEL.clone();
            model.setLocalScale(0.01F);

            var result = new EditorAudioNode(getCamera());
            result.setModel(model);

            return result;
        }));

        audioModel.setAudioNode(audio);
        audioModel.sync();

        audioNode.attachChild(audioModel);
        audioNode.attachChild(audioModel.getModel());

        audioNodes.add(audioModel);
    }

    /**
     * Remove the audio node from this editor.
     *
     * @param audio the audio node.
     */
    @FromAnyThread
    public void removeAudioNode(@NotNull AudioNode audio) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> removeAudioNodeInJme(audio));
    }

    /**
     * Remove the audio node from this editor in jME thread.
     *
     * @param audio the audio node.
     */
    @JmeThread
    private void removeAudioNodeInJme(@NotNull AudioNode audio) {

        var audioModel = cachedAudioNodes.get(audio);

        if (audioModel == null) {
            return;
        }

        audioModel.setAudioNode(null);

        audioNode.detachChild(audioModel);
        audioNode.detachChild(notNull(audioModel.getModel()));

        audioNodes.fastRemove(audioModel);
    }

    /**
     * Add the presentable object to this editor.
     *
     * @param presentable the presentable object.
     */
    @FromAnyThread
    public void addPresentable(@NotNull ScenePresentable presentable) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> addPresentableInJme(presentable));
    }

    /**
     * Add the presentable object to this editor in jME.
     *
     * @param presentable the presentable object.
     */
    @JmeThread
    private void addPresentableInJme(@NotNull ScenePresentable presentable) {

        var node = notNull(cachedPresentableObjects.get(presentable, EditorPresentableNode::new));

        node.setObject(presentable);
        node.updateGeometry();
        node.sync();

        var editedNode = node.getEditedNode();
        editedNode.setCullHint(CullHint.Always);

        presentableNode.attachChild(node);
        presentableNode.attachChild(node.getModel());

        node.setObject(presentable);

        presentableNodes.add(node);
    }

    /**
     * Remove the presentable object from this editor.
     *
     * @param presentable the presentable.
     */
    @FromAnyThread
    public void removePresentable(@NotNull ScenePresentable presentable) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> removePresentableInJme(presentable));
    }

    /**
     * Remove the presentable object from this editor in jME thread.
     *
     * @param presentable the presentable.
     */
    @JmeThread
    private void removePresentableInJme(@NotNull ScenePresentable presentable) {

        var node = cachedPresentableObjects.get(presentable);

        if (node == null) {
            return;
        }

        node.setObject(null);

        presentableNode.detachChild(node);
        presentableNode.detachChild(notNull(node.getModel()));

        presentableNodes.fastRemove(node);
    }

    /**
     * Get a light node for the light.
     *
     * @param light the light.
     * @return the light node or null.
     */
    @FromAnyThread
    public @Nullable EditorLightNode getLightNode(@NotNull Light light) {
        return lightNodes.findAny(light,
                (node, toCheck) -> node.getLight() == toCheck);
    }

    /**
     * Get an optional of a light node for the light.
     *
     * @param light the light.
     * @return the optional value.
     */
    @FromAnyThread
    public @NotNull Optional<EditorLightNode> getLightNodeOpt(@NotNull Light light) {
        return Optional.ofNullable(getLightNode(light));
    }

    /**
     * Get a light node for the model.
     *
     * @param model the model.
     * @return the light node or null.
     */
    @FromAnyThread
    public @Nullable EditorLightNode getLightNode(@NotNull Spatial model) {
        return lightNodes.findAny(model,
                (node, toCheck) -> node.getModel() == toCheck);
    }

    /**
     * Get an editor audio node of the audio node.
     *
     * @param audioNode the audio node.
     * @return the editor audio node or null.
     */
    @FromAnyThread
    public @Nullable EditorAudioNode getAudioNode(@NotNull AudioNode audioNode) {
        return audioNodes.findAny(audioNode,
                (node, toCheck) -> node.getAudioNode() == toCheck);
    }

    /**
     * Get an optional of an editor audio node for the audio node.
     *
     * @param audioNode the audio node.
     * @return the optional value.
     */
    @FromAnyThread
    public @NotNull Optional<EditorAudioNode> getAudioNodeOpt(@NotNull AudioNode audioNode) {
        return Optional.ofNullable(getAudioNode(audioNode));
    }

    /**
     * Get an editor audio node for the model.
     *
     * @param model the model.
     * @return the editor audio node or null.
     */
    @FromAnyThread
    public @Nullable EditorAudioNode getAudioNode(@NotNull Spatial model) {
        return audioNodes.findAny(model,
                (node, toCheck) -> node.getModel() == toCheck);
    }

    /**
     * Get an optional of an editor audio node for the model.
     *
     * @param model the model.
     * @return the optional value.
     */
    @FromAnyThread
    public @NotNull Optional<EditorAudioNode> getAudioNodeOpt(@NotNull Spatial model) {
        return Optional.ofNullable(getAudioNode(model));
    }

    /**
     * Get an editor presentable node for the presentable object.
     *
     * @param presentable the presentable object.
     * @return the editor presentable node or null.
     */
    @FromAnyThread
    public @Nullable EditorPresentableNode getPresentableNode(@NotNull ScenePresentable presentable) {
        return presentableNodes.findAny(presentable,
                (node, toCheck) -> node.getObject() == toCheck);
    }

    /**
     * Get an optional of an editor presentable node for the presentable object.
     *
     * @param presentable the presentable object.
     * @return the optional value.
     */
    @FromAnyThread
    public @NotNull Optional<EditorPresentableNode> getPresentableNodeOpt(@NotNull ScenePresentable presentable) {
        return Optional.ofNullable(getPresentableNode(presentable));
    }

    /**
     * Get an editor presentable node for the model.
     *
     * @param model the model.
     * @return the editor presentable node or null.
     */
    @FromAnyThread
    public @Nullable EditorPresentableNode getPresentableNode(@NotNull Spatial model) {
        return presentableNodes.findAny(model,
                (node, toCheck) -> node.getModel() == toCheck);
    }
/*
    @Override
    @JmeThread
    protected void notifyChangedCameraSettings(
            @NotNull Vector3f cameraLocation,
            float hRotation,
            float vRotation,
            float targetDistance,
            float cameraFlySpeed
    ) {
        super.notifyChangedCameraSettings(cameraLocation, hRotation, vRotation, targetDistance, cameraFlySpeed);

        var executorManager = ExecutorManager.getInstance();
        executorManager.addFxTask(() ->
                fileEditor.notifyChangedCameraSettings(cameraLocation, hRotation, vRotation, targetDistance, cameraFlySpeed));
    }
*/
    @Override
    @JmeThread
    public @NotNull Camera getCamera() {
        return EditorUtils.getGlobalCamera();
    }
}
