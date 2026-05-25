package com.example.app_week_2.ui.admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app_week_2.R;
import com.example.app_week_2.data.AppDatabase;
import com.example.app_week_2.data.OrderDao;
import com.example.app_week_2.data.repository.PhoneRepository;
import com.example.app_week_2.data.remote.FirestoreManager;
import com.example.app_week_2.models.Order;
import com.example.app_week_2.models.Phone;

import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {

    private PhoneRepository phoneRepository;
    private ListView phoneList;
    private List<Phone> phones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        phoneRepository = new PhoneRepository(this);
        phoneList = findViewById(R.id.adminPhoneList);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        findViewById(R.id.addPhoneBtn).setOnClickListener(v ->
                startActivity(new Intent(this, AdminPhoneFormActivity.class))
        );

        loadStats();
        loadPhones();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStats();
        loadPhones(); // refresh after add/edit
    }

    private void loadStats() {
        new Thread(() -> {
            // Revenue + order count from local Room
            OrderDao orderDao = AppDatabase.getInstance(this).orderDao();
            List<Order> orders = orderDao.getAll();
            double totalRevenue = 0;
            for (Order o : orders) totalRevenue += o.total;
            final double revenue = totalRevenue;
            final int orderCount = orders.size();

            // Phone count
            int phoneCount = AppDatabase.getInstance(this).phoneDao().count();

            runOnUiThread(() -> {
                ((TextView) findViewById(R.id.statRevenue)).setText(
                        String.format("$%.0f", revenue));
                ((TextView) findViewById(R.id.statOrders)).setText(
                        String.valueOf(orderCount));
                ((TextView) findViewById(R.id.statProducts)).setText(
                        String.valueOf(phoneCount));
            });
        }).start();
    }

    private void loadPhones() {
        new Thread(() -> {
            phones = phoneRepository.getAllLocal();
            runOnUiThread(() -> {
                AdminPhoneAdapter adapter = new AdminPhoneAdapter(this, phones);
                phoneList.setAdapter(adapter);
            });
        }).start();
    }

    class AdminPhoneAdapter extends ArrayAdapter<Phone> {

        AdminPhoneAdapter(Context context, List<Phone> items) {
            super(context, 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.item_admin_phone, parent, false);
            }

            Phone phone = getItem(position);
            if (phone == null) return convertView;

            ImageView image = convertView.findViewById(R.id.adminPhoneImage);
            TextView brand  = convertView.findViewById(R.id.adminPhoneBrand);
            TextView name   = convertView.findViewById(R.id.adminPhoneName);
            TextView price  = convertView.findViewById(R.id.adminPhonePrice);
            TextView edit   = convertView.findViewById(R.id.adminEditBtn);
            TextView delete = convertView.findViewById(R.id.adminDeleteBtn);

            int resId = getResources().getIdentifier(
                    phone.getImageName(), "drawable", getPackageName());
            if (resId != 0) image.setImageResource(resId);

            brand.setText(phone.getBrand());
            name.setText(phone.getName());
            price.setText(String.format("$%.2f", phone.getPrice()));

            // Edit — open form pre-filled
            edit.setOnClickListener(v -> {
                Intent intent = new Intent(AdminDashboardActivity.this,
                        AdminPhoneFormActivity.class);
                intent.putExtra("phone", phone);
                startActivity(intent);
            });

            // Delete — confirm dialog
            delete.setOnClickListener(v -> {
                new AlertDialog.Builder(AdminDashboardActivity.this)
                        .setTitle("Delete " + phone.getName() + "?")
                        .setMessage("This will remove it from both the local DB and Firestore.")
                        .setPositiveButton("Delete", (dialog, which) -> deletePhone(phone))
                        .setNegativeButton("Cancel", null)
                        .show();
            });

            return convertView;
        }
    }

    private void deletePhone(Phone phone) {
        new Thread(() -> {
            // Delete from local Room
            AppDatabase.getInstance(this).phoneDao().deleteById(phone.id);
            // Delete from Firestore
            FirestoreManager.deletePhone(phone.id);
            runOnUiThread(() -> {
                Toast.makeText(this, phone.getName() + " deleted", Toast.LENGTH_SHORT).show();
                loadPhones();
                loadStats();
            });
        }).start();
    }
}