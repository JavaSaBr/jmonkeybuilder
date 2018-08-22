package com.ss.builder.plugin.api.property.control;

import com.jme3.asset.AssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.scene.Geometry;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.plugin.api.property.PropertyDefinition;
import com.ss.builder.util.NodeUtils;
import com.ss.rlib.common.util.VarTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The control to edit spatial values from asset.
 *
 * @author JavaSaBr
 */
public class GeometryAssetResourcePropertyControl extends SpatialAssetResourcePropertyControl<Geometry> {

    public GeometryAssetResourcePropertyControl(
            @NotNull VarTable vars,
            @NotNull PropertyDefinition definition,
            @NotNull Runnable validationCallback
    ) {
        super(vars, definition, validationCallback);
    }

    @Override
    @FxThread
    protected @Nullable Geometry findResource(@NotNull AssetManager assetManager, @NotNull ModelKey modelKey) {
        return NodeUtils.findGeometry(assetManager.loadModel(modelKey));
    }
}
