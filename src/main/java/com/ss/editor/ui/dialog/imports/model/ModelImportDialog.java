package com.ss.editor.ui.dialog.imports.model;

import static com.ss.editor.extension.property.EditablePropertyType.*;
import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.editor.util.EditorUtil.toAssetPath;
import static com.ss.rlib.util.ObjectUtils.notNull;
import static java.nio.file.StandardOpenOption.*;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.asset.MaterialKey;
import com.jme3.asset.TextureKey;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.material.MatParam;
import com.jme3.material.MatParamTexture;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.asset.locator.FileSystemAssetLocator;
import com.ss.editor.asset.locator.FolderAssetLocator;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.manager.JmeFilePreviewManager;
import com.ss.editor.model.tool.TangentGenerator;
import com.ss.editor.plugin.api.file.creator.GenericFileCreator;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.serializer.MaterialSerializer;
import com.ss.editor.util.EditorUtil;
import com.ss.editor.util.NodeUtils;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.VarTable;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import com.ss.rlib.util.dictionary.DictionaryFactory;
import com.ss.rlib.util.dictionary.ObjectDictionary;
import javafx.beans.property.ObjectProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.function.Function;

/**
 * The implementation of a dialog to import external models.
 *
 * @author JavaSaBr
 */
public class ModelImportDialog extends GenericFileCreator {

    @NotNull
    private static final EditorConfig EDITOR_CONFIG = EditorConfig.getInstance();

    private static final String PROP_FILE = "file";
    private static final String PROP_NEED_MATERIALS_EXPORT = "needMaterialsExport";
    private static final String PROP_MATERIALS_FOLDER = "materialsFolder";
    private static final String PROP_OVERWRITE_MATERIALS = "overwriteMaterials";
    private static final String PROP_OVERWRITE_TEXTURES = "overwriteTextures";
    private static final String PROP_TEXTURES_FOLDER = "texturesFolder";

    @NotNull
    private static final Array<String> MATERIAL_EXPORT_DEPS = ArrayFactory.asArray(PROP_NEED_MATERIALS_EXPORT);

    /**
     * The image view to show preview of model.
     */
    @Nullable
    private ImageView imageView;

    /**
     * The rendered file.
     */
    @Nullable
    private Path renderedFile;

    public ModelImportDialog() {
        setTitleText(Messages.IMPORT_MODEL_DIALOG_TITLE);
    }

    @Override
    @FromAnyThread
    protected @NotNull String getButtonOkText() {
        return Messages.SIMPLE_DIALOG_BUTTON_IMPORT;
    }

    @Override
    @FromAnyThread
    protected @NotNull Array<PropertyDefinition> getPropertyDefinitions() {

        final Array<PropertyDefinition> result = ArrayFactory.newArray(PropertyDefinition.class);
        result.add(new PropertyDefinition(EXTERNAL_FILE, Messages.IMPORT_MODEL_DIALOG_EXTERNAL_FILE, PROP_FILE, null));
        result.add(new PropertyDefinition(FOLDER_FROM_ASSET_FOLDER, Messages.IMPORT_MODEL_DIALOG_TEXTURES_FOLDER, PROP_TEXTURES_FOLDER, null));
        result.add(new PropertyDefinition(BOOLEAN, Messages.IMPORT_MODEL_DIALOG_OVERWRITE_TEXTURES, PROP_OVERWRITE_TEXTURES, true));
        result.add(new PropertyDefinition(BOOLEAN, Messages.MODEL_CONVERTER_DIALOG_EXPORT_MATERIALS, PROP_NEED_MATERIALS_EXPORT, false));
        result.add(new PropertyDefinition(FOLDER_FROM_ASSET_FOLDER, MATERIAL_EXPORT_DEPS, Messages.MODEL_CONVERTER_DIALOG_MATERIAL_FOLDER, PROP_MATERIALS_FOLDER, null));
        result.add(new PropertyDefinition(BOOLEAN, MATERIAL_EXPORT_DEPS, Messages.MODEL_CONVERTER_DIALOG_OVERWRITE_MATERIALS, PROP_OVERWRITE_MATERIALS, false));

        return result;
    }

    @Override
    @FxThread
    protected void createPreview(@NotNull final BorderPane container) {
        super.createPreview(container);
        imageView = new ImageView();
        imageView.fitHeightProperty().bind(container.heightProperty().subtract(4));
        imageView.fitWidthProperty().bind(container.widthProperty().subtract(4));
        container.setCenter(imageView);
    }

