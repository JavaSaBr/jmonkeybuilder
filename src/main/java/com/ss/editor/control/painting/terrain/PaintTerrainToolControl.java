package com.ss.editor.control.painting.terrain;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.terrain.Terrain;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.control.painting.PaintingInput;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.component.painting.terrain.TerrainPaintingComponent;
import com.ss.editor.ui.control.property.operation.PropertyOperation;
import com.ss.editor.util.LocalObjects;
import com.ss.rlib.function.ObjectFloatObjectConsumer;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;

/**
 * The implementation of terrain tool to paint textures.
 *
 * @author JavaSaBr
 */
public class PaintTerrainToolControl extends TerrainToolControl {

    private static class ColorPoint {

        private final int index;

        private float red;
        private float green;
        private float blue;
        private float alpha;

        public ColorPoint(final int index, final float red, final float green, final float blue, final float alpha) {
            this.index = index;
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final ColorPoint that = (ColorPoint) o;
            return index == that.index;
        }

        @Override
        public int hashCode() {
            return index;
        }
    }

    @NotNull
    private static final ObjectFloatObjectConsumer<ColorRGBA, Boolean> RED_FUNCTION = (color, value, erase) -> {
        if (erase) {
            color.r -= value;
        } else {
            color.r += value;
        }
    };

    @NotNull
    private static final ObjectFloatObjectConsumer<ColorRGBA, Boolean> GREEN_FUNCTION = (color, value, erase) -> {
        if (erase) {
            color.g -= value;
        } else {
            color.g += value;
        }
    };

    @NotNull
    private static final ObjectFloatObjectConsumer<ColorRGBA, Boolean> BLUE_FUNCTION = (color, value, erase) -> {
        if (erase) {
            color.b -= value;
        } else {
            color.b += value;
        }
    };

    @NotNull
    private static final ObjectFloatObjectConsumer<ColorRGBA, Boolean> ALPHA_FUNCTION = (color, value, erase) -> {
        if (erase) {
            color.a -= value;
        } else {
            color.a += value;
        }
    };

    @NotNull
    private static final ObjectFloatObjectConsumer<ColorRGBA, Boolean>[] COLOR_FUNCTIONS =
            ArrayFactory.toArray(RED_FUNCTION, GREEN_FUNCTION, BLUE_FUNCTION, ALPHA_FUNCTION);

    /**
     * The list of color points.
     */
    @NotNull
    private final Array<ColorPoint> colorPoints;

    /**
     * The alpha texture to paint.
     */
    @Nullable
    private Texture alphaTexture;

    /**
     * Previous image buffer.
     */
    @Nullable
    private ByteBuffer prevBuffer;

    /**
     * The edited layer.
     */
    private int layer;

    public PaintTerrainToolControl(@NotNull final TerrainPaintingComponent component) {
        super(component);
        this.colorPoints = ArrayFactory.newArray(ColorPoint.class);
    }

    @Override
    @FromAnyThread
    protected @NotNull ColorRGBA getBrushColor() {
        return ColorRGBA.Blue;
    }

    @Override
    @JmeThread
    public void startPainting(@NotNull final PaintingInput input, @NotNull final Quaternion brushRotation,
                              @NotNull final Vector3f contactPoint) {

        final Texture alphaTexture = getAlphaTexture();
        if (alphaTexture == null) {
            return;
        }

        super.startPainting(input, brushRotation, contactPoint);

        switch (input) {
            case MOUSE_PRIMARY:
            case MOUSE_SECONDARY: {
                startChange();
                paintTexture(input, contactPoint);
                break;
            }
        }
    }

    @Override
    @JmeThread
    public void updatePainting(@NotNull final Quaternion brushRotation, @NotNull final Vector3f contactPoint,
                               final float tpf) {

        final PaintingInput input = notNull(getCurrentInput());

        switch (input) {
            case MOUSE_PRIMARY:
            case MOUSE_SECONDARY: {
                paintTexture(input, contactPoint);
                break;
            }
        }
    }

    @Override
    @JmeThread
    public void finishPainting(@NotNull final Quaternion brushRotation, @NotNull final Vector3f contactPoint) {

        final PaintingInput input = notNull(getCurrentInput());

        switch (input) {
            case MOUSE_PRIMARY:
            case MOUSE_SECONDARY: {
                paintTexture(input, contactPoint);
                commitChanges();
                break;
            }
        }

        super.finishPainting(brushRotation, contactPoint);
    }

    /**
     * Get the list of color points.
     *
     * @return the list of color points.
     */
    @JmeThread
    private @NotNull Array<ColorPoint> getColorPoints() {
        return colorPoints;
    }

