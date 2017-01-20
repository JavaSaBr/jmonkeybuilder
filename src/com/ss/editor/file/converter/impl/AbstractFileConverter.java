package com.ss.editor.file.converter.impl;

import static rlib.util.FileUtils.containsExtensions;

import com.jme3.asset.AssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.scene.Spatial;
import com.ss.editor.Editor;
import com.ss.editor.JFXApplication;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.file.converter.FileConverter;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.tool.TangentGenerator;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.scene.EditorFXScene;
import com.ss.editor.util.EditorUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import rlib.logging.Logger;
import rlib.logging.LoggerManager;
import rlib.util.FileUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The base implementation of a file converter.
 *
 * @author JavaSaBr.
 */
public abstract class AbstractFileConverter implements FileConverter {

    protected static final Logger LOGGER = LoggerManager.getLogger(FileConverter.class);

    private static final Array<String> EMPTY_ARRAY = ArrayFactory.newArray(String.class);

    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();
    protected static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();
    protected static final JFXApplication JFX_APPLICATION = JFXApplication.getInstance();
    protected static final EditorConfig EDITOR_CONFIG = EditorConfig.getInstance();
    protected static final Editor EDITOR = Editor.getInstance();

    @Override
    public void convert(@NotNull final Path source) {

        final String targetFileName = FileUtils.getNameWithoutExtension(source) + "." + getTargetExtension();

        final Path parent = source.getParent();
        final Path targetFile = parent.resolve(targetFileName);

        convert(source, targetFile);
    }

    @Override
    public void convert(@NotNull final Path source, @NotNull final Path destination) {

        if (Files.isDirectory(source) || Files.isDirectory(destination)) {
            throw new IllegalArgumentException("source or destination is folder.");
        }

        final Array<String> extensions = getAvailableExtensions();
        if (!extensions.isEmpty() && !containsExtensions(extensions.array(), source)) {
            throw new IllegalArgumentException("incorrect extension of file " + source);
        }

        final EditorFXScene scene = JFX_APPLICATION.getScene();
        scene.incrementLoading();

        EXECUTOR_MANAGER.addBackgroundTask(() -> {
            try {
                convertImpl(source, destination, Files.exists(destination));
            } catch (final Exception e) {
                EditorUtil.handleException(LOGGER, this, e);
                EXECUTOR_MANAGER.addFXTask(() -> notifyFileCreatedImpl(null));
            }
        });
    }

    protected void convertImpl(@NotNull final Path source, @NotNull final Path destination, final boolean overwrite) {

        final Path assetFile = Objects.requireNonNull(EditorUtil.getAssetFile(source),
                "Not found asset file for " + source);

        final ModelKey modelKey = new ModelKey(assetFile.toString());

        final AssetManager assetManager = EDITOR.getAssetManager();
        assetManager.clearAssetEventListeners();

        final Spatial model = assetManager.loadAsset(modelKey);

        if (EDITOR_CONFIG.isAutoTangentGenerating()) {
            TangentGenerator.useMikktspaceGenerator(model);
        }

        final BinaryExporter exporter = BinaryExporter.getInstance();

        try (final OutputStream out = Files.newOutputStream(destination)) {
            exporter.save(model, out);
        } catch (final IOException e) {
            LOGGER.warning(this, e);
        }

        if (overwrite) {
            notifyFileChanged(destination);
        } else {
            notifyFileCreated(destination);
        }
    }

    /**
     * @return the list of available extensions.
     */
    @NotNull
    protected Array<String> getAvailableExtensions() {
        return EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getTargetExtension() {
        return "";
    }

    /**
     * Notify the Editor about file changing.
     *
     * @param file the changed file.
     */
    protected void notifyFileChanged(@NotNull final Path file) {
        EXECUTOR_MANAGER.addFXTask(() -> notifyFileChangedImpl(file));
    }

    private void notifyFileChangedImpl(@NotNull final Path file) {
        final EditorFXScene scene = JFX_APPLICATION.getScene();
        scene.decrementLoading();
    }

    /**
     * Notify the Editor about file creating.
     *
     * @param file the created file.
     */
    protected void notifyFileCreated(@Nullable final Path file) {
        EXECUTOR_MANAGER.addFXTask(() -> notifyFileCreatedImpl(file));
    }

    private void notifyFileCreatedImpl(@Nullable final Path file) {
        final EditorFXScene scene = JFX_APPLICATION.getScene();
        scene.decrementLoading();
    }
}
