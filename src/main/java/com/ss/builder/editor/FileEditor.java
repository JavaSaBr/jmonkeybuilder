package com.ss.builder.editor;

import com.ss.builder.annotation.BackgroundThread;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.editor.event.FileEditorEvent;
import com.ss.builder.fx.editor.layout.EditorLayout;
import com.ss.builder.fx.editor.part.ui.EditorUiPart;
import com.ss.builder.jme.editor.part3d.Editor3dPart;
import com.ss.rlib.common.util.array.Array;
import javafx.beans.property.BooleanProperty;
import javafx.event.Event;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

/**
 * The interface for implementing file editor.
 *
 * @author JavaSaBr
 */
public interface FileEditor {

    /**
     * Get a visible page of this editor
     *
     * @return the visible page of this editor
     */
    @FxThread
    @Deprecated
    @NotNull Parent getUiPage();

    /**
     * Get this editor's layout.
     *
     * @return this editor's layout.
     */
    @FxThread
    @NotNull EditorLayout getLayout();

    /**
     * Get an area to place 3D scene.
     *
     * @return the area to place 3D scene.
     */
    @FxThread
    @Deprecated
    default @Nullable BorderPane get3dArea() {
        return null;
    }

    /**
     * Get the file name of the current opened file.
     *
     * @return the file name of the current opened file.
     */
    @FxThread
    @NotNull String getFileName();

    /**
     * Get the editing file.
     *
     * @return the editing file.
     */
    @FxThread
    @NotNull Path getFile();

    /**
     * Open the file.
     *
     * @param file the file.
     */
    @BackgroundThread
    void openFile(@NotNull Path file);

    /**
     * Dirty property boolean property.
     *
     * @return the dirty property of this editor.
     */
    @FxThread
    @NotNull BooleanProperty dirtyProperty();

    /**
     * Return true if the current file was changed.
     *
     * @return true if the current file was changed.
     */
    @FromAnyThread
    boolean isDirty();

    /**
     * Save new changes.
     */
    @FromAnyThread
    @NotNull CompletableFuture<FileEditor> save();

    /**
     * Get the editor's 3D parts.
     *
     * @return the editor's 3D parts.
     */
    @FxThread
    default @NotNull Array<Editor3dPart> get3dParts() {
        return Array.empty();
    }

    /**
     * Get the editor's UI parts.
     *
     * @return the editor's UI parts.
     */
    @FxThread
    default @NotNull Array<EditorUiPart> getUiParts() {
        return Array.empty();
    }

    /**
     * Notify this editor about the happened event.
     *
     * @param event the editor's event.
     */
    @FxThread
    void notify(@NotNull FileEditorEvent event);

    /**
     * Get the editor's descriptor.
     *
     * @return the editor's descriptor.
     */
    @FromAnyThread
    @NotNull EditorDescriptor getDescriptor();

    /**
     * Return true if the point is inside in the editing area.
     *
     * @param sceneX    the scene x
     * @param sceneY    the scene y
     * @param eventType the event type.
     * @return true if the point is inside in the editing area.
     */
    @FxThread
    default boolean isInside(double sceneX, double sceneY, @NotNull Class<? extends Event> eventType) {
        return false;
    }

    /**
     * Handle an external key action.
     *
     * @param keyCode            the key code.
     * @param isPressed          true if key is pressed.
     * @param isControlDown      true if control is down.
     * @param isShiftDown        true if shift is down.
     * @param isButtonMiddleDown true if mouse middle button is pressed.
     */
    @FromAnyThread
    void handleKeyAction(
            @NotNull KeyCode keyCode,
            boolean isPressed,
            boolean isControlDown,
            boolean isShiftDown,
            boolean isButtonMiddleDown
    );
}
