package com.ss.editor.ui.component.creator.impl;

import com.ss.editor.Messages;
import com.ss.editor.ui.component.creator.FileCreatorDescription;
import com.ss.editor.util.EditorUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import rlib.util.StringUtils;

/**
 * Реализация создателя новых пустых файлов.
 *
 * @author Ronn
 */
public class EmptyFileCreator extends AbstractFileCreator {

    public static final FileCreatorDescription DESCRIPTION = new FileCreatorDescription();

    static {
        DESCRIPTION.setFileDescription(Messages.EMPTY_FILE_CREATOR_DESCRIPTION);
        DESCRIPTION.setConstructor(EmptyFileCreator::new);
    }

    public EmptyFileCreator() {
    }

    @Override
    protected String getTitleText() {
        return Messages.EMPTY_FILE_CREATOR_TITLE;
    }

    @Override
    protected String getFileExtension() {
        return StringUtils.EMPTY;
    }

    @Override
    protected void processCreate() {
        super.processCreate();

        final Path fileToCreate = getFileToCreate();

        try {
            Files.createFile(fileToCreate);
        } catch (final IOException e) {
            EditorUtil.handleException(LOGGER, this, e);
            return;
        }

        notifyFileCreated(fileToCreate, true);
    }
}
