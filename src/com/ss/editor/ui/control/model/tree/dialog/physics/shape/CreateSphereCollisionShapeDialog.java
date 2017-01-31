package com.ss.editor.ui.control.model.tree.dialog.physics.shape;

import static java.util.Objects.requireNonNull;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
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
 * The dialog to create sphere collision shape.
 *
 * @author JavaSaBr
 */
public class CreateSphereCollisionShapeDialog extends AbstractCreateShapeDialog {

    private static final Point DIALOG_SIZE = new Point(400, 124);

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
    protected void createContent(@NotNull final VBox root) {
        super.createContent(root);

        final Label radiusLabel = new Label("Radius:");
        radiusLabel.setId(CSSIds.EDITOR_DIALOG_SHORT_LABEL);

        radiusField = new FloatTextField();
        radiusField.prefWidthProperty().bind(widthProperty());
        radiusField.setId(CSSIds.SETTINGS_DIALOG_FIELD);
        radiusField.setMinMax(0, Integer.MAX_VALUE);
        radiusField.setValue(1);

        final GridPane container = new GridPane();
        container.add(radiusLabel, 0, 0);
        container.add(radiusField, 1, 0);

        FXUtils.addClassTo(radiusLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(radiusField, CSSClasses.SPECIAL_FONT_14);

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
