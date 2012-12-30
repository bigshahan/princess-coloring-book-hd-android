package com.gpasoftware.princess;

import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;
import com.gpasoftware.princess.SceneSelectorImage;


public class ArtSelector extends Activity implements OnClickListener  {
	SceneSelectorImage art;
	int current = 1;
	int total = 13;
	int[] artwork = { 0, R.raw.scene13, R.raw.scene1, R.raw.scene2, R.raw.scene3, R.raw.scene4, R.raw.scene5, R.raw.scene6, R.raw.scene7, R.raw.scene8, R.raw.scene9 , R.raw.scene10 , R.raw.scene11, R.raw.scene12 };
	Bitmap artBitmap;
	Canvas artCanvas;
	RectF artRect;
	Boolean playMusic = true;
	Intent musicIntent;
	
	private boolean mIsBound = false;
	private MusicService mServ;
	private ServiceConnection Scon = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			mServ = ((MusicService.ServiceBinder) binder).getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mServ = null;
		}
	};

	void doBindService() {
	 	bindService(new Intent(this,MusicService.class), Scon,Context.BIND_AUTO_CREATE);
	 	mIsBound = true;
	}

	void doUnbindService() {
		if(mIsBound) {
			unbindService(Scon);
	      	mIsBound = false;
		}
	}

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
		art = (SceneSelectorImage) this.findViewById(R.id.artImage);
		artRect = new RectF(0,0,640,960);
			
		// setup buttons
		ImageButton previous = (ImageButton) this.findViewById(R.id.previous);
		ImageButton next = (ImageButton) this.findViewById(R.id.next);
		
		// load artwork
		loadArtwork(artwork[current]);
		
		// setup click handling
		previous.setOnClickListener(this);
		next.setOnClickListener(this);
		art.setOnClickListener(this);
		((ToggleButton) this.findViewById(R.id.music_toggle)).setOnClickListener(this);
		
		// setup audio
		// get play music prefs
	    SharedPreferences settings = getSharedPreferences("PrincessColor", MODE_PRIVATE);
	    playMusic = settings.getBoolean("playMusic", true);
	    ((ToggleButton) this.findViewById(R.id.music_toggle)).setChecked(playMusic);
	    
	    
		playMusic();
	}
	
	private void playMusic() {
		if(playMusic) {
			doBindService();
			musicIntent = new Intent();
			musicIntent.setClass(this, MusicService.class);
			startService(musicIntent);
		}
	}
	
	private void stopMusic() {
		doUnbindService();
		
		if(musicIntent != null) {
			stopService(musicIntent);
		}
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
				} else {
					current = total;
				}
				
				loadArtwork(artwork[current]);
			break;
			case R.id.next:
				if(current+1 <= total) {
					current++;
				} else {
					current = 1;
				}
				
				loadArtwork(artwork[current]);
			break;
			case R.id.music_toggle:
				playMusic = ((ToggleButton) this.findViewById(R.id.music_toggle)).isChecked();
				
				if(playMusic) {
					playMusic();
				} else {
					stopMusic();
				}
				
				SharedPreferences settings = getSharedPreferences("PrincessColor", MODE_PRIVATE);
			    Editor editor = settings.edit();
			    editor.putBoolean("playMusic", playMusic);
			    editor.commit();
			break;
		}
	}
	
	// display artwork in art ImageView
	private void loadArtwork(int artwork) {
		SVG svg = SVGParser.getSVGFromResource(getResources(), artwork);
	    Picture picture = svg.getPicture();
	    Paint whitePaint = new Paint();
	    whitePaint.setColor(Color.WHITE);
		
		// render image
		artBitmap = Bitmap.createBitmap((int) artRect.width(), (int) artRect.height(), Config.ARGB_8888);
		
		artCanvas = new Canvas(artBitmap);
		art.setImageBitmap(artBitmap);
	    
		artCanvas.drawPicture(picture, artRect);
		getWindow().getDecorView().invalidate();
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		doUnbindService();
		
		if(musicIntent != null) {
			stopService(musicIntent);
		}
	}

}
