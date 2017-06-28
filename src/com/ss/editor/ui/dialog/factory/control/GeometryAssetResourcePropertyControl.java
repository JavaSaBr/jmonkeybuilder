package com.ss.editor.ui.dialog.factory.control;

import com.jme3.asset.AssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.ss.editor.ui.dialog.factory.PropertyDefinition;
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

    @Nullable
    @Override
    protected Geometry findResource(@NotNull final AssetManager assetManager, @NotNull final ModelKey modelKey) {
        final Spatial spatial = assetManager.loadModel(modelKey);
        return NodeUtils.findGeometry(spatial);
    }
}
