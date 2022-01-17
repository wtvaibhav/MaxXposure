package com.maxxposure.app.sharedpref;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.maxxposure.app.MaxXposureApplication;
import com.maxxposure.app.model.DamagePoint;
import com.maxxposure.app.model.ImageTypeSpin;
import com.maxxposure.app.model.ImageTypeStill;
import com.maxxposure.app.model.WorkFlowData;
import com.maxxposure.app.utils.FileUtils;

import java.util.ArrayList;
import java.util.Arrays;


public class VehicleFormData {

    private static VehicleFormData _instance = null;
    private static SharedPreferences _sharedPreferences = null;
    private static Editor _sharedPrefEditor = null;
    private final String SHARED_PREFERENCE_NAME = "vehicle_data";


    //for reg screen step 1
    private final String REG_NO = "REG_NO";
    //for vin number step 2
    private final String VIN_NO = "VIN_NO";
    private final String VIN_IMAGE_PATH = "VIN_IMAGE_PATH";
    //for workflow step 3
    private final String BRAND_NAME = "BRAND_NAME";
    private final String BRAND_NAME_IMAGE_URL = "BRAND_NAME_IMAGE_URL";
    //for still images 4
    private final String SIDE_IMAGE_URL = "SIDE_IMAGE_URL";
    private final String REAR_IMAGE_URL = "REAR_IMAGE_URL";
    private final String DASHBOARD_IMAGE_URL = "DASHBOARD_IMAGE_URL";
    private final String INTERIOR_IMAGE_URL = "INTERIOR_IMAGE_URL";
    private final String FRONT_IMAGE_URL = "FRONT_IMAGE_URL";
    private final String FRONT2_IMAGE_URL = "FRONT2_IMAGE_URL";
    private final String DAMAGE_POINTS = "DAMAGE_POINTS";


    private VehicleFormData() {

    }

    public static VehicleFormData getInstance() {
        if (_instance == null) {
            _instance = new VehicleFormData();
            _instance._initSharedPreferences();
        }
        return _instance;
    }

    /**
     * This method is used to initialized {@link SharedPreferences} and
     * {@link Editor}
     */
    public void _initSharedPreferences() {
        _sharedPreferences = _getSharedPref();
        _sharedPrefEditor = _getSharedPrefEditor();
    }

    /**
     * Method to get the SharedPreferences.
     *
     * @return the {@link SharedPreferences} object.
     */
    private SharedPreferences _getSharedPref() {
        if (_sharedPreferences == null) {
            _sharedPreferences = MaxXposureApplication.appContext.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        }
        return _sharedPreferences;
    }

    /**
     * Method to get the {@link Editor} for writing values to {@link SharedPreferences}.
     *
     * @return the {@link Editor} object.
     */
    private Editor _getSharedPrefEditor() {
        if (_sharedPrefEditor == null) {
            _sharedPrefEditor = _getSharedPref().edit();
        }
        return _sharedPrefEditor;
    }


    public void setREG_NO(String token) {
        _getSharedPrefEditor().putString(REG_NO, token).commit();
    }

    public String getREG_NO() {
        return _getSharedPref().getString(REG_NO, null);
    }

    public void setVIN_NO(String token) {
        _getSharedPrefEditor().putString(VIN_NO, token).commit();
    }

    public String getVIN_NO() {
        return _getSharedPref().getString(VIN_NO, null);
    }

    public void setVIN_IMAGE_PATH(String token) {
        _getSharedPrefEditor().putString(VIN_IMAGE_PATH, token).commit();
    }

    public String getVIN_IMAGE_PATH() {
        return _getSharedPref().getString(VIN_IMAGE_PATH, null);
    }

    public void setBRAND_NAME_ID(int token) {
        _getSharedPrefEditor().putInt(BRAND_NAME, token).commit();
    }

    public int getBRAND_NAME_ID() {
        return _getSharedPref().getInt(BRAND_NAME, -1);
    }

    public void setBRAND_NAME_IMAGE(String token) {
        _getSharedPrefEditor().putString(BRAND_NAME_IMAGE_URL, token).commit();
    }

    public String getBRAND_NAME_IMAGE() {
        return _getSharedPref().getString(BRAND_NAME_IMAGE_URL, null);
    }

    public void setSIDE_IMAGE_URL(String token) {
        _getSharedPrefEditor().putString(SIDE_IMAGE_URL, token).commit();
    }

    public String getSIDE_IMAGE_URL() {
        return _getSharedPref().getString(SIDE_IMAGE_URL, null);
    }

