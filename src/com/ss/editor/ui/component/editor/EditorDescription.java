package com.ss.editor.ui.component.editor;

import java.util.concurrent.Callable;

import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * Класс для описания редактора.
 *
 * @author Ronn
 */
public class EditorDescription {

    /**
     * Набор расширений, поддерживаемых этим редактором.
     */
    private final Array<String> extensions;

    /**
     * Конструктор редактора.
     */
    private Callable<FileEditor> constructor;

    /**
     * Название редактора.
     */
    private String editorName;

    public EditorDescription() {
        this.extensions = ArrayFactory.newArray(String.class);
    }

    /**
     * @param extension поддерживаемое расширение.
     */
    public void addExtension(final String extension) {
        this.extensions.add(extension);
    }

    /**
     * @param editorName название редактора.
     */
    public void setEditorName(final String editorName) {
        this.editorName = editorName;
    }

    /**
     * @param constructor конструктор редактора.
     */
    public void setConstructor(final Callable<FileEditor> constructor) {
        this.constructor = constructor;
    }

    /**
     * @return набор расширений, поддерживаемых этим редактором.
     */
    public Array<String> getExtensions() {
        return extensions;
    }

    /**
     * @return конструктор редактора.
     */
    public Callable<FileEditor> getConstructor() {
        return constructor;
    }

    /**
     * @return название редактора.
     */
    public String getEditorName() {
        return editorName;
    }

    @Override
    public String toString() {
        return "EditorDescription{" +
                "editorName='" + editorName + '\'' +
                ", extensions=" + extensions +
                '}';
    }
}
