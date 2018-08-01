package com.example.lamdonguyenbao.homework7.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.lamdonguyenbao.homework7.R;
import com.example.lamdonguyenbao.homework7.model.Contacts;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private Context context;
    private List<Contacts> listContacts;
    private OnItemLongClickListener mListener;

    public ContactsAdapter(Context context, List<Contacts> listContacts) {
        this.context = context;
        this.listContacts = listContacts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView= inflater.inflate(R.layout.inline_contact,parent,false);
        return new ViewHolder(itemView);
    }

    private boolean isChecked(){
        for (Contacts contact: listContacts) {
                if(contact.isChecked()){
                    return true;
                }
        }
        return false;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Contacts contact = listContacts.get(position);
        holder.tv_name.setText(contact.getName());
        holder.tv_number.setText(contact.getNumber());
        if(isChecked()){
            holder.cb_check.setVisibility(View.VISIBLE);
            if(listContacts.get(position).isChecked()){
                holder.cb_check.setChecked(true);
            }else{
                holder.cb_check.setChecked(false);
            }
        }else{
            holder.cb_check.setVisibility(View.INVISIBLE);
        }
        holder.cb_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                listContacts.get(position).setChecked(isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(listContacts == null){
            return 0;
        }else{
            return listContacts.size();
        }
    }

    public void setOnItemLongClickListener(@NonNull OnItemLongClickListener listener){
        this.mListener = listener;
    }

    public interface OnItemLongClickListener {
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        TextView tv_name,tv_number;
        CheckBox cb_check;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_number = (TextView) itemView.findViewById(R.id.tv_number);
            cb_check = (CheckBox) itemView.findViewById(R.id.cb_check);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            if(mListener != null){
                mListener.onItemClick(getAdapterPosition());
                Log.d("check", "onLongClick: "+getAdapterPosition());
            }
            return false;
        }
    }
}
