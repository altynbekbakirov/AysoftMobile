package kz.burhancakmak.aysoftmobile.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.jetbrains.annotations.NotNull;

import kz.burhancakmak.aysoftmobile.Clients.InfoFragment;
import kz.burhancakmak.aysoftmobile.Clients.KasaFragment;
import kz.burhancakmak.aysoftmobile.Clients.PhotoFragment;
import kz.burhancakmak.aysoftmobile.Clients.SiparisFragment;
import kz.burhancakmak.aysoftmobile.Clients.VisitFragment;

public class FragmentAdapter extends FragmentStateAdapter {
    public FragmentAdapter(@NonNull @NotNull FragmentManager fragmentManager, @NonNull @NotNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new InfoFragment();
            case 1:
                return new VisitFragment();
            case 2:
                return new SiparisFragment();
            case 3:
                return new KasaFragment();
            case 4:
                return new PhotoFragment();
        }
        return new InfoFragment();
    }

    @Override
    public int getItemCount() {
        return 5;
    }


}
