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
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.DefaultModelPropertyControl;
import com.ss.editor.ui.control.model.property.MaterialKeyModelPropertyEditor;
import com.ss.editor.ui.control.model.property.ModelPropertyControl;
import com.ss.editor.ui.control.model.property.builder.PropertyBuilder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import rlib.ui.util.FXUtils;
import rlib.util.StringUtils;
import tonegod.emitter.geometry.ParticleGeometry;

/**
 * The implementation of the {@link PropertyBuilder} for building property controls for {@link Geometry} objects.
 *
 * @author JavaSaBr
 */
public class GeometryPropertyBuilder extends AbstractPropertyBuilder {

    private static final BiConsumer<Geometry, MaterialKey> MATERIAL_APPLY_HANDLER = (geometry, materialKey) -> {

        final AssetManager assetManager = EDITOR.getAssetManager();

        if (materialKey == null) {

            final Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            material.setColor("Color", ColorRGBA.Gray);

            geometry.setMaterial(material);

        } else {

            assetManager.deleteFromCache(materialKey);

            final Material material = assetManager.loadAsset(materialKey);
            geometry.setMaterial(material);
        }
    };

    private static final Function<Geometry, MaterialKey> MATERIAL_SYNC_HANDLER = geometry -> {
        final Material material = geometry.getMaterial();
        return (MaterialKey) material.getKey();
    };

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

    private static final PropertyBuilder INSTANCE = new GeometryPropertyBuilder();

    public static PropertyBuilder getInstance() {
        return INSTANCE;
    }

    @Override
    public void buildFor(@NotNull final Object object, @Nullable final Object parent, @NotNull final VBox container,
                         @NotNull final ModelChangeConsumer modelChangeConsumer) {

        if (!(object instanceof Geometry)) return;

        final Geometry geometry = (Geometry) object;
        final Material material = geometry.getMaterial();
        final MaterialKey materialKey = (MaterialKey) material.getKey();
        final BoundingVolume modelBound = geometry.getModelBound();

        final ModelPropertyControl<Geometry, MaterialKey> materialControl =
                new MaterialKeyModelPropertyEditor<>(materialKey, Messages.MODEL_PROPERTY_MATERIAL, modelChangeConsumer);
        materialControl.setApplyHandler(MATERIAL_APPLY_HANDLER);
        materialControl.setSyncHandler(MATERIAL_SYNC_HANDLER);
        materialControl.setEditObject(geometry);

        final DefaultModelPropertyControl<BoundingVolume> boundingVolumeControl =
                new DefaultModelPropertyControl<>(modelBound, Messages.BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_NAME, modelChangeConsumer);
        boundingVolumeControl.setToStringFunction(BOUNDING_VOLUME_TO_STRING);
        boundingVolumeControl.reload();
        boundingVolumeControl.setEditObject(geometry);

        final Line splitLine = createSplitLine(container);

        if (canEditMaterial(geometry)) FXUtils.addToPane(materialControl, container);
        FXUtils.addToPane(boundingVolumeControl, container);
        FXUtils.addToPane(splitLine, container);

        VBox.setMargin(splitLine, SPLIT_LINE_OFFSET);
    }

    protected boolean canEditMaterial(final Geometry geometry) {
        return !(geometry instanceof ParticleGeometry);
    }
}
