package com.example.app_week_2.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.app_week_2.R;
import com.example.app_week_2.models.Order;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends ArrayAdapter<Order> {

    public OrderAdapter(Context context, List<Order> orders) {
        super(context, 0, orders);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_order, parent, false);
        }

        Order order = getItem(position);
        if (order != null) {
            TextView orderId = convertView.findViewById(R.id.orderId);
            TextView orderDate = convertView.findViewById(R.id.orderDate);
            TextView orderItems = convertView.findViewById(R.id.orderItems);
            TextView orderItemCount = convertView.findViewById(R.id.orderItemCount);
            TextView orderTotal = convertView.findViewById(R.id.orderTotal);

            orderId.setText(String.format("Order #%d", 1000 + order.id));
            orderDate.setText(order.date);
            orderItems.setText(order.itemsSummary);
            orderItemCount.setText(String.format(Locale.getDefault(), "%d Items", order.itemCount));
            orderTotal.setText(String.format(Locale.getDefault(), "$%.2f", order.total));
        }

        return convertView;
    }
}
