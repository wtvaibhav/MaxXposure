package com.maxxposure.app.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.maxxposure.app.R;
import com.maxxposure.app.model.WorkFlowData;
import com.maxxposure.app.model.WorkflowDetails;
import com.maxxposure.app.view.ScrChooseWorkFlow;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.MyViewHolder> {

    private ArrayList<WorkFlowData.AllWorkflowDetails> list;
    private ScrChooseWorkFlow context;
    private brandClickHandler brandClickHandler;

    public static int selected_id = -1;


    public BrandAdapter(ScrChooseWorkFlow activity) {
        this.context = activity;
    }

    public void initBrandClickHandler(brandClickHandler brandClickHandler) {
        this.brandClickHandler = brandClickHandler;
    }

    public void setList(ArrayList<WorkFlowData.AllWorkflowDetails> list, int selected_id) {
        this.list = list;
        BrandAdapter.selected_id = selected_id;
    }

    @NonNull
    @Override
    public BrandAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.item_brand, parent, false);
        return new BrandAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final WorkFlowData.AllWorkflowDetails cat = list.get(position);
        Picasso.with(context).load(cat.getWorkflowDisplayImageUrl())
                .placeholder(context.getResources().getDrawable(R.drawable.ic_serinity_2x))
                .error(context.getResources().getDrawable(R.drawable.ic_serinity_2x))
                .into(holder.iv_brand);

        if (selected_id == Integer.parseInt(cat.getWorkflowId())) {
            holder.cv_green_bg.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        } else {
            holder.cv_green_bg.setCardBackgroundColor(context.getResources().getColor(R.color.input_bg));
        }

        holder.cv_green_bg.setTag(cat);
        holder.cv_green_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WorkFlowData.AllWorkflowDetails categories = ( WorkFlowData.AllWorkflowDetails) v.getTag();
                brandClickHandler.OnBrandClickListner(Integer.parseInt(categories.getWorkflowId()), categories.getWorkflowDisplayImageUrl());
                selected_id =Integer.parseInt(categories.getWorkflowId());
                notifyDataSetChanged();
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
        private CardView cv_green_bg;
        private ImageView iv_brand;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cv_green_bg = itemView.findViewById(R.id.cv_green_bg);
            iv_brand = itemView.findViewById(R.id.iv_brand);
        }
    }

    public interface brandClickHandler {

        void OnBrandClickListner(int brand_id, String brand_image_url);
    }

}
