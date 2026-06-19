package com.uitm.ict602.goldzakat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.uitm.ict602.goldzakat.databinding.ActivityMainBinding;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    // Uruf (Nisab) constants in grams
    private static final double URUF_KEEP = 85.0;
    private static final double URUF_WEAR = 200.0;

    // Zakat rate (2.5%)
    private static final double ZAKAT_RATE = 0.025;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Show app title on the ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
        }

        setupSpinner();
        setupCalculateButton();
    }

    /** Sets up the Gold Type spinner adapter (Keep / Wear). */
    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.gold_type_array,
                R.layout.spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerGoldType.setAdapter(adapter);
    }

    /** Attaches the click listener to the Calculate button. */
    private void setupCalculateButton() {
        binding.btnCalculate.setOnClickListener(v -> calculateZakat());
    }

    /**
     * Validates user input and, if valid, calculates and displays
     * the Zakat values. Friendly inline errors are shown on invalid input.
     */
    private void calculateZakat() {
        // Clear any previous errors
        binding.tilWeight.setError(null);
        binding.tilGoldValue.setError(null);

        String weightInput = binding.etWeight.getText() != null
                ? binding.etWeight.getText().toString().trim() : "";
        String valueInput = binding.etGoldValue.getText() != null
                ? binding.etGoldValue.getText().toString().trim() : "";

        boolean hasError = false;

        // --- Validate weight field ---
        Double weight = null;
        if (TextUtils.isEmpty(weightInput)) {
            binding.tilWeight.setError(getString(R.string.error_weight_empty));
            hasError = true;
        } else {
            try {
                weight = Double.parseDouble(weightInput);
                if (weight < 0) {
                    binding.tilWeight.setError(getString(R.string.error_weight_negative));
                    hasError = true;
                }
            } catch (NumberFormatException e) {
                binding.tilWeight.setError(getString(R.string.error_weight_invalid));
                hasError = true;
            }
        }

        // --- Validate gold value field ---
        Double goldValue = null;
        if (TextUtils.isEmpty(valueInput)) {
            binding.tilGoldValue.setError(getString(R.string.error_value_empty));
            hasError = true;
        } else {
            try {
                goldValue = Double.parseDouble(valueInput);
                if (goldValue < 0) {
                    binding.tilGoldValue.setError(getString(R.string.error_value_negative));
                    hasError = true;
                }
            } catch (NumberFormatException e) {
                binding.tilGoldValue.setError(getString(R.string.error_value_invalid));
                hasError = true;
            }
        }

        if (hasError || weight == null || goldValue == null) {
            return;
        }

        // --- Determine Uruf based on selected gold type ---
        // Spinner index 0 = Keep (85g), index 1 = Wear (200g)
        int selectedType = binding.spinnerGoldType.getSelectedItemPosition();
        double uruf = (selectedType == 0) ? URUF_KEEP : URUF_WEAR;

        // --- Core calculations ---
        double totalGoldValue = weight * goldValue;
        double zakatPayableWeight = Math.max(weight - uruf, 0);
        double zakatPayableValue = zakatPayableWeight * goldValue;
        double totalZakat = zakatPayableValue * ZAKAT_RATE;

        // --- Display results formatted as RM currency ---
        binding.tvTotalGoldValue.setText(formatCurrency(totalGoldValue));
        binding.tvZakatPayableValue.setText(formatCurrency(zakatPayableValue));
        binding.tvTotalZakat.setText(formatCurrency(totalZakat));
    }

    /** Formats a double value as an RM currency string, e.g. "RM 1,234.56". */
    private String formatCurrency(double value) {
        return "RM " + String.format(Locale.US, "%,.2f", value);
    }

    // ----------------------------------------------------------------
    // ActionBar menu: Share + About
    // ----------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_share) {
            shareApp();
            return true;
        } else if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** Launches the Android Share Intent with the app's promo text. */
    private void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text));
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_chooser_title)));
    }
}
