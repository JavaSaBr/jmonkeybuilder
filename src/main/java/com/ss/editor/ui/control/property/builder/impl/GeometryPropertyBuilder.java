package com.ss.editor.ui.control.property.builder.impl;

import com.jme3.asset.MaterialKey;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingSphere;
import com.jme3.bounding.BoundingVolume;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.builder.PropertyBuilder;
import com.ss.editor.ui.control.property.impl.DefaultPropertyControl;
import com.ss.editor.ui.control.property.impl.LodLevelPropertyControl;
import com.ss.editor.ui.control.property.impl.MaterialKeyPropertyControl;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.common.util.ExtMath;
import com.ss.rlib.common.util.StringUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import com.ss.rlib.fx.util.FXUtils;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * The implementation of the {@link PropertyBuilder} to build property controls for {@link Geometry} objects.
 *
 * @author JavaSaBr
 */
public class GeometryPropertyBuilder extends AbstractPropertyBuilder<ModelChangeConsumer> {

    @FunctionalInterface
    public interface CanEditMaterialChecker {

        boolean canEdit(@NotNull Geometry geometry);

        default @NotNull CanEditMaterialChecker negate() {
            return (geometry) -> !canEdit(geometry);
        }
    }
    /**
     * The list of additional checkers.
     */
    private static final Array<CanEditMaterialChecker> CAN_EDIT_MATERIAL_CHECKERS =
            ArrayFactory.newCopyOnModifyArray(CanEditMaterialChecker.class);

    /**
     * Register the additional checker which should return false if we can't edit material for a geometry.
     *
     * @param checker the additional checker.
     */
    @FromAnyThread
    public static void registerCanEditMaterialChecker(@NotNull CanEditMaterialChecker checker) {
        CAN_EDIT_MATERIAL_CHECKERS.add(checker.negate());
    }

    private static final BiConsumer<Geometry, MaterialKey> MATERIAL_APPLY_HANDLER = (geometry, materialKey) -> {

        var assetManager = EditorUtil.getAssetManager();

        if (materialKey == null) {

            var material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            material.setColor("Color", ColorRGBA.Gray);

            geometry.setMaterial(material);

        } else {
            var material = assetManager.loadAsset(materialKey);
            geometry.setMaterial(material);
        }
    };

    private static final Function<Geometry, MaterialKey> MATERIAL_SYNC_HANDLER = geometry -> {
        var material = geometry.getMaterial();
        return (MaterialKey) material.getKey();
    };

    private static final Function<BoundingVolume, String> BOUNDING_VOLUME_TO_STRING = boundingVolume -> {

        if (boundingVolume instanceof BoundingSphere) {

            var sphere = Messages.BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_SPHERE;
            var radius = Messages.BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_SPHERE_RADIUS;
            var boundingSphere = (BoundingSphere) boundingVolume;

            return sphere + ": [" + radius + "=" + boundingSphere.getRadius() + "]";

        } else if (boundingVolume instanceof BoundingBox) {

            var box = Messages.BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_BOX;
            var boundingBox = (BoundingBox) boundingVolume;
            var xExtent = ExtMath.cut(boundingBox.getXExtent(), 100);
            var yExtent = ExtMath.cut(boundingBox.getYExtent(), 100);
            var zExtent = ExtMath.cut(boundingBox.getZExtent(), 100);

            return box + ": [x=" + xExtent + ", y=" + yExtent + ", z=" + zExtent + "]";
        }

        return StringUtils.EMPTY;
    };

    private static final PropertyBuilder INSTANCE = new GeometryPropertyBuilder();

    @FromAnyThread
    public static @NotNull PropertyBuilder getInstance() {
        return INSTANCE;
    }

    private GeometryPropertyBuilder() {
        super(ModelChangeConsumer.class);
    }

    @Override
    @FxThread
    protected void buildForImpl(
            @NotNull Object object,
            @Nullable Object parent,
            @NotNull VBox container,
            @NotNull ModelChangeConsumer changeConsumer
    ) {

        if (!(object instanceof Geometry)) {
            return;
        }

        var geometry = (Geometry) object;
        var modelBound = geometry.getModelBound();
        var lodLevel = geometry.getLodLevel();

        final DefaultPropertyControl<ModelChangeConsumer, Geometry, BoundingVolume> boundingVolumeControl =
                new DefaultPropertyControl<>(modelBound, Messages.BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_NAME, changeConsumer);

        boundingVolumeControl.setToStringFunction(BOUNDING_VOLUME_TO_STRING);
        boundingVolumeControl.reload();
        boundingVolumeControl.setEditObject(geometry);

        if (canEditMaterial(geometry)) {

            final Material material = geometry.getMaterial();
            final MaterialKey materialKey = (MaterialKey) material.getKey();

            final MaterialKeyPropertyControl<ModelChangeConsumer, Geometry> materialControl =
                    new MaterialKeyPropertyControl<>(materialKey, Messages.MODEL_PROPERTY_MATERIAL, changeConsumer);

            materialControl.setApplyHandler(MATERIAL_APPLY_HANDLER);
            materialControl.setSyncHandler(MATERIAL_SYNC_HANDLER);
            materialControl.setEditObject(geometry);

            FXUtils.addToPane(materialControl, container);
        }

        FxUtils.addChild(container, boundingVolumeControl);

        buildSplitLine(container);

        final LodLevelPropertyControl<ModelChangeConsumer> lodLevelControl =
                new LodLevelPropertyControl<>(lodLevel, Messages.MODEL_PROPERTY_LOD, changeConsumer);

        lodLevelControl.setApplyHandler(Geometry::setLodLevel);
        lodLevelControl.setSyncHandler(Geometry::getLodLevel);
        lodLevelControl.setEditObject(geometry, true);

        FxUtils.addChild(container, lodLevelControl);
    }

    /**
     * Can edit material boolean.
     *
     * @param geometry the geometry.
     * @return true if we can editor the material.
     */
    @FromAnyThread
    private boolean canEditMaterial(@NotNull Geometry geometry) {
        return CAN_EDIT_MATERIAL_CHECKERS.search(geometry, CanEditMaterialChecker::canEdit) == null;
    }
}