    @Override
    @FromAnyThread
    protected boolean needPreview() {
        return true;
    }

    /**
     * Get the image view.
     *
     * @return the image view.
     */
    @FxThread
    private @NotNull ImageView getImageView() {
        return notNull(imageView);
    }

    @Override
    @FxThread
    protected boolean validate(@NotNull final VarTable vars) {

        final ImageView imageView = getImageView();

        if (!vars.has(PROP_FILE)) {
            imageView.setImage(null);
            return false;
        }

        final Path file = vars.get(PROP_FILE);

        if (!JmeFilePreviewManager.isModelFile(file)) {
            imageView.setImage(null);
            return false;
        }

        final Path renderedFile = getRenderedFile();
        if (renderedFile != null && file.equals(renderedFile)) {
            return super.validate(vars);
        }

        final int width = (int) imageView.getFitWidth();
        final int height = (int) imageView.getFitHeight();

        final JmeFilePreviewManager previewManager = JmeFilePreviewManager.getInstance();
        previewManager.showExternal(file, width, height);

        final ImageView sourceView = previewManager.getImageView();
        final ObjectProperty<Image> imageProperty = imageView.imageProperty();
        imageProperty.bind(sourceView.imageProperty());

        setRenderedFile(file);

        return super.validate(vars);
    }

    @Override
    @FxThread
    protected void processOk() {
        hide();
        EditorUtil.incrementLoading();
        EXECUTOR_MANAGER.addBackgroundTask(() -> {
            try {
                importModel();
            } finally {
                EXECUTOR_MANAGER.addFxTask(EditorUtil::decrementLoading);
            }
        });
    }

    @Override
    @FromAnyThread
    protected @NotNull String getFileExtension() {
        return FileExtensions.JME_OBJECT;
    }

