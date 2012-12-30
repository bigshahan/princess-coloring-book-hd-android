package com.gpasoftware.princess;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;

public class MusicActivity extends Activity {
	protected Boolean playMusic = true;
	protected Intent musicIntent;
	
	protected void loadMusicPrefs() {
		SharedPreferences settings = getSharedPreferences("PrincessColor", MODE_PRIVATE);
	    playMusic = settings.getBoolean("playMusic", true);
	}
	
	protected boolean mIsBound = false;
	//private MusicService mServ;
	protected ServiceConnection Scon = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			//mServ = ((MusicService.ServiceBinder) binder).getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			//mServ = null;
		}
	};

	protected void doBindService() {
	 	bindService(new Intent(this,MusicService.class), Scon,Context.BIND_AUTO_CREATE);
	 	mIsBound = true;
	}

	protected void doUnbindService() {
		if(mIsBound) {
			unbindService(Scon);
	      	mIsBound = false;
		}
	}
	
	protected void playMusic() {
	    loadMusicPrefs();

		if(playMusic) {
			doBindService();
			
			if(musicIntent == null) {
				musicIntent = new Intent();
				musicIntent.setClass(this, MusicService.class);
			}
			
			startService(musicIntent);
		}
	}
	
	protected void stopMusic() {
		doUnbindService();
		
		if(musicIntent != null) {
			stopService(musicIntent);
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		stopMusic();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		playMusic();
	}

	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopMusic();
	}
}
