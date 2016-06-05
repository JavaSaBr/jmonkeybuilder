package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.Messages;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.component.creator.FileCreatorDescription;
import com.ss.editor.ui.component.creator.FileCreatorRegistry;

import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import rlib.util.array.Array;

/**
 * Реализация действия по созданию файла.
 *
 * @author Ronn
 */
public class NewFileAction extends Menu {

    private static final FileCreatorRegistry CREATOR_REGISTRY = FileCreatorRegistry.getInstance();

    /**
     * Элемент действия.
     */
    private final ResourceElement element;

    public NewFileAction(final ResourceElement element) {
        this.element = element;
        setText(Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_NEW_FILE);

        final ObservableList<MenuItem> items = getItems();

        final Array<FileCreatorDescription> descriptions = CREATOR_REGISTRY.getDescriptions();
        descriptions.forEach(description -> items.add(new NewFileByCreatorAction(element, description)));
    }
}
