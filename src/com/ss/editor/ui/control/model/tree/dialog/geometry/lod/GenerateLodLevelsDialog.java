package com.ss.editor.ui.control.model.tree.dialog.geometry.lod;

import static javafx.collections.FXCollections.observableArrayList;

import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.ss.editor.Messages;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.property.operation.ModelPropertyOperation;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.dialog.AbstractNodeDialog;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.util.EditorUtil;
import com.ss.editor.util.GeomUtils;

import org.jetbrains.annotations.NotNull;

import java.awt.Point;
import java.util.Objects;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import jme3tools.optimize.LodGenerator;
import jme3tools.optimize.LodGenerator.TriangleReductionMethod;
import rlib.ui.util.FXUtils;

/**
 * The implementation of a dialog for generating lod levels.
 *
 * @author JavaSaBr
 */
public class GenerateLodLevelsDialog extends AbstractNodeDialog {

    private static final ObservableList<ReductionMethod> METHOD_TYPES = observableArrayList(ReductionMethod.VALUES);

    private static final Point DIALOG_SIZE = new Point(360, 296);
    private static final Insets FIELD_OFFSET = new Insets(6, CANCEL_BUTTON_OFFSET.getRight(), 6, 0);

    private static final double LIST_WIDTH_PERCENT = 0.94;

    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    public enum ReductionMethod {
        PROPORTIONAL,
        CONSTANT;
        public static final ReductionMethod[] VALUES = values();
    }

    /**
     * The model tree component.
     */
    @NotNull
    private final ModelNodeTree nodeTree;

    /**
     * The geometry.
     */
    @NotNull
    private final Geometry geometry;

    /**
     * The mesh.
     */
    @NotNull
    private final Mesh mesh;

    /**
     * The list of reduction methods.
     */
    private ComboBox<ReductionMethod> reductionMethodComboBox;

    /**
     * The list view with levels of LoD.
     */
    private ListView<Number> levelsList;

    public GenerateLodLevelsDialog(@NotNull final ModelNodeTree nodeTree, final @NotNull Geometry geometry) {
        this.nodeTree = nodeTree;
        this.geometry = geometry;
        this.mesh = geometry.getMesh();
        updateButtonOk();
    }

    /**
     * @return the model tree component.
     */
    @NotNull
    private ModelNodeTree getNodeTree() {
        return nodeTree;
    }

    /**
     * @return the mesh.
     */
    @NotNull
    public Mesh getMesh() {
        return mesh;
    }

    /**
     * @return the geometry.
     */
    @NotNull
    private Geometry getGeometry() {
        return geometry;
    }

    /**
     * @return the reduction method.
     */
    @NotNull
    public ReductionMethod getMethod() {
        final ComboBox<ReductionMethod> comboBox = getReductionMethodComboBox();
        final SingleSelectionModel<ReductionMethod> selectionModel = comboBox.getSelectionModel();
        return selectionModel.getSelectedItem();
    }

    /**
     * @return the list view with levels of LoD.
     */
    @NotNull
    private ListView<Number> getLevelsList() {
        return Objects.requireNonNull(levelsList);
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.GENERATE_LOD_DIALOG_TITLE;
    }

    @Override
    protected void createContent(@NotNull final VBox root) {
        super.createContent(root);

        root.setAlignment(Pos.CENTER_LEFT);

        final HBox reductionMethodContainer = new HBox();

        final Label reductionMethodLabel = new Label(Messages.GENERATE_LOD_DIALOG_METHOD + ":");
        reductionMethodLabel.setId(CSSIds.EDITOR_DIALOG_SHORT_LABEL);

        reductionMethodComboBox = new ComboBox<>(GenerateLodLevelsDialog.METHOD_TYPES);
        reductionMethodComboBox.setId(CSSIds.SETTINGS_DIALOG_FIELD);
        reductionMethodComboBox.prefWidthProperty().bind(root.widthProperty());

        final SingleSelectionModel<ReductionMethod> selectionModel = reductionMethodComboBox.getSelectionModel();
        selectionModel.select(ReductionMethod.PROPORTIONAL);
        selectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> clearLevels());

        FXUtils.addToPane(reductionMethodLabel, reductionMethodContainer);
        FXUtils.addToPane(reductionMethodComboBox, reductionMethodContainer);
        FXUtils.addToPane(reductionMethodContainer, root);

        levelsList = new ListView<>();
        levelsList.setId(CSSIds.GENERATE_LOD_DIALOG_LIST_VIEW);
        levelsList.setCellFactory(param -> new LodValueCell(this));
        levelsList.setEditable(true);
        levelsList.prefWidthProperty().bind(widthProperty().multiply(LIST_WIDTH_PERCENT));
        levelsList.maxWidthProperty().bind(widthProperty().multiply(LIST_WIDTH_PERCENT));
        levelsList.getItems().addListener((ListChangeListener<? super Number>) c -> updateButtonOk());

        FXUtils.addToPane(levelsList, root);

