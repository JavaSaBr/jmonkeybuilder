package com.ss.builder.plugin.api.property.control;

import static com.ss.builder.util.EditorUtils.toAssetPath;
import static com.ss.rlib.common.util.ClassUtils.unsafeCast;
import com.jme3.asset.AssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.scene.Spatial;
import com.ss.builder.FileExtensions;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.util.EditorUtils;
import com.ss.builder.FileExtensions;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.plugin.api.property.PropertyDefinition;
import com.ss.builder.util.EditorUtils;
import com.ss.rlib.common.util.FileUtils;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.common.util.array.Array;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;

/**
 * The control to edit spatial values from asset.
 *
 * @author JavaSaBr
 */
public class SpatialAssetResourcePropertyControl<T extends Spatial> extends AssetResourcePropertyEditorControl<T> {

    private static final Array<String> EXTENSIONS =
            Array.of(FileExtensions.JME_OBJECT);

    public SpatialAssetResourcePropertyControl(
            @NotNull VarTable vars,
            @NotNull PropertyDefinition definition,
            @NotNull Runnable validationCallback
    ) {
        super(vars, definition, validationCallback);
    }

    @Override
    @FromAnyThread
    protected @NotNull Array<String> getExtensions() {
        return EXTENSIONS;
    }

    @Override
    @FxThread
    protected void chooseNewResource(@NotNull Path file) {

        var assetManager = EditorUtils.getAssetManager();

        var assetFile = EditorUtils.requireAssetFile(file);
        var modelKey = new ModelKey(EditorUtils.toAssetPath(assetFile));
        var spatial = findResource(assetManager, modelKey);

        setPropertyValue(unsafeCast(spatial));

        super.chooseNewResource(file);
    }

    /**
     * Find a resource rom asset folder by the model key.
     *
     * @param assetManager the asset manager.
     * @param modelKey     the model key.
     * @return the target resource.
     */
    @FxThread
    protected @Nullable T findResource(@NotNull AssetManager assetManager, @NotNull ModelKey modelKey) {
        return unsafeCast(assetManager.loadModel(modelKey));
    }

    @Override
    @FxThread
    protected boolean canAccept(@NotNull File file) {
        return EXTENSIONS.contains(FileUtils.getExtension(file.getName()));
    }

    @Override
    @FxThread
    protected void reloadImpl() {

        var model = getPropertyValue();
        var rootKey = EditorUtils.findRootKey(model);

        resourceLabel.setText(rootKey == null ? NOT_SELECTED : rootKey + "[" + model.getName() + "]");

        super.reloadImpl();
    }
}
