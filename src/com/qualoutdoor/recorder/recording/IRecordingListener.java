package com.qualoutdoor.recorder.recording;


/**
 * A listener interface for monitoring the state/progress of the RecordingService.
 * Override the callback to implements your specific behavior.
 */
public interface IRecordingListener {
    /** Callback invoked when device call state changes. */
    void onRecordingChanged(boolean isRecording);

}