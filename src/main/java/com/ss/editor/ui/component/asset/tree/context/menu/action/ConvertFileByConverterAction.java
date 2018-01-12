package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.annotation.FxThread;
import com.ss.editor.file.converter.FileConverterDescription;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.event.impl.RequestedConvertFileEvent;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to transformation a file by a transformer.
 *
 * @author JavaSaBr
 */
class ConvertFileByConverterAction extends FileAction {

    /**
     * The transformer description.
     */
    @NotNull
    private final FileConverterDescription description;

    public ConvertFileByConverterAction(@NotNull final ResourceElement element,
                                        @NotNull final FileConverterDescription description) {
        super(element);
        this.description = description;
        setText(description.getDescription());
    }

    @FxThread
    @Override
    protected @Nullable Image getIcon() {
        return Icons.TRANSFORMATION_16;
    }

    @FxThread
    @Override
    protected void execute(@Nullable final ActionEvent event) {
        super.execute(event);

        final ResourceElement element = getElement();
        final RequestedConvertFileEvent newEvent = new RequestedConvertFileEvent();
        newEvent.setFile(element.getFile());
        newEvent.setDescription(description);

        FX_EVENT_MANAGER.notify(newEvent);
    }
}
