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
        if (Platform.isFxApplicationThread()) {
            executeImpl(operation);
        } else {
            final ExecutorManager executorManager = ExecutorManager.getInstance();
            executorManager.addFXTask(() -> executeImpl(operation));
        }
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


        final Array<EditorOperation> toRedo = getToRedo();
        toRedo.clear();
    }

    /**
     * Отмена последней операции.
     */
    public synchronized void undo() {

        final Array<EditorOperation> operations = getOperations();
        final EditorOperation operation = operations.pop();

        if(operation == null) {
            return;
        }

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

        final Array<EditorOperation> toRedo = getToRedo();
        final EditorOperation operation = toRedo.pop();

        if(operation == null) {
            return;
        }

        operation.redo(editor);

        final UndoableEditor editor = getEditor();
        editor.incrementChange();

        final Array<EditorOperation> operations = getOperations();
        operations.add(operation);
    }
}
