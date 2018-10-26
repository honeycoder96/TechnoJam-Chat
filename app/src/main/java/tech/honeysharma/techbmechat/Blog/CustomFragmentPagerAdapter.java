package tech.honeysharma.techbmechat.Blog;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class CustomFragmentPagerAdapter extends FragmentPagerAdapter {

    public CustomFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        if (position == 0) {
            return new EventsFragment();
        } else if (position == 1) {
            return new BlogsFragment();
        } else if (position == 2) {
            return new NewsFragment();
        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        String title = "";

        if (position == 0) {
            title = "Events";
        } else if (position == 1) {
            title = "Blog";
        } else if (position == 2) {
            title = "Tech News";
        }
        return title;
    }
}
