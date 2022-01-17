package com.maxxposure.app.view.custom;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.maxxposure.app.R;


public class SupportBottomSheetDialog extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;

    public SupportBottomSheetDialog(BottomSheetListener listener) {
        this.mListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dlg_bottom_camera_gallery, container, false);
        ImageView iv_camera = v.findViewById(R.id.iv_camera);
        ImageView iv_gallery = v.findViewById(R.id.iv_gallery);

        iv_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(1);
                dismiss();
            }
        });
        iv_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(2);
                dismiss();
            }
        });
        return v;
    }


    public interface BottomSheetListener {
        void onButtonClicked(int button_type);
    }


}
