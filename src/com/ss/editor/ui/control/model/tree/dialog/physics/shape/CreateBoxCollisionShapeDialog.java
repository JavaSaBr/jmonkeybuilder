package com.ss.editor.ui.control.model.tree.dialog.physics.shape;

import static java.util.Objects.requireNonNull;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.math.Vector3f;
import com.ss.editor.Messages;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.ss.rlib.ui.control.input.FloatTextField;
import com.ss.rlib.ui.util.FXUtils;

import java.awt.*;

/**
 * The dialog to create a box collision shape.
 *
 * @author JavaSaBr
 */
public class CreateBoxCollisionShapeDialog extends AbstractCreateShapeDialog {

    private static final Point DIALOG_SIZE = new Point(400, 184);

    /**
     * The x size.
     */
    @Nullable
    private FloatTextField xField;

    /**
     * The y size.
     */
    @Nullable
    private FloatTextField yField;

    /**
     * The z size.
     */
    @Nullable
    private FloatTextField zField;

    /**
     * Instantiates a new Create box collision shape dialog.
     *
     * @param nodeTree        the node tree
     * @param collisionObject the collision object
     */
    public CreateBoxCollisionShapeDialog(@NotNull final AbstractNodeTree<?> nodeTree,
                                         @NotNull final PhysicsCollisionObject collisionObject) {
        super(nodeTree, collisionObject);
    }

    @Override
    protected void createContent(@NotNull final GridPane root) {
        super.createContent(root);

        final Label xLabel = new Label("x:");
        xLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        xLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        xField = new FloatTextField();
        xField.setId(CSSIds.EDITOR_DIALOG_FIELD);
        xField.setMinMax(0, Integer.MAX_VALUE);
        xField.setScrollPower(5F);
        xField.setValue(1);
        xField.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        final Label yLabel = new Label("y:");
        yLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        yLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        yField = new FloatTextField();
        yField.setId(CSSIds.EDITOR_DIALOG_FIELD);
        yField.setMinMax(0, Integer.MAX_VALUE);
        yField.setScrollPower(5F);
        yField.setValue(1);
        yField.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        final Label zLabel = new Label("z:");
        zLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        zLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        zField = new FloatTextField();
        zField.setId(CSSIds.EDITOR_DIALOG_FIELD);
        zField.setMinMax(0, Integer.MAX_VALUE);
        zField.setScrollPower(5F);
        zField.setValue(1);
        zField.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        root.add(xLabel, 0, 0);
        root.add(xField, 1, 0);
        root.add(yLabel, 0, 1);
        root.add(yField, 1, 1);
        root.add(zLabel, 0, 2);
        root.add(zField, 1, 2);

        FXUtils.addClassTo(xLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(yLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(zLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(xField, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(yField, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(zField, CSSClasses.SPECIAL_FONT_14);
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.CREATE_BOX_COLLISION_SHAPE_DIALOG_TITLE;
    }

    /**
     * Gets x field.
     *
     * @return the x size.
     */
    @NotNull
    protected FloatTextField getXField() {
        return requireNonNull(xField);
    }

    /**
     * Gets y field.
     *
     * @return the y size.
     */
    @NotNull
    protected FloatTextField getYField() {
        return requireNonNull(yField);
    }

    /**
     * Gets z field.
     *
     * @return the z size.
     */
    @NotNull
    protected FloatTextField getZField() {
        return requireNonNull(zField);
    }

    @NotNull
    @Override
    protected CollisionShape createShape() {

        final FloatTextField xField = getXField();
        final FloatTextField yField = getYField();
        final FloatTextField zField = getZField();

        return new BoxCollisionShape(new Vector3f(xField.getValue(), yField.getValue(), zField.getValue()));
    }

    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
