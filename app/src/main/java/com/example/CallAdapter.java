package com.example;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycallingapp.R;

import java.util.List;

public class CallAdapter extends RecyclerView.Adapter<CallAdapter.viewHolder> {


    List<User> list;
    Context context;
    CallClick callClick;

    CallAdapter(Context context,List<User> list,CallClick callClick){
        this.context=context;
        this.list=list;
        this.callClick=callClick;
    }



    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(context).inflate(R.layout.call_list,parent,false);
        return new viewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        User user=list.get(position);
        holder.PersonName.setText(user.Name);
        holder.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callClick.PersonToCall(position);
            }
        });

    }


   static class viewHolder extends RecyclerView.ViewHolder {

        TextView PersonName;
        ImageButton callButton;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            PersonName=itemView.findViewById(R.id.personName);
            callButton=itemView.findViewById(R.id.callButton);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface CallClick{
        void PersonToCall(int position);

    }
}
