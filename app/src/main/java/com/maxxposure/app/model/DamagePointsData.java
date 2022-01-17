package com.maxxposure.app.model;

import java.util.ArrayList;

public class DamagePointsData {
    private String screenSize;
    private String previewSize;
    private ArrayList<DamagePoint> damagepoints;

    public String getScreenSize() {
        return screenSize;
    }

    public void setScreenSize(String screenSize) {
        this.screenSize = screenSize;
    }

    public String getPreviewSize() {
        return previewSize;
    }

    public void setPreviewSize(String previewSize) {
        this.previewSize = previewSize;
    }

    public ArrayList<DamagePoint> getDamagepoints() {
        return damagepoints;
    }

    public void setDamagepoints(ArrayList<DamagePoint> damagepoints) {
        this.damagepoints = damagepoints;
    }
}
