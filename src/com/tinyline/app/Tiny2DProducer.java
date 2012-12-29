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

import com.tinyline.tiny2d.*;

/**
 * The <tt>Tiny2DProducer</tt> implementation of the TinyProducer interface.
 * @version 2.1
 */

public class Tiny2DProducer implements TinyProducer
{
    /** The ImageConsumer associated with this TinyProducer */
    private ImageConsumer theConsumer;
    /* The Tiny2D graphics */
    private Tiny2D  g;

    /** The Tiny2DProducer implementation */
    Tiny2DProducer imageProducer;

    /**
    * Constructs a new <tt>MIDPTiny2DProducer</tt>.
    */
    public Tiny2DProducer(Tiny2D  g)
    {
        this.g  = g;
    }

    /**
     * Sets the ImageConsumer for this renderer
     * @param imageconsumer the specified <code>ImageConsumer</code>
     */
    public void setConsumer(ImageConsumer consumer)
    {
        theConsumer = consumer;
    }

    /**
     * Returns true if this renderer has a consumer; otherwise
     * returns false
     */
    public boolean hasConsumer()
    {
        return (theConsumer != null);
    }

    /**
     * Sends pixel data to the consumer.
     */
    public void sendPixels()
    {
        TinyRect clipRect = g.getState().devClip;
        theConsumer.newPixels(clipRect.xmin, clipRect.ymin,
          clipRect.xmax - clipRect.xmin, clipRect.ymax - clipRect.ymin);
    }

    /**
     * Notifies the consumer.
     */
    public void  imageComplete()
    {
    }
}
