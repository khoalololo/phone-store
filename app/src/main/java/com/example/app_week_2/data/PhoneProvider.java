package com.example.app_week_2.data;

import com.example.app_week_2.models.Phone;
import java.util.ArrayList;
import java.util.List;

public class PhoneProvider {
    public static List<Phone> getPhones() {
        List<Phone> phones = new ArrayList<>();
        
        phones.add(new Phone(
                "Apple", "iPhone 15 Pro", "iphone", 999.00,
                "256GB / 512GB / 1TB", "3274 mAh", "6.1-inch LTPO Super Retina XDR, 120Hz", "iOS 17",
                "The iPhone 15 Pro features a strong and light aerospace-grade titanium design with contoured edges, a new Action button, and a powerful camera system.",
                4.8f,
                "A17 Pro (3nm)",
                "48MP Main + 12MP Telephoto + 12MP Ultrawide",
                "20W Wired, 15W MagSafe Wireless",
                "IP68, FaceID, USB-C 3.0, Action Button"
        ));

        phones.add(new Phone(
                "Samsung", "Galaxy S24 Ultra", "samsung", 1199.00,
                "256GB / 512GB / 1TB", "5000 mAh", "6.8-inch Dynamic AMOLED 2X, 120Hz, 2600 nits", "Android 14",
                "Galaxy S24 Ultra features Galaxy AI, an integrated S Pen, and a 200MP camera system for professional-grade photography and productivity.",
                4.9f,
                "Snapdragon 8 Gen 3 for Galaxy",
                "200MP Main + 50MP Periscope + 10MP Telephoto + 12MP Ultrawide",
                "45W Wired, 15W Wireless",
                "Titanium Frame, IP68, S Pen, Ray Tracing"
        ));

        phones.add(new Phone(
                "Google", "Pixel 8 Pro", "pixel", 899.00,
                "128GB / 256GB / 512GB", "5050 mAh", "6.7-inch Super Actua Display, 1-120Hz", "Android 14",
                "Pixel 8 Pro is the all-pro phone engineered by Google. It's sleek, sophisticated, and has a powerful camera system for amazing photos and videos.",
                4.7f,
                "Google Tensor G3",
                "50MP Main + 48MP Ultrawide + 48MP Telephoto",
                "30W Wired, 23W Wireless",
                "AI Photo Editor, Magic Eraser, Temperature Sensor"
        ));

        phones.add(new Phone(
                "OnePlus", "12R", "oneplus", 499.00,
                "128GB / 256GB", "5500 mAh", "6.78-inch LTPO4 AMOLED, 1.5K, 4500 nits", "OxygenOS 14",
                "The OnePlus 12R is the perfect balance of power and value, featuring a stunning display and lightning-fast 80W SUPERVOOC charging.",
                4.5f,
                "Snapdragon 8 Gen 2",
                "50MP Main + 8MP Ultrawide + 2MP Macro",
                "80W SUPERVOOC Wired",
                "Dual Cryo-velocity Cooling, Alert Slider, 5G"
        ));

        phones.add(new Phone(
                "Apple", "iPhone 14", "iphone14", 699.00,
                "128GB / 256GB / 512GB", "3279 mAh", "6.1-inch Super Retina XDR OLED", "iOS 16",
                "iPhone 14 comes with the most impressive dual-camera system on iPhone, featuring Photonic Engine for incredible detail and color.",
                4.6f,
                "A15 Bionic (5nm)",
                "12MP Main + 12MP Ultrawide",
                "20W Wired, 15W MagSafe Wireless",
                "Emergency SOS via Satellite, Crash Detection"
        ));

        phones.add(new Phone(
                "Samsung", "Galaxy A55", "samsung_a55", 449.00,
                "128GB / 256GB", "5000 mAh", "6.6-inch Super AMOLED, 120Hz, Vision Booster", "Android 14",
                "The Galaxy A55 5G combines iconic design with a powerful octa-core processor and Knox Vault for security and performance.",
                4.4f,
                "Exynos 1480 (4nm)",
                "50MP Main + 12MP Ultrawide + 5MP Macro",
                "25W Fast Wired Charging",
                "Metal Frame, IP67, Gorilla Glass Victus+"
        ));

        phones.add(new Phone(
                "Sony", "Xperia 1 V", "sony", 1399.00,
                "256GB / 512GB", "5000 mAh", "6.5-inch 4K HDR OLED, 120Hz, 21:9 ratio", "Android 13",
                "The Xperia 1 V's revolutionary Exmor T for mobile image sensor has an innovative 2-Layer Transistor Pixel for great low-light performance.",
                4.3f,
                "Snapdragon 8 Gen 2",
                "52MP Main + 12MP Telephoto + 12MP Ultrawide",
                "30W Wired, 15W Wireless",
                "Zeiss Optics, 4K 120fps Video, MicroSD Slot"
        ));

        phones.add(new Phone(
                "Xiaomi", "14 Ultra", "xiaomi", 1099.00,
                "512GB", "5000 mAh", "6.73-inch LTPO AMOLED, WQHD+, 3000 nits", "HyperOS",
                "Xiaomi 14 Ultra features a Leica Summilux optical lens, a 1-inch main camera, and WQHD+ AMOLED display for an ultimate flagship experience.",
                4.7f,
                "Snapdragon 8 Gen 3",
                "50MP Main (1-inch) + 50MP Tele + 50MP Periscope + 50MP Ultrawide",
                "90W Wired HyperCharge, 80W Wireless",
                "Leica Optics, IP68, Xiaomi Shield Glass"
        ));

        return phones;
    }
}
