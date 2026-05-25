# Tech Pulse Hub — Android Phone Store

A full-stack Android e-commerce app for browsing, favoriting, and purchasing phones. Built with a dual-storage architecture: **Room (SQLite)** for instant offline access and **Firebase Firestore** for cloud sync across devices. Includes a complete admin panel for product management.

---

## Package Structure

```
com.example.app_week_2/
│
├── data/                              # Data layer
│   ├── AppDatabase.java               # Room DB (version 6): users, favorites,
│   │                                  # cart_items, orders, phones, reviews
│   ├── UserDao.java                   # CRUD for users
│   ├── FavoriteDao.java               # CRUD for favorites
│   ├── CartDao.java                   # CRUD for cart items
│   ├── OrderDao.java                  # Insert + query orders
│   ├── PhoneDao.java                  # Insert, query, delete phones
│   ├── ReviewDao.java                 # Insert + query reviews by phoneId
│   ├── PhoneProvider.java             # Static seed catalog (first-launch only)
│   ├── SessionManager.java            # SharedPreferences: login state + isAdmin
│   │
│   ├── remote/
│   │   ├── FirebaseAuthManager.java   # Anonymous sign-in (UID for Firestore scope)
│   │   └── FirestoreManager.java      # All Firestore read/write/delete operations
│   │                                  # + coupon validation + admin check
│   │
│   └── repository/                    # Single source of truth coordinators
│       ├── UserRepository.java        # Firebase Email Auth + Room + isAdmin check
│       ├── FavoriteRepository.java    # Local write + cloud sync + image repair
│       ├── CartRepository.java        # Cart mutations + cloud sync after each op
│       ├── OrderRepository.java       # Order placement + cloud sync
│       └── PhoneRepository.java       # Catalog: seed, save (upsert), sync from cloud
│
├── models/
│   ├── Phone.java                     # @Entity: id, brand, name, imageName, price,
│   │                                  # storage, battery, display, chipset, camera,
│   │                                  # charging, os, description, rating, features
│   ├── User.java                      # @Entity: id, username, email, password
│   ├── FavoritePhone.java             # @Entity: mirrors Phone fields + legacy setters
│   ├── CartItem.java                  # @Entity: brand, name, imageName, price, qty
│   ├── Order.java                     # @Entity: date, total, itemsSummary, itemCount
│   └── Review.java                    # @Entity: phoneId, username, rating, comment,
│                                      # timestamp
│
└── ui/
    ├── auth/
    │   ├── LandingActivity.java       # Entry screen: session check → skip if logged in
    │   ├── LoginActivity.java         # Firebase Email Auth via UserRepository
    │   ├── RegisterActivity.java      # Register + auto-login + Firestore user sync
    │   └── ProfileActivity.java       # User info, order history, sign out,
    │                                  # Admin Panel row (visible to admins only)
    │
    ├── home/
    │   ├── HomeActivity.java          # GridView: search + brand filter + price sort
    │   ├── PhoneAdapter.java          # Grid card adapter (imageName → drawable)
    │   ├── FavoritesActivity.java     # Favorites list + cloud sync on load
    │   ├── FavoriteAdapter.java       # Live image repair on bind
    │   ├── CartActivity.java          # Cart list + quantity controls + total
    │   ├── CartAdapter.java           # Per-row qty +/− and remove
    │   ├── PaymentActivity.java       # Checkout: address, payment method,
    │   │                              # coupon code → Firestore lookup → discount
    │   ├── OrderHistoryActivity.java  # Order list + cloud sync
    │   ├── OrderAdapter.java          # Card-based order rows with status badge
    │   ├── NonScrollGridView.java     # Utility: non-scrolling GridView for nesting
    │   └── NonScrollListView.java     # Utility: non-scrolling ListView for nesting
    │
    ├── detail/
    │   └── PhoneDetailActivity.java   # Full specs, related phones (same brand),
    │                                  # review form + review list, add to cart/favorites,
    │                                  # push notification on cart add
    │
    └── admin/
        ├── AdminDashboardActivity.java  # Stats (revenue, orders, products) + phone list
        │                                # with inline edit/delete per row
        └── AdminPhoneFormActivity.java  # Add or edit phone: all fields, saves to
                                         # Room + Firestore via PhoneRepository
```

---

## Screens & Features

