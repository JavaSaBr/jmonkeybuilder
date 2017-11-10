package com.ss.editor.ui.component.creator.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.component.creator.FileCreatorDescription;
import com.ss.editor.util.EditorUtil;
import org.jetbrains.annotations.NotNull;
import com.ss.rlib.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The creator to create a folder.
 *
 * @author JavaSaBr
 */
public class FolderCreator extends AbstractFileCreator {

    /**
     * The constant DESCRIPTION.
     */
    @NotNull
    public static final FileCreatorDescription DESCRIPTION = new FileCreatorDescription();

    static {
        DESCRIPTION.setFileDescription(Messages.FOLDER_CREATOR_DESCRIPTION);
        DESCRIPTION.setConstructor(FolderCreator::new);
    }

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
    @FXThread
    protected void processOk() {
        super.hide();

        final Path fileToCreate = notNull(getFileToCreate());
        try {
            Files.createDirectory(fileToCreate);
        } catch (final IOException e) {
            EditorUtil.handleException(LOGGER, this, e);
            return;
        }

        notifyFileCreated(fileToCreate, true);
    }
}
