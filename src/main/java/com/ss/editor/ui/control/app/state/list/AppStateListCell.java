package com.ss.editor.ui.control.app.state.list;

import com.ss.editor.extension.scene.app.state.EditableSceneAppState;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.control.app.state.operation.DisableAppStateOperation;
import com.ss.editor.ui.control.app.state.operation.EnableAppStateOperation;
import com.ss.editor.ui.control.list.AbstractListCell;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.rlib.ui.util.FXUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of list cell to present an editable scene app state.
 *
 * @author JavaSaBr
 */
public class AppStateListCell extends AbstractListCell<EditableSceneAppState> {

    /**
     * The list of app states.
     */
    @NotNull
    private final AppStateList stateList;

    /**
     * Instantiates a new App state list cell.
     *
     * @param stateList the state list
     */
    public AppStateListCell(@NotNull final AppStateList stateList) {
        this.stateList = stateList;
        FXUtils.addClassTo(this, CSSClasses.SCENE_APP_STATE_LIST_CELL);
    }

    /**
     * @return the list of app states.
     */
    @NotNull
    private AppStateList getStateList() {
        return stateList;
    }

    @Override
    protected void processHideImpl() {

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
    protected boolean isEnabled(@Nullable final EditableSceneAppState item) {
        return item != null && item.isEnabled();
    }

    @NotNull
    @Override
    protected String getName(@Nullable final EditableSceneAppState item) {
        return item == null ? "" : item.getName();
    }
}
