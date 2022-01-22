package com.maxxposure.app.model;

import java.util.ArrayList;

public class WorkFlowData {
    private ArrayList<AllWorkflowDetails> allWorkflowDetails;

    public ArrayList<AllWorkflowDetails> getAllWorkflowDetails() {
        return allWorkflowDetails;
    }

    public void setAllWorkflowDetails(ArrayList<AllWorkflowDetails> allWorkflowDetails) {
        this.allWorkflowDetails = allWorkflowDetails;
    }


    public static class AllWorkflowDetails {
        private ImageRatio[] imageRatio;

        private String createdAt;

        private String backgroundReplacement;

        private ArrayList<ImageTypeSpin> imageTypeSpin;

        private DisplayOrder[] displayOrder;

        private String workflowName;

        private String enableTaggingOrHotspot;

        private String workflowId;

        private String workflowDisplayImageUrl;

        private ShootingOrder[] shootingOrder;

        private ArrayList<ImageTypeStill> imageTypeStill;

        private String updatedAt;

        public ImageRatio[] getImageRatio() {
            return imageRatio;
        }

        public void setImageRatio(ImageRatio[] imageRatio) {
            this.imageRatio = imageRatio;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getBackgroundReplacement() {
            return backgroundReplacement;
        }

        public void setBackgroundReplacement(String backgroundReplacement) {
            this.backgroundReplacement = backgroundReplacement;
        }

        public ArrayList<ImageTypeSpin> getImageTypeSpin() {
            return imageTypeSpin;
        }

        public void setImageTypeSpin(ArrayList<ImageTypeSpin> imageTypeSpin) {
            this.imageTypeSpin = imageTypeSpin;
        }

        public DisplayOrder[] getDisplayOrder() {
            return displayOrder;
        }

        public void setDisplayOrder(DisplayOrder[] displayOrder) {
            this.displayOrder = displayOrder;
        }

        public String getWorkflowName() {
            return workflowName;
        }

        public void setWorkflowName(String workflowName) {
            this.workflowName = workflowName;
        }

        public String getEnableTaggingOrHotspot() {
            return enableTaggingOrHotspot;
        }

        public void setEnableTaggingOrHotspot(String enableTaggingOrHotspot) {
            this.enableTaggingOrHotspot = enableTaggingOrHotspot;
        }

        public String getWorkflowId() {
            return workflowId;
        }

        public void setWorkflowId(String workflowId) {
            this.workflowId = workflowId;
        }

        public String getWorkflowDisplayImageUrl() {
            return workflowDisplayImageUrl;
        }

        public void setWorkflowDisplayImageUrl(String workflowDisplayImageUrl) {
            this.workflowDisplayImageUrl = workflowDisplayImageUrl;
        }

        public ShootingOrder[] getShootingOrder() {
            return shootingOrder;
        }

        public void setShootingOrder(ShootingOrder[] shootingOrder) {
            this.shootingOrder = shootingOrder;
        }

        public ArrayList<ImageTypeStill> getImageTypeStill() {
            return imageTypeStill;
        }

        public void setImageTypeStill(ArrayList<ImageTypeStill> imageTypeStill) {
            this.imageTypeStill = imageTypeStill;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public static class ShootingOrder {
            private String shootingOrderId;

            private String createdAt;

            private String shootingOrderImageUrl;

            private String updatedAt;

            public String getShootingOrderId() {
                return shootingOrderId;
            }

            public void setShootingOrderId(String shootingOrderId) {
                this.shootingOrderId = shootingOrderId;
            }

            public String getCreatedAt() {
                return createdAt;
            }

            public void setCreatedAt(String createdAt) {
                this.createdAt = createdAt;
            }

            public String getShootingOrderImageUrl() {
                return shootingOrderImageUrl;
            }

            public void setShootingOrderImageUrl(String shootingOrderImageUrl) {
                this.shootingOrderImageUrl = shootingOrderImageUrl;
            }

            public String getUpdatedAt() {
                return updatedAt;
            }

            public void setUpdatedAt(String updatedAt) {
                this.updatedAt = updatedAt;
            }

        }

        public static class DisplayOrder {
            private String createdAt;

            private String displayOrderImageUrl;

            private String displayOrderId;

            private String updatedAt;

            public String getCreatedAt() {
                return createdAt;
            }

            public void setCreatedAt(String createdAt) {
                this.createdAt = createdAt;
            }

            public String getDisplayOrderImageUrl() {
                return displayOrderImageUrl;
            }

            public void setDisplayOrderImageUrl(String displayOrderImageUrl) {
                this.displayOrderImageUrl = displayOrderImageUrl;
            }

            public String getDisplayOrderId() {
                return displayOrderId;
            }

            public void setDisplayOrderId(String displayOrderId) {
                this.displayOrderId = displayOrderId;
            }

            public String getUpdatedAt() {
                return updatedAt;
            }

            public void setUpdatedAt(String updatedAt) {
                this.updatedAt = updatedAt;
            }

        }


    }
}