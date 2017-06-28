package com.ss.editor.ui.component.log;

import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.function.Consumer;

/**
 * The type Output stream wrapper.
 *
 * @author JavaSaBr
 */
public class OutputStreamWrapper extends PrintStream {

    @NotNull
    private final Consumer<String> consumer;

    /**
     * Instantiates a new Output stream wrapper.
     *
     * @param out      the out
     * @param consumer the consumer
     */
    public OutputStreamWrapper(@NotNull final OutputStream out, @NotNull final Consumer<String> consumer) {
        super(out);
        this.consumer = consumer;
    }

    @Override
    public void write(final byte[] buf, final int off, final int len) {
        consumer.accept(new String(buf, off, len));
        super.write(buf, off, len);
    }
}
