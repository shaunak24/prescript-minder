package android.example.com.prescriptminder.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.example.com.prescriptminder.R;
import android.example.com.prescriptminder.helperclasses.MedicineAdapter;
import android.example.com.prescriptminder.utils.MyHttpRequest;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends Fragment {

    private static RecordFragment recordFragment;

    public static String PRINT_URL;
    private ImageView startButton;
    private ImageView pauseButton;
    private ImageView printQR;
    private ImageView upload;
    //private TextView recordPrompt;
    private int recordPromptCount = 0;
    private Boolean startRecording = true;
    private Boolean pauseRecording = true;
    private Chronometer chronometer;
    private long timeWhenPaused = 0;
    private String outputFile;
    private MediaRecorder mediaRecorder;
    private String fileName;
    private File file;
    public static RecyclerView recyclerView;
    public static MedicineAdapter medicineAdapter;

    public RecordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        MainActivity.navigation.setSelectedItemId(R.id.navigation_record_audio);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        recyclerView = view.findViewById(R.id.medicine_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setHasFixedSize(false);
        medicineAdapter = new MedicineAdapter(view.getContext());

        final Button addMedicineButton = view.findViewById(R.id.add_medicine_button);
        addMedicineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPrescriptionFragment addPrescriptionFragment = new AddPrescriptionFragment();
                addPrescriptionFragment.setCancelable(false);
                addPrescriptionFragment.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "Prescription");
            }
        });

        chronometer = view.findViewById(R.id.chronometer2);
        startButton = view.findViewById(R.id.record_button);
        pauseButton = view.findViewById(R.id.pause_button);
        printQR = view.findViewById(R.id.print_QR_button);
        //recordPrompt = view.findViewById(R.id.recording_status);
        //playButton = view.findViewById(R.id.play_button);
        upload = view.findViewById(R.id.upload_audio);

        mediaRecorder_setup();

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRecordStart(startRecording);
                startRecording = !startRecording;
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRecordPause(pauseRecording);
                pauseRecording = !pauseRecording;
            }
        });

//        playButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                MediaPlayer mediaPlayer = new MediaPlayer();
//                try {
//                    mediaPlayer.setDataSource(outputFile);
//                    mediaPlayer.prepare();
//                    mediaPlayer.start();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        printQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            BluetoothConnectFragment.sendUrl(PRINT_URL);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                file = new File(outputFile);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            SharedPreferences sharedPref = Objects.requireNonNull(getActivity()).getPreferences(Context.MODE_PRIVATE);
                            String email = sharedPref.getString("email", "null");
                            Response response = MyHttpRequest.uploadAudio(file, "shaunak.12.24@gmail.com", email);
                            PRINT_URL = response.body().string();
                            Log.e("RecordActivity", PRINT_URL);
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
        fileName = "audio1";//DateFormat.getDateTimeInstance().format(new Date());
        outputFile = Environment.getExternalStorageDirectory() + "/" + fileName;
        mediaRecorder.setOutputFile(outputFile);
    }

    private void onRecordStart(Boolean start) {

        if(start) {
            startButton.setImageResource(R.drawable.ic_stop);
            showToast("Recording started");

            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
//            chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
//                @Override
//                public void onChronometerTick(Chronometer chronometer) {
//                    if(recordPromptCount == 0) {
//                        recordPrompt.setText("Recording" + ".");
//                    }
//                    else if(recordPromptCount == 1) {
//                        recordPrompt.setText("Recording" + "..");
//                    }
//                    else if(recordPromptCount == 2) {
//                        recordPrompt.setText("Recording" + "...");
//                        recordPromptCount = -1;
//                    }
//                    recordPromptCount++;
//                }
//            });

            startRecordingService();
            //recordPrompt.setText("Recording" + ".");
            recordPromptCount++;
        }
        else {
            startButton.setImageResource(R.drawable.ic_mic);
            chronometer.stop();
            chronometer.setBase(SystemClock.elapsedRealtime());
            timeWhenPaused = 0;
            //recordPrompt.setText("Tap the button to start recording");
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
            //pauseButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play, 0, 0, 0);
            //recordPrompt.setText("Resume");
            //pauseButton.setText("Resume");
            timeWhenPaused = chronometer.getBase() - SystemClock.elapsedRealtime();
            chronometer.stop();
            mediaRecorder.pause();
        }
        else {
            //pauseButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause, 0, 0, 0);
            //recordPrompt.setText("Pause");
            //pauseButton.setText("Pause");
            chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenPaused);
            chronometer.start();
            mediaRecorder.resume();
        }
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static RecordFragment getRecordFragment() {
        if (recordFragment == null)
            recordFragment = new RecordFragment();
        return recordFragment;
    }
}
