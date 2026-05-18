package com.example.app_week_2.data.remote;

import android.util.Log;

import com.example.app_week_2.models.CartItem;
import com.example.app_week_2.models.FavoritePhone;
import com.example.app_week_2.models.Order;
import com.example.app_week_2.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;

public class FirestoreManager {

    private static final String TAG = "FIRESTORE";
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static String uid() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return null;
        }
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    // --- USER PROFILE ---

    public static void syncUser(User user) {
        String uid = uid();
        if (uid == null) return;

        db.collection("users")
                .document(uid)
                .set(user, SetOptions.merge())
                .addOnSuccessListener(v -> Log.d(TAG, "User profile synced"))
                .addOnFailureListener(e -> Log.e(TAG, "User sync failed", e));
    }

    // --- FAVORITES ---

    public static void syncFavorite(FavoritePhone phone) {
        String uid = uid();
        if (uid == null) return;

        db.collection("users")
                .document(uid)
                .collection("favorites")
                .document(phone.getName())
                .set(phone)
                .addOnSuccessListener(v -> Log.d(TAG, "Favorite synced"))
                .addOnFailureListener(e -> Log.e(TAG, "Sync favorite failed", e));
    }

    public static void removeFavorite(String phoneName) {
        String uid = uid();
        if (uid == null) return;

        db.collection("users")
                .document(uid)
                .collection("favorites")
                .document(phoneName)
                .delete();
    }

    public static void downloadFavorites(FavoritesCallback callback) {
        String uid = uid();
        if (uid == null) return;

        db.collection("users")
                .document(uid)
                .collection("favorites")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<FavoritePhone> favorites = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        FavoritePhone phone = doc.toObject(FavoritePhone.class);
                        if (phone != null) favorites.add(phone);
                    }
                    callback.onFavoritesLoaded(favorites);
                });
    }

    // --- CART ---

    public static void syncCart(List<CartItem> items) {
        String uid = uid();
        if (uid == null) return;

        // For cart, we can just overwrite the 'cart' collection or a single document
        // Let's use a document called "current_cart" to keep it simple
        db.collection("users")
                .document(uid)
                .collection("cart")
                .document("data")
                .set(new CartWrapper(items))
                .addOnSuccessListener(v -> Log.d(TAG, "Cart synced"));
    }

    // Helper class for Cart sync
    public static class CartWrapper {
        public List<CartItem> items;
        public CartWrapper() {}
        public CartWrapper(List<CartItem> items) { this.items = items; }
    }

    // --- ORDERS ---

    public static void syncOrder(Order order) {
        String uid = uid();
        if (uid == null) return;

        db.collection("users")
                .document(uid)
                .collection("orders")
                .document(String.valueOf(order.id))
                .set(order)
                .addOnSuccessListener(v -> Log.d(TAG, "Order synced"));
    }

    public static void downloadOrders(OrdersCallback callback) {
        String uid = uid();
        if (uid == null) return;

        db.collection("users")
                .document(uid)
                .collection("orders")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Order> orders = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Order order = doc.toObject(Order.class);
                        if (order != null) orders.add(order);
                    }
                    callback.onOrdersLoaded(orders);
                });
    }

    // --- CALLBACKS ---

    public interface FavoritesCallback {
        void onFavoritesLoaded(List<FavoritePhone> favorites);
    }

    public interface OrdersCallback {
        void onOrdersLoaded(List<Order> orders);
    }
}
