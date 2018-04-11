package com.ss.editor.util.svg;

import static org.apache.batik.transcoder.SVGAbstractTranscoder.KEY_HEIGHT;
import static org.apache.batik.transcoder.SVGAbstractTranscoder.KEY_WIDTH;
import com.ss.editor.annotation.FxThread;
import com.sun.javafx.iio.ImageFrame;
import com.sun.javafx.iio.ImageStorage;
import de.codecentric.centerdevice.javafxsvg.BufferedImageTranscoder;
import de.codecentric.centerdevice.javafxsvg.FixedPixelDensityImageFrame;
import de.codecentric.centerdevice.javafxsvg.ScreenHelper;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @author JavaSaBr
 */
public class SvgImageLoader extends de.codecentric.centerdevice.javafxsvg.SvgImageLoader {

    @NotNull
    public static ThreadLocal<Color> OVERRIDE_COLOR = new ThreadLocal<>();

    private static final int DEFAULT_SIZE = 400;
    private static final int BYTES_PER_PIXEL = 4; // RGBA

    private static Float pixelScale;

    @NotNull
    private final InputStream input;

    protected SvgImageLoader(@NotNull final InputStream input) {
        super(input);
        this.input = input;
    }

    @Override
    @FxThread
    public float getPixelScale() {

        if (pixelScale == null) {
            pixelScale = calculateMaxRenderScale();
        }

        return pixelScale;
    }

    @Override
    @FxThread
    public @NotNull ImageFrame load(int imageIndex, int width, int height, boolean preserveAspectRatio, boolean smooth)
            throws IOException {

        if (0 != imageIndex) {
            return null;
        }

        int imageWidth = width > 0 ? width : DEFAULT_SIZE;
        int imageHeight = height > 0 ? height : DEFAULT_SIZE;

        try {
            return createImageFrame(imageWidth, imageHeight, getPixelScale());
        } catch (final TranscoderException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    @FxThread
    public float calculateMaxRenderScale() {

        float maxRenderScale = 0;

        ScreenHelper.ScreenAccessor accessor = ScreenHelper.getScreenAccessor();

        for (final Screen screen : Screen.getScreens()) {
            maxRenderScale = Math.max(maxRenderScale, accessor.getRenderScale(screen));
        }

        return maxRenderScale;
    }

    @FxThread
    private @NotNull ImageFrame createImageFrame(final int width, final int height, final float pixelScale)
            throws TranscoderException {

        final BufferedImage bufferedImage = getTranscodedImage(width * pixelScale, height * pixelScale);
        final ByteBuffer imageData = getImageData(bufferedImage);

        return new FixedPixelDensityImageFrame(ImageStorage.ImageType.RGBA, imageData, bufferedImage.getWidth(),
                bufferedImage.getHeight(), getStride(bufferedImage), null, pixelScale, null);
    }

    @FxThread
    private @NotNull BufferedImage getTranscodedImage(final float width, final float height) throws TranscoderException {
        final BufferedImageTranscoder trans = new BufferedImageTranscoder(BufferedImage.TYPE_INT_ARGB);
        trans.addTranscodingHint(KEY_WIDTH, width);
        trans.addTranscodingHint(KEY_HEIGHT, height);
        trans.transcode(new TranscoderInput(this.input), null);
        return trans.getBufferedImage();
    }

    @FxThread
    private int getStride(@NotNull final BufferedImage bufferedImage) {
        return bufferedImage.getWidth() * BYTES_PER_PIXEL;
    }

    /**
     * Extract bytes pixels from the image.
     *
     * @param bufferedImage the image.
     * @return the bytes pixels.
     */
    @FxThread
    private @NotNull ByteBuffer getImageData(@NotNull final BufferedImage bufferedImage) {

        final int[] argbData = bufferedImage.getRGB(0, 0, bufferedImage.getWidth(),
                bufferedImage.getHeight(), null, 0, bufferedImage.getWidth());
        final byte[] imageData = new byte[getStride(bufferedImage) * bufferedImage.getHeight()];

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
    private void copyColorToBytes(final int[] argbData, final byte[] imageData) {

        if (argbData.length * BYTES_PER_PIXEL != imageData.length) {
            throw new ArrayIndexOutOfBoundsException();
        }

        final Color overrideColor = OVERRIDE_COLOR.get();

        for (int i = 0; i < argbData.length; i++) {

            final int argb = argbData[i];

            int alpha = argb >>> 24;
            int red = (argb >> 16) & 0xff;
            int green = (argb >> 8) & 0xff;
            int blue = (argb) & 0xff;

            if (overrideColor != null) {
                red = (int) (overrideColor.getRed() * 255);
                green = (int) (overrideColor.getGreen() * 255);
                blue = (int) (overrideColor.getBlue() * 255);
            }

            int dataOffset = BYTES_PER_PIXEL * i;
            imageData[dataOffset] = (byte) red;
            imageData[dataOffset + 1] = (byte) green;
            imageData[dataOffset + 2] = (byte) blue;
            imageData[dataOffset + 3] = (byte) alpha;
        }
    }
}
