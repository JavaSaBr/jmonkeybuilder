package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.file.converter.FileConverterDescription;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.RequestedConvertFileEvent;

import javafx.scene.control.MenuItem;

/**
 * Реализация действия по конвертированию файла.
 *
 * @author Ronn
 */
public class ConvertFileByConverterAction extends MenuItem {

    private static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();

    /**
     * Элемент действия.
     */
    private final ResourceElement element;

    /**
     * Описание конвертера.
     */
    private final FileConverterDescription description;

    public ConvertFileByConverterAction(final ResourceElement element, final FileConverterDescription description) {
        this.element = element;
        this.description = description;
        setText(description.getDescription());
        setOnAction(event -> processConvert());
    }

    /**
     * Процесс конвертирования файла.
     */
    private void processConvert() {

        final RequestedConvertFileEvent event = new RequestedConvertFileEvent();
        event.setFile(element.getFile());
        event.setDescription(description);

        FX_EVENT_MANAGER.notify(event);
    }
}
