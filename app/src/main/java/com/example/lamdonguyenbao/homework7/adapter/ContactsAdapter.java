package com.example.lamdonguyenbao.homework7.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.lamdonguyenbao.homework7.R;
import com.example.lamdonguyenbao.homework7.model.Contacts;

import java.util.List;

public class ContactsAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<Contacts> listContacts;
    private boolean checked;


    public ContactsAdapter(Context context, int layout, List<Contacts> listContacts,boolean checked) {
        this.context = context;
        this.layout = layout;
        this.listContacts = listContacts;
        this.checked = checked;
    }

    @Override
    public int getCount() {
        if(listContacts == null){
            return 0;
        }else{
            return listContacts.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class Holder{
        TextView tv_name,tv_number;
        CheckBox cb_check;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView == null){
            holder = new Holder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(layout,null);

            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_number = (TextView) convertView.findViewById(R.id.tv_number);
            holder.cb_check = (CheckBox) convertView.findViewById(R.id.cb_check);

            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
        }

        Contacts contact = listContacts.get(position);
        holder.tv_name.setText(contact.getName());
        holder.tv_number.setText(contact.getNumber());
        if(checked){
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
        return convertView;
    }
}
