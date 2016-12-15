package com.ss.editor.ui.event;

import com.ss.editor.ui.component.editor.FileEditor;
import com.ss.editor.ui.component.editor.area.EditorAreaComponent;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Stage;

/**
 * The class for redirecting stage events to JME.
 *
 * @author JavaSaBr
 */
public class EventRedirector {

    /**
     * The editor area.
     */
    private final EditorAreaComponent editorAreaComponent;

    /**
     * The image view for showing 3D.
     */
    private final ImageView imageView;

    /**
     * The stage.
     */
    private final Stage stage;

    /**
     * The flag of pressing mouse.
     */
    private final boolean[] mousePressed;

    /**
     * The current mouse scene X.
     */
    private double sceneX;

    /**
     * The current mouse scene Y.
     */
    private double sceneY;

    public EventRedirector(final EditorAreaComponent editorAreaComponent, final ImageView imageView, final Stage stage) {
        this.editorAreaComponent = editorAreaComponent;
        this.imageView = imageView;
        this.stage = stage;
        this.mousePressed = new boolean[MouseButton.values().length];
        init();
    }

    /**
     * Init listeners.
     */
    private void init() {
        stage.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {

            final EventTarget target = event.getTarget();
            if (target == imageView) return;

            final FileEditor currentEditor = editorAreaComponent.getCurrentEditor();
            if (currentEditor == null) return;

            if (!isMousePressed(event.getButton()) && !currentEditor.isInside(event.getSceneX(), event.getSceneY())) {
                return;
            }

            setMousePressed(event.getButton(), false);

            Event.fireEvent(imageView, event.copyFor(event.getSource(), imageView));
        });

        stage.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {

            final EventTarget target = event.getTarget();
            if (target == imageView) return;

            final FileEditor currentEditor = editorAreaComponent.getCurrentEditor();
            if (currentEditor == null || !currentEditor.isInside(event.getSceneX(), event.getSceneY())) {
                return;
            }

            setMousePressed(event.getButton(), true);

            Event.fireEvent(imageView, event.copyFor(event.getSource(), imageView));
        });

        stage.addEventFilter(MouseEvent.MOUSE_MOVED, event -> {

            final EventTarget target = event.getTarget();
            if (target == imageView) return;

            updateCoords(event);

            final FileEditor currentEditor = editorAreaComponent.getCurrentEditor();
            if (currentEditor == null || !currentEditor.isInside(event.getSceneX(), event.getSceneY())) {
                return;
            }

            Event.fireEvent(imageView, event.copyFor(event.getSource(), imageView));
        });

        stage.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {

            final EventTarget target = event.getTarget();
            if (target == imageView) return;

            updateCoords(event);

            final FileEditor currentEditor = editorAreaComponent.getCurrentEditor();
            if (currentEditor == null) return;

            if (!isMousePressed(event.getButton()) && !currentEditor.isInside(event.getSceneX(), event.getSceneY())) {
                return;
            }

            Event.fireEvent(imageView, event.copyFor(event.getSource(), imageView));
        });

        stage.addEventHandler(ScrollEvent.ANY, event -> {

            final EventTarget target = event.getTarget();
            if (target == imageView) return;

            final FileEditor currentEditor = editorAreaComponent.getCurrentEditor();
            if (currentEditor == null || !currentEditor.isInside(event.getSceneX(), event.getSceneY())) {
                return;
            }

            Event.fireEvent(imageView, event.copyFor(event.getSource(), imageView));
        });

        stage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {

            final EventTarget target = event.getTarget();
            if (target == imageView) return;

            final FileEditor currentEditor = editorAreaComponent.getCurrentEditor();
            if (currentEditor == null || !currentEditor.isInside(getSceneX(), getSceneY())) {
                return;
            }

            Event.fireEvent(imageView, event.copyFor(event.getSource(), imageView));
        });

        stage.addEventHandler(KeyEvent.KEY_RELEASED, event -> {

            final EventTarget target = event.getTarget();
            if (target == imageView) return;

            final FileEditor currentEditor = editorAreaComponent.getCurrentEditor();
            if (currentEditor == null || !currentEditor.isInside(getSceneX(), getSceneY())) {
                return;
            }

            Event.fireEvent(imageView, event.copyFor(event.getSource(), imageView));
        });
    }

    /**
     * Update mouse coords.
     */
    private void updateCoords(final MouseEvent event) {
        this.sceneX = event.getSceneX();
        this.sceneY = event.getSceneY();
    }

    /**
     * @return the current mouse scene X.
     */
    private double getSceneX() {
        return sceneX;
    }

    /**
     * @return the current mouse scene Y.
     */
    private double getSceneY() {
        return sceneY;
    }

    /**
     * @param button       the mouse button.
     * @param mousePressed true if mouse is pressed.
     */
    private void setMousePressed(final MouseButton button, final boolean mousePressed) {
        this.mousePressed[button.ordinal()] = mousePressed;
    }

    /**
     * @param button the mouse button.
     * @return true if mouse is pressed.
     */
    private boolean isMousePressed(final MouseButton button) {
        return mousePressed[button.ordinal()];
    }
}
