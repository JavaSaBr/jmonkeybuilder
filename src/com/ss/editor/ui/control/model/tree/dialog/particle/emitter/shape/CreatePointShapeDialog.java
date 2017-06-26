package com.ss.editor.ui.control.model.tree.dialog.particle.emitter.shape;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.shapes.EmitterPointShape;
import com.jme3.effect.shapes.EmitterShape;
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
 * The dialog to create a point shape.
 *
 * @author JavaSaBr
 */
public class CreatePointShapeDialog extends AbstractCreateShapeDialog {

    private static final Point DIALOG_SIZE = new Point(400, 129);

    /**
     * The x point size.
     */
    @Nullable
    private FloatTextField xPointField;

    /**
     * The y point size.
     */
    @Nullable
    private FloatTextField yPointField;

    /**
     * The z point size.
     */
    @Nullable
    private FloatTextField zPointField;

    /**
     * Instantiates a new Create point shape dialog.
     *
     * @param nodeTree the node tree
     * @param emitter  the emitter
     */
    public CreatePointShapeDialog(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ParticleEmitter emitter) {
        super(nodeTree, emitter);
    }

    @Override
    protected void createContent(@NotNull final GridPane root) {
        super.createContent(root);

        final Label minLabel = new Label(getPointLabel() + ":");
        minLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        minLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT3));

        xPointField = new FloatTextField();
        xPointField.setId(CSSIds.EDITOR_DIALOG_FIELD);
        xPointField.setMinMax(0, Integer.MAX_VALUE);
        xPointField.setScrollPower(5F);
        xPointField.setValue(1);
        xPointField.prefWidthProperty().bind(root.widthProperty()
                .multiply(DEFAULT_FIELD_W_PERCENT4).multiply(0.33));

        yPointField = new FloatTextField();
        yPointField.setId(CSSIds.EDITOR_DIALOG_FIELD);
        yPointField.setMinMax(0, Integer.MAX_VALUE);
        yPointField.setScrollPower(5F);
        yPointField.setValue(1);
        yPointField.prefWidthProperty().bind(root.widthProperty()
                .multiply(DEFAULT_FIELD_W_PERCENT4).multiply(0.33));

        zPointField = new FloatTextField();
        zPointField.setId(CSSIds.EDITOR_DIALOG_FIELD);
        zPointField.setMinMax(0, Integer.MAX_VALUE);
        zPointField.setScrollPower(5F);
        zPointField.setValue(1);
        zPointField.prefWidthProperty().bind(root.widthProperty()
                .multiply(DEFAULT_FIELD_W_PERCENT4).multiply(0.33));

        root.add(minLabel, 0, 0);
        root.add(xPointField, 1, 0);
        root.add(yPointField, 2, 0);
        root.add(zPointField, 3, 0);

        FXUtils.addClassTo(minLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(xPointField, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(yPointField, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(zPointField, CSSClasses.SPECIAL_FONT_14);
    }

    /**
     * Gets point label.
     *
     * @return the point label
     */
    @NotNull
    protected String getPointLabel() {
        return Messages.MODEL_PROPERTY_POINT;
    }

    @NotNull
    @Override
    protected EmitterShape createEmitterShape() {

        final FloatTextField xMinField = getXPointField();
        final FloatTextField yMinField = getYPointField();
        final FloatTextField zMinField = getZPointField();

        final Vector3f point = new Vector3f(xMinField.getValue(), yMinField.getValue(), zMinField.getValue());

        return new EmitterPointShape(point);
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.CREATE_PARTICLE_EMITTER_POINT_SHAPE_DIALOG_TITLE;
    }

    /**
     * Gets x point field.
     *
     * @return the x point field.
     */
    @NotNull
    protected FloatTextField getXPointField() {
        return notNull(xPointField);
    }

    /**
     * Gets y point field.
     *
     * @return the y point field.
     */
    @NotNull
    protected FloatTextField getYPointField() {
        return notNull(yPointField);
    }

    /**
     * Gets z point field.
     *
     * @return the z point field.
     */
    @NotNull
    protected FloatTextField getZPointField() {
        return notNull(zPointField);
    }

    @NotNull
    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
