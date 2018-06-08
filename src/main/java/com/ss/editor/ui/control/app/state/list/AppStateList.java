package com.ss.editor.ui.control.app.state.list;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.scene.SceneNode;
import com.ss.editor.extension.scene.app.state.EditableSceneAppState;
import com.ss.editor.extension.scene.app.state.SceneAppState;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.model.undo.impl.RemoveAppStateOperation;
import com.ss.editor.ui.FxConstants;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.dialog.CreateSceneAppStateDialog;
import com.ss.editor.ui.util.DynamicIconSupport;
import com.ss.editor.util.JmbEditorEnvoriment;
import com.ss.rlib.fx.util.FxControlUtils;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    public AppStateList(
            @NotNull Consumer<EditableSceneAppState> selectHandler,
            @NotNull SceneChangeConsumer changeConsumer
    ) {
        this.changeConsumer = changeConsumer;
        this.selectHandler = selectHandler;
        createComponents();
        FxUtils.addClass(this, CssClasses.SCENE_APP_STATE_CONTAINER);
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
        listView.setFixedCellSize(FxConstants.LIST_CELL_HEIGHT);

        FxControlUtils.onSelectedItemChange(listView, selectHandler);

        var selectionModel = listView.getSelectionModel();

        var addButton = new Button();
        addButton.setGraphic(new ImageView(Icons.ADD_12));
        addButton.setOnAction(event -> addAppState());

        var removeButton = new Button();
        removeButton.setGraphic(new ImageView(Icons.REMOVE_12));
        removeButton.setOnAction(event -> removeAppState());
        removeButton.disableProperty().bind(selectionModel.selectedItemProperty().isNull());

        var buttonContainer = new HBox(addButton, removeButton);

        FxUtils.addClass(buttonContainer, CssClasses.DEF_HBOX)
                .addClass(addButton, CssClasses.BUTTON_WITHOUT_RIGHT_BORDER)
                .addClass(removeButton, CssClasses.BUTTON_WITHOUT_LEFT_BORDER)
                .addClass(listView, CssClasses.TRANSPARENT_LIST_VIEW);

        FxUtils.addChild(this, listView, buttonContainer);

        DynamicIconSupport.addSupport(addButton, removeButton);
    }

    /**
     * Fill a list of app states.
     *
     * @param sceneNode the scene node
     */
    @FxThread
    public void fill(@NotNull SceneNode sceneNode) {

        var listView = getListView();
        var selectionModel = listView.getSelectionModel();
        var selected = selectionModel.getSelectedItem();

        var items = listView.getItems();
        items.clear();

        var appStates = sceneNode.getAppStates();
        appStates.stream().filter(EditableSceneAppState.class::isInstance)
                .map(EditableSceneAppState.class::cast)
                .forEach(items::add);

        if (selected != null && appStates.contains(selected)) {
            selectionModel.select(selected);
        }
    }

    /**
     * Get the context menu for the element.
     *
     * @param requestedNode the requested node.
     * @return the context menu.
     */
    @FxThread
    public @Nullable ContextMenu getContextMenu(@Nullable SceneAppState appState) {

        if (!(appState instanceof EditableSceneAppState)) {
            return null;
        }

        var actions = ((EditableSceneAppState) appState)
                .getModifyingActions(JmbEditorEnvoriment.getInstance());

        if (actions.isEmpty()) {
            return null;
        }

        var contextMenu = new ContextMenu();
        var items = contextMenu.getItems();

        actions.


        return contextMenu;
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
        var selectionModel = getListView().getSelectionModel();
        selectionModel.select(null);
    }

    /**
     * Get the current selected item.
     *
     * @return the current selected item.
     */
    @FxThread
    public @Nullable EditableSceneAppState getSelected() {
        return getListView()
                .getSelectionModel()
                .getSelectedItem();
    }

    /**
     * Handle adding a new app state.
     */
    @FxThread
    private void addAppState() {
        var dialog = new CreateSceneAppStateDialog(changeConsumer);
        dialog.show();
    }

    /**
     * Handle removing an old app state.
     */
    @FxThread
    private void removeAppState() {

        var appState = getListView()
                .getSelectionModel()
                .getSelectedItem();

        var sceneNode = changeConsumer.getCurrentModel();

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
