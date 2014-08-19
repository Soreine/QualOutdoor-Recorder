package com.qualoutdoor.recorder.recording;

import com.qualoutdoor.recorder.ServiceProvider;

/**
 * Interface for context that can hand out a RecordingService ServiceProvider
 * 
 * @author Gaborit Nicolas
 */
public interface RecordingContext {

    /** Get a RecordingService Provider */
    ServiceProvider<RecordingService> getRecordingServiceProvider();
}
