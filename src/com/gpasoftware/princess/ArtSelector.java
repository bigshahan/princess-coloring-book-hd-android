package com.gpasoftware.princess;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

public class ArtSelector extends Activity implements OnClickListener  {
	ImageView art;
	TextView counter;
	int current = 1;
	int total = 5;
	int[] artwork = {0, R.raw.scene1, R.raw.scene2, R.raw.scene3, R.raw.scene4, R.raw.scene5};
	Bitmap artBitmap;
	Canvas artCanvas;
	RectF artRect;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// detect if tablet
		Context context  = getApplicationContext();
		boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
		boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
		
		if(! (xlarge || large)) {
			// not a tablet. show error
			setContentView(R.layout.not_tablet);
			return;
		}
		
		setContentView(R.layout.art_selector);
		
		// setup ImageView and Counter
		art = (ImageView) this.findViewById(R.id.artImage);
		counter = (TextView) this.findViewById(R.id.counter);
		
		// setup buttons
		Button previous = (Button) this.findViewById(R.id.previous);
		Button next = (Button) this.findViewById(R.id.next);
		
		// load artwork
		Resources r = getResources();
		float x = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 350, r.getDisplayMetrics());
		float y = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, r.getDisplayMetrics());
		artRect = new RectF(0,0,x,y);
		artBitmap = Bitmap.createBitmap((int) x, (int) y, Config.ARGB_8888);
		
		artCanvas = new Canvas(artBitmap);
		art.setImageBitmap(artBitmap);
		loadArtwork(artwork[5]);
		
		// setup click handling
		previous.setOnClickListener(this);
		next.setOnClickListener(this);
		art.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.artImage:
				// open selected artwork
				Intent intent = new Intent(this, Sketch.class);
				intent.putExtra("artworkId", artwork[current]);
			    startActivity(intent);
			break;
			case R.id.previous:
				if(current-1 > 0) {
					current--;
					loadArtwork(artwork[current]);
					updateCounter();
				}
			break;
			case R.id.next:
				if(current+1 <= total) {
					current++;
					loadArtwork(artwork[current]);
					updateCounter();
				}
			break;
		}
	}
	
	// display artwork in art ImageView
	private void loadArtwork(int artwork) {
		SVG svg = SVGParser.getSVGFromResource(getResources(), artwork);
	    Picture picture = svg.getPicture();
	    Paint whitePaint = new Paint();
	    whitePaint.setColor(Color.WHITE);
	    
	    artCanvas.drawPaint(whitePaint);
	    artCanvas.drawPicture(picture, artRect);
	    getWindow().getDecorView().invalidate();
	}
	
	// update counter
	private void updateCounter() {
		counter.setText(Integer.toString(current) + '/' + Integer.toString(total));
	}

}
