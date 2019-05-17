package android.example.com.prescriptminder;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

class RecordingService extends Service {

    String file_name, file_path;
    MediaRecorder mediaRecorder = null;
    long startTimeMillis = 0;
    long elapsedTimeMillis = 0;
    OnTimerChangedListener onTimerChangedListener = null;
    static final java.text.SimpleDateFormat timerFormat = new java.text.SimpleDateFormat("mm:ss", Locale.getDefault());
    Timer mTimer = null;
    TimerTask incrementTimerTask = null;

    public interface OnTimerChangedListener {
        void onTimerChanged(int seconds);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //database = new DBHelper(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startRecording();
        return START_STICKY;
    }

    private void startRecording() {
    }

    @Override
    public void onDestroy() {
        if(mediaRecorder != null) {
            stopRecoding(); 
        }
        super.onDestroy();
    }

    private void stopRecoding() {
    }
}
