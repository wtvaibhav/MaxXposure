package com.maxxposure.app.model;

import java.util.ArrayList;

public class WorkflowDetails {

    private int workflowId;
    private String workflowDisplayImageUrl;
    private String workflowName;
    private String backgroundReplacement;
    private String enableTaggingOrHotspot;

    private ArrayList<ImageTypeSpin> imageTypeSpinArrayList;
    private ArrayList<ImageTypeStill> imageTypeStillArrayList;

    public WorkflowDetails(int workflowId, String workflowDisplayImageUrl, String workflowName, String backgroundReplacement, String enableTaggingOrHotspot, ArrayList<ImageTypeSpin> imageTypeSpinArrayList, ArrayList<ImageTypeStill> imageTypeStillArrayList) {
        this.workflowId = workflowId;
        this.workflowDisplayImageUrl = workflowDisplayImageUrl;
        this.workflowName = workflowName;
        this.backgroundReplacement = backgroundReplacement;
        this.enableTaggingOrHotspot = enableTaggingOrHotspot;
        this.imageTypeSpinArrayList = imageTypeSpinArrayList;
        this.imageTypeStillArrayList = imageTypeStillArrayList;
    }

    public int getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(int workflowId) {
        this.workflowId = workflowId;
    }

    public String getWorkflowDisplayImageUrl() {
        return workflowDisplayImageUrl;
    }

    public void setWorkflowDisplayImageUrl(String workflowDisplayImageUrl) {
        this.workflowDisplayImageUrl = workflowDisplayImageUrl;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    public String getBackgroundReplacement() {
        return backgroundReplacement;
    }

    public void setBackgroundReplacement(String backgroundReplacement) {
        this.backgroundReplacement = backgroundReplacement;
    }

    public String getEnableTaggingOrHotspot() {
        return enableTaggingOrHotspot;
    }

    public void setEnableTaggingOrHotspot(String enableTaggingOrHotspot) {
        this.enableTaggingOrHotspot = enableTaggingOrHotspot;
    }

    public ArrayList<ImageTypeSpin> getImageTypeSpinArrayList() {
        return imageTypeSpinArrayList;
    }

    public void setImageTypeSpinArrayList(ArrayList<ImageTypeSpin> imageTypeSpinArrayList) {
        this.imageTypeSpinArrayList = imageTypeSpinArrayList;
    }

    public ArrayList<ImageTypeStill> getImageTypeStillArrayList() {
        return imageTypeStillArrayList;
    }

    public void setImageTypeStillArrayList(ArrayList<ImageTypeStill> imageTypeStillArrayList) {
        this.imageTypeStillArrayList = imageTypeStillArrayList;
    }
}
