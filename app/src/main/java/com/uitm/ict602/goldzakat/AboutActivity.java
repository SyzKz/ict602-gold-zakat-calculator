package com.uitm.ict602.goldzakat;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;

import androidx.appcompat.app.AppCompatActivity;

import com.uitm.ict602.goldzakat.databinding.ActivityAboutBinding;

/**
 * AboutActivity
 * ----------------------------------------------------------------------
 * Displays information, copyright
 * notice, and a clickable GitHub repository link.
 */
public class AboutActivity extends AppCompatActivity {

    private ActivityAboutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Show "About" title on the ActionBar with a back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.about_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Ensure the GitHub URL is clickable (autoLink alone can be unreliable
        // on some OEM skins, so we also set the movement method explicitly).
        binding.tvGithubLink.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Handles the ActionBar back arrow
        onBackPressed();
        return true;
    }
}
