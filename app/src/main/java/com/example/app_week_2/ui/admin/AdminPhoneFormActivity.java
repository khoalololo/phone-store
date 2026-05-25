package com.example.app_week_2.ui.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app_week_2.R;
import com.example.app_week_2.data.repository.PhoneRepository;
import com.example.app_week_2.models.Phone;

public class AdminPhoneFormActivity extends AppCompatActivity {

    private PhoneRepository phoneRepository;
    private Phone editingPhone;

    private EditText fieldBrand, fieldName, fieldImageName, fieldPrice, fieldRating,
            fieldDescription, fieldStorage, fieldBattery, fieldDisplay,
            fieldChipset, fieldCamera, fieldCharging, fieldOs, fieldFeatures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_phone_form);

        phoneRepository = new PhoneRepository(this);

        fieldBrand       = findViewById(R.id.fieldBrand);
        fieldName        = findViewById(R.id.fieldName);
        fieldImageName   = findViewById(R.id.fieldImageName);
        fieldPrice       = findViewById(R.id.fieldPrice);
        fieldRating      = findViewById(R.id.fieldRating);
        fieldDescription = findViewById(R.id.fieldDescription);
        fieldStorage     = findViewById(R.id.fieldStorage);
        fieldBattery     = findViewById(R.id.fieldBattery);
        fieldDisplay     = findViewById(R.id.fieldDisplay);
        fieldChipset     = findViewById(R.id.fieldChipset);
        fieldCamera      = findViewById(R.id.fieldCamera);
        fieldCharging    = findViewById(R.id.fieldCharging);
        fieldOs          = findViewById(R.id.fieldOs);
        fieldFeatures    = findViewById(R.id.fieldFeatures);

        Button saveBtn = findViewById(R.id.savePhoneBtn);

        editingPhone = (Phone) getIntent().getSerializableExtra("phone");
        if (editingPhone != null) {
            ((TextView) findViewById(R.id.formTitle)).setText("Edit Phone");
            saveBtn.setText("Update Phone");
            prefillFields(editingPhone);
        }

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        saveBtn.setOnClickListener(v -> savePhone());
    }

    private void prefillFields(Phone phone) {
        fieldBrand.setText(phone.getBrand());
        fieldName.setText(phone.getName());
        fieldImageName.setText(phone.getImageName());
        fieldPrice.setText(String.valueOf(phone.getPrice()));
        fieldRating.setText(String.valueOf(phone.getRating()));
        fieldDescription.setText(phone.getDescription());
        fieldStorage.setText(phone.getStorage());
        fieldBattery.setText(phone.getBattery());
        fieldDisplay.setText(phone.getDisplay());
        fieldChipset.setText(phone.getChipset());
        fieldCamera.setText(phone.getCamera());
        fieldCharging.setText(phone.getCharging());
        fieldOs.setText(phone.getOs());
        fieldFeatures.setText(phone.getFeatures());
    }

    private void savePhone() {
        String brand       = fieldBrand.getText().toString().trim();
        String name        = fieldName.getText().toString().trim();
        String imageName   = fieldImageName.getText().toString().trim();
        String priceStr    = fieldPrice.getText().toString().trim();
        String ratingStr   = fieldRating.getText().toString().trim();
        String description = fieldDescription.getText().toString().trim();
        String storage     = fieldStorage.getText().toString().trim();
        String battery     = fieldBattery.getText().toString().trim();
        String display     = fieldDisplay.getText().toString().trim();
        String chipset     = fieldChipset.getText().toString().trim();
        String camera      = fieldCamera.getText().toString().trim();
        String charging    = fieldCharging.getText().toString().trim();
        String os          = fieldOs.getText().toString().trim();
        String features    = fieldFeatures.getText().toString().trim();

        // Basic validation
        if (brand.isEmpty() || name.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Brand, name and price are required", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        float rating;
        try {
            price  = Double.parseDouble(priceStr);
            rating = ratingStr.isEmpty() ? 0f : Float.parseFloat(ratingStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Price and rating must be numbers", Toast.LENGTH_SHORT).show();
            return;
        }

        // Build the Phone object
        // ID: use existing ID if editing, otherwise generate from name
        String id = editingPhone != null
                ? editingPhone.id
                : name.toLowerCase().replace(" ", "_");

        Phone phone = new Phone(id, brand, name, imageName, price, storage, battery,
                display, os, description, rating, chipset, camera,
                charging, features);

        //seedDatabase handles both Room insert + Firestore upload
        phoneRepository.savePhone(phone);

        Toast.makeText(this,
                editingPhone != null ? "Phone updated!" : "Phone added!",
                Toast.LENGTH_SHORT).show();

        finish(); // go back to dashboard
    }
}