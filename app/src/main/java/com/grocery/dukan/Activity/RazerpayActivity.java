package com.grocery.dukan.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.grocery.dukan.Model.User;
import com.grocery.dukan.R;
import com.grocery.dukan.Utils.SessionManager;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import butterknife.ButterKnife;

import static com.grocery.dukan.Fragment.OrderSumrryFragment.paymentsucsses;


public class RazerpayActivity extends AppCompatActivity implements PaymentResultListener {
    SessionManager sessionManager;
    int amount = 0;

    User user;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_razorpay);
        ButterKnife.bind(this);
        sessionManager = new SessionManager(this);
        user = sessionManager.getUserDetails("");
        amount = getIntent().getIntExtra("amount", 0);
        //Log.e("amount==", "===" + amount);

        startPayment(String.valueOf(amount));
    }

    public void startPayment(String amount) {
        /**
         * You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = this;

        final Checkout co = new Checkout();

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Fresh Fast Grocery Delivery");
            //You can omit the image option to fetch the image from dashboard
            options.put("currency", "INR");

            double total = Double.parseDouble(amount);
            total = total * 100;
            options.put("amount", total);

            JSONObject preFill = new JSONObject();
            preFill.put("email", user.getEmail());
            preFill.put("contact", user.getMobile());

            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPaymentSuccess(String s) {

        Log.e("onPaymentSuccess ", "-->" + s);
        paymentsucsses = 1;
        finish();

    }

    @Override
    public void onPaymentError(int i, String s) {

        Log.e("error", "-->" + i);
        Log.e("error", "-->" + s);
        finish();
    }


}