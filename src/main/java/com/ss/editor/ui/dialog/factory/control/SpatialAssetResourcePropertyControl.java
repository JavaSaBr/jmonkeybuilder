package com.ss.editor.ui.dialog.factory.control;

import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.editor.util.EditorUtil.toAssetPath;
import static com.ss.editor.util.NodeUtils.findParent;
import static com.ss.rlib.util.ClassUtils.unsafeCast;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.scene.Spatial;
import com.ss.editor.FileExtensions;
import com.ss.editor.ui.dialog.factory.PropertyDefinition;
import com.ss.rlib.util.VarTable;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.scene.control.Label;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    public SpatialAssetResourcePropertyControl(@NotNull final VarTable vars,
                                                  @NotNull final PropertyDefinition definition,
                                                  @NotNull final Runnable validationCallback) {
        super(vars, definition, validationCallback);
    }

    @NotNull
    @Override
    protected Array<String> getExtensions() {
        return EXTENSIONS;
    }

    @Override
    protected void processSelect(@NotNull final Path file) {

        final AssetManager assetManager = EDITOR.getAssetManager();

        final Path assetFile = notNull(getAssetFile(file));
        final ModelKey modelKey = new ModelKey(toAssetPath(assetFile));
        final T spatial = findResource(assetManager, modelKey);

        setPropertyValue(unsafeCast(spatial));

        super.processSelect(file);
    }

    /**
     * Finds a resource rom asset folder by the model key.
     *
     * @param assetManager the asset manager.
     * @param modelKey     the model key.
     * @return the target resource.
     */
    @Nullable
    protected T findResource(@NotNull final AssetManager assetManager, @NotNull final ModelKey modelKey) {
        return unsafeCast(assetManager.loadModel(modelKey));
    }

    @Override
    protected void reload() {

        final T model = getPropertyValue();
        final Spatial root = model == null ? null : findParent(model, spatial -> spatial.getKey() != null);
        final AssetKey key = root == null ? null : root.getKey();

        final Label resourceLabel = getResourceLabel();
        resourceLabel.setText(key == null ? NOT_SELECTED : key.getName() + "[" + model.getName() + "]");

        super.reload();
    }
}
