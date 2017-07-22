package com.ss.editor.ui.component.editor;

import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.state.editor.Editor3DState;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.beans.property.BooleanProperty;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

/**
 * The interface for implementing file editor.
 *
 * @author JavaSaBr
 */
public interface FileEditor {

    /**
     * The Empty states.
     */
    Array<Editor3DState> EMPTY_3D_STATES = ArrayFactory.newArray(Editor3DState.class);

    /**
     * Get the page for showing the editor.
     *
     * @return the page for showing the editor.
     */
    @NotNull
    @FXThread
    Parent getPage();

    /**
     * Gets an area to place 3D scene.
     *
     * @return the area to place 3D scene.
     */
    @Nullable
    @FXThread
    default BorderPane get3DArea() {
        return null;
    }

    /**
     * Gets file name.
     *
     * @return the file name of the current opened file.
     */
    @NotNull
    @FXThread
    String getFileName();

    /**
     * Gets edit file.
     *
     * @return the editing file.
     */
    @NotNull
    @FXThread
    Path getEditFile();

    /**
     * Open the file.
     *
     * @param file the file.
     */
    @FXThread
    void openFile(@NotNull final Path file);

    /**
     * Dirty property boolean property.
     *
     * @return the dirty property of this editor.
     */
    @NotNull
    @FXThread
    BooleanProperty dirtyProperty();

    /**
     * Is dirty boolean.
     *
     * @return true if the current file was changed.
     */
    @FXThread
    boolean isDirty();

    /**
     * Save new changes.
     */
    @FXThread
    default void doSave() {
    }

    /**
     * Gets states.
     *
     * @return the 3D part of this editor.
     */
    @NotNull
    @FXThread
    default Array<Editor3DState> get3DStates() {
        return EMPTY_3D_STATES;
    }

    /**
     * Notify that this editor was closed.
     */
    @FXThread
    default void notifyClosed() {
    }

    /**
     * Notify about renamed files.
     *
     * @param prevFile the prev file
     * @param newFile  the new file
     */
    @FXThread
    default void notifyRenamed(@NotNull final Path prevFile, @NotNull final Path newFile) {
    }

    /**
     * Notify about moved file.
     *
     * @param prevFile the prev file
     * @param newFile  the new file
     */
    @FXThread
    default void notifyMoved(@NotNull final Path prevFile, @NotNull final Path newFile) {
    }

    /**
     * Gets description.
     *
     * @return the description of this editor.
     */
    @NotNull
    @FromAnyThread
    EditorDescription getDescription();

    /**
     * Notify that this editor was showed.
     */
    @FXThread
    default void notifyShowed() {
    }

    /**
     * Notify that this editor was hided.
     */
    @FXThread
    default void notifyHided() {
    }

    /**
     * Check the coords that it's inside in the editing area of this editor.
     *
     * @param sceneX the scene x
     * @param sceneY the scene y
     * @return true if the point is inside in the editing area.
     */
    @FXThread
    default boolean isInside(double sceneX, double sceneY) {
        return false;
    }
}
