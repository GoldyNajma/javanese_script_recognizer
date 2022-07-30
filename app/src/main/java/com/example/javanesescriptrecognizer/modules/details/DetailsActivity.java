package com.example.javanesescriptrecognizer.modules.details;

import com.example.javanesescriptrecognizer.R;
import com.example.javanesescriptrecognizer.base.BaseFragmentHolderActivity;

public class DetailsActivity extends BaseFragmentHolderActivity {
    DetailsFragment detailsFragment;
    private final int UPDATE_REQUEST = 2019;

    @Override
    protected void initializeFragment() {
//        initializeView();

        setTitle(getString(R.string.details_title));

        detailsFragment = new DetailsFragment();
        setCurrentFragment(detailsFragment, false);

        setTitle(getString(R.string.details_title));
    }
}
