package com.example.lamdonguyenbao.homework7.activity;

import android.app.Dialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lamdonguyenbao.homework7.R;
import com.example.lamdonguyenbao.homework7.adapter.ContactsAdapter;
import com.example.lamdonguyenbao.homework7.db.Database;
import com.example.lamdonguyenbao.homework7.model.Contacts;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String COLUMNS_NAME = "name";
    private static final String COLUMNS_NUMBER = "number";

    ListView lv_contact;
    ArrayList<Contacts> listContact;
    ContactsAdapter contactAdapter;
    Database database;
    boolean checked = false;
    MenuItem itemRecycle;
    String sql="";
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        process();
    }

    private void initView() {
        lv_contact = (ListView) findViewById(R.id.lv_contact);
        database = new Database(this, "contact.sqlite", null, 1);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        createTable();
        getDB();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        itemRecycle = menu.findItem(R.id.btn_delete);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.btn_add:
                getDialog();
                break;
            case R.id.btn_delete:
                for(Contacts contact : listContact){
                    if(contact.isChecked()){
                        Log.d("check", "onClick: "+contact.getName());
                        sql ="delete from Contacts where "+COLUMNS_NAME+" ='"+contact.getName()+"' and "+COLUMNS_NUMBER+" ='"+contact.getNumber()+"'";
                        new SetDatabase().execute(sql);
                    }
                }
                if(listContact.size() ==0){
                    hideButtonDelete();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    * get data from sqlite
    */
    private void getDB(){
        listContact = new ArrayList<>();
        sql = "select * from Contacts";
        Cursor dataContacts = database.getData(sql);
        while (dataContacts.moveToNext()){
            String name = dataContacts.getString(dataContacts.getColumnIndex(COLUMNS_NAME));
            String number = dataContacts.getString(dataContacts.getColumnIndex(COLUMNS_NUMBER));
            Contacts contacts = new Contacts(name,number);
            listContact.add(contacts);
        }
        contactAdapter = new ContactsAdapter(MainActivity.this,
                R.layout.inline_contact,
                listContact,checked);
        lv_contact.setAdapter(contactAdapter);
    }

    /**
     * create table Contacts in sqlite
     * */
    private void createTable(){
        sql ="CREATE TABLE IF NOT EXISTS Contacts(Id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMNS_NAME+" VARCHAR(200),"+COLUMNS_NUMBER+" VARCHAR(200) )";
        new SetDatabase().execute(sql);
//        database.queryData(sql);
    }

    /*
    * excute query in async task
    * */

    private class SetDatabase extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... strings) {
            try {
                database.queryData(strings[0]);
                return "true";
            }catch (Exception e){
                return "false";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("true")){
                getDB();
            }else{
                Toast.makeText(MainActivity.this,"Error when excute sql",Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void process() {
        lv_contact.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                checked = true;
//                btn_delete.setVisibility(View.VISIBLE);
                itemRecycle.setVisible(true);
                contactAdapter = new ContactsAdapter(MainActivity.this,
                        R.layout.inline_contact,
                        listContact,checked);
                lv_contact.setAdapter(contactAdapter);
                listContact.get(position).setChecked(true);
                return false;
            }
        });
    }

    /*
     *hide button delete
     */

    private void hideButtonDelete(){
        checked = false;
//        btn_delete.setVisibility(View.INVISIBLE);
        itemRecycle.setVisible(false);
        for(Contacts item:listContact){
            item.setChecked(false);
        }
        contactAdapter = new ContactsAdapter(MainActivity.this,
                R.layout.inline_contact,
                listContact,checked);
        lv_contact.setAdapter(contactAdapter);
    }

    @Override
    public void onBackPressed() {
        if(checked){
            hideButtonDelete();
            return;
        }else{
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
        Button btn_cancel= (Button) dialog.findViewById(R.id.btn_cancel);
        Button btn_save = (Button) dialog.findViewById(R.id.btn_save);


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et_name.getText().toString();
                String number = et_number.getText().toString();
                if("".equals(name.trim()) ||"".equals(number.trim())){
                    Toast.makeText(MainActivity.this,"name and number can not be empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                sql = "INSERT INTO Contacts VALUES(null,'"+name+"','"+number+"')";
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
