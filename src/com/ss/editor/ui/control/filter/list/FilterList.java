package com.ss.editor.ui.control.filter.list;

import com.ss.editor.JFXApplication;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.filter.dialog.CreateSceneFilterDialog;
import com.ss.editor.ui.control.filter.operation.RemoveSceneFilterOperation;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.scene.EditorFXScene;
import com.ss.extension.scene.SceneNode;
import com.ss.extension.scene.filter.EditableSceneFilter;
import com.ss.extension.scene.filter.SceneFilter;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import rlib.ui.util.FXUtils;
import rlib.util.array.Array;

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

    public FilterList(@NotNull final Consumer<EditableSceneFilter<?>> selectHandler,
                      @NotNull final SceneChangeConsumer changeConsumer) {
        setId(CSSIds.SCENE_APP_STATE_CONTAINER);
        this.changeConsumer = changeConsumer;
        this.selectHandler = selectHandler;
        createComponents();
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

        final MultipleSelectionModel<EditableSceneFilter<?>> selectionModel = listView.getSelectionModel();
        selectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) ->
                selectHandler.accept(newValue));

        final Button addButton = new Button();
        addButton.setGraphic(new ImageView(Icons.ADD_18));
        addButton.setOnAction(event -> addFilter());

        final Button removeButton = new Button();
        removeButton.setGraphic(new ImageView(Icons.REMOVE_18));
        removeButton.setOnAction(event -> removeFilter());
        removeButton.disableProperty().bind(selectionModel.selectedItemProperty().isNull());

        final HBox buttonContainer = new HBox(addButton, removeButton);
        buttonContainer.setAlignment(Pos.CENTER);

        FXUtils.addToPane(listView, this);
        FXUtils.addToPane(buttonContainer, this);
        FXUtils.addClassTo(listView, CSSClasses.TRANSPARENT_LIST_VIEW);
    }

    /**
     * Fill a list of filters.
     */
    public void fill(@NotNull final SceneNode sceneNode) {

        final MultipleSelectionModel<EditableSceneFilter<?>> selectionModel = listView.getSelectionModel();
        final EditableSceneFilter<?> selected = selectionModel.getSelectedItem();

        final ObservableList<EditableSceneFilter<?>> items = listView.getItems();
        items.clear();

        final Array<SceneFilter<?>> filters = sceneNode.getFilters();
        filters.stream().filter(sceneFilter -> sceneFilter instanceof EditableSceneFilter<?>)
                .map(editableFilter -> (EditableSceneFilter<?>) editableFilter)
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
     * @return the changes consumer.
     */
    @NotNull
    public SceneChangeConsumer getChangeConsumer() {
        return changeConsumer;
    }
}
