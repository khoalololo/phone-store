package com.example.app_week_2.ui.detail;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app_week_2.R;
import com.example.app_week_2.data.repository.CartRepository;
import com.example.app_week_2.data.repository.FavoriteRepository;
import com.example.app_week_2.models.Phone;
import java.util.Locale;
import com.example.app_week_2.models.CartItem;
import com.example.app_week_2.models.FavoritePhone;
import android.content.Intent;
import android.widget.Toast;
import com.google.android.material.snackbar.Snackbar;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.example.app_week_2.ui.home.CartActivity;
import com.example.app_week_2.ui.auth.ProfileActivity;
import com.example.app_week_2.data.AppDatabase;
import com.example.app_week_2.data.ReviewDao;
import com.example.app_week_2.models.Review;
import com.example.app_week_2.data.SessionManager;
import android.widget.RatingBar;
import android.widget.EditText;
import android.widget.LinearLayout;
import java.util.List;
import android.view.LayoutInflater;
import android.view.View;
import com.example.app_week_2.data.repository.PhoneRepository;
import java.util.ArrayList;

public class PhoneDetailActivity extends AppCompatActivity {

    private FavoriteRepository favoriteRepository;
    private CartRepository cartRepository;
    private ReviewDao reviewDao;
    private SessionManager sessionManager;
    private Phone currentPhone;
    private PhoneRepository phoneRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_detail);

        favoriteRepository = new FavoriteRepository(this);
        phoneRepository = new PhoneRepository(this);
        cartRepository = new CartRepository(this);
        reviewDao = AppDatabase.getInstance(this).reviewDao();
        sessionManager = new SessionManager(this);

        currentPhone = (Phone) getIntent().getSerializableExtra("phone");

        if (currentPhone == null) {
            Toast.makeText(this, "Error: Phone details not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadRelatedPhones();
        populateUI(currentPhone);
        loadReviews();

        Button favoriteBtn  = findViewById(R.id.favoriteBtn);
        Button addToCartBtn = findViewById(R.id.addToCartBtn);
        Button submitReviewBtn = findViewById(R.id.submitReviewBtn);
        // Check if already favorited
        new Thread(() -> {
            boolean isFav = favoriteRepository.getAllLocal().stream()
                    .anyMatch(p -> p != null && p.getName() != null && currentPhone != null && p.getName().equals(currentPhone.getName()));
            runOnUiThread(() -> {
                if (isFav) favoriteBtn.setText("Saved");
            });
        }).start();

        favoriteBtn.setOnClickListener(v -> {
            if (!sessionManager.isLoggedIn()) {
                redirectToLogin();
                return;
            }
            if (currentPhone == null) return;
            new Thread(() -> {
                boolean isFav = favoriteRepository.getAllLocal().stream()
                        .anyMatch(p -> p != null && p.getName() != null && p.getName().equals(currentPhone.getName()));
                if (isFav) {
                    favoriteRepository.removeFavorite(currentPhone.getName());
                    runOnUiThread(() -> {
                        favoriteBtn.setText("Favorite");
                        Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    favoriteRepository.addFavorite(FavoritePhone.fromPhone(currentPhone));
                    runOnUiThread(() -> {
                        favoriteBtn.setText("Saved");
                        Toast.makeText(this, "Added to favorites!", Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        });

        addToCartBtn.setOnClickListener(v -> {
            if (!sessionManager.isLoggedIn()) {
                redirectToLogin();
                return;
            }
            if (currentPhone == null) return;
            cartRepository.addToCart(CartItem.fromPhone(currentPhone));
            showNotification(currentPhone.getName());
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                    "Added to cart!", Snackbar.LENGTH_LONG);
            snackbar.setAction("VIEW CART", v2 -> {
                startActivity(new Intent(this, CartActivity.class));
            });
            snackbar.show();
        });

        submitReviewBtn.setOnClickListener(v -> {
            if (!sessionManager.isLoggedIn()) {
                redirectToLogin();
                return;
            }
            submitReview();
        });

        findViewById(R.id.cartButton).setOnClickListener(v -> {
            startActivity(new Intent(this, CartActivity.class));
        });

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    private void showNotification(String phoneName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
                return;
            }
        }

        String channelId = "cart_notifications";
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Cart", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Added to Cart")
                .setContentText(phoneName + " has been added to your cart.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        manager.notify(1, builder.build());
    }

    private void loadReviews() {
        if (currentPhone == null) return;
        new Thread(() -> {
            List<Review> reviews = reviewDao.getReviewsForPhone(currentPhone.id);
            final float finalAvg = reviews.isEmpty() ? 0 : reviewDao.getAverageRating(currentPhone.id);
            runOnUiThread(() -> {
                displayReviews(reviews);
                if (finalAvg > 0) {
                    ((TextView) findViewById(R.id.detailPhoneRating)).setText(String.format(Locale.getDefault(), "%.1f", finalAvg));
                }
            });
        }).start();
    }

    private void displayReviews(List<Review> reviews) {
        LinearLayout container = findViewById(R.id.reviewsContainer);
        container.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (Review review : reviews) {
            View view = inflater.inflate(R.layout.item_review, container, false);
            ((TextView) view.findViewById(R.id.reviewUser)).setText(review.username);
            ((TextView) view.findViewById(R.id.reviewComment)).setText(review.comment);
            ((RatingBar) view.findViewById(R.id.reviewItemRating)).setRating(review.rating);
            container.addView(view);
        }
    }

    private void redirectToLogin() {
        Toast.makeText(this, "Please login to use this feature", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, com.example.app_week_2.ui.auth.LoginActivity.class);
        startActivity(intent);
    }

    private void submitReview() {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Please login to leave a review", Toast.LENGTH_SHORT).show();
            return;
        }

        RatingBar ratingBar = findViewById(R.id.reviewRatingBar);
        EditText editText = findViewById(R.id.reviewEditText);
        float rating = ratingBar.getRating();
        String comment = editText.getText().toString().trim();

        if (rating == 0) {
            Toast.makeText(this, "Please select a rating", Toast.LENGTH_SHORT).show();
            return;
        }

        if (comment.isEmpty()) {
            Toast.makeText(this, "Please write a comment", Toast.LENGTH_SHORT).show();
            return;
        }

        Review review = new Review(currentPhone.id, sessionManager.getUsername(), rating, comment);

        new Thread(() -> {
            reviewDao.insert(review);
            runOnUiThread(() -> {
                Toast.makeText(this, "Review submitted!", Toast.LENGTH_SHORT).show();
                ratingBar.setRating(0);
                editText.setText("");
                loadReviews();
            });
        }).start();
    }

    private void populateUI(Phone phone) {
        int resId = getResources().getIdentifier(phone.getImageName(), "drawable", getPackageName());
        ((ImageView) findViewById(R.id.detailPhoneImage)).setImageResource(resId);
        ((TextView) findViewById(R.id.detailPhoneBrand)).setText(phone.getBrand());
        ((TextView) findViewById(R.id.detailPhoneName)).setText(phone.getName());
        ((TextView) findViewById(R.id.detailPhoneRating)).setText(String.valueOf(phone.getRating()));
        ((TextView) findViewById(R.id.detailPhonePrice)).setText(String.format(Locale.getDefault(), "$%.2f", phone.getPrice()));
        ((TextView) findViewById(R.id.detailPhoneDescription)).setText(phone.getDescription());
        ((TextView) findViewById(R.id.detailStorage)).setText(phone.getStorage());
        ((TextView) findViewById(R.id.detailBattery)).setText(phone.getBattery());
        ((TextView) findViewById(R.id.detailDisplay)).setText(phone.getDisplay());
        ((TextView) findViewById(R.id.detailOS)).setText(phone.getOs());
        ((TextView) findViewById(R.id.detailChipset)).setText(phone.getChipset());
        ((TextView) findViewById(R.id.detailCamera)).setText(phone.getCamera());
        ((TextView) findViewById(R.id.detailCharging)).setText(phone.getCharging());
        ((TextView) findViewById(R.id.detailFeatures)).setText(phone.getFeatures());
    }

    private void loadRelatedPhones() {
        new Thread(() -> {
            List<Phone> allPhones = phoneRepository.getAllLocal();
            List<Phone> related = new ArrayList<>();
            for (Phone p : allPhones) {
                // Same brand, different phone
                if (p != null && p.getBrand() != null && currentPhone != null && currentPhone.getBrand() != null
                        && p.getBrand().equalsIgnoreCase(currentPhone.getBrand())
                        && p.getName() != null && currentPhone.getName() != null
                        && !p.getName().equals(currentPhone.getName())) {
                    related.add(p);
                }
            }
            runOnUiThread(() -> displayRelatedPhones(related));
        }).start();
    }

    private void displayRelatedPhones(List<Phone> phones) {
        LinearLayout container = findViewById(R.id.relatedPhonesContainer);
        container.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (Phone phone : phones) {
            View card = inflater.inflate(R.layout.item_related_phone, container, false);

            ImageView img = card.findViewById(R.id.relatedPhoneImage);
            TextView name = card.findViewById(R.id.relatedPhoneName);
            TextView price = card.findViewById(R.id.relatedPhonePrice);

            int resId = getResources().getIdentifier(
                    phone.getImageName(), "drawable", getPackageName());
            if (resId != 0) img.setImageResource(resId);
            name.setText(phone.getName());
            price.setText(String.format(Locale.getDefault(), "$%.2f", phone.getPrice()));

            // Tap related phone → open its detail page
            card.setOnClickListener(v -> {
                Intent intent = new Intent(this, PhoneDetailActivity.class);
                intent.putExtra("phone", phone);
                startActivity(intent);
            });
            container.addView(card);
        }
    }

}
