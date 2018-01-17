package com.ss.editor.ui.component.editor;

import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.part3d.editor.Editor3DPart;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.beans.property.BooleanProperty;
import javafx.event.Event;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * The interface for implementing file editor.
 *
 * @author JavaSaBr
 */
public interface FileEditor {

    /**
     * The Empty states.
     */
    @NotNull Array<Editor3DPart> EMPTY_3D_STATES = ArrayFactory.newArray(Editor3DPart.class);

    /**
     * Get the page for showing the editor.
     *
     * @return the page for showing the editor.
     */
    @FxThread
    @NotNull Parent getPage();

    /**
     * Gets an area to place 3D scene.
     *
     * @return the area to place 3D scene.
     */
    @FxThread
    default @Nullable BorderPane get3DArea() {
        return null;
    }

    /**
     * Gets file name.
     *
     * @return the file name of the current opened file.
     */
    @FxThread
    @NotNull String getFileName();

    /**
     * Gets edit file.
     *
     * @return the editing file.
     */
    @FxThread
    @NotNull Path getEditFile();

    /**
     * Open the file.
     *
     * @param file the file.
     */
    @FxThread
    void openFile(@NotNull final Path file);

    /**
     * Dirty property boolean property.
     *
     * @return the dirty property of this editor.
     */
    @FxThread
    @NotNull BooleanProperty dirtyProperty();

    /**
     * Is dirty boolean.
     *
     * @return true if the current file was changed.
     */
    @FxThread
    boolean isDirty();

    /**
     * Save new changes.
     */
    @FxThread
    default void save() {
        save(null);
    }

    /**
     * Save new changes.
     *
     * @param callback the callback.
     */
    @FxThread
    default void save(@Nullable Consumer<@NotNull FileEditor> callback) {
    }

    /**
     * Gets states.
     *
     * @return the 3D part of this editor.
     */
    @FxThread
    default @NotNull Array<Editor3DPart> get3DStates() {
        return EMPTY_3D_STATES;
    }

    /**
     * Notify that this editor was closed.
     */
    @FxThread
    default void notifyClosed() {
    }

    /**
     * Notify about renamed files.
     *
     * @param prevFile the prev file
     * @param newFile  the new file
     */
    @FxThread
    default void notifyRenamed(@NotNull final Path prevFile, @NotNull final Path newFile) {
    }

    /**
     * Notify about moved file.
     *
     * @param prevFile the prev file
     * @param newFile  the new file
     */
    @FxThread
    default void notifyMoved(@NotNull final Path prevFile, @NotNull final Path newFile) {
    }

    /**
     * Gets description.
     *
     * @return the description of this editor.
     */
    @FromAnyThread
    @NotNull EditorDescription getDescription();

    /**
     * Notify that this editor was showed.
     */
    @FxThread
    default void notifyShowed() {
    }

    /**
     * Notify that this editor was hided.
     */
    @FxThread
    default void notifyHided() {
    }

    /**
     * Check the coords that it's inside in the editing area of this editor.
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
}
