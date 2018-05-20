package com.ss.editor.plugin.api.property.control;

import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.editor.util.EditorUtil.toAssetPath;
import static com.ss.rlib.common.util.ClassUtils.unsafeCast;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.asset.AssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.scene.Spatial;
import com.ss.editor.FileExtensions;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.common.util.FileUtils;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
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

    @NotNull
    private static final Array<String> EXTENSIONS = ArrayFactory.newArray(String.class);

    static {
        EXTENSIONS.add(FileExtensions.JME_OBJECT);
    }

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
    protected void chooseNew(@NotNull Path file) {

        var assetManager = EditorUtil.getAssetManager();

        var assetFile = notNull(getAssetFile(file));
        var modelKey = new ModelKey(toAssetPath(assetFile));
        var spatial = findResource(assetManager, modelKey);

        setPropertyValue(unsafeCast(spatial));

        super.chooseNew(file);
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
    protected void handleFile(@NotNull File file) {
        chooseNew(file.toPath());
    }

    @Override
    @FxThread
    public void reload() {

        var model = getPropertyValue();
        var rootKey = EditorUtil.findRootKey(model);

        var resourceLabel = getResourceLabel();
        resourceLabel.setText(rootKey == null ? NOT_SELECTED : rootKey + "[" + model.getName() + "]");

        super.reload();
    }
}
