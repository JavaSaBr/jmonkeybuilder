package com.ss.editor.util.svg;

import com.sun.javafx.iio.ImageFormatDescription;
import com.sun.javafx.iio.ImageLoader;
import com.sun.javafx.iio.ImageLoaderFactory;
import com.sun.javafx.iio.ImageStorage;
import de.codecentric.centerdevice.javafxsvg.SvgDescriptor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author JavaSaBr
 */
public class SvgImageLoaderFactory implements ImageLoaderFactory {

    @NotNull
    private static final ImageLoaderFactory instance = new SvgImageLoaderFactory();

    public static void install() {
        ImageStorage.addImageLoaderFactory(instance);
    }

    @NotNull
    public static ImageLoaderFactory getInstance() {
        return instance;
    }

    @Override
    public ImageFormatDescription getFormatDescription() {
        return SvgDescriptor.getInstance();
    }

    @Override
    public ImageLoader createImageLoader(@NotNull InputStream input) throws IOException {
        return new SvgImageLoader(input);
    }

}
