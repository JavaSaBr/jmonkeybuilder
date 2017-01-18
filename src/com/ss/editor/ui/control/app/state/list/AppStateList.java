package com.ss.editor.ui.control.app.state.list;

import com.ss.editor.ui.Icons;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.extension.state.EditableSceneAppState;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;

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
     * The list view with created scene app states.
     */
    private ListView<EditableSceneAppState> listView;

    public AppStateList(@NotNull final Consumer<EditableSceneAppState> selectHandler) {
        setId(CSSIds.SCENE_APP_STATE_CONTAINER);
        this.selectHandler = selectHandler;
        createComponents();
    }

    /**
     * Create components of this component.
     */
    private void createComponents() {

        listView = new ListView<>();
        listView.setCellFactory(param -> new AppStateListCell(this));
        listView.setEditable(true);
        listView.setFocusTraversable(true);
        listView.prefHeightProperty().bind(heightProperty());
        listView.prefWidthProperty().bind(widthProperty());

        final MultipleSelectionModel<EditableSceneAppState> selectionModel = listView.getSelectionModel();
        selectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) ->
                selectHandler.accept(newValue));

        final Button addButton = new Button();
        addButton.setGraphic(new ImageView(Icons.ADD_18));

        final Button removeButton = new Button();
        removeButton.setGraphic(new ImageView(Icons.REMOVE_18));
        removeButton.disableProperty().bind(selectionModel.selectedItemProperty().isNull());

        final HBox buttonContainer = new HBox(addButton, removeButton);
        buttonContainer.setAlignment(Pos.CENTER);

        FXUtils.addToPane(listView, this);
        FXUtils.addToPane(buttonContainer, this);
        FXUtils.addClassTo(listView, CSSClasses.TRANSPARENT_LIST_VIEW);
    }
}
