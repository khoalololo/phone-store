# PhoneStore App - Week 2 (Enhanced)

A robust Android e-commerce application for browsing, favoriting, and purchasing phones. Features local Room storage with high-performance background processing and seamless Firebase/Firestore cloud synchronization.

---

## Layer / Package Structure

```
com.example.app_week_2/
│
├── data/                          # Data layer
│   ├── AppDatabase.java           # Room database definition (version 5)
│   ├── UserDao.java               # SQL queries for users
│   ├── FavoriteDao.java           # SQL queries for favorites
│   ├── CartDao.java               # SQL queries for cart items
│   ├── OrderDao.java              # SQL queries for orders
│   ├── ReviewDao.java             # SQL queries for user reviews & ratings
│   ├── PhoneProvider.java         # Static master catalog of phones
│   ├── SessionManager.java        # SharedPreferences session management
│   │
│   ├── remote/
│   │   ├── FirebaseAuthManager.java   # Firebase Auth orchestration
│   │   └── FirestoreManager.java      # Firestore cloud operations
│   │
│   └── repository/
│       ├── UserRepository.java        # Auth & Profile coordination
│       ├── FavoriteRepository.java    # Favorites with Data Repair logic
│       ├── CartRepository.java        # Persistent shopping cart management
│       ├── OrderRepository.java       # Secure order placement
│       └── PhoneRepository.java       # Catalog management with seeding
│
├── models/                        # Plain data models
│   ├── Phone.java                 # Core catalog model
│   ├── User.java                  # User entity
│   ├── FavoritePhone.java         # Favorite entity with legacy mapping
│   ├── CartItem.java              # Cart entity
│   ├── Order.java                 # Order entity
│   └── Review.java                # Review entity (phoneId, rating, comment)
│
└── ui/                            # UI layer (Activities + Enhanced Adapters)
    ├── auth/
    │   ├── LandingActivity.java   # Optimized entry screen
    │   ├── LoginActivity.java
    │   ├── RegisterActivity.java
    │   └── ProfileActivity.java
    │
    ├── home/
    │   ├── HomeActivity.java      # Grid browse + threading-optimized loading
    │   ├── PhoneAdapter.java      # GridView adapter for phone cards
    │   ├── FavoritesActivity.java
    │   ├── FavoriteAdapter.java   # Optimized adapter with Live Image Repair
    │   ├── CartActivity.java
    │   ├── CartAdapter.java       # ListView adapter with qty controls
    │   ├── PaymentActivity.java   # Secure checkout form
    │   ├── OrderHistoryActivity.java
    │   └── OrderAdapter.java      # Premium card-based Order UI
    │
    └── detail/
        └── PhoneDetailActivity.java  # Specs + User Review System
```

---

## New & Enhanced Features

| Feature | Description | Implementation |
|---|---|---|
| **User Review System** | Users can rate (1-5 stars) and comment on phones. | `PhoneDetailActivity` + `ReviewDao` |
| **Dynamic Ratings** | Average rating is recalculated instantly on review submission. | `ReviewDao.getAverageRating()` |
| **Premium Order History** | Redesigned card-based UI with Order IDs and status badges. | `OrderAdapter` + `item_order.xml` |
| **Threading Architecture** | All DB operations moved to background threads to prevent UI lock. | `new Thread()` + `runOnUiThread()` |
| **Auto-Seeding Catalog** | App automatically seeds local DB if empty to ensure availability. | `PhoneRepository.seedDatabase()` |
| **Live Image Repair** | Automatically fixes missing/mismatched images in Favorites. | `FavoriteAdapter` (Live Lookup) |
| **Legacy Data Support** | Compatible with older Firestore fields like `imageResource`. | `FavoritePhone` (Setters) |

---

## Data Persistence & Resilience

### Local (Room)

| Entity | Table | Responsibility |
|---|---|---|
| `User` | `users` | Local profile storage |
| `FavoritePhone`| `favorites` | Offline-access favorites |
| `CartItem` | `cart_items` | Active shopping session |
| `Order` | `orders` | Permanent transaction record |
| `Review` | `reviews` | Community feedback storage |

### The "Resilience" Strategy

The application implements a **Defensive Sync** pattern:
1. **Initial Load**: Room provides immediate data to the UI.
2. **Cloud Merge**: Firestore pulls latest changes and updates existing Room records.
3. **Data Repair**: During sync or UI binding, if critical data (like an image name) is missing, the app automatically recovers it from the static `PhoneProvider` catalog based on the item name.

---

## Firestore Sync Strategy

```
UI Activity
    └── Repository (Source of Truth Coordinator)
            ├── Room DAO (Immediate read/write)
            └── FirestoreManager (Background cloud backup)
```

- **Offline-First**: User actions (like adding a favorite) update Room instantly. The Firestore sync happens in parallel on the same background thread.
- **Background Seeding**: Catalog data is uploaded to Firestore automatically on the first launch, ensuring the cloud catalog is always up-to-date.

---

## User Review & Rating Logic

The `PhoneDetailActivity` now features a dual-purpose system:
- **Write**: Users must be logged in to submit a review. Submissions are persisted locally and pushed to the `reviews` collection in Firestore.
- **Read**: All reviews for a specific `phoneId` are fetched. The app performs a SQL `AVG(rating)` to update the phone's primary star rating displayed at the top of the card.
