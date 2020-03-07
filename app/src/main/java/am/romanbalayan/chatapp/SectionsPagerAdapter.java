package am.romanbalayan.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import am.romanbalayan.chatapp.Chat.ChatsFragment;
import am.romanbalayan.chatapp.Feed.FeedFragment;

class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    public SectionsPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FeedFragment();
            case 1:
                return new ChatsFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Feed";
            case 1:
                return "Chats";
            default:
                return null;
        }
    }
}
