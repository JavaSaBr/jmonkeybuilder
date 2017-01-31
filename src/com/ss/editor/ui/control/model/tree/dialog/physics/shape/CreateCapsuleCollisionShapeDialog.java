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
import javafx.scene.layout.VBox;
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
public class CreateCapsuleCollisionShapeDialog extends AbstractCreateShapeDialog {

    private static final Point DIALOG_SIZE = new Point(400, 150);

    /**
     * The radius field.
     */
    @Nullable
    private FloatTextField radiusField;

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
    protected void createContent(@NotNull final VBox root) {
        super.createContent(root);

        final Label radiusLabel = new Label("Radius:");
        radiusLabel.setId(CSSIds.EDITOR_DIALOG_SHORT_LABEL);

        radiusField = new FloatTextField();
        radiusField.prefWidthProperty().bind(widthProperty());
        radiusField.setId(CSSIds.SETTINGS_DIALOG_FIELD);
        radiusField.setMinMax(0, Integer.MAX_VALUE);
        radiusField.setValue(1);

        final Label heightLabel = new Label("Height:");
        heightLabel.setId(CSSIds.EDITOR_DIALOG_SHORT_LABEL);

        heightField = new FloatTextField();
        heightField.prefWidthProperty().bind(widthProperty());
        heightField.setId(CSSIds.SETTINGS_DIALOG_FIELD);
        heightField.setMinMax(0, Integer.MAX_VALUE);
        heightField.setValue(1);

        final GridPane container = new GridPane();
        container.add(radiusLabel, 0, 0);
        container.add(radiusField, 1, 0);
        container.add(heightLabel, 0, 1);
        container.add(heightField, 1, 1);

        FXUtils.addClassTo(radiusLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(heightLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(radiusField, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(heightField, CSSClasses.SPECIAL_FONT_14);

        FXUtils.addToPane(container, root);

        VBox.setMargin(container, CONTAINER_OFFSET);
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return super.getTitleText();
    }

    /**
     * @return the radius size.
     */
    @NotNull
    private FloatTextField getRadiusField() {
        return requireNonNull(radiusField);
    }

    /**
     * @return the height size.
     */
    @NotNull
    private FloatTextField getHeightField() {
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
