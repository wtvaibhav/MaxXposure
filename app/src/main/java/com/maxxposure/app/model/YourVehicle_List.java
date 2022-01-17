package com.maxxposure.app.model;

import com.maxxposure.app.communication.Constants;

import java.util.ArrayList;

public class YourVehicle_List {

    private ArrayList<VehicleData> list = new ArrayList<>();
    private VehicleData category;
    private static YourVehicle_List _instance = null;

    private YourVehicle_List() {

    }

    public static YourVehicle_List getInstance() {
        if (_instance == null) {
            _instance = new YourVehicle_List();
        }
        return _instance;
    }

    public void setList(ArrayList<VehicleData> list) {
        this.list = list;
    }

    public void add(VehicleData category) {
        list.add(category);
    }

    public ArrayList<VehicleData> getList() {
        return list;
    }


    public ArrayList<VehicleData> getCompletedVehicles() {
        ArrayList<VehicleData> completed_list = new ArrayList<>();
        for (VehicleData VehicleData : list) {

            if (VehicleData.getVehicleStatus().equals(Constants.COMPLETED)) {
                completed_list.add(VehicleData);
            }
        }
        return completed_list;
    }

    public ArrayList<VehicleData> getInProgressVehicles() {
        ArrayList<VehicleData> progreess_list = new ArrayList<>();
        for (VehicleData VehicleData : list) {

            if (VehicleData.getVehicleStatus().equals(Constants.INPROGRESS)) {
                progreess_list.add(VehicleData);
            }
        }
        return progreess_list;
    }


    public int getVehicleDataId(String VINumbe) {
        for (VehicleData category : list) {
            if (category.getVehicleVINNumber().equals(VINumbe)) {
                return Integer.parseInt(category.getVehicleId());
            }
        }
        return -1;
    }


    public VehicleData getSelectedVehicleDataID() {
        return category;
    }

    public void setSelectedVehicleDataID(VehicleData category) {
        this.category = category;
    }

    public void clearList() {
        if (list.size() > 0) {
            list.clear();
        }
    }
}
