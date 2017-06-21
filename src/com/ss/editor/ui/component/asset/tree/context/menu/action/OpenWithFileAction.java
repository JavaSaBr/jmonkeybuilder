package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.Messages;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.component.editor.EditorRegistry;

import org.jetbrains.annotations.NotNull;

import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import com.ss.rlib.util.array.Array;

/**
 * The action to choose an editor to open a file.
 *
 * @author JavaSaBr
 */
public class OpenWithFileAction extends Menu {

    private static final EditorRegistry EDITOR_REGISTRY = EditorRegistry.getInstance();

    /**
     * The action element.
     */
    @NotNull
    private final ResourceElement element;

    /**
     * Instantiates a new Open with file action.
     *
     * @param element the element
     */
    public OpenWithFileAction(@NotNull final ResourceElement element) {
        this.element = element;
        setText(Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_OPEN_WITH_FILE);

        final ObservableList<MenuItem> items = getItems();

        final Array<EditorDescription> descriptions = EDITOR_REGISTRY.getAvailableEditorsFor(element.getFile());
        descriptions.forEach(description -> items.add(new OpenFileByEditorAction(element, description)));
    }
}
