package com.commandcenter.classiccarleads.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.commandcenter.classiccarleads.R;

/**
 * Created by Command Center on 11/22/2017.
 */

public class ListingViewHolder extends RecyclerView.ViewHolder{

    public TextView tv_title, tv_price, tv_desc, tv_loc;
    public ImageView iv_listingImg;
    public Button btn_remove;
    public View mView;
    public ListingViewHolder(View itemView) {
        super(itemView);

        mView = itemView;
        tv_title = itemView.findViewById(R.id.listing_single_row_tv_listingTitle);
        tv_price = itemView.findViewById(R.id.listing_single_row_tv_price);
        tv_desc  = itemView.findViewById(R.id.listing_single_row_tv_desc);
        tv_loc   = itemView.findViewById(R.id.listing_single_row_tv_location);
        iv_listingImg = itemView.findViewById(R.id.listing_single_row_iv_listingImg);
        btn_remove    = itemView.findViewById(R.id.listing_single_row_btn_remove);

    }
}
