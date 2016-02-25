package com.ss.editor.ui.component.editor;

import com.ss.editor.ui.component.editor.impl.TextFileEditor;
import com.ss.editor.ui.component.editor.impl.post.filter.PostFilterEditor;

import java.nio.file.Path;
import java.util.concurrent.Callable;

import rlib.logging.Logger;
import rlib.logging.LoggerManager;
import rlib.util.FileUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;
import rlib.util.dictionary.DictionaryFactory;
import rlib.util.dictionary.ObjectDictionary;

/**
 * Реестр редакторов.
 *
 * @author Ronn
 */
public class EditorRegistry {

    private static final Logger LOGGER = LoggerManager.getLogger(EditorRegistry.class);

    public static final String ALL_FORMATS = "*";

    private static final EditorRegistry INSTANCE = new EditorRegistry();

    public static EditorRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * Таблица с описаниями редакторов.
     */
    private final ObjectDictionary<String, Array<EditorDescription>> editorDescriptions;

    public EditorRegistry() {
        this.editorDescriptions = DictionaryFactory.newObjectDictionary();
        loadDescriptions();
    }

    /**
     * Загрузка описаний редакторов.
     */
    private void loadDescriptions() {
        addDescription(TextFileEditor.DESCRIPTION);
        addDescription(PostFilterEditor.DESCRIPTION);
    }

    /**
     * @return таблица описаний редакторов.
     */
    private ObjectDictionary<String, Array<EditorDescription>> getEditorDescriptions() {
        return editorDescriptions;
    }

    /**
     * Добавление нового описания редактора.
     */
    private void addDescription(final EditorDescription description) {

        final ObjectDictionary<String, Array<EditorDescription>> editorDescriptions = getEditorDescriptions();

        final Array<String> extensions = description.getExtensions();
        extensions.forEach(extension -> {

            Array<EditorDescription> descriptions = editorDescriptions.get(extension);

            if(descriptions == null) {
                descriptions = ArrayFactory.newArray(EditorDescription.class);
                editorDescriptions.put(extension, descriptions);
            }

            descriptions.add(description);
        });
    }

    /**
     * Создание редактора для указанного файла.
     *
     * @param file редактируемый файл.
     * @return редактор для этого файла или null.
     */
    public FileEditor createEditorFor(final Path file) {

        final String extension = FileUtils.getExtension(file.getFileName().toString());

        EditorDescription description = null;

        final ObjectDictionary<String, Array<EditorDescription>> editorDescriptions = getEditorDescriptions();

        Array<EditorDescription> descriptions = editorDescriptions.get(extension);

        if(descriptions != null) {
            description = descriptions.first();
        } else {

            descriptions = editorDescriptions.get(ALL_FORMATS);
            description = descriptions == null? null : descriptions.first();
        }

        if(description == null) {
            return null;
        }

        final Callable<FileEditor> constructor = description.getConstructor();

        try {
            return constructor.call();
        } catch (Exception e) {
            LOGGER.warning(e);
        }

        return null;
    }
}
