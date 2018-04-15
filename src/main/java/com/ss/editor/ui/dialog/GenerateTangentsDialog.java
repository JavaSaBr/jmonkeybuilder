package com.ss.editor.ui.dialog;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import static javafx.collections.FXCollections.observableArrayList;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.util.TangentGenerator;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.impl.ChangeMeshOperation;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.fx.util.FXUtils;
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
    private final NodeTree<?> nodeTree;

    /**
     * The generated node.
     */
    @NotNull
    private final TreeNode<?> node;

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

    public GenerateTangentsDialog(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        this.nodeTree = nodeTree;
        this.node = node;
    }

    /**
     * Gets node tree.
     *
     * @return the node tree component.
     */
    @FxThread
    protected @NotNull NodeTree<?> getNodeTree() {
        return nodeTree;
    }

    /**
     * Gets node.
     *
     * @return the generated node.
     */
    @FxThread
    protected @NotNull TreeNode<?> getNode() {
        return node;
    }

    @Override
    @FromAnyThread
    protected @NotNull String getTitleText() {
        return Messages.GENERATE_TANGENTS_DIALOG_TITLE;
    }

    @Override
    @FxThread
    protected void createContent(@NotNull final GridPane root) {
        super.createContent(root);

        final Label algorithmTypeLabel = new Label(Messages.GENERATE_TANGENTS_DIALOG_ALGORITHM_LABEL + ":");
        algorithmTypeLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT3));

        algorithmTypeComboBox = new ComboBox<>(GenerateTangentsDialog.ALGORITHM_TYPES);
        algorithmTypeComboBox.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT3));

        final SingleSelectionModel<AlgorithmType> selectionModel = algorithmTypeComboBox.getSelectionModel();
        selectionModel.select(AlgorithmType.MIKKTSPACE);

        final Label splitMirroredLabel = new Label(Messages.GENERATE_TANGENTS_DIALOG_SPLIT_MIRRORED + ":");
        splitMirroredLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT3));

        splitMirroredCheckBox = new CheckBox();
        splitMirroredCheckBox.disableProperty().bind(selectionModel.selectedItemProperty().isNotEqualTo(AlgorithmType.STANDARD));
        splitMirroredCheckBox.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT3));

        root.add(algorithmTypeLabel, 0, 0);
        root.add(algorithmTypeComboBox, 1, 0);
        root.add(splitMirroredLabel, 0, 1);
        root.add(splitMirroredCheckBox, 1, 1);

        FXUtils.addClassTo(algorithmTypeLabel, splitMirroredLabel, CssClasses.DIALOG_DYNAMIC_LABEL);
        FXUtils.addClassTo(algorithmTypeComboBox, splitMirroredCheckBox, CssClasses.DIALOG_FIELD);
    }

    @Override
    @FromAnyThread
    protected boolean isGridStructure() {
        return true;
    }

    /**
     * @return the check box about spliting mirrored.
     */
    @FxThread
    private @NotNull CheckBox getSplitMirroredCheckBox() {
        return notNull(splitMirroredCheckBox);
    }

    /**
     * @return the list of types.
     */
    @FxThread
    private @NotNull ComboBox<AlgorithmType> getAlgorithmTypeComboBox() {
        return notNull(algorithmTypeComboBox);
    }

    @Override
    @FxThread
    protected void processOk() {

        final NodeTree<?> nodeTree = getNodeTree();

        final TreeNode<?> node = getNode();
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

    @Override
    @FromAnyThread
    protected @NotNull String getButtonOkText() {
        return Messages.SIMPLE_DIALOG_BUTTON_GENERATE;
    }

    @Override
    @FromAnyThread
    protected @NotNull Point getSize() {
        return DIALOG_SIZE;
    }
}
