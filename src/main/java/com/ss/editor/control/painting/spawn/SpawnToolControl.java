package com.ss.editor.control.painting.spawn;

import static com.ss.editor.part3d.editor.impl.scene.AbstractSceneEditor3DPart.KEY_IGNORE_RAY_CAST;
import static com.ss.editor.util.EditorUtil.getAssetManager;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import static com.ss.rlib.common.util.array.ArrayCollectors.toArray;
import com.jme3.asset.ModelKey;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.AssetLinkNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.Terrain;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.control.painting.PaintingInput;
import com.ss.editor.control.painting.impl.AbstractPaintingControl;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AddChildOperation;
import com.ss.editor.ui.component.painting.spawn.SpawnPaintingComponent;
import com.ss.editor.util.GeomUtils;
import com.ss.editor.util.NodeUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import jme3tools.optimize.GeometryBatchFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The implementation of spawn tool.
 *
 * @author JavaSaBr
 */
public class SpawnToolControl extends AbstractPaintingControl<SpawnPaintingComponent> {

    public enum SpawnMethod {
        AS_IS(Messages.PAINTING_COMPONENT_SPAWN_MODELS_METHOD_AS_IS),
        BATCH(Messages.PAINTING_COMPONENT_SPAWN_MODELS_METHOD_BATCH),
        LINK(Messages.PAINTING_COMPONENT_SPAWN_MODELS_METHOD_LINK);

        @NotNull
        private final String label;

