package com.grocery.dukan.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grocery.dukan.Activity.HomeActivity;
import com.grocery.dukan.Activity.ItemDetailsActivity;
import com.grocery.dukan.Adepter.ReletedItemAllAdp;
import com.grocery.dukan.Model.ProductItem;
import com.grocery.dukan.Model.User;
import com.grocery.dukan.R;
import com.grocery.dukan.Utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.grocery.dukan.Fragment.HomeFragment.ProductDatum;


public class PopularFragment extends Fragment implements ReletedItemAllAdp.ItemClickListener {
    @BindView(R.id.recycler_view)
    RecyclerView reyCategory;
    List<String> myListData = new ArrayList<>();
    SessionManager sessionManager;
    User userData;

    public PopularFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        ButterKnife.bind(this, view);
        sessionManager = new SessionManager(getActivity());
        userData = sessionManager.getUserDetails("");

        reyCategory.setHasFixedSize(true);
        reyCategory.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        ReletedItemAllAdp itemAdp = new ReletedItemAllAdp(getActivity(), ProductDatum, this);
        reyCategory.setAdapter(itemAdp);
        return view;
    }

    @Override
    public void onItemClick(ProductItem productItem, int position) {
        getActivity().startActivity(new Intent(getActivity(), ItemDetailsActivity.class).putExtra("MyClass", productItem).putParcelableArrayListExtra("MyList", productItem.getPrice()));
    }

    @Override
    public void onResume() {
        super.onResume();
        HomeActivity.getInstance().serchviewHideOrShow(true);
        HomeActivity.getInstance().setFrameMargin(60);
    }
}
