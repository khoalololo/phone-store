package com.example.app_week_2.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.app_week_2.R;
import com.example.app_week_2.models.Phone;

import java.util.List;

public class PhoneAdapter extends ArrayAdapter<Phone> {
    private int resourceLayout;
    private Context mContext;

    public PhoneAdapter(Context context, int resource, List<Phone> items) {
        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(resourceLayout, parent, false);
            holder = new ViewHolder();
            holder.phoneImage = convertView.findViewById(R.id.phoneImage);
            holder.phoneBrand = convertView.findViewById(R.id.phoneBrand);
            holder.phoneName = convertView.findViewById(R.id.phoneName);
            holder.phonePrice = convertView.findViewById(R.id.phonePrice);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Phone phone = getItem(position);
        if (phone != null) {
            int resId = mContext.getResources().getIdentifier(phone.getImageName(), "drawable", mContext.getPackageName());
            holder.phoneImage.setImageResource(resId);
            holder.phoneBrand.setText(phone.getBrand());
            holder.phoneName.setText(phone.getName());
            holder.phonePrice.setText(String.format("$%.2f", phone.getPrice()));
        }
        return convertView;
    }

    static class ViewHolder {
        ImageView phoneImage;
        TextView phoneBrand, phoneName, phonePrice;
    }
}
