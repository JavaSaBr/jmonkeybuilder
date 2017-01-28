package com.ss.extension.scene.app.state.impl.bullet;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState.ThreadingType;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsSpace.BroadphaseType;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.control.PhysicsControl;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.util.clone.Cloner;
import com.ss.extension.property.EditableProperty;
import com.ss.extension.property.EditablePropertyType;
import com.ss.extension.property.SimpleProperty;
import com.ss.extension.scene.SceneNode;
import com.ss.extension.scene.app.state.EditableSceneAppState;
import com.ss.extension.scene.app.state.impl.bullet.debug.BulletDebugAppState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rlib.logging.Logger;
import rlib.logging.LoggerManager;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * The implementation of an editable bullet state.
 *
 * @author JavaSaBr
 */
public class EditableBulletSceneAppState extends AbstractAppState implements EditableSceneAppState,
        PhysicsTickListener {

    protected static final Logger LOGGER = LoggerManager.getLogger(EditableBulletSceneAppState.class);

    protected final Callable<Boolean> physicsUpdateTask = () -> {
        final PhysicsSpace physicsSpace = getPhysicsSpace();
        if (physicsSpace == null) return false;
        physicsSpace.update(getTpf() * getSpeed());
        return true;
    };

    /***
     * The physics space.
     */
    @Nullable
    protected volatile PhysicsSpace physicsSpace;

    /**
     * The time per frame.
     */
    protected volatile float tpf;

    /**
     * The executor.
     */
    @Nullable
    protected ScheduledExecutorService executor;

    /**
     * The state manager.
     */
    @Nullable
    protected AppStateManager stateManager;

    /**
     * The application.
     */
    @Nullable
    protected Application application;

    /**
     * The debug state.
     */
    @Nullable
    protected BulletDebugAppState debugAppState;

    /**
     * The scene node.
     */
    @Nullable
    protected SceneNode sceneNode;

    /**
     * The threading type.
     */
    @NotNull
    protected ThreadingType threadingType;

    /**
     * The prev threading type.
     */
    @Nullable
    protected ThreadingType prevThreadingType;

    /**
     * The broadphase type.
     */
    @NotNull
    protected BroadphaseType broadphaseType;

    /**
     * The world min.
     */
    @NotNull
    protected Vector3f worldMin;

    /**
     * Thw world max.
     */
    @NotNull
    protected Vector3f worldMax;

    /**
     * The reference to background physics updating.
     */
    protected Future<?> physicsFuture;

    /**
     * The speed.
     */
    protected float speed;

    /**
     * The flag to enable debug.
     */
    protected boolean debugEnabled;

    public EditableBulletSceneAppState() {
        this.threadingType = ThreadingType.SEQUENTIAL;
        this.broadphaseType = BroadphaseType.DBVT;
        this.worldMin = new Vector3f(-10000f, -10000f, -10000f);
        this.worldMax = new Vector3f(10000f, 10000f, 10000f);
        this.debugEnabled = false;
    }

    @Override
    public void setSceneNode(@Nullable final SceneNode sceneNode) {
        this.sceneNode = sceneNode;
    }

    /**
     * @return the scene node.
     */
    @Nullable
    protected SceneNode getSceneNode() {
        return sceneNode;
    }

    /**
     * @param debugEnabled the flag to enable debug.
     */
    public void setDebugEnabled(final boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
        rebuildState();
    }

    /**
     * @return true if debug is enabled.
     */
    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    /**
     * @return the physics space.
     */
    @Nullable
    public PhysicsSpace getPhysicsSpace() {
        return physicsSpace;
    }

    /**
     * @return the speed.
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * @param speed the speed.
     */
    public void setSpeed(final float speed) {
        this.speed = speed;
    }

    /**
     * @return the time per frame.
     */
    public float getTpf() {
        return tpf;
    }

    @NotNull
    @Override
    public String getName() {
        return "Bullet state";
    }

    public void setThreadingType(@NotNull final ThreadingType threadingType) {
        this.prevThreadingType = getPhysicsSpace() != null ? getThreadingType() : null;
        this.threadingType = threadingType;
        rebuildState();
    }

    @NotNull
    public ThreadingType getThreadingType() {
        return threadingType;
    }

    public void setBroadphaseType(@NotNull final BroadphaseType broadphaseType) {
        this.broadphaseType = broadphaseType;
        rebuildState();
    }

    @NotNull
    public BroadphaseType getBroadphaseType() {
        return broadphaseType;
    }

    @NotNull
    public Vector3f getWorldMax() {
        return worldMax;
    }

    public void setWorldMax(@NotNull final Vector3f worldMax) {
        this.worldMax.set(worldMax);
        rebuildState();
    }

    @NotNull
    public Vector3f getWorldMin() {
        return worldMin;
    }

    public void setWorldMin(@NotNull final Vector3f worldMin) {
        this.worldMin.set(worldMin);
        rebuildState();
    }

    @Override
    public void initialize(@NotNull final AppStateManager stateManager, @NotNull final Application app) {
        super.initialize(stateManager, app);
        this.stateManager = stateManager;
        this.application = app;

        startPhysics();

        if (isDebugEnabled()) {
            debugAppState = new BulletDebugAppState(physicsSpace);
            stateManager.attach(debugAppState);
        }

        final SceneNode sceneNode = getSceneNode();
        if (sceneNode != null) {
            updateNode(sceneNode, physicsSpace);
        }
    }

    /**
     * Update a spatial.
     *
     * @param spatial      the spatial.
     * @param physicsSpace the new physical space or null.
     */
    private void updateNode(@NotNull final Spatial spatial, @Nullable final PhysicsSpace physicsSpace) {
        spatial.depthFirstTraversal(sp -> {

            final int numControls = sp.getNumControls();

            for (int i = 0; i < numControls; i++) {
                final Control control = sp.getControl(i);
                if (control instanceof PhysicsControl) {
                    ((PhysicsControl) control).setPhysicsSpace(physicsSpace);
                }
            }
        });
    }

    @Override
    public void cleanup() {
        super.cleanup();

        if (debugAppState != null) {
            stateManager.detach(debugAppState);
            debugAppState = null;
        }

        final SceneNode sceneNode = getSceneNode();
        if (sceneNode != null) {
            updateNode(sceneNode, null);
        }

        stopPhysics();

        this.stateManager = null;
        this.application = null;
    }

    /**
     * Start physics.
     */
    public void startPhysics() {
        if (physicsSpace != null) return;

        if (threadingType == ThreadingType.PARALLEL) {
            startBackgroundPhysics();
        } else {
            physicsSpace = new PhysicsSpace(worldMin, worldMax, broadphaseType);
            physicsSpace.addTickListener(this);
        }

        if (threadingType == ThreadingType.PARALLEL) {
            PhysicsSpace.setLocalThreadPhysicsSpace(physicsSpace);
        }
    }

    /**
     * Start background physics.
     */
    protected boolean startBackgroundPhysics() {

        if (executor == null) {
            executor = Executors.newSingleThreadScheduledExecutor();
        }

        try {
            return executor.submit(() -> {
                physicsSpace = new PhysicsSpace(worldMin, worldMax, broadphaseType);
                physicsSpace.addTickListener(this);
                return true;
            })
                           .get();
        } catch (final InterruptedException | ExecutionException e) {
            LOGGER.warning(e);
            return false;
        }
    }

    /**
     * Rebuild this state.
     */
    protected void rebuildState() {
        if (!isInitialized()) return;

        final SceneNode sceneNode = getSceneNode();

        if (debugAppState != null) {
            stateManager.detach(debugAppState);
            debugAppState = null;
        }

        if (sceneNode != null) {
            updateNode(sceneNode, null);
        }

        stopPhysics();
        startPhysics();

        if (isDebugEnabled()) {
            debugAppState = new BulletDebugAppState(physicsSpace);
            stateManager.attach(debugAppState);
        }

        if (sceneNode != null) {
            updateNode(sceneNode, physicsSpace);
        }
    }

    /**
     * Stop physics.
     */
    public void stopPhysics() {
        if (physicsSpace == null) return;

        if (executor != null) {
            executor.shutdown();
            executor = null;
        }

        final ThreadingType threadingType = prevThreadingType != null ? prevThreadingType : getThreadingType();

        if (threadingType == ThreadingType.PARALLEL) {
            PhysicsSpace.setLocalThreadPhysicsSpace(null);
            prevThreadingType = null;
        }

        physicsSpace.removeTickListener(this);
        physicsSpace.destroy();
        physicsSpace = null;
    }

    @Override
    public void render(@NotNull final RenderManager renderManager) {
        if (!isEnabled()) return;
        switch (threadingType) {
            case PARALLEL: {
                physicsFuture = executor.submit(physicsUpdateTask);
                break;
            }
            case SEQUENTIAL: {
                physicsSpace.update(tpf * speed);
                break;
            }
        }
    }

    @Override
    public void postRender() {
        if (physicsFuture == null) return;
        try {
            physicsFuture.get();
            physicsFuture = null;
        } catch (final InterruptedException | ExecutionException e) {
            LOGGER.warning(e);
        }
    }

    @Override
    public void prePhysicsTick(@NotNull final PhysicsSpace space, final float tpf) {

    }

    @Override
    public void physicsTick(@NotNull final PhysicsSpace space, final float tpf) {

    }

    @Override
    public void update(final float tpf) {
        super.update(tpf);

        final PhysicsSpace physicsSpace = getPhysicsSpace();
        if (physicsSpace != null) {
            physicsSpace.distributeEvents();
        }

        this.tpf = tpf;
    }

    @Override
    public void notifyAdded(@NotNull final Object object) {
        if (object instanceof PhysicsControl) {
            ((PhysicsControl) object).setPhysicsSpace(getPhysicsSpace());
        } else if (object instanceof Spatial) {
            updateNode((Spatial) object, getPhysicsSpace());
        }
    }

    @Override
    public void notifyRemoved(@NotNull final Object object) {
        if (object instanceof PhysicsControl) {
            ((PhysicsControl) object).setPhysicsSpace(null);
        } else if (object instanceof Spatial) {
            updateNode((Spatial) object, null);
        }
    }

    @NotNull
    @Override
    public Array<EditableProperty<?, ?>> getEditableProperties() {

        final Array<EditableProperty<?, ?>> result = ArrayFactory.newArray(EditableProperty.class);

        result.add(new SimpleProperty<>(EditablePropertyType.BOOLEAN, "Debug enabled", this,
                                        EditableBulletSceneAppState::isDebugEnabled,
                                        EditableBulletSceneAppState::setDebugEnabled));
        result.add(new SimpleProperty<>(EditablePropertyType.FLOAT, "Speed", this,
                                        EditableBulletSceneAppState::getSpeed,
                                        EditableBulletSceneAppState::setSpeed));
        result.add(new SimpleProperty<>(EditablePropertyType.ENUM, "Broadphase type", this,
                                        EditableBulletSceneAppState::getBroadphaseType,
                                        EditableBulletSceneAppState::setBroadphaseType));
        result.add(new SimpleProperty<>(EditablePropertyType.ENUM, "Threading type", this,
                                        EditableBulletSceneAppState::getThreadingType,
                                        EditableBulletSceneAppState::setThreadingType));
        result.add(new SimpleProperty<>(EditablePropertyType.VECTOR_3F, "World max", this,
                                        EditableBulletSceneAppState::getWorldMax,
                                        EditableBulletSceneAppState::setWorldMax));
        result.add(new SimpleProperty<>(EditablePropertyType.VECTOR_3F, "World max", this,
                                        EditableBulletSceneAppState::getWorldMin,
                                        EditableBulletSceneAppState::setWorldMin));

        return result;
    }

    @Override
    public Object jmeClone() {
        try {
            return super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cloneFields(@NotNull final Cloner cloner, @NotNull final Object original) {
        sceneNode = cloner.clone(sceneNode);
        worldMin = cloner.clone(worldMin);
        worldMax = cloner.clone(worldMax);
    }

    @Override
    public void write(@NotNull final JmeExporter exporter) throws IOException {
        final OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(sceneNode, "sceneNode", null);
        capsule.write(threadingType, "threadingType", ThreadingType.SEQUENTIAL);
        capsule.write(broadphaseType, "broadphaseType", BroadphaseType.DBVT);
        capsule.write(worldMin, "worldMin", null);
        capsule.write(worldMax, "worldMax", null);
        capsule.write(speed, "speed", 0);
        capsule.write(debugEnabled, "debugEnabled", false);
    }

    @Override
    public void read(@NotNull final JmeImporter importer) throws IOException {
        final InputCapsule capsule = importer.getCapsule(this);
        sceneNode = (SceneNode) capsule.readSavable("sceneNode", null);
        threadingType = capsule.readEnum("threadingType", ThreadingType.class, ThreadingType.SEQUENTIAL);
        broadphaseType = capsule.readEnum("broadphaseType", BroadphaseType.class, BroadphaseType.DBVT);
        worldMin = (Vector3f) capsule.readSavable("worldMin", null);
        worldMax = (Vector3f) capsule.readSavable("worldMax", null);
        speed = capsule.readFloat("speed", 0);
        debugEnabled = capsule.readBoolean("debugEnabled", false);
    }
}
