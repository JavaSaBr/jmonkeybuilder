package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.Messages;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.component.editor.EditorRegistry;

import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import rlib.util.array.Array;

/**
 * Реализация действия по открытию файла с возможность выбрать редактор.
 *
 * @author Ronn
 */
public class OpenWithFileAction extends Menu {

    private static final EditorRegistry EDITOR_REGISTRY = EditorRegistry.getInstance();

    /**
     * Элемент действия.
     */
    private final ResourceElement element;

    public OpenWithFileAction(final ResourceElement element) {
        this.element = element;
        setText(Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_OPEN_WITH_FILE);

        final ObservableList<MenuItem> items = getItems();

        final Array<EditorDescription> descriptions = EDITOR_REGISTRY.getAvailableEditorsFor(element.getFile());
        descriptions.forEach(description -> items.add(new OpenFileByEditorAction(element, description)));
    }
}
