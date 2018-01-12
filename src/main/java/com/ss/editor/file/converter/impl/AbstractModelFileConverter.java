package com.ss.editor.file.converter.impl;

import static com.ss.editor.extension.property.EditablePropertyType.*;
import static com.ss.editor.util.EditorUtil.*;
import static com.ss.rlib.util.FileUtils.containsExtensions;
import static com.ss.rlib.util.FileUtils.normalizeName;
import static com.ss.rlib.util.ObjectUtils.notNull;
import static java.nio.file.StandardOpenOption.*;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.tool.TangentGenerator;
import com.ss.editor.plugin.api.dialog.GenericFactoryDialog;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.serializer.MaterialSerializer;
import com.ss.editor.util.EditorUtil;
import com.ss.editor.util.NodeUtils;
import com.ss.rlib.util.FileUtils;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.VarTable;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import com.ss.rlib.util.dictionary.DictionaryFactory;
import com.ss.rlib.util.dictionary.ObjectDictionary;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
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

    @NotNull
    private static final Point DIALOG_SIZE = new Point(500, -1);

    private static final String PROP_RESULT_NAME = "resultName";
    private static final String PROP_DESTINATION = "destination";
    private static final String PROP_EXPORT_MATERIALS = "exportMaterials";
    private static final String PROP_MATERIALS_FOLDER = "materialsFolder";
    private static final String PROP_OVERWRITE_MATERIALS = "overwriteMaterials";

    @NotNull
    private static final Array<String> MATERIAL_DEPS = ArrayFactory.asArray(PROP_EXPORT_MATERIALS);

    @Override
    public void convert(@NotNull final Path source, @NotNull final Path destination) {

        if (Files.isDirectory(source) || Files.isDirectory(destination)) {
            throw new IllegalArgumentException("source or destination is folder.");
        }

        final Array<String> extensions = getAvailableExtensions();
        if (!extensions.isEmpty() && !containsExtensions(extensions.array(), source)) {
            throw new IllegalArgumentException("incorrect extension of file " + source);
        }

        final String resultName = FileUtils.getNameWithoutExtension(source);
        final Path assetDestination = getAssetFile(destination.getParent());

        final Array<PropertyDefinition> definitions = ArrayFactory.newArray(PropertyDefinition.class);
        definitions.add(new PropertyDefinition(STRING, Messages.MODEL_CONVERTER_DIALOG_RESULT_NAME, PROP_RESULT_NAME, resultName));
        definitions.add(new PropertyDefinition(FOLDER_FROM_ASSET_FOLDER, Messages.MODEL_CONVERTER_DIALOG_DESTINATION_FOLDER, PROP_DESTINATION, assetDestination));
        definitions.add(new PropertyDefinition(BOOLEAN, Messages.MODEL_CONVERTER_DIALOG_EXPORT_MATERIALS, PROP_EXPORT_MATERIALS, false));
        definitions.add(new PropertyDefinition(FOLDER_FROM_ASSET_FOLDER, MATERIAL_DEPS, Messages.MODEL_CONVERTER_DIALOG_MATERIAL_FOLDER, PROP_MATERIALS_FOLDER, null));
        definitions.add(new PropertyDefinition(BOOLEAN, MATERIAL_DEPS, Messages.MODEL_CONVERTER_DIALOG_OVERWRITE_MATERIALS, PROP_OVERWRITE_MATERIALS, false));

        final GenericFactoryDialog dialog = new GenericFactoryDialog(definitions, vars -> convert(source, vars), this::validate);
        dialog.setButtonOkText(Messages.SIMPLE_DIALOG_BUTTON_CONVERT);
        dialog.setTitle(Messages.MODEL_CONVERTER_DIALOG_TITLE);
        dialog.configureSize(DIALOG_SIZE);
        dialog.show();
    }

    /**
     * Validate the settings.
     *
     * @param vars the variables.
     * @return true if all are ok.
     */
    @FxThread
    private boolean validate(@NotNull final VarTable vars) {

        if (!vars.has(PROP_RESULT_NAME) || !vars.has(PROP_DESTINATION)) {
            return false;
        }

        final String resultName = vars.getString(PROP_RESULT_NAME);
        if (StringUtils.isEmpty(resultName)) {
            return false;
        }

        final boolean exportMaterials = vars.getBoolean(PROP_EXPORT_MATERIALS);
        return !exportMaterials || vars.has(PROP_MATERIALS_FOLDER);
    }

    /**
     * Convert a file using settings from the dialog.
     */
    @FxThread
    private void convert(@NotNull final Path source, @NotNull final VarTable vars) {
        EditorUtil.incrementLoading();
        EXECUTOR_MANAGER.addBackgroundTask(() -> {
            try {
                convertImpl(source, vars);
            } catch (final Exception e) {
                EditorUtil.handleException(LOGGER, this, e);
                EXECUTOR_MANAGER.addFXTask(EditorUtil::decrementLoading);
            }
        });
    }

    /**
     * Convert a file using settings from the dialog.
     */
    @BackgroundThread
    private void convertImpl(@NotNull final Path source, @NotNull final VarTable vars) throws IOException {

        final String filename = vars.getString(PROP_RESULT_NAME);
        final Path destinationFolder = notNull(getRealFile(vars.get(PROP_DESTINATION, Path.class)));
        final Path destination = destinationFolder.resolve(filename + "." + FileExtensions.JME_OBJECT);
        final boolean isOverwrite = Files.exists(destination);

        final Path assetFile = notNull(getAssetFile(source), "Not found asset file for " + source);
        final ModelKey modelKey = new ModelKey(toAssetPath(assetFile));

        final AssetManager assetManager = JME_APPLICATION.getAssetManager();
        final Spatial model = assetManager.loadAsset(modelKey);

        if (EDITOR_CONFIG.isAutoTangentGenerating()) {
            TangentGenerator.useMikktspaceGenerator(model);
        }

        if (vars.getBoolean(PROP_EXPORT_MATERIALS)) {

            final Array<Geometry> geometries = ArrayFactory.newArray(Geometry.class);
            final ObjectDictionary<String, Geometry> mapping = DictionaryFactory.newObjectDictionary();

            final Path materialsFolder = vars.get(PROP_MATERIALS_FOLDER);
            final boolean canOverwrite = vars.getBoolean(PROP_OVERWRITE_MATERIALS);

            NodeUtils.visitGeometry(model, geometry -> checkAndAdd(geometries, geometry));
            geometries.forEach(geometry -> generateNames(mapping, geometry));
            mapping.forEach((materialName, geometry) -> storeMaterials(materialsFolder, canOverwrite, materialName, geometry));
        }

        final BinaryExporter exporter = BinaryExporter.getInstance();

        try (final OutputStream out = Files.newOutputStream(destination, WRITE, TRUNCATE_EXISTING, CREATE)) {
            exporter.save(model, out);
        }

        if (isOverwrite) {
            notifyFileChanged(destination);
        } else {
            notifyFileCreated(destination);
        }
    }

    /**
     * Store the embedded materials.
     *
     * @param materialsFolder the materials destination folder.
     * @param canOverwrite    can we overwrite exists materials.
     * @param materialName    the material name.
     * @param geometry        the geometry.
     */
    private void storeMaterials(@NotNull final Path materialsFolder, final boolean canOverwrite,
                                @NotNull final String materialName, @NotNull final Geometry geometry) {

        final Path resultFile = materialsFolder.resolve(normalizeName(materialName) + "." + FileExtensions.JME_MATERIAL);
        final Path assetFile = getAssetFile(resultFile);

        if (assetFile == null) {
            LOGGER.warning("Can't get asset file for the file " + resultFile);
            return;
        }

        final Material currentMaterial = geometry.getMaterial();

        if (!Files.exists(resultFile) || canOverwrite) {
            try (PrintWriter pout = new PrintWriter(Files.newOutputStream(resultFile, WRITE, TRUNCATE_EXISTING, CREATE))) {
                pout.println(MaterialSerializer.serializeToString(currentMaterial));
            } catch (final IOException e) {
                EditorUtil.handleException(LOGGER, this, e);
            }
        }

        final String assetPath = toAssetPath(assetFile);

        final AssetManager assetManager = JME_APPLICATION.getAssetManager();
        geometry.setMaterial(assetManager.loadMaterial(assetPath));
    }

    /**
     * Generate names for the materials.
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

        if (key == null) {
            geometries.add(geometry);
        }
    }
}
