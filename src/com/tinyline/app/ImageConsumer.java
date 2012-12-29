/******************************************************************
 * Copyright (C) 2012 TinyLine. All rights reserved.              *
 * ---------------------------------------------------------------*
 * This software is published under the terms of the TinyLine     *
 * License, a copy of which has been included with this           *
 * distribution.                                                  *
 *                                                                *
 * For more information on the TinyLine,                          *
 * please see <http://www.tinyline.com/>.                         *
 *****************************************************************/

package com.tinyline.app;

/**
 * The interface for objects expressing interest in image data through
 * the ImageProducer interfaces.  When a consumer is added to an image
 * producer, the producer delivers all of the data about the image
 * using the method calls defined in this interface.
 *
 * @see ImageProducer
 *
 */
public interface ImageConsumer
{
    /**
     * Delivers the pixels of the image. The pixel (px,py) is
		 * stored in the pixels array at index (px * scansize + py + off).
     * @param x,&nbsp;y the coordinates of the upper-left corner of the
     *        area of pixels to be set
     * @param w the width of the area of pixels
     * @param h the height of the area of pixels
     */
    public void newPixels(int x, int y, int w, int h);
}
