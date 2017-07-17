package com.ss.editor.file.converter.impl;

import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.editor.util.EditorUtil.toAssetPath;
import static com.ss.rlib.util.FileUtils.containsExtensions;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.ss.editor.FileExtensions;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.tool.TangentGenerator;
import com.ss.editor.serializer.MaterialSerializer;
import com.ss.editor.ui.dialog.converter.ModelConverterDialog;
import com.ss.editor.ui.scene.EditorFXScene;
import com.ss.editor.util.EditorUtil;
import com.ss.editor.util.NodeUtils;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import com.ss.rlib.util.dictionary.DictionaryFactory;
import com.ss.rlib.util.dictionary.ObjectDictionary;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The base implementation of a file converter.
 *
 * @author JavaSaBr
 */
public abstract class AbstractModelFileConverter extends AbstractFileConverter {

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
        final ModelConverterDialog dialog = new ModelConverterDialog(source, destination, settings -> convert(source, settings));
        dialog.show(scene.getWindow());
    }

    /**
     * Convert a file using settings from the dialog.
     */
    @FXThread
    private void convert(@NotNull final Path source, @NotNull final ModelConverterDialog dialog) {

        final EditorFXScene scene = JFX_APPLICATION.getScene();
        scene.incrementLoading();

        EXECUTOR_MANAGER.addBackgroundTask(() -> convertImpl(source, dialog));
    }

    /**
     * Convert a file using settings from the dialog.
     */
    @BackgroundThread
    private void convertImpl(@NotNull final Path source, @NotNull final ModelConverterDialog dialog) {

        final String filename = dialog.getFilename();
        final Path destinationFolder = dialog.getDestinationFolder();
        final Path destination = destinationFolder.resolve(filename);
        final boolean isOverwrite = Files.exists(destination);

        final Path assetFile = notNull(getAssetFile(source), "Not found asset file for " + source);
        final ModelKey modelKey = new ModelKey(assetFile.toString());

        final AssetManager assetManager = EDITOR.getAssetManager();
        final Spatial model = assetManager.loadAsset(modelKey);

        if (EDITOR_CONFIG.isAutoTangentGenerating()) {
            TangentGenerator.useMikktspaceGenerator(model);
        }

        if (dialog.isExportMaterials()) {

            final Array<Geometry> geometries = ArrayFactory.newArray(Geometry.class);
            final ObjectDictionary<String, Geometry> mapping = DictionaryFactory.newObjectDictionary();

            final Path materialsFolder = dialog.getMaterialsFolder();
            final boolean canOverwrite = dialog.isOverwriteMaterials();

            NodeUtils.visitGeometry(model, geometry -> checkAndAdd(geometries, geometry));
            geometries.forEach(geometry -> generateNames(mapping, geometry));
            mapping.forEach((materialName, geometry) -> storeMaterials(materialsFolder, canOverwrite, materialName, geometry));
        }

        final BinaryExporter exporter = BinaryExporter.getInstance();

        try (final OutputStream out = Files.newOutputStream(destination)) {
            exporter.save(model, out);
        } catch (final IOException e) {
            LOGGER.warning(this, e);
        }

        if (isOverwrite) {
            notifyFileChanged(destination);
        } else {
            notifyFileCreated(destination);
        }
    }

    /**
     * Store embedded materials.
     *
     * @param materialsFolder the materials destination folder.
     * @param canOverwrite    can we overwrite exists materials.
     * @param materialName    the material name.
     * @param geometry        the geometry.
     */
    private void storeMaterials(@NotNull final Path materialsFolder, final boolean canOverwrite,
                                @NotNull final String materialName, @NotNull final Geometry geometry) {

        final Path resultFile = materialsFolder.resolve(materialName + "." + FileExtensions.JME_MATERIAL);
        final Material currentMaterial = geometry.getMaterial();

        if (!Files.exists(resultFile) || canOverwrite) {
            try (PrintWriter pout = new PrintWriter(Files.newOutputStream(resultFile))) {
                pout.println(MaterialSerializer.serializeToString(currentMaterial));
            } catch (final IOException e) {
                EditorUtil.handleException(LOGGER, this, e);
            }
        }

        final Path assetFile = notNull(getAssetFile(resultFile));
        final String assetPath = toAssetPath(assetFile);

        final AssetManager assetManager = EDITOR.getAssetManager();
        geometry.setMaterial(assetManager.loadMaterial(assetPath));
    }

    /**
     * Generate names for materials.
     */
    private void generateNames(@NotNull final ObjectDictionary<String, Geometry> mapping,
                               @NotNull final Geometry geometry) {

        final Material material = geometry.getMaterial();
        final String originalName = material.getName();
        final String name = StringUtils.isEmpty(geometry.getName()) ? "geom" : geometry.getName();

        String resultName = StringUtils.isEmpty(originalName) ? "embedded-mat-" + name : originalName;

        if (!mapping.containsKey(resultName)) {
            mapping.put(resultName, geometry);
        } else {
            for (int i = 1; mapping.containsKey(resultName); i++) {
                resultName = (StringUtils.isEmpty(originalName) ? "embedded-mat-" : originalName) + name + "_" + i;
            }
            mapping.put(resultName, geometry);
        }
    }

    private void checkAndAdd(@NotNull final Array<Geometry> geometries, @NotNull final Geometry geometry) {
        final Material material = geometry.getMaterial();
        final AssetKey key = material.getKey();
        if (key == null) geometries.add(geometry);
    }
}
