package android.example.com.prescriptminder;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import java.io.File;

public class RecordActivity extends AppCompatActivity {

    FloatingActionButton startButton;
    Button pauseButton;
    TextView recordPrompt;
    int recordPromptCount = 0;
    Boolean startRecording = true;
    Boolean pauseRecording = true;
    Chronometer chronometer;
    long timeWhenPaused = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        chronometer = findViewById(R.id.chronometer);
        startButton = findViewById(R.id.record_button);
        pauseButton = findViewById(R.id.pause_button);
        recordPrompt = findViewById(R.id.recording_status);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseButton.setVisibility(View.VISIBLE);
                onRecordStart(startRecording);
                startRecording = !startRecording;
            }
        });

        pauseButton.setVisibility(View.GONE);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRecordPause(pauseRecording);
                pauseRecording = !pauseRecording;
            }
        });
    }

    private void onRecordStart(Boolean start) {

        Intent intent = new Intent(getApplication(), RecordingService.class);
        if(start) {
            startButton.setImageResource(R.drawable.ic_stop);
            showToast("Recording started");

            File folder = new File(getApplication().getFilesDir(), "/PrescriptMinder");
            if(!folder.exists()) {
                folder.mkdir();
            }
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
            chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    if(recordPromptCount == 0) {
                        recordPrompt.setText("Recording" + ".");
                    }
                    else if(recordPromptCount == 1) {
                        recordPrompt.setText("Recording" + "..");
                    }
                    else if(recordPromptCount == 2) {
                        recordPrompt.setText("Recording" + "...");
                        recordPromptCount = -1;
                    }
                    recordPromptCount++;
                }
            });

            startService(intent);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            recordPrompt.setText("Recording" + ".");
            recordPromptCount++;
        }
        else {
            startButton.setImageResource(R.drawable.ic_mic);
            chronometer.stop();
            chronometer.setBase(SystemClock.elapsedRealtime());
            timeWhenPaused = 0;
            recordPrompt.setText("Tap the button to start recording");
            stopService(intent);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void onRecordPause(Boolean pause) {
        if(pause) {
            pauseButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play, 0, 0, 0);
            recordPrompt.setText("Resume");
            pauseButton.setText("Resume");
            timeWhenPaused = chronometer.getBase() - SystemClock.elapsedRealtime();
            chronometer.stop();
        }
        else {
            pauseButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause, 0, 0, 0);
            recordPrompt.setText("Pause");
            pauseButton.setText("Pause");
            chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenPaused);
            chronometer.start();
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.record_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.settings_id:
                //Open settings activity
        }
        return super.onOptionsItemSelected(item);
    }
}

