package com.ss.editor.ui.control.model.tree.dialog.particle.emitter.shape;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.shapes.EmitterBoxShape;
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
 * The dialog to create a box shape.
 *
 * @author JavaSaBr
 */
public class CreateBoxShapeDialog extends CreatePointShapeDialog {

    private static final Point DIALOG_SIZE = new Point(400, 156);

    /**
     * The x max size field.
     */
    @Nullable
    private FloatTextField xMaxField;

    /**
     * The y max size field.
     */
    @Nullable
    private FloatTextField yMaxField;

    /**
     * The z max size field.
     */
    @Nullable
    private FloatTextField zMaxField;

    /**
     * Instantiates a new Create box shape dialog.
     *
     * @param nodeTree the node tree
     * @param emitter  the emitter
     */
    public CreateBoxShapeDialog(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ParticleEmitter emitter) {
        super(nodeTree, emitter);
    }

    @Override
    protected void createContent(@NotNull final GridPane root) {
        super.createContent(root);

        final Label maxLabel = new Label(Messages.CONTROL_PROPERTY_MAX + ":");
        maxLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        maxLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT3));

        xMaxField = new FloatTextField();
        xMaxField.setId(CSSIds.EDITOR_DIALOG_FIELD);
        xMaxField.setMinMax(0, Integer.MAX_VALUE);
        xMaxField.setScrollPower(5F);
        xMaxField.setValue(1);

        yMaxField = new FloatTextField();
        yMaxField.setId(CSSIds.EDITOR_DIALOG_FIELD);
        yMaxField.setMinMax(0, Integer.MAX_VALUE);
        yMaxField.setScrollPower(5F);
        yMaxField.setValue(1);

        zMaxField = new FloatTextField();
        zMaxField.setId(CSSIds.EDITOR_DIALOG_FIELD);
        zMaxField.setMinMax(0, Integer.MAX_VALUE);
        zMaxField.setScrollPower(5F);
        zMaxField.setValue(1);

        root.add(maxLabel, 0, 1);
        root.add(xMaxField, 1, 1);
        root.add(yMaxField, 2, 1);
        root.add(zMaxField, 3, 1);

        FXUtils.addClassTo(maxLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(xMaxField, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(yMaxField, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(zMaxField, CSSClasses.SPECIAL_FONT_14);
    }

    @NotNull
    @Override
    protected String getPointLabel() {
        return Messages.CONTROL_PROPERTY_MIN;
    }

    @NotNull
    @Override
    protected EmitterShape createEmitterShape() {

        final FloatTextField xMinField = getXPointField();
        final FloatTextField yMinField = getYPointField();
        final FloatTextField zMinField = getZPointField();

        final FloatTextField xMaxField = getXMaxField();
        final FloatTextField yMaxField = getYMaxField();
        final FloatTextField zMaxField = getZMaxField();

        final Vector3f min = new Vector3f(xMinField.getValue(), yMinField.getValue(), zMinField.getValue());
        final Vector3f max = new Vector3f(xMaxField.getValue(), yMaxField.getValue(), zMaxField.getValue());

        return new EmitterBoxShape(min, max);
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.CREATE_EMITTER_BOX_SHAPE_DIALOG_TITLE;
    }

    /**
     * Gets x max size field.
     *
     * @return the x max size field.
     */
    @NotNull
    protected FloatTextField getXMaxField() {
        return notNull(xMaxField);
    }

    /**
     * Gets y max size field.
     *
     * @return the y max size field.
     */
    @NotNull
    protected FloatTextField getYMaxField() {
        return notNull(yMaxField);
    }

    /**
     * Gets z max size field.
     *
     * @return the z max size field.
     */
    @NotNull
    protected FloatTextField getZMaxField() {
        return notNull(zMaxField);
    }

    @NotNull
    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
