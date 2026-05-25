package com.example.app_week_2.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app_week_2.R;
import com.example.app_week_2.data.AppDatabase;
import com.example.app_week_2.data.CartDao;
import com.example.app_week_2.data.OrderDao;
import com.example.app_week_2.data.remote.FirestoreManager;
import com.example.app_week_2.models.CartItem;
import com.example.app_week_2.models.Order;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.view.View;

import com.example.app_week_2.data.repository.OrderRepository;

public class PaymentActivity extends AppCompatActivity {

    private CartDao cartDao;
    private List<CartItem> cartItems;
    private OrderRepository orderRepository;

    private double discountAmount = 0;
    private double originalTotal  = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        orderRepository = new OrderRepository(this);
        cartDao  = AppDatabase.getInstance(this).cartDao();

        TextView summaryText  = findViewById(R.id.orderSummaryText);
        TextView totalText    = findViewById(R.id.paymentTotal);
        EditText addressInput = findViewById(R.id.shippingAddress);
        RadioGroup paymentGroup = findViewById(R.id.paymentMethodGroup);
        LinearLayout cardContainer = findViewById(R.id.cardDetailsContainer);
        EditText cardNumber   = findViewById(R.id.cardNumber);
        EditText cardExpiry   = findViewById(R.id.cardExpiry);
        EditText cardCvv      = findViewById(R.id.cardCvv);
        EditText cardName     = findViewById(R.id.cardName);
        Button placeOrderBtn  = findViewById(R.id.placeOrderBtn);
        EditText couponInput   = findViewById(R.id.couponInput);
        Button applyCouponBtn  = findViewById(R.id.applyCouponBtn);
        TextView couponStatus  = findViewById(R.id.couponStatus);

        applyCouponBtn.setOnClickListener(v -> {
            String code = couponInput.getText().toString().trim();
            if (code.isEmpty()) {
                Toast.makeText(this, "Please enter a coupon code", Toast.LENGTH_SHORT).show();
                return;
            }

            FirestoreManager.validateCoupon(code, (valid, discount, type) -> {
                runOnUiThread(() -> {
                    couponStatus.setVisibility(View.VISIBLE);
                    if (valid) {
                        if (type.equals("percent")) {
                            discountAmount = originalTotal * (discount / 100.0);
                        } else {
                            discountAmount = Math.min(discount, originalTotal);
                        }
                        double newTotal = originalTotal - discountAmount;

                        couponStatus.setTextColor(0xFF00897B); // green
                        couponStatus.setText("Coupon applied! You save $" + String.format("%.2f", discountAmount));
                        totalText.setText(String.format("$%.2f", newTotal));
                        applyCouponBtn.setEnabled(false); // prevent double-applying
                    } else {
                        couponStatus.setTextColor(0xFFC62828); // red
                        couponStatus.setText("Invalid or expired coupon code");
                        discountAmount = 0;
                    }
                });
            });
        });

        paymentGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioCard) {
                cardContainer.setVisibility(View.VISIBLE);
            } else {
                cardContainer.setVisibility(View.GONE);
            }
        });

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        // Load cart and build summary
        new Thread(() -> {
            cartItems = cartDao.getAll();
            runOnUiThread(() -> {
                if (cartItems == null || cartItems.isEmpty()) { finish(); return; }

                StringBuilder summary = new StringBuilder();
                double total = 0;
                for (CartItem item : cartItems) {
                    summary.append("• ").append(item.name)
                            .append(" x").append(item.quantity)
                            .append("  ($").append(String.format("%.2f", item.getSubtotal())).append(")\n");
                    total += item.getSubtotal();
                }
                summaryText.setText(summary.toString().trim());
                totalText.setText(String.format("$%.2f", total));
                originalTotal = total;
            });
        }).start();

        placeOrderBtn.setOnClickListener(v -> {
            // Address validation
            if (addressInput.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Please enter your delivery address", Toast.LENGTH_SHORT).show();
                return;
            }

            // Payment method validation
            if (paymentGroup.getCheckedRadioButtonId() == R.id.radioCard) {
                if (cardNumber.getText().toString().trim().length() < 16) {
                    Toast.makeText(this, "Enter a valid card number", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (cardExpiry.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, "Enter expiry date", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (cardCvv.getText().toString().trim().length() < 3) {
                    Toast.makeText(this, "Enter a valid CVV", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (cardName.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, "Enter cardholder name", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Build order summary string
            StringBuilder summary = new StringBuilder();
            double total = 0;
            int count = 0;
            for (CartItem item : cartItems) {
                summary.append(item.name).append(" x").append(item.quantity).append(", ");
                total += item.getSubtotal();
                count += item.quantity;
            }

            String date = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
            double finalTotal = total - discountAmount;
            Order order = new Order(date, finalTotal, summary.toString().replaceAll(", $", ""), count);

            orderRepository.placeOrder(order, () -> {
                new Thread(() -> {
                    cartDao.clearAll();
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Order placed! Thank you.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    });
                }).start();
            });
        });
    }
}
