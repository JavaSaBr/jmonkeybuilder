package com.ss.editor.ui.control.filter.list;

import com.ss.editor.JFXApplication;
import com.ss.editor.extension.scene.SceneNode;
import com.ss.editor.extension.scene.filter.EditableSceneFilter;
import com.ss.editor.extension.scene.filter.SceneFilter;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.FXConstants;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.filter.dialog.CreateSceneFilterDialog;
import com.ss.editor.ui.control.filter.operation.RemoveSceneFilterOperation;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.scene.EditorFXScene;
import com.ss.editor.ui.util.DynamicIconSupport;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.array.Array;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * The component to show and to edit filters.
 *
 * @author JavaSaBr
 */
public class FilterList extends VBox {

    private static final JFXApplication JFX_APPLICATION = JFXApplication.getInstance();

    /**
     * The selection handler.
     */
    @NotNull
    private final Consumer<EditableSceneFilter<?>> selectHandler;

    /**
     * The changes consumer.
     */
    @NotNull
    private final SceneChangeConsumer changeConsumer;

    /**
     * The list view with filters.
     */
    private ListView<EditableSceneFilter<?>> listView;

    /**
     * Instantiates a new Filter list.
     *
     * @param selectHandler  the select handler
     * @param changeConsumer the change consumer
     */
    public FilterList(@NotNull final Consumer<EditableSceneFilter<?>> selectHandler,
                      @NotNull final SceneChangeConsumer changeConsumer) {
        this.changeConsumer = changeConsumer;
        this.selectHandler = selectHandler;
        createComponents();
        FXUtils.addClassTo(this, CSSClasses.SCENE_FILTER_CONTAINER);
    }

    /**
     * Create components of this component.
     */
    private void createComponents() {

        listView = new ListView<>();
        listView.setCellFactory(param -> new FilterListCell(this));
        listView.setEditable(false);
        listView.setFocusTraversable(true);
        listView.prefHeightProperty().bind(heightProperty());
        listView.prefWidthProperty().bind(widthProperty());
        listView.setFixedCellSize(FXConstants.LIST_CELL_HEIGHT);

        final MultipleSelectionModel<EditableSceneFilter<?>> selectionModel = listView.getSelectionModel();
        selectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) ->
                selectHandler.accept(newValue));

        final Button addButton = new Button();
        addButton.setGraphic(new ImageView(Icons.ADD_12));
        addButton.setOnAction(event -> addFilter());

        final Button removeButton = new Button();
        removeButton.setGraphic(new ImageView(Icons.REMOVE_12));
        removeButton.setOnAction(event -> removeFilter());
        removeButton.disableProperty().bind(selectionModel.selectedItemProperty().isNull());

        final HBox buttonContainer = new HBox(addButton, removeButton);

        FXUtils.addToPane(listView, this);
        FXUtils.addToPane(buttonContainer, this);

        FXUtils.addClassTo(buttonContainer, CSSClasses.DEF_HBOX);
        FXUtils.addClassTo(addButton, CSSClasses.BUTTON_WITHOUT_RIGHT_BORDER);
        FXUtils.addClassTo(removeButton, CSSClasses.BUTTON_WITHOUT_LEFT_BORDER);
        FXUtils.addClassTo(listView, CSSClasses.TRANSPARENT_LIST_VIEW);

        DynamicIconSupport.addSupport(addButton, removeButton);
    }

    /**
     * Fill a list of filters.
     *
     * @param sceneNode the scene node
     */
    public void fill(@NotNull final SceneNode sceneNode) {

        final MultipleSelectionModel<EditableSceneFilter<?>> selectionModel = listView.getSelectionModel();
        final EditableSceneFilter<?> selected = selectionModel.getSelectedItem();

        final ObservableList<EditableSceneFilter<?>> items = listView.getItems();
        items.clear();

        final Array<SceneFilter<?>> filters = sceneNode.getFilters();
        filters.stream().filter(EditableSceneFilter.class::isInstance)
                .map(EditableSceneFilter.class::cast)
                .forEach(items::add);

        if (selected != null && filters.contains(selected)) {
            selectionModel.select(selected);
        }
    }

    /**
     * Clear selection.
     */
    public void clearSelection() {
        final MultipleSelectionModel<EditableSceneFilter<?>> selectionModel = listView.getSelectionModel();
        selectionModel.select(null);
    }

    /**
     * Handle adding a new filter.
     */
    private void addFilter() {
        final EditorFXScene scene = JFX_APPLICATION.getScene();
        final CreateSceneFilterDialog dialog = new CreateSceneFilterDialog(changeConsumer);
        dialog.show(scene.getWindow());
    }

    /**
     * Handle removing an old filter.
     */
    private void removeFilter() {

        final MultipleSelectionModel<EditableSceneFilter<?>> selectionModel = listView.getSelectionModel();
        final EditableSceneFilter<?> filter = selectionModel.getSelectedItem();
        final SceneNode sceneNode = changeConsumer.getCurrentModel();

        changeConsumer.execute(new RemoveSceneFilterOperation(filter, sceneNode));
    }

    /**
     * Gets change consumer.
     *
     * @return the changes consumer.
     */
    @NotNull
    public SceneChangeConsumer getChangeConsumer() {
        return changeConsumer;
    }
}