    /**
     * Start making changes.
     */
    @JmeThread
    private void startChange() {

        final Array<ColorPoint> colorPoints = getColorPoints();
        colorPoints.clear();

        final Texture alphaTexture = notNull(getAlphaTexture());
        final Image image = alphaTexture.getImage();
        final ByteBuffer data = image.getData(0);

        if (prevBuffer == null) {
            prevBuffer = BufferUtils.createByteBuffer(data.capacity());
        } else if (prevBuffer.capacity() < data.capacity()) {
            BufferUtils.destroyDirectBuffer(prevBuffer);
            prevBuffer = BufferUtils.createByteBuffer(data.capacity());
        }

        final int position = data.position();
        data.position(0);
        prevBuffer.clear();
        prevBuffer.put(data);
        prevBuffer.flip();
        data.position(position);
    }

    /**
     * Notify about wanting to change color of a point.
     *
     * @param index the index
     * @param color the color
     */
    @JmeThread
    protected void change(final int index, @NotNull final ColorRGBA color) {

        final Array<ColorPoint> colorPoints = getColorPoints();
        final ColorPoint search = colorPoints.search(index, (point, toCheck) -> point.index == toCheck);

        if (search != null) {
            search.red = color.r;
            search.green = color.g;
            search.blue = color.b;
            search.alpha = color.a;
            return;
        }

        colorPoints.add(new ColorPoint(index, color.r, color.g, color.b, color.a));
    }

    /**
     * Get the previous image buffer.
     *
     * @return the previous image buffer.
     */
    @JmeThread
    private @NotNull ByteBuffer getPrevBuffer() {
        return notNull(prevBuffer);
    }

    /**
     * Commit all changes.
     */
    @JmeThread
    private void commitChanges() {

        final ByteBuffer prevBuffer = getPrevBuffer();
        final Texture alphaTexture = notNull(getAlphaTexture());
        final Image image = alphaTexture.getImage();

        final Array<ColorPoint> colorPoints = getColorPoints();
        final Array<ColorPoint> prevColorPoints = ArrayFactory.newArray(ColorPoint.class, colorPoints.size());
        final Array<ColorPoint> newColorPoints = ArrayFactory.newArray(ColorPoint.class, colorPoints.size());
        newColorPoints.addAll(colorPoints);

        fillPrevColorPoints(prevBuffer, image, colorPoints, prevColorPoints);

        final PropertyOperation<ChangeConsumer, Image, Array<ColorPoint>> operation =
                new PropertyOperation<>(image, "AlphaMap", newColorPoints, prevColorPoints);
        operation.setApplyHandler((img, toApply) -> applyColorPoints(img, toApply, toApply == newColorPoints));

        colorPoints.clear();

        final ModelChangeConsumer changeConsumer = getChangeConsumer();
        changeConsumer.execute(operation);
    }

    /**
     * Apply color points to an image.
     */
    @JmeThread
    private static void applyColorPoints(@NotNull final Image image, @NotNull final Array<ColorPoint> colorPoints,
                                         final boolean isRedo) {

        final ByteBuffer buffer = image.getData(0);

        switch (image.getFormat()) {
            case RGBA8: {
                for (final ColorPoint targetPoint : colorPoints) {
                    buffer.position(targetPoint.index);
                    buffer.put(float2byte(targetPoint.red));
                    buffer.put(float2byte(targetPoint.green));
                    buffer.put(float2byte(targetPoint.blue));
                    buffer.put(float2byte(targetPoint.alpha));
                }
                break;
            }
            case ABGR8: {
                for (final ColorPoint targetPoint : colorPoints) {
                    buffer.position(targetPoint.index);
                    buffer.put(float2byte(targetPoint.alpha));
                    buffer.put(float2byte(targetPoint.blue));
                    buffer.put(float2byte(targetPoint.green));
                    buffer.put(float2byte(targetPoint.red));
                }
                break;
            }
        }

        buffer.rewind();

        if (isRedo) {
            image.incrementChange();
        } else {
            image.decrementChanges();
        }

        image.setUpdateNeeded();
    }

