package com.example.onlinekonobar.ui.logout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.onlinekonobar.BossActivity;
import com.example.onlinekonobar.MainActivity;
import com.example.onlinekonobar.databinding.FragmentLogoutBinding;
import com.google.firebase.auth.FirebaseAuth;

public class LogoutFragment extends Fragment {

private FragmentLogoutBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        LogoutViewModel notificationsViewModel =
                new ViewModelProvider(this).get(LogoutViewModel.class);

    binding = FragmentLogoutBinding.inflate(inflater, container, false);
    View root = binding.getRoot();

        LogoutViewModel logoutViewModel = new ViewModelProvider(getActivity()).get(LogoutViewModel.class);
        logoutViewModel.setLogout(true);

        return root;
    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}