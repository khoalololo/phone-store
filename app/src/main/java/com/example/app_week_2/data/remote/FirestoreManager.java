package com.example.app_week_2.data.remote;

import android.util.Log;

import com.example.app_week_2.models.CartItem;
import com.example.app_week_2.models.FavoritePhone;
import com.example.app_week_2.models.Order;
import com.example.app_week_2.models.Phone;
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

    // user profile

    public static void syncUser(User user) {
        String uid = uid();
        if (uid == null) return;

        db.collection("users")
                .document(uid)
                .set(user, SetOptions.merge())
                .addOnSuccessListener(v -> Log.d(TAG, "User profile synced"))
                .addOnFailureListener(e -> Log.e(TAG, "User sync failed", e));
    }

    public static void downloadUserByUsername(String username, UserCallback callback) {
        db.collection("users")
                .whereEqualTo("username", username)
                .limit(1)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.isEmpty()) {
                        User user = snapshot.getDocuments().get(0).toObject(User.class);
                        callback.onUserLoaded(user);
                    } else {
                        callback.onUserLoaded(null);
                    }
                })
                .addOnFailureListener(e -> callback.onUserLoaded(null));
    }

    public static void downloadUserByEmail(String email, UserCallback callback) {
        db.collection("users")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.isEmpty()) {
                        User user = snapshot.getDocuments().get(0).toObject(User.class);
                        callback.onUserLoaded(user);
                    } else {
                        callback.onUserLoaded(null);
                    }
                })
                .addOnFailureListener(e -> callback.onUserLoaded(null));
    }

    // favorites

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

    // cart

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

    // orders

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

    public static void removeOrder(int orderId) {
        String uid = uid();
        if (uid == null) return;

        db.collection("users")
                .document(uid)
                .collection("orders")
                .document(String.valueOf(orderId))
                .delete()
                .addOnSuccessListener(v -> Log.d(TAG, "Order removed from Firestore"));
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

    // phones

    public static void uploadPhone(Phone phone) {
        db.collection("phones")
                .document(phone.id)
                .set(phone)
                .addOnSuccessListener(v -> Log.d(TAG, "Phone uploaded: " + phone.getName()))
                .addOnFailureListener(e -> Log.e(TAG, "Upload failed", e));
    }

    public static void downloadPhones(PhonesCallback callback) {
        db.collection("phones")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Phone> phones = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Phone phone = doc.toObject(Phone.class);
                        if (phone != null) phones.add(phone);
                    }
                    callback.onPhonesLoaded(phones);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Download phones failed", e);
                    callback.onPhonesLoaded(new ArrayList<>()); // empty on failure
                });
    }

    public static void deletePhone(String phoneId) {
        db.collection("phones")
                .document(phoneId)
                .delete()
                .addOnSuccessListener(v -> Log.d(TAG, "Phone deleted: " + phoneId))
                .addOnFailureListener(e -> Log.e(TAG, "Delete failed", e));
    }

    // Call this after login to check if user is admin.
    // In Firestore Console, set  users/{uid}/isAdmin = true  for admin accounts.
    public static void getIsAdmin(String uid, AdminCallback callback) {
        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    Boolean isAdmin = doc.getBoolean("isAdmin");
                    callback.onResult(isAdmin != null && isAdmin);
                })
                .addOnFailureListener(e -> callback.onResult(false));
    }

    // Firestore structure:
    //   coupons/{code}  →  { discount: 20, type: "percent", active: true }
    // discount = 20 means 20% off. type "percent" or "fixed" ($20 off).
    public static void validateCoupon(String code, CouponCallback callback) {
        db.collection("coupons")
                .document(code.toUpperCase())   // store codes in uppercase
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Boolean active = doc.getBoolean("active");
                        if (active != null && active) {
                            double discount = doc.getDouble("discount") != null
                                    ? doc.getDouble("discount") : 0;
                            String type = doc.getString("type") != null
                                    ? doc.getString("type") : "percent";
                            callback.onResult(true, discount, type);
                        } else {
                            callback.onResult(false, 0, ""); // expired
                        }
                    } else {
                        callback.onResult(false, 0, ""); // not found
                    }
                })
                .addOnFailureListener(e -> callback.onResult(false, 0, ""));
    }




    public interface PhonesCallback {
        void onPhonesLoaded(List<Phone> phones);
    }
    public interface FavoritesCallback {
        void onFavoritesLoaded(List<FavoritePhone> favorites);
    }

    public interface UserCallback {
        void onUserLoaded(User user);
    }

    public interface OrdersCallback {
        void onOrdersLoaded(List<Order> orders);
    }

    public interface AdminCallback {
        void onResult(boolean isAdmin);
    }

    public interface CouponCallback {
        // valid=true means code exists and is active
        // discount = the discount value
        // type = "percent" or "fixed"
        void onResult(boolean valid, double discount, String type);
    }

}
