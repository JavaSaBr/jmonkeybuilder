package com.ss.builder.ui.component.editor;

import com.ss.builder.annotation.FromAnyThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.rlib.common.util.array.Array;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Callable;

/**
 * The class to describe an editor.
 *
 * @author JavaSar
 */
public class EditorDescriptor {

    /**
     * The list of supported extensions.
     */
    @NotNull
    private final Array<String> extensions;

    /**
     * The editor constructor.
     */
    @NotNull
    private final Callable<FileEditor> constructor;

    /**
     * The editor name.
     */
    @NotNull
    private final String editorName;

    /**
     * The editor id.
     */
    @NotNull
    private final String editorId;

    /**
     * The icon.
     */
    @Nullable
    private Image icon;

    public EditorDescriptor(
            @NotNull Callable<FileEditor> constructor,
            @NotNull String editorName,
            @NotNull String editorId,
            @NotNull String... extensions
    ) {
        this.extensions = Array.of(extensions);
        this.constructor = constructor;
        this.editorName = editorName;
        this.editorId = editorId;
    }

    public EditorDescriptor(
            @NotNull Callable<FileEditor> constructor,
            @NotNull String editorName,
            @NotNull String editorId,
            @NotNull Array<String> extensions
    ) {
        this.extensions = Array.of(extensions.toArray(String.class));
        this.constructor = constructor;
        this.editorName = editorName;
        this.editorId = editorId;
    }

    /**
     * Get the editor id.
     *
     * @return the editor id.
     */
    @FromAnyThread
    public @NotNull String getEditorId() {
        return editorId;
    }

    /**
     * Get list of supported extensions.
     *
     * @return the list of supported extensions.
     */
    @FromAnyThread
    public @NotNull Array<String> getExtensions() {
        return extensions;
    }

    /**
     * Get the editor's constructor.
     *
     * @return the editor's constructor.
     */
    @FromAnyThread
    public @NotNull Callable<FileEditor> getConstructor() {
        return constructor;
    }

    /**
     * Get the editor's name.
     *
     * @return the editor's name.
     */
    @FromAnyThread
    public @NotNull String getEditorName() {
        return editorName;
    }

    /**
     * Get the editor's icon.
     *
     * @return the editor's icon or null.
     */
    @FromAnyThread
    public @Nullable Image getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return "EditorDescription{" +
                "extensions=" + extensions +
                ", constructor=" + constructor +
                ", editorName='" + editorName + '\'' +
                ", editorId='" + editorId + '\'' +
                '}';
    }
}
