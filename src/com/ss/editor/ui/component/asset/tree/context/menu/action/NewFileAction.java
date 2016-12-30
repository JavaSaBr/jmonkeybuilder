package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.component.creator.FileCreatorDescription;
import com.ss.editor.ui.component.creator.FileCreatorRegistry;

import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import rlib.util.array.Array;

/**
 * The action to create a new file.
 *
 * @author JavaSaBr
 */
public class NewFileAction extends Menu {

    private static final FileCreatorRegistry CREATOR_REGISTRY = FileCreatorRegistry.getInstance();

    /**
     * The action element.
     */
    private final ResourceElement element;

    public NewFileAction(final ResourceElement element) {
        this.element = element;
        setText(Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_NEW_FILE);
        setGraphic(new ImageView(Icons.NEW_FILE_16));

        final ObservableList<MenuItem> items = getItems();

        final Array<FileCreatorDescription> descriptions = CREATOR_REGISTRY.getDescriptions();
        descriptions.forEach(description -> items.add(new NewFileByCreatorAction(element, description)));
    }
}