        SpawnMethod(@NotNull final String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    /**
     * The list of spawned models.
     */
    @NotNull
    private final Array<Spatial> spawnedModels;

    /**
     * The list of examples model.
     */
    @NotNull
    private final Array<Spatial> examples;

    /**
     * The models min minScale.
     */
    @NotNull
    private final Vector3f minScale;

    /**
     * The models max minScale.
     */
    @NotNull
    private final Vector3f maxScale;

    /**
     * The models padding.
     */
    @NotNull
    private final Vector3f padding;

    /**
     * The spawn method.
     */
    @NotNull
    private SpawnMethod method;

    /**
     * The painting time.
     */
    private float time;

    public SpawnToolControl(@NotNull SpawnPaintingComponent component) {
        super(component);
        this.spawnedModels = ArrayFactory.newArray(Spatial.class);
        this.examples = ArrayFactory.newArray(Spatial.class);
        this.method = SpawnMethod.BATCH;
        this.minScale = Vector3f.UNIT_XYZ.clone();
        this.maxScale = Vector3f.UNIT_XYZ.clone();
        this.padding = Vector3f.ZERO.clone();
    }

    /**
     * Get the spawn method.
     *
     * @return the spawn method.
     */
    @JmeThread
    public @NotNull SpawnMethod getMethod() {
        return method;
    }

    /**
     * Set the spawn method.
     *
     * @param method the spawn method.
     */
    @JmeThread
    public void setMethod(@NotNull SpawnMethod method) {
        this.method = method;
    }

    /**
     * Get the models min scale.
     *
     * @return the models min scale.
     */
    @JmeThread
    private @NotNull Vector3f getMinScale() {
        return minScale;
    }

    /**
     * Get the models max scale.
     *
     * @return the models max scale.
     */
    @JmeThread
    private @NotNull Vector3f getMaxScale() {
        return maxScale;
    }

    /**
     * Get the models padding.
     *
     * @return the models padding.
     */
    @JmeThread
    private @NotNull Vector3f getPadding() {
        return minScale;
    }

    /**
     * Set the models min scale.
     *
     * @param minScale the models min scale.
     */
    @JmeThread
    public void setMinScale(@NotNull Vector3f minScale) {
        this.minScale.set(minScale);
    }

    /**
     * Set the models min scale.
     *
     * @param minScale the models min scale.
     */
    @JmeThread
    public void setMaxScale(@NotNull Vector3f minScale) {
        this.maxScale.set(minScale);
    }

    /**
     * Set the models min scale.
     *
     * @param padding the models padding.
     */
    @JmeThread
    public void setPadding(@NotNull Vector3f padding) {
        this.padding.set(padding);
    }

    @Override
    @FromAnyThread
    protected @NotNull ColorRGBA getBrushColor() {
        return ColorRGBA.Orange;
    }

    /**
     * Get the list of spawned models.
     *
     * @return the list of spawned models.
     */
    @JmeThread
    private @NotNull Array<Spatial> getSpawnedModels() {
        return spawnedModels;
    }

    /**
     * Update available examples.
     *
     * @param examples the list of available examples.
     */
    @JmeThread
    public void updateExamples(@NotNull Array<Spatial> examples) {
        this.examples.clear();
        this.examples.addAll(examples);
    }

    /**
     * Get the list of examples model.
     *
     * @return the list of examples model.
     */
    @JmeThread
    private @NotNull Array<Spatial> getExamples() {
        return examples;
    }

    /**
     * Get the change consumer.
     *
     * @return the change consumer.
     */
    @FromAnyThread
    protected @NotNull ModelChangeConsumer getChangeConsumer() {
        return component.getChangeConsumer();
    }

    @Override
    @JmeThread
    public @Nullable Node getPaintedModel() {
        return component.getPaintedObject();
    }

    @Override
    public void startPainting(
            @NotNull PaintingInput input,
            @NotNull Quaternion brushRotation,
            @NotNull Vector3f contactPoint
    ) {

        var spawnedModel = getExamples();
        if (spawnedModel.isEmpty()) {
            return;
        }

        super.startPainting(input, brushRotation, contactPoint);

        getSpawnedModels().clear();
        time = 0;
    }

    @Override
    @JmeThread
    public void updatePainting(@NotNull Quaternion brushRotation, @NotNull Vector3f contactPoint, float tpf) {

        time += (tpf * 10F);

        if (time > getBrushPower()) {
            time = 0;
            return;
        }

        var currentInput = notNull(getCurrentInput());

        switch (currentInput) {
            case MOUSE_PRIMARY: {
                spawn(brushRotation, contactPoint);
                break;
            }
        }
    }

    /**
     * Spawn models.
     *
     * @param brushRotation the brush rotation.
     * @param contactPoint  the contact point.
     */
    @JmeThread
    protected void spawn(@NotNull Quaternion brushRotation, @NotNull Vector3f contactPoint) {

        var brushSize = getBrushSize();

        var random = ThreadLocalRandom.current();
        var local = getLocalObjects();
        var spawnPosition = local.nextVector();

        var paintedModel = getPaintedModel();
        var direction = GeomUtils.getDirection(brushRotation, local.nextVector())
                .negateLocal()
                .multLocal(10);

        var sourcePoint = contactPoint.subtract(direction, local.nextVector());
        var ray = local.nextRay();
        ray.setOrigin(sourcePoint);

        var minScale = getMinScale();
        var maxScale = getMaxScale();
        var padding = getPadding();

        var resultPosition = local.nextVector();
        var collisions = local.nextCollisionResults();
        var spawnedCollisions = local.nextCollisionResults();
        var resultScale = local.nextVector();
        var needCalculateScale = !minScale.equals(maxScale);

        var maxCount = (int) Math.max(getBrushPower() / 2F, 1F);
        var spawnedModels = getSpawnedModels();

        for(var count = 0; count < maxCount; count++) {
            for (var attempts = 0; attempts < 10; attempts++, attempts++) {

                collisions.clear();
                spawnedCollisions.clear();

                var x = nextOffset(brushSize, random);
                var y = nextOffset(brushSize, random);
                var z = nextOffset(brushSize, random);

                spawnPosition.set(x, y, z)
                        .addLocal(contactPoint)
                        .subtractLocal(sourcePoint)
                        .normalizeLocal();

                ray.setDirection(spawnPosition);

                paintedModel.collideWith(ray, collisions);

                var closest = collisions.getClosestCollision();
                if (closest == null || contactPoint.distance(closest.getContactPoint()) > brushSize / 2) {
                    continue;
                }

                resultPosition.set(closest.getContactPoint())
                        .subtractLocal(paintedModel.getWorldTranslation());

                Spatial clone = examples.get(random.nextInt(0, examples.size())).clone();
                clone.setUserData(KEY_IGNORE_RAY_CAST, Boolean.TRUE);
                clone.setLocalTranslation(resultPosition);

                if (needCalculateScale) {
                    clone.setLocalScale(nextScale(minScale, maxScale, resultScale, random));
                } else {
                    clone.setLocalScale(minScale);
                }

                clone.updateModelBound();

                var worldBound = clone.getWorldBound();

                if (!Vector3f.ZERO.equals(padding)) {
                    worldBound = addPadding(worldBound, padding);
                }

                if (paintedModel.collideWith(worldBound, spawnedCollisions) > 2) {
                    continue;
                }

                spawnedModels.add(clone);
                paintedModel.attachChild(clone);
                break;
            }
        }
    }

    protected BoundingVolume addPadding(@NotNull BoundingVolume boundingVolume, @NotNull Vector3f padding) {

        if (boundingVolume instanceof BoundingBox) {
            var box = (BoundingBox) boundingVolume;
            var xExtent = box.getXExtent() + padding.getX();
            var yExtent = box.getYExtent() + padding.getY();
            var zExtent = box.getZExtent() + padding.getZ();
            return new BoundingBox(box.getCenter(), xExtent, yExtent, zExtent);
        }

        return boundingVolume;
    }

    /**
     * Calculate a new random scale.
     *
     * @param minScale the min scale.
     * @param maxScale the max scale.
     * @param result   the result vector.
     * @param random   the random.
     * @return the result vector.
     */
    protected Vector3f nextScale(
            @NotNull Vector3f minScale,
            @NotNull Vector3f maxScale,
            @NotNull Vector3f result,
            @NotNull Random random
    ) {

        float newX = nextScale(random, minScale.getX(), maxScale.getX());
        float newY = nextScale(random, minScale.getX(), maxScale.getX());
        float newZ = nextScale(random, minScale.getX(), maxScale.getX());

        return result.set(newX, newY, newZ);
    }

    protected float nextScale(@NotNull Random random, float min, float max) {
        int minInt = (int) (Math.min(min, max) * 1000);
        int maxInt = (int) (Math.max(max, min) * 1000);
        int added = random.nextInt(maxInt - minInt);
        return Math.min(min, max) + (added / 1000F);
    }

    /**
     * Calculate next random offset.
     *
     * @param brushSize the brush size.
     * @param random    the random.
     * @return the new coordinate.
     */
    @JmeThread
    protected float nextOffset(float brushSize, @NotNull ThreadLocalRandom random) {
        float result = random.nextInt(0, (int) (brushSize * 100)) / 100F;
        result /= 2F;
        return random.nextBoolean() ? result * -1 : result;
    }

    @Override
    @JmeThread
    public void finishPainting(@NotNull Quaternion brushRotation, @NotNull Vector3f contactPoint) {
        super.finishPainting(brushRotation, contactPoint);

        var spawnedModels = getSpawnedModels();
        if (spawnedModels.isEmpty()) {
            return;
        }

        var local = getLocalObjects();
        var location = local.nextVector();
        var offset = local.nextVector()
                .set(contactPoint);

        spawnedModels.stream().peek(Spatial::removeFromParent)
                .forEach(sp -> sp.setUserData(KEY_IGNORE_RAY_CAST, null));

        var paintedModel = notNull(getPaintedModel());

        Node parent = paintedModel instanceof Terrain ?
                NodeUtils.findParent(paintedModel, sp -> !(sp instanceof Terrain)) : paintedModel;

        if (parent != paintedModel) {

            var diff = local.nextVector();
            diff.set(parent.getWorldTranslation())
                    .subtractLocal(paintedModel.getWorldTranslation());

            offset.addLocal(diff);
        }

        var changeConsumer = getChangeConsumer();

        var method = getMethod();

        switch (method) {
            case AS_IS: {
                var spawnedNode = new Node("Spawned");
                spawnedNode.setLocalTranslation(contactPoint);
                spawnedModels.forEach(geom -> updatePositionAndAttach(offset, location, spawnedNode, geom));
                spawnedNode.updateModelBound();
                changeConsumer.execute(new AddChildOperation(spawnedNode, parent, false));
                break;
            }
            case LINK: {

                var spawnedNode = new Node("Spawned");
                spawnedNode.setLocalTranslation(contactPoint);
                spawnedModels.stream().map(this::linkSpatial)
                        .forEach(geom -> updatePositionAndAttach(offset, location, spawnedNode, geom));

                spawnedNode.updateModelBound();

                changeConsumer.execute(new AddChildOperation(spawnedNode, parent, false));
                break;
            }
            case BATCH: {

                var spawnedNode = new Node("Spawned");
                spawnedNode.setLocalTranslation(contactPoint);

                var geometries = spawnedModels.stream()
                        .flatMap(NodeUtils::children)
                        .filter(Geometry.class::isInstance)
                        .map(Geometry.class::cast)
                        .collect(toArray(Geometry.class));

                GeometryBatchFactory.makeBatches(geometries)
                        .forEach(geom -> updatePositionAndAttach(offset, location, spawnedNode, geom));

                spawnedNode.updateModelBound();

                changeConsumer.execute(new AddChildOperation(spawnedNode, parent, false));
                break;
            }
        }
    }

    @JmeThread
    protected void updatePositionAndAttach(
            @NotNull Vector3f contactPoint,
            @NotNull Vector3f location,
            @NotNull Node spawnedNode,
            @NotNull Spatial geom
    ) {

        var newPosition = location.set(geom.getLocalTranslation())
                .subtractLocal(contactPoint);

        geom.setLocalTranslation(newPosition);
        spawnedNode.attachChild(geom);
    }

    /**
     * Create an asset link node for the spatial.
     *
     * @param spatial the spatial.
     * @return the asset link node.
     */
    @JmeThread
    protected @NotNull AssetLinkNode linkSpatial(@NotNull Spatial spatial) {
        var linkNode = new AssetLinkNode();
        linkNode.setLocalTranslation(spatial.getLocalTranslation());
        linkNode.setName(spatial.getName());
        linkNode.setLocalScale(getMinScale());
        linkNode.attachLinkedChild(getAssetManager(), (ModelKey) spatial.getKey());
        return linkNode;
    }
}
