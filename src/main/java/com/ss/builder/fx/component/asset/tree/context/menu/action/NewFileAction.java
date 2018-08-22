package com.ss.builder.fx.component.asset.tree.context.menu.action;

import com.ss.builder.Messages;
import com.ss.builder.fx.Icons;
import com.ss.builder.fx.component.asset.tree.resource.ResourceElement;
import com.ss.builder.fx.component.creator.FileCreatorRegistry;
import com.ss.builder.Messages;
import com.ss.builder.fx.Icons;
import com.ss.builder.fx.component.asset.tree.resource.ResourceElement;
import com.ss.builder.fx.component.creator.FileCreatorRegistry;
import javafx.scene.control.Menu;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;

/**
 * The action to create a new file.
 *
 * @author JavaSaBr
 */
public class NewFileAction extends Menu {

    public NewFileAction(@NotNull ResourceElement element) {
        setText(Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_NEW_FILE);
        setGraphic(new ImageView(Icons.NEW_FILE_16));

        var items = getItems();

        FileCreatorRegistry.getInstance()
                .getDescriptors()
                .forEach(description -> items.add(new NewFileByCreatorAction(element, description)));
    }
}
