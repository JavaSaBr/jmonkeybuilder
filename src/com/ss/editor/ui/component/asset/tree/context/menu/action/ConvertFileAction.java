package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.Messages;
import com.ss.editor.file.converter.FileConverterDescription;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;

import org.jetbrains.annotations.NotNull;

import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import rlib.util.array.Array;

/**
 * The action to transformation a file.
 *
 * @author JavaSaBr
 */
public class ConvertFileAction extends Menu {

    /**
     * The action element.
     */
    @NotNull
    private final ResourceElement element;

    public ConvertFileAction(@NotNull final ResourceElement element, @NotNull final Array<FileConverterDescription> descriptions) {
        this.element = element;
        setText(Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_CONVERT_FILE);
        setGraphic(new ImageView(Icons.TRANSFORMATION_16));

        final ObservableList<MenuItem> items = getItems();
        descriptions.forEach(description -> items.add(new ConvertFileByConverterAction(element, description)));
    }
}
