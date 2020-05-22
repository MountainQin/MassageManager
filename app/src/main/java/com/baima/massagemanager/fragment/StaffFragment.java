package com.baima.massagemanager.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baima.massagemanager.R;

public class StaffFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_staff, container, false);
        TextView tv_search=view.findViewById(R.id.tv_search);
        TextView tv_add=view.findViewById(R.id.tv_add);
        ListView lv_staff=view.findViewById(R.id.lv_staff);

        tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "search staff", Toast.LENGTH_SHORT).show();
            }
        });
        tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "add staff", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
