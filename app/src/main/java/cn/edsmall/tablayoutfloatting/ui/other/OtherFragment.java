package cn.edsmall.tablayoutfloatting.ui.other;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import cn.edsmall.router_annotation.destination.FragmentDestination;
import cn.edsmall.tablayoutfloatting.R;

@FragmentDestination(pageUrl = "main/tabs/other",asStarter = false)
public class OtherFragment extends Fragment {

    private OtherViewModel ohterViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ohterViewModel =
                ViewModelProviders.of(this).get(OtherViewModel.class);
        View root = inflater.inflate(R.layout.fragment_other, container, false);
        final TextView textView = root.findViewById(R.id.text_other);
        ohterViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        Log.e("tag","OtherFragment onCreateView");
        return root;
    }
}