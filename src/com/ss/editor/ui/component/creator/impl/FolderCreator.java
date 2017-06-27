package com.ss.editor.ui.component.creator.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.Messages;
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

    private FolderCreator() {
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.FOLDER_CREATOR_TITLE;
    }

    @NotNull
    @Override
    protected String getFileExtension() {
        return StringUtils.EMPTY;
    }

    @NotNull
    @Override
    protected String getFileNameLabelText() {
        return Messages.FOLDER_CREATOR_FILE_NAME_LABEL;
    }

    @Override
    protected void processOk() {
        super.processOk();

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
