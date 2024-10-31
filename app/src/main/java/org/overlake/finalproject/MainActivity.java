package org.overlake.finalproject;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.overlake.finalproject.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final float ALPHA_SOLID = 1;
    private static final float ALPHA_FADED = 0.5F;
    private static final int INDEX_ABOUT_PAGE = 0;
    private static final int INDEX_INSTRUCTIONS_PAGE = 1;
    private static final int INDEX_DASHBOARD_PAGE = 2;
    private static final int INDEX_MAP_PAGE = 3;
    private static final int INDEX_RESOURCES_PAGE = 4;
    private static final int INDEX_GROCERY_TAB = 0;
    private static final int INDEX_TRANSPORTATION_TAB = 1;
    private static final int INDEX_COMMUNITY_TAB = 2;
    private static final Class[] GROCERY_DROPDOWN_CALLBACKS = new Class[]{GroceryAboutFragment.class, GroceryInstructionsFragment.class, GroceryListOfListsFragment.class, GroceryMapFragment.class, GroceryRepository.class};
    private static final Class[] TRANSPORTATION_DROPDOWN_CALLBACKS = new Class[]{TransportationAboutFragment.class, TransportationInstructionsFragment.class, TransportationDashboardFragment.class, TransportationMapFragment.class, TransportationResourcesFragment.class};
    private static final Class[] COMMUNITY_DROPDOWN_CALLBACKS = new Class[]{};
    private ActivityMainBinding binding;
    public FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        fm = getSupportFragmentManager();

        EdgeToEdge.enable(this);

        binding.menuDropdownIcon.setOnClickListener(v -> onMenuDropdownIconClicked());

        binding.footer.getMenu().getItem(INDEX_GROCERY_TAB).setOnMenuItemClickListener(item -> {
            onGroceryPage();
            item.setChecked(true);
            return true;
        });
        binding.footer.getMenu().getItem(INDEX_TRANSPORTATION_TAB).setOnMenuItemClickListener(item -> {
            onTransportationPage();
            item.setChecked(true);
            return true;
        });
        binding.footer.getMenu().getItem(INDEX_COMMUNITY_TAB).setOnMenuItemClickListener(item -> {
            onCommunityPage();
            item.setChecked(true);
            return true;
        });

        setContentView(binding.getRoot());

    }

    private void onMenuDropdownIconClicked() {
        if (binding.fragmentContainerView.findViewById(R.id.welcome_msg) != null) {
            return; // don't show the dropdown menu if user is still on welcome splash screen
        }
        if (binding.menuDropdown.getVisibility() == View.VISIBLE) {
            hideMenuDropdown();
        } else {
            showMenuDropdown();
        }
    }
    private void onGroceryPage() {
        hideMenuDropdown();
        changeMenuDropdownItems(GROCERY_DROPDOWN_CALLBACKS);
        putMainContent(GroceryAboutFragment.class, null);
    }
    private void onTransportationPage() {
        hideMenuDropdown();
        changeMenuDropdownItems(TRANSPORTATION_DROPDOWN_CALLBACKS);
        putMainContent(TransportationAboutFragment.class, null);
    }
    private void onCommunityPage() {
        hideMenuDropdown();
        changeMenuDropdownItems(COMMUNITY_DROPDOWN_CALLBACKS);
    }

    private void hideMenuDropdown() {
        binding.menuDropdown.setVisibility(View.GONE);
        binding.fragmentContainerView.setAlpha(ALPHA_SOLID);
    }
    private void showMenuDropdown() {
        binding.menuDropdown.setVisibility(View.VISIBLE);
        binding.fragmentContainerView.setAlpha(ALPHA_FADED);
    }
    private void changeMenuDropdownItems(Class[] callbacks) {
        binding.dropdownItem1.setOnClickListener(v -> putMainContent(callbacks[INDEX_ABOUT_PAGE], null));
        binding.dropdownItem2.setOnClickListener(v -> putMainContent(callbacks[INDEX_INSTRUCTIONS_PAGE], null));
        binding.dropdownItem3.setOnClickListener(v -> putMainContent(callbacks[INDEX_DASHBOARD_PAGE], null));
        binding.dropdownItem4.setOnClickListener(v -> putMainContent(callbacks[INDEX_MAP_PAGE], null));
        binding.dropdownItem5.setOnClickListener(v -> putMainContent(callbacks[INDEX_RESOURCES_PAGE], null));
    }

    public void putMainContent(Class fragment, Bundle args) {
        fm.beginTransaction()
                .replace(binding.fragmentContainerView.getId(), fragment, args, null)
                .commit();
    }


}