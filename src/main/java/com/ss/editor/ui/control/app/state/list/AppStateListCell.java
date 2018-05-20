package com.ss.editor.ui.control.app.state.list;

import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.scene.app.state.EditableSceneAppState;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.model.undo.impl.DisableAppStateOperation;
import com.ss.editor.model.undo.impl.EnableAppStateOperation;
import com.ss.editor.ui.control.list.AbstractListCell;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.fx.util.FXUtils;
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

    public AppStateListCell(@NotNull final AppStateList stateList) {
        this.stateList = stateList;
        FXUtils.addClassTo(this, CssClasses.SCENE_APP_STATE_LIST_CELL);
    }

    /**
     * Get the list of app states.
     *
     * @return the list of app states.
     */
    @FxThread
    private @NotNull AppStateList getStateList() {
        return stateList;
    }

    @Override
    @FxThread
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
    @FxThread
    protected boolean isEnabled(@Nullable final EditableSceneAppState item) {
        return item != null && item.isEnabled();
    }

    @Override
    @FxThread
    protected @NotNull String getName(@Nullable final EditableSceneAppState item) {
        return item == null ? "" : item.getName();
    }
}
