package com.ss.builder.file.handler.delete;

import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.file.handler.delete.impl.DeleteMaterialsModelFileDeleteHandler;
import com.ss.rlib.common.util.array.ArrayCollectors;
import org.jetbrains.annotations.NotNull;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;

import java.nio.file.Path;

/**
 * The factory of delete handlers of deleted file.
 *
 * @author JavaSaBr
 */
public class FileDeleteHandlerFactory {

    private static final Array<FileDeleteHandler> HANDLERS = ArrayFactory.newArray(FileDeleteHandler.class);

    static {
        HANDLERS.add(new DeleteMaterialsModelFileDeleteHandler());
    }

    /**
     * Find handlers for the file.
     *
     * @param file the file.
     * @return the list of handlers.
     */
    @FromAnyThread
    public static @NotNull Array<FileDeleteHandler> findFor(@NotNull Path file) {
        return HANDLERS.stream()
                .filter(handler -> handler.isNeedHandle(file))
                .map(FileDeleteHandler::clone)
                .collect(ArrayCollectors.toArray(FileDeleteHandler.class));
    }
}
