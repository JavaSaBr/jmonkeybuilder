package com.ss.editor.model.undo;

import com.ss.editor.manager.ExecutorManager;

import javafx.application.Platform;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * Реалищация контролеера операций в редактораз.
 *
 * @author Ronn
 */
public class EditorOperationControl {

    public static final int HISTORY_SIZE = 20;

    /**
     * Список операций.
     */
    private final Array<EditorOperation> operations;

    /**
     * Список откаченных операций.
     */
    private final Array<EditorOperation> toRedo;

    /**
     * Редактор с поддержкой отменяемости.
     */
    private final UndoableEditor editor;

    public EditorOperationControl(final UndoableEditor editor) {
        this.editor = editor;
        this.operations = ArrayFactory.newArray(EditorOperation.class);
        this.toRedo = ArrayFactory.newArray(EditorOperation.class);
    }

    /**
     * @return список операций.
     */
    private Array<EditorOperation> getOperations() {
        return operations;
    }

    /**
     * @return список откаченных операций.
     */
    private Array<EditorOperation> getToRedo() {
        return toRedo;
    }

    /**
     * @return редактор с поддержкой отменяемости.
     */
    private UndoableEditor getEditor() {
        return editor;
    }

    /**
     * Выполнение новой операции.
     */
    public synchronized void execute(final EditorOperation operation) {
        final ExecutorManager executorManager = ExecutorManager.getInstance();
        executorManager.addFXTask(() -> executeImpl(operation));
    }

    /**
     * Выполнение новой операции.
     */
    private void executeImpl(final EditorOperation operation) {

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
     * Отмена последней операции.
     */
    public void undo() {
        if (Platform.isFxApplicationThread()) {
            undoImpl();
        } else {
            final ExecutorManager executorManager = ExecutorManager.getInstance();
            executorManager.addFXTask(this::undoImpl);
        }
    }

    /**
     * Отмена последней операции.
     */
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
     * Отмена отмены последнего изменения.
     */
    public synchronized void redo() {
        if (Platform.isFxApplicationThread()) {
            redoImpl();
        } else {
            final ExecutorManager executorManager = ExecutorManager.getInstance();
            executorManager.addFXTask(this::redoImpl);
        }
    }

    /**
     * Отмена последней операции.
     */
    private synchronized void redoImpl() {

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
     * Очистка истории.
     */
    public synchronized void clear() {

        final Array<EditorOperation> operations = getOperations();
        operations.clear();

        final Array<EditorOperation> toRedo = getToRedo();
        toRedo.clear();
    }
}
