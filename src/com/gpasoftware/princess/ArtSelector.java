package com.gpasoftware.princess;

import android.os.Bundle;
import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;
import android.content.Context;
import android.content.res.Configuration;

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
		} else {
			// not a tablet. show error
			setContentView(R.layout.not_tablet);
		}
		
	}
}
