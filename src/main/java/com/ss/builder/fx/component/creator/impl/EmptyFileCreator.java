package com.ss.builder.fx.component.creator.impl;

import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.fx.component.creator.FileCreatorDescriptor;
import com.ss.rlib.common.util.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * The creator to create an empty file.
 *
 * @author JavaSaBr
 */
public class EmptyFileCreator extends AbstractFileCreator {

    public static final FileCreatorDescriptor DESCRIPTOR = new FileCreatorDescriptor(
            Messages.EMPTY_FILE_CREATOR_DESCRIPTION,
            EmptyFileCreator::new
    );

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
