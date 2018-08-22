package com.ss.builder.ui.component.asset.tree.context.menu.action;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.file.converter.FileConverterDescription;
import com.ss.builder.ui.Icons;
import com.ss.builder.ui.component.asset.tree.resource.ResourceElement;
import com.ss.builder.ui.event.FxEventManager;
import com.ss.builder.ui.event.impl.RequestedConvertFileEvent;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.file.converter.FileConverterDescription;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.event.FxEventManager;
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
public class ConvertFileByConverterAction extends FileAction {

    /**
     * The transformer description.
     */
    @NotNull
    private final FileConverterDescription description;

    public ConvertFileByConverterAction(
            @NotNull ResourceElement element,
            @NotNull FileConverterDescription description
    ) {
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
    protected void execute(@Nullable ActionEvent event) {
        super.execute(event);

        FxEventManager.getInstance()
                .notify(new RequestedConvertFileEvent(description, getFile()));
    }
}
