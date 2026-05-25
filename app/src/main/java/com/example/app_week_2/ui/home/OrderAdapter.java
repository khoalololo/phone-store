package com.example.app_week_2.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import com.example.app_week_2.R;
import com.example.app_week_2.data.repository.OrderRepository;
import com.example.app_week_2.models.Order;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends ArrayAdapter<Order> {

    private OrderRepository repository;
    private Runnable onOrderCancelled;

    public OrderAdapter(Context context, List<Order> orders, Runnable onOrderCancelled) {
        super(context, 0, orders);
        this.repository = new OrderRepository(context);
        this.onOrderCancelled = onOrderCancelled;
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
            TextView orderStatus = convertView.findViewById(R.id.orderStatus);
            TextView orderDate = convertView.findViewById(R.id.orderDate);
            TextView orderItems = convertView.findViewById(R.id.orderItems);
            TextView orderItemCount = convertView.findViewById(R.id.orderItemCount);
            TextView orderTotal = convertView.findViewById(R.id.orderTotal);

            orderId.setText(String.format("Order #%d", 1000 + order.id));
            orderStatus.setText(order.status);
            orderDate.setText(order.date);
            orderItems.setText(order.itemsSummary);
            orderItemCount.setText(String.format(Locale.getDefault(), "%d Items", order.itemCount));
            orderTotal.setText(String.format(Locale.getDefault(), "$%.2f", order.total));

            Button btnCancel = convertView.findViewById(R.id.btnCancelOrder);
            btnCancel.setOnClickListener(v -> {
                new AlertDialog.Builder(getContext())
                        .setTitle("Cancel Order")
                        .setMessage("Are you sure you want to cancel this order?")
                        .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                            repository.cancelOrder(order.id, onOrderCancelled);
                        })
                        .setNegativeButton("No", null)
                        .show();
            });
        }

        return convertView;
    }
}
