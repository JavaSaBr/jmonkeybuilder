package com.ss.editor.ui.dialog.model;

import com.jme3.scene.Geometry;
import com.ss.editor.Messages;
import com.ss.editor.model.tool.TangentGenerator;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.dialog.EditorDialog;

import java.awt.*;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * Реализация диалога генерации тангентов.
 *
 * @author Ronn
 */
public class GenerateTangentsDialog extends EditorDialog {

    private static final Insets OK_BUTTON_OFFSET = new Insets(0, 4, 0, 0);
    private static final Insets CANCEL_BUTTON_OFFSET = new Insets(0, 15, 0, 0);

    private static final Point DIALOG_SIZE = new Point(600, 160);

    private static final Insets ALGORITHM_OFFSET = new Insets(15, 0, 0, 0);
    private static final Insets SPLIT_OFFSET = new Insets(8, 0, 10, 150);

    public enum AlgorithmType {
        STANDARD,
        MIKKTSPACE;

        public static final AlgorithmType[] VALUES = values();
    }

    /**
     * Компонент структуры модели.
     */
    private final ModelNodeTree nodeTree;

    /**
     * Узел модели.
     */
    private final ModelNode<?> node;

    /**
     * Комбобокс с выбором алгоритма.
     */
    private ComboBox<AlgorithmType> algorithmTypeComboBox;

    /**
     * Флаг необходимости разделять зеркальные UV.
     */
    private CheckBox splitMirroredCheckBox;

    /**
     * Кнопка генерации.
     */
    private Button okButton;

    public GenerateTangentsDialog(final ModelNodeTree nodeTree, final ModelNode<?> node) {
        this.nodeTree = nodeTree;
        this.node = node;
    }

    /**
     * @return компонент структуры модели.
     */
    protected ModelNodeTree getNodeTree() {
        return nodeTree;
    }

    /**
     * @return узел модели.
     */
    protected ModelNode<?> getNode() {
        return node;
    }

    @Override
    protected String getTitleText() {
        return Messages.GENERATE_TANGENTS_DIALOG_TITLE;
    }

    @Override
    protected void createContent(final VBox root) {
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

        FXUtils.addClassTo(algorithmTypeLabel, CSSClasses.MAIN_FONT_13);
        FXUtils.addClassTo(splitMirroredCheckBox, CSSClasses.MAIN_FONT_13);
        FXUtils.addClassTo(splitMirroredCheckBox, CSSClasses.MAIN_FONT_13);

        VBox.setMargin(algorithmTypeContainer, ALGORITHM_OFFSET);
        VBox.setMargin(splitMirroredCheckBox, SPLIT_OFFSET);
    }

    @Override
    protected void createActions(final VBox root) {
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

        HBox.setMargin(okButton, OK_BUTTON_OFFSET);
        HBox.setMargin(cancelButton, CANCEL_BUTTON_OFFSET);
    }

    /**
     * @return флаг необходимости разделять зеркальные UV.
     */
    private CheckBox getSplitMirroredCheckBox() {
        return splitMirroredCheckBox;
    }

    /**
     * @return комбобокс с выбором алгоритма.
     */
    private ComboBox<AlgorithmType> getAlgorithmTypeComboBox() {
        return algorithmTypeComboBox;
    }

    /**
     * Обработка генерации.
     */
    private void processOk() {

        final ModelNode<?> node = getNode();
        final Geometry element = (Geometry) node.getElement();

        final ComboBox<AlgorithmType> algorithmTypeComboBox = getAlgorithmTypeComboBox();
        final AlgorithmType algorithmType = algorithmTypeComboBox.getSelectionModel().getSelectedItem();

        if (algorithmType == AlgorithmType.STANDARD) {

            final CheckBox splitMirroredCheckBox = getSplitMirroredCheckBox();
            TangentGenerator.useStandardGenerator(element, splitMirroredCheckBox.isSelected());

        } else {
            TangentGenerator.useMikktspaceGenerator(element);
        }

        final ModelNodeTree nodeTree = getNodeTree();
        nodeTree.notifyChanged(node);

        hide();
    }

    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
