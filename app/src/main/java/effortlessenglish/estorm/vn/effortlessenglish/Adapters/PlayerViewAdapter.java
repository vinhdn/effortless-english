package effortlessenglish.estorm.vn.effortlessenglish.Adapters;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import effortlessenglish.estorm.vn.effortlessenglish.Fragments.ContentLessionFragment;
import effortlessenglish.estorm.vn.effortlessenglish.Fragments.ListLessionFragment;
import effortlessenglish.estorm.vn.effortlessenglish.Services.PlayerService;

/**
 * Created by Vinh on 1/9/15.
 */
public class PlayerViewAdapter extends FragmentPagerAdapter {

    PlayerService service;

    public PlayerViewAdapter(FragmentManager fm,PlayerService service) {
        super(fm);
        this.service = service;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ContentLessionFragment.create(service);
            case 1:
                return ListLessionFragment.create(service);
            default:
                return ListLessionFragment.create(service);
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
