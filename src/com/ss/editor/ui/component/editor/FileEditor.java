package com.ss.editor.ui.component.editor;

import com.ss.editor.state.editor.EditorState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

import javafx.beans.property.BooleanProperty;
import javafx.scene.Parent;
import rlib.util.array.Array;

/**
 * The interface for implementing file editor.
 *
 * @author JavaSaBr.
 */
public interface FileEditor {

    /**
     * Get the page for showing the editor.
     *
     * @return the page for showing the editor.
     */
    @NotNull
    Parent getPage();

    /**
     * @return the file name of the current opened file.
     */
    @NotNull
    String getFileName();

    /**
     * @return the editing file.
     */
    @NotNull
    Path getEditFile();

    /**
     * Open the file.
     *
     * @param file the file.
     */
    void openFile(@NotNull final Path file);

    /**
     * @return the dirty property of this editor.
     */
    @NotNull
    BooleanProperty dirtyProperty();

    /**
     * @return true if the current file was changed.
     */
    boolean isDirty();

    /**
     * Save new changes.
     */
    default void doSave() {
    }

    /**
     * @return the 3D part of this editor.
     */
    @Nullable
    default Array<EditorState> getStates() {
        return null;
    }

    /**
     * Notify that this editor was closed.
     */
    default void notifyClosed() {
    }

    /**
     * Notify about renamed files.
     */
    default void notifyRenamed(@NotNull final Path prevFile, @NotNull final Path newFile) {
    }

    /**
     * Notify about moved file.
     */
    default void notifyMoved(@NotNull final Path prevFile, @NotNull final Path newFile) {
    }

    /**
     * @return the description of this editor.
     */
    @NotNull
    EditorDescription getDescription();

    /**
     * Notify that this editor was showed.
     */
    default void notifyShowed() {
    }

    /**
     * Notify that this editor was hided.
     */
    default void notifyHided() {
    }

    /**
     * @return true if the point is inside in this editor.
     */
    default boolean isInside(double sceneX, double sceneY) {
        return false;
    }
}
