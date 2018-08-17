package com.ss.editor.plugin.api.editor;

import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.manager.WorkspaceManager;
import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.model.undo.EditorOperationControl;
import com.ss.editor.model.undo.UndoableEditor;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.component.editor.impl.AbstractFileEditor;
import com.ss.editor.ui.component.editor.state.EditorState;
import javafx.event.Event;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * The base implementation of {@link com.ss.editor.ui.component.editor.FileEditor}.
 *
 * @author JavaSaBr
 */
public abstract class BaseFileEditor<S extends EditorState> extends AbstractFileEditor<StackPane> implements
        UndoableEditor, ChangeConsumer {

    /**
     * The operation control.
     */
    @NotNull
    protected final EditorOperationControl operationControl;

    /**
     * The changes counter.
     */
    @NotNull
    private final AtomicInteger changeCounter;

    /**
     * The state of this editor.
     */
    @Nullable
    protected S editorState;

    /**
     * The flag for ignoring listeners.
     */
    private boolean ignoreListeners;

    protected BaseFileEditor() {
        this.operationControl = createOperationControl();
        this.changeCounter = new AtomicInteger();
    }

    /**
     * Create an editor operation control.
     *
     * @return the editor operation control.
     */
    @FxThread
    protected @NotNull EditorOperationControl createOperationControl() {
        return new EditorOperationControl(this);
    }

    @Override
    @FromAnyThread
    public void execute(@NotNull final EditorOperation operation) {
        operationControl.execute(operation);
    }

    @FxThread
    @Override
    protected boolean handleKeyActionInFx(
            @NotNull KeyCode keyCode,
            boolean isPressed,
            boolean isControlDown,
            boolean isShiftDown,
            boolean isButtonMiddleDown
    ) {

        if (isPressed && isControlDown && keyCode == KeyCode.Z) {
            undo();
            return true;
        } else if (isPressed && isControlDown && isShiftDown && keyCode == KeyCode.Z) {
            redo();
            return true;
        } else if (isPressed && isControlDown && keyCode == KeyCode.Y) {
            redo();
            return true;
        }

        return super.handleKeyActionInFx(keyCode, isPressed, isControlDown, isShiftDown, isButtonMiddleDown);
    }

    @Override
    @FxThread
    public void incrementChange() {
        var result = changeCounter.incrementAndGet();
        setDirty(result != 0);
    }

    @Override
    @FxThread
    public void decrementChange() {
        var result = changeCounter.decrementAndGet();
        setDirty(result != 0);
    }

    @Override
    @FromAnyThread
    public void redo() {
        operationControl.redo();
    }

    @Override
    @FromAnyThread
    public void undo() {
        operationControl.undo();
    }

    @Override
    @BackgroundThread
    public void openFile(@NotNull Path file) {
        super.openFile(file);

        try {
            doOpenFile(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ExecutorManager.getInstance()
                .addFxTask(this::loadState);
    }

    /**
     * Loading a state of this editor.
     */
    @FxThread
    protected void loadState() {

        var stateFactory = getEditorStateFactory();

        if (stateFactory == null) {
            return;
        }

        var currentWorkspace = WorkspaceManager.getInstance()
                .requiredCurrentWorkspace();

        editorState = currentWorkspace.getEditorState(getFile(), stateFactory);
    }

    /**
     * Get the factory to make an editor state.
     *
     * @return the factory to make an editor state.
     */
    @FxThread
    protected @Nullable Supplier<EditorState> getEditorStateFactory() {
        return null;
    }

    /**
     * Do main activities to open the file.
     *
     * @param file the file to open.
     * @throws IOException if was some problem with writing to the file.
     */
    @BackgroundThread
    protected void doOpenFile(@NotNull Path file) throws IOException {
    }

    /**
     * Set true if need to ignore listeners.
     *
     * @param ignoreListeners true if need to ignore listeners.
     */
    @FromAnyThread
    protected void setIgnoreListeners(boolean ignoreListeners) {
        this.ignoreListeners = ignoreListeners;
    }

    /**
     * Return true if need to ignore listeners.
     *
     * @return true if need to ignore listeners.
     */
    @FromAnyThread
    protected boolean isIgnoreListeners() {
        return ignoreListeners;
    }

    @Override
    @FxThread
    protected @NotNull StackPane createRoot() {
        return new StackPane();
    }

    @Override
    @FxThread
    public @Nullable BorderPane get3dArea() {
        return null;
    }

    /**
     * Get the editor state.
     *
     * @return the editor state.
     */
    @FromAnyThread
    protected @Nullable S getEditorState() {
        return editorState;
    }

    @Override
    @FxThread
    public boolean isInside(double sceneX, double sceneY, @NotNull Class<? extends Event> eventType) {
        return false;
    }
}
