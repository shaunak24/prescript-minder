package android.example.com.prescriptminder.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.example.com.prescriptminder.R;
import android.example.com.prescriptminder.helperclasses.Medicine;
import android.example.com.prescriptminder.helperclasses.MedicineAdapter;
import android.example.com.prescriptminder.utils.Constants;
import android.example.com.prescriptminder.utils.OkHttpUtils;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
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

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends Fragment {

    private static RecordFragment recordFragment;
    public static final MediaType WAV = MediaType.parse("audio/x-wav");
    public static String PRINT_URL;
    private ImageView startButton;
    private ImageView pauseButton;
    private ImageView printQR;
    private ImageView upload;
    private ImageView playButton;
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
    private String pres_id;
    private Call call;

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
        playButton = view.findViewById(R.id.play_button);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setHasFixedSize(false);
        medicineAdapter = new MedicineAdapter(view.getContext());
        recyclerView.setAdapter(medicineAdapter);

        final Button addMedicineButton = view.findViewById(R.id.add_medicine_button);
        addMedicineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPrescriptionFragment addPrescriptionFragment = new AddPrescriptionFragment();
                addPrescriptionFragment.setCancelable(true);
                addPrescriptionFragment.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "Prescription");
            }
        });

        chronometer = view.findViewById(R.id.chronometer2);
        startButton = view.findViewById(R.id.record_button);
        pauseButton = view.findViewById(R.id.pause_button);
        printQR = view.findViewById(R.id.print_QR_button);
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
                showNotification("Medicine 1", "8 am");
            }
        });

        printQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            BluetoothConnectFragment.sendUrl(PRINT_URL);
                            Log.e("tag", "Printing QR");
                            //MainActivity.replaceFragment(RecentScanFragment.getRecentScanFragment());
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
                final ArrayList<Medicine> arrayList = MedicineAdapter.getMedicineArrayList();
                file = new File(outputFile);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            sendMedicineData(arrayList);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        });
    }

    private static void showNotification(String name, String time) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getRecordFragment().getActivity())
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Reminder to take medicines")
                .setContentText("Medicine " + name + " should be taken at " + time)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getRecordFragment().getContext());
        notificationManager.notify(1, builder.build());
    }

    private void sendMedicineData(ArrayList<Medicine> arrayList) throws JSONException {
        String url = Constants.BASE_URL + "prescript/store/";
        final SharedPreferences sharedPref = Objects.requireNonNull(getActivity()).getPreferences(Context.MODE_PRIVATE);
        String email = sharedPref.getString("email", "null");
        String data = "shaunak.12.24@gmail.com," + email + ",";
        data += getRemainingData(arrayList);
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), data);
        call = OkHttpUtils.sendHttpPostRequest(url, requestBody);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                PRINT_URL = response.body().string();
                Log.e("PRINT URL", PRINT_URL);
                String[] split = PRINT_URL.split("/");
                pres_id = split[split.length - 1];
                MultipartBody multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("file", "hi", RequestBody.create(WAV, file))
                        .build();
                call = OkHttpUtils.sendHttpPostRequest(Constants.BASE_URL + "prescript/storeaudio/" + pres_id + "/", multipartBody);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.e("Audio upload response", response.body().string());
                    }
                });
            }
        });
    }

    private String getRemainingData(ArrayList<Medicine> list) throws JSONException {
        String remaining = "";
        for (Medicine m : list) {
            remaining += m.getMedicineName() + ",";
            remaining += m.getNote() + ",";
            remaining += m.getDetails() + ",";
        }
        return remaining;
    }

    private void mediaRecorder_setup() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        fileName = "audio1";
        outputFile = Environment.getExternalStorageDirectory() + "/" + fileName;
        mediaRecorder.setOutputFile(outputFile);
    }

    private void onRecordStart(Boolean start) {

        if (start) {
            startButton.setImageResource(R.drawable.ic_stop);
            showToast("Recording started");

            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();

            startRecordingService();

        } else {
            startButton.setImageResource(R.drawable.ic_microphone_2);
            chronometer.stop();
            chronometer.setBase(SystemClock.elapsedRealtime());
            timeWhenPaused = 0;
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
        if (pause) {
            timeWhenPaused = chronometer.getBase() - SystemClock.elapsedRealtime();
            chronometer.stop();
            mediaRecorder.pause();
        } else {
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
