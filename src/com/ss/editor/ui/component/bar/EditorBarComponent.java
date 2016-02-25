package com.ss.editor.ui.component.bar;

import com.ss.editor.Messages;
import com.ss.editor.ui.component.ScreenComponent;

import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

/**
 * Реализация меню бара для редактора.
 *
 * @author Ronn
 */
public class EditorBarComponent extends MenuBar implements ScreenComponent {

    public EditorBarComponent() {
        super();
        createItems();
    }

    private void createItems() {
        final ObservableList<Menu> menus = getMenus();
        menus.add(createAssetMenu());
    }

    private Menu createAssetMenu() {

        final Menu result = new Menu(Messages.EDITOR_BAR_ASSET);
        final ObservableList<MenuItem> items = result.getItems();

        final MenuItem openAssetItem = new MenuItem(Messages.EDITOR_BAR_ASSET_OPEN_ASSET);

        items.add(openAssetItem);

        return result;
    }
}
