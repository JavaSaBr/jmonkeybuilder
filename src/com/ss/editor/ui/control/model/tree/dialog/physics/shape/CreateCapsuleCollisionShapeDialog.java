package com.ss.editor.ui.control.model.tree.dialog.physics.shape;

import static java.util.Objects.requireNonNull;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
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
 * The dialog to create capsule collision shape.
 *
 * @author JavaSaBr
 */
public class CreateCapsuleCollisionShapeDialog extends CreateSphereCollisionShapeDialog {

    private static final Point DIALOG_SIZE = new Point(400, 150);

    /**
     * The height field.
     */
    @Nullable
    private FloatTextField heightField;

    public CreateCapsuleCollisionShapeDialog(@NotNull final AbstractNodeTree<?> nodeTree,
                                             @NotNull final PhysicsCollisionObject collisionObject) {
        super(nodeTree, collisionObject);
    }

    @Override
    protected void createContent(@NotNull final GridPane root) {
        super.createContent(root);

        final Label heightLabel = new Label("Height:");
        heightLabel.setId(CSSIds.EDITOR_DIALOG_SHORT_LABEL);

        heightField = new FloatTextField();
        heightField.prefWidthProperty().bind(widthProperty());
        heightField.setId(CSSIds.SETTINGS_DIALOG_FIELD);
        heightField.setMinMax(0, Integer.MAX_VALUE);
        heightField.setValue(1);

        root.add(heightLabel, 0, 1);
        root.add(heightField, 1, 1);

        FXUtils.addClassTo(heightLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(heightField, CSSClasses.SPECIAL_FONT_14);
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return super.getTitleText();
    }

    /**
     * @return the height size.
     */
    @NotNull
    FloatTextField getHeightField() {
        return requireNonNull(heightField);
    }

    @NotNull
    @Override
    protected CollisionShape createShape() {

        final FloatTextField radiusField = getRadiusField();
        final FloatTextField heightField = getHeightField();

        return new CapsuleCollisionShape(radiusField.getValue(), heightField.getValue());
    }

    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
