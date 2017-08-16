package com.ss.editor.ui.component.creator.impl;

import com.ss.editor.Messages;
import com.ss.editor.ui.component.creator.FileCreatorDescription;
import com.ss.rlib.util.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * The creator to create an empty file.
 *
 * @author JavaSaBr
 */
public class EmptyFileCreator extends AbstractFileCreator {

    /**
     * The constant DESCRIPTION.
     */
    @NotNull
    public static final FileCreatorDescription DESCRIPTION = new FileCreatorDescription();

    static {
        DESCRIPTION.setFileDescription(Messages.EMPTY_FILE_CREATOR_DESCRIPTION);
        DESCRIPTION.setConstructor(EmptyFileCreator::new);
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.EMPTY_FILE_CREATOR_TITLE;
    }

    @NotNull
    @Override
    protected String getFileExtension() {
        return StringUtils.EMPTY;
    }
}
