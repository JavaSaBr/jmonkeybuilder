package com.ss.editor.ui.control.model.tree.dialog;

import static com.ss.rlib.util.ObjectUtils.notNull;
import static javafx.collections.FXCollections.observableArrayList;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.ss.editor.Messages;
import com.ss.editor.model.tool.TangentGenerator;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.model.tree.action.operation.ChangeMeshOperation;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import com.ss.rlib.ui.util.FXUtils;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * The implementation of a dialog for generating tangents.
 *
 * @author JavaSaBr
 */
public class GenerateTangentsDialog extends AbstractSimpleEditorDialog {

    @NotNull
    private static final ObservableList<AlgorithmType> ALGORITHM_TYPES = observableArrayList(AlgorithmType.VALUES);

    @NotNull
    private static final Point DIALOG_SIZE = new Point(530, -1);

    /**
     * The enum Algorithm type.
     */
    public enum AlgorithmType {
        /**
         * Standard algorithm type.
         */
        STANDARD,
        /**
         * Mikktspace algorithm type.
         */
        MIKKTSPACE;

        /**
         * The constant VALUES.
         */
        public static final AlgorithmType[] VALUES = values();
    }

    /**
     * The node tree component.
     */
    @NotNull
    private final AbstractNodeTree<?> nodeTree;

    /**
     * The generated node.
     */
    @NotNull
    private final ModelNode<?> node;

    /**
     * The list of types.
     */
    @Nullable
    private ComboBox<AlgorithmType> algorithmTypeComboBox;

    /**
     * The check box about spliting mirrored.
     */
    @Nullable
    private CheckBox splitMirroredCheckBox;

    /**
     * Instantiates a new Generate tangents dialog.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public GenerateTangentsDialog(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        this.nodeTree = nodeTree;
        this.node = node;
    }

    /**
     * Gets node tree.
     *
     * @return the node tree component.
     */
    @NotNull
    protected AbstractNodeTree<?> getNodeTree() {
        return nodeTree;
    }

    /**
     * Gets node.
     *
     * @return the generated node.
     */
    @NotNull
    protected ModelNode<?> getNode() {
        return node;
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.GENERATE_TANGENTS_DIALOG_TITLE;
    }

    @Override
    protected void createContent(@NotNull final GridPane root) {
        super.createContent(root);

        final Label algorithmTypeLabel = new Label(Messages.GENERATE_TANGENTS_DIALOG_ALGORITHM_LABEL + ":");
        algorithmTypeLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT2));

        algorithmTypeComboBox = new ComboBox<>(GenerateTangentsDialog.ALGORITHM_TYPES);
        algorithmTypeComboBox.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT2));

        final SingleSelectionModel<AlgorithmType> selectionModel = algorithmTypeComboBox.getSelectionModel();
        selectionModel.select(AlgorithmType.STANDARD);

        final Label splitMirroredLabel = new Label(Messages.GENERATE_TANGENTS_DIALOG_SPLIT_MIRRORED + ":");
        splitMirroredLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT2));

        splitMirroredCheckBox = new CheckBox();
        splitMirroredCheckBox.disableProperty().bind(selectionModel.selectedItemProperty().isNotEqualTo(AlgorithmType.STANDARD));
        splitMirroredCheckBox.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT2));

        root.add(algorithmTypeLabel, 0, 0);
        root.add(algorithmTypeComboBox, 1, 0);
        root.add(splitMirroredLabel, 0, 1);
        root.add(splitMirroredCheckBox, 1, 1);

        FXUtils.addClassTo(algorithmTypeLabel, splitMirroredLabel, CSSClasses.DIALOG_DYNAMIC_LABEL);
        FXUtils.addClassTo(algorithmTypeComboBox, splitMirroredCheckBox, CSSClasses.DIALOG_FIELD);
    }

    @Override
    protected boolean isGridStructure() {
        return true;
    }

    /**
     * @return the check box about spliting mirrored.
     */
    @NotNull
    private CheckBox getSplitMirroredCheckBox() {
        return notNull(splitMirroredCheckBox);
    }

    /**
     * @return the list of types.
     */
    @NotNull
    private ComboBox<AlgorithmType> getAlgorithmTypeComboBox() {
        return notNull(algorithmTypeComboBox);
    }

    @Override
    protected void processOk() {

        final AbstractNodeTree<?> nodeTree = getNodeTree();

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

        final ChangeConsumer changeConsumer = notNull(nodeTree.getChangeConsumer());
        changeConsumer.execute(new ChangeMeshOperation(newMesh, oldMesh, geometry));

        super.processOk();
    }

    @NotNull
    @Override
    protected String getButtonOkLabel() {
        return Messages.GENERATE_TANGENTS_DIALOG_BUTTON_OK;
    }

    @NotNull
    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