    /**
     * Import the external model in a background thread.
     */
    @BackgroundThread
    private void importModel() {

        final Path modelFile = notNull(getFileToCreate());

        final Path parent = modelFile.getParent();
        final VarTable vars = getVars();

        final Path importedFile = vars.get(PROP_FILE);

        final AssetManager assetManager = JME_APPLICATION.getAssetManager();
        final Spatial model;

        FolderAssetLocator.setIgnore(true);
        try {
            model = assetManager.loadModel(importedFile.toString());
        } finally {
            FolderAssetLocator.setIgnore(false);
        }

        if (EDITOR_CONFIG.isAutoTangentGenerating()) {
            TangentGenerator.useMikktspaceGenerator(model);
        }

        final Path texturesFolder = vars.get(PROP_TEXTURES_FOLDER, parent);
        final boolean overwriteTextures = vars.getBoolean(PROP_OVERWRITE_TEXTURES);

        final boolean needExportMaterials = vars.getBoolean(PROP_NEED_MATERIALS_EXPORT);
        final Path materialsFolder = vars.get(PROP_MATERIALS_FOLDER, parent);
        final boolean overwriteMaterials = vars.getBoolean(PROP_OVERWRITE_MATERIALS, false);

        final Array<Texture> textures = ArrayFactory.newArray(Texture.class);
        final Array<Geometry> geometries = ArrayFactory.newArray(Geometry.class);

        NodeUtils.visitGeometry(model, geometry -> {

            final Material material = geometry.getMaterial();
            if (needExportMaterials) geometries.add(geometry);

            material.getParams().stream()
                    .filter(MatParamTexture.class::isInstance)
                    .map(MatParam::getValue)
                    .filter(Texture.class::isInstance)
                    .map(Texture.class::cast)
                    .filter(texture -> texture.getKey() != null)
                    .forEach(textures::add);
        });

        copyTextures(texturesFolder, overwriteTextures, textures);

        if (needExportMaterials) {
            exportMaterials(materialsFolder, overwriteMaterials, geometries);
        }

        final Path assetFile = notNull(getAssetFile(modelFile));
        final String assetPath = toAssetPath(assetFile);

        model.setName(assetPath);

        final BinaryExporter exporter = BinaryExporter.getInstance();

        try (final OutputStream out = Files.newOutputStream(modelFile, WRITE, TRUNCATE_EXISTING, CREATE)) {
            exporter.save(model, out);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        notifyFileCreated(modelFile, true);
    }

    /**
     * Export all embedded materials from the external model.
     *
     * @param materialsFolder    the materials folder.
     * @param overwriteMaterials true if we can overwrite existing materials.
     * @param geometries         the found geometries in the model.
     */
    @BackgroundThread
    private void exportMaterials(@NotNull final Path materialsFolder, final boolean overwriteMaterials,
                                 @NotNull final Array<Geometry> geometries) {

        if (geometries.isEmpty()) {
            return;
        }

        final ObjectDictionary<String, String> resultNameToKey = DictionaryFactory.newObjectDictionary();

        for (final Geometry geometry : geometries) {

            final Material material = geometry.getMaterial();
            final String originalName = material.getName();
            final String name = StringUtils.isEmpty(geometry.getName()) ? "geom" : geometry.getName();
            final String resultName = StringUtils.isEmpty(originalName) ? "embedded-mat-" + name : originalName;

            final String newKey = resultNameToKey.get(resultName);
            if (newKey != null) {
                material.setKey(new MaterialKey(newKey));
                continue;
            }

            final Path resultFile = materialsFolder.resolve(resultName + "." + FileExtensions.JME_MATERIAL);

            if (!Files.exists(resultFile) || overwriteMaterials) {
                try (PrintWriter pout = new PrintWriter(Files.newOutputStream(resultFile, WRITE, TRUNCATE_EXISTING, CREATE))) {
                    pout.println(MaterialSerializer.serializeToString(material));
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }

            final Path assetFile = notNull(getAssetFile(resultFile));
            final String assetPath = toAssetPath(assetFile);

            material.setKey(new MaterialKey(assetPath));
            resultNameToKey.put(resultName, assetPath);
        }
    }

    /**
     * Copy textures from the external model.
     *
     * @param texturesFolder    the textures folder.
     * @param overwriteTextures true if need to overwrite existing textures.
     * @param textures          the found textures.
     */
    @BackgroundThread
    private void copyTextures(@NotNull final Path texturesFolder, final boolean overwriteTextures,
                              @NotNull final Array<Texture> textures) {

        if (textures.isEmpty()) {
            return;
        }

        final ObjectDictionary<String, String> oldKeyToNew = DictionaryFactory.newObjectDictionary();
        final Array<AssetKey<?>> newTextureKeys = ArrayFactory.newArray(AssetKey.class);

        for (final Texture texture : textures) {

            final TextureKey textureKey = (TextureKey) texture.getKey();
            if (newTextureKeys.contains(textureKey)) continue;

            final String newKey = oldKeyToNew.get(textureKey.getName(), makeCopyTextureFunction(texturesFolder, overwriteTextures));

            final TextureKey newTextureKey = new TextureKey(newKey, textureKey.isFlipY());
            newTextureKey.setGenerateMips(textureKey.isGenerateMips());
            newTextureKey.setTextureTypeHint(textureKey.getTextureTypeHint());

            texture.setKey(newTextureKey);
            newTextureKeys.add(newTextureKey);
        }
    }

    /**
     * Create the copy texture function.
     *
     * @param texturesFolder    the textures folder.
     * @param overwriteTextures true if we can overwrite existing textures.
     * @return the new asset path.
     */
    @FromAnyThread
    private @NotNull Function<String, String> makeCopyTextureFunction(@NotNull final Path texturesFolder,
                                                                      final boolean overwriteTextures) {
        return oldPath -> {

            final Path textureFile = Paths.get(oldPath);
            final Path fileName = textureFile.getFileName();

            final Path newTextureFile = texturesFolder.resolve(fileName);
            final Path assetFile = notNull(getAssetFile(newTextureFile));
            final String assetPath = toAssetPath(assetFile);

            if (Files.exists(newTextureFile) && !overwriteTextures) {
                return assetPath;
            }

            try {
                Files.copy(textureFile, newTextureFile, StandardCopyOption.REPLACE_EXISTING);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }

            return assetPath;
        };
    }

    /**
     * Set the rendered file.
     *
     * @param renderedFile the rendered file.
     */
    @FxThread
    private void setRenderedFile(@Nullable final Path renderedFile) {
        this.renderedFile = renderedFile;
    }

    /**
     * Get the rendered file.
     *
     * @return the rendered file.
     */
    @FxThread
    private @Nullable Path getRenderedFile() {
        return renderedFile;
    }

    @Override
    @FxThread
    public void hide() {

        final JmeFilePreviewManager previewManager = JmeFilePreviewManager.getInstance();
        previewManager.clear();

        FileSystemAssetLocator.clear();

        super.hide();
    }
}
