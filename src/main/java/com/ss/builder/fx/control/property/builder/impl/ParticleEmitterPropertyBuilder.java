package com.ss.builder.fx.control.property.builder.impl;

import static com.ss.editor.extension.property.EditablePropertyType.*;
import static com.ss.rlib.common.util.ObjectUtils.ifNull;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.effect.ParticleEmitter;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.editor.extension.property.*;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.fx.control.property.builder.PropertyBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * The implementation of the {@link PropertyBuilder} to build property controls for {@link ParticleEmitter} objects.
 *
 * @author JavaSaBr
 */
public class ParticleEmitterPropertyBuilder extends EditableModelObjectPropertyBuilder {

    @FunctionalInterface
    private interface ChangeHandler<T> extends Setter<ParticleEmitter, T> {

        @Override
        default void set(@NotNull ParticleEmitter emitter, @Nullable T value) {
            setImpl(emitter, notNull(value));
            emitter.killAllParticles();
        }

        void setImpl(@NotNull ParticleEmitter emitter, @NotNull T value);
    }

    private static final Getter<ParticleEmitter, Vector2f> SIZE_GETTER =
            emitter -> new Vector2f(emitter.getStartSize(), emitter.getEndSize());
    private static final Getter<ParticleEmitter, Vector2f> LIFE_GETTER =
            emitter -> new Vector2f(emitter.getLowLife(), emitter.getHighLife());
    private static final Getter<ParticleEmitter, Vector2f> SPRITES_GETTER =
            emitter -> new Vector2f(emitter.getImagesX(), emitter.getImagesY());

    private static final ChangeHandler<Vector2f> SIZE_SETTER = (emitter, size) -> {
        emitter.setStartSize(size.getX());
        emitter.setEndSize(size.getY());
    };

    private static final ChangeHandler<Vector2f> LIFE_SETTER = (emitter, size) -> {
        emitter.setLowLife(size.getX());
        emitter.setHighLife(size.getY());
    };

    private static final ChangeHandler<Vector2f> SPRITES_SETTER = (emitter, size) -> {
        emitter.setImagesX((int) size.getX());
        emitter.setImagesY((int) size.getY());
    };

    private static final PropertyBuilder INSTANCE = new ParticleEmitterPropertyBuilder();

    @FromAnyThread
    public static @NotNull PropertyBuilder getInstance() {
        return INSTANCE;
    }

    private ParticleEmitterPropertyBuilder() {
        super(ModelChangeConsumer.class);
    }

    @Override
    protected @Nullable List<EditableProperty<?, ?>> getProperties(@NotNull Object object) {

        if (!(object instanceof ParticleEmitter)) {
            return null;
        }

        var emitter = (ParticleEmitter) object;

        var result = new ArrayList<EditableProperty<?, ?>>();
        result.add(new SimpleProperty<>(BOOLEAN, Messages.MODEL_PROPERTY_IS_ENABLED, emitter,
                ParticleEmitter::isEnabled, ParticleEmitter::setEnabled));
        result.add(new SimpleProperty<>(BOOLEAN, Messages.MODEL_PROPERTY_IS_FACING_VELOCITY, emitter,
                ParticleEmitter::isFacingVelocity, ParticleEmitter::setFacingVelocity));
        result.add(new SimpleProperty<>(BOOLEAN, Messages.MODEL_PROPERTY_IS_IN_WORLD_SPACE, emitter,
                ParticleEmitter::isInWorldSpace, ParticleEmitter::setInWorldSpace));
        result.add(new SimpleProperty<>(BOOLEAN, Messages.MODEL_PROPERTY_IS_RANDOM_ANGLE, emitter,
                ParticleEmitter::isRandomAngle, ParticleEmitter::setRandomAngle));
        result.add(new SimpleProperty<>(BOOLEAN, Messages.MODEL_PROPERTY_IS_SELECT_RANDOM_IMAGE, emitter,
                ParticleEmitter::isSelectRandomImage, ParticleEmitter::setSelectRandomImage));

        result.add(new SimpleProperty<>(INTEGER, Messages.MODEL_PROPERTY_MAX_PARTICLES, 1F, 0, Integer.MAX_VALUE, emitter,
                ParticleEmitter::getMaxNumParticles, ParticleEmitter::setNumParticles));
        result.add(new SimpleProperty<>(FLOAT, Messages.MODEL_PROPERTY_ROTATE_SPEED, emitter,
                ParticleEmitter::getRotateSpeed, ParticleEmitter::setRotateSpeed));
        result.add(new SimpleProperty<>(ENUM, Messages.MODEL_PROPERTY_MESH_TYPE, emitter,
                ParticleEmitter::getMeshType, ParticleEmitter::setMeshType));

        result.add(new SimpleProperty<>(COLOR, Messages.MODEL_PROPERTY_START_COLOR, emitter,
                ParticleEmitter::getStartColor, ParticleEmitter::setStartColor));
        result.add(new SimpleProperty<>(COLOR, Messages.MODEL_PROPERTY_END_COLOR, emitter,
                ParticleEmitter::getEndColor, ParticleEmitter::setEndColor));

        result.add(new SimpleProperty<>(VECTOR_2F, Messages.MODEL_PROPERTY_SIZE, 1F, 0F, 100F, emitter,
                SIZE_GETTER, SIZE_SETTER));
        result.add(new SimpleProperty<>(MIN_MAX_2F, Messages.MODEL_PROPERTY_LIFE, 1F, 0F, 100F, emitter,
                LIFE_GETTER, LIFE_SETTER));
        result.add(new SimpleProperty<>(VECTOR_2F, Messages.MODEL_PROPERTY_SPRITE_COUNT, 1F, 1, 100F, emitter,
                SPRITES_GETTER, SPRITES_SETTER));

        result.add(new SimpleProperty<>(VECTOR_3F, Messages.MODEL_PROPERTY_GRAVITY, emitter,
                ParticleEmitter::getGravity, ParticleEmitter::setGravity));
        result.add(new SimpleProperty<>(VECTOR_3F, Messages.MODEL_PROPERTY_FACE_NORMAL, emitter,
                em -> ifNull(em.getFaceNormal(), Vector3f::new), ParticleEmitter::setFaceNormal));

        result.add(SeparatorProperty.getInstance());

        return result;
    }

    @Override
    public int getPriority() {
        return SpatialPropertyBuilder.PRIORITY + 1;
    }
}
