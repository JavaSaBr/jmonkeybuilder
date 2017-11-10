package com.ss.editor.ui.control.model.tree.dialog.geometry.lod;

import static com.ss.editor.ui.FXConstants.DIALOG_LIST_WIDTH_PERCENT;
import static com.ss.rlib.util.ObjectUtils.notNull;
import static javafx.collections.FXCollections.observableArrayList;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.FXConstants;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.property.operation.PropertyOperation;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import com.ss.editor.ui.util.DynamicIconSupport;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.ui.util.FXUtils;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import jme3tools.optimize.LodGenerator;
import jme3tools.optimize.LodGenerator.TriangleReductionMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * The implementation of a dialog for generating lod levels.
 *
 * @author JavaSaBr
 */
public class GenerateLodLevelsDialog extends AbstractSimpleEditorDialog {

    @NotNull
    private static final ObservableList<ReductionMethod> METHOD_TYPES = observableArrayList(ReductionMethod.VALUES);

    @NotNull
    private static final Point DIALOG_SIZE = new Point(360, -1);


    @NotNull
    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * The enum Reduction method.
     */
    public enum ReductionMethod {
        /**
         * Proportional reduction method.
         */
        PROPORTIONAL,
        /**
         * Constant reduction method.
         */
        CONSTANT;

        /**
         * The constant VALUES.
         */
        public static final ReductionMethod[] VALUES = values();
    }

    /**
     * The node tree component.
     */
    @NotNull
    private final NodeTree<?> nodeTree;

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
    @Nullable
    private ComboBox<ReductionMethod> reductionMethodComboBox;

    /**
     * The list view with levels of LoD.
     */
    @Nullable
    private ListView<Number> levelsList;

    public GenerateLodLevelsDialog(@NotNull final NodeTree<?> nodeTree, final @NotNull Geometry geometry) {
        this.nodeTree = nodeTree;
        this.geometry = geometry;
        this.mesh = geometry.getMesh();
        updateButtonOk();
    }

    /**
     * @return the node tree component.
     */
    @FXThread
    private @NotNull NodeTree<?> getNodeTree() {
        return nodeTree;
    }

    /**
     * Gets mesh.
     *
     * @return the mesh.
     */
    @FXThread
    public @NotNull Mesh getMesh() {
        return mesh;
    }

    /**
     * @return the geometry.
     */
    @FXThread
    private @NotNull Geometry getGeometry() {
        return geometry;
    }

    /**
     * Gets method.
     *
     * @return the reduction method.
     */
    @FXThread
    public @NotNull ReductionMethod getMethod() {
        final ComboBox<ReductionMethod> comboBox = getReductionMethodComboBox();
        final SingleSelectionModel<ReductionMethod> selectionModel = comboBox.getSelectionModel();
        return selectionModel.getSelectedItem();
    }

    /**
     * @return the list view with levels of LoD.
     */
    @FXThread
    private @NotNull ListView<Number> getLevelsList() {
        return notNull(levelsList);
    }

    @Override
    @FromAnyThread
    protected @NotNull String getTitleText() {
        return Messages.GENERATE_LOD_DIALOG_TITLE;
    }

    @Override
    @FXThread
    protected void createContent(@NotNull final VBox root) {
        super.createContent(root);

        final HBox reductionMethodContainer = new HBox();

        final Label reductionMethodLabel = new Label(Messages.GENERATE_LOD_DIALOG_METHOD + ":");
        reductionMethodLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        reductionMethodComboBox = new ComboBox<>(GenerateLodLevelsDialog.METHOD_TYPES);
        reductionMethodComboBox.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        final SingleSelectionModel<ReductionMethod> selectionModel = reductionMethodComboBox.getSelectionModel();
        selectionModel.select(ReductionMethod.PROPORTIONAL);
        selectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> clearLevels());

        FXUtils.addToPane(reductionMethodLabel, reductionMethodContainer);
        FXUtils.addToPane(reductionMethodComboBox, reductionMethodContainer);
        FXUtils.addToPane(reductionMethodContainer, root);

        levelsList = new ListView<>();
        levelsList.setCellFactory(param -> new LodValueCell(this));
        levelsList.setEditable(true);
        levelsList.prefWidthProperty().bind(widthProperty().multiply(DIALOG_LIST_WIDTH_PERCENT));
        levelsList.maxWidthProperty().bind(widthProperty().multiply(DIALOG_LIST_WIDTH_PERCENT));
        levelsList.getItems().addListener((ListChangeListener<? super Number>) c -> updateButtonOk());
        levelsList.setFixedCellSize(FXConstants.LIST_CELL_HEIGHT);

        FXUtils.addToPane(levelsList, root);