    /**
     * Fill previous color points.
     *
     * @param prevBuffer      the buffer of previous image.
     * @param image           the current image.
     * @param colorPoints     the list of changes color points.
     * @param prevColorPoints the list to fill original color points.
     */
    @JmeThread
    private void fillPrevColorPoints(@NotNull final ByteBuffer prevBuffer, @NotNull final Image image,
                                     @NotNull final Array<ColorPoint> colorPoints,
                                     @NotNull final Array<ColorPoint> prevColorPoints) {

        switch (image.getFormat()) {
            case RGBA8: {
                for (final ColorPoint colorPoint : colorPoints) {
                    prevBuffer.position(colorPoint.index);
                    final float r = byte2float(prevBuffer.get());
                    final float g = byte2float(prevBuffer.get());
                    final float b = byte2float(prevBuffer.get());
                    final float a = byte2float(prevBuffer.get());
                    prevColorPoints.add(new ColorPoint(colorPoint.index, r, g, b, a));
                }
                break;
            }
            case ABGR8: {
                for (final ColorPoint colorPoint : colorPoints) {
                    prevBuffer.position(colorPoint.index);
                    float a = byte2float(prevBuffer.get());
                    float b = byte2float(prevBuffer.get());
                    float g = byte2float(prevBuffer.get());
                    float r = byte2float(prevBuffer.get());
                    prevColorPoints.add(new ColorPoint(colorPoint.index, r, g, b, a));
                }
                break;
            }
        }
    }

    /**
     * Set the alpha texture.
     *
     * @param alphaTexture the alpha texture to paint.
     */
    @JmeThread
    public void setAlphaTexture(@Nullable final Texture alphaTexture) {
        this.alphaTexture = alphaTexture;
    }

    /**
     * Get the alpha texture to paint.
     *
     * @return the alpha texture to paint.
     */
    @JmeThread
    private @Nullable Texture getAlphaTexture() {
        return alphaTexture;
    }

    /**
     * Set the edited layer.
     *
     * @param layer the edited layer.
     */
    @JmeThread
    public void setLayer(final int layer) {
        this.layer = layer;
    }

    /**
     * Get the edited layer.
     *
     * @return the edited layer.
     */
    @JmeThread
    private int getLayer() {
        return layer;
    }

    /**
     * Paint texture.
     *
     * @param input the editing input.
     * @param contactPoint the contact point.
     */
    @JmeThread
    private void paintTexture(@NotNull final PaintingInput input, @NotNull final Vector3f contactPoint) {

        final Texture alphaTexture = getAlphaTexture();
        if (alphaTexture == null) {
            return;
        }

        final LocalObjects local = getLocalObjects();
        final Spatial terrainNode = notNull(getPaintedModel());
        final Terrain terrain = (Terrain) terrainNode;
        final Image image = alphaTexture.getImage();

        final Vector3f worldTranslation = terrainNode.getWorldTranslation();
        final Vector3f localPoint = contactPoint.subtract(worldTranslation, local.nextVector());
        final Vector3f localScale = terrainNode.getLocalScale();
        final Vector2f uv = getPointPercentagePosition(terrain, localPoint, localScale, local.nextVector2f());
        final Vector2f temp = local.nextVector2f();
        final ColorRGBA color = local.nextColor();

        final int layer = getLayer();

        // get the radius of the brush in pixel-percent
        float brushSize = getBrushSize() / (terrain.getTerrainSize() * localScale.getX());
        float brushPower = getBrushPower();

        if (input == PaintingInput.MOUSE_SECONDARY) {
            brushPower *= -1;
        }

        // selectedTextureIndex/4 is an int floor, do not simplify the equation
        final ObjectFloatObjectConsumer<ColorRGBA, Boolean> colorFunction = COLOR_FUNCTIONS[layer - ((layer / 4) * 4)];

        doPaintAction(colorFunction, image, uv, temp, color, brushSize, false, brushPower);

        image.setUpdateNeeded();
    }

    @JmeThread
    private @NotNull Vector2f getPointPercentagePosition(@NotNull final Terrain terrain,
                                                         @NotNull final Vector3f localPoint,
                                                         @NotNull final Vector3f localScale,
                                                         @NotNull final Vector2f result) {
        result.set(localPoint.x, -localPoint.z);

        float scale = localScale.getX();

        // already centered on Terrain's node origin (0,0)
        float scaledSize = terrain.getTerrainSize() * scale;
        result.addLocal(scaledSize / 2, scaledSize / 2); // shift the bottom left corner up to 0,0
        result.divideLocal(scaledSize); // get the location as a percentage

        return result;
    }

