package com.ss.editor.control.painting.spawn;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.control.painting.PaintingInput;
import com.ss.editor.control.painting.impl.AbstractPaintingControl;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.part3d.editor.impl.scene.AbstractSceneEditor3DPart;
import com.ss.editor.ui.component.painting.spawn.SpawnPaintingComponent;
import com.ss.editor.util.GeomUtils;
import com.ss.editor.util.LocalObjects;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

/**
 * The implementation of spawn tool.
 *
 * @author JavaSaBr
 */
public class SpawnToolControl extends AbstractPaintingControl<SpawnPaintingComponent> {

    /**
     * The list of spawned models.
     */
    @NotNull
    private final Array<Spatial> spawnedModels;

    /**
     * The spawned model.
     */
    @Nullable
    private Spatial spawnedModel;

    private float time;

    public SpawnToolControl(@NotNull final SpawnPaintingComponent component) {
        super(component);
        this.spawnedModels = ArrayFactory.newArray(Spatial.class);
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
     * Set the spawned model.
     *
     * @param spawnedModel the spawned model.
     */
    @JmeThread
    public void setSpawnedModel(@Nullable final Spatial spawnedModel) {
        this.spawnedModel = spawnedModel;
    }

    /**
     * Get the spawned model.
     *
     * @return the the spawned model.
     */
    @JmeThread
    private @Nullable Spatial getSpawnedModel() {
        return spawnedModel;
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
    public void startPainting(@NotNull final PaintingInput input, @NotNull final Quaternion brushRotation,
                              @NotNull final Vector3f contactPoint) {

        final Spatial spawnedModel = getSpawnedModel();
        if (spawnedModel == null) {
            return;
        }

        super.startPainting(input, brushRotation, contactPoint);

        time = 0;
    }

    @Override
    @JmeThread
    public void updatePainting(@NotNull final Quaternion brushRotation, @NotNull final Vector3f contactPoint,
                               final float tpf) {

        time += (tpf * 10F);

        if (time > getBrushPower()) {
            time = 0;
            return;
        }

        final PaintingInput currentInput = notNull(getCurrentInput());

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
    protected void spawn(@NotNull final Quaternion brushRotation, @NotNull final Vector3f contactPoint) {

        final float brushSize = getBrushSize();
        final float brushPower = getBrushPower();

        final ThreadLocalRandom random = ThreadLocalRandom.current();
        final LocalObjects local = getLocalObjects();
        final Vector3f spawnPosition = local.nextVector();

        final Node paintedModel = getPaintedModel();
        final Vector3f direction = GeomUtils.getDirection(brushRotation, local.nextVector())
                .negateLocal()
                .multLocal(10);

        final Vector3f sourcePoint = contactPoint.subtract(direction, local.nextVector());
        final Ray ray = local.nextRay();
        ray.setOrigin(sourcePoint);

        final Vector3f resultPosition = local.nextVector();
        final CollisionResults collisions = local.nextCollisionResults();

        for (int i = 0, max = 0; i < brushPower && max < 100; i++, max++) {
            collisions.clear();

            final float x = nextOffset(brushSize, random);
            final float y = nextOffset(brushSize, random);
            final float z = nextOffset(brushSize, random);

            spawnPosition.set(x, y, z)
                    .addLocal(contactPoint)
                    .subtractLocal(sourcePoint)
                    .normalizeLocal();

            ray.setDirection(spawnPosition);

            paintedModel.collideWith(ray, collisions);

            final CollisionResult closest = collisions.getClosestCollision();
            if (closest == null || contactPoint.distance(closest.getContactPoint()) > brushSize / 2) {
                i--;
                continue;
            }

            resultPosition.set(closest.getContactPoint())
                    .subtractLocal(paintedModel.getWorldTranslation());

            final Spatial clone = spawnedModel.clone();
            clone.setUserData(AbstractSceneEditor3DPart.KEY_IGNORE_RAY_CAST, Boolean.TRUE);
            clone.setLocalTranslation(resultPosition);

            paintedModel.attachChild(clone);
        }
    }

    @JmeThread
    protected float nextOffset(final float brushSize, @NotNull final ThreadLocalRandom random) {
        float result = random.nextInt(0, (int) (brushSize * 100)) / 100F;
        result /= 2F;
        return random.nextBoolean() ? result * -1 : result;
    }

    @Override
    public void finishPainting(@NotNull final Quaternion brushRotation, @NotNull final Vector3f contactPoint) {
        super.finishPainting(brushRotation, contactPoint);
    }
}
