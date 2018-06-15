package com.ss.editor.util.svg;

import com.ss.editor.annotation.FxThread;
import com.sun.javafx.iio.ImageFormatDescription;
import com.sun.javafx.iio.ImageLoader;
import com.sun.javafx.iio.ImageLoaderFactory;
import com.sun.javafx.iio.ImageStorage;
import de.codecentric.centerdevice.javafxsvg.SvgDescriptor;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

/**
 * @author JavaSaBr
 */
public class SvgImageLoaderFactory implements ImageLoaderFactory {

    private static final ImageLoaderFactory INSTANCE = new SvgImageLoaderFactory();

    @FxThread
    public static void install() {
        ImageStorage.addImageLoaderFactory(INSTANCE);
    }

    @FxThread
    public static @NotNull ImageLoaderFactory getInstance() {
        return INSTANCE;
    }

    @Override
    @FxThread
    public ImageFormatDescription getFormatDescription() {
        return SvgDescriptor.getInstance();
    }

    @Override
    @FxThread
    public ImageLoader createImageLoader(@NotNull InputStream input) {
        return new SvgImageLoader(input);
    }
}
