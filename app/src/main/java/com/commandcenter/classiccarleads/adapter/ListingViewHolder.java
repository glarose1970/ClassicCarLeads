package com.commandcenter.classiccarleads.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.commandcenter.classiccarleads.R;
import com.commandcenter.classiccarleads.Single_Listing_View;

/**
 * Created by Command Center on 11/22/2017.
 */

public class ListingViewHolder extends RecyclerView.ViewHolder{

    public TextView tv_listingID, tv_title, tv_price, tv_desc;
    public ImageView iv_listingImg;
    public View mView;
    public ListingViewHolder(View itemView) {
        super(itemView);

        mView = itemView;
        tv_listingID = itemView.findViewById(R.id.listing_single_row_tv_listingID);
        tv_title = itemView.findViewById(R.id.listing_single_row_tv_listingTitle);
        tv_price = itemView.findViewById(R.id.listing_single_row_tv_price);
        tv_desc  = itemView.findViewById(R.id.listing_single_row_tv_desc);
        iv_listingImg = itemView.findViewById(R.id.listing_single_row_iv_listingImg);

    }
}
