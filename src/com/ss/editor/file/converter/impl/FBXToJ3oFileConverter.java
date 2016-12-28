package com.ss.editor.file.converter.impl;

import com.jme3.asset.AssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.scene.Spatial;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.file.converter.FileConverterDescription;
import com.ss.editor.util.EditorUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The implementation of {@link AbstractFileConverter} for converting .blend file to .j3o.
 *
 * @author JavaSaBr
 */
public class FBXToJ3oFileConverter extends AbstractFileConverter {

    private static final Array<String> EXTENSIONS = ArrayFactory.newArray(String.class);

    static {
        EXTENSIONS.add(FileExtensions.FBX);
        EXTENSIONS.asUnsafe().trimToSize();
    }

    public static final FileConverterDescription DESCRIPTION = new FileConverterDescription();

    static {
        DESCRIPTION.setDescription(Messages.FBX_TO_J3O_FILE_CONVERTER_DESCRIPTION);
        DESCRIPTION.setConstructor(FBXToJ3oFileConverter::new);
        DESCRIPTION.setExtensions(EXTENSIONS);
    }

    private FBXToJ3oFileConverter() {
    }

    @Override
    protected void convertImpl(@NotNull final Path source, @NotNull final Path destination, final boolean overwrite) {

        final Path assetFile = Objects.requireNonNull(EditorUtil.getAssetFile(source),
                "Not found asset file for " + source);

        final ModelKey modelKey = new ModelKey(assetFile.toString());

        final AssetManager assetManager = EDITOR.getAssetManager();
        assetManager.clearAssetEventListeners();

        final Spatial model = assetManager.loadAsset(modelKey);
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

    @NotNull
    @Override
    protected Array<String> getAvailableExtensions() {
        return EXTENSIONS;
    }

    @NotNull
    @Override
    public String getTargetExtension() {
        return FileExtensions.JME_OBJECT;
    }
}
