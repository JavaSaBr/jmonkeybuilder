package com.ss.editor.file.reader;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * The TGA image reader.
 *
 * @author JavaSaBr
 */
public class TGAReader {

    /**
     * Gets image.
     *
     * @param buffer the buffer
     * @return the image
     */
    @Nullable
    public static Image getImage(@NotNull final byte[] buffer) {
        return decode(buffer);
    }

    private static int btoi(final byte b) {
        return ((int) b < 0 ? 256 + (int) b : (int) b);
    }

    private static int read(final int offset, @NotNull final byte[] buffer) {
        return btoi(buffer[offset]);
    }

    @Nullable
    private static Image decode(@NotNull final byte[] buffer) {

        int offset = 0;

        for (int i = 0; i < 12; i++) {
            read(offset++, buffer);
        }

        int width = read(offset++, buffer) + (read(offset++, buffer) << 8);   // 00,04=1024
        int height = read(offset++, buffer) + (read(offset++, buffer) << 8);  // 40,02=576

        read(offset++, buffer);
        read(offset++, buffer);

        int n = width * height;
        int[] pixels = new int[n];
        int idx = 0;

        if (buffer[2] == 0x02 && buffer[16] == 0x20) { // uncompressed BGRA

            while (n > 0) {
                int b = read(offset++, buffer);
                int g = read(offset++, buffer);
                int r = read(offset++, buffer);
                int a = read(offset++, buffer);
                int v = (a << 24) | (r << 16) | (g << 8) | b;
                pixels[idx++] = v;
                n -= 1;
            }

        } else if (buffer[2] == 0x02 && buffer[16] == 0x18) {  // uncompressed BGR

            while (n > 0) {
                int b = read(offset++, buffer);
                int g = read(offset++, buffer);
                int r = read(offset++, buffer);
                int a = 255; // opaque pixel
                int v = (a << 24) | (r << 16) | (g << 8) | b;
                pixels[idx++] = v;
                n -= 1;
            }

        } else {

            // RLE compressed
            while (n > 0) {

                int nb = read(offset++, buffer); // num of pixels

                if ((nb & 0x80) == 0) { // 0x80=dec 128, bits 10000000
                    for (int i = 0; i <= nb; i++) {
                        int b = read(offset++, buffer);
                        int g = read(offset++, buffer);
                        int r = read(offset++, buffer);
                        pixels[idx++] = 0xff000000 | (r << 16) | (g << 8) | b;
                    }
                } else {
                    nb &= 0x7f;
                    int b = read(offset++, buffer);
                    int g = read(offset++, buffer);
                    int r = read(offset++, buffer);
                    int v = 0xff000000 | (r << 16) | (g << 8) | b;
                    for (int i = 0; i <= nb; i++)
                        pixels[idx++] = v;
                }
                n -= nb + 1;
            }
        }

        final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        bufferedImage.setRGB(0, 0, width, height, pixels, 0, width);

        return bufferedImage;
    }
}