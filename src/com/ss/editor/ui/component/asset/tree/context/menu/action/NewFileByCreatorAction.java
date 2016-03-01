package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.component.creator.FileCreatorDescription;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.RequestedCreateFileEvent;

import javafx.scene.control.MenuItem;

/**
 * Реализация действия по созданию файла.
 *
 * @author Ronn
 */
public class NewFileByCreatorAction extends MenuItem {

    private static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();

    /**
     * Элемент действия.
     */
    private final ResourceElement element;

    /**
     * Описание создателя файлов.
     */
    private final FileCreatorDescription description;

    public NewFileByCreatorAction(final ResourceElement element, final FileCreatorDescription description) {
        this.element = element;
        this.description = description;
        setText(description.getFileDescription());
        setOnAction(event -> processCreate());
    }

    /**
     * Процесс создания файла.
     */
    private void processCreate() {

        final RequestedCreateFileEvent event = new RequestedCreateFileEvent();
        event.setFile(element.getFile());
        event.setDescription(description);

        FX_EVENT_MANAGER.notify(event);
    }
}
