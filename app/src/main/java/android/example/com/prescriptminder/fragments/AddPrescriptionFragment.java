package android.example.com.prescriptminder.fragments;


import android.app.Dialog;
import android.example.com.prescriptminder.R;
import android.example.com.prescriptminder.helperclasses.Medicine;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddPrescriptionFragment extends DialogFragment {

    private static AddPrescriptionFragment addPrescriptionFragment;

    public AddPrescriptionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_prescription, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {

        final EditText medicineText = view.findViewById(R.id.medicine_name_text);
        medicineText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0)
                    medicineText.setError("Field cannot be blank");
            }
        });

        Button saveButton = view.findViewById(R.id.save_prescription_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(medicineText.getText()))
                {
                    String medicineName = medicineText.getText().toString();
                    String timing = "";
                    CheckBox bbCheck = view.findViewById(R.id.bb_checkbox);
                    if (bbCheck.isChecked())
                        timing += "1";
                    else
                        timing += "0";
                    CheckBox abCheck = view.findViewById(R.id.ab_checkbox);
                    if (abCheck.isChecked())
                        timing += "1";
                    else
                        timing += "0";
                    CheckBox blCheck = view.findViewById(R.id.bl_checkbox);
                    if (blCheck.isChecked())
                        timing += "1";
                    else
                        timing += "0";
                    CheckBox alCheck = view.findViewById(R.id.al_checkbox);
                    if (alCheck.isChecked())
                        timing += "1";
                    else
                        timing += "0";
                    CheckBox bdCheck = view.findViewById(R.id.bd_checkbox);
                    if (bdCheck.isChecked())
                        timing += "1";
                    else
                        timing += "0";
                    CheckBox adCheck = view.findViewById(R.id.ad_checkbox);
                    if (adCheck.isChecked())
                        timing += "1";
                    else
                        timing += "0";
                    EditText noteText = view.findViewById(R.id.note_text);

                    String note = noteText.getText().toString();
                    Medicine medicine = new Medicine(medicineName, note);
                    RecordFragment.medicineAdapter.addMedicine(medicine);
                    RecordFragment.recyclerView.setAdapter(RecordFragment.medicineAdapter);

                    //TODO: Make network call before dismiss()

                    dismiss();
                }
                else
                    Toast.makeText(getContext(), "Medicine name cannot be blank", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static AddPrescriptionFragment getAddPrescriptionFragment() {
        if (addPrescriptionFragment == null)
            addPrescriptionFragment = new AddPrescriptionFragment();
        return addPrescriptionFragment;
    }
}
