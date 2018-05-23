package com.ss.editor.ui.component.bar;

import com.ss.editor.Messages;
import com.ss.editor.ui.component.ScreenComponent;
import com.ss.editor.ui.component.bar.action.*;
import com.ss.editor.ui.css.CssIds;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import org.jetbrains.annotations.NotNull;

/**
 * The menu bar of the Editor.
 *
 * @author JavaSaBr
 */
public class EditorMenuBarComponent extends MenuBar implements ScreenComponent {

    private static final String COMPONENT_ID = "EditorMenuBarComponent";

    public EditorMenuBarComponent() {
        super();
        setId(CssIds.EDITOR_MENU_BAR_COMPONENT);
        createComponents();
    }

    private void createComponents() {
        getMenus().addAll(createFileMenu(),
                createOtherMenu(),
                createHelpMenu());
    }

    private @NotNull Menu createFileMenu() {

        var menu = new Menu(Messages.EDITOR_MENU_FILE);
        menu.getItems().addAll(new OpenAssetAction(),
                new ReopenAssetMenu(),
                new ExitAction());

        return menu;
    }

    private @NotNull Menu createOtherMenu() {

        var menu = new Menu(Messages.EDITOR_MENU_OTHER);
        menu.getItems().addAll(new OpenSettingsAction(),
                new OpenPluginsAction(),
                new ClearAssetCacheAction(),
                new UpdateClasspathAndAssetCacheAction());

        return menu;
    }

    private @NotNull Menu createHelpMenu() {

        var menu = new Menu(Messages.EDITOR_MENU_HELP);
        menu.getItems().addAll(new AboutAction());

        return menu;
    }

    @Override
    public String getComponentId() {
        return COMPONENT_ID;
    }
}
