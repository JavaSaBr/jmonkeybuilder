package com.ss.editor.ui.control.app.state.list;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.scene.SceneNode;
import com.ss.editor.extension.scene.app.state.EditableSceneAppState;
import com.ss.editor.extension.scene.app.state.SceneAppState;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.FXConstants;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.dialog.CreateSceneAppStateDialog;
import com.ss.editor.model.undo.impl.RemoveAppStateOperation;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.util.DynamicIconSupport;
import com.ss.rlib.ui.util.FXUtils;
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
 * The component to show and to edit app states.
 *
 * @author JavaSaBr
 */
public class AppStateList extends VBox {

    /**
     * The selection handler.
     */
    @NotNull
    private final Consumer<EditableSceneAppState> selectHandler;

    /**
     * The changes consumer.
     */
    @NotNull
    private final SceneChangeConsumer changeConsumer;

    /**
     * The list view with created scene app states.
     */
    @Nullable
    private ListView<EditableSceneAppState> listView;

    public AppStateList(@NotNull final Consumer<EditableSceneAppState> selectHandler,
                        @NotNull final SceneChangeConsumer changeConsumer) {
        this.changeConsumer = changeConsumer;
        this.selectHandler = selectHandler;
        createComponents();
        FXUtils.addClassTo(this, CssClasses.SCENE_APP_STATE_CONTAINER);
    }

    /**
     * Create components of this component.
     */
    @FxThread
    private void createComponents() {

        listView = new ListView<>();
        listView.setCellFactory(param -> new AppStateListCell(this));
        listView.setEditable(false);
        listView.setFocusTraversable(true);
        listView.prefHeightProperty().bind(heightProperty());
        listView.prefWidthProperty().bind(widthProperty());
        listView.setFixedCellSize(FXConstants.LIST_CELL_HEIGHT);

        final MultipleSelectionModel<EditableSceneAppState> selectionModel = listView.getSelectionModel();
        selectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) ->
                selectHandler.accept(newValue));

        final Button addButton = new Button();
        addButton.setGraphic(new ImageView(Icons.ADD_12));
        addButton.setOnAction(event -> addAppState());

        final Button removeButton = new Button();
        removeButton.setGraphic(new ImageView(Icons.REMOVE_12));
        removeButton.setOnAction(event -> removeAppState());
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
     * Fill a list of app states.
     *
     * @param sceneNode the scene node
     */
    @FxThread
    public void fill(@NotNull final SceneNode sceneNode) {

        final ListView<EditableSceneAppState> listView = getListView();
        final MultipleSelectionModel<EditableSceneAppState> selectionModel = listView.getSelectionModel();
        final EditableSceneAppState selected = selectionModel.getSelectedItem();

        final ObservableList<EditableSceneAppState> items = listView.getItems();
        items.clear();

        final List<SceneAppState> appStates = sceneNode.getAppStates();
        appStates.stream().filter(EditableSceneAppState.class::isInstance)
                .map(EditableSceneAppState.class::cast)
                .forEach(items::add);

        if (selected != null && appStates.contains(selected)) {
            selectionModel.select(selected);
        }
    }

    /**
     * Get the list view with created scene app states.
     *
     * @return the list view with created scene app states.
     */
    @FxThread
    private @NotNull ListView<EditableSceneAppState> getListView() {
        return notNull(listView);
    }

    /**
     * Clear selection.
     */
    @FxThread
    public void clearSelection() {
        final MultipleSelectionModel<EditableSceneAppState> selectionModel = getListView().getSelectionModel();
        selectionModel.select(null);
    }

    /**
     * Handle adding a new app state.
     */
    @FxThread
    private void addAppState() {
        final CreateSceneAppStateDialog dialog = new CreateSceneAppStateDialog(changeConsumer);
        dialog.show();
    }

    /**
     * Handle removing an old app state.
     */
    @FxThread
    private void removeAppState() {

        final MultipleSelectionModel<EditableSceneAppState> selectionModel = getListView().getSelectionModel();
        final EditableSceneAppState appState = selectionModel.getSelectedItem();
        final SceneNode sceneNode = changeConsumer.getCurrentModel();

        changeConsumer.execute(new RemoveAppStateOperation(appState, sceneNode));
    }

    /**
     * Get the change consumer.
     *
     * @return the changes consumer.
     */
    @FromAnyThread
    public @NotNull SceneChangeConsumer getChangeConsumer() {
        return changeConsumer;
    }
}
