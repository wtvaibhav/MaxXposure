package com.maxxposure.app.model;

import java.util.ArrayList;

public class WorkflowDetails_List {

    private ArrayList<WorkflowDetails> list = new ArrayList<>();
    private WorkflowDetails category;
    private static WorkflowDetails_List _instance = null;

    private WorkflowDetails_List() {

    }

    public static WorkflowDetails_List getInstance() {
        if (_instance == null) {
            _instance = new WorkflowDetails_List();
        }
        return _instance;
    }

    public void add(WorkflowDetails category) {
        list.add(category);
    }

    public ArrayList<WorkflowDetails> getList() {
        return list;
    }

    public ArrayList<ImageTypeSpin> getSpinImages() {
        ArrayList<ImageTypeSpin> imageTypeSpin = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            WorkflowDetails workflowDetails = list.get(i);
            imageTypeSpin.add(workflowDetails.getImageTypeSpinArrayList().get(i));
        }
        return  imageTypeSpin;
    }

    public ArrayList<ImageTypeStill> getStilImages() {
        ArrayList<ImageTypeStill> imageTypeSpin = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            WorkflowDetails workflowDetails = list.get(i);
            imageTypeSpin.add(workflowDetails.getImageTypeStillArrayList().get(i));
        }
        return imageTypeSpin;
    }

    public WorkflowDetails getSelected() {
        return category;
    }

    public void setSelected(WorkflowDetails category) {
        this.category = category;
    }

    public void clearList() {
        if (list.size() > 0) {
            list.clear();
        }
    }
}
