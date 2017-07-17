package com.ss.editor.ui.event;

import com.ss.editor.ui.component.editor.FileEditor;
import com.ss.editor.ui.component.editor.area.EditorAreaComponent;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.input.*;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

/**
 * The class for redirecting stage events to JME.
 *
 * @author JavaSaBr
 */
public class EventRedirector {

    /**
     * The editor area.
     */
    @NotNull
    private final EditorAreaComponent editorAreaComponent;

    /**
     * The view for showing 3D.
     */
    @NotNull
    private final Node destination;

    /**
     * The stage.
     */
    @NotNull
    private final Stage stage;

    /**
     * The flag of pressing mouse.
     */
    @NotNull
    private final boolean[] mousePressed;

    /**
     * The current mouse scene X.
     */
    private double sceneX;

    /**
     * The current mouse scene Y.
     */
    private double sceneY;

    /**
     * Instantiates a new Event redirector.
     *
     * @param editorAreaComponent the editor area component
     * @param destination         the destination
     * @param stage               the stage
     */
    public EventRedirector(@NotNull final EditorAreaComponent editorAreaComponent, @NotNull final Node destination,
                           @NotNull final Stage stage) {
        this.editorAreaComponent = editorAreaComponent;
        this.destination = destination;
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
            if (target == destination) return;

            final FileEditor currentEditor = editorAreaComponent.getCurrentEditor();
            if (currentEditor == null) return;

            if (!isMousePressed(event.getButton())) {
                return;
            }

            setMousePressed(event.getButton(), false);

            Event.fireEvent(destination, event.copyFor(event.getSource(), destination));
        });

        stage.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {

            final EventTarget target = event.getTarget();
            if (target == destination) return;

            final FileEditor currentEditor = editorAreaComponent.getCurrentEditor();
            if (currentEditor == null || !currentEditor.isInside(event.getSceneX(), event.getSceneY())) {
                return;
            }

            setMousePressed(event.getButton(), true);

            Event.fireEvent(destination, event.copyFor(event.getSource(), destination));
        });

        stage.addEventFilter(MouseEvent.MOUSE_MOVED, event -> {

            final EventTarget target = event.getTarget();
            if (target == destination) return;

            updateCoords(event);

            final FileEditor currentEditor = editorAreaComponent.getCurrentEditor();
            if (currentEditor == null || !currentEditor.isInside(event.getSceneX(), event.getSceneY())) {
                return;
            }

            Event.fireEvent(destination, event.copyFor(event.getSource(), destination));
        });

        stage.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {

            final EventTarget target = event.getTarget();
            if (target == destination) return;

            updateCoords(event);

            final FileEditor currentEditor = editorAreaComponent.getCurrentEditor();
            if (currentEditor == null) return;

            if (!isMousePressed(event.getButton()) && !currentEditor.isInside(event.getSceneX(), event.getSceneY())) {
                return;
            }

            Event.fireEvent(destination, event.copyFor(event.getSource(), destination));
        });

        stage.addEventHandler(ScrollEvent.ANY, this::redirect);
        stage.addEventHandler(KeyEvent.KEY_PRESSED, this::redirect);
        stage.addEventHandler(KeyEvent.KEY_RELEASED, this::redirect);
    }

    private void redirect(@NotNull final GestureEvent event) {

        final EventTarget target = event.getTarget();
        if (target == destination) return;

        final FileEditor currentEditor = editorAreaComponent.getCurrentEditor();
        if (currentEditor == null || !currentEditor.isInside(event.getSceneX(), event.getSceneY())) {
            return;
        }

        Event.fireEvent(destination, event.copyFor(event.getSource(), destination));
    }

    private void redirect(@NotNull final InputEvent event) {

        final EventTarget target = event.getTarget();
        if (target == destination) return;

        final EventType<? extends InputEvent> eventType = event.getEventType();

        final FileEditor currentEditor = editorAreaComponent.getCurrentEditor();
        if (currentEditor == null || eventType != KeyEvent.KEY_RELEASED && !currentEditor.isInside(getSceneX(), getSceneY())) {
            return;
        }

        Event.fireEvent(destination, event.copyFor(event.getSource(), destination));
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
