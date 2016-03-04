package com.ss.editor.state.editor.impl;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.ChaseCamera;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.ss.editor.Editor;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.state.editor.EditorState;

import rlib.logging.Logger;
import rlib.logging.LoggerManager;

/**
 * Базовая реализация.
 *
 * @author Ronn
 */
public abstract class AbstractEditorState extends AbstractAppState implements EditorState {

    protected static final Logger LOGGER = LoggerManager.getLogger(EditorState.class);

    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();
    protected static final Editor EDITOR = Editor.getInstance();

    /**
     * Опциональная камера для сцены.
     */
    private final ChaseCamera chaseCamera;

    /**
     * Источник света для chase камеры.
     */
    private final DirectionalLight lightForChaseCamera;

    /**
     * Рутовый узел.
     */
    private final Node stateNode;

    public AbstractEditorState() {
        this.stateNode = new Node(getClass().getSimpleName());
        this.chaseCamera = needChaseCamera() ? createChaseCamera() : null;
        this.lightForChaseCamera = needLightForChaseCamera() ? createLightForChaseCamera() : null;

        if (lightForChaseCamera != null) {
            stateNode.addLight(lightForChaseCamera);
        }
    }

    /**
     * @return рутовый узел.
     */
    protected Node getStateNode() {
        return stateNode;
    }

    /**
     * @return опциональная камера для сцены.
     */
    protected ChaseCamera getChaseCamera() {
        return chaseCamera;
    }

    @Override
    public void initialize(final AppStateManager stateManager, final Application application) {
        super.initialize(stateManager, application);

        final Node rootNode = EDITOR.getRootNode();
        rootNode.attachChild(getStateNode());

        final ChaseCamera chaseCamera = getChaseCamera();

        if (chaseCamera != null) {
            chaseCamera.setEnabled(true);
        }
    }

    @Override
    public void cleanup() {
        super.cleanup();

        final Node rootNode = EDITOR.getRootNode();
        rootNode.detachChild(getStateNode());

        final ChaseCamera chaseCamera = getChaseCamera();

        if (chaseCamera != null) {
            chaseCamera.setEnabled(false);
        }
    }

    /**
     * Нужна ли камера для этой части.
     */
    protected boolean needChaseCamera() {
        return false;
    }

    /**
     * Нужен ли источник света для chase камеры.
     */
    protected boolean needLightForChaseCamera() {
        return false;
    }

    protected ChaseCamera createChaseCamera() {

        final Camera camera = EDITOR.getCamera();

        final ChaseCamera chaser = new ChaseCamera(camera, getNodeForChaseCamera(), EDITOR.getInputManager());
        chaser.setDragToRotate(true);
        chaser.setMinVerticalRotation(-FastMath.HALF_PI);
        chaser.setMaxDistance(1000);
        chaser.setSmoothMotion(true);
        chaser.setRotationSensitivity(10);
        chaser.setZoomSensitivity(5);

        return chaser;
    }

    /**
     * @return источник света для chase камеры.
     */
    protected DirectionalLight createLightForChaseCamera() {

        final DirectionalLight directionalLight = new DirectionalLight();
        directionalLight.setColor(ColorRGBA.White);

        return directionalLight;
    }

    /**
     * @return узел на который должна смотреть камера.
     */
    protected Node getNodeForChaseCamera() {
        return stateNode;
    }

    /**
     * @return источник света для chase камеры.
     */
    protected DirectionalLight getLightForChaseCamera() {
        return lightForChaseCamera;
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        final ChaseCamera chaseCamera = getChaseCamera();
        final DirectionalLight lightForChaseCamera = getLightForChaseCamera();

        if (chaseCamera != null && lightForChaseCamera != null && needUpdateChaseCameraLight()) {
            final Camera camera = EDITOR.getCamera();
            lightForChaseCamera.setDirection(camera.getDirection());
        }
    }

    /**
     * @return нужно ли обновлять направление света.
     */
    protected boolean needUpdateChaseCameraLight() {
        return false;
    }
}
