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

    /**
     * Instantiates a new Editor description.
     */
    public EditorDescription() {
        this.extensions = ArrayFactory.newArray(String.class);
    }

    /**
     * Gets editor id.
     *
     * @return the editor id.
     */
    @NotNull
    public String getEditorId() {
        return editorId;
    }

    /**
     * Sets editor id.
     *
     * @param editorId the editor id.
     */
    public void setEditorId(@NotNull final String editorId) {
        this.editorId = editorId;
    }

    /**
     * Add extension.
     *
     * @param extension the supported extension.
     */
    public void addExtension(@NotNull final String extension) {
        this.extensions.add(extension);
    }

    /**
     * Gets extensions.
     *
     * @return the list of supported extensions.
     */
    @NotNull
    public Array<String> getExtensions() {
        return extensions;
    }

    /**
     * Gets constructor.
     *
     * @return the editor constructor.
     */
    @NotNull
    public Callable<FileEditor> getConstructor() {
        return constructor;
    }

    /**
     * Sets constructor.
     *
     * @param constructor the editor constructor.
     */
    public void setConstructor(@NotNull final Callable<FileEditor> constructor) {
        this.constructor = constructor;
    }

    /**
     * Gets editor name.
     *
     * @return the editor name.
     */
    @NotNull
    public String getEditorName() {
        return editorName;
    }

    /**
     * Sets editor name.
     *
     * @param editorName the editor name.
     */
    public void setEditorName(@NotNull final String editorName) {
        this.editorName = editorName;
    }

    /**
     * Gets icon.
     *
     * @return the icon.
     */
    @Nullable
    public Image getIcon() {
        return icon;
    }

    /**
     * Sets icon.
     *
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
