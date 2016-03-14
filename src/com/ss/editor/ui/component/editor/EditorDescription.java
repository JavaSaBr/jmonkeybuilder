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

    /**
     * Уникальный ид редактора.
     */
    private String editorId;

    public EditorDescription() {
        this.extensions = ArrayFactory.newArray(String.class);
    }

    /**
     * @return уникальный ид редактора.
     */
    public String getEditorId() {
        return editorId;
    }

    /**
     * @param editorId уникальный ид редактора.
     */
    public void setEditorId(String editorId) {
        this.editorId = editorId;
    }

    /**
     * @param extension поддерживаемое расширение.
     */
    public void addExtension(final String extension) {
        this.extensions.add(extension);
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
     * @param constructor конструктор редактора.
     */
    public void setConstructor(final Callable<FileEditor> constructor) {
        this.constructor = constructor;
    }

    /**
     * @return название редактора.
     */
    public String getEditorName() {
        return editorName;
    }

    /**
     * @param editorName название редактора.
     */
    public void setEditorName(final String editorName) {
        this.editorName = editorName;
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
