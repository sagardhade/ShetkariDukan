package com.grocery.dukan.Fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.grocery.dukan.Activity.HomeActivity;
import com.grocery.dukan.Adepter.ItemAdp;
import com.grocery.dukan.DataBase.DatabaseHelper;
import com.grocery.dukan.DataBase.MyCart;
import com.grocery.dukan.Model.Product;
import com.grocery.dukan.Model.ProductItem;
import com.grocery.dukan.R;
import com.grocery.dukan.Utils.SessionManager;
import com.grocery.dukan.retrofit.APIClient;
import com.grocery.dukan.retrofit.GetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;

import static com.grocery.dukan.Utils.SessionManager.CURRUNCY;
import static com.grocery.dukan.Utils.SessionManager.ISCART;

public class ItemListFragment extends Fragment implements GetResult.MyListener {
    @BindView(R.id.lvlbacket)
    LinearLayout lvlbacket;
    @BindView(R.id.txt_item)
    TextView txtItem;
    @BindView(R.id.txt_price)
    TextView txtPrice;
    @BindView(R.id.my_recycler_view)
    RecyclerView myRecyclerView;

    @BindView(R.id.lvl_nodata)
    LinearLayout lvlNodata;

    @BindView(R.id.txt_nodatatitle)
    TextView txtNodatatitle;

    Unbinder unbinder;

    ItemAdp itemAdp;
    List<ProductItem> productDataList;
    int CID = 0;
    int SCID = 0;
    SessionManager sessionManager;

    private StaggeredGridLayoutManager gaggeredGridLayoutManager;

    public static ItemListFragment itemListFragment;

    public static ItemListFragment getInstance() {
        return itemListFragment;
    }

    public ItemListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        Bundle b = getArguments();
        databaseHelper = new DatabaseHelper(getActivity());
        itemListFragment = this;
        sessionManager=new SessionManager(getActivity());
        CID = b.getInt("cid");
        SCID = b.getInt("scid");
        myRecyclerView.setHasFixedSize(true);
        productDataList = new ArrayList<>();
        gaggeredGridLayoutManager = new StaggeredGridLayoutManager(1, 1);
        myRecyclerView.setLayoutManager(gaggeredGridLayoutManager);
        itemAdp = new ItemAdp(getActivity(), productDataList);
        myRecyclerView.setAdapter(itemAdp);

        if (CID == 0) {
            String keyword = b.getString("search");
            if (keyword.trim().length() != 0) {
                getSearch(keyword);
            } else {
                getFragmentManager().popBackStackImmediate();
            }
        } else {
            getProduct();
        }

        Cursor res = databaseHelper.getAllData();
        if (res.getCount() == 0) {
            lvlbacket.setVisibility(View.GONE);
        } else {
            lvlbacket.setVisibility(View.VISIBLE);
            updateItem();
        }
        return view;
    }

    public void updateItem() {
        try {
            Cursor res = databaseHelper.getAllData();
            if (res.getCount() == 0) {
                lvlbacket.setVisibility(View.GONE);
            } else {
                lvlbacket.setVisibility(View.VISIBLE);

                double totalRs = 0;
                double ress = 0;
                double totalItem = 0;
                while (res.moveToNext()) {
                    MyCart rModel = new MyCart();
                    rModel.setCost(res.getString(5));
                    rModel.setQty(res.getString(6));
                    rModel.setDiscount(res.getInt(7));
                    ress = (Double.parseDouble(res.getString(5)) * rModel.getDiscount()) / 100;
                    ress = Double.parseDouble(res.getString(5)) - ress;
                    double temp = Integer.parseInt(res.getString(6)) * ress;
                    totalItem = totalItem + Integer.parseInt(res.getString(6));
                    totalRs = totalRs + temp;
                }
//            txtPrice.setText("Total : " + totalRs);

                txtItem.setText("Total Item:(" + new DecimalFormat("##.##").format(totalItem) + ")");
                txtPrice.setText(sessionManager.getStringData(CURRUNCY) + totalRs);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void getProduct() {


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cid", CID);
            jsonObject.put("sid", SCID);
            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().getGetProduct((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "1");
            HomeActivity.custPrograssbar.PrograssCreate(getActivity());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getSearch(String key) {
        Log.e("searchKey===", key + "");
        if (key.length() == 0) {

        }
        HomeActivity.custPrograssbar.PrograssCreate(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("keyword", key);
            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().getSearch((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        HomeActivity.custPrograssbar.ClosePrograssBar();
        JSONObject jsonObject;

        try {
            Gson gson = new Gson();
            jsonObject = new JSONObject(result.toString());
            Product product = gson.fromJson(jsonObject.toString(), Product.class);
            productDataList.clear();

            if (product.getResult().equals("true")) {
                productDataList.addAll(product.getData());
                lvlNodata.setVisibility(View.GONE);

            } else {
                lvlNodata.setVisibility(View.VISIBLE);
                txtNodatatitle.setText(""+product.getResponseMsg());
            }
            itemAdp.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.img_cart);
        if (item != null)
            item.setVisible(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (ISCART) {
            ISCART = false;
            CardFragment fragment = new CardFragment();
            HomeActivity.getInstance().callFragment(fragment);
        } else if (itemAdp != null) {
            itemAdp.notifyDataSetChanged();
        }
        HomeActivity.getInstance().serchviewHideOrShow(true);
        HomeActivity.getInstance().setFrameMargin(60);
    }

    @OnClick(R.id.txt_gocart)
    public void onViewClicked() {
        CardFragment fragment = new CardFragment();
        HomeActivity.getInstance().callFragment(fragment);
    }
}
