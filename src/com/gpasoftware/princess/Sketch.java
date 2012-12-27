package com.gpasoftware.princess;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.res.Resources;
import android.view.Display;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
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
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;

public class Sketch extends Activity implements OnTouchListener, SeekBar.OnSeekBarChangeListener, OnClickListener {
	ImageView im;
	Bitmap bm;
	Canvas c;
	Paint m;
	float lastX = 0;
	float lastY = 0;
	float currentThickness = 8;
	Resources r;
	SeekBar thicknessSlider;
	ImageView thicknessImage;
	Canvas thicknessCanvas;
	Bitmap thicknessBitmap;
	LinearLayout thicknessLayout;
	Paint thicknessClear;
	Paint thicknessFill;
	
	@Override
	public boolean onTouch(View V, MotionEvent event) {
		float currentX = event.getX(0);
		float currentY = event.getY(0);
		float midX = 0;
		float midY = 0;
		
		int action = event.getActionMasked();
		
		if(action == MotionEvent.ACTION_DOWN) {
			lastX = currentX;
			lastY = currentY;
			m.setStrokeWidth(0);
			c.drawCircle(currentX, currentY, currentThickness/2, m);
			m.setStrokeWidth(currentThickness);
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
					path.lineTo(midX, midY);
					c.drawPath(path, m);
					m.setStrokeWidth(0);
					c.drawCircle(midX, midY, currentThickness/2, m);
					m.setStrokeWidth(currentThickness);
					lastX = midX;
					lastY = midY;
				}
			}
			
			c.drawLine(lastX, lastY, currentX, currentY, m);
			m.setStrokeWidth(0);
			c.drawCircle(currentX, currentY, currentThickness/2, m);
			m.setStrokeWidth(currentThickness);
			
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
		r = getResources();
		
		// artwork to display
		Bundle extras = getIntent().getExtras();
		int artworkId = extras.getInt("artworkId");

		// load view
		setContentView(R.layout.sketch);
		
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
			x = display.getWidth();
			y = display.getHeight();
		}
		
		y = y - (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, r.getDisplayMetrics());
		
		// setup test render
		ImageView im2;
		im2 = (ImageView) this.findViewById(R.id.imageView1);
		
		SVG svg = SVGParser.getSVGFromResource(getResources(), artworkId);

		RectF dest = svg.getBounds();
		
		if(dest == null|| (int) dest.width() == 0 || (int) dest.height() == 0) {
			dest = svg.getLimits();
		}
		
		if(dest != null && (int) dest.width() != 0 && (int) dest.height() != 0) {
			Bitmap bm2;
			Canvas c2;
			
		    // scale picture
			Picture picture = svg.getPicture();
			
			float xScaled = dest.width();
			float yScaled = dest.height();
			
			// scale DOWN if EITHER dimensions are bigger
			if((xScaled > x || yScaled > y) && x > 0 && y > 0) {
				if(xScaled > yScaled) {
					// width is bigger than height
					// match to x value
					float scaleFactor = xScaled/x;
					xScaled = xScaled * scaleFactor;
					yScaled = yScaled * scaleFactor;
					
				} else {
					// height is bigger than or equal to width
					// match to y value
					float scaleFactor = yScaled/y;
					xScaled = xScaled * scaleFactor;
					yScaled = yScaled * scaleFactor;
				}
				
				// update dest
				dest = new RectF(0,0,xScaled, yScaled);

			// scale UP if BOTH dimensions are smaller
			} if(xScaled < x && yScaled < y && x > 0 && y > 0) {
				if(xScaled > yScaled) {
					// width is bigger than height
					// match to x value
					float scaleFactor = x/xScaled;
					xScaled = xScaled * scaleFactor;
					yScaled = yScaled * scaleFactor;
					
				} else {
					// height is bigger than or equal to width
					// match to y value
					float scaleFactor = y/yScaled;
					xScaled = xScaled * scaleFactor;
					yScaled = yScaled * scaleFactor;
				}
				
				// update dest
				dest = new RectF(0,0,xScaled, yScaled);
			}

		    // draw picture
			bm2 = Bitmap.createBitmap((int) xScaled, (int) yScaled, Config.ARGB_8888);
			c2 = new Canvas(bm2);
			
			im2.setImageBitmap(bm2);
			
		    c2.drawPicture(picture, dest);
		    
		    im2.setOnTouchListener(this);
		}
		
		
		// setup drawable region
		im = (ImageView) this.findViewById(R.id.imageView2);
		
		bm = Bitmap.createBitmap(x, y, Config.ARGB_8888);
		c = new Canvas(bm);
		
		im.setImageBitmap(bm);
		
		// setup current paint
		m = new Paint();
		m.setColor(Color.MAGENTA);
		// m.setAlpha(200);
		m.setStyle(Style.FILL_AND_STROKE);
		m.setAntiAlias(true);
		
		// setup thickness paints
		thicknessClear = new Paint();
		thicknessClear.setColor(getResources().getColor(R.color.light_pink));
		thicknessClear.setAntiAlias(true);
		thicknessClear.setStyle(Style.FILL);
		
		// setup thickness views
		thicknessSlider = (SeekBar) this.findViewById(R.id.thicknessSlider);
		thicknessSlider.setOnSeekBarChangeListener(this);
		thicknessImage = (ImageView) this.findViewById(R.id.thicknessImage);
		int thicknessX = dpToPxInt(60);
		int thicknessY = dpToPxInt(40);
		thicknessBitmap = Bitmap.createBitmap(thicknessX, thicknessY, Config.ARGB_8888);
		thicknessCanvas = new Canvas(thicknessBitmap);
		thicknessImage.setImageBitmap(thicknessBitmap);
		thicknessLayout = (LinearLayout) this.findViewById(R.id.thicknessLayout);
		
		setThickness(8);
		
		// setup save button
		ImageButton saveButton = (ImageButton) this.findViewById(R.id.saveButton);
		saveButton.setOnClickListener(this);
	}
	
	// DP TO PIXELS, INT
	int dpToPxInt(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
	}
	
	// MANAGE THICKNESS
	void setThickness(float thickness) {
		// set thickness. convert dp to px
		currentThickness = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, thickness, r.getDisplayMetrics());
		updateThickness();
	}
	
	void updateThickness() {
		// clear canvas
		thicknessCanvas.drawPaint(thicknessClear);
		
		// draw circle
		float posX = ((float) dpToPxInt(60))/2;
		float posY = currentThickness/2;
		
		m.setStrokeWidth(0);
		thicknessCanvas.drawCircle(posX, posY, currentThickness/2, m);
		m.setStrokeWidth(currentThickness);
		
		// invalidate
		thicknessLayout.invalidate();
	}

	@Override
	public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
		// TODO Auto-generated method stub
		float thickness = (float) 5 + (float) progress * (float) 0.15;
		setThickness(thickness);
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}

	// save!!
	@Override
	public void onClick(View view) {
		// save to gallery
		
		
		// show alert telling about success
		
		
	}
}
