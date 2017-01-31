package com.allnew.facebookgraphapi;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Model> modelArrayList;
    private MainActivity activity;
    private String next;

    public FBAdapter(ArrayList<Model> modelArrayList, MainActivity activity, String nextLink) {
        this.modelArrayList = modelArrayList;
        this.activity = activity;
        next = nextLink;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            final View view = LayoutInflater.from(activity).inflate(R.layout.extra_row, parent, false);
            return new ViewHolderExtra(view);
        } else {
            final View view = LayoutInflater.from(activity).inflate(R.layout.fb_row, parent, false);
            return new ViewHolderFB(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Model model = modelArrayList.get(position);
        if (holder.getItemViewType() == 0) {
            final ViewHolderExtra viewHolderExtra = (ViewHolderExtra) holder;
//            Glide.with(activity).load(activity.getResources().getDrawable(R.drawable.add)).into(viewHolderExtra.imgFb);
//            Glide.with(activity).load(activity.getResources().getDrawable(R.drawable.test)).asGif().into(viewHolderExtra.imgFb);
        } else {
            final ViewHolderFB viewHolderFB = (ViewHolderFB) holder;
            viewHolderFB.txtFb.setText(model.getDescreption());
            viewHolderFB.txtFb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.callNewWeb(model.getVideoUrl());
                }
            });
        }
        if (modelArrayList.size() == position + 5) {
            activity.callGraphAPI();
        }
    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return modelArrayList.get(position) == null ? 0 : 1;
    }

    private class ViewHolderFB extends RecyclerView.ViewHolder {
        private TextView txtFb;

        public ViewHolderFB(View itemView) {
            super(itemView);
            txtFb = (TextView) itemView.findViewById(R.id.title);
        }
    }

    private class ViewHolderExtra extends RecyclerView.ViewHolder {
        private ImageView imgFb;

        public ViewHolderExtra(View itemView) {
            super(itemView);
            imgFb = (ImageView) itemView.findViewById(R.id.img);
        }
    }
}
