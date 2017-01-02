package com.ss.editor.ui.control.model.tree.dialog.geometry.lod;

import static javafx.collections.FXCollections.observableArrayList;

import com.ss.editor.Messages;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.dialog.AbstractNodeDialog;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import org.jetbrains.annotations.NotNull;

import java.awt.Point;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;

/**
 * The implementation of a dialog for generating lod levels.
 *
 * @author JavaSaBr
 */
public class GenerateLodLevelsDialog extends AbstractNodeDialog {

    private static final ObservableList<ReductionMethod> ALGORITHM_TYPES = observableArrayList(ReductionMethod.VALUES);

    private static final Point DIALOG_SIZE = new Point(530, 154);

    private static final Insets FIELD_OFFSET = new Insets(6, CANCEL_BUTTON_OFFSET.getRight(), 0, 0);
    private static final Insets LAST_FIELD_OFFSET = new Insets(FIELD_OFFSET.getTop(),
            CANCEL_BUTTON_OFFSET.getRight(), 20, 0);

    public enum ReductionMethod {
        PROPORTIONAL,
        CONSTANT;
        public static final ReductionMethod[] VALUES = values();
    }

    /**
     * The model tree component.
     */
    private final ModelNodeTree nodeTree;

    /**
     * The generated node.
     */
    private final ModelNode<?> node;

    /**
     * The list of reduction methods.
     */
    private ComboBox<ReductionMethod> reductionMethodComboBox;

    public GenerateLodLevelsDialog(final ModelNodeTree nodeTree, final ModelNode<?> node) {
        this.nodeTree = nodeTree;
        this.node = node;
    }

    /**
     * @return the model tree component.
     */
    protected ModelNodeTree getNodeTree() {
        return nodeTree;
    }

    /**
     * @return the generated node.
     */
    protected ModelNode<?> getNode() {
        return node;
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return "LOD";
    }

    @Override
    protected void createContent(@NotNull final VBox root) {
        super.createContent(root);

        root.setAlignment(Pos.CENTER_LEFT);

        final HBox algorithmTypeContainer = new HBox();

        final Label algorithmTypeLabel = new Label(Messages.GENERATE_TANGENTS_DIALOG_ALGORITHM_LABEL + ":");
        algorithmTypeLabel.setId(CSSIds.SETTINGS_DIALOG_LABEL);

        reductionMethodComboBox = new ComboBox<>(GenerateLodLevelsDialog.ALGORITHM_TYPES);
        reductionMethodComboBox.setId(CSSIds.SETTINGS_DIALOG_FIELD);
        reductionMethodComboBox.prefWidthProperty().bind(root.widthProperty());

        final SingleSelectionModel<ReductionMethod> selectionModel = reductionMethodComboBox.getSelectionModel();
        selectionModel.select(ReductionMethod.PROPORTIONAL);

        FXUtils.addToPane(algorithmTypeLabel, algorithmTypeContainer);
        FXUtils.addToPane(reductionMethodComboBox, algorithmTypeContainer);
        FXUtils.addToPane(algorithmTypeContainer, root);


        FXUtils.addClassTo(algorithmTypeLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(reductionMethodComboBox, CSSClasses.SPECIAL_FONT_14);

        VBox.setMargin(algorithmTypeContainer, FIELD_OFFSET);
    }

    @Override
    protected void processKey(@NotNull final KeyEvent event) {
        super.processKey(event);
        if (event.getCode() == KeyCode.ENTER) {
            processOk();
        }
    }

    /**
     * @return the list of reduction methods.
     */
    private ComboBox<ReductionMethod> getReductionMethodComboBox() {
        return reductionMethodComboBox;
    }

    @Override
    protected void processOk() {


        hide();
    }

    @Override
    protected String getButtonOkLabel() {
        return "Generate";
    }

    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
