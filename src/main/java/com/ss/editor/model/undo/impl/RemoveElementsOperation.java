package com.ss.editor.model.undo.impl;

import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.jme3.light.Light;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
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

        public Element(@NotNull final Object element, @NotNull final Object parent) {
            this.element = element;
            this.parent = parent;
            this.index = -1;
        }

        /**
         * Get the element to remove.
         *
         * @return the element to remove.
         */
        private @NotNull Object getElement() {
            return element;
        }

        /**
         * Get the element's parent.
         *
         * @return the element's parent.
         */
        private @NotNull Object getParent() {
            return parent;
        }

        /**
         * Get the index of position in the parent.
         *
         * @return the index of position in the parent.
         */
        private int getIndex() {
            return index;
        }

        /**
         * Set the index of position in the parent.
         *
         * @param index the index of position in the parent.
         */
        private void setIndex(final int index) {
            this.index = index;
        }
    }

    /**
     * The elements to remove.
     */
    @NotNull
    private final Array<Element> elements;

    public RemoveElementsOperation(@NotNull final Array<Element> elements) {
        this.elements = elements;
    }

    @Override
    @JmeThread
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addJmeTask(() -> {

            for (final Element element : elements) {

                final Object toRemove = element.getElement();

                if (toRemove instanceof Spatial) {
                    removeSpatial(element, (Spatial) toRemove);
                } else if (toRemove instanceof Light) {
                    removeLight(element, (Light) toRemove);
                } else if (toRemove instanceof Animation) {
                    removeAnimation(element, (Animation) toRemove);
                } else if (toRemove instanceof Control) {
                    removeControl(element, (Control) toRemove);
                }
            }

            EXECUTOR_MANAGER.addFxTask(() -> {
                elements.forEach(editor, (element, consumer) ->
                        consumer.notifyFxRemovedChild(element.getParent(), element.getElement()));
            });
        });
    }

    @Override
    @JmeThread
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addJmeTask(() -> {

            for (final Element element : elements) {

                final Object toRestore = element.getElement();

                if (toRestore instanceof Spatial) {
                    restoreSpatial(element, (Spatial) toRestore);
                } else if (toRestore instanceof Light) {
                    restoreLight(element, (Light) toRestore);
                } else if (toRestore instanceof Animation) {
                    restoreAnimation(element, (Animation) toRestore);
                } else if (toRestore instanceof Control) {
                    restoreControl(element, (Control) toRestore);
                }
            }

            EXECUTOR_MANAGER.addFxTask(() -> {
                elements.forEach(editor, (element, consumer) ->
                        consumer.notifyFxAddedChild(element.getParent(), element.getElement(), element.getIndex(), false));
            });
        });
    }

    @JmeThread
    private void removeSpatial(@NotNull final Element element, @NotNull final Spatial toRemove) {
        final Node parent = (Node) element.getParent();
        element.setIndex(parent.getChildIndex(toRemove));
        parent.detachChild(toRemove);
    }

    @JmeThread
    private void restoreSpatial(@NotNull final Element element, @NotNull final Spatial toRestore) {
        final Node parent = (Node) element.getParent();
        parent.attachChildAt(toRestore, element.getIndex());
    }

    @JmeThread
    private void removeControl(@NotNull final Element element, @NotNull final Control toRemove) {
        final Spatial parent = (Spatial) element.getParent();
        parent.removeControl(toRemove);
    }

    @JmeThread
    private void restoreControl(@NotNull final Element element, @NotNull final Control toRestore) {
        final Spatial parent = (Spatial) element.getParent();
        parent.addControl(toRestore);
    }

    @JmeThread
    private void removeLight(@NotNull final Element element, @NotNull final Light toRemove) {
        final Spatial parent = (Spatial) element.getParent();
        parent.removeLight(toRemove);
    }

    @JmeThread
    private void restoreLight(@NotNull final Element element, @NotNull final Light toRestore) {
        final Spatial parent = (Spatial) element.getParent();
        parent.addLight(toRestore);
    }

    @JmeThread
    private void removeAnimation(@NotNull final Element element, @NotNull final Animation toRemove) {
        final AnimControl parent = (AnimControl) element.getParent();
        parent.removeAnim(toRemove);
    }

    @JmeThread
    private void restoreAnimation(@NotNull final Element element, @NotNull final Animation toRestore) {
        final AnimControl parent = (AnimControl) element.getParent();
        parent.addAnim(toRestore);
    }
}
