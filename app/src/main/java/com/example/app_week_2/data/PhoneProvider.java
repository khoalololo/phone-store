package com.example.app_week_2.data;

import com.example.app_week_2.R;
import com.example.app_week_2.models.Phone;
import java.util.ArrayList;
import java.util.List;

public class PhoneProvider {
    public static List<Phone> getPhones() {
        List<Phone> phones = new ArrayList<>();
        
        phones.add(new Phone(
                "Apple", "iPhone 15 Pro", R.drawable.iphone, 999.00,
                "256GB", "3274 mAh", "6.1-inch LTPO Super Retina XDR", "iOS 17",
                "The iPhone 15 Pro features a strong and light aerospace-grade titanium design with contoured edges, a new Action button, and a powerful camera system.",
                4.8f
        ));

        phones.add(new Phone(
                "Samsung", "Galaxy S24 Ultra", R.drawable.samsung, 1199.00,
                "512GB", "5000 mAh", "6.8-inch Dynamic AMOLED 2X, 120Hz", "Android 14",
                "Galaxy S24 Ultra features Galaxy AI, an integrated S Pen, and a 200MP camera system for professional-grade photography and productivity.",
                4.9f
        ));

        phones.add(new Phone(
                "Google", "Pixel 8 Pro", R.drawable.pixel, 899.00,
                "128GB", "5050 mAh", "6.7-inch Super Actua Display", "Android 14",
                "Pixel 8 Pro is the all-pro phone engineered by Google. It's sleek, sophisticated, and has a powerful camera system for amazing photos and videos.",
                4.7f
        ));

        phones.add(new Phone(
                "OnePlus", "12R", R.drawable.oneplus, 499.00,
                "256GB", "5500 mAh", "6.78-inch LTPO4 AMOLED", "OxygenOS 14",
                "The OnePlus 12R is the perfect balance of power and value, featuring a stunning display and lightning-fast 80W SUPERVOOC charging.",
                4.5f
        ));

        phones.add(new Phone(
                "Apple", "iPhone 14", R.drawable.iphone14, 699.00,
                "128GB", "3279 mAh", "6.1-inch Super Retina XDR", "iOS 16",
                "iPhone 14 comes with the most impressive dual-camera system on iPhone, featuring Photonic Engine for incredible detail and color.",
                4.6f
        ));

        phones.add(new Phone(
                "Samsung", "Galaxy A55", R.drawable.samsung_a55, 449.00,
                "128GB", "5000 mAh", "6.6-inch Super AMOLED, 120Hz", "Android 14",
                "The Galaxy A55 5G combines iconic design with a powerful octa-core processor and Knox Vault for security and performance.",
                4.4f
        ));

        phones.add(new Phone(
                "Sony", "Xperia 1 V", R.drawable.sony, 1399.00,
                "256GB", "5000 mAh", "6.5-inch 4K HDR OLED", "Android 13",
                "The Xperia 1 V's revolutionary Exmor T for mobile image sensor has an innovative 2-Layer Transistor Pixel for great low-light performance.",
                4.3f
        ));

        phones.add(new Phone(
                "Xiaomi", "14 Ultra", R.drawable.xiaomi, 1099.00,
                "512GB", "5000 mAh", "6.73-inch LTPO AMOLED", "HyperOS",
                "Xiaomi 14 Ultra features a Leica Summilux optical lens, a 1-inch main camera, and WQHD+ AMOLED display for an ultimate flagship experience.",
                4.7f
        ));

        return phones;
    }
}
