package com.ss.editor.ui.control.filter.list;

import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.scene.SceneNode;
import com.ss.editor.extension.scene.filter.EditableSceneFilter;
import com.ss.editor.extension.scene.filter.SceneFilter;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.FXConstants;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.dialog.CreateSceneFilterDialog;
import com.ss.editor.model.undo.impl.RemoveSceneFilterOperation;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.util.DynamicIconSupport;
import com.ss.rlib.fx.util.FXUtils;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

/**
 * The component to show and to edit filters.
 *
 * @author JavaSaBr
 */
public class FilterList extends VBox {

    /**
     * The selection handler.
     */
    @NotNull
    private final Consumer<EditableSceneFilter> selectHandler;

    /**
     * The changes consumer.
     */
    @NotNull
    private final SceneChangeConsumer changeConsumer;

    /**
     * The list view with filters.
     */
    @Nullable
    private ListView<EditableSceneFilter> listView;

    public FilterList(@NotNull final Consumer<EditableSceneFilter> selectHandler,
                      @NotNull final SceneChangeConsumer changeConsumer) {
        this.changeConsumer = changeConsumer;
        this.selectHandler = selectHandler;
        createComponents();
        FXUtils.addClassTo(this, CssClasses.SCENE_FILTER_CONTAINER);
    }

    /**
     * Create components of this component.
     */
    @FxThread
    private void createComponents() {

        listView = new ListView<>();
        listView.setCellFactory(param -> new FilterListCell(this));
        listView.setEditable(false);
        listView.setFocusTraversable(true);
        listView.prefHeightProperty().bind(heightProperty());
        listView.prefWidthProperty().bind(widthProperty());
        listView.setFixedCellSize(FXConstants.LIST_CELL_HEIGHT);

        final MultipleSelectionModel<EditableSceneFilter> selectionModel = listView.getSelectionModel();
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

        FXUtils.addClassTo(buttonContainer, CssClasses.DEF_HBOX);
        FXUtils.addClassTo(addButton, CssClasses.BUTTON_WITHOUT_RIGHT_BORDER);
        FXUtils.addClassTo(removeButton, CssClasses.BUTTON_WITHOUT_LEFT_BORDER);
        FXUtils.addClassTo(listView, CssClasses.TRANSPARENT_LIST_VIEW);

        DynamicIconSupport.addSupport(addButton, removeButton);
    }

    /**
     * Fill a list of filters.
     *
     * @param sceneNode the scene node
     */
    @FxThread
    public void fill(@NotNull final SceneNode sceneNode) {

        final MultipleSelectionModel<EditableSceneFilter> selectionModel = listView.getSelectionModel();
        final EditableSceneFilter selected = selectionModel.getSelectedItem();

        final ObservableList<EditableSceneFilter> items = listView.getItems();
        items.clear();

        final List<SceneFilter> filters = sceneNode.getFilters();
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
    @FxThread
    public void clearSelection() {
        final MultipleSelectionModel<EditableSceneFilter> selectionModel = listView.getSelectionModel();
        selectionModel.select(null);
    }

    /**
     * Get the current selected item.
     *
     * @return the current selected item.
     */
    @FxThread
    public @Nullable EditableSceneFilter getSelected() {
        final MultipleSelectionModel<EditableSceneFilter> selectionModel = listView.getSelectionModel();
        return selectionModel.getSelectedItem();
    }

    /**
     * Add a new filter.
     */
    @FxThread
    private void addFilter() {
        final CreateSceneFilterDialog dialog = new CreateSceneFilterDialog(changeConsumer);
        dialog.show();
    }

    /**
     * Remove the selected filter.
     */
    @FxThread
    private void removeFilter() {

        final MultipleSelectionModel<EditableSceneFilter> selectionModel = listView.getSelectionModel();
        final EditableSceneFilter filter = selectionModel.getSelectedItem();
        final SceneNode sceneNode = changeConsumer.getCurrentModel();

        changeConsumer.execute(new RemoveSceneFilterOperation(filter, sceneNode));
    }

    /**
     * Get the change consumer.
     *
     * @return the changes consumer.
     */
    @FxThread
    public @NotNull SceneChangeConsumer getChangeConsumer() {
        return changeConsumer;
    }
}
