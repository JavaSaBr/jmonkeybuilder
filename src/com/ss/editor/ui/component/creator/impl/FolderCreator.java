package com.ss.editor.ui.component.creator.impl;

import com.ss.editor.Messages;
import com.ss.editor.ui.component.creator.FileCreatorDescription;
import com.ss.editor.util.EditorUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import rlib.util.StringUtils;

/**
 * Реализация создателя новых папок.
 *
 * @author Ronn
 */
public class FolderCreator extends AbstractFileCreator {

    public static final FileCreatorDescription DESCRIPTION = new FileCreatorDescription();

    static {
        DESCRIPTION.setFileDescription(Messages.FOLDER_CREATOR_DESCRIPTION);
        DESCRIPTION.setConstructor(FolderCreator::new);
    }

    public FolderCreator() {
    }

    @Override
    protected String getTitleText() {
        return Messages.FOLDER_CREATOR_TITLE;
    }

    @Override
    protected String getFileExtension() {
        return StringUtils.EMPTY;
    }

    @Override
    protected String getFileNameLabelText() {
        return Messages.FOLDER_CREATOR_FILE_NAME_LABEL;
    }

    @Override
    protected void processCreate() {
        super.processCreate();

        final Path fileToCreate = getFileToCreate();

        try {
            Files.createDirectory(fileToCreate);
        } catch (final IOException e) {
            EditorUtil.handleException(LOGGER, this, e);
            return;
        }

        notifyFileCreated(fileToCreate, true);
    }
}
