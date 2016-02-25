package com.ss.editor.ui.component.bar;

import com.ss.editor.Messages;
import com.ss.editor.ui.component.ScreenComponent;
import com.ss.editor.ui.component.bar.action.OpenAssetAction;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.util.UIUtils;

import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import rlib.ui.util.FXUtils;
import rlib.util.array.Array;

import static com.ss.editor.ui.css.CSSClasses.MAIN_FONT_13;

/**
 * Реализация меню бара для редактора.
 *
 * @author Ronn
 */
public class EditorBarComponent extends MenuBar implements ScreenComponent {

    public static final String COMPONENT_ID = "EditorBarComponent";

    public EditorBarComponent() {
        super();
        setId(CSSIds.EDITOR_BAR_COMPONENT);
        createItems();
    }

    private void createItems() {

        final ObservableList<Menu> menus = getMenus();
        menus.add(createAssetMenu());

        final Array<MenuItem> allItems = UIUtils.getAllItems(this);
        allItems.forEach(menuItem -> FXUtils.addClassTo(menuItem, MAIN_FONT_13));
    }

    private Menu createAssetMenu() {

        final Menu result = new Menu(Messages.EDITOR_BAR_ASSET);
        final ObservableList<MenuItem> items = result.getItems();

        final MenuItem openAssetItem = new OpenAssetAction();

        items.add(openAssetItem);

        return result;
    }

    @Override
    public String getComponentId() {
        return COMPONENT_ID;
    }
}
