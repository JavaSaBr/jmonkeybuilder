package com.ss.editor.ui.control.model.tree.dialog.particle.emitter.shape;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.shapes.EmitterShape;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.math.Vector3f;
import com.ss.editor.Messages;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.rlib.ui.control.input.FloatTextField;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * The dialog to create a sphere shape.
 *
 * @author JavaSaBr
 */
public class CreateSphereShapeDialog extends CreatePointShapeDialog {

    private static final Point DIALOG_SIZE = new Point(400, 156);

    /**
     * The radius field.
     */
    @Nullable
    private FloatTextField radiusField;

    /**
     * Instantiates a new Create sphere shape dialog.
     *
     * @param nodeTree the node tree
     * @param emitter  the emitter
     */
    public CreateSphereShapeDialog(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ParticleEmitter emitter) {
        super(nodeTree, emitter);
    }

    @Override
    protected void createContent(@NotNull final GridPane root) {
        super.createContent(root);

        final Label radiusLabel = new Label(Messages.CONTROL_PROPERTY_RADIUS + ":");
        radiusLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        radiusLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT3));

        radiusField = new FloatTextField();
        radiusField.setId(CSSIds.EDITOR_DIALOG_FIELD);
        radiusField.setMinMax(0, Integer.MAX_VALUE);
        radiusField.setScrollPower(5F);
        radiusField.setValue(1);
        radiusField.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        root.add(radiusLabel, 0, 1);
        root.add(radiusField, 1, 1, 3, 1);

        FXUtils.addClassTo(radiusLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(radiusField, CSSClasses.SPECIAL_FONT_14);
    }

    @NotNull
    @Override
    protected String getPointLabel() {
        return Messages.CONTROL_PROPERTY_CENTER;
    }

    @NotNull
    @Override
    protected EmitterShape createEmitterShape() {

        final FloatTextField xCenterField = getXPointField();
        final FloatTextField yCenterField = getYPointField();
        final FloatTextField zCenterField = getZPointField();

        final FloatTextField radiusField = getRadiusField();
        final Vector3f center = new Vector3f(xCenterField.getValue(), yCenterField.getValue(), zCenterField.getValue());

        return new EmitterSphereShape(center, radiusField.getValue());
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.CREATE_EMITTER_SPHERE_SHAPE_DIALOG_TITLE;
    }

    /**
     * Gets radius field.
     *
     * @return the radius field.
     */
    @NotNull
    protected FloatTextField getRadiusField() {
        return notNull(radiusField);
    }

    @NotNull
    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
