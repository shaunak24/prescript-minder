package android.example.com.prescriptminder.fragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.example.com.prescriptminder.R;
import android.example.com.prescriptminder.utils.Constants;
import android.example.com.prescriptminder.utils.OkHttpUtils;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private static ProfileFragment profileFragment;

    private static EditText birthdayText;
    private EditText cityText;
    private RadioGroup genderRadioGroup;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {

        SharedPreferences sharedPref = Objects.requireNonNull(getActivity()).getPreferences(Context.MODE_PRIVATE);
        final String email = sharedPref.getString("email", "null");
        String personName = sharedPref.getString("personName", "null");
        String profilePicture = sharedPref.getString("profilePicture", "null");
        String gender = sharedPref.getString("gender", "null");
        String birthday = sharedPref.getString("birthday", "null");
        String city = sharedPref.getString("city", "null");

        ProgressBar progressBar = view.findViewById(R.id.profile_progress);
        TextView nameText = view.findViewById(R.id.username_text);
        nameText.setText(personName);
        TextView emailText = view.findViewById(R.id.email_text);
        emailText.setText(email);
        ImageView profileImage = view.findViewById(R.id.profile_picture);
        setCircleCropImage(getContext(), profilePicture, profileImage, progressBar);
        cityText = view.findViewById(R.id.city_text);
        cityText.setText(city);
        cityText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0)
                    cityText.setError("Field cannot be blank");
            }
        });
        birthdayText = view.findViewById(R.id.birthday_text);
        birthdayText.setText(birthday);
        birthdayText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0)
                    birthdayText.setError("Field cannot be blank");
            }
        });
        birthdayText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {
                    DialogFragment dateFragment = new DatePickerFragment();
                    dateFragment.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "datepicker");
                }
            }
        });
        genderRadioGroup = view.findViewById(R.id.gender_radio_group);
        switch (Objects.requireNonNull(gender))
        {
            case "Male":
                genderRadioGroup.check(R.id.male_radio_button);
                break;
            case "Female":
                genderRadioGroup.check(R.id.female_radio_button);
                break;
            case "Other":
                genderRadioGroup.check(R.id.other_radio_button);
                break;
        }

        view.findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(birthdayText.getText()) && !TextUtils.isEmpty(cityText.getText()))
                {
                    try
                    {
                        final String url = Constants.BASE_URL + "user/update/";
                        FormBody.Builder formBodyBuilder = new FormBody.Builder();
                        formBodyBuilder.add("email", Objects.requireNonNull(email));
                        formBodyBuilder.add("gender", ((RadioButton)(view.findViewById(genderRadioGroup.getCheckedRadioButtonId()))).getText().toString());
                        formBodyBuilder.add("birthday", birthdayText.getText().toString());
                        formBodyBuilder.add("city", cityText.getText().toString());
                        FormBody formBody = formBodyBuilder.build();
                        Call call = OkHttpUtils.getOkHttpUtils().sendHttpPostRequest(url, formBody);
                        call.enqueue(new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {

                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                JSONObject responseJson = null;
                                try {
                                    responseJson = new JSONObject(Objects.requireNonNull(response.body()).string());
                                    Log.e("responseJson", responseJson.getString("status"));
//                                    Toast.makeText(getContext(), responseJson.getString("status"), Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    catch (Exception e)
                    {
                        Log.e("ProfileFragment", e.toString());
                    }
                }
                else
                    Toast.makeText(getContext(), "Field(s) cannot be blank", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setCircleCropImage(Context context, String url, ImageView imageView, final ProgressBar progressBar)
    {
        Glide.with(context)
                .load(url)
                .apply(RequestOptions.circleCropTransform())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (progressBar != null)
                            progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageView);
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private int month,year,day;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(Objects.requireNonNull(getActivity()), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user

            String date = day + "-" + (month+1) + "-" + year;
            birthdayText.setText(date);
        }
    }

    public static ProfileFragment getProfileFragment() {
        if (profileFragment == null)
            profileFragment = new ProfileFragment();
        return profileFragment;
    }
}
