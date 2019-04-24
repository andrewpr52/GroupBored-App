package com.terminalreach.groupbored;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class Tab1 extends Fragment {
    private List<String> list = new ArrayList<>();
    int listSelection;

    public Tab1() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_tab1, container, false);

        final ListView listView = rootView.findViewById(R.id.groups_list_view);
        list.add("Followed Users");
        list.add("All Posts");
        list.add("General");
        list.add("Games");
        list.add("News/Politics");
        list.add("Science");
        list.add("Sports");
        list.add("Technology");

        if (getActivity() != null) {
            ArrayAdapter arrAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, list);
            listView.setAdapter(arrAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                    listSelection = i;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (getFragmentManager() != null) {
                                Fragment fragment = getFragmentManager().findFragmentById(R.id.fragTab2);
                                if (fragment != null) {
                                    getFragmentManager().beginTransaction().remove(fragment).commit();
                                }
                            }

                            Tab2 mfragment = new Tab2();
                            android.support.v4.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.fragTab2, mfragment);
                            transaction.commit();

                            Bundle bundle = new Bundle();
                            bundle.putString("groupSelection", list.get(i));
                            mfragment.setArguments(bundle); //data being send to SecondFragment
                        }
                    }, 300);

                    // Gets the viewPager (pager) created in MainActivity and changes the current tab to 1
                    if (getActivity() != null) {
                        ViewPager mPager = getActivity().findViewById(R.id.pager);
                        mPager.setCurrentItem(1);
                    }
                }
            });
        }

        // Inflate the layout for this fragment
        return rootView;
    }
}
