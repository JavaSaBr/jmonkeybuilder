package com.ss.editor.ui.component.bar;

import com.ss.editor.Messages;
import com.ss.editor.ui.component.ScreenComponent;
import com.ss.editor.ui.component.bar.action.*;
import com.ss.editor.ui.css.CSSIds;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import org.jetbrains.annotations.NotNull;

/**
 * The menu bar of the Editor.
 *
 * @author JavaSaBr
 */
public class EditorMenuBarComponent extends MenuBar implements ScreenComponent {

    /**
     * The constant COMPONENT_ID.
     */
    @NotNull
    private static final String COMPONENT_ID = "EditorMenuBarComponent";

    /**
     * Instantiates a new Editor bar component.
     */
    public EditorMenuBarComponent() {
        super();
        setId(CSSIds.EDITOR_MENU_BAR_COMPONENT);
        createComponents();
    }

    private void createComponents() {
        final ObservableList<Menu> menus = getMenus();
        menus.addAll(createFileMenu(), createOtherMenu(), createHelpMenu());
    }

    @NotNull
    private Menu createFileMenu() {

        final MenuItem openAssetItem = new OpenAssetAction();
        final MenuItem reopenAssetItem = new ReopenAssetMenu();
        final MenuItem exitItem = new ExitAction();

        final Menu menu = new Menu(Messages.EDITOR_MENU_FILE);
        menu.getItems().addAll(openAssetItem, reopenAssetItem, exitItem);

        return menu;
    }

    @NotNull
    private Menu createOtherMenu() {

        final MenuItem settingsAction = new OpenSettingsAction();

        final Menu menu = new Menu(Messages.EDITOR_MENU_OTHER);
        menu.getItems().addAll(settingsAction);

        return menu;
    }

    @NotNull
    private Menu createHelpMenu() {

        final MenuItem aboutAction = new AboutAction();

        final Menu menu = new Menu(Messages.EDITOR_MENU_HELP);
        menu.getItems().addAll(aboutAction);

        return menu;
    }

    @Override
    public String getComponentId() {
        return COMPONENT_ID;
    }
}
