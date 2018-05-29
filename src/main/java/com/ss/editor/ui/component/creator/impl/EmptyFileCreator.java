package com.ss.editor.ui.component.creator.impl;

import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.component.creator.FileCreatorDescription;
import com.ss.rlib.common.util.StringUtils;
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

    @Override
    @FromAnyThread
    protected @NotNull String getTitleText() {
        return Messages.EMPTY_FILE_CREATOR_TITLE;
    }


    @Override
    @FromAnyThread
    protected @NotNull String getFileExtension() {
        return StringUtils.EMPTY;
    }
}
