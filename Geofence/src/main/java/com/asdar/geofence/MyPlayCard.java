package com.asdar.geofence;

/**
 * Created by Ehsan on 1/28/14.
 */

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fima.cardsui.objects.RecyclableCard;

import java.util.ArrayList;

public class MyPlayCard extends RecyclableCard {
    private ArrayList<Integer> icons;
    private Context context;
    public MyPlayCard(String titlePlay, String description, String color,
                      String titleColor, Boolean hasOverflow, Boolean isClickable, ArrayList<Integer> drawableicons,Context c) {
        super(titlePlay, description, color, titleColor, hasOverflow,
                isClickable);
        icons = drawableicons;
        context = c;
    }

    @Override
    protected int getCardLayoutId() {
        return R.layout.card_play;
    }

    @Override
    protected void applyTo(View convertView) {
        ((TextView) convertView.findViewById(R.id.title)).setText(titlePlay);
        ((TextView) convertView.findViewById(R.id.title)).setTextColor(Color
                .parseColor(titleColor));
        ((TextView) convertView.findViewById(R.id.description))
                .setText(description);
        ((ImageView) convertView.findViewById(R.id.stripe))
                .setBackgroundColor(Color.parseColor(color));
        LinearLayout titlelayout = (LinearLayout)convertView.findViewById(R.id.titleLayout);
        for (Integer i : icons){
            ImageView img = new ImageView(context);
         //   img.setBaseline(R.id.title);
            img.setVisibility(View.VISIBLE);
            img.setImageResource(i);
            titlelayout.addView(img);
        }
        if (isClickable == true)
            ((LinearLayout) convertView.findViewById(R.id.contentLayout))
                    .setBackgroundResource(R.drawable.selectable_background_cardbank);

        if (hasOverflow == true)
            ((ImageView) convertView.findViewById(R.id.overflow))
                    .setVisibility(View.VISIBLE);
        else
            ((ImageView) convertView.findViewById(R.id.overflow))
                    .setVisibility(View.GONE);
    }
}