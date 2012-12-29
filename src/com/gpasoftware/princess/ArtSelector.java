package com.gpasoftware.princess;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import com.tinyline.app.SVGView;
import com.tinyline.tiny2d.Tiny2D;
import com.tinyline.tiny2d.TinyState;


public class ArtSelector extends Activity implements OnClickListener  {
	int current = 1;
	int total = 13;
	SVGView canvas;
	Tiny2D t2d;
    TinyState tstate;
	
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
		canvas = (SVGView) this.findViewById(R.id.artImage);
			
		// setup buttons
		ImageButton previous = (ImageButton) this.findViewById(R.id.previous);
		ImageButton next = (ImageButton) this.findViewById(R.id.next);
		
		// load artwork
		loadArtwork();
		
		// setup click handling
		previous.setOnClickListener(this);
		next.setOnClickListener(this);
		canvas.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.artImage:
				// open selected artwork
				Intent intent = new Intent(this, Sketch.class);
				intent.putExtra("artworkId", current);
			    startActivity(intent);
			break;
			case R.id.previous:
				if(current-1 > 0) {
					current--;
				} else {
					current = total;
				}
				
				loadArtwork();
			break;
			case R.id.next:
				if(current+1 <= total) {
					current++;
				} else {
					current = 1;
				}
				
				loadArtwork();
			break;
		}
	}
	
	// display artwork in art ImageView
	private void loadArtwork() {
		canvas.goURL("svg/scene" + current + ".svg");
		t2d = canvas.raster.getTiny2D();
		tstate = t2d.getState();	
		Log.w("tsate", tstate.toString());
//		getWindow().getDecorView().invalidate();
	}

}
