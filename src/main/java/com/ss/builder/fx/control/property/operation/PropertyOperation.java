package com.ss.builder.ui.control.property.operation;

import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.builder.model.undo.impl.AbstractEditorOperation;
import com.ss.builder.util.EditorUtils;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import com.ss.editor.util.EditorUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

import static com.ss.rlib.common.util.ObjectUtils.notNull;

/**
 * The implementation of the {@link AbstractEditorOperation} to edit properties of objects.
 *
 * @param <C> the type of changed consumer
 * @param <D> the type of edited object
 * @param <T> the type of edited property
 * @author JavaSaBr
 */
public class PropertyOperation<C extends ChangeConsumer, D, T> extends AbstractEditorOperation<C> {

    /**
     * The property name.
     */
    @NotNull
    protected final String propertyName;

    /**
     * The new value of the property.
     */
    @Nullable
    protected final T newValue;

    /**
     * The old value of the property.
     */
    @Nullable
    protected final T oldValue;

    /**
     * The target object.
     */
    @NotNull
    protected final D target;

    /**
     * The handler for applying new value.
     */
    @Nullable
    private volatile BiConsumer<D, T> applyHandler;

    public PropertyOperation(
            @NotNull D target,
            @NotNull String propertyName,
            @Nullable T newValue,
            @Nullable T oldValue
    ) {

        this.newValue = newValue;
        this.oldValue = oldValue;
        this.target = target;
        this.propertyName = propertyName;
    }

    @Override
    @JmeThread
    protected void startInJme(@NotNull C editor) {
        super.startInJme(editor);
        editor.notifyJmePreChangeProperty(target, propertyName);
    }

    @Override
    @JmeThread
    protected void endInJme(@NotNull C editor) {
        super.endInJme(editor);
        editor.notifyJmeChangedProperty(target, propertyName);
    }

    @Override
    @JmeThread
    protected void redoInJme(@NotNull C editor) {
        super.redoInJme(editor);
        apply(target, newValue);
    }

    @Override
    @JmeThread
    protected void undoInJme(@NotNull C editor) {
        super.undoInJme(editor);
        apply(target, oldValue);
    }

    @Override
    @FxThread
    protected void endInFx(@NotNull C editor) {
        super.endInFx(editor);
        editor.notifyFxChangeProperty(target, propertyName);
    }

    /**
     * Sets apply handler.
     *
     * @param applyHandler the handler for applying new value.
     */
    @FromAnyThread
    public void setApplyHandler(@NotNull BiConsumer<D, T> applyHandler) {
        this.applyHandler = applyHandler;
    }

    /**
     * Apply new value of the property to the model.
     *
     * @param spatial the spatial
     * @param value   the value
     */
    @JmeThread
    protected void apply(@NotNull D spatial, @Nullable T value) {
        try {
            notNull(applyHandler).accept(spatial, value);
        } catch (Exception e) {
            EditorUtils.handleException(LOGGER, this, e);
        }
    }
}