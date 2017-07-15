package com.ss.editor.ui.component.editor;

import static com.ss.rlib.util.array.ArrayFactory.newArray;
import com.ss.editor.ui.component.editor.impl.*;
import com.ss.editor.ui.component.editor.impl.material.MaterialFileEditor;
import com.ss.editor.ui.component.editor.impl.model.ModelFileEditor;
import com.ss.editor.ui.component.editor.impl.scene.SceneFileEditor;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
import com.ss.rlib.util.FileUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.dictionary.DictionaryFactory;
import com.ss.rlib.util.dictionary.ObjectDictionary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * THe registry of editors.
 *
 * @author JavaSaBr
 */
public class EditorRegistry {

    private static final Logger LOGGER = LoggerManager.getLogger(EditorRegistry.class);

    /**
     * The constant ALL_FORMATS.
     */
    public static final String ALL_FORMATS = "*";

    private static final EditorRegistry INSTANCE = new EditorRegistry();

    /**
     * Gets instance.
     *
     * @return the instance
     */
    @NotNull
    public static EditorRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * The table with editor descriptions.
     */
    private final ObjectDictionary<String, Array<EditorDescription>> editorDescriptions;

    /**
     * The table with mapping editor id to editor description.
     */
    private final ObjectDictionary<String, EditorDescription> editorIdToDescription;

    /**
     * Instantiates a new Editor registry.
     */
    public EditorRegistry() {
        this.editorDescriptions = DictionaryFactory.newObjectDictionary();
        this.editorIdToDescription = DictionaryFactory.newObjectDictionary();
        loadDescriptions();
    }

    /**
     * Load available descriptors.
     */
    private void loadDescriptions() {
        addDescription(TextFileEditor.DESCRIPTION);
        addDescription(MaterialFileEditor.DESCRIPTION);
        addDescription(ModelFileEditor.DESCRIPTION);
        addDescription(ImageViewerEditor.DESCRIPTION);
        addDescription(GLSLFileEditor.DESCRIPTION);
        addDescription(MaterialDefinitionFileEditor.DESCRIPTION);
        addDescription(AudioViewerEditor.DESCRIPTION);
        addDescription(SceneFileEditor.DESCRIPTION);
    }

    /**
     * @return the table with editor descriptions.
     */
    @NotNull
    private ObjectDictionary<String, Array<EditorDescription>> getEditorDescriptions() {
        return editorDescriptions;
    }

    /**
     * @return the table with maaping editor id to editor description.
     */
    @NotNull
    private ObjectDictionary<String, EditorDescription> getEditorIdToDescription() {
        return editorIdToDescription;
    }

    /**
     * Add new description.
     */
    private void addDescription(@NotNull final EditorDescription description) {

        final ObjectDictionary<String, Array<EditorDescription>> editorDescriptions = getEditorDescriptions();

        final Array<String> extensions = description.getExtensions();
        extensions.forEach(extension -> addDescription(description, extension, editorDescriptions));

        final ObjectDictionary<String, EditorDescription> editorIdToDescription = getEditorIdToDescription();
        editorIdToDescription.put(description.getEditorId(), description);
    }

    private void addDescription(@NotNull final EditorDescription description, @NotNull final String extension,
                                @NotNull final ObjectDictionary<String, Array<EditorDescription>> editorDescriptions) {
        final Array<EditorDescription> descriptions = editorDescriptions.get(extension, () -> newArray(EditorDescription.class));
        Objects.requireNonNull(descriptions);
        descriptions.add(description);
    }

    /**
     * Gets description.
     *
     * @param editorId the editor id
     * @return the description for the editor id or null.
     */
    @Nullable
    public EditorDescription getDescription(@NotNull final String editorId) {
        final ObjectDictionary<String, EditorDescription> editorIdToDescription = getEditorIdToDescription();
        return editorIdToDescription.get(editorId);
    }

    /**
     * Create an editor for the file.
     *
     * @param file the edited file.
     * @return the editor for this file or null.
     */
    @Nullable
    public FileEditor createEditorFor(@NotNull final Path file) {
        if (Files.isDirectory(file)) return null;

        final String extension = FileUtils.getExtension(file);
        final ObjectDictionary<String, Array<EditorDescription>> editorDescriptions = getEditorDescriptions();

        Array<EditorDescription> descriptions = editorDescriptions.get(extension);
        EditorDescription description;

        if (descriptions != null) {
            description = descriptions.first();
        } else {
            descriptions = editorDescriptions.get(ALL_FORMATS);
            description = descriptions == null ? null : descriptions.first();
        }

        if (description == null) return null;

        final Callable<FileEditor> constructor = description.getConstructor();
        try {
            return constructor.call();
        } catch (Exception e) {
            LOGGER.warning(e);
        }

        return null;
    }

    /**
     * Create an editor for the file.
     *
     * @param description the editor description.
     * @param file        the edited file.
     * @return the editor or null.
     */
    @Nullable
    public FileEditor createEditorFor(@NotNull final EditorDescription description, @NotNull final Path file) {

        final Callable<FileEditor> constructor = description.getConstructor();

        try {
            return constructor.call();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets available editors for.
     *
     * @param file the file
     * @return the list of available editors for the file.
     */
    @NotNull
    public Array<EditorDescription> getAvailableEditorsFor(@NotNull final Path file) {

        final Array<EditorDescription> result = newArray(EditorDescription.class);
        final String extension = FileUtils.getExtension(file);

        final ObjectDictionary<String, Array<EditorDescription>> editorDescriptions = getEditorDescriptions();
        final Array<EditorDescription> descriptions = editorDescriptions.get(extension);

        if (descriptions != null) {
            result.addAll(descriptions);
        }

        final Array<EditorDescription> universal = editorDescriptions.get(ALL_FORMATS);

        if (universal != null) {
            result.addAll(universal);
        }

        return result;
    }
}
