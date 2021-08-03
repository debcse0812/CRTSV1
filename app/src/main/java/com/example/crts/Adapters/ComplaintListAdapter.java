package com.example.crts.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crts.R;
import com.example.crts.complaintModel.ComplaintsModel;
import com.example.crts.interfaces.RecyclerViewClickListener;

import java.util.ArrayList;
import java.util.Random;

public class ComplaintListAdapter extends RecyclerView.Adapter<ComplaintListAdapter.MyViewHolder> {

    ArrayList<ComplaintsModel> arrayList;

    Context context;
    final private RecyclerViewClickListener clickListener;

    public ComplaintListAdapter(Context context, ArrayList<ComplaintsModel> arrayList, RecyclerViewClickListener clickListener) {
        this.arrayList = arrayList;
        this.context = context;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View pView = LayoutInflater.from(context).inflate(R.layout.single_complaint_item,parent,false );

        final MyViewHolder myViewHolder = new MyViewHolder(pView);

        int[] allColors = pView.getResources().getIntArray(R.array.allcolors);
        int boxColor = allColors[new Random().nextInt(allColors.length)];

        myViewHolder.accordian_title.setBackgroundColor(boxColor);

        myViewHolder.arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myViewHolder.accordian_body.getVisibility() == View.VISIBLE){
                    myViewHolder.accordian_body.setVisibility(View.GONE);
                }else {
                    myViewHolder.accordian_body.setVisibility(View.VISIBLE);
                }
            }
        });

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintListAdapter.MyViewHolder holder, int position) {
        String cid = arrayList.get(position).getCid();
        final String registered_by = arrayList.get(position).getName();
        final String registration_date = arrayList.get(position).getDate();
        final String complaint_type = arrayList.get(position).getC_type();
        final String complaint_detail = arrayList.get(position).getC_detail();
        final String complaint_status = arrayList.get(position).getStatus();
        final String address = arrayList.get(position).getAddress();
        // hashed the cid using a hash function:
        cid = hashCID(cid);

        holder.cid.setText("Complaint ID: " + cid);
        holder.registration_date.setText("Registered On: " + registration_date);
        holder.complaint_type.setText("Complaint Type: " + complaint_type);
        holder.complaint_detail.setText("Complaint Detail: " + complaint_detail);
        holder.complaint_status.setText("Status: " + complaint_status); // we can update color etc depending on the compliant status.
        holder.registered_by.setText("Registered By: " + registered_by);
        holder.address.setText("Address: " + address);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        CardView accordian_title;
        TextView cid, registration_date, complaint_type, complaint_detail, complaint_status, registered_by, address;
        RelativeLayout accordian_body;
        ImageView arrow, deleteBtn, editBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            accordian_title = itemView.findViewById(R.id.accordian_title);
            cid = itemView.findViewById(R.id.cid);
            registration_date = itemView.findViewById(R.id.registration_date);
            complaint_type = itemView.findViewById(R.id.complaint_type);
            complaint_detail = itemView.findViewById(R.id.complaint_detail);
            complaint_status = itemView.findViewById(R.id.complaint_status);
            registered_by = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            accordian_body = itemView.findViewById(R.id.accordian_body);

            arrow = itemView.findViewById(R.id.arrow);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            editBtn = itemView.findViewById(R.id.editBtn);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onItemClick(getAdapterPosition());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    clickListener.onLongItemClick(getAdapterPosition());
                    return true;
                }
            });
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.editButtonClick(getAdapterPosition());
                }
            });
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.deleteButtonClick(getAdapterPosition());
                }
            });
        }
    }
    private static String hashCID(String cid){
        int mod = 100003;
        int hash = 7;
        for (int i = 0; i < cid.length(); i++) {
            hash = ((hash * 31) + cid.charAt(i)) % mod;
        }

        String hashVal = String.format("%05d",hash);
        return hashVal;
    }
}
