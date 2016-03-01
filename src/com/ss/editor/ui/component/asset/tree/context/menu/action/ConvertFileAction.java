package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.Messages;
import com.ss.editor.file.converter.FileConverterDescription;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;

import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import rlib.util.array.Array;

/**
 * Реализация действия по конвертированию файла.
 *
 * @author Ronn
 */
public class ConvertFileAction extends Menu {

    /**
     * Элемент действия.
     */
    private final ResourceElement element;

    public ConvertFileAction(final ResourceElement element, final Array<FileConverterDescription> descriptions) {
        this.element = element;
        setText(Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_CONVERT_FILE);

        final ObservableList<MenuItem> items = getItems();
        descriptions.forEach(description -> items.add(new ConvertFileByConverterAction(element, description)));
    }
}
