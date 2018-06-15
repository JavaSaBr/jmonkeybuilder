package com.ss.editor.ui.dialog.plugin;

import com.ss.editor.plugin.EditorPlugin;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.util.DynamicIconSupport;
import com.ss.rlib.common.util.StringUtils;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of {@link ListCell} to present a plugin.
 *
 * @author JavaSaBr
 */
public class PluginListCell extends ListCell<EditorPlugin> {

    /**
     * The icon.
     */
    @NotNull
    private final ImageView icon;

    public PluginListCell() {
        this.icon = new ImageView(Icons.PLUGIN_16);
        FxUtils.addClass(this, CssClasses.PLUGIN_LIST_CELL);
        DynamicIconSupport.updateListener(this, icon, selectedProperty());
    }

    @Override
    protected void updateItem(@Nullable EditorPlugin item, boolean empty) {
        super.updateItem(item, empty);

        if(item == null) {
            setGraphic(null);
            setText(StringUtils.EMPTY);
            return;
        }

        setText(item.getName());
        setGraphic(icon);
    }
}