        final HBox buttonContainer = new HBox();
        buttonContainer.prefWidthProperty().bind(widthProperty().multiply(DIALOG_LIST_WIDTH_PERCENT));
        buttonContainer.maxWidthProperty().bind(widthProperty().multiply(DIALOG_LIST_WIDTH_PERCENT));

        final Button addButton = new Button();
        addButton.setGraphic(new ImageView(Icons.ADD_12));
        addButton.setOnAction(event -> processAdd());

        final Button removeButton = new Button();
        removeButton.setGraphic(new ImageView(Icons.REMOVE_12));
        removeButton.setOnAction(event -> processRemove());
        removeButton.disableProperty().bind(levelsList.getSelectionModel().selectedItemProperty().isNull());

        FXUtils.addToPane(addButton, buttonContainer);
        FXUtils.addToPane(removeButton, buttonContainer);
        FXUtils.addToPane(buttonContainer, root);

        FXUtils.addClassTo(buttonContainer, CSSClasses.DEF_HBOX);
        FXUtils.addClassTo(reductionMethodLabel, CSSClasses.DIALOG_DYNAMIC_LABEL);
        FXUtils.addClassTo(reductionMethodComboBox, CSSClasses.DIALOG_FIELD);
        FXUtils.addClassTo(addButton, CSSClasses.BUTTON_WITHOUT_RIGHT_BORDER);
        FXUtils.addClassTo(removeButton, CSSClasses.BUTTON_WITHOUT_LEFT_BORDER);
        FXUtils.addClassTo(root, CSSClasses.GENERATE_LOD_DIALOG);

        DynamicIconSupport.addSupport(addButton, removeButton);
    }

    /**
     * Update disabling of OK button.
     */
    @FXThread
    private void updateButtonOk() {

        final ListView<Number> levelsList = getLevelsList();
        final ObservableList<Number> items = levelsList.getItems();

        final Button okButton = getOkButton();
        okButton.setDisable(items.isEmpty());
    }

    /**
     * Clear added levels.
     */
    @FXThread
    private void clearLevels() {
        final ListView<Number> levelsList = getLevelsList();
        final ObservableList<Number> items = levelsList.getItems();
        items.clear();
    }

    /**
     * Remove a selected level.
     */
    @FXThread
    private void processRemove() {

        final ListView<Number> levelsList = getLevelsList();
        final MultipleSelectionModel<Number> selectionModel = levelsList.getSelectionModel();

        final ObservableList<Number> items = levelsList.getItems();
        items.remove(selectionModel.getSelectedIndex());
    }

    /**
     * Add a new level.
     */
    @FXThread
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
    @FXThread
    protected void processKey(@NotNull final KeyEvent event) {
    }

    /**
     * @return the list of reduction methods.
     */
    @FXThread
    private @NotNull ComboBox<ReductionMethod> getReductionMethodComboBox() {
        return notNull(reductionMethodComboBox);
    }

    @Override
    @FXThread
    protected void processOk() {
        EditorUtil.incrementLoading();
        EXECUTOR_MANAGER.addBackgroundTask(this::processGenerate);
        super.processOk();
    }

    /**
     * Process of generating.
     */
    @FXThread
    private void processGenerate() {

        final Geometry geometry = getGeometry();
        final Mesh mesh = getMesh();
        final int currentLevels = mesh.getNumLodLevels();

        final VertexBuffer[] prevLodLevels = new VertexBuffer[currentLevels];

        for (int i = 0; i < currentLevels; i++) {
            prevLodLevels[i] = mesh.getLodLevel(i);
        }

        final ReductionMethod method = getMethod();
        final TriangleReductionMethod resultMethod =
                method == ReductionMethod.CONSTANT ? TriangleReductionMethod.CONSTANT :
                        TriangleReductionMethod.PROPORTIONAL;

        final ListView<Number> levelsList = getLevelsList();
        final ObservableList<Number> items = levelsList.getItems();
        final float[] values = new float[items.size()];

        for (int i = 0; i < items.size(); i++) {
            values[i] = items.get(i).floatValue();
        }

        final LodGenerator generator = new LodGenerator(geometry);
        final VertexBuffer[] newLodLevels = generator.computeLods(resultMethod, values);

        EXECUTOR_MANAGER.addFXTask(() -> {

            final NodeTree<?> nodeTree = getNodeTree();
            final ChangeConsumer consumer = notNull(nodeTree.getChangeConsumer());

            final PropertyOperation<ChangeConsumer, Geometry, VertexBuffer[]> operation =
                    new PropertyOperation<>(geometry, Messages.MODEL_PROPERTY_LOD, newLodLevels, prevLodLevels);

            operation.setApplyHandler((geom, buffers) -> geom.getMesh().setLodLevels(buffers));

            consumer.execute(operation);

            EditorUtil.decrementLoading();
        });
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
