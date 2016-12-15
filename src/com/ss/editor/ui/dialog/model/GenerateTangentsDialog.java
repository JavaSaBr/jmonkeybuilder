package com.ss.editor.ui.dialog.model;

import static javafx.collections.FXCollections.observableArrayList;

import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.ss.editor.Messages;
import com.ss.editor.model.tool.TangentGenerator;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.operation.ChangeMeshOperation;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.dialog.EditorDialog;
import com.ss.editor.util.GeomUtils;

import org.jetbrains.annotations.NotNull;

import java.awt.Point;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;

/**
 * The implementation of a dialog for generating tangents.
 *
 * @author JavaSaBr
 */
public class GenerateTangentsDialog extends EditorDialog {

    private static final Insets OK_BUTTON_OFFSET = new Insets(0, 4, 0, 0);
    private static final Insets CANCEL_BUTTON_OFFSET = new Insets(0, 15, 0, 0);

    private static final Point DIALOG_SIZE = new Point(500, 160);

    private static final Insets ALGORITHM_OFFSET = new Insets(15, 0, 0, 0);
    private static final Insets SPLIT_OFFSET = new Insets(8, 0, 10, 150);

    public enum AlgorithmType {
        STANDARD,
        MIKKTSPACE;
        public static final AlgorithmType[] VALUES = values();
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
     * The list of types.
     */
    private ComboBox<AlgorithmType> algorithmTypeComboBox;

    /**
     * The check box about spliting morrored.
     */
    private CheckBox splitMirroredCheckBox;

    /**
     * The ok button.
     */
    private Button okButton;

    public GenerateTangentsDialog(final ModelNodeTree nodeTree, final ModelNode<?> node) {
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
        return Messages.GENERATE_TANGENTS_DIALOG_TITLE;
    }

    @Override
    protected void createContent(@NotNull final VBox root) {
        super.createContent(root);

        root.setAlignment(Pos.CENTER_LEFT);

        final HBox algorithmTypeContainer = new HBox();

        final Label algorithmTypeLabel = new Label(Messages.GENERATE_TANGENTS_DIALOG_ALGORITHM_LABEL + ":");
        algorithmTypeLabel.setId(CSSIds.GENERATE_TANGENTS_DIALOG_LABEL);

        algorithmTypeComboBox = new ComboBox<>(observableArrayList(AlgorithmType.VALUES));
        algorithmTypeComboBox.setId(CSSIds.GENERATE_TANGENTS_DIALOG_COMBO_BOX);

        final SingleSelectionModel<AlgorithmType> selectionModel = algorithmTypeComboBox.getSelectionModel();
        selectionModel.select(AlgorithmType.STANDARD);

        FXUtils.addToPane(algorithmTypeLabel, algorithmTypeContainer);
        FXUtils.addToPane(algorithmTypeComboBox, algorithmTypeContainer);
        FXUtils.addToPane(algorithmTypeContainer, root);

        splitMirroredCheckBox = new CheckBox(Messages.GENERATE_TANGENTS_DIALOG_SPLIT_MIRRORED);
        splitMirroredCheckBox.setId(CSSIds.GENERATE_TANGENTS_DIALOG_COMBO_BOX);
        splitMirroredCheckBox.disableProperty().bind(selectionModel.selectedItemProperty().isNotEqualTo(AlgorithmType.STANDARD));

        FXUtils.addToPane(splitMirroredCheckBox, root);

        FXUtils.addClassTo(algorithmTypeLabel, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(splitMirroredCheckBox, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(splitMirroredCheckBox, CSSClasses.SPECIAL_FONT_13);

        VBox.setMargin(algorithmTypeContainer, ALGORITHM_OFFSET);
        VBox.setMargin(splitMirroredCheckBox, SPLIT_OFFSET);
    }

    @Override
    protected void processKey(@NotNull final KeyEvent event) {
        super.processKey(event);
        if (event.getCode() == KeyCode.ENTER) {
            processOk();
        }
    }

    @Override
    protected void createActions(@NotNull final VBox root) {
        super.createActions(root);

        final HBox container = new HBox();
        container.setId(CSSIds.ASSET_EDITOR_DIALOG_BUTTON_CONTAINER);

        okButton = new Button(Messages.GENERATE_TANGENTS_DIALOG_BUTTON_OK);
        okButton.setId(CSSIds.EDITOR_DIALOG_BUTTON_OK);
        okButton.setOnAction(event -> processOk());

        final Button cancelButton = new Button(Messages.GENERATE_TANGENTS_DIALOG_BUTTON_CANCEL);
        cancelButton.setId(CSSIds.EDITOR_DIALOG_BUTTON_CANCEL);
        cancelButton.setOnAction(event -> hide());

        FXUtils.addToPane(okButton, container);
        FXUtils.addToPane(cancelButton, container);
        FXUtils.addToPane(container, root);

        FXUtils.addClassTo(okButton, CSSClasses.SPECIAL_FONT_16);
        FXUtils.addClassTo(cancelButton, CSSClasses.SPECIAL_FONT_16);

        HBox.setMargin(okButton, OK_BUTTON_OFFSET);
        HBox.setMargin(cancelButton, CANCEL_BUTTON_OFFSET);
    }

    /**
     * @return the check box about spliting morrored.
     */
    private CheckBox getSplitMirroredCheckBox() {
        return splitMirroredCheckBox;
    }

    /**
     * @return the list of types.
     */
    private ComboBox<AlgorithmType> getAlgorithmTypeComboBox() {
        return algorithmTypeComboBox;
    }

    /**
     * Handle generating.
     */
    private void processOk() {

        final ModelNodeTree nodeTree = getNodeTree();
        final ModelChangeConsumer modelChangeConsumer = nodeTree.getModelChangeConsumer();

        final ModelNode<?> node = getNode();
        final Geometry geometry = (Geometry) node.getElement();
        final Mesh newMesh = geometry.getMesh();
        final Mesh oldMesh = newMesh.deepClone();

        final ComboBox<AlgorithmType> algorithmTypeComboBox = getAlgorithmTypeComboBox();
        final AlgorithmType algorithmType = algorithmTypeComboBox.getSelectionModel().getSelectedItem();

        if (algorithmType == AlgorithmType.STANDARD) {
            final CheckBox splitMirroredCheckBox = getSplitMirroredCheckBox();
            TangentGenerator.useStandardGenerator(geometry, splitMirroredCheckBox.isSelected());
        } else {
            TangentGenerator.useMikktspaceGenerator(geometry);
        }

        final int index = GeomUtils.getIndex(modelChangeConsumer.getCurrentModel(), geometry);

        modelChangeConsumer.execute(new ChangeMeshOperation(newMesh, oldMesh, index));

        hide();
    }

    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
