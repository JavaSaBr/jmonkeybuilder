package com.ss.editor.ui.control.filter.list;

import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.scene.filter.EditableSceneFilter;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.model.undo.impl.DisableSceneFilterOperation;
import com.ss.editor.model.undo.impl.EnableSceneFilterOperation;
import com.ss.editor.ui.control.list.AbstractListCell;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.fx.util.FXUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of list cell to present an editable scene filter.
 *
 * @author JavaSaBr
 */
public class FilterListCell extends AbstractListCell<EditableSceneFilter> {

    /**
     * The list of filters.
     */
    @NotNull
    private final FilterList stateList;

    /**
     * Instantiates a new Filter list cell.
     *
     * @param stateList the state list
     */
    public FilterListCell(@NotNull final FilterList stateList) {
        this.stateList = stateList;
        FXUtils.addClassTo(this, CssClasses.SCENE_FILTER_LIST_CELL);
    }

    /**
     * @return the list of app states.
     */
    @NotNull
    private FilterList getStateList() {
        return stateList;
    }

    @FxThread
    @Override
    protected void processHideImpl() {

        final EditableSceneFilter item = getItem();
        final FilterList stateList = getStateList();
        final SceneChangeConsumer changeConsumer = stateList.getChangeConsumer();

        if (item.isEnabled()) {
            changeConsumer.execute(new DisableSceneFilterOperation(item));
        } else {
            changeConsumer.execute(new EnableSceneFilterOperation(item));
        }
    }

    @FxThread
    @Override
    protected boolean isEnabled(@Nullable final EditableSceneFilter item) {
        return item != null && item.isEnabled();
    }

    @NotNull
    @Override
    protected String getName(@Nullable final EditableSceneFilter item) {
        return item == null ? "" : item.getName();
    }
}
