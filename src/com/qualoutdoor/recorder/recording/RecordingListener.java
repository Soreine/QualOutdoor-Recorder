package com.qualoutdoor.recorder.recording;


/**
 * A listener class for monitoring the state/progress of the RecordingService.
 * Override the callback to implements your specific behavior.
 */
public class RecordingListener {
    /** Callback invoked when device call state changes. */
    public void onRecordingChanged(boolean isRecording) {}

}