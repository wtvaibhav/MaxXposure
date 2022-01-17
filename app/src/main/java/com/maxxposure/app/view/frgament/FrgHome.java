package com.maxxposure.app.view.frgament;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.maxxposure.app.R;
import com.maxxposure.app.communication.ApiInterface;
import com.maxxposure.app.communication.Constants;
import com.maxxposure.app.communication.RetrofitBase;
import com.maxxposure.app.databinding.FrgHomeBinding;
import com.maxxposure.app.model.AllWorkFlowModel;
import com.maxxposure.app.model.YourVehicle_List;
import com.maxxposure.app.sharedpref.UserData;
import com.maxxposure.app.utils.CustomDialog;
import com.maxxposure.app.utils.CustomIntent;
import com.maxxposure.app.utils.CustomToast;
import com.maxxposure.app.utils.FileUtils;
import com.maxxposure.app.view.ScrRegNo;
import com.maxxposure.app.view.adapter.YourVehicleAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FrgHome extends Fragment implements View.OnClickListener {


    private String TAG = FrgHome.class.getName();

    String[] permissionArrays = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    public static final int RequestPermissionCode = 1;

    private ApiInterface apiInterface;
    private FrgHomeBinding binding;
    private YourVehicleAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = DataBindingUtil.inflate(
                inflater, R.layout.frg_home, container, false);
        View view = binding.getRoot();
        initUI();
        return view;
    }

    private void initUI() {
        apiInterface = RetrofitBase.getInstance().create(ApiInterface.class);

        binding.ivAddVehicle.setOnClickListener(this);
        binding.ivOpMenu.setOnClickListener(this);

        binding.llAll.setOnClickListener(this);
        binding.llCompleted.setOnClickListener(this);
        binding.llInProgress.setOnClickListener(this);

        binding.rvVehicles.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        adapter = new YourVehicleAdapter(this);
        binding.rvVehicles.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        getuserData();
        call_getAllWorkFlow();
    }

    public void getuserData() {
        Log.d(TAG, "getuserData: token : " + UserData.getInstance().getTOKEN());
        Log.d(TAG, "getuserData: user id : " + UserData.getInstance().getUSER_ID());
        Log.d(TAG, "getuserData: admin id : " + UserData.getInstance().getADMIN_ID());
    }

    public void call_getAllWorkFlow() {
        Call<String> call = apiInterface.getAllWorkFlow("Bearer " + UserData.getInstance().getTOKEN(), UserData.getInstance().getUSER_ID());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    Log.d(TAG, "onResponse: call_getAllWorkFlow code : " + response.code());
                    Log.d(TAG, "onResponse: call_getAllWorkFlow : " + response.body());
                    Log.d(TAG, "onResponse: call_getAllWorkFlow url : " + call.request().url().toString());

                    YourVehicle_List.getInstance().clearList();
                    JSONObject object;
                    switch (response.code()) {
                        case 200:
                            if (!response.isSuccessful()) {
                                return;
                            }

                            AllWorkFlowModel allWorkFlowModel = (AllWorkFlowModel) FileUtils.jsonToObject(response.body(), AllWorkFlowModel.class);
                            YourVehicle_List.getInstance().setList(allWorkFlowModel.getVehicleData());
                            Log.d(TAG, "onResponse: all count : " + YourVehicle_List.getInstance().getList().size());
                            Log.d(TAG, "onResponse: completed count : " + YourVehicle_List.getInstance().getCompletedVehicles().size());
                            Log.d(TAG, "onResponse: inprogress count : " + YourVehicle_List.getInstance().getInProgressVehicles().size());

                            setListingAapter(1);
                            break;

                        case 404:
                            CustomToast.showToast(getActivity(), "Vehicles Not Found");
                            break;

                        case 401:
                            CustomToast.showToast(getActivity(), "Unauthorised");
                            break;

                        default:
                            CustomToast.showToast(getActivity(), "Something went wrong please try again later.");
                            break;
                    }


                } catch (Exception exception) {
                    exception.getMessage();
                    Log.d(TAG, "onResponse: JSON Exception : " + exception.getMessage());
                }


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                showEmptyLayout();
            }
        });
    }

    public void showEmptyLayout() {
        if (adapter.getItemCount() > 0) {
            binding.llNoData.setVisibility(View.GONE);
        } else {
            binding.llNoData.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_add_vehicle:
                if ((ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(permissionArrays, RequestPermissionCode);
                        Log.d("TAG", "onClick: camera permission called");
                    }
                } else {
                    CustomIntent.startActivity(getActivity(), ScrRegNo.class, false);
                }
                break;

            case R.id.iv_op_menu:
                showDialogForOpMenu();
                break;

            case R.id.ll_all:
                setListingAapter(1);
                break;

            case R.id.ll_completed:
                setListingAapter(2);
                break;

            case R.id.ll_in_progress:
                setListingAapter(3);
                break;
        }
    }

    public void setListingAapter(int type) {
        switch (type) {
            case 1:
                binding.tvAllHeader.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                binding.tvCompletedHeader.setTextColor(getActivity().getResources().getColor(R.color.grey));
                binding.tvInProgressHeader.setTextColor(getActivity().getResources().getColor(R.color.grey));

                binding.tvAllCount.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                binding.tvCompletedCount.setTextColor(getActivity().getResources().getColor(R.color.grey));
                binding.tvInProgressCount.setTextColor(getActivity().getResources().getColor(R.color.grey));

                adapter.setList(YourVehicle_List.getInstance().getList());
                adapter.notifyDataSetChanged();
                break;

            case 2:
                binding.tvAllHeader.setTextColor(getActivity().getResources().getColor(R.color.grey));
                binding.tvCompletedHeader.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                binding.tvInProgressHeader.setTextColor(getActivity().getResources().getColor(R.color.grey));

                binding.tvAllCount.setTextColor(getActivity().getResources().getColor(R.color.grey));
                binding.tvCompletedCount.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                binding.tvInProgressCount.setTextColor(getActivity().getResources().getColor(R.color.grey));

                adapter.setList(YourVehicle_List.getInstance().getCompletedVehicles());
                adapter.notifyDataSetChanged();
                break;

            case 3:
                binding.tvAllHeader.setTextColor(getActivity().getResources().getColor(R.color.grey));
                binding.tvCompletedHeader.setTextColor(getActivity().getResources().getColor(R.color.grey));
                binding.tvInProgressHeader.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));

                binding.tvAllCount.setTextColor(getActivity().getResources().getColor(R.color.grey));
                binding.tvCompletedCount.setTextColor(getActivity().getResources().getColor(R.color.grey));
                binding.tvInProgressCount.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));

                adapter.setList(YourVehicle_List.getInstance().getInProgressVehicles());
                adapter.notifyDataSetChanged();
                break;
        }

        binding.tvAllCount.setText(YourVehicle_List.getInstance().getList().size() + "");
        binding.tvCompletedCount.setText(YourVehicle_List.getInstance().getCompletedVehicles().size() + "");
        binding.tvInProgressCount.setText(YourVehicle_List.getInstance().getInProgressVehicles().size() + "");
        showEmptyLayout();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Checking the request code of our request
        if (requestCode == 123) {
            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(getActivity(), "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(getActivity(), "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showDialogForOpMenu() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dlg_menu_refresh_delete_all);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        LinearLayout ll_refresh = dialog.findViewById(R.id.ll_refresh);
        LinearLayout ll_delete_all = dialog.findViewById(R.id.ll_delete_all);


        ll_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                call_getAllWorkFlow();
            }
        });
        ll_delete_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showDialogForDeleteAll();
            }
        });
        dialog.setCancelable(true);
        dialog.show();
    }


    private void showDialogForDeleteAll() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dlg_delete_all_vehicle);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        TextView tv_cancel = dialog.findViewById(R.id.tv_cancel);
        CardView cv_proceed = dialog.findViewById(R.id.cv_proceed);


        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
        cv_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                call_deleteAllVehicles();

            }
        });
        dialog.setCancelable(true);
        dialog.show();
    }

    public void call_deleteAllVehicles() {
        CustomDialog.showDialog(getActivity(), Constants.PROGRESS);
        Call<String> call = apiInterface.deleteAllVehicle("Bearer " + UserData.getInstance().getTOKEN(), UserData.getInstance().getUSER_ID());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    CustomDialog.closeDialog(getActivity());
                    Log.d(TAG, "onResponse: call_deleteAllVehicles code : " + response.code());
                    Log.d(TAG, "onResponse: call_deleteAllVehicles : " + response.body());
                    Log.d(TAG, "onResponse: call_deleteAllVehicles url : " + call.request().url().toString());

                    JSONObject object;
                    switch (response.code()) {
                        case 200:
                            if (!response.isSuccessful()) {
                                return;
                            }
                            object = new JSONObject(response.body());
                            YourVehicle_List.getInstance().clearList();
                            adapter.notifyDataSetChanged();
                            showEmptyLayout();
                            break;

                        case 404:
                            CustomToast.showToast(getActivity(), "Vehicles Not Found");
                            break;

                        case 401:
                            CustomToast.showToast(getActivity(), "Unauthorised");
                            break;

                        default:
                            CustomToast.showToast(getActivity(), "Something went wrong please try again later.");
                            break;
                    }

                } catch (JSONException exception) {
                    exception.getMessage();
                    Log.d(TAG, "onResponse: JSON Exception : " + exception.getMessage());
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "onFailure: call_deleteAllVehicles " + t.getMessage());
                CustomDialog.closeDialog(getActivity());
            }
        });
    }
}
