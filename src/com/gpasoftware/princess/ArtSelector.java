package com.gpasoftware.princess;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.widget.ImageView;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;
import android.graphics.Color;

public class ArtSelector extends Activity {

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
			
			// setup test render
			ImageView imageView;
			imageView = (ImageView) this.findViewById(R.id.imageView1);
			SVG svg = SVGParser.getSVGFromResource(getResources(), R.raw.test);
			imageView.setImageDrawable(svg.createPictureDrawable());
		} else {
			// not a tablet. show error
			setContentView(R.layout.not_tablet);
		}
		
	}
}
