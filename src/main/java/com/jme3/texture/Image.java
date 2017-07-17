/*
 * Copyright (c) 2009-2012 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.texture;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.math.FastMath;
import com.jme3.renderer.Caps;
import com.jme3.renderer.Renderer;
import com.jme3.texture.image.ColorSpace;
import com.jme3.texture.image.LastTextureState;
import com.jme3.util.BufferUtils;
import com.jme3.util.NativeObject;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <code>Image</code> defines a data format for a graphical image. The image
 * is defined by a format, a height and width, and the image data. The width and
 * height must be greater than 0. The data is contained in a byte buffer, and
 * should be packed before creation of the image object.
 *
 * @author Mark Powell
 * @author Joshua Slack
 * @version $Id : Image.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public class Image extends NativeObject implements Savable /*, Cloneable*/ {

    /**
     * The enum Format.
     */
    public enum Format {
        /**
         * 8-bit alpha
         */
        Alpha8(8),

        /**
         * Reserved 1 format.
         */
        @Deprecated
        Reserved1(0),

        /**
         * 8-bit grayscale/luminance.
         */
        Luminance8(8),

        /**
         * Reserved 2 format.
         */
        @Deprecated
        Reserved2(0),

        /**
         * half-precision floating-point grayscale/luminance.
         * <p>
         * Requires {@link Caps#FloatTexture}.
         */
        Luminance16F(16,true),

        /**
         * single-precision floating-point grayscale/luminance.
         * <p>
         * Requires {@link Caps#FloatTexture}.
         */
        Luminance32F(32,true),

        /**
         * 8-bit luminance/grayscale and 8-bit alpha.
         */
        Luminance8Alpha8(16),

        /**
         * Reserved 3 format.
         */
        @Deprecated
        Reserved3(0),

        /**
         * half-precision floating-point grayscale/luminance and alpha.
         * <p>
         * Requires {@link Caps#FloatTexture}.
         */
        Luminance16FAlpha16F(32,true),

        /**
         * Reserved 4 format.
         */
        @Deprecated
        Reserved4(0),

        /**
         * Reserved 5 format.
         */
        @Deprecated
        Reserved5(0),

        /**
         * 8-bit blue, green, and red.
         */
        BGR8(24), // BGR and ABGR formats are often used on windows systems

        /**
         * 8-bit red, green, and blue.
         */
        RGB8(24),

        /**
         * Reserved 6 format.
         */
        @Deprecated
        Reserved6(0),

        /**
         * Reserved 7 format.
         */
        @Deprecated
        Reserved7(0),

        /**
         * 5-bit red, 6-bit green, and 5-bit blue.
         */
        RGB565(16),

        /**
         * Reserved 8 format.
         */
        @Deprecated
        Reserved8(0),

        /**
         * 5-bit red, green, and blue with 1-bit alpha.
         */
        RGB5A1(16),

        /**
         * 8-bit red, green, blue, and alpha.
         */
        RGBA8(32),

        /**
         * 8-bit alpha, blue, green, and red.
         */
        ABGR8(32),

        /**
         * 8-bit alpha, red, blue and green
         */
        ARGB8(32),

        /**
         * 8-bit blue, green, red and alpha.
         */
        BGRA8(32),

        /**
         * Reserved 9 format.
         */
        @Deprecated
        Reserved9(0),

        /**
         * S3TC compression DXT1.
         */
        DXT1(4,false,true, false),

        /**
         * S3TC compression DXT1 with 1-bit alpha.
         */
        DXT1A(4,false,true, false),

        /**
         * S3TC compression DXT3 with 4-bit alpha.
         */
        DXT3(8,false,true, false),

        /**
         * S3TC compression DXT5 with interpolated 8-bit alpha.
         */
        DXT5(8,false,true, false),

        /**
         * Luminance-Alpha Texture Compression.
         *
         * @deprecated Not supported by OpenGL 3.0.
         */
        @Deprecated
        Reserved10(0),

        /**
         * Arbitrary depth format. The precision is chosen by the video
         * hardware.
         */
        Depth(0,true,false,false),

        /**
         * 16-bit depth.
         */
        Depth16(16,true,false,false),

        /**
         * 24-bit depth.
         */
        Depth24(24,true,false,false),

        /**
         * 32-bit depth.
         */
        Depth32(32,true,false,false),

        /**
         * single-precision floating point depth.
         * <p>
         * Requires {@link Caps#FloatDepthBuffer}.
         */
        Depth32F(32,true,false,true),

        /**
         * Texture data is stored as {@link Format#RGB16F} in system memory,
         * but will be converted to {@link Format#RGB111110F} when sent
         * to the video hardware.
         * <p>
         * Requires {@link Caps#FloatTexture} and {@link Caps#PackedFloatTexture}.
         */
        RGB16F_to_RGB111110F(48,true),

        /**
         * unsigned floating-point red, green and blue that uses 32 bits.
         * <p>
         * Requires {@link Caps#PackedFloatTexture}.
         */
        RGB111110F(32,true),

        /**
         * Texture data is stored as {@link Format#RGB16F} in system memory,
         * but will be converted to {@link Format#RGB9E5} when sent
         * to the video hardware.
         * <p>
         * Requires {@link Caps#FloatTexture} and {@link Caps#SharedExponentTexture}.
         */
        RGB16F_to_RGB9E5(48,true),

        /**
         * 9-bit red, green and blue with 5-bit exponent.
         * <p>
         * Requires {@link Caps#SharedExponentTexture}.
         */
        RGB9E5(32,true),

        /**
         * half-precision floating point red, green, and blue.
         * <p>
         * Requires {@link Caps#FloatTexture}.
         */
        RGB16F(48,true),

        /**
         * half-precision floating point red, green, blue, and alpha.
         * <p>
         * Requires {@link Caps#FloatTexture}.
         */
        RGBA16F(64,true),

        /**
         * single-precision floating point red, green, and blue.
         * <p>
         * Requires {@link Caps#FloatTexture}.
         */
        RGB32F(96,true),

        /**
         * single-precision floating point red, green, blue and alpha.
         * <p>
         * Requires {@link Caps#FloatTexture}.
         */
        RGBA32F(128,true),

        /**
         * Reserved 11 format.
         */
        @Deprecated
        Reserved11(0),

        /**
         * 24-bit depth with 8-bit stencil.
         * Check the cap {@link Caps#PackedDepthStencilBuffer}.
         */
        Depth24Stencil8(32, true, false, false),

        /**
         * Reserved 12 format.
         */
        @Deprecated
        Reserved12(0),

        /**
         * Ericsson Texture Compression. Typically used on Android.
         * <p>
         * Requires {@link Caps#TextureCompressionETC1}.
         */
        ETC1(4, false, true, false),

        /**
         * R 8 i format.
         */
        R8I(8),
        /**
         * R 8 ui format.
         */
        R8UI(8),
        /**
         * R 16 i format.
         */
        R16I(16),
        /**
         * R 16 ui format.
         */
        R16UI(16),
        /**
         * R 32 i format.
         */
        R32I(32),
        /**
         * R 32 ui format.
         */
        R32UI(32),
        /**
         * Rg 8 i format.
         */
        RG8I(16),
        /**
         * Rg 8 ui format.
         */
        RG8UI(16),
        /**
         * Rg 16 i format.
         */
        RG16I(32),
        /**
         * Rg 16 ui format.
         */
        RG16UI(32),
        /**
         * Rg 32 i format.
         */
        RG32I(64),
        /**
         * Rg 32 ui format.
         */
        RG32UI(64),
        /**
         * Rgb 8 i format.
         */
        RGB8I(24),
        /**
         * Rgb 8 ui format.
         */
        RGB8UI(24),
        /**
         * Rgb 16 i format.
         */
        RGB16I(48),
        /**
         * Rgb 16 ui format.
         */
        RGB16UI(48),
        /**
         * Rgb 32 i format.
         */
        RGB32I(96),
        /**
         * Rgb 32 ui format.
         */
        RGB32UI(96),
        /**
         * Rgba 8 i format.
         */
        RGBA8I(32),
        /**
         * Rgba 8 ui format.
         */
        RGBA8UI(32),
        /**
         * Rgba 16 i format.
         */
        RGBA16I(64),
        /**
         * Rgba 16 ui format.
         */
        RGBA16UI(64),
        /**
         * Rgba 32 i format.
         */
        RGBA32I(128),
        /**
         * Rgba 32 ui format.
         */
        RGBA32UI(128)
        ;

        private int bpp;
        private boolean isDepth;
        private boolean isCompressed;
        private boolean isFloatingPoint;

        private Format(int bpp){
            this.bpp = bpp;
        }

        private Format(int bpp, boolean isFP){
            this(bpp);
            this.isFloatingPoint = isFP;
        }

        private Format(int bpp, boolean isDepth, boolean isCompressed, boolean isFP){
            this(bpp, isFP);
            this.isDepth = isDepth;
            this.isCompressed = isCompressed;
        }

        /**
         * Get bits per pixel int.
         *
         * @return bits per pixel.
         */
        public int getBitsPerPixel(){
            return bpp;
        }

        /**
         * Is depth format boolean.
         *
         * @return True if this format is a depth format, false otherwise.
         */
        public boolean isDepthFormat(){
            return isDepth;
        }

        /**
         * Is depth stencil format boolean.
         *
         * @return True if this format is a depth + stencil (packed) format, false otherwise.
         */
        boolean isDepthStencilFormat() {
            return this == Depth24Stencil8;
        }

        /**
         * Is compressed boolean.
         *
         * @return True if this is a compressed image format, false if uncompressed.
         */
        public boolean isCompressed() {
            return isCompressed;
        }

        /**
         * Is floating pont boolean.
         *
         * @return True if this image format is in floating point, false if it is an integer format.
         */
        public boolean isFloatingPont(){
            return isFloatingPoint;
        }



    }

    /**
     * The Format.
     */
    // image attributes
    protected Format format;
    /**
     * The Width.
     */
    protected int width, /**
     * The Height.
     */
    height, /**
     * The Depth.
     */
    depth;
    /**
     * The Mip map sizes.
     */
    protected int[] mipMapSizes;
    /**
     * The Data.
     */
    protected ArrayList<ByteBuffer> data;
    /**
     * The Multi samples.
     */
    protected int multiSamples = 1;
    /**
     * The Color space.
     */
    protected ColorSpace colorSpace = null;
    //    protected int mipOffset = 0;

    /**
     * The Mips were generated.
     */
    // attributes relating to GL object
    protected boolean mipsWereGenerated = false;
    /**
     * The Need generated mips.
     */
    protected boolean needGeneratedMips = false;
    /**
     * The Last texture state.
     */
    protected LastTextureState lastTextureState = new LastTextureState();

    /**
     * Internal use only.
     * The renderer stores the texture state set from the last texture
     * so it doesn't have to change it unless necessary.
     *
     * @return The image parameter state.
     */
    public LastTextureState getLastTextureState() {
        return lastTextureState;
    }

    /**
     * Internal use only.
     * The renderer marks which images have generated mipmaps in VRAM
     * and which do not, so it can generate them as needed.
     *
     * @param generated If mipmaps were generated or not.
     */
    public void setMipmapsGenerated(boolean generated) {
        this.mipsWereGenerated = generated;
    }

    /**
     * Internal use only.
     * Check if the renderer has generated mipmaps for this image in VRAM
     * or not.
     *
     * @return If mipmaps were generated already.
     */
    public boolean isMipmapsGenerated() {
        return mipsWereGenerated;
    }

    /**
     * (Package private) Called by {@link Texture} when
     * {@link #isMipmapsGenerated() } is false in order to generate
     * mipmaps for this image.
     */
    void setNeedGeneratedMipmaps() {
        needGeneratedMips = true;
    }

    /**
     * Is generated mipmaps required boolean.
     *
     * @return True if the image needs to have mipmaps generated for it (as requested by the texture). This stays true even after mipmaps have been generated.
     */
    public boolean isGeneratedMipmapsRequired() {
        return needGeneratedMips;
    }

    /**
     * Sets the update needed flag, while also checking if mipmaps
     * need to be regenerated.
     */
    @Override
    public void setUpdateNeeded() {
        super.setUpdateNeeded();
        if (isGeneratedMipmapsRequired() && !hasMipmaps()) {
            // Mipmaps are no longer valid, since the image was changed.
            setMipmapsGenerated(false);
        }
    }

    /**
     * Determine if the image is NPOT.
     *
     * @return if the image is a non-power-of-2 image, e.g. having dimensions that are not powers of 2.
     */
    public boolean isNPOT() {
        return width != 0 && height != 0
                && (!FastMath.isPowerOfTwo(width) || !FastMath.isPowerOfTwo(height));
    }

    @Override
    public void resetObject() {
        this.id = -1;
        this.mipsWereGenerated = false;
        this.lastTextureState.reset();
        setUpdateNeeded();
    }

    @Override
    protected void deleteNativeBuffers() {
        for (ByteBuffer buf : data) {
            BufferUtils.destroyDirectBuffer(buf);
        }
    }

    @Override
    public void deleteObject(Object rendererObject) {
        ((Renderer)rendererObject).deleteImage(this);
    }

    @Override
    public NativeObject createDestructableClone() {
        return new Image(id);
    }

    @Override
    public long getUniqueId() {
        return ((long)OBJTYPE_TEXTURE << 32) | ((long)id);
    }

    /**
     * @return A shallow clone of this image. The data is not cloned.
     */
    @Override
    public Image clone(){
        Image clone = (Image) super.clone();
        clone.mipMapSizes = mipMapSizes != null ? mipMapSizes.clone() : null;
        clone.data = data != null ? new ArrayList<ByteBuffer>(data) : null;
        clone.lastTextureState = new LastTextureState();
        clone.setUpdateNeeded();
        return clone;
    }

    /**
     * Constructor instantiates a new <code>Image</code> object. All values
     * are undefined.
     */
    public Image() {
        super();
        data = new ArrayList<ByteBuffer>(1);
    }

    /**
     * Instantiates a new Image.
     *
     * @param id the id
     */
    protected Image(int id){
        super(id);
    }

    /**
     * Constructor instantiates a new <code>Image</code> object. The
     * attributes of the image are defined during construction.
     *
     * @param format      the data format of the image.
     * @param width       the width of the image.
     * @param height      the height of the image.
     * @param depth       the depth
     * @param data        the image data.
     * @param mipMapSizes the array of mipmap sizes, or null for no mipmaps.
     * @param colorSpace  the color space
     * @see ColorSpacethe colorSpace of the image
     */
    public Image(Format format, int width, int height, int depth, ArrayList<ByteBuffer> data,
                 int[] mipMapSizes, ColorSpace colorSpace) {

        this();

        if (mipMapSizes != null) {
            if (mipMapSizes.length <= 1) {
                mipMapSizes = null;
            } else {
                needGeneratedMips = false;
                mipsWereGenerated = true;
            }
        }

        setFormat(format);
        this.width = width;
        this.height = height;
        this.data = data;
        this.depth = depth;
        this.mipMapSizes = mipMapSizes;
        this.colorSpace = colorSpace;
    }

    /**
     * Instantiates a new Image.
     *
     * @param format      the format
     * @param width       the width
     * @param height      the height
     * @param depth       the depth
     * @param data        the data
     * @param mipMapSizes the mip map sizes
     * @see {@link #Image(com.jme3.texture.Image.Format, int, int, int, java.util.ArrayList, int[], boolean)}
     * @deprecated use {@link #Image(com.jme3.texture.Image.Format, int, int, int, java.util.ArrayList, int[], boolean)}
     */
    @Deprecated
    public Image(Format format, int width, int height, int depth, ArrayList<ByteBuffer> data,
                 int[] mipMapSizes) {
        this(format, width, height, depth, data, mipMapSizes, ColorSpace.Linear);
    }

    /**
     * Constructor instantiates a new <code>Image</code> object. The
     * attributes of the image are defined during construction.
     *
     * @param format      the data format of the image.
     * @param width       the width of the image.
     * @param height      the height of the image.
     * @param data        the image data.
     * @param mipMapSizes the array of mipmap sizes, or null for no mipmaps.
     * @param colorSpace  the color space
     * @see ColorSpacethe colorSpace of the image
     */
    public Image(Format format, int width, int height, ByteBuffer data,
                 int[] mipMapSizes, ColorSpace colorSpace) {

        this();

        if (mipMapSizes != null && mipMapSizes.length <= 1) {
            mipMapSizes = null;
        } else {
            needGeneratedMips = false;
            mipsWereGenerated = true;
        }

        setFormat(format);
        this.width = width;
        this.height = height;
        if (data != null){
            this.data = new ArrayList<ByteBuffer>(1);
            this.data.add(data);
        }
        this.mipMapSizes = mipMapSizes;
        this.colorSpace = colorSpace;
    }

    /**
     * Instantiates a new Image.
     *
     * @param format      the format
     * @param width       the width
     * @param height      the height
     * @param data        the data
     * @param mipMapSizes the mip map sizes
     * @see {@link #Image(com.jme3.texture.Image.Format, int, int, java.nio.ByteBuffer, int[], boolean)}
     * @deprecated use {@link #Image(com.jme3.texture.Image.Format, int, int, java.nio.ByteBuffer, int[], boolean)}
     */
    @Deprecated
    public Image(Format format, int width, int height, ByteBuffer data,
                 int[] mipMapSizes) {
        this(format, width, height, data, mipMapSizes, ColorSpace.Linear);
    }

    /**
     * Constructor instantiates a new <code>Image</code> object. The
     * attributes of the image are defined during construction.
     *
     * @param format     the data format of the image.
     * @param width      the width of the image.
     * @param height     the height of the image.
     * @param depth      the depth
     * @param data       the image data.
     * @param colorSpace the color space
     * @see ColorSpacethe colorSpace of the image
     */
    public Image(Format format, int width, int height, int depth, ArrayList<ByteBuffer> data, ColorSpace colorSpace) {
        this(format, width, height, depth, data, null, colorSpace);
    }

    /**
     * Instantiates a new Image.
     *
     * @param format the format
     * @param width  the width
     * @param height the height
     * @param depth  the depth
     * @param data   the data
     * @see {@link #Image(com.jme3.texture.Image.Format, int, int, int, java.util.ArrayList, boolean)}
     * @deprecated use {@link #Image(com.jme3.texture.Image.Format, int, int, int, java.util.ArrayList, boolean)}
     */
    @Deprecated
    public Image(Format format, int width, int height, int depth, ArrayList<ByteBuffer> data) {
        this(format, width, height, depth, data, ColorSpace.Linear);
    }

    /**
     * Constructor instantiates a new <code>Image</code> object. The
     * attributes of the image are defined during construction.
     *
     * @param format     the data format of the image.
     * @param width      the width of the image.
     * @param height     the height of the image.
     * @param data       the image data.
     * @param colorSpace the color space
     * @see ColorSpacethe colorSpace of the image
     */
    public Image(Format format, int width, int height, ByteBuffer data, ColorSpace colorSpace) {
        this(format, width, height, data, null, colorSpace);
    }


    /**
     * Instantiates a new Image.
     *
     * @param format the format
     * @param width  the width
     * @param height the height
     * @param data   the data
     * @see {@link #Image(com.jme3.texture.Image.Format, int, int, java.nio.ByteBuffer, boolean)}
     * @deprecated use {@link #Image(com.jme3.texture.Image.Format, int, int, java.nio.ByteBuffer, boolean)}
     */
    @Deprecated
    public Image(Format format, int width, int height, ByteBuffer data) {
        this(format, width, height, data, null, ColorSpace.Linear);
    }


    /**
     * Gets multi samples.
     *
     * @return The number of samples (for multisampled textures).
     * @see Image#setMultiSamples(int) Image#setMultiSamples(int)
     */
    public int getMultiSamples() {
        return multiSamples;
    }

    /**
     * Sets multi samples.
     *
     * @param multiSamples Set the number of samples to use for this image, setting this to a value higher than 1 turns this image/texture into a multisample texture (on OpenGL3.1 and higher).
     */
    public void setMultiSamples(int multiSamples) {
        if (multiSamples <= 0)
            throw new IllegalArgumentException("multiSamples must be > 0");

        if (getData(0) != null)
            throw new IllegalArgumentException("Cannot upload data as multisample texture");

        if (hasMipmaps())
            throw new IllegalArgumentException("Multisample textures do not support mipmaps");

        this.multiSamples = multiSamples;
    }

    /**
     * <code>setData</code> sets the data that makes up the image. This data
     * is packed into an array of <code>ByteBuffer</code> objects.
     *
     * @param data the data that contains the image information.
     */
    public void setData(ArrayList<ByteBuffer> data) {
        this.data = data;
        setUpdateNeeded();
    }

    /**
     * <code>setData</code> sets the data that makes up the image. This data
     * is packed into a single <code>ByteBuffer</code>.
     *
     * @param data the data that contains the image information.
     */
    public void setData(ByteBuffer data) {
        this.data = new ArrayList<ByteBuffer>(1);
        this.data.add(data);
        setUpdateNeeded();
    }

    /**
     * Add data.
     *
     * @param data the data
     */
    public void addData(ByteBuffer data) {
        if (this.data == null)
            this.data = new ArrayList<ByteBuffer>(1);
        this.data.add(data);
        setUpdateNeeded();
    }

    /**
     * Sets data.
     *
     * @param index the index
     * @param data  the data
     */
    public void setData(int index, ByteBuffer data) {
        if (index >= 0) {
            while (this.data.size() <= index) {
                this.data.add(null);
            }
            this.data.set(index, data);
            setUpdateNeeded();
        } else {
            throw new IllegalArgumentException("index must be greater than or equal to 0.");
        }
    }

    /**
     * Set efficent data.
     *
     * @param efficientData the efficient data
     * @deprecated This feature is no longer used by the engine
     */
    @Deprecated
    public void setEfficentData(Object efficientData){
    }

    /**
     * Get efficent data object.
     *
     * @return the object
     * @deprecated This feature is no longer used by the engine
     */
    @Deprecated
    public Object getEfficentData(){
        return null;
    }

    /**
     * Sets the mipmap sizes stored in this image's data buffer. Mipmaps are
     * stored sequentially, and the first mipmap is the main image data. To
     * specify no mipmaps, pass null and this will automatically be expanded
     * into a single mipmap of the full
     *
     * @param mipMapSizes the mipmap sizes array, or null for a single image map.
     */
    public void setMipMapSizes(int[] mipMapSizes) {
        if (mipMapSizes != null && mipMapSizes.length <= 1)
            mipMapSizes = null;

        this.mipMapSizes = mipMapSizes;

        if (mipMapSizes != null) {
            needGeneratedMips = false;
            mipsWereGenerated = false;
        } else {
            needGeneratedMips = true;
            mipsWereGenerated = false;
        }

        setUpdateNeeded();
    }

    /**
     * <code>setHeight</code> sets the height value of the image. It is
     * typically a good idea to try to keep this as a multiple of 2.
     *
     * @param height the height of the image.
     */
    public void setHeight(int height) {
        this.height = height;
        setUpdateNeeded();
    }

    /**
     * <code>setDepth</code> sets the depth value of the image. It is
     * typically a good idea to try to keep this as a multiple of 2. This is
     * used for 3d images.
     *
     * @param depth the depth of the image.
     */
    public void setDepth(int depth) {
        this.depth = depth;
        setUpdateNeeded();
    }

    /**
     * <code>setWidth</code> sets the width value of the image. It is
     * typically a good idea to try to keep this as a multiple of 2.
     *
     * @param width the width of the image.
     */
    public void setWidth(int width) {
        this.width = width;
        setUpdateNeeded();
    }

    /**
     * <code>setFormat</code> sets the image format for this image.
     *
     * @param format the image format.
     * @throws NullPointerException if format is null
     * @see Format
     */
    public void setFormat(Format format) {
        if (format == null) {
            throw new NullPointerException("format may not be null.");
        }

        this.format = format;
        setUpdateNeeded();
    }

    /**
     * <code>getFormat</code> returns the image format for this image.
     *
     * @return the image format.
     * @see Format
     */
    public Format getFormat() {
        return format;
    }

    /**
     * <code>getWidth</code> returns the width of this image.
     *
     * @return the width of this image.
     */
    public int getWidth() {
        return width;
    }

    /**
     * <code>getHeight</code> returns the height of this image.
     *
     * @return the height of this image.
     */
    public int getHeight() {
        return height;
    }

    /**
     * <code>getDepth</code> returns the depth of this image (for 3d images).
     *
     * @return the depth of this image.
     */
    public int getDepth() {
        return depth;
    }

    /**
     * <code>getData</code> returns the data for this image. If the data is
     * undefined, null will be returned.
     *
     * @return the data for this image.
     */
    public List<ByteBuffer> getData() {
        return data;
    }

    /**
     * <code>getData</code> returns the data for this image. If the data is
     * undefined, null will be returned.
     *
     * @param index the index
     * @return the data for this image.
     */
    public ByteBuffer getData(int index) {
        if (data.size() > index)
            return data.get(index);
        else
            return null;
    }

    /**
     * Returns whether the image data contains mipmaps.
     *
     * @return true if the image data contains mipmaps, false if not.
     */
    public boolean hasMipmaps() {
        return mipMapSizes != null;
    }

    /**
     * Returns the mipmap sizes for this image.
     *
     * @return the mipmap sizes for this image.
     */
    public int[] getMipMapSizes() {
        return mipMapSizes;
    }

    /**
     * image loader is responsible for setting this attribute based on the color
     * space in which the image has been encoded with. In the majority of cases,
     * this flag will be set to sRGB by default since many image formats do not
     * contain any color space information and the most frequently used colors
     * space is sRGB
     * <p>
     * The material loader may override this attribute to Lineat if it determines that
     * such conversion must not be performed, for example, when loading normal
     * maps.
     *
     * @param colorSpace @see ColorSpace. Set to sRGB to enable srgb -&gt; linear conversion, Linear otherwise.
     * @seealso Renderer#setLinearizeSrgbImages(boolean)
     */
    public void setColorSpace(ColorSpace colorSpace) {
        this.colorSpace = colorSpace;
    }

    /**
     * Specifies that this image is an SRGB image and therefore must undergo an
     * sRGB -&gt; linear RGB color conversion prior to being read by a shader and
     * with the {@link Renderer#setLinearizeSrgbImages(boolean)} option is
     * enabled.
     * <p>
     * This option is only supported for the 8-bit color and grayscale image
     * formats. Determines if the image is in SRGB color space or not.
     *
     * @return True, if the image is an SRGB image, false if it is linear RGB.
     * @seealso Renderer#setLinearizeSrgbImages(boolean)
     */
    public ColorSpace getColorSpace() {
        return colorSpace;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append("[size=").append(width).append("x").append(height);

        if (depth > 1)
            sb.append("x").append(depth);

        sb.append(", format=").append(format.name());

        if (hasMipmaps())
            sb.append(", mips");

        if (getId() >= 0)
            sb.append(", id=").append(id);

        sb.append("]");

        return sb.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Image)) {
            return false;
        }
        Image that = (Image) other;
        if (this.getFormat() != that.getFormat())
            return false;
        if (this.getWidth() != that.getWidth())
            return false;
        if (this.getHeight() != that.getHeight())
            return false;
        if (this.getData() != null && !this.getData().equals(that.getData()))
            return false;
        if (this.getData() == null && that.getData() != null)
            return false;
        if (this.getMipMapSizes() != null
                && !Arrays.equals(this.getMipMapSizes(), that.getMipMapSizes()))
            return false;
        if (this.getMipMapSizes() == null && that.getMipMapSizes() != null)
            return false;
        if (this.getMultiSamples() != that.getMultiSamples())
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.format != null ? this.format.hashCode() : 0);
        hash = 97 * hash + this.width;
        hash = 97 * hash + this.height;
        hash = 97 * hash + this.depth;
        hash = 97 * hash + Arrays.hashCode(this.mipMapSizes);
        hash = 97 * hash + (this.data != null ? this.data.hashCode() : 0);
        hash = 97 * hash + this.multiSamples;
        return hash;
    }

    public void write(JmeExporter e) throws IOException {
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(format, "format", Format.RGBA8);
        capsule.write(width, "width", 0);
        capsule.write(height, "height", 0);
        capsule.write(depth, "depth", 0);
        capsule.write(mipMapSizes, "mipMapSizes", null);
        capsule.write(multiSamples, "multiSamples", 1);
        capsule.writeByteBufferArrayList(data, "data", null);
        capsule.write(colorSpace, "colorSpace", null);
    }

    public void read(JmeImporter e) throws IOException {
        InputCapsule capsule = e.getCapsule(this);
        format = capsule.readEnum("format", Format.class, Format.RGBA8);
        width = capsule.readInt("width", 0);
        height = capsule.readInt("height", 0);
        depth = capsule.readInt("depth", 0);
        mipMapSizes = capsule.readIntArray("mipMapSizes", null);
        multiSamples = capsule.readInt("multiSamples", 1);
        data = (ArrayList<ByteBuffer>) capsule.readByteBufferArrayList("data", null);
        colorSpace = capsule.readEnum("colorSpace", ColorSpace.class, null);

        if (mipMapSizes != null) {
            needGeneratedMips = false;
            mipsWereGenerated = true;
        }
    }

    private int changes;

    /**
     * Increment change.
     */
    public void incrementChange() {
        changes++;
    }

    /**
     * Decrement changes.
     */
    public void decrementChanges() {
        changes--;
    }

    /**
     * Is changed boolean.
     *
     * @return the boolean
     */
    public boolean isChanged() {
        return changes != 0;
    }

    /**
     * Clear changes.
     */
    public void clearChanges() {
        changes = 0;
    }
}
