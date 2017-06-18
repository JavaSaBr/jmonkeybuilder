package com.ss.editor.ui.component.editor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Callable;

import javafx.scene.image.Image;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;

/**
 * The class to describe an editor.
 *
 * @author JavaSar
 */
public class EditorDescription {

    /**
     * The list of supported extensions.
     */
    @NotNull
    private final Array<String> extensions;

    /**
     * The editor constructor.
     */
    private Callable<FileEditor> constructor;

    /**
     * The editor name.
     */
    private String editorName;

    /**
     * The editor id.
     */
    private String editorId;

    /**
     * The icon.
     */
    @Nullable
    private Image icon;

    public EditorDescription() {
        this.extensions = ArrayFactory.newArray(String.class);
    }

    /**
     * @return the editor id.
     */
    @NotNull
    public String getEditorId() {
        return editorId;
    }

    /**
     * @param editorId the editor id.
     */
    public void setEditorId(@NotNull final String editorId) {
        this.editorId = editorId;
    }

    /**
     * @param extension the supported extension.
     */
    public void addExtension(@NotNull final String extension) {
        this.extensions.add(extension);
    }

    /**
     * @return the list of supported extensions.
     */
    @NotNull
    public Array<String> getExtensions() {
        return extensions;
    }

    /**
     * @return the editor constructor.
     */
    @NotNull
    public Callable<FileEditor> getConstructor() {
        return constructor;
    }

    /**
     * @param constructor the editor constructor.
     */
    public void setConstructor(@NotNull final Callable<FileEditor> constructor) {
        this.constructor = constructor;
    }

    /**
     * @return the editor name.
     */
    @NotNull
    public String getEditorName() {
        return editorName;
    }

    /**
     * @param editorName the editor name.
     */
    public void setEditorName(@NotNull final String editorName) {
        this.editorName = editorName;
    }

    /**
     * @return the icon.
     */
    @Nullable
    public Image getIcon() {
        return icon;
    }

    /**
     * @param icon the icon.
     */
    public void setIcon(@Nullable final Image icon) {
        this.icon = icon;
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
