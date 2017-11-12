package com.ss.editor.plugin.api.editor;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.manager.WorkspaceManager;
import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.model.undo.EditorOperationControl;
import com.ss.editor.model.undo.UndoableEditor;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.workspace.Workspace;
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
    private final EditorOperationControl operationControl;

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
    @FXThread
    protected @NotNull EditorOperationControl createOperationControl() {
        return new EditorOperationControl(this);
    }

    @Override
    @FromAnyThread
    public void execute(@NotNull final EditorOperation operation) {
        operationControl.execute(operation);
    }

    @FXThread
    @Override
    protected boolean handleKeyActionImpl(@NotNull final KeyCode keyCode, final boolean isPressed,
                                          final boolean isControlDown, final boolean isShiftDown,
                                          final boolean isButtonMiddleDown) {

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

        return super.handleKeyActionImpl(keyCode, isPressed, isControlDown, isShiftDown, isButtonMiddleDown);
    }

    @Override
    @FXThread
    public void incrementChange() {
        final int result = changeCounter.incrementAndGet();
        setDirty(result != 0);
    }

    @Override
    @FXThread
    public void decrementChange() {
        final int result = changeCounter.decrementAndGet();
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

    /**
     * Get the editor operation control.
     *
     * @return the editor operation control.
     */
    @FromAnyThread
    protected @NotNull EditorOperationControl getOperationControl() {
        return operationControl;
    }

    @Override
    @FXThread
    public void openFile(@NotNull final Path file) {
        super.openFile(file);

        try {
            doOpenFile(file);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        EXECUTOR_MANAGER.addFXTask(this::loadState);
    }

    /**
     * Loading a state of this editor.
     */
    @FXThread
    protected void loadState() {

        final Supplier<EditorState> stateFactory = getEditorStateFactory();
        if (stateFactory == null) {
            return;
        }

        final WorkspaceManager workspaceManager = WorkspaceManager.getInstance();
        final Workspace currentWorkspace = notNull(workspaceManager.getCurrentWorkspace());

        editorState = currentWorkspace.getEditorState(getEditFile(), stateFactory);
    }

    /**
     * Get the factory to make an editor state.
     *
     * @return the factory to make an editor state.
     */
    @FXThread
    protected @Nullable Supplier<EditorState> getEditorStateFactory() {
        return null;
    }

    /**
     * Do main activities to open the file.
     *
     * @param file the file to open.
     * @throws IOException if was some problem with writing to the file.
     */
    @FXThread
    protected void doOpenFile(@NotNull final Path file) throws IOException {
    }

    /**
     * @param ignoreListeners the flag for ignoring listeners.
     */
    @FromAnyThread
    protected void setIgnoreListeners(final boolean ignoreListeners) {
        this.ignoreListeners = ignoreListeners;
    }

    /**
     * @return the flag for ignoring listeners.
     */
    @FromAnyThread
    protected boolean isIgnoreListeners() {
        return ignoreListeners;
    }

    @Override
    @FXThread
    protected @NotNull StackPane createRoot() {
        return new StackPane();
    }

    @Override
    @FXThread
    public @Nullable BorderPane get3DArea() {
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
    @FXThread
    public boolean isInside(final double sceneX, final double sceneY, @NotNull final Class<? extends Event> eventType) {
        return false;
    }
}
