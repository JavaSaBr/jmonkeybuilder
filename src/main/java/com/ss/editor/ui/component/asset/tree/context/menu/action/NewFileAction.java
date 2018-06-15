package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.component.creator.FileCreatorRegistry;
import javafx.scene.control.Menu;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;

/**
 * The action to create a new file.
 *
 * @author JavaSaBr
 */
public class NewFileAction extends Menu {

    private static final FileCreatorRegistry CREATOR_REGISTRY = FileCreatorRegistry.getInstance();

    public NewFileAction(@NotNull ResourceElement element) {
        setText(Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_NEW_FILE);
        setGraphic(new ImageView(Icons.NEW_FILE_16));

        var items = getItems();

        CREATOR_REGISTRY.getDescriptions()
                .forEach(description -> items.add(new NewFileByCreatorAction(element, description)));
    }
}
