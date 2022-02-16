package com.grocery.dukan.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.grocery.dukan.Activity.HomeActivity;
import com.grocery.dukan.Adepter.SubCategoryAdp;
import com.grocery.dukan.Model.SubCategory;
import com.grocery.dukan.Model.SubcatItem;
import com.grocery.dukan.Model.User;
import com.grocery.dukan.R;
import com.grocery.dukan.Utils.SessionManager;
import com.grocery.dukan.retrofit.APIClient;
import com.grocery.dukan.retrofit.GetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;

import static com.grocery.dukan.Activity.HomeActivity.homeActivity;


public class SubCategoryFragment extends Fragment implements GetResult.MyListener, SubCategoryAdp.RecyclerTouchListener {
    // TODO: Rename parameter arguments, choose names that match

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.lvl_nodata)
    LinearLayout lvlNodata;

    @BindView(R.id.txt_nodatatitle)
    TextView txtNodatatitle;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    SubCategoryAdp adapter;
    List<SubcatItem> categoryList;
    Unbinder unbinder;
    SessionManager sessionManager;
    User user;
    int CID = 0;

    public SubCategoryFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SubCategoryFragment newInstance(String param1, String param2) {
        SubCategoryFragment fragment = new SubCategoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_subcategory, container, false);
        unbinder = ButterKnife.bind(this, view);
        Bundle b = getArguments();
        CID = b.getInt("id");

        categoryList = new ArrayList<>();
        sessionManager = new SessionManager(getActivity());
        user = sessionManager.getUserDetails("");


        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        getSubCategory();
        return view;
    }

    @Override
    public void onClickItem(View v, int cid, int scid) {
        homeActivity.ShowMenu();
        Bundle args = new Bundle();
        args.putInt("cid", cid);
        args.putInt("scid", scid);
        Fragment fragment = new ItemListFragment();
        fragment.setArguments(args);
        HomeActivity.getInstance().callFragment(fragment);
    }

    @Override
    public void onLongClickItem(View v, int position) {

    }

    private void getSubCategory() {
        HomeActivity.custPrograssbar.PrograssCreate(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("category_id", CID);
            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().getSubcategory((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {


            HomeActivity.custPrograssbar.ClosePrograssBar();
            if (callNo.equalsIgnoreCase("1") && result.toString() != null) {
                Gson gson = new Gson();
                SubCategory category = gson.fromJson(result.toString(), SubCategory.class);
                if (category.getResult().equalsIgnoreCase("true")) {
                    lvlNodata.setVisibility(View.GONE);
                    categoryList = category.getData();
                    adapter = new SubCategoryAdp(getActivity(), categoryList, this);
                    recyclerView.setAdapter(adapter);
                } else {
                    lvlNodata.setVisibility(View.VISIBLE);
                    txtNodatatitle.setText("" + category.getResponseMsg());
                }

            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        HomeActivity.getInstance().serchviewHideOrShow(true);
        HomeActivity.getInstance().setFrameMargin(60);
    }
}
