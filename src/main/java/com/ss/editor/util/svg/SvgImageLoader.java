package com.ss.editor.util.svg;

import static org.apache.batik.transcoder.SVGAbstractTranscoder.KEY_HEIGHT;
import static org.apache.batik.transcoder.SVGAbstractTranscoder.KEY_WIDTH;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.sun.javafx.iio.ImageFrame;
import com.sun.javafx.iio.ImageStorage.ImageType;
import de.codecentric.centerdevice.javafxsvg.BufferedImageTranscoder;
import de.codecentric.centerdevice.javafxsvg.FixedPixelDensityImageFrame;
import de.codecentric.centerdevice.javafxsvg.ScreenHelper;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @author JavaSaBr
 */
public class SvgImageLoader extends de.codecentric.centerdevice.javafxsvg.SvgImageLoader {

    private static final Logger LOGGER = LoggerManager.getLogger(SvgImageLoader.class);

    public static final ThreadLocal<Color> OVERRIDE_COLOR = new ThreadLocal<>();

    private static final int DEFAULT_SIZE = 400;
    private static final int BYTES_PER_PIXEL = 4; // RGBA

    @NotNull
    private final InputStream input;

    protected SvgImageLoader(@NotNull InputStream input) {
        super(input);
        this.input = input;
    }

    @Override
    @FxThread
    public float getPixelScale() {
        return (float) EditorUtil.getFxStage()
                .getRenderScaleX();
    }

    @Override
    @FxThread
    public @Nullable ImageFrame load(int imageIndex, int width, int height, boolean preserveAspectRatio, boolean smooth)
            throws IOException {

        if (0 != imageIndex) {
            return null;
        }

        var imageWidth = width > 0 ? width : DEFAULT_SIZE;
        var imageHeight = height > 0 ? height : DEFAULT_SIZE;

        try {
            return createImageFrame(imageWidth, imageHeight, getPixelScale());
        } catch (TranscoderException ex) {
            LOGGER.error(ex);
            throw new IOException(ex);
        }
    }

    @FxThread
    private @NotNull ImageFrame createImageFrame(int width, int height, float pixelScale)
            throws TranscoderException {

        var bufferedImage = getTranscodedImage(width * pixelScale, height * pixelScale);
        var imageData = getImageData(bufferedImage);

        return new FixedPixelDensityImageFrame(ImageType.RGBA, imageData, bufferedImage.getWidth(),
                bufferedImage.getHeight(), getStride(bufferedImage), null, pixelScale, null);
    }

    @FxThread
    private @NotNull BufferedImage getTranscodedImage(float width, float height) throws TranscoderException {

        var trans = new BufferedImageTranscoder(BufferedImage.TYPE_INT_ARGB);
        trans.addTranscodingHint(KEY_WIDTH, width);
        trans.addTranscodingHint(KEY_HEIGHT, height);
        trans.transcode(new TranscoderInput(this.input), null);

        return trans.getBufferedImage();
    }

    @FxThread
    private int getStride(@NotNull BufferedImage bufferedImage) {
        return bufferedImage.getWidth() * BYTES_PER_PIXEL;
    }

    /**
     * Extract bytes pixels from the image.
     *
     * @param bufferedImage the image.
     * @return the bytes pixels.
     */
    @FxThread
    private @NotNull ByteBuffer getImageData(@NotNull BufferedImage bufferedImage) {

        var argbData = bufferedImage.getRGB(0, 0, bufferedImage.getWidth(),
                bufferedImage.getHeight(), null, 0, bufferedImage.getWidth());

        var imageData = new byte[getStride(bufferedImage) * bufferedImage.getHeight()];

        copyColorToBytes(argbData, imageData);

        return ByteBuffer.wrap(imageData);
    }

    /**
     * Copy pixels from image to byte array.
     *
     * @param argbData  the argb pixels data.
     * @param imageData the bytes pixels data.
     */
    @FxThread
    private void copyColorToBytes(int[] argbData, byte[] imageData) {

        if (argbData.length * BYTES_PER_PIXEL != imageData.length) {
            throw new ArrayIndexOutOfBoundsException();
        }

        var overrideColor = OVERRIDE_COLOR.get();

        for (int i = 0; i < argbData.length; i++) {

            var argb = argbData[i];
            var alpha = argb >>> 24;
            var red = (argb >> 16) & 0xff;
            var green = (argb >> 8) & 0xff;
            var blue = (argb) & 0xff;

            if (overrideColor != null) {
                red = (int) (overrideColor.getRed() * 255);
                green = (int) (overrideColor.getGreen() * 255);
                blue = (int) (overrideColor.getBlue() * 255);
            }

            var dataOffset = BYTES_PER_PIXEL * i;

            imageData[dataOffset] = (byte) red;
            imageData[dataOffset + 1] = (byte) green;
            imageData[dataOffset + 2] = (byte) blue;
            imageData[dataOffset + 3] = (byte) alpha;
        }
    }
}
