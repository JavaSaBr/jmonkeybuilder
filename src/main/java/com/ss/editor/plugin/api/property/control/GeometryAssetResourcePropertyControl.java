package com.ss.editor.plugin.api.property.control;

import com.jme3.asset.AssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.util.NodeUtils;
import com.ss.rlib.util.VarTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The control to edit spatial values from asset.
 *
 * @author JavaSaBr
 */
public class GeometryAssetResourcePropertyControl extends SpatialAssetResourcePropertyControl<Geometry> {

    public GeometryAssetResourcePropertyControl(@NotNull final VarTable vars,
                                                @NotNull final PropertyDefinition definition,
                                                @NotNull final Runnable validationCallback) {
        super(vars, definition, validationCallback);
    }

    @Override
    @FXThread
    protected @Nullable Geometry findResource(@NotNull final AssetManager assetManager, @NotNull final ModelKey modelKey) {
        final Spatial spatial = assetManager.loadModel(modelKey);
        return NodeUtils.findGeometry(spatial);
    }
}
