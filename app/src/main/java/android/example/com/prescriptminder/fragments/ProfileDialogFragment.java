package android.example.com.prescriptminder.fragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.example.com.prescriptminder.R;
import android.example.com.prescriptminder.activities.LoginActivity;
import android.example.com.prescriptminder.activities.MainActivity;
import android.example.com.prescriptminder.utils.Constants;
import android.example.com.prescriptminder.utils.OkHttpUtils;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

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
public class ProfileDialogFragment extends DialogFragment {

    private static EditText birthdayText;

    public ProfileDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.user_profile_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(Objects.requireNonNull(getContext()));
        String personName = Objects.requireNonNull(googleSignInAccount).getGivenName() + " " + googleSignInAccount.getFamilyName();
        final String email = Objects.requireNonNull(googleSignInAccount).getEmail();
        Uri profilePicture = googleSignInAccount.getPhotoUrl();

//        setProfileRequestJson(email);
        TextView nameText = view.findViewById(R.id.username_text);
        nameText.setText(personName);
        final EditText emailText = view.findViewById(R.id.email_text);
        emailText.setText(email);
        ImageView profileImage = view.findViewById(R.id.profile_picture);
        setCircleCropImage(getContext(), profilePicture, profileImage, (ProgressBar) (view.findViewById(R.id.profile_progress)));
        final EditText cityText = view.findViewById(R.id.city_text);
        cityText.setText("");
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
        birthdayText.setText("");
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
                    DialogFragment dateFragment = new ProfileDialogFragment.DatePickerFragment();
                    dateFragment.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "datepicker");
                }
            }
        });
        final RadioGroup genderRadioGroup = view.findViewById(R.id.gender_radio_group);
        genderRadioGroup.clearCheck();

        view.findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(birthdayText.getText()) && !TextUtils.isEmpty(cityText.getText()))
                {
                    try
                    {
                        //TODO: Change url
                        final String url = Constants.BASE_URL + "user/update/";
                        FormBody.Builder formBodyBuilder = new FormBody.Builder();
                        formBodyBuilder.add("email", Objects.requireNonNull(email));
                        formBodyBuilder.add("gender", ((RadioButton)(view.findViewById(genderRadioGroup.getCheckedRadioButtonId()))).getText().toString());
                        formBodyBuilder.add("birthday", birthdayText.getText().toString());
                        formBodyBuilder.add("city", cityText.getText().toString());
                        FormBody formBody = formBodyBuilder.build();
//                        OkHttpClient okHttpClient = new OkHttpClient();
//                        Request request = new Request.Builder().url(url).post(formBody).build();
                        Call call = OkHttpUtils.getOkHttpUtils().sendHttpPostRequest(url, formBody);
                        call.enqueue(new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {

                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                try
                                {
                                    JSONObject responseJson = new JSONObject(Objects.requireNonNull(response.body()).string());
                                    String status = responseJson.getString("status");
                                    if (status.equalsIgnoreCase("ok"))
                                    {
                                        dismiss();
                                        Intent intent = new Intent(getContext(), MainActivity.class);
                                        intent.putExtra("userType", LoginActivity.userType);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                }
                                catch (Exception e)
                                {
                                    Log.e("ProfileFragment", e.toString());
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

    public void setCircleCropImage(Context context, Uri url, ImageView imageView, final ProgressBar progressBar)
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
}
