package com.example.app_week_2.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.app_week_2.R;
import com.example.app_week_2.data.PhoneProvider;
import com.example.app_week_2.data.SessionManager;
import com.example.app_week_2.data.repository.PhoneRepository;
import com.example.app_week_2.models.Phone;
import com.example.app_week_2.ui.auth.ProfileActivity;
import com.example.app_week_2.ui.detail.PhoneDetailActivity;
import com.example.app_week_2.ui.home.FavoritesActivity;
import com.example.app_week_2.ui.home.CartActivity;
import com.example.app_week_2.ui.auth.ProfileActivity;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private final List<Phone> allPhones = new ArrayList<>();
    private PhoneAdapter adapter;
    private String selectedBrand = "All";
    private String searchQuery = "";
    private String currentSort = "None";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Session
        SessionManager session  = new SessionManager(this);
        TextView greetingText   = findViewById(R.id.greetingText);
        ImageView profileAvatar = findViewById(R.id.profileAvatar);

        if (session.isLoggedIn()) {
            greetingText.setText("Hello, " + session.getUsername());
            profileAvatar.setVisibility(View.VISIBLE);
            profileAvatar.setOnClickListener(v ->
                    startActivity(new Intent(this, ProfileActivity.class))
            );
        }


        PhoneRepository phoneRepository = new PhoneRepository(this);

        GridView gridView = findViewById(R.id.phoneGridView);
        adapter = new PhoneAdapter(this, R.layout.item_phone, new ArrayList<>(allPhones));
        gridView.setAdapter(adapter);

        // Load local data in background
        new Thread(() -> {
            List<Phone> local = phoneRepository.getAllLocal();
            if (local.isEmpty()) {
                // First time: Seed from PhoneProvider
                List<Phone> seed = PhoneProvider.getPhones();
                for (Phone p : seed) {
                    p.id = p.getName().toLowerCase().replace(" ", "_");
                }
                phoneRepository.seedDatabase(seed);
                local = seed; // Show seed data immediately
            }
            
            final List<Phone> toDisplay = local;
            runOnUiThread(() -> {
                allPhones.clear();
                allPhones.addAll(toDisplay);
                adapter.clear();
                adapter.addAll(allPhones);
                adapter.notifyDataSetChanged();
            });
        }).start();

        // Sync from Firestore in background, refresh when done
        phoneRepository.syncFromCloud(() -> {
            List<Phone> updated = phoneRepository.getAllLocal();
            runOnUiThread(() -> {
                allPhones.clear();
                allPhones.addAll(updated);
                adapter.clear();
                adapter.addAll(allPhones);
                adapter.notifyDataSetChanged();
            });
        });

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            Phone tapped = adapter.getItem(position);
            Intent intent = new Intent(this, PhoneDetailActivity.class);
            intent.putExtra("phone", tapped);
            startActivity(intent);
        });

        setupSearch();
        setupTags();
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        findViewById(R.id.navHome).setOnClickListener(v -> { /* already here */ });
        findViewById(R.id.navFavorites).setOnClickListener(v ->
                startActivity(new Intent(this, FavoritesActivity.class)));
        findViewById(R.id.navCart).setOnClickListener(v ->
                startActivity(new Intent(this, CartActivity.class)));
        findViewById(R.id.navProfile).setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));

    }

    private void setupSearch() {
        EditText searchInput = findViewById(R.id.searchInput);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString().toLowerCase().trim();
                filter();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupTags() {
        findViewById(R.id.tagAll).setOnClickListener(v -> updateTagSelection("All", (TextView) v));
        findViewById(R.id.tagApple).setOnClickListener(v -> updateTagSelection("Apple", (TextView) v));
        findViewById(R.id.tagSamsung).setOnClickListener(v -> updateTagSelection("Samsung", (TextView) v));
        findViewById(R.id.tagGoogle).setOnClickListener(v -> updateTagSelection("Google", (TextView) v));
        findViewById(R.id.tagOnePlus).setOnClickListener(v -> updateTagSelection("OnePlus", (TextView) v));

        findViewById(R.id.sortLow).setOnClickListener(v -> updateSortSelection("Low", (TextView) v));
        findViewById(R.id.sortHigh).setOnClickListener(v -> updateSortSelection("High", (TextView) v));
    }

    private void updateSortSelection(String sort, TextView selectedTag) {
        if (currentSort.equals(sort)) {
            currentSort = "None";
            selectedTag.setBackgroundResource(R.drawable.tag_inactive_bg);
            selectedTag.setTextColor(ContextCompat.getColor(this, R.color.pink_primary));
        } else {
            currentSort = sort;
            // Reset other sort tags
            findViewById(R.id.sortLow).setBackgroundResource(R.drawable.tag_inactive_bg);
            findViewById(R.id.sortHigh).setBackgroundResource(R.drawable.tag_inactive_bg);
            ((TextView)findViewById(R.id.sortLow)).setTextColor(ContextCompat.getColor(this, R.color.pink_primary));
            ((TextView)findViewById(R.id.sortHigh)).setTextColor(ContextCompat.getColor(this, R.color.pink_primary));

            selectedTag.setBackgroundResource(R.drawable.tag_active_bg);
            selectedTag.setTextColor(ContextCompat.getColor(this, R.color.white));
        }
        filter();
    }

    private void updateTagSelection(String brand, TextView selectedTag) {
        selectedBrand = brand;
        LinearLayout container = findViewById(R.id.tagContainer);
        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            if (child instanceof TextView) {
                TextView tv = (TextView) child;
                tv.setBackgroundResource(R.drawable.tag_inactive_bg);
                tv.setTextColor(ContextCompat.getColor(this, R.color.pink_primary));
                tv.setTypeface(null, android.graphics.Typeface.NORMAL);
            }
        }
        selectedTag.setBackgroundResource(R.drawable.tag_active_bg);
        selectedTag.setTextColor(ContextCompat.getColor(this, R.color.white));
        selectedTag.setTypeface(null, android.graphics.Typeface.BOLD);
        filter();
    }

    private void filter() {
        adapter.clear();
        List<Phone> filtered = new ArrayList<>();
        for (Phone phone : allPhones) {
            boolean matchesBrand = selectedBrand.equals("All") || phone.getBrand().equalsIgnoreCase(selectedBrand);
            boolean matchesSearch = searchQuery.isEmpty() || 
                    phone.getName().toLowerCase().contains(searchQuery) || 
                    phone.getBrand().toLowerCase().contains(searchQuery);
            if (matchesBrand && matchesSearch) filtered.add(phone);
        }

        // Apply sorting
        if (currentSort.equals("Low")) {
            Collections.sort(filtered, (p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice()));
        } else if (currentSort.equals("High")) {
            Collections.sort(filtered, (p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()));
        }

        adapter.addAll(filtered);
        adapter.notifyDataSetChanged();
    }
}
