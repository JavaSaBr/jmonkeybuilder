package com.ss.editor.ui.component.bar;

import com.ss.editor.Messages;
import com.ss.editor.ui.component.ScreenComponent;
import com.ss.editor.ui.component.bar.action.CloseEditorAction;
import com.ss.editor.ui.component.bar.action.OpenAssetAction;
import com.ss.editor.ui.component.bar.action.OpenGraphicSettingsAction;
import com.ss.editor.ui.component.bar.action.OpenOtherSettingsAction;
import com.ss.editor.ui.component.bar.action.ReopenAssetMenu;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.util.UIUtils;

import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import rlib.ui.util.FXUtils;
import rlib.util.array.Array;

/**
 * Реализация меню бара для редактора.
 *
 * @author Ronn
 */
public class EditorBarComponent extends MenuBar implements ScreenComponent {

    public static final String COMPONENT_ID = "EditorBarComponent";

    /**
     * Меню с переоткрытием ассетов.
     */
    private ReopenAssetMenu reopenAssetMenu;

    public EditorBarComponent() {
        super();
        setId(CSSIds.EDITOR_BAR_COMPONENT);
        createItems();
    }

    private void createItems() {

        final ObservableList<Menu> menus = getMenus();
        menus.add(createAssetMenu());
        menus.add(createSettingsMenu());

        final Array<MenuItem> allItems = UIUtils.getAllItems(this);
        allItems.forEach(menuItem -> FXUtils.addClassTo(menuItem, CSSClasses.MAIN_FONT_13));
        allItems.forEach(menuItem -> FXUtils.addClassTo(menuItem, CSSClasses.TRANSPARENT_MENU_ITEM));
    }

    private Menu createAssetMenu() {

        final Menu result = new Menu(Messages.EDITOR_BAR_ASSET);
        final ObservableList<MenuItem> items = result.getItems();

        final MenuItem openAssetItem = new OpenAssetAction();
        final MenuItem reopenAssetItem = new ReopenAssetMenu();
        final MenuItem closeEditorAction = new CloseEditorAction();

        items.add(openAssetItem);
        items.add(reopenAssetItem);
        items.add(closeEditorAction);

        return result;
    }

    private Menu createSettingsMenu() {

        final Menu result = new Menu(Messages.EDITOR_BAR_SETTINGS);
        final ObservableList<MenuItem> items = result.getItems();

        final MenuItem graphicSettings = new OpenGraphicSettingsAction();
        final MenuItem otherSettings = new OpenOtherSettingsAction();

        items.add(graphicSettings);
        items.add(otherSettings);

        return result;
    }

    /**
     * @return меню с переоткрытием ассетов.
     */
    public ReopenAssetMenu getReopenAssetMenu() {
        return reopenAssetMenu;
    }

    @Override
    public String getComponentId() {
        return COMPONENT_ID;
    }
}
