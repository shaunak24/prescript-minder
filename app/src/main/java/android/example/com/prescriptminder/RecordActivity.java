package android.example.com.prescriptminder;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import java.io.File;
import java.io.IOException;

import okhttp3.Response;

public class RecordActivity extends AppCompatActivity {

    public static String PRINT_URL;
    private FloatingActionButton startButton;
    private Button pauseButton;
    private Button playButton;
    private Button printQR;
    private TextView recordPrompt;
    private int recordPromptCount = 0;
    private Boolean startRecording = true;
    private Boolean pauseRecording = true;
    private Chronometer chronometer;
    private long timeWhenPaused = 0;
    private String outputFile;
    private MediaRecorder mediaRecorder;
    private String fileName;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        chronometer = findViewById(R.id.chronometer);
        startButton = findViewById(R.id.record_button);
        pauseButton = findViewById(R.id.pause_button);
        printQR = findViewById(R.id.print_QR_button);
        recordPrompt = findViewById(R.id.recording_status);
        playButton = findViewById(R.id.play_button);

        mediaRecorder_setup();

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseButton.setVisibility(View.VISIBLE);
                playButton.setVisibility(View.VISIBLE);
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

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(outputFile);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        printQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                file = new File(outputFile);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Response response = MyHttpRequest.uploadAudio(file);
                            PRINT_URL = "Shaunak";//response.body().toString();
                            //BluetoothActivity.sendUrl(PRINT_URL);

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        });
    }

    private void mediaRecorder_setup() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        fileName = "Hello";//DateFormat.getDateTimeInstance().format(new Date());
        outputFile = Environment.getExternalStorageDirectory() + "/" + fileName;
        mediaRecorder.setOutputFile(outputFile);
    }

    private void onRecordStart(Boolean start) {

        if(start) {
            printQR.setVisibility(View.INVISIBLE);
            startButton.setImageResource(R.drawable.ic_stop);
            showToast("Recording started");

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

            startRecordingService();
            recordPrompt.setText("Recording" + ".");
            recordPromptCount++;
        }
        else {
            printQR.setVisibility(View.VISIBLE);
            startButton.setImageResource(R.drawable.ic_mic);
            chronometer.stop();
            chronometer.setBase(SystemClock.elapsedRealtime());
            timeWhenPaused = 0;
            recordPrompt.setText("Tap the button to start recording");
            stopRecordingService();
        }
    }

    private void startRecordingService() {
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopRecordingService() {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        mediaRecorder_setup();
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

