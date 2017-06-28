package com.ss.editor.ui.control.model.property.operation;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tonegod.emitter.influencers.ParticleInfluencer;

import java.util.function.BiConsumer;

/**
 * The implementation of the {@link AbstractEditorOperation} to edit {@link ParticleInfluencer}.
 *
 * @param <D> the type of a {@link ParticleInfluencer}
 * @param <T> the type of an edited value.
 * @author JavaSaBr
 */
public class ParticleInfluencerPropertyOperation<D extends ParticleInfluencer, T> extends
        AbstractEditorOperation<ModelChangeConsumer> {

    @NotNull
    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * The property name.
     */
    @NotNull
    private final String propertyName;

    /**
     * The particle influencer.
     */
    private final D influencer;

    /**
     * The parent of the infuencer.
     */
    @NotNull
    private final Object parent;

    /**
     * The new value of the property.
     */
    @Nullable
    private final T newValue;

    /**
     * The old value of the property.
     */
    @Nullable
    private final T oldValue;

    /**
     * The handler for applying new value.
     */
    @Nullable
    private BiConsumer<D, T> applyHandler;

    /**
     * Instantiates a new Particle influencer property operation.
     *
     * @param influencer   the influencer
     * @param parent       the parent
     * @param propertyName the property name
     * @param newValue     the new value
     * @param oldValue     the old value
     */
    public ParticleInfluencerPropertyOperation(@NotNull final D influencer, @NotNull final Object parent,
                                               @NotNull final String propertyName, @Nullable final T newValue,
                                               @Nullable final T oldValue) {
        this.parent = parent;
        this.newValue = newValue;
        this.oldValue = oldValue;
        this.influencer = influencer;
        this.propertyName = propertyName;
    }

    /**
     * Sets apply handler.
     *
     * @param applyHandler the handler for applying new value.
     */
    public void setApplyHandler(@NotNull final BiConsumer<D, T> applyHandler) {
        this.applyHandler = applyHandler;
    }

    @Override
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            apply(influencer, newValue);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyChangeProperty(parent, influencer, propertyName));
        });
    }

    /**
     * Apply new value of the property to the model.
     *
     * @param spatial the spatial
     * @param value   the value
     */
    protected void apply(@NotNull final D spatial, @Nullable final T value) {
        notNull(applyHandler).accept(spatial, value);
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            apply(influencer, oldValue);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyChangeProperty(parent, influencer, propertyName));
        });
    }
}