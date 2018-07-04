package com.ss.editor.ui.component.editor;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.component.editor.impl.*;
import com.ss.editor.ui.component.editor.impl.material.MaterialFileEditor;
import com.ss.editor.ui.component.editor.impl.model.ModelFileEditor;
import com.ss.editor.ui.component.editor.impl.scene.SceneFileEditor;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.plugin.extension.ExtensionPoint;
import com.ss.rlib.common.plugin.extension.ExtensionPointManager;
import com.ss.rlib.common.util.FileUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.dictionary.ConcurrentObjectDictionary;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * THe registry of editors.
 *
 * @author JavaSaBr
 */
public class EditorRegistry {

    private static final Logger LOGGER = LoggerManager.getLogger(EditorRegistry.class);

    public static final String ALL_FORMATS = "*";

    /**
     * @see EditorDescriptor
     */
    public static final String EP_DESCRIPTORS = "EditorRegistry#descriptors";

    private static final ExtensionPoint<EditorDescriptor> DESCRIPTORS =
            ExtensionPointManager.register(EP_DESCRIPTORS);

    private static final Supplier<Array<EditorDescriptor>> ARRAY_FACTORY =
            Array.supplier(EditorDescriptor.class);

    private static final EditorRegistry INSTANCE = new EditorRegistry();

    public static @NotNull EditorRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * The table with editor descriptions.
     */
    @NotNull
    private final ConcurrentObjectDictionary<String, Array<EditorDescriptor>> editorDescriptors;

    /**
     * The table with mapping editor id to editor description.
     */
    @NotNull
    private final ConcurrentObjectDictionary<String, EditorDescriptor> editorIdToDescriptor;

    /**
     * True if this registry was fully initialized.
     */
    @NotNull
    private final AtomicBoolean initialized;

    private EditorRegistry() {
        this.initialized = new AtomicBoolean(false);
        this.editorDescriptors = ConcurrentObjectDictionary.ofType(String.class, Array.class);
        this.editorIdToDescriptor = ConcurrentObjectDictionary.ofType(String.class, EditorDescriptor.class);

        DESCRIPTORS.register(TextFileEditor.DESCRIPTOR)
                .register(MaterialFileEditor.DESCRIPTOR)
                .register(ModelFileEditor.DESCRIPTOR)
                .register(ImageViewerEditor.DESCRIPTOR)
                .register(GLSLFileEditor.DESCRIPTOR)
                .register(MaterialDefinitionFileEditor.DESCRIPTOR)
                .register(AudioViewerEditor.DESCRIPTOR)
                .register(SceneFileEditor.DESCRIPTOR);

        LOGGER.info("initialized.");
    }

    @FromAnyThread
    private void checkAndInitialize() {
        if (initialized.compareAndSet(false, true)) {
            DESCRIPTORS.getExtensions().forEach(this::register);
        }
    }

    /**
     * Add the new descriptor.
     *
     * @param descriptor the descriptor of the editor.
     */
    @FromAnyThread
    private void register(@NotNull EditorDescriptor descriptor) {

        descriptor.getExtensions()
                .forEach(descriptor, this::register);

        editorIdToDescriptor
                .runInWriteLock(descriptor.getEditorId(), descriptor, ObjectDictionary::put);
    }

    @FromAnyThread
    private void register(@NotNull String extension, @NotNull EditorDescriptor descriptor) {
        editorDescriptors.runInWriteLock(extension, descriptor,
                (dict, ext, toAdd) -> dict.get(ext, ARRAY_FACTORY).add(toAdd));
    }

    /**
     * Get an editor description by the editor id.
     *
     * @param editorId the editor id.
     * @return the description or null.
     */
    @FromAnyThread
    public @Nullable EditorDescriptor getDescription(@NotNull String editorId) {
        checkAndInitialize();
        return editorIdToDescriptor
                .getInReadLock(editorId, ObjectDictionary::get);
    }

    /**
     * Create an editor for the file.
     *
     * @param file the edited file.
     * @return the editor for this file or null.
     */
    @FromAnyThread
    public @Nullable FileEditor createEditorFor(@NotNull Path file) {
        checkAndInitialize();

        if (Files.isDirectory(file)) {
            return null;
        }

        var extension = FileUtils.getExtension(file);
        var descriptions = editorDescriptors.get(extension);

        EditorDescriptor description;

        if (descriptions != null) {
            description = descriptions.first();
        } else {
            descriptions = editorDescriptors.get(ALL_FORMATS);
            description = descriptions == null ? null : descriptions.first();
        }

        if (description == null) {
            return null;
        }

        var constructor = description.getConstructor();
        try {
            var fileEditor = constructor.call();
            fileEditor.createContent();
            return fileEditor;
        } catch (Exception e) {
            EditorUtil.handleException(LOGGER, this, e);
        }

        return null;
    }

    /**
     * Create an editor for the file.
     *
     * @param descriptor the editor descriptor.
     * @param file        the edited file.
     * @return the editor or null.
     */
    @FromAnyThread
    public @Nullable FileEditor createEditorFor(@NotNull EditorDescriptor descriptor, @NotNull Path file) {
        checkAndInitialize();

        var constructor = descriptor.getConstructor();
        try {
            var fileEditor = constructor.call();
            fileEditor.createContent();
            return fileEditor;
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
    @FromAnyThread
    public @NotNull Array<EditorDescriptor> getAvailableEditorsFor(@NotNull Path file) {
        checkAndInitialize();

        var result = Array.<EditorDescriptor>ofType(EditorDescriptor.class);
        var extension = FileUtils.getExtension(file);

        var descriptions = editorDescriptors.get(extension);

        if (descriptions != null) {
            result.addAll(descriptions);
        }

        var universal = editorDescriptors.get(ALL_FORMATS);

        if (universal != null) {
            result.addAll(universal);
        }

        return result;
    }
}
