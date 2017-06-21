package com.ss.editor.model.undo;

import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.manager.ExecutorManager;

import org.jetbrains.annotations.NotNull;

import javafx.application.Platform;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;

/**
 * The implementation of controller to support undo/redo operations.
 *
 * @author JavaSaBr
 */
public class EditorOperationControl {

    private static final int HISTORY_SIZE = 20;

    /**
     * The list of operations.
     */
    @NotNull
    private final Array<EditorOperation> operations;

    /**
     * The list of operations to redo.
     */
    @NotNull
    private final Array<EditorOperation> toRedo;

    /**
     * The editor with supporting endo/redo.
     */
    @NotNull
    private final UndoableEditor editor;

    /**
     * Instantiates a new Editor operation control.
     *
     * @param editor the editor
     */
    public EditorOperationControl(@NotNull final UndoableEditor editor) {
        this.editor = editor;
        this.operations = ArrayFactory.newArray(EditorOperation.class);
        this.toRedo = ArrayFactory.newArray(EditorOperation.class);
    }

    /**
     * @return the list of operations.
     */
    @NotNull
    private Array<EditorOperation> getOperations() {
        return operations;
    }

    /**
     * @return the list of operations to redo.
     */
    @NotNull
    private Array<EditorOperation> getToRedo() {
        return toRedo;
    }

    /**
     * @return the editor with supporting endo/redo.
     */
    @NotNull
    private UndoableEditor getEditor() {
        return editor;
    }

    /**
     * Execute an operation.
     *
     * @param operation the operation.
     */
    @FromAnyThread
    public void execute(@NotNull final EditorOperation operation) {
        if (Platform.isFxApplicationThread()) {
            executeImpl(operation);
        } else {
            final ExecutorManager executorManager = ExecutorManager.getInstance();
            executorManager.addFXTask(() -> executeImpl(operation));
        }
    }

    /**
     * Executing an operation.
     *
     * @param operation the operation.
     */
    @FXThread
    private void executeImpl(@NotNull final EditorOperation operation) {

        final UndoableEditor editor = getEditor();
        operation.redo(editor);
        editor.incrementChange();

        final Array<EditorOperation> operations = getOperations();
        operations.add(operation);

        if (operations.size() > HISTORY_SIZE) operations.poll();

        final Array<EditorOperation> toRedo = getToRedo();
        toRedo.clear();
    }

    /**
     * Undo the last operation.
     */
    @FromAnyThread
    public void undo() {
        if (Platform.isFxApplicationThread()) {
            undoImpl();
        } else {
            final ExecutorManager executorManager = ExecutorManager.getInstance();
            executorManager.addFXTask(this::undoImpl);
        }
    }

    /**
     * Undo the last operation.
     */
    @FXThread
    private synchronized void undoImpl() {

        final Array<EditorOperation> operations = getOperations();
        final EditorOperation operation = operations.pop();
        if (operation == null) return;

        final UndoableEditor editor = getEditor();
        operation.undo(editor);
        editor.decrementChange();

        final Array<EditorOperation> toRedo = getToRedo();
        toRedo.add(operation);
    }

    /**
     * Redo the last undo operation.
     */
    @FromAnyThread
    public void redo() {
        if (Platform.isFxApplicationThread()) {
            redoImpl();
        } else {
            final ExecutorManager executorManager = ExecutorManager.getInstance();
            executorManager.addFXTask(this::redoImpl);
        }
    }

    /**
     * Redo the last undo operation.
     */
    @FXThread
    private void redoImpl() {

        final Array<EditorOperation> toRedo = getToRedo();
        final EditorOperation operation = toRedo.pop();
        if (operation == null) return;

        operation.redo(editor);

        final UndoableEditor editor = getEditor();
        editor.incrementChange();

        final Array<EditorOperation> operations = getOperations();
        operations.add(operation);
    }

    /**
     * Clear operation history.
     */
    @FromAnyThread
    public void clear() {
        if (Platform.isFxApplicationThread()) {
            clearImpl();
        } else {
            final ExecutorManager executorManager = ExecutorManager.getInstance();
            executorManager.addFXTask(this::clearImpl);
        }
    }

    /**
     * Clear operation history.
     */
    private void clearImpl() {

        final Array<EditorOperation> operations = getOperations();
        operations.clear();

        final Array<EditorOperation> toRedo = getToRedo();
        toRedo.clear();
    }

    @Override
    public String toString() {
        return "EditorOperationControl{" +
                "operations=" + operations +
                ", toRedo=" + toRedo +
                ", editor=" + editor +
                '}';
    }
}
