package com.example.app_week_2.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.app_week_2.R;
import com.example.app_week_2.models.CartItem;
import java.util.List;

public class CartAdapter extends ArrayAdapter<CartItem> {

    public interface OnQuantityChangedListener {
        void onChange(CartItem item, int newQty);
    }

    public interface OnRemoveListener {
        void onRemove(CartItem item);
    }

    private int resourceLayout;
    private Context mContext;
    private OnQuantityChangedListener qtyListener;
    private OnRemoveListener removeListener;

    public CartAdapter(Context context, int resource, List<CartItem> items) {
        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;
    }

    public void setOnQuantityChangedListener(OnQuantityChangedListener l) { this.qtyListener = l; }
    public void setOnRemoveListener(OnRemoveListener l) { this.removeListener = l; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(resourceLayout, parent, false);
            holder = new ViewHolder();
            holder.image     = convertView.findViewById(R.id.cartPhoneImage);
            holder.brand     = convertView.findViewById(R.id.cartPhoneBrand);
            holder.name      = convertView.findViewById(R.id.cartPhoneName);
            holder.price     = convertView.findViewById(R.id.cartPhonePrice);
            holder.qtyMinus  = convertView.findViewById(R.id.cartQtyMinus);
            holder.qtyValue  = convertView.findViewById(R.id.cartQtyValue);
            holder.qtyPlus   = convertView.findViewById(R.id.cartQtyPlus);
            holder.removeBtn = convertView.findViewById(R.id.cartRemoveBtn);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CartItem item = getItem(position);
        if (item != null) {
            int resId = mContext.getResources().getIdentifier(item.imageName, "drawable", mContext.getPackageName());
            holder.image.setImageResource(resId);
            holder.brand.setText(item.brand);
            holder.name.setText(item.name);
            holder.price.setText(String.format("$%.2f", item.getSubtotal()));
            holder.qtyValue.setText(String.valueOf(item.quantity));

            holder.qtyMinus.setOnClickListener(v -> {
                if (qtyListener != null) qtyListener.onChange(item, item.quantity - 1);
            });
            holder.qtyPlus.setOnClickListener(v -> {
                if (qtyListener != null) qtyListener.onChange(item, item.quantity + 1);
            });
            holder.removeBtn.setOnClickListener(v -> {
                if (removeListener != null) removeListener.onRemove(item);
            });
        }

        return convertView;
    }

    static class ViewHolder {
        ImageView image;
        TextView brand, name, price, qtyMinus, qtyValue, qtyPlus;
        View removeBtn;
    }
}