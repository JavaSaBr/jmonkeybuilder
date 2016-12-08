package com.ss.editor.ui.component.editor;

import com.ss.editor.state.editor.EditorState;

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
    Parent getPage();

    /**
     * @return the file name of the current opened file.
     */
    String getFileName();

    /**
     * @return the editing file.
     */
    Path getEditFile();

    /**
     * Open the file.
     *
     * @param file the file.
     */
    void openFile(final Path file);

    /**
     * @return the dirty property of this editor.
     */
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
    default void notifyRenamed(final Path prevFile, final Path newFile) {
    }

    /**
     * Notify about moved file.
     */
    default void notifyMoved(final Path prevFile, final Path newFile) {
    }

    /**
     * @return the description of this editor.
     */
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
}
