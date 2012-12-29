/******************************************************************
 * Copyright (C) 2002-2012 TinyLine. All rights reserved.         *
 * ---------------------------------------------------------------*
 * This software is published under the terms of the TinyLine     *
 * License, a copy of which has been included with this           *
 * distribution.                                                  *
 *                                                                *
 * For more information on the TinyLine,                          *
 * please see <http://www.tinyline.com/>.                         *
 *****************************************************************/

package com.tinyline.app;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.*;
import java.net.*;

import com.tinyline.svg.*;
import com.tinyline.tiny2d.*;
import com.tinyline.util.*;

/**
 * This class implements TinyLine SVG Tiny View
 * @version 2.2
 */

public class SVGView extends View implements 
ImageConsumer, ImageLoader
{

    /** The SVG renderer */
    public SVGRaster raster;

    /** The Tiny2DProducer implementation */
    Tiny2DProducer imageProducer;

    /** The base URL */
    URL      baseURL;
    /* The image cash */
    TinyHash imageCash;
    /* The current SVG document URL */
    String  currentURL = "";
    /* The current loading status */
    boolean load = true;

    int width, height;

    Context context;
    Canvas c;

    public SVGView(Context context) {
        super(context);
        init(context);
    }

    public SVGView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context)
    {
        
//        setFocusable(true);
        this.context = context;

        width = 20; 
        height = 20;  

        int lickey[]= { 0x3b515516, 0xf6c68f17, 0x59f2e902, 0x3fd13dd4, 0x33e55fcf,  };

        Tiny2D.init(lickey);

        // Creates the pixels buffer (width X height)
        TinyBuffer buffer = new TinyBuffer();
        buffer.width = width;
        buffer.height = height;
        buffer.pixels32 = new int[width * height];

        // Creates the SVGRaster graphics
        raster = new SVGRaster(buffer);
        Tiny2D t2d = raster.getTiny2D();
        // Creates the TinyProducer and links it to the Tiny2D
        imageProducer = new Tiny2DProducer(t2d);
        imageProducer.setConsumer(this);
        t2d.setProducer(imageProducer);

        // Makes the image cash
        imageCash = new TinyHash(11);

        // Sets the ImageLoader implementation needed for
        // loading bitmaps
        SVGImageElem.setImageLoader(this);
    }


    /**
     * Loads and dispalys an SVGT document from the given URL.
     */
    public void goURL(String url)
    {
        currentURL = url;
        SVGDocument document = loadSVG(currentURL);
        document.idTable.clear();
        document.addIds(document.root);
        // 1. Set the SVGT document to be drawn
        raster.setSVGDocument(document);
        // Fire the update event
        SVGSVGElem root = (SVGSVGElem) raster.root;
        Tiny2D t2d = raster.getTiny2D();
        root.createOutline();
        t2d.invalidate();
        raster.update();
        t2d.sendPixels();
    }

    /**
     *  Returns the current SVGT document to its original view.
     */
    public void origView()
    {
        SVGSVGElem root = (SVGSVGElem) raster.root;
        TinyMatrix xform = (TinyMatrix) root.cameraXform.data[1];
        xform.reset();
        xform = (TinyMatrix) root.cameraXform.data[0];
        xform.reset();

        // Fire the update event
        Tiny2D t2d = raster.getTiny2D();
        root.createOutline();
        t2d.invalidate();
        raster.update();
        t2d.sendPixels();
    }

    public boolean zoom(int direction)
    {
        SVGSVGElem root = (SVGSVGElem) raster.root;
        TinyVector tv = new TinyVector(3); 
        Tiny2D t2d = raster.getTiny2D();
        TinyBuffer target = t2d.getTarget();
        TinyMatrix xform = (TinyMatrix) root.cameraXform.data[1];
        TinyMatrix t;
        int x = (target.width / 2)<<8;
        int y = (target.height / 2)<<8;

        int scale = (xform.a);

        // zoom in '0' size / 2
        if (direction == 0)
        {
            scale >>= 1;
            if (scale < 1 << 8)
            {
                scale = (1 << 8);
            }
        }
        else //zoom out size * 2
        {
            scale <<= 1;
            if (scale > 1 << 24)
            {
                scale = (1 << 24);
            }
        }

        t = new TinyMatrix();
        t.translate(x, y);
        tv.addElement(t);
        t = new TinyMatrix();
        t.scale(scale, scale);
        tv.addElement(t);
        t = new TinyMatrix();
        t.translate(-x, -y);
        tv.addElement(t);
        xform.copy(Tiny2D.getTinyMatrix(tv));

        root.createOutline();
        t2d.invalidate();
        raster.update();
        t2d.sendPixels();
        return true;
    }
    
    public void pan(int x, int y)
    {
        SVGSVGElem root = (SVGSVGElem) raster.root;
        TinyMatrix xform = (TinyMatrix)root.cameraXform.data[1];
        // Scale pan distances according to the current
        // scale factor. Change the current viewport.
        xform = (TinyMatrix)root.cameraXform.data[0];
        xform.tx -= x << Tiny2D.FIX_BITS;
        xform.ty -= y << Tiny2D.FIX_BITS;
        root.createOutline();

        // Fire the update event
        Tiny2D t2d = raster.getTiny2D();
        root.createOutline();
        t2d.invalidate();
        raster.update();
        t2d.sendPixels();
    }
        
    /**
     * Delivers the pixels of the image. The pixel (px,py) is
     * stored in the pixels array at index (px * scansize + py + off).
     * @param x,&nbsp;y the coordinates of the upper-left corner of the
     *        area of pixels to be set
     * @param w the width of the area of pixels
     * @param h the height of the area of pixels
     * @see  ImageConsumer
     */
    public void newPixels(int x, int y, int w, int h)
    {
        postInvalidate(x, y, x + w, y + h);
    }

    public InputStream getResAsStream(String name) throws IOException
    {
        InputStream is = getClass().getResourceAsStream(name);
        return is;  
    }

    
    /**
     * Fetch the Bitmap. If the name begins with "http:" fetch it with
     * url.openConnection(). Otherwise load it as a Java resource.
     * 
     * @param name
     *            of the image to load
     * @return image created
     * @exception IOException
     *                if errors occur during loading
     */
    private Bitmap createImage(String name) throws IOException
    {
        Bitmap rawImage;
        int w, h;
        int pixels[];
        Bitmap image = null;
        InputStream is = null;
        BufferedInputStream bis = null;

        try
        {
            /* Open the input stream */
            if (name.startsWith("http:")|| name.startsWith("file:") || name.startsWith("..") )
            {
                // Open a new URL and get the InputStream to load data from it.
                URLConnection c = null;
                URL url = new URL(name);
                c = url.openConnection();
                c.connect();
                is = c.getInputStream();
            } else
            {
                // Load as a resource
                is = getClass().getResourceAsStream(name);
            }

            /* Use buffered stream for a performance plus. */
            bis = new BufferedInputStream(is, 4096);
            /* Decode the stream data to a bitmap. */
            rawImage = BitmapFactory.decodeStream(bis);
            /*
             * Create a deep copy using getPixels() and the default
             * Bitmap.Config.ARGB_8888
             */
            w = rawImage.getWidth();
            h = rawImage.getHeight();
            pixels = new int[w * h];
            rawImage.getPixels(pixels, 0, w, 0, 0, w, h);
            image = Bitmap.createBitmap(pixels, 0, w, w, h,
                    Bitmap.Config.ARGB_8888);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        } finally
        {
            if (bis != null)
                bis.close();
            if (is != null)
                is.close();
        }
        return image;
    }

    /**
     * Returns a TinyBuffer for the given image URL or path.
     * @param imgRef
     *            The image URL or path.
     * @return a TinyBuffer object which gets its pixel data from the specified
     *         URL or path.
     * @ see ImageLoader Interface
     */
    public TinyBuffer createTinyBuffer(TinyString uri)
    {
        String imgRef = new String(uri.data);
        TinyBuffer buffer = null;
        try
        {
            // check in the cash
            URL url = new URL(baseURL,imgRef);
            imgRef = url.toExternalForm();

            buffer = (TinyBuffer) imageCash.get(imgRef);
            // not found
            if (buffer == null)
            {
                Bitmap image = createImage(imgRef);
                buffer = new TinyBuffer();
                buffer.width = image.getWidth();
                buffer.height = image.getHeight();

                // Grap bits
                buffer.pixels32 = new int[buffer.width * buffer.height];
                image.getPixels(buffer.pixels32, 0, buffer.width, 0, 0,
                        buffer.width, buffer.height);
                imageCash.put(imgRef, buffer);
            }
        } catch (Exception ex)
        {
            ex.printStackTrace();    
        }
        return buffer;
    }
    /**
     * Loads <tt> TinyBuffer </tt> raster image.
     * @param imageData     The input image data buffer.
     * @param imageOffset   The input image data buffer pointer.
     * @param imageLength   The input image data buffer length.
     * @return The raster image.
     * @ see ImageLoader Interface
     */
    public TinyBuffer createTinyBuffer(byte[] imageData, int imageOffset, int imageLength)
    {
        Bitmap rawImage;
        int w, h;
        int pixels[];
        Bitmap image = null;
        InputStream is = null;
        TinyBuffer buffer = null;
        try
        {
            /* Use the stream. */
            is = new ByteArrayInputStream(imageData,imageOffset, imageLength);
            /* Decode the stream data to a bitmap. */
            rawImage = BitmapFactory.decodeStream(is);
            /*
             * Create a deep copy using getPixels() and the default
             * Bitmap.Config.ARGB_8888
             */
            w = rawImage.getWidth();
            h = rawImage.getHeight();
            pixels = new int[w * h];
            rawImage.getPixels(pixels, 0, w, 0, 0, w, h);
            image = Bitmap.createBitmap(pixels, 0, w, w, h,
                    Bitmap.Config.ARGB_8888);

            buffer = new TinyBuffer();
            
            buffer.width = image.getWidth();
            buffer.height = image.getHeight();

            // Grap bits
            buffer.pixels32 = new int[buffer.width * buffer.height];
            image.getPixels(buffer.pixels32, 0, buffer.width, 0, 0,
                    buffer.width, buffer.height);
            is.close();
        }
        catch (Throwable thr)
        {
            thr.printStackTrace();
        } 
        return buffer;
    }
    
    protected void onDraw(Canvas canvas)
    {
        doDraw(canvas);
    }
    
    private void doDraw(Canvas canvas)
    {
        Tiny2D t2d = raster.getTiny2D();

        TinyBuffer pixbuf = t2d.getTarget();
 
        // True if pixbuf has an alpha channel, false if all pixels are fully
        // opaque
//        boolean processAlpha = ((state.bg & 0xff000000) != 0xff000000);

        boolean processAlpha =  false;
        
        /* For SVGView is should draw the whole bitmap because we
         * do not control the refresh loop     
         */
        // draw the color array directly, w/o creating a bitmap object
        canvas.drawBitmap(pixbuf.pixels32, 0, pixbuf.width, 
                0, 
                0, 
                pixbuf.width,
                pixbuf.height,
                processAlpha, null);
    }


    /* Callback invoked when the view dimensions change. */
    protected void onSizeChanged (int w, int h, int oldw, int oldh)
    {
        setSurfaceSize(w, h);
    }
    
    /* Callback invoked when the surface dimensions change. */
    public void setSurfaceSize(int width, int height) {
        
        if( (this.width == width) && (this.height == height))
        {
            return ;
        }
        synchronized (this) 
        {
            this.width = width;
            this.height = height;

            // Creates the new target's pixels buffer (width X height)
            TinyBuffer buffer = new TinyBuffer();
            buffer.width = width;
            buffer.height = height;
            buffer.pixels32 = new int[width * height];

            // Creates the new SVGRaster graphics
            raster = new SVGRaster(buffer);
            Tiny2D t2d = raster.getTiny2D();
            // Creates the TinyProducer and links it to the Tiny2D
            imageProducer = new Tiny2DProducer(t2d);
            // Creates the TinyProducer and links it to the Tiny2D
            imageProducer = new Tiny2DProducer(t2d);
            imageProducer.setConsumer(this);
            t2d.setProducer(imageProducer);
            if(!currentURL.equals(""))
            {    
                goURL(currentURL);
            }    
            else
            {    
                raster.root.createOutline();
                t2d.invalidate();
                // Update pixels under the current clip.
                raster.update();
                // Notify TinyProducer about new pixels.
                t2d.sendPixels();
            }
        }
    }
    
    
    /**
     * Loads a TinyFont object from the given URL.
     * @param name  The TinyFont URL or path.
     * @return     A  TinyFont object.
     */
    public TinyFont loadFont(String name)
    {
        TinyFont f = null;
        InputStream is = null;
        TinyInputStream tis = null;
        try
        {
            /* Open the input stream */
            if (name.startsWith("http:"))
            {
                // Open a new URL and get the InputStream to load data from it.
                URLConnection c = null;
                URL url = new URL(name);
                c = url.openConnection();
                c.connect();
                is = c.getInputStream();
            } else
            {
                // Load as a resource
                is = getClass().getResourceAsStream(name);
            }
            // Reads and parses the stream
            tis =  new TinyInputStream(is);
            f = tis.readTinyFont();
            if (tis != null)
                tis.close();
            if (is != null)
                is.close();
        } 
        catch (Exception ex)
        {
            ex.printStackTrace();
        } 
        return f;
    }
    /**
     * Loads an SVG document.
     * @param url The SVG document URL.
     * @return The loaded document.
     */
    public SVGDocument loadSVG(String name)
    {
        System.out.println(""+name);
        load = true;
        SVGDocument doc = raster.createSVGDocument();
        InputStream is = null;
        Runtime.getRuntime().gc();
        try
        {
            /* Open the input stream */
            if (name.startsWith("http:")|| name.startsWith("file:") || name.startsWith("..") )
            {
                // Open a new URL and get the InputStream to load data from it.
                URLConnection c = null;
                URL url = new URL(baseURL,name);
                baseURL = url;
                c = url.openConnection();
                c.connect();
                is = c.getInputStream();
            } else
            {
                // Load as a resource
               is = getClass().getResourceAsStream(name);
            }
            // GZIPInputStream is buffered already, so use buffered stream for others
            if(name.endsWith("svgz"))
            {
                is = new GZIPInputStream(is);
            }
            else
            {
                is = new BufferedInputStream(is);
            }
            // Read and parse the SVGT stream
            TinyBuffer pixbuf = raster.getTiny2D().getTarget();
            // Create the SVGT attributes parser
            SVGAttr attrParser = new SVGAttr(pixbuf.width, pixbuf.height);
            // Create the SVGT stream parser
            SVGParser parser = new SVGParser(attrParser);
            // Parse the input SVGT stream parser into the document
            parser.load(doc,is);
            load = true;
        }
        catch( IOException ioe)
        {
            doc = null;
            alertError(ioe.getMessage() );
        }
        catch(OutOfMemoryError memerror)
        {
            doc = null;
            alertError("Not enought memory");
            Runtime.getRuntime().gc();
        }
        catch( Throwable thr)
        {
            doc = null;
            alertError("Not in SVGT format");
        }
        finally
        {
            try
            {
               if (is != null) is.close();
            }
            catch( IOException ioe) {}
        }
        load = false;
        return doc;
    }


    
    /**
     * Display error message.
     * 
     * @param message
     *            the error message
     */
    void alertError(String message)
    {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
/*EOF*/