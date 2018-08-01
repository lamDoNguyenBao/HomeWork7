package com.example.lamdonguyenbao.homework7.activity;

import android.app.Dialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.lamdonguyenbao.homework7.R;
import com.example.lamdonguyenbao.homework7.adapter.ContactsAdapter;
import com.example.lamdonguyenbao.homework7.db.Database;
import com.example.lamdonguyenbao.homework7.model.Contacts;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ContactsAdapter.OnItemLongClickListener {
    private static final String COLUMNS_NAME = "name";
    private static final String COLUMNS_NUMBER = "number";

    RecyclerView lv_contact;
    Switch sw_view;
    ArrayList<Contacts> listContact;
    ContactsAdapter contactAdapter;
    Database database;
    MenuItem itemDelete,itemAdd;
    String sql = "";
    Toolbar toolbar;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        process();
    }

    private void initView() {
        listContact = new ArrayList<>();
        lv_contact = (RecyclerView) findViewById(R.id.lv_contact);
        sw_view = (Switch) findViewById(R.id.sw_view);
        database = new Database(this, "contact.sqlite", null, 1);

        lv_contact.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        lv_contact.setLayoutManager(layoutManager);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        contactAdapter = new ContactsAdapter(MainActivity.this,
                listContact);
        contactAdapter.setOnItemLongClickListener(this);
        lv_contact.setAdapter(contactAdapter);
        
        createTable();
        getDB();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        itemDelete = menu.findItem(R.id.btn_delete);
        itemAdd = menu.findItem(R.id.btn_add);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_add:
                getDialog();
                break;
            case R.id.btn_delete:
                for (Contacts contact : listContact) {
                    if (contact.isChecked()) {
                        sql = "delete from Contacts where " + COLUMNS_NAME + " ='" + contact.getName() + "' and " + COLUMNS_NUMBER + " ='" + contact.getNumber() + "'";
                        new SetDatabase().execute(sql);
                    }
                }
                hideButtonDelete();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     * get data from sqlite
     */
    private void getDB() {
        listContact.clear();
        sql = "select * from Contacts";
        Cursor dataContacts = database.getData(sql);
        while (dataContacts.moveToNext()) {
            String name = dataContacts.getString(dataContacts.getColumnIndex(COLUMNS_NAME));
            String number = dataContacts.getString(dataContacts.getColumnIndex(COLUMNS_NUMBER));
            Contacts contacts = new Contacts(name, number);
            listContact.add(contacts);
        }

        contactAdapter.notifyDataSetChanged();
    }

    /**
     * create table Contacts in sqlite
     */
    private void createTable() {
        sql = "CREATE TABLE IF NOT EXISTS Contacts(Id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMNS_NAME + " VARCHAR(200)," + COLUMNS_NUMBER + " VARCHAR(200) )";
        new SetDatabase().execute(sql);
//        database.queryData(sql);
    }

    @Override
    public void onItemClick(int position) {
        itemDelete.setVisible(true);
        itemAdd.setEnabled(false);
        listContact.get(position).setChecked(true);
        contactAdapter.notifyDataSetChanged();
    }

    /*
     * excute query in async task
     * */

    private class SetDatabase extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                database.queryData(strings[0]);
                return "true";
            } catch (Exception e) {
                return "false";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("true")) {
                getDB();
            } else {
                Toast.makeText(MainActivity.this, "Error when excute sql", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void process() {
        sw_view.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
                } else {
                    layoutManager = new GridLayoutManager(MainActivity.this, 2);
                }
                lv_contact.setLayoutManager(layoutManager);
            }
        });
    }

    /*
     *hide button delete
     */

    private void hideButtonDelete() {
        itemDelete.setVisible(false);
        itemAdd.setEnabled(true);
        for (Contacts item : listContact) {
            item.setChecked(false);
        }
        contactAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (itemDelete.isVisible()) {
            hideButtonDelete();
            return;
        } else {
            super.onBackPressed();
        }
    }

    /*
     *show dialog and set event for button in dialog
     */
    private void getDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_custom);
        final EditText et_name = (EditText) dialog.findViewById(R.id.et_name);
        final EditText et_number = (EditText) dialog.findViewById(R.id.et_number);
        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        Button btn_save = (Button) dialog.findViewById(R.id.btn_save);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et_name.getText().toString();
                String number = et_number.getText().toString();
                if ("".equals(name.trim()) || "".equals(number.trim())) {
                    Toast.makeText(MainActivity.this, "name and number can not be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                sql = "INSERT INTO Contacts VALUES(null,'" + name + "','" + number + "')";
                new SetDatabase().execute(sql);
                dialog.dismiss();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}
