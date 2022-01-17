package com.maxxposure.app.model;

import java.util.ArrayList;

public class PostVehicleData {
    private ArrayList<UserSpinImage> userSpinImage;

    private ArrayList<UserStillImage> userStillImage;

    private String vehicleWorkFlowImageUrl;

    private String vehicleVINNumber;

    private String vehicleRegNumber;

    private String vehicleStatus;

    public ArrayList<UserSpinImage> getUserSpinImage() {
        return userSpinImage;
    }

    public void setUserSpinImage(ArrayList<UserSpinImage> userSpinImage) {
        this.userSpinImage = userSpinImage;
    }

    public ArrayList<UserStillImage> getUserStillImage() {
        return userStillImage;
    }

    public void setUserStillImage(ArrayList<UserStillImage> userStillImage) {
        this.userStillImage = userStillImage;
    }

    public String getVehicleWorkFlowImageUrl() {
        return vehicleWorkFlowImageUrl;
    }

    public void setVehicleWorkFlowImageUrl(String vehicleWorkFlowImageUrl) {
        this.vehicleWorkFlowImageUrl = vehicleWorkFlowImageUrl;
    }

    public String getVehicleVINNumber() {
        return vehicleVINNumber;
    }

    public void setVehicleVINNumber(String vehicleVINNumber) {
        this.vehicleVINNumber = vehicleVINNumber;
    }

    public String getVehicleRegNumber() {
        return vehicleRegNumber;
    }

    public void setVehicleRegNumber(String vehicleRegNumber) {
        this.vehicleRegNumber = vehicleRegNumber;
    }

    public String getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(String vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

  public static  class UserStillImage {

        private String imageUrl;

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

    }

    public  static class UserSpinImage {

        private String imageUrl ="testing";


        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }


}
