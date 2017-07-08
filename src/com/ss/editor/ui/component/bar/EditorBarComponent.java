package com.ss.editor.ui.component.bar;

import com.ss.editor.Messages;
import com.ss.editor.ui.component.ScreenComponent;
import com.ss.editor.ui.component.bar.action.OpenAssetAction;
import com.ss.editor.ui.component.bar.action.OpenSettingsAction;
import com.ss.editor.ui.component.bar.action.ReopenAssetMenu;
import com.ss.editor.ui.css.CSSIds;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import org.jetbrains.annotations.NotNull;

/**
 * The toolbar of the Editor.
 *
 * @author JavaSaBr
 */
public class EditorBarComponent extends MenuBar implements ScreenComponent {

    /**
     * The constant COMPONENT_ID.
     */
    public static final String COMPONENT_ID = "EditorBarComponent";

    /**
     * Instantiates a new Editor bar component.
     */
    public EditorBarComponent() {
        super();
        setId(CSSIds.EDITOR_BAR_COMPONENT);
        createComponents();
    }

    private void createComponents() {
        final ObservableList<Menu> menus = getMenus();
        menus.addAll(createAssetMenu(), createOtherMenu());
    }

    @NotNull
    private Menu createAssetMenu() {

        final MenuItem openAssetItem = new OpenAssetAction();
        final MenuItem reopenAssetItem = new ReopenAssetMenu();

        final Menu menu = new Menu(Messages.EDITOR_BAR_ASSET);
        menu.getItems().addAll(openAssetItem, reopenAssetItem);

        return menu;
    }

    @NotNull
    private Menu createOtherMenu() {

        final MenuItem settingsAction = new OpenSettingsAction();

        final Menu menu = new Menu(Messages.EDITOR_BAR_OTHER);
        menu.getItems().addAll(settingsAction);

        return menu;
    }

    @Override
    public String getComponentId() {
        return COMPONENT_ID;
    }
}
