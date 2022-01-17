package com.maxxposure.app.view.adapter;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.maxxposure.app.R;
import com.maxxposure.app.communication.ApiInterface;
import com.maxxposure.app.communication.Constants;
import com.maxxposure.app.communication.RetrofitBase;
import com.maxxposure.app.model.VehicleData;
import com.maxxposure.app.sharedpref.UserData;
import com.maxxposure.app.utils.CustomDialog;
import com.maxxposure.app.utils.CustomToast;
import com.maxxposure.app.view.frgament.FrgHome;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class YourVehicleAdapter extends RecyclerView.Adapter<YourVehicleAdapter.MyViewHolder> {

    private String TAG = YourVehicleAdapter.class.getName();
    private ApiInterface apiInterface;
    private ArrayList<VehicleData> list;
    private FrgHome frg;
    private CategoryClickHandler categoryClickHandler;


    public YourVehicleAdapter(FrgHome activity) {
        this.frg = activity;
    }

    public void initCategoryClickHandler(CategoryClickHandler categoryClickHandler) {
        this.categoryClickHandler = categoryClickHandler;
    }

    public void setList(ArrayList<VehicleData> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public YourVehicleAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(frg.getActivity());
        view = inflater.inflate(R.layout.item_your_vehicles_home, parent, false);
        apiInterface = RetrofitBase.getInstance().create(ApiInterface.class);
        return new YourVehicleAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final YourVehicleAdapter.MyViewHolder holder, final int position) {
        final VehicleData cat = list.get(position);
        ArrayList<VehicleData.UserStillImage> userStillImages = cat.getUserStillImage();
        if (userStillImages != null && userStillImages.size() > 0) {
            Picasso.with(frg.getActivity()).load(userStillImages.get(0).getImageUrl())
                    .placeholder(frg.getResources().getDrawable(R.drawable.ic_car_demo))
                    .error(frg.getResources().getDrawable(R.drawable.ic_car_demo))
                    .into(holder.iv_car);

        }
        Picasso.with(frg.getActivity()).load(cat.getVehicleWorkFlowImageUrl())
                .placeholder(frg.getResources().getDrawable(R.drawable.ic_car_demo))
                .error(frg.getResources().getDrawable(R.drawable.ic_car_demo))
                .into(holder.iv_logo);

        holder.tv_title.setText(cat.getVehicleVINNumber());
        holder.tv_sub_title.setText(cat.getVehicleRegNumber());
        holder.tv_status.setText(cat.getVehicleStatus() + "");


        holder.iv_delete.setTag(cat);
        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VehicleData vehicle = (VehicleData) v.getTag();
                Log.d(TAG, "onClick: vehicle ID : " + vehicle.getVehicleId());
                showDialog(position, Integer.parseInt(vehicle.getVehicleId()));
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_car;
        private ImageView iv_logo;
        private ImageView iv_delete;
        private TextView tv_title;
        private TextView tv_status;
        private TextView tv_sub_title;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_car = itemView.findViewById(R.id.iv_car);
            iv_logo = itemView.findViewById(R.id.iv_logo);
            iv_delete = itemView.findViewById(R.id.iv_delete);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_status = itemView.findViewById(R.id.tv_status);
            tv_sub_title = itemView.findViewById(R.id.tv_sub_title);
        }
    }

    public interface CategoryClickHandler {

        void OnCategoryClickListner(int cat_id);
    }

    private void showDialog(int position, int vehicle_id) {

        final Dialog dialog = new Dialog(frg.getActivity());
        dialog.setContentView(R.layout.dlg_delete_vehicle);
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
                call_deleteVehicles(position, vehicle_id);
            }
        });
        dialog.setCancelable(true);
        dialog.show();
    }

    public void call_deleteVehicles(int position, int vehicle_id) {
        CustomDialog.showDialog(frg.getActivity(), Constants.PROGRESS);
        Call<String> call = apiInterface.deleteVehicle("Bearer " + UserData.getInstance().getTOKEN(), vehicle_id + "");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    CustomDialog.closeDialog(frg.getActivity());
                    Log.d(TAG, "onResponse: call_deleteVehicles code : " + response.code());
                    Log.d(TAG, "onResponse: call_deleteVehicles : " + response.body());
                    Log.d(TAG, "onResponse: call_deleteVehicles url : " + call.request().url().toString());
                    JSONObject object;
                    switch (response.code()) {
                        case 200:
                            if (!response.isSuccessful()) {
                                return;
                            }
                            object = new JSONObject(response.body());
                            String message = object.getString("message");
                            CustomToast.showToast(frg.getActivity(), message);
                            if (list.size() > 0) {
                                list.remove(position);
                                notifyDataSetChanged();
                                frg.showEmptyLayout();
                            }

                            break;

                        case 404:
                            CustomToast.showToast(frg.getActivity(), "Vehicles Not Found");
                            break;

                        case 401:
                            CustomToast.showToast(frg.getActivity(), "Unauthorised");
                            break;
                        case 500:
                            CustomToast.showToast(frg.getActivity(), "Unauthorised");
                            break;

                        default:
                            CustomToast.showToast(frg.getActivity(), "Something went wrong please try again later.");
                            break;
                    }

                } catch (JSONException exception) {
                    exception.getMessage();
                    Log.d(TAG, "onResponse: JSON Exception : " + exception.getMessage());
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "onFailure: call_deleteVehicles " + t.getMessage());
                CustomDialog.closeDialog(frg.getActivity());
            }
        });
    }

}
