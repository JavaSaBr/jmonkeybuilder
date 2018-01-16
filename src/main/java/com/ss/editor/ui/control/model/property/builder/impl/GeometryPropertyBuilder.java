package com.ss.editor.ui.control.model.property.builder.impl;

import static com.ss.editor.util.EditorUtil.clipNumber;
import com.jme3.asset.AssetManager;
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
import com.ss.editor.ui.control.property.builder.impl.AbstractPropertyBuilder;
import com.ss.editor.ui.control.property.impl.DefaultPropertyControl;
import com.ss.editor.ui.control.property.impl.LodLevelPropertyControl;
import com.ss.editor.ui.control.property.impl.MaterialKeyPropertyControl;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The implementation of the {@link PropertyBuilder} to build property controls for {@link Geometry} objects.
 *
 * @author JavaSaBr
 */
public class GeometryPropertyBuilder extends AbstractPropertyBuilder<ModelChangeConsumer> {

    /**
     * The list of additional checkers.
     */
    @NotNull
    private static final Array<Predicate<@NotNull Geometry>> CAN_EDIT_MATERIAL_CHECKERS = ArrayFactory.newArray(Predicate.class);

    /**
     * Register the additional checker which should return false if we can't edit material for a geometry.
     *
     * @param checker the additional checker.
     */
    @FxThread
    public static void registerCanEditMaterialChecker(@NotNull final Predicate<@NotNull Geometry> checker) {
        CAN_EDIT_MATERIAL_CHECKERS.add(checker.negate());
    }

    @NotNull
    private static final BiConsumer<Geometry, MaterialKey> MATERIAL_APPLY_HANDLER = (geometry, materialKey) -> {

        final AssetManager assetManager = JME_APPLICATION.getAssetManager();

        if (materialKey == null) {

            final Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            material.setColor("Color", ColorRGBA.Gray);

            geometry.setMaterial(material);

        } else {
            final Material material = assetManager.loadAsset(materialKey);
            geometry.setMaterial(material);
        }
    };

    @NotNull
    private static final Function<Geometry, MaterialKey> MATERIAL_SYNC_HANDLER = geometry -> {
        final Material material = geometry.getMaterial();
        return (MaterialKey) material.getKey();
    };

    @NotNull
    private static final Function<BoundingVolume, String> BOUNDING_VOLUME_TO_STRING = boundingVolume -> {

        if (boundingVolume instanceof BoundingSphere) {
            final BoundingSphere boundingSphere = (BoundingSphere) boundingVolume;
            return Messages.BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_SPHERE + ": [" +
                    Messages.BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_SPHERE_RADIUS + "=" + boundingSphere.getRadius() + "]";
        } else if (boundingVolume instanceof BoundingBox) {

            final BoundingBox boundingBox = (BoundingBox) boundingVolume;

            final float xExtent = clipNumber(boundingBox.getXExtent(), 100);
            final float yExtent = clipNumber(boundingBox.getYExtent(), 100);
            final float zExtent = clipNumber(boundingBox.getZExtent(), 100);

            return Messages.BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_BOX +
                    ": [x=" + xExtent + ", y=" + yExtent + ", z=" + zExtent + "]";
        }

        return StringUtils.EMPTY;
    };

    @NotNull
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
    protected void buildForImpl(@NotNull final Object object, @Nullable final Object parent,
                                @NotNull final VBox container, @NotNull final ModelChangeConsumer changeConsumer) {

        if (!(object instanceof Geometry)) return;

        final Geometry geometry = (Geometry) object;
        final BoundingVolume modelBound = geometry.getModelBound();
        final int lodLevel = geometry.getLodLevel();

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

        FXUtils.addToPane(boundingVolumeControl, container);

        buildSplitLine(container);

        final LodLevelPropertyControl<ModelChangeConsumer> lodLevelControl =
                new LodLevelPropertyControl<>(lodLevel, Messages.MODEL_PROPERTY_LOD, changeConsumer);

        lodLevelControl.setApplyHandler(Geometry::setLodLevel);
        lodLevelControl.setSyncHandler(Geometry::getLodLevel);
        lodLevelControl.setEditObject(geometry, true);

        FXUtils.addToPane(lodLevelControl, container);
    }

    /**
     * Can edit material boolean.
     *
     * @param geometry the geometry.
     * @return true if we can editor the material.
     */
    @FxThread
    private boolean canEditMaterial(@NotNull final Geometry geometry) {
        return CAN_EDIT_MATERIAL_CHECKERS.search(geometry, Predicate::test) == null;
    }
}
