package com.example.lamdonguyenbao.homework7.model;

public class Contacts {

    private String name;
    private String number;
    private boolean checked;

    public Contacts(String name, String number) {
        this.name = name;
        this.number = number;
        this.checked = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
