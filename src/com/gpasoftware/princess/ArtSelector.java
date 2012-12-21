package com.gpasoftware.princess;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.view.Display;
import android.widget.ImageView;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Point;
import android.graphics.Bitmap.Config;
import android.graphics.RectF;
import android.util.Log;

public class ArtSelector extends Activity {
	ImageView im;
	Bitmap bm;
	Canvas c;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// show view
		Context context  = getApplicationContext();
		boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
		boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
		
		if(xlarge || large) {
			// load view
			setContentView(R.layout.activity_art_selector);
			
			// setup display size
			Display display = getWindowManager().getDefaultDisplay(); 
			Point size = new Point();
			
			int x = 0;
			int y = 0;
			
			if(android.os.Build.VERSION.SDK_INT >= 13) {
				display.getSize(size);
				x = size.x;
				y = size.y;
			} else {
				x = display.getHeight();
				y = display.getWidth();
			}
			
			// setup test render
			ImageView im2;
			im2 = (ImageView) this.findViewById(R.id.imageView1);
			SVG svg = SVGParser.getSVGFromResource(getResources(), R.raw.test);
			
			Bitmap bm2;
			Canvas c2;
			
			bm2 = Bitmap.createBitmap(x, y, Config.ARGB_8888);
			c2 = new Canvas(bm2);
			
			im2.setImageBitmap(bm2);
			
			RectF dest = new RectF(0,0,x,y);
			
		    Picture picture = svg.getPicture();
		    
		    c2.drawPicture(picture, dest);
			
			// setup drawable region
			im = (ImageView) this.findViewById(R.id.imageView2);
			
			Log.w("x", Integer.toString(x));
			Log.w("y", Integer.toString(y));
			
			bm = Bitmap.createBitmap(x, y, Config.ARGB_8888);
			c = new Canvas(bm);
			
			im.setImageBitmap(bm);
			
			Paint textPaint = new Paint();
			textPaint.setColor(Color.GREEN);
			c.drawLine(0, 0, c.getWidth(), c.getHeight(), textPaint);
		} else {
			// not a tablet. show error
			setContentView(R.layout.not_tablet);
		}
		
	}
}
