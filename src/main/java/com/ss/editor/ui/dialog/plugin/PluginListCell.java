package com.ss.editor.ui.dialog.plugin;

import com.ss.editor.plugin.EditorPlugin;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.rlib.plugin.Version;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.StringUtils;
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
        FXUtils.addClassTo(this, CSSClasses.PLUGIN_LIST_CELL);
    }

    @Override
    protected void updateItem(@Nullable final EditorPlugin item, final boolean empty) {
        super.updateItem(item, empty);

        if(item == null) {
            setGraphic(null);
            setText(StringUtils.EMPTY);
            return;
        }


        final String name = item.getName();
        final Version version = item.getVersion();

        setText(name + " [" + version + "]");
        setGraphic(icon);
    }
}
