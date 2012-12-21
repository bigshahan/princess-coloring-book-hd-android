package com.gpasoftware.princess;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.Display;
import android.widget.ImageView;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.Point;
import android.graphics.Bitmap.Config;
import android.graphics.RectF;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;

public class ArtSelector extends Activity implements OnTouchListener {
	ImageView im;
	Bitmap bm;
	Canvas c;
	Paint m;
	float lastX = 0;
	float lastY = 0;
	float px = 5;
	
	@Override
	public boolean onTouch(View V, MotionEvent event) {
		float currentX = event.getRawX();
		float currentY = event.getRawY();
		float midX = 0;
		float midY = 0;
		
		

		int action = event.getActionMasked();
		
		if(action == MotionEvent.ACTION_DOWN) {
			lastX = currentX;
			lastY = currentY;
			return true;
		}
		
		Path path;
		
		if((action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_UP) && lastX != 0 && lastY != 0) {
			if(event.getHistorySize() > 0) {
				for(int i = 0; i < event.getHistorySize(); i++) {
					midX = event.getHistoricalX(i);
					midY = event.getHistoricalY(i);
					
					path = new Path();
					path.moveTo(lastX, lastY);
//					path.setFillType(Path.FillType.INVERSE_EVEN_ODD); 
					path.lineTo(midX, midY);
					c.drawPath(path, m);
					c.drawPoint(midX, midY, m);
					lastX = midX;
					lastY = midY;
				}
			}
			
			c.drawLine(lastX, lastY, currentX, currentY, m);
			c.drawPoint(currentX, currentY, m);

			V.invalidate();
		}

		
		
		if(action == MotionEvent.ACTION_MOVE) {
			lastX = currentX;
			lastY = currentY;
		} else {
			lastX = 0;
			lastY = 0;
		}
		
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.w("started", "yup");
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
		    
		    im2.setOnTouchListener(this);
			
			// setup drawable region
			im = (ImageView) this.findViewById(R.id.imageView2);
			
			Log.w("x", Integer.toString(x));
			Log.w("y", Integer.toString(y));
			
			bm = Bitmap.createBitmap(x, y, Config.ARGB_8888);
			c = new Canvas(bm);
			
			im.setImageBitmap(bm);
			
			m = new Paint();
			m.setColor(Color.MAGENTA);
//			m.setAlpha(200);
			m.setStyle(Style.STROKE);
			m.setAntiAlias(true);
			
			// determine stroke width
			Resources r = getResources();
			px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, r.getDisplayMetrics());
			
			m.setStrokeWidth(px);
			
			c.drawLine(0, 0, c.getWidth(), c.getHeight(), m);
		} else {
			// not a tablet. show error
			setContentView(R.layout.not_tablet);
		}
		
	}
}
