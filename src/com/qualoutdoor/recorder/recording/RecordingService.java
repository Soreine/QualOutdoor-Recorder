package com.qualoutdoor.recorder.recording;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class RecordingService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** Indicates whether the service is currently recording datas */
	public boolean isRecording() {
		return false;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
