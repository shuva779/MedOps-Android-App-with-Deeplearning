package com.istiaksaif.medops.Model;

public class DaySelectModel {

    private String text;
    private boolean isSelected = false;

    public DaySelectModel(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public boolean isSelected() {
        return isSelected;
    }
}
