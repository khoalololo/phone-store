# PhoneStore App - Week 2

A simple Android e-commerce app for browsing, favoriting, and purchasing phones, with local Room storage and Firebase/Firestore cloud sync.

---

## Layer / Package Structure

```
com.example.app_week_2/
│
├── data/                          # Data layer
│   ├── AppDatabase.java           # Room database definition (all DAOs + entities)
│   ├── UserDao.java               # SQL queries for users
│   ├── FavoriteDao.java           # SQL queries for favorites
│   ├── CartDao.java               # SQL queries for cart items
│   ├── OrderDao.java              # SQL queries for orders
│   ├── PhoneProvider.java         # Static in-memory list of phones (mock catalog)
│   ├── SessionManager.java        # SharedPreferences wrapper for login session
│   │
│   ├── remote/
│   │   ├── FirebaseAuthManager.java   # Anonymous Firebase Auth sign-in
│   │   └── FirestoreManager.java      # All Firestore read/write operations
│   │
│   └── repository/
│       ├── UserRepository.java        # Register/login logic (Room + Firebase Auth + Firestore)
│       ├── FavoriteRepository.java    # Favorites CRUD (Room + Firestore)
│       ├── CartRepository.java        # Cart CRUD (Room + Firestore)
│       └── OrderRepository.java       # Order placement (Room + Firestore)
│
├── models/                        # Plain data models
│   ├── Phone.java                 # Catalog item (not persisted to Room)
│   ├── User.java                  # Room entity - users table
│   ├── FavoritePhone.java         # Room entity - favorites table
│   ├── CartItem.java              # Room entity - cart_items table
│   └── Order.java                 # Room entity - orders table
│
└── ui/                            # UI layer (Activities + Adapters)
    ├── auth/
    │   ├── LandingActivity.java   # Entry screen (Login / Register / Browse)
    │   ├── LoginActivity.java
    │   ├── RegisterActivity.java
    │   └── ProfileActivity.java
    │
    ├── home/
    │   ├── HomeActivity.java      # Grid browse + search + brand filter
    │   ├── PhoneAdapter.java      # GridView adapter for phone cards
    │   ├── FavoritesActivity.java
    │   ├── FavoriteAdapter.java
    │   ├── CartActivity.java
    │   ├── CartAdapter.java       # ListView adapter with qty +/- controls
    │   ├── PaymentActivity.java   # Checkout form + order placement
    │   └── OrderHistoryActivity.java
    │
    └── detail/
        └── PhoneDetailActivity.java  # Full phone spec + Add to Cart / Favorite
```

---

## Features

| Feature | Where |
|---|---|
| Register / Login / Guest browse | `LandingActivity`, `LoginActivity`, `RegisterActivity` |
| Session persistence across app restarts | `SessionManager` (SharedPreferences) |
| Browse phones in a grid | `HomeActivity` + `PhoneAdapter` |
| Filter by brand tag | `HomeActivity.setupTags()` |
| Live search by name / brand | `HomeActivity.setupSearch()` |
| View full phone specs | `PhoneDetailActivity` |
| Add / remove favorites | `PhoneDetailActivity`, `FavoritesActivity` |
| Add to cart, adjust quantity, remove | `CartActivity` + `CartAdapter` |
| Push notification on add-to-cart | `PhoneDetailActivity.showNotification()` |
| Checkout with card form | `PaymentActivity` |
| Order history | `OrderHistoryActivity` |

---

## Data Persistence

### Local (Room)

Room is an SQLite ORM. Each `@Entity` class maps to a table, and each `@Dao` interface provides type-safe SQL queries.

| Entity | Table | Key fields |
|---|---|---|
| `User` | `users` | username, email, password |
| `FavoritePhone` | `favorites` | name (used as lookup key) |
| `CartItem` | `cart_items` | name, quantity, price |
| `Order` | `orders` | date, total, itemsSummary |

`AppDatabase` is a singleton accessed everywhere via `AppDatabase.getInstance(context)`.

### Repository Pattern

Each repository wraps its DAO and Firestore calls together. The UI only talks to repositories — it never touches DAOs or Firestore directly.

```
UI Activity
    └── Repository        (coordinates local + cloud)
            ├── Room DAO  (fast, offline-first reads/writes)
            └── FirestoreManager  (async cloud sync)
```

All Room operations run on background threads (`new Thread(() -> { ... }).start()`). UI updates always call `runOnUiThread(...)`.

---

## Firebase & Firestore

### Authentication

`FirebaseAuthManager.signInAnonymously()` is called once on the landing screen. This gives every user (even guests) a unique `uid` in Firebase Auth, which is used as the Firestore document key.

For registered users, `UserRepository` calls `auth.createUserWithEmailAndPassword(...)` on register and `auth.signInWithEmailAndPassword(...)` on login.

### Firestore Structure

```
users/
  {uid}/
    (user profile fields — username, email)
    favorites/
      {phoneName}/   ← document per phone
    cart/
      data/          ← single document wrapping List<CartItem>
    orders/
      {orderId}/     ← document per order
```

### Sync Strategy

The app uses an **offline-first** approach:

1. **Writes** — Room is updated immediately (fast, works offline), then Firestore is synced in the same background thread.
2. **Reads** — Room is the source of truth for the UI. Firestore is pulled once on screen open (`syncFromCloud`) to catch data from other devices, then merged into Room.

```
// Write flow (e.g. add favorite)
dao.insert(phone);                  // 1. write local instantly
FirestoreManager.syncFavorite(phone); // 2. push to cloud async

// Read flow (e.g. open Favorites screen)
favorites = dao.getAll();           // 1. show local data immediately
repository.syncFromCloud(() -> {    // 2. pull cloud data in background
    loadFavorites();                //    then refresh UI
});
```

### Key Firestore Methods

| Method | Purpose |
|---|---|
| `syncUser(user)` | Upsert user profile on register |
| `syncFavorite(phone)` | Add/update a favorite document |
| `removeFavorite(name)` | Delete a favorite document |
| `downloadFavorites(callback)` | Fetch all favorites for current user |
| `syncCart(items)` | Overwrite the cart document with current list |
| `syncOrder(order)` | Save a placed order |
| `downloadOrders(callback)` | Fetch all orders (for new device restore) |
| `downloadUserByUsername/Email` | Look up a user on login from a new device |

### Why Anonymous Auth?

Even guests get an anonymous `uid`, so the app can always safely call `FirebaseAuth.getInstance().getCurrentUser().getUid()` without null-checking for login state before every Firestore operation.

---

## Known Simplifications (good to improve later)

- Passwords are stored in plain text in Room and Firestore while a real app would hash them.
- `fallbackToDestructiveMigration()` wipes the local database on schema changes instead of migrating.
- Cart sync overwrites the entire cart document each time rather than doing per-item updates.
- `OrderRepository.syncFromCloud` does not deduplicate, repeated syncs on the same device would insert duplicate orders.
- No LiveData or ViewModel: Activities directly query Room on background threads and update UI manually.
