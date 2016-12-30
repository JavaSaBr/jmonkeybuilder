package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.file.converter.FileConverterDescription;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.RequestedConvertFileEvent;

import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;

/**
 * The action to transformation a file by a transformer.
 *
 * @author JavaSaBr
 */
public class ConvertFileByConverterAction extends MenuItem {

    private static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();

    /**
     * The action element.
     */
    private final ResourceElement element;

    /**
     * The transformer description.
     */
    private final FileConverterDescription description;

    public ConvertFileByConverterAction(final ResourceElement element, final FileConverterDescription description) {
        this.element = element;
        this.description = description;
        setText(description.getDescription());
        setOnAction(event -> processConvert());
        setGraphic(new ImageView(Icons.TRANSFORMATION_16));
    }

    /**
     * Process of transformation.
     */
    private void processConvert() {

        final RequestedConvertFileEvent event = new RequestedConvertFileEvent();
        event.setFile(element.getFile());
        event.setDescription(description);

        FX_EVENT_MANAGER.notify(event);
    }
}
