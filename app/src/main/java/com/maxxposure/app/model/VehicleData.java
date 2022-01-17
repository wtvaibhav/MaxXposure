package com.maxxposure.app.model;

import java.util.ArrayList;

public class VehicleData {
    private String createdAt;
    private ArrayList<VehicleData.UserSpinImage> userSpinImage;
    private ArrayList<VehicleData.UserStillImage> userStillImage;
    private String vehicleWorkFlowImageUrl;

    private String vehicleId;

    private String vehicleVINNumber;

    private String vehicleRegNumber;

    private String vehicleStatus;

    private String updatedAt;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public ArrayList<VehicleData.UserSpinImage> getUserSpinImage() {
        return userSpinImage;
    }

    public void setUserSpinImage(ArrayList<VehicleData.UserSpinImage> userSpinImage) {
        this.userSpinImage = userSpinImage;
    }

    public ArrayList<VehicleData.UserStillImage> getUserStillImage() {
        return userStillImage;
    }

    public void setUserStillImage(ArrayList<VehicleData.UserStillImage> userStillImage) {
        this.userStillImage = userStillImage;
    }

    public String getVehicleWorkFlowImageUrl() {
        return vehicleWorkFlowImageUrl;
    }

    public void setVehicleWorkFlowImageUrl(String vehicleWorkFlowImageUrl) {
        this.vehicleWorkFlowImageUrl = vehicleWorkFlowImageUrl;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
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

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }


    public static class UserStillImage {
        private String userWorkflowStillId;

        private String createdAt;

        private String imageUrl;

        private String updatedAt;

        public String getUserWorkflowStillId() {
            return userWorkflowStillId;
        }

        public void setUserWorkflowStillId(String userWorkflowStillId) {
            this.userWorkflowStillId = userWorkflowStillId;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }
    }


    public static class UserSpinImage {
        private String userWorkflowSpinId;

        private String createdAt;

        private String imageUrl;

        private String updatedAt;

        public String getUserWorkflowSpinId() {
            return userWorkflowSpinId;
        }

        public void setUserWorkflowSpinId(String userWorkflowSpinId) {
            this.userWorkflowSpinId = userWorkflowSpinId;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

    }

}
