package com.ss.editor.ui.control.app.state.list;

import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.app.state.operation.DisableAppStateOperation;
import com.ss.editor.ui.control.app.state.operation.EnableAppStateOperation;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.extension.scene.app.state.EditableSceneAppState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.StringUtils;

/**
 * The implementation of list cell to present an editable scene app state.
 *
 * @author JavaSaBr
 */
public class AppStateListCell extends TextFieldListCell<EditableSceneAppState> {

    private static final Insets VISIBLE_ICON_OFFSET = new Insets(0, 3, 0, 0);

    /**
     * The list of app states.
     */
    @NotNull
    private final AppStateList stateList;

    /**
     * The content box.
     */
    @NotNull
    private final HBox content;

    /**
     * The label of this cell.
     */
    @NotNull
    private final Label text;

    /**
     * The visible icon.
     */
    @NotNull
    private final ImageView visibleIcon;

    public AppStateListCell(final @NotNull AppStateList stateList) {
        this.stateList = stateList;
        this.content = new HBox();
        this.text = new Label();
        this.visibleIcon = new ImageView();
        this.visibleIcon.addEventFilter(MouseEvent.MOUSE_RELEASED, this::processHide);
        this.visibleIcon.setOnMouseReleased(this::processHide);
        this.visibleIcon.setPickOnBounds(true);

        FXUtils.addClassTo(this, CSSClasses.TRANSPARENT_LIST_CELL);
        FXUtils.addClassTo(this, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(text, CSSClasses.SPECIAL_FONT_13);

        FXUtils.addToPane(visibleIcon, content);
        FXUtils.addToPane(text, content);

        HBox.setMargin(visibleIcon, VISIBLE_ICON_OFFSET);

        setId(CSSIds.SCENE_APP_STATE_LIST_CELL);
        setEditable(false);
    }

    /**
     * @return the list of app states.
     */
    @NotNull
    private AppStateList getStateList() {
        return stateList;
    }

    /**
     * Update hide status.
     */
    private void processHide(@NotNull final MouseEvent event) {
        event.consume();

        if (event.getButton() != MouseButton.PRIMARY) {
            return;
        }

        final EditableSceneAppState item = getItem();
        final AppStateList stateList = getStateList();
        final SceneChangeConsumer changeConsumer = stateList.getChangeConsumer();

        if (item.isEnabled()) {
            changeConsumer.execute(new DisableAppStateOperation(item));
        } else {
            changeConsumer.execute(new EnableAppStateOperation(item));
        }
    }

    @Override
    public void updateItem(@Nullable final EditableSceneAppState item, final boolean empty) {
        super.updateItem(item, empty);

        if (item == null) {
            setText(StringUtils.EMPTY);
            setGraphic(null);
            return;
        }

        visibleIcon.setVisible(true);
        visibleIcon.setManaged(true);
        visibleIcon.setImage(!item.isEnabled() ? Icons.INVISIBLE_16 : Icons.VISIBLE_16);
        visibleIcon.setOpacity(!item.isEnabled() ? 0.5D : 1D);

        text.setText(item.getName());

        setText(StringUtils.EMPTY);
        setGraphic(content);
    }
}
