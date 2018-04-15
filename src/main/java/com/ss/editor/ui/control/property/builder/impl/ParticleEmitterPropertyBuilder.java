package com.ss.editor.ui.control.property.builder.impl;

import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.impl.particle.ParticleEmitterImagesModelPropertyControl;
import com.ss.editor.ui.control.property.builder.PropertyBuilder;
import com.ss.editor.ui.control.property.impl.*;
import com.ss.rlib.fx.util.FXUtils;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * The implementation of the {@link PropertyBuilder} to build property controls for {@link ParticleEmitter} objects.
 *
 * @author JavaSaBr
 */
public class ParticleEmitterPropertyBuilder extends AbstractPropertyBuilder<ModelChangeConsumer> {

    @NotNull
    private static final ParticleMesh.Type[] MESH_TYPES = ParticleMesh.Type.values();

    @NotNull
    private static final BiConsumer<ParticleEmitter, Vector2f> APPLY_SIZE_HANDLER = (emitter, result) -> {
        emitter.setStartSize(result.getX());
        emitter.setEndSize(result.getY());
        emitter.killAllParticles();
    };

    @NotNull
    private static final BiConsumer<ParticleEmitter, Vector2f> APPLY_LIFE_HANDLER = (emitter, result) -> {
        emitter.setLowLife(result.getX());
        emitter.setHighLife(result.getY());
        emitter.killAllParticles();
    };

    @NotNull
    private static final BiConsumer<ParticleEmitter, Vector2f> APPLY_IMAGES_HANDLER = (emitter, result) -> {
        emitter.setImagesX((int) result.getX());
        emitter.setImagesY((int) result.getY());
        emitter.killAllParticles();
    };

    @NotNull
    private static final Function<ParticleEmitter, Vector2f> SYNC_SIZE_HANDLER = emitter ->
            new Vector2f(emitter.getStartSize(), emitter.getEndSize());

    @NotNull
    private static final Function<ParticleEmitter, Vector2f> SYNC_LIFE_HANDLER = emitter ->
            new Vector2f(emitter.getLowLife(), emitter.getHighLife());

    @NotNull
    private static final Function<ParticleEmitter, Vector2f> SYNC_IMAGES_HANDLER = emitter ->
            new Vector2f(emitter.getImagesX(), emitter.getImagesY());

    @NotNull
    private static final Function<ParticleEmitter, Vector3f> SYNC_FACE_NORMAL_HANDLER = emitter -> {
        final Vector3f faceNormal = emitter.getFaceNormal();
        return faceNormal == null ? new Vector3f() : faceNormal;
    };

    @NotNull
    private static final PropertyBuilder INSTANCE = new ParticleEmitterPropertyBuilder();

    @FromAnyThread
    public static @NotNull PropertyBuilder getInstance() {
        return INSTANCE;
    }

    private ParticleEmitterPropertyBuilder() {
        super(ModelChangeConsumer.class);
    }

    @Override
    @FxThread
    protected void buildForImpl(@NotNull final Object object, @Nullable final Object parent,
                                @NotNull final VBox container, @NotNull final ModelChangeConsumer changeConsumer) {

        if (!(object instanceof ParticleEmitter)) {
            return;
        }

        buildFor(container, changeConsumer, (ParticleEmitter) object);
        buildSplitLine(container);
    }