        final HBox addRemoveButtonsContainer = new HBox();
        addRemoveButtonsContainer.prefWidthProperty().bind(widthProperty().multiply(LIST_WIDTH_PERCENT));
        addRemoveButtonsContainer.maxWidthProperty().bind(widthProperty().multiply(LIST_WIDTH_PERCENT));

        final Button addButton = new Button();
        addButton.setGraphic(new ImageView(Icons.ADD_18));
        addButton.setOnAction(event -> processAdd());

        final Button removeButton = new Button();
        removeButton.setGraphic(new ImageView(Icons.REMOVE_18));
        removeButton.setOnAction(event -> processRemove());
        removeButton.disableProperty().bind(levelsList.getSelectionModel()
                .selectedItemProperty().isNull());

        FXUtils.addToPane(addButton, addRemoveButtonsContainer);
        FXUtils.addToPane(removeButton, addRemoveButtonsContainer);
        FXUtils.addToPane(addRemoveButtonsContainer, root);

        FXUtils.addClassTo(levelsList, CSSClasses.TRANSPARENT_LIST_VIEW);
        FXUtils.addClassTo(reductionMethodLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(reductionMethodComboBox, CSSClasses.SPECIAL_FONT_14);

        VBox.setMargin(reductionMethodContainer, FIELD_OFFSET);
    }

    /**
     * Update disabling of OK button.
     */
    private void updateButtonOk() {

        final ListView<Number> levelsList = getLevelsList();
        final ObservableList<Number> items = levelsList.getItems();

        final Button okButton = getOkButton();
        okButton.setDisable(items.isEmpty());
    }

    /**
     * Clear added levels.
     */
    private void clearLevels() {
        final ListView<Number> levelsList = getLevelsList();
        final ObservableList<Number> items = levelsList.getItems();
        items.clear();
    }

    /**
     * Remove a selected level.
     */
    private void processRemove() {

        final ListView<Number> levelsList = getLevelsList();
        final MultipleSelectionModel<Number> selectionModel = levelsList.getSelectionModel();

        final ObservableList<Number> items = levelsList.getItems();
        items.remove(selectionModel.getSelectedIndex());
    }

    /**
     * Add a new level.
     */
    private void processAdd() {

        final ListView<Number> levelsList = getLevelsList();
        final ObservableList<Number> items = levelsList.getItems();

        if (getMethod() == ReductionMethod.PROPORTIONAL) {
            items.add(0.1F);
        } else {
            final Mesh mesh = getMesh();
            items.add(mesh.getTriangleCount());
        }
    }

    @Override
    protected void processKey(@NotNull final KeyEvent event) {
    }

    /**
     * @return the list of reduction methods.
     */
    @NotNull
    private ComboBox<ReductionMethod> getReductionMethodComboBox() {
        return Objects.requireNonNull(reductionMethodComboBox);
    }

    @Override
    protected void processOk() {
        EditorUtil.incrementLoading();
        EXECUTOR_MANAGER.addBackgroundTask(this::processGenerate);
        hide();
    }

    /**
     * Process of generating.
     */
    private void processGenerate() {

        final Geometry geometry = getGeometry();
        final Mesh mesh = getMesh();
        final int currentLevels = mesh.getNumLodLevels();

        final VertexBuffer[] prevLodLevels = new VertexBuffer[currentLevels];

        for (int i = 0; i < currentLevels; i++) {
            prevLodLevels[i] = mesh.getLodLevel(i);
        }

        final ReductionMethod method = getMethod();
        final TriangleReductionMethod resultMethod = method == ReductionMethod.CONSTANT ?
                TriangleReductionMethod.CONSTANT : TriangleReductionMethod.PROPORTIONAL;

        final ListView<Number> levelsList = getLevelsList();
        final ObservableList<Number> items = levelsList.getItems();
        final float[] values = new float[items.size()];

        for (int i = 0; i < items.size(); i++) {
            values[i] = items.get(i).floatValue();
        }

        final LodGenerator generator = new LodGenerator(geometry);
        final VertexBuffer[] newLodLevels = generator.computeLods(resultMethod, values);

        EXECUTOR_MANAGER.addFXTask(() -> {

            final ModelNodeTree nodeTree = getNodeTree();
            final ModelChangeConsumer consumer = nodeTree.getModelChangeConsumer();

            final int index = GeomUtils.getIndex(consumer.getCurrentModel(), geometry);

            final ModelPropertyOperation<Geometry, VertexBuffer[]> operation = new ModelPropertyOperation<>(index,
                    Messages.MODEL_PROPERTY_LOD, newLodLevels, prevLodLevels);
            operation.setApplyHandler((geom, buffers) -> geom.getMesh().setLodLevels(buffers));

            consumer.execute(operation);

            EditorUtil.decrementLoading();
        });
    }

    @Override
    protected String getButtonOkLabel() {
        return Messages.GENERATE_LOD_DIALOG_BUTTON_GENERATE;
    }

    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
