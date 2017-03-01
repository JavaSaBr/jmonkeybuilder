package com.ss.editor.ui.control.model.tree.dialog.physics.shape;

import static java.util.Objects.requireNonNull;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.ss.editor.Messages;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rlib.ui.control.input.FloatTextField;
import rlib.ui.util.FXUtils;

import java.awt.*;

/**
 * The dialog to create sphere collision shape.
 *
 * @author JavaSaBr
 */
public class CreateSphereCollisionShapeDialog extends AbstractCreateShapeDialog {

    private static final Point DIALOG_SIZE = new Point(400, 130);

    /**
     * The radius field.
     */
    @Nullable
    private FloatTextField radiusField;

    public CreateSphereCollisionShapeDialog(@NotNull final AbstractNodeTree<?> nodeTree,
                                            @NotNull final PhysicsCollisionObject collisionObject) {
        super(nodeTree, collisionObject);
    }

    @Override
    protected void createContent(@NotNull final GridPane root) {
        super.createContent(root);

        final Label radiusLabel = new Label(Messages.CONTROL_PROPERTY_RADIUS + ":");
        radiusLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        radiusLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        radiusField = new FloatTextField();
        radiusField.setId(CSSIds.EDITOR_DIALOG_FIELD);
        radiusField.setMinMax(0, Integer.MAX_VALUE);
        radiusField.setScrollPower(10F);
        radiusField.setValue(1);
        radiusField.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        root.add(radiusLabel, 0, 0);
        root.add(radiusField, 1, 0);

        FXUtils.addClassTo(radiusLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(radiusField, CSSClasses.SPECIAL_FONT_14);
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.CREATE_SPHERE_COLLISION_SHAPE_DIALOG_TITLE;
    }

    /**
     * @return the radius size.
     */
    @NotNull
    FloatTextField getRadiusField() {
        return requireNonNull(radiusField);
    }

    @NotNull
    @Override
    protected CollisionShape createShape() {
        final FloatTextField radiusField = getRadiusField();
        return new SphereCollisionShape(radiusField.getValue());
    }

    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