    @FxThread
    private void buildFor(@NotNull final VBox container, @NotNull final ModelChangeConsumer changeConsumer,
                          @NotNull final ParticleEmitter emitter) {

        final boolean facingVelocity = emitter.isFacingVelocity();
        final boolean enabled = emitter.isEnabled();
        final boolean inWorldSpace = emitter.isInWorldSpace();
        final boolean randomAngle = emitter.isRandomAngle();
        final boolean selectRandomImage = emitter.isSelectRandomImage();

        final float startSize = emitter.getStartSize();
        final float endSize = emitter.getEndSize();

        final float lowLife = emitter.getLowLife();
        final float highLife = emitter.getHighLife();

        final int imagesX = emitter.getImagesX();
        final int imagesY = emitter.getImagesY();

        final int maxNumParticles = emitter.getMaxNumParticles();
        final float rotateSpeed = emitter.getRotateSpeed();

        final ColorRGBA startColor = emitter.getStartColor();
        final ColorRGBA endColor = emitter.getEndColor();

        final ParticleMesh.Type meshType = emitter.getMeshType();

        final Vector3f gravity = emitter.getGravity();
        final Vector3f faceNormal = emitter.getFaceNormal() == null? new Vector3f() : emitter.getFaceNormal();

        final BooleanPropertyControl<ModelChangeConsumer, ParticleEmitter> enabledControl =
                new BooleanPropertyControl<>(enabled, Messages.MODEL_PROPERTY_IS_ENABLED, changeConsumer);
        enabledControl.setApplyHandler(ParticleEmitter::setEnabled);
        enabledControl.setSyncHandler(ParticleEmitter::isEnabled);
        enabledControl.setEditObject(emitter);

        final BooleanPropertyControl<ModelChangeConsumer, ParticleEmitter> facingVelocityControl =
                new BooleanPropertyControl<>(facingVelocity, Messages.MODEL_PROPERTY_IS_FACING_VELOCITY, changeConsumer);
        facingVelocityControl.setApplyHandler(ParticleEmitter::setFacingVelocity);
        facingVelocityControl.setSyncHandler(ParticleEmitter::isFacingVelocity);
        facingVelocityControl.setEditObject(emitter);

        final BooleanPropertyControl<ModelChangeConsumer, ParticleEmitter> inWorldSpaceControl =
                new BooleanPropertyControl<>(inWorldSpace, Messages.MODEL_PROPERTY_IS_IN_WORLD_SPACE, changeConsumer);
        inWorldSpaceControl.setApplyHandler(ParticleEmitter::setInWorldSpace);
        inWorldSpaceControl.setSyncHandler(ParticleEmitter::isInWorldSpace);
        inWorldSpaceControl.setEditObject(emitter);

        final BooleanPropertyControl<ModelChangeConsumer, ParticleEmitter> randomAngleControl =
                new BooleanPropertyControl<>(randomAngle, Messages.MODEL_PROPERTY_IS_RANDOM_ANGLE, changeConsumer);
        randomAngleControl.setApplyHandler(ParticleEmitter::setRandomAngle);
        randomAngleControl.setSyncHandler(ParticleEmitter::isRandomAngle);
        randomAngleControl.setEditObject(emitter);

        final BooleanPropertyControl<ModelChangeConsumer, ParticleEmitter> selectRandomImageControl =
                new BooleanPropertyControl<>(selectRandomImage, Messages.MODEL_PROPERTY_IS_SELECT_RANDOM_IMAGE, changeConsumer);
        selectRandomImageControl.setApplyHandler(ParticleEmitter::setSelectRandomImage);
        selectRandomImageControl.setSyncHandler(ParticleEmitter::isSelectRandomImage);
        selectRandomImageControl.setEditObject(emitter);

        final IntegerPropertyControl<ModelChangeConsumer, ParticleEmitter> maxNumParticlesControl =
                new IntegerPropertyControl<>(maxNumParticles, Messages.MODEL_PROPERTY_MAX_PARTICLES, changeConsumer);
        maxNumParticlesControl.setApplyHandler(ParticleEmitter::setNumParticles);
        maxNumParticlesControl.setSyncHandler(ParticleEmitter::getMaxNumParticles);
        maxNumParticlesControl.setEditObject(emitter);

        final FloatPropertyControl<ModelChangeConsumer, ParticleEmitter> rotateSpeedControl =
                new FloatPropertyControl<>(rotateSpeed, Messages.MODEL_PROPERTY_ROTATE_SPEED, changeConsumer);
        rotateSpeedControl.setApplyHandler(ParticleEmitter::setRotateSpeed);
        rotateSpeedControl.setSyncHandler(ParticleEmitter::getRotateSpeed);
        rotateSpeedControl.setEditObject(emitter);

        final EnumPropertyControl<ModelChangeConsumer, ParticleEmitter, ParticleMesh.Type> meshTypeControl =
                new EnumPropertyControl<>(meshType, Messages.MODEL_PROPERTY_MESH_TYPE, changeConsumer, MESH_TYPES);
        meshTypeControl.setApplyHandler(ParticleEmitter::setMeshType);
        meshTypeControl.setSyncHandler(ParticleEmitter::getMeshType);
        meshTypeControl.setEditObject(emitter);

        final ColorPropertyControl<ModelChangeConsumer, ParticleEmitter> startColorControl =
                new ColorPropertyControl<>(startColor, Messages.MODEL_PROPERTY_START_COLOR, changeConsumer);
        startColorControl.setApplyHandler(ParticleEmitter::setStartColor);
        startColorControl.setSyncHandler(ParticleEmitter::getStartColor);
        startColorControl.setEditObject(emitter);

        final ColorPropertyControl<ModelChangeConsumer, ParticleEmitter> endColorControl =
                new ColorPropertyControl<>(endColor, Messages.MODEL_PROPERTY_END_COLOR, changeConsumer);
        endColorControl.setApplyHandler(ParticleEmitter::setEndColor);
        endColorControl.setSyncHandler(ParticleEmitter::getEndColor);
        endColorControl.setEditObject(emitter);

        final MinMaxPropertyControl<ModelChangeConsumer, ParticleEmitter> sizeControl =
                new MinMaxPropertyControl<>(new Vector2f(startSize, endSize), Messages.MODEL_PROPERTY_SIZE, changeConsumer);
        sizeControl.setApplyHandler(APPLY_SIZE_HANDLER);
        sizeControl.setSyncHandler(SYNC_SIZE_HANDLER);
        sizeControl.setEditObject(emitter);

        final MinMaxPropertyControl<ModelChangeConsumer, ParticleEmitter> lifeControl =
                new MinMaxPropertyControl<>(new Vector2f(lowLife, highLife), Messages.MODEL_PROPERTY_LIFE, changeConsumer);
        lifeControl.setApplyHandler(APPLY_LIFE_HANDLER);
        lifeControl.setSyncHandler(SYNC_LIFE_HANDLER);
        lifeControl.setEditObject(emitter);

        final ParticleEmitterImagesModelPropertyControl imagesControl =
                new ParticleEmitterImagesModelPropertyControl(new Vector2f(imagesX, imagesY), Messages.MODEL_PROPERTY_SPRITE_COUNT, changeConsumer);
        imagesControl.setApplyHandler(APPLY_IMAGES_HANDLER);
        imagesControl.setSyncHandler(SYNC_IMAGES_HANDLER);
        imagesControl.setEditObject(emitter);

        final Vector3fPropertyControl<ModelChangeConsumer, ParticleEmitter> gravityControl =
                new Vector3fPropertyControl<>(gravity, Messages.MODEL_PROPERTY_GRAVITY, changeConsumer);
        gravityControl.setApplyHandler(ParticleEmitter::setGravity);
        gravityControl.setSyncHandler(ParticleEmitter::getGravity);
        gravityControl.setEditObject(emitter);

        final Vector3fPropertyControl<ModelChangeConsumer, ParticleEmitter> faceNormalControl =
                new Vector3fPropertyControl<>(faceNormal, Messages.MODEL_PROPERTY_FACE_NORMAL, changeConsumer);
        faceNormalControl.setApplyHandler(ParticleEmitter::setFaceNormal);
        faceNormalControl.setSyncHandler(SYNC_FACE_NORMAL_HANDLER);
        faceNormalControl.setEditObject(emitter);

        FXUtils.addToPane(enabledControl, container);
        FXUtils.addToPane(facingVelocityControl, container);
        FXUtils.addToPane(inWorldSpaceControl, container);
        FXUtils.addToPane(randomAngleControl, container);
        FXUtils.addToPane(selectRandomImageControl, container);
        FXUtils.addToPane(maxNumParticlesControl, container);
        FXUtils.addToPane(rotateSpeedControl, container);
        FXUtils.addToPane(meshTypeControl, container);
        FXUtils.addToPane(startColorControl, container);
        FXUtils.addToPane(endColorControl, container);
        FXUtils.addToPane(sizeControl, container);
        FXUtils.addToPane(lifeControl, container);
        FXUtils.addToPane(imagesControl, container);

        buildSplitLine(container);

        FXUtils.addToPane(gravityControl, container);
        FXUtils.addToPane(faceNormalControl, container);
    }
}
