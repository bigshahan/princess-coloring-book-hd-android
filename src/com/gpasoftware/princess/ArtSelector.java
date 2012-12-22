package com.gpasoftware.princess;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ArtSelector extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.art_selector);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.art_selector, menu);
		return true;
	}

}
