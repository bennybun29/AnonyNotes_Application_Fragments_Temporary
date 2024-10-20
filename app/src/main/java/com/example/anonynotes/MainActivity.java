package com.example.anonynotes;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.anonynotes.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        binding.bottomNavMenuNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.Home_Button_Nav_Menu) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.Write_Button_Nav_Menu) {
                replaceFragment(new WriteFragment());
            } else if (itemId == R.id.Profile_Button_Nav_Menu) {
                replaceFragment(new ProfileFragment());
            }
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check if the intent has extra to select the Profile tab
        Intent intent = getIntent();
        boolean selectProfileTab = intent.getBooleanExtra("selectProfileTab", false);
        boolean selectHomeTab = intent.getBooleanExtra("selectHomeTab", false);

        if (selectProfileTab) {
            // Programmatically select the Profile tab
            binding.bottomNavMenuNavigation.setSelectedItemId(R.id.Profile_Button_Nav_Menu);
            replaceFragment(new ProfileFragment());
        } else if (selectHomeTab) {
            // Programmatically select the Home tab
            binding.bottomNavMenuNavigation.setSelectedItemId(R.id.Home_Button_Nav_Menu);
            replaceFragment(new HomeFragment());
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Get the count of fragments in the back stack
        int backStackCount = fragmentManager.getBackStackEntryCount();

        // Check if we are currently on the HomeFragment
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.frame_layout);
        if (currentFragment instanceof HomeFragment) {
            // Show a confirmation dialog to exit the app
            new AlertDialog.Builder(this)
                    .setTitle("Exit")
                    .setMessage("Are you sure you want to exit AnonyNotes?")
                    .setPositiveButton("Yes", (dialog, which) -> finish()) // Close the app
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss()) // Dismiss dialog
                    .show();
        } else if (backStackCount > 0) {
            // If there are fragments in the back stack, pop the last one
            fragmentManager.popBackStack();
            // After popping, set the Home button as selected
            binding.bottomNavMenuNavigation.setSelectedItemId(R.id.Home_Button_Nav_Menu);
        } else {
            // If not on the HomeFragment and no fragments in back stack, navigate to HomeFragment
            replaceFragment(new HomeFragment());
            binding.bottomNavMenuNavigation.setSelectedItemId(R.id.Home_Button_Nav_Menu);
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