    /**
     * Goes through each pixel in the image. At each pixel it looks to see if the UV mouse coordinate is within the
     * of the brush. If it is in the brush radius, it gets the existing color from that pixel so it can add/subtract to/from it.
     * Essentially it does a radius check and adds in a fade value. It does this to the color value returned by the
     * first pixel color query.
     * Next it sets the color of that pixel. If it was within the radius, the color will change. If it was outside
     * the radius, then nothing will change, the color will be the same; but it will set it nonetheless. Not efficient.
     * <p>
     * If the mouse is being dragged with the button down, then the dragged value should be set to true. This will reduce
     * the intensity of the brush to 10% of what it should be per spray. Otherwise it goes to 100% opacity within a few pixels.
     * This makes it work a little more realistically.
     *
     * @param colorFunction the color function.
     * @param image         to manipulate
     * @param uv            the world x,z coordinate
     * @param radius        in percentage so it can be translated to the image dimensions
     * @param erase         true if the tool should remove the paint instead of add it
     * @param fadeFalloff   the percentage of the radius when the paint begins to start fading
     */
    @JmeThread
    private void doPaintAction(@NotNull final ObjectFloatObjectConsumer<ColorRGBA, Boolean> colorFunction,
                               @NotNull final Image image, @NotNull final Vector2f uv, @NotNull final Vector2f temp,
                               @NotNull final ColorRGBA color, final float radius, final boolean erase,
                               final float fadeFalloff) {

        final ByteBuffer buffer = image.getData(0);

        final int width = image.getWidth();
        final float height = image.getHeight();

        // convert percents to pixels to limit how much we iterate
        final int minX = (int) Math.max(0, (uv.getX() * width - radius * width));
        final int maxX = (int) Math.min(width, (uv.getX() * width + radius * width));
        final int minY = (int) Math.max(0, (uv.getY() * height - radius * height));
        final int maxY = (int) Math.min(height, (uv.getY() * height + radius * height));

        final float radiusSquared = radius * radius;

        // go through each pixel, in the radius of the tool, in the image
        for (int y = minY; y < maxY; y++) {
            for (int x = minX; x < maxX; x++) {

                // gets the position in percentage so it can compare with the mouse UV coordinate
                temp.set((float) x / width, (float) y / height);

                float dist = temp.distanceSquared(uv);

                // if the pixel is within the distance of the radius, set a color (distance times intensity)
                if (dist < radiusSquared) {

                    final int position = (y * width + x) * 4;
                    if (position > buffer.capacity() - 1 || position < 0) {
                        continue;
                    }

                    // gets the color at that location (false means don't write to the buffer)
                    manipulatePixel(image, buffer, color, position, false);

                    // calculate the fade falloff intensity
                    final float intensity = (1.0f - (dist / radiusSquared)) * fadeFalloff;

                    colorFunction.accept(color, intensity, erase);
                    color.clamp();

                    change(position, color);

                    // set the new color
                    manipulatePixel(image, buffer, color, position, true);
                }
            }
        }

        image.getData(0).rewind();
    }

    /**
     * We are only using RGBA8 images for alpha textures right now.
     *
     * @param image    to get/set the color on
     * @param buffer   the buffer of the image.
     * @param color    color to get/set
     * @param position position in the buffer.
     * @param write    to write the color or not
     */
    @JmeThread
    private void manipulatePixel(@NotNull final Image image, @NotNull final ByteBuffer buffer,
                                 @NotNull final ColorRGBA color, final int position, final boolean write) {

        if (write) {
            switch (image.getFormat()) {
                case RGBA8: {
                    buffer.position(position);
                    buffer.put(float2byte(color.r));
                    buffer.put(float2byte(color.g));
                    buffer.put(float2byte(color.b));
                    buffer.put(float2byte(color.a));
                    return;
                }
                case ABGR8: {
                    buffer.position(position);
                    buffer.put(float2byte(color.a));
                    buffer.put(float2byte(color.b));
                    buffer.put(float2byte(color.g));
                    buffer.put(float2byte(color.r));
                    return;
                }
                default: {
                    throw new UnsupportedOperationException("Image format: " + image.getFormat());
                }
            }
        } else {
            switch (image.getFormat()) {
                case RGBA8: {
                    buffer.position(position);
                    final float r = byte2float(buffer.get());
                    final float g = byte2float(buffer.get());
                    final float b = byte2float(buffer.get());
                    final float a = byte2float(buffer.get());
                    color.set(r, g, b, a);
                    return;
                }
                case ABGR8: {
                    buffer.position(position);
                    float a = byte2float(buffer.get());
                    float b = byte2float(buffer.get());
                    float g = byte2float(buffer.get());
                    float r = byte2float(buffer.get());
                    color.set(r, g, b, a);
                    return;
                }
                default: {
                    throw new UnsupportedOperationException("Image format: " + image.getFormat());
                }
            }
        }
    }

    @FromAnyThread
    private static float byte2float(byte b) {
        return ((float) (b & 0xFF)) / 255f;
    }

    @FromAnyThread
    private static byte float2byte(float f) {
        return (byte) (f * 255f);
    }
}
