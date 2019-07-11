package android.example.com.prescriptminder.helperclasses;

import android.content.Context;
import android.example.com.prescriptminder.R;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder>
{
    private Context context;
    private LayoutInflater layoutInflater;
    public static ArrayList<Medicine> medicineArrayList;

    public MedicineAdapter(Context context)
    {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        medicineArrayList = new ArrayList<>();
    }

    public void addMedicine(Medicine medicine)
    {
        medicineArrayList.add(medicine);
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MedicineViewHolder(layoutInflater.inflate(R.layout.list_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder medicineViewHolder, int i) {
        Medicine medicine = medicineArrayList.get(i);
        medicineViewHolder.setMedicineText(medicine.getMedicineName());
        medicineViewHolder.setNoteText(medicine.getNote());
    }

    @Override
    public int getItemCount() {
        return medicineArrayList.size();
    }

    class MedicineViewHolder extends RecyclerView.ViewHolder
    {
        private TextView medicineText;
        private TextView noteText;

        MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void setMedicineText(String medicine)
        {
            medicineText.setText(medicine);
        }

        void setNoteText(String note)
        {
            noteText.setText(note);
        }
    }
}
