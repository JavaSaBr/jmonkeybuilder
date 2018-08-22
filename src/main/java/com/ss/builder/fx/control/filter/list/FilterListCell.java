package com.ss.builder.fx.control.filter.list;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.SceneChangeConsumer;
import com.ss.builder.model.undo.impl.DisableSceneFilterOperation;
import com.ss.builder.model.undo.impl.EnableSceneFilterOperation;
import com.ss.builder.fx.css.CssClasses;
import com.ss.builder.annotation.FxThread;
import com.ss.editor.extension.scene.filter.EditableSceneFilter;
import com.ss.builder.model.undo.editor.SceneChangeConsumer;
import com.ss.builder.model.undo.impl.DisableSceneFilterOperation;
import com.ss.builder.model.undo.impl.EnableSceneFilterOperation;
import com.ss.builder.fx.control.list.AbstractListCell;
import com.ss.builder.fx.css.CssClasses;
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
        super(stateList.getChangeConsumer(), filter -> null);
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