    public void setREAR_IMAGE_URL(String token) {
        _getSharedPrefEditor().putString(REAR_IMAGE_URL, token).commit();
    }

    public String getREAR_IMAGE_URL() {
        return _getSharedPref().getString(REAR_IMAGE_URL, null);
    }

    public void setDASHBOARD_IMAGE_URL(String token) {
        _getSharedPrefEditor().putString(DASHBOARD_IMAGE_URL, token).commit();
    }

    public String getDASHBOARD_IMAGE_URL() {
        return _getSharedPref().getString(DASHBOARD_IMAGE_URL, null);
    }

    public void setINTERIOR_IMAGE_URL(String token) {
        _getSharedPrefEditor().putString(INTERIOR_IMAGE_URL, token).commit();
    }

    public String getINTERIOR_IMAGE_URL() {
        return _getSharedPref().getString(INTERIOR_IMAGE_URL, null);
    }

    public void setFRONT_IMAGE_URL(String token) {
        _getSharedPrefEditor().putString(FRONT_IMAGE_URL, token).commit();
    }

    public String getFRONT_IMAGE_URL() {
        return _getSharedPref().getString(FRONT_IMAGE_URL, null);
    }

    public void setFRONT2_IMAGE_URL(String token) {
        _getSharedPrefEditor().putString(FRONT2_IMAGE_URL, token).commit();
    }

    public String getFRONT2_IMAGE_URL() {
        return _getSharedPref().getString(FRONT2_IMAGE_URL, null);
    }

    public void setDamagePoints(DamagePoint point) {
        String damagePoints = getDamagePoints();
        if (TextUtils.isEmpty(damagePoints)) {
            DamagePoint damagePointArr[] = new DamagePoint[1];
            damagePointArr[0] = point;
            String data = FileUtils.getJsonFromObject(damagePointArr);
            _getSharedPrefEditor().putString(DAMAGE_POINTS, data).commit();
        } else {
            DamagePoint damagePointArr[] = (DamagePoint[]) FileUtils.jsonToObject(damagePoints, DamagePoint[].class);
            ArrayList<DamagePoint> list = new ArrayList<>(Arrays.asList(damagePointArr));
            list.add(point);
            damagePointArr = (DamagePoint[]) list.toArray(new DamagePoint[list.size()]);
            String data = FileUtils.getJsonFromObject(damagePointArr);
            _getSharedPrefEditor().putString(DAMAGE_POINTS, data).commit();
        }
    }

    public String getDamagePoints() {
        return _getSharedPref().getString(DAMAGE_POINTS, "");
    }

    public DamagePoint getDamagePoint(float dX, float dY) {
        DamagePoint retPoint = null;
        float upX = dX + 25;
        float downX = dX - 25;

        float upY = dY + 25;
        float downY = dY - 25;
        String data = getDamagePoints();
        if (TextUtils.isEmpty(data)) {
            return null;
        }
        DamagePoint damagePointArr[] = (DamagePoint[]) FileUtils.jsonToObject(data, DamagePoint[].class);
        for (DamagePoint point : damagePointArr) {
            if ((point.getX() > downX && point.getX() < upX) && (point.getY() > downY && point.getY() < upY)) {
                retPoint = point;
                break;
            }

        }

        return retPoint;
    }


    public void clearData() {
        _getSharedPrefEditor().clear();
        _getSharedPrefEditor().commit();
    }

    private WorkFlowData workFlowData;

    public WorkFlowData getWorkFlowData() {
        return workFlowData;
    }

    public void setWorkFlowData(WorkFlowData workFlowData) {
        this.workFlowData = workFlowData;
    }


    public ArrayList<ImageTypeSpin> getImageTypeSpins(int workFlowId) {
        ArrayList<WorkFlowData.AllWorkflowDetails> list = workFlowData.getAllWorkflowDetails();
        for (WorkFlowData.AllWorkflowDetails allWorkflowDetails : list) {
            if (allWorkflowDetails.getWorkflowId().equals(String.valueOf(workFlowId))) {
                return allWorkflowDetails.getImageTypeSpin();
            }
        }
        return null;

    }

    public ArrayList<ImageTypeStill> getImageTypeStills(int workFlowId) {
        ArrayList<WorkFlowData.AllWorkflowDetails> list = workFlowData.getAllWorkflowDetails();
        for (WorkFlowData.AllWorkflowDetails allWorkflowDetails : list) {
            if (allWorkflowDetails.getWorkflowId().equals(String.valueOf(workFlowId))) {
                return allWorkflowDetails.getImageTypeStill();
            }
        }
        return null;

    }
}
