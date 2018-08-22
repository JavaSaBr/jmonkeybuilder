package com.ss.builder.fx.control.app.state.list;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.impl.DisableAppStateOperation;
import com.ss.builder.model.undo.impl.EnableAppStateOperation;
import com.ss.builder.fx.control.list.AbstractListCell;
import com.ss.builder.fx.css.CssClasses;
import com.ss.builder.annotation.FxThread;
import com.ss.editor.extension.scene.app.state.EditableSceneAppState;
import com.ss.builder.model.undo.impl.DisableAppStateOperation;
import com.ss.builder.model.undo.impl.EnableAppStateOperation;
import com.ss.builder.fx.control.list.AbstractListCell;
import com.ss.builder.fx.css.CssClasses;
import com.ss.rlib.fx.util.FxUtils;
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

    public AppStateListCell(@NotNull AppStateList stateList) {
        super(stateList.getChangeConsumer(), stateList::getContextMenu);
        this.stateList = stateList;
        FxUtils.addClass(this, CssClasses.SCENE_APP_STATE_LIST_CELL);
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

        var item = getItem();
        var stateList = getStateList();
        var changeConsumer = stateList.getChangeConsumer();

        if (item.isEnabled()) {
            changeConsumer.execute(new DisableAppStateOperation(item));
        } else {
            changeConsumer.execute(new EnableAppStateOperation(item));
        }
    }

    @Override
    @FxThread
    protected boolean isEnabled(@Nullable EditableSceneAppState item) {
        return item != null && item.isEnabled();
    }

    @Override
    @FxThread
    protected @NotNull String getName(@Nullable EditableSceneAppState item) {
        return item == null ? "" : item.getName();
    }
}
