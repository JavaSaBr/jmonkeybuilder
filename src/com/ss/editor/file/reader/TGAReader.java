package com.ss.editor.file.reader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Реализация читальщика TGA изображений.
 *
 * @author Ronn
 */
public class TGAReader {

    public static Image getImage(final byte[] buffer) throws IOException {
        return decode(buffer);
    }

    private static int btoi(final byte b) {
        int a = b;
        return (a < 0 ? 256 + a : a);
    }

    private static int read(final int offset, final byte[] buffer) {
        return btoi(buffer[offset]);
    }

    public static Image decode(final byte[] buffer) {

        int offset = 0;

        // Reading header bytes
        // buffer[2]=image type code 0x02=uncompressed BGR or BGRA
        // buffer[12]+[13]=width
        // buffer[14]+[15]=height
        // buffer[16]=image pixel size 0x20=32bit, 0x18=24bit
        // buffer{17]=Image Descriptor Byte=0x28 (00101000)=32bit/origin upperleft/non-interleaved
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