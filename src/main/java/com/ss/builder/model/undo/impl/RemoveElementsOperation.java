package com.ss.editor.model.undo.impl;

import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.jme3.light.Light;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link AbstractEditorOperation} to remove elements from a scene.
 *
 * @author JavaSaBr.
 */
public class RemoveElementsOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    public static class Element {

        /**
         * The element to remove.
         */
        @NotNull
        private final Object element;

        /**
         * The element's parent.
         */
        @NotNull
        private final Object parent;

        /**
         * The index of position in the parent.
         */
        private int index;

        public Element(@NotNull Object element, @NotNull Object parent) {
            this.element = element;
            this.parent = parent;
            this.index = -1;
        }
    }

    /**
     * The elements to remove.
     */
    @NotNull
    private final Array<Element> elements;

    public RemoveElementsOperation(@NotNull Array<Element> elements) {
        this.elements = elements;
    }

    @Override
    @JmeThread
    protected void redoInJme(@NotNull ModelChangeConsumer editor) {
        super.redoInJme(editor);

        elements.forEach(this, (element, op) -> {

            var toRemove = element.element;

            if (toRemove instanceof Spatial) {
                op.removeSpatial(element, (Spatial) toRemove);
            } else if (toRemove instanceof Light) {
                op.removeLight(element, (Light) toRemove);
            } else if (toRemove instanceof Animation) {
                op.removeAnimation(element, (Animation) toRemove);
            } else if (toRemove instanceof Control) {
                op.removeControl(element, (Control) toRemove);
            }
        });
    }

    @Override
    @FxThread
    protected void endRedoInFx(@NotNull ModelChangeConsumer editor) {
        super.endRedoInFx(editor);
        elements.forEach(editor, (element, consumer) ->
                consumer.notifyFxRemovedChild(element.parent, element.element));
    }

    @Override
    @JmeThread
    protected void undoInJme(@NotNull ModelChangeConsumer editor) {
        super.undoInJme(editor);

        elements.forEach(this, (element, op) -> {

            var toRemove = element.element;

            if (toRemove instanceof Spatial) {
                op.restoreSpatial(element, (Spatial) toRemove);
            } else if (toRemove instanceof Light) {
                op.restoreLight(element, (Light) toRemove);
            } else if (toRemove instanceof Animation) {
                op.restoreAnimation(element, (Animation) toRemove);
            } else if (toRemove instanceof Control) {
                op.restoreControl(element, (Control) toRemove);
            }
        });
    }

    @Override
    @FxThread
    protected void endUndoInFx(@NotNull ModelChangeConsumer editor) {
        super.endUndoInFx(editor);
        elements.forEach(editor, (element, consumer) ->
                consumer.notifyFxAddedChild(element.parent, element.element, element.index, false));
    }

    @JmeThread
    private void removeSpatial(@NotNull Element element, @NotNull Spatial toRemove) {
        var parent = (Node) element.parent;
        element.index = parent.getChildIndex(toRemove);
        parent.detachChild(toRemove);
    }

    @JmeThread
    private void restoreSpatial(@NotNull Element element, @NotNull Spatial toRestore) {
        var parent = (Node) element.parent;
        parent.attachChildAt(toRestore, element.index);
    }

    @JmeThread
    private void removeControl(@NotNull Element element, @NotNull Control toRemove) {
        var parent = (Spatial) element.parent;
        parent.removeControl(toRemove);
    }

    @JmeThread
    private void restoreControl(@NotNull Element element, @NotNull Control toRestore) {
        var parent = (Spatial) element.parent;
        parent.addControl(toRestore);
    }

    @JmeThread
    private void removeLight(@NotNull Element element, @NotNull Light toRemove) {
        var parent = (Spatial) element.parent;
        parent.removeLight(toRemove);
    }

    @JmeThread
    private void restoreLight(@NotNull Element element, @NotNull Light toRestore) {
        var parent = (Spatial) element.parent;
        parent.addLight(toRestore);
    }

    @JmeThread
    private void removeAnimation(@NotNull Element element, @NotNull Animation toRemove) {
        var parent = (AnimControl) element.parent;
        parent.removeAnim(toRemove);
    }

    @JmeThread
    private void restoreAnimation(@NotNull Element element, @NotNull Animation toRestore) {
        var parent = (AnimControl) element.parent;
        parent.addAnim(toRestore);
    }
}
