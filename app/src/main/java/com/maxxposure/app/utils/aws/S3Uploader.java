package com.maxxposure.app.utils.aws;

import android.content.Context;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;

import java.io.File;

public class S3Uploader {

    private static final String TAG = "S3Uploader";

    private TransferUtility transferUtility;
    public S3UploadInterface s3UploadInterface;
    private String filePath;

    public S3Uploader(Context context) {
        transferUtility = AmazonUtil.getTransferUtility(context);

    }

    public void initUpload(String filePath) {
        this.filePath = filePath;
        File file = new File(filePath);
        ObjectMetadata myObjectMetadata = new ObjectMetadata();
        myObjectMetadata.setContentType("image/*");
        String mediaUrl = file.getName();
        Log.d(TAG, "initUpload: medua Url : "+mediaUrl);
        TransferObserver observer = transferUtility.upload(AWSKeys.BUCKET_NAME, mediaUrl,
                file, CannedAccessControlList.PublicRead);
        observer.setTransferListener(new UploadListener());
    }

    private class UploadListener implements TransferListener {

        @Override
        public void onError(int id, Exception e) {
            Log.e(TAG, "Error during upload: " + id, e);
            s3UploadInterface.onUploadError(e.toString());
            s3UploadInterface.onUploadError("Error");
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            Log.d(TAG, String.format("onProgressChanged: %d, total: %d, current: %d",
                    id, bytesTotal, bytesCurrent));
        }

        @Override
        public void onStateChanged(int id, TransferState newState) {
            Log.d(TAG, "onStateChanged: " + id + ", " + newState);
            if (newState == TransferState.COMPLETED) {
                s3UploadInterface.onUploadSuccess(filePath);
            }
        }
    }

    public void setOns3UploadDone(S3UploadInterface s3UploadInterface) {
        this.s3UploadInterface = s3UploadInterface;
    }

    public interface S3UploadInterface {
        void onUploadSuccess(String response);

        void onUploadError(String response);

    }
}
