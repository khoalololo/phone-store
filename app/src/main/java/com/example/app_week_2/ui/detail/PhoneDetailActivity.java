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


public class PhoneDetailActivity extends AppCompatActivity {

    private FavoriteRepository favoriteRepository;
    private CartRepository cartRepository;
    private ReviewDao reviewDao;
    private SessionManager sessionManager;
    private Phone currentPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_detail);

        favoriteRepository = new FavoriteRepository(this);
        cartRepository = new CartRepository(this);
        reviewDao = AppDatabase.getInstance(this).reviewDao();
        sessionManager = new SessionManager(this);

        currentPhone = (Phone) getIntent().getSerializableExtra("phone");

        if (currentPhone == null) {
            Toast.makeText(this, "Error: Phone details not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        populateUI(currentPhone);
        loadReviews();

        Button favoriteBtn  = findViewById(R.id.favoriteBtn);
        Button addToCartBtn = findViewById(R.id.addToCartBtn);
        Button submitReviewBtn = findViewById(R.id.submitReviewBtn);
        // Check if already favorited
        new Thread(() -> {
            boolean isFav = favoriteRepository.getAllLocal().stream()
                    .anyMatch(p -> p.getName().equals(currentPhone.getName()));
            runOnUiThread(() -> {
                if (isFav) favoriteBtn.setText("Saved");
            });
        }).start();

        favoriteBtn.setOnClickListener(v -> {
            new Thread(() -> {
                boolean isFav = favoriteRepository.getAllLocal().stream()
                        .anyMatch(p -> p.getName().equals(currentPhone.getName()));
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
            cartRepository.addToCart(CartItem.fromPhone(currentPhone));
            showNotification(currentPhone.getName());
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                    "Added to cart!", Snackbar.LENGTH_LONG);
            snackbar.setAction("VIEW CART", v2 -> {
                startActivity(new Intent(this, CartActivity.class));
            });
            snackbar.show();
        });

        submitReviewBtn.setOnClickListener(v -> submitReview());

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
        new Thread(() -> {
            List<Review> reviews = reviewDao.getReviewsForPhone(currentPhone.id);
            float avg = 0;
            if (!reviews.isEmpty()) {
                avg = reviewDao.getAverageRating(currentPhone.id);
            }
            final float finalAvg = avg;
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
}
