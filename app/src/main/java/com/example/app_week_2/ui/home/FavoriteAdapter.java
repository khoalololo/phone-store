package com.example.app_week_2.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;
import com.example.app_week_2.R;
import com.example.app_week_2.models.FavoritePhone;
import com.example.app_week_2.models.Phone;
import com.example.app_week_2.data.PhoneProvider;
import java.util.List;

public class FavoriteAdapter extends ArrayAdapter<FavoritePhone> {

    public interface OnRemoveListener {
        void onRemove(FavoritePhone phone);
    }

    private int resourceLayout;
    private Context mContext;
    private OnRemoveListener removeListener;

    public FavoriteAdapter(Context context, int resource, List<FavoritePhone> items) {
        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;
    }

    public void setOnRemoveListener(OnRemoveListener listener) {
        this.removeListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(resourceLayout, parent, false);
            holder = new ViewHolder();
            holder.image     = convertView.findViewById(R.id.favPhoneImage);
            holder.brand     = convertView.findViewById(R.id.favPhoneBrand);
            holder.name      = convertView.findViewById(R.id.favPhoneName);
            holder.price     = convertView.findViewById(R.id.favPhonePrice);
            holder.removeBtn = convertView.findViewById(R.id.favRemoveBtn);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        FavoritePhone phone = getItem(position);
        if (phone != null) {
            // Live Repair: If imageName is null or a numeric placeholder, recover it immediately
            if (phone.imageName == null || phone.imageName.matches("\\d+")) {
                for (Phone ref : PhoneProvider.getPhones()) {
                    if (ref.getName().equalsIgnoreCase(phone.name)) {
                        phone.imageName = ref.getImageName();
                        break;
                    }
                }
            }

            Log.d("FAV_ADAPTER", "Loading favorite: " + phone.name + ", imageName: " + phone.imageName);
            int resId = 0;
            if (phone.imageName != null) {
                resId = mContext.getResources().getIdentifier(phone.imageName, "drawable", mContext.getPackageName());
            }
            
            if (resId != 0) {
                holder.image.setImageResource(resId);
            } else {
                holder.image.setImageResource(R.drawable.phone); // Fallback
            }

            holder.brand.setText(phone.brand != null ? phone.brand : "Unknown");
            holder.name.setText(phone.name != null ? phone.name : "Unknown Phone");
            holder.price.setText(String.format("$%.2f", phone.price));
            holder.removeBtn.setOnClickListener(v -> {
                if (removeListener != null) removeListener.onRemove(phone);
            });
        }

        return convertView;
    }

    static class ViewHolder {
        ImageView image;
        TextView brand, name, price;
        View removeBtn;
    }
}