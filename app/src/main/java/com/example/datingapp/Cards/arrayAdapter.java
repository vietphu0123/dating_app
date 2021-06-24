package com.example.datingapp.Cards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.datingapp.R;

import java.util.List;

public class arrayAdapter extends ArrayAdapter<cards> {
    Context context;
    public arrayAdapter(Context context, int resourceID, List<cards> items)
    {
        super(context,resourceID,items);

    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        cards card_item=getItem(position);
        if(convertView==null)
        {
          convertView= LayoutInflater.from(getContext()).inflate(R.layout.item,parent,false);
        }
        TextView name=(TextView)convertView.findViewById(R.id.name);
        ImageView image=(ImageView)convertView.findViewById(R.id.image);
        TextView budget=(TextView)convertView.findViewById(R.id.budget);
        ImageView mNeedImage=(ImageView)convertView.findViewById(R.id.needImage);
        ImageView mGiveImage=(ImageView)convertView.findViewById(R.id.giveImage);

        name.setText(card_item.getName());
        budget.setText(card_item.getBudget());

        //need Image

        if(card_item.getNeed().equals("Netflix"))
            mNeedImage.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.netflix));
        else if(card_item.getNeed().equals("Youtube Originals"))
            mNeedImage.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.youtube));
        else if(card_item.getNeed().equals("HBO Now"))
            mNeedImage.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.hbo));
        else
            mNeedImage.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.none));


        //Give Image

        if(card_item.getGive().equals("Netflix"))
            mGiveImage.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.netflix));
        else if(card_item.getGive().equals("Youtube Originals"))
            mGiveImage.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.youtube));
        else if(card_item.getGive().equals("HBO Now"))
            mGiveImage.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.hbo));
        else
            mGiveImage.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.none));

        switch (card_item.getProfileImageUrl()){
            case "default":
                Glide.with(convertView.getContext()).load(R.drawable.profile).into(image);
                break;
            default:
                Glide.clear(image);
                Glide.with(convertView.getContext()).load(card_item.getProfileImageUrl()).into(image);
                break;

        }
        return convertView;
    }
}
