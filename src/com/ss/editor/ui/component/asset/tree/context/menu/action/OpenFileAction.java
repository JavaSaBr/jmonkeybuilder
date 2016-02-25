package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.RequestedOpenFileEvent;

import javafx.scene.control.MenuItem;

import static com.ss.editor.Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_OPEN_FILE;

/**
 * Реализация действия по открытию файла.
 *
 * @author Ronn
 */
public class OpenFileAction extends MenuItem {

    public static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();

    /**
     * Элемент действия.
     */
    private final ResourceElement element;

    public OpenFileAction(final ResourceElement element) {
        this.element = element;
        setText(ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_OPEN_FILE);
        setOnAction(event -> processOpen());
    }

    /**
     * Процесс открытия файла.
     */
    private void processOpen() {

        final RequestedOpenFileEvent event = new RequestedOpenFileEvent();
        event.setFile(element.getFile());

        FX_EVENT_MANAGER.notify(event);
    }
}
