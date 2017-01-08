package com.ss.editor.ui.control.model.tree.dialog;

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
import com.ss.editor.util.GeomUtils;

import org.jetbrains.annotations.NotNull;

import java.awt.Point;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
public class GenerateTangentsDialog extends AbstractNodeDialog {

    private static final ObservableList<AlgorithmType> ALGORITHM_TYPES = observableArrayList(AlgorithmType.VALUES);

    private static final Point DIALOG_SIZE = new Point(530, 154);

    private static final Insets FIELD_OFFSET = new Insets(6, CANCEL_BUTTON_OFFSET.getRight(), 0, 0);
    private static final Insets LAST_FIELD_OFFSET = new Insets(FIELD_OFFSET.getTop(),
            CANCEL_BUTTON_OFFSET.getRight(), 20, 0);

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
        algorithmTypeLabel.setId(CSSIds.SETTINGS_DIALOG_LABEL);

        algorithmTypeComboBox = new ComboBox<>(GenerateTangentsDialog.ALGORITHM_TYPES);
        algorithmTypeComboBox.setId(CSSIds.SETTINGS_DIALOG_FIELD);
        algorithmTypeComboBox.prefWidthProperty().bind(root.widthProperty());

        final SingleSelectionModel<AlgorithmType> selectionModel = algorithmTypeComboBox.getSelectionModel();
        selectionModel.select(AlgorithmType.STANDARD);

        FXUtils.addToPane(algorithmTypeLabel, algorithmTypeContainer);
        FXUtils.addToPane(algorithmTypeComboBox, algorithmTypeContainer);
        FXUtils.addToPane(algorithmTypeContainer, root);

        final HBox splitMirroredContainer = new HBox();

        final Label splitMirroredLabel = new Label(Messages.GENERATE_TANGENTS_DIALOG_SPLIT_MIRRORED + ":");
        splitMirroredLabel.setId(CSSIds.SETTINGS_DIALOG_LABEL);

        splitMirroredCheckBox = new CheckBox();
        splitMirroredCheckBox.setId(CSSIds.SETTINGS_DIALOG_FIELD);
        splitMirroredCheckBox.disableProperty().bind(selectionModel.selectedItemProperty().isNotEqualTo(AlgorithmType.STANDARD));

        FXUtils.addToPane(splitMirroredLabel, splitMirroredContainer);
        FXUtils.addToPane(splitMirroredCheckBox, splitMirroredContainer);
        FXUtils.addToPane(splitMirroredContainer, root);

        FXUtils.addClassTo(algorithmTypeLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(algorithmTypeComboBox, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(splitMirroredLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(splitMirroredCheckBox, CSSClasses.SPECIAL_FONT_14);

        VBox.setMargin(algorithmTypeContainer, FIELD_OFFSET);
        VBox.setMargin(splitMirroredContainer, LAST_FIELD_OFFSET);
    }

    @Override
    protected void processKey(@NotNull final KeyEvent event) {
        super.processKey(event);
        if (event.getCode() == KeyCode.ENTER) {
            processOk();
        }
    }

    /**
     * @return the check box about spliting mirrored.
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

    @Override
    protected void processOk() {

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

    @NotNull
    @Override
    protected String getButtonOkLabel() {
        return Messages.GENERATE_TANGENTS_DIALOG_BUTTON_OK;
    }

    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