| Screen | Key Features |
|---|---|
| Landing | Session check — skips straight to Home if already logged in |
| Login | Firebase Email Auth, falls back to Firestore lookup on new device |
| Register | Creates Firebase Auth account + Room record + Firestore user doc |
| Home | 2-column grid, live search, brand filter tabs, price sort (low/high) |
| Phone Detail | Full specs table, related phones (same brand horizontal scroll), star rating, review form, add to cart (with push notification + snackbar), add to favorites |
| Favorites | Saved phones, tap to open detail, tap ❤️ to remove, cloud sync on load |
| Cart | Quantity controls, subtotal per item, running total, proceed to checkout |
| Payment | Order summary, shipping address, card or COD selector, coupon code with live Firestore validation and discount calculation |
| Order History | All past orders with order number, date, item summary, total, status badge |
| Profile | Username/email display, order history link, admin panel link (admin only), sign out |
| Admin Dashboard | Revenue / orders / product count stats, full phone list with edit and delete |
| Admin Phone Form | Add new phone or edit existing — all fields, saves to Room + Firestore |

---

## Local Storage (Room)

| Entity | Table | Notes |
|---|---|---|
| `User` | `users` | Passwords stored as-is (hash in production) |
| `FavoritePhone` | `favorites` | Mirrors Phone fields for offline access |
| `CartItem` | `cart_items` | `REPLACE` on conflict — handles upsert |
| `Order` | `orders` | Permanent record, never deleted locally |
| `Phone` | `phones` | Seeded from `PhoneProvider` on first launch, synced from Firestore after |
| `Review` | `reviews` | Keyed by `phoneId` (Phone's string ID) |

---

## Firestore Structure

```
/users/{uid}
    username, email, password, isAdmin (boolean)

    /favorites/{phoneName}       ← per-user subcollection
    /cart/data                   ← single doc wrapping the cart array
    /orders/{orderId}            ← one doc per placed order

/phones/{phoneId}                ← global catalog, writable by admin
/reviews/{auto-id}               ← all reviews across all phones
/coupons/{CODE}                  ← discount codes (discount, type, active)
```

---

## Firebase / Firestore Sync Strategy

```
User action (add favorite, place order, etc.)
    │
    ▼
Repository
    ├── Room DAO  ← writes immediately (offline-first, UI responds instantly)
    └── FirestoreManager ← fires in parallel on same background thread (cloud backup)

On screen open:
    1. Load from Room → show UI immediately
    2. syncFromCloud() → merge any changes from Firestore → refresh UI
```

**Key rules:**
- Room is always written first — the app works fully offline.
- Firestore sync is fire-and-forget — failures are logged, never crash the app.
- `REPLACE` conflict strategy on Room inserts means cloud updates overwrite stale local data cleanly.
- Cart is synced as a single document (`cart/data`) to avoid per-item write overhead.
- Favorites use per-document writes so individual removes are cheap (`favorites/{phoneName}.delete()`).

---

## Admin System

**How to grant admin access:**

1. Register the account normally through the app.
2. In the Firebase Console → Firestore → `users` collection → find the user's document → add field: `isAdmin: true` (boolean).
3. That user's next login will call `getIsAdmin()` → sets `SessionManager.isAdmin = true` → Admin Panel row appears in Profile.

**Admin can:**
- View revenue, order count, and product count on the dashboard.
- Add new phones via the form (all fields, saves to Room + Firestore instantly).
- Edit any existing phone — form pre-fills with current data.
- Delete phones (confirm dialog → removes from Room + Firestore).

---

## Coupon Codes

Coupons live in Firestore under `/coupons/{CODE}` with three fields:

| Field | Type | Example |
|---|---|---|
| `discount` | number | `20` |
| `type` | string | `"percent"` or `"fixed"` |
| `active` | boolean | `true` |

`"percent"` takes 20% off the total. `"fixed"` takes $20 off. Set `active: false` to expire a code without deleting it. Codes are matched case-insensitively (stored uppercase, input is uppercased before lookup).

---

## Session Management (SharedPreferences)

`SessionManager` stores four keys in a private SharedPreferences file:

| Key | Type | Purpose |
|---|---|---|
| `is_logged_in` | boolean | Gate for LandingActivity skip and UI personalization |
| `username` | String | Displayed in Home greeting and Profile |
| `email` | String | Displayed in Profile |
| `is_admin` | boolean | Controls Admin Panel row visibility in Profile |

Cleared entirely on sign out via `clearSession()`.

---

## Image Resolution

Phones don't store a drawable resource ID — they store a **string name** (`imageName`) like `"iphone"` or `"samsung_a55"`. Adapters resolve it at bind time:

```java
int resId = context.getResources().getIdentifier(
    phone.getImageName(), "drawable", context.getPackageName()
);
```

This means adding a new phone only requires dropping a PNG into `res/drawable/` and using its filename (without extension) as the `imageName`. No code changes needed.

If `resId == 0` (image not found), adapters fall back to `R.drawable.phone` to prevent a blank or crashed row.
