package android.example.com.prescriptminder.fragments;


import android.example.com.prescriptminder.R;
import android.example.com.prescriptminder.activities.MainActivity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private static ProfileFragment profileFragment;

    private Button sendButton;
    private TextView responseText;
    private EditText urlEditText;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        MainActivity.navigation.setSelectedItemId(R.id.navigation_profile);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        sendButton = view.findViewById(R.id.url_send_button);
//        responseText = view.findViewById(R.id.response);
//        urlEditText = view.findViewById(R.id.url);
//
//        sendButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
    }

    public void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public static ProfileFragment getProfileFragment() {
        if (profileFragment == null)
            profileFragment = new ProfileFragment();
        return profileFragment;
    }
}
