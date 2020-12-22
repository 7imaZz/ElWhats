package com.shorbgy.elwhats.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.shorbgy.elwhats.ui.fragments.ChatFragment;
import com.shorbgy.elwhats.ui.fragments.ProfileFragment;
import com.shorbgy.elwhats.ui.fragments.UsersFragment;

public class MyFragmentStateAdapter extends FragmentStateAdapter{

    public MyFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new UsersFragment();
        }else if (position == 2){
            return new ProfileFragment();
        }
        return new ChatFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public String getPageTitle(int pos){
        if (pos == 1){
            return "Users";
        }else if (pos == 2){
            return "Profile";
        }
        return "Chat";
    }
}
