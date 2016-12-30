package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.component.editor.EditorRegistry;

import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import rlib.util.array.Array;

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
    private final ResourceElement element;

    public OpenWithFileAction(final ResourceElement element) {
        this.element = element;
        setText(Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_OPEN_WITH_FILE);
        setGraphic(new ImageView(Icons.VIEW_16));

        final ObservableList<MenuItem> items = getItems();

        final Array<EditorDescription> descriptions = EDITOR_REGISTRY.getAvailableEditorsFor(element.getFile());
        descriptions.forEach(description -> items.add(new OpenFileByEditorAction(element, description)));
    }
}
