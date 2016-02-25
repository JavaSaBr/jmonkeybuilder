package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;

import javafx.scene.control.MenuItem;

import static com.ss.editor.Messages.ASSET_COMPONENT_RESORCE_TREE_CONTEXT_MENU_OPEN_FILE;

/**
 * Реализация действия по открытию файла.
 *
 * @author Ronn
 */
public class OpenFileAction extends MenuItem {

    private final ResourceElement element;

    public OpenFileAction(final ResourceElement element) {
        this.element = element;
        setText(ASSET_COMPONENT_RESORCE_TREE_CONTEXT_MENU_OPEN_FILE);
        setOnAction(event -> processOpen());
    }

    private void processOpen() {

    }
}
