package android.example.com.prescriptminder.utils;

import android.content.Context;
import android.example.com.prescriptminder.R;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MedicinesAdapter extends RecyclerView.Adapter<MedicinesAdapter.MedicinesViewHolder> {

    private Context mContext;
    private ArrayList<Medicines> medicines_list;

    public MedicinesAdapter(Context mContext, ArrayList<Medicines> medicines_list) {
        this.mContext = mContext;
        this.medicines_list = medicines_list;
    }

    @NonNull
    @Override
    public MedicinesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.list_layout, null);
        return new MedicinesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicinesViewHolder medicinesViewHolder, int i) {

        Medicines medicines = medicines_list.get(i);
        medicinesViewHolder.medicine_name.setText(medicines.getName());
        medicinesViewHolder.medicine_note.setText(medicines.getNote());
    }

    @Override
    public int getItemCount() {
        return medicines_list.size();
    }

    class MedicinesViewHolder extends RecyclerView.ViewHolder {

        TextView medicine_name, medicine_note;
        View timeline;

        public MedicinesViewHolder(@NonNull View itemView) {

            super(itemView);
            medicine_name = itemView.findViewById(R.id.medicine_name);
            medicine_note = itemView.findViewById(R.id.medicine_note);
            timeline = itemView.findViewById(R.id.timeline);
        }
    }
}
