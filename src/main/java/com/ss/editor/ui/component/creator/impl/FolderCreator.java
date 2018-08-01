package com.ss.editor.ui.component.creator.impl;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.component.creator.FileCreatorDescriptor;
import com.ss.editor.util.EditorUtils;
import com.ss.rlib.common.util.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;

/**
 * The creator to create a folder.
 *
 * @author JavaSaBr
 */
public class FolderCreator extends AbstractFileCreator {

    public static final FileCreatorDescriptor DESCRIPTOR = new FileCreatorDescriptor(
            Messages.FOLDER_CREATOR_DESCRIPTION,
            FolderCreator::new
    );

    @Override
    @FromAnyThread
    protected @NotNull String getTitleText() {
        return Messages.FOLDER_CREATOR_TITLE;
    }

    @Override
    @FromAnyThread
    protected @NotNull String getFileExtension() {
        return StringUtils.EMPTY;
    }

    @Override
    @FromAnyThread
    protected @NotNull String getFileNameLabelText() {
        return Messages.FOLDER_CREATOR_FILE_NAME_LABEL;
    }

    @Override
    @FxThread
    protected void processOk() {
        super.hide();

        var fileToCreate = notNull(getFileToCreate());
        try {
            Files.createDirectory(fileToCreate);
        } catch (IOException e) {
            EditorUtils.handleException(LOGGER, this, e);
            return;
        }

        notifyFileCreated(fileToCreate, true);
    }
}
