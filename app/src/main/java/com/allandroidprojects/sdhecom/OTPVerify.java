package com.allandroidprojects.sdhecom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.allandroidprojects.sdhecom.startup.MainActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.allandroidprojects.sdhecom.Api_Urls.BASE_URL;
import static com.allandroidprojects.sdhecom.Constants.API_TOKEN;
import static com.allandroidprojects.sdhecom.Constants.FCM_KEY;
import static com.allandroidprojects.sdhecom.Constants.LOGINKEY;
import static com.allandroidprojects.sdhecom.Constants.USER_STATUS;

public class OTPVerify extends AppCompatActivity {
    String mobile;
    EditText edt_otp;
    String android_id;
    String validateOtp = "validate-otp";
    String sendOtp = "send-otp";
    ViewPager viewPager;
    LinearLayout sliderDotspanel;
    private int dotscount;
    private ImageView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverify);
        mobile = getIntent().getStringExtra("mobile_no");
        edt_otp = (EditText) findViewById(R.id.edt_otp);
        android_id = Settings.Secure.getString(OTPVerify.this.getContentResolver(), Settings.Secure.ANDROID_ID);
        final ConnectionDetector connectionDetector = new ConnectionDetector(this);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        sliderDotspanel = (LinearLayout) findViewById(R.id.SliderDots);
        forBanner();

        findViewById(R.id.btn_verifyotp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivity(new Intent(OTPVerify.this, MainActivity.class));
                if (connectionDetector.isConnected()) {
                    validateOtp();
                } else {
                    Toast.makeText(OTPVerify.this, "Check InterNet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.txt_resend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectionDetector.isConnected()) {
                    edt_otp.setText("");
                    sendOtp();
                } else {
                    Toast.makeText(OTPVerify.this, "Check InterNet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void validateOtp() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, BASE_URL + validateOtp, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Otp otp = Otp.createobjectFromJson(response.toString());
                    if (jsonObject.getBoolean("status")) {
                        // JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                        Toast.makeText(OTPVerify.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                       /* startActivity(new Intent(OTPVerify.this, Dashboard.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));*/
                        // JSONObject jsonObject1=jsonObject.getJSONObject("data");
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            Constants.savePreferences(OTPVerify.this, LOGINKEY, jsonObject1.getString("id"));
                            Constants.savePreferences(OTPVerify.this, API_TOKEN, jsonObject1.getString("api_token"));
                            Constants.savePreferences(OTPVerify.this, USER_STATUS, jsonObject1.getString("status"));
                        }
                        if (otp != null) {
                            SharedPrefereceUserData sharedPrefereceUserData = new SharedPrefereceUserData(OTPVerify.this);
                            SharedPreferences.Editor editor = sharedPrefereceUserData.getEditorData();
                            editor.putString("User", User.createjsonFromObject(otp.getData().get(0)));
                            editor.commit();
                          /*  if (otp.getData().get(0).getUsertype() != null && otp.getData().get(0).getUsertype().equalsIgnoreCase("owner")) {
                                startActivity(new Intent(OTPVerify.this, Owner_Dashboard.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            } else {*/
                            startActivity(new Intent(OTPVerify.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            //  }
                        }
                    } else {
                        Toast.makeText(OTPVerify.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Toast.makeText(OTPVerify.this, response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(OTPVerify.this, "Please Check your internet connection...", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("mobile_no", mobile);
                params.put("device_id", android_id);
                params.put("fcm_token", Constants.getSavedPreferences(OTPVerify.this, FCM_KEY, ""));
                params.put("otp_text", edt_otp.getText().toString().trim());
                return params;
                // return super.getParams();
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(OTPVerify.this);
        requestQueue.add(stringRequest);

    }

    public void sendOtp() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, BASE_URL + sendOtp, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                        //  startActivity(new Intent(OTPVerify.this, OTPVerify.class).putExtra("mobile_no", jsonObject1.getString("mobile")));
                        Toast.makeText(OTPVerify.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(OTPVerify.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
//jsonObject.getString("error_code");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //  Toast.makeText(LoginActivity.this, response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(OTPVerify.this, "Please Check your internet connection...", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("mobile_no", mobile);
                params.put("usertype", "owner");
                return params;
                // return super.getParams();
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(OTPVerify.this);
        requestQueue.add(stringRequest);

    }

    public void forBanner() {
        final ProgressDialog progressDialog = new ProgressDialog(OTPVerify.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, BASE_URL + "banner", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        String data = jsonObject.getString("data");
                        if (data != null) {
                            final List<Banner> bannerList = Banner.createListFromJson(data);
                            if (bannerList != null && bannerList.size() > 0) {
                                final BannerAdapter viewPagerAdapter = new BannerAdapter(OTPVerify.this, bannerList);
                                viewPager.setAdapter(viewPagerAdapter);
                                viewPager.setAdapter(viewPagerAdapter);
                                dotscount = viewPagerAdapter.getCount();
                                dots = new ImageView[dotscount];

                                for (int i = 0; i < dotscount; i++) {
                                    try {
                                        dots[i] = new ImageView(OTPVerify.this);
                                        dots[i].setImageDrawable(getResources().getDrawable(R.drawable.non_active_dot));

                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                        params.setMargins(8, 0, 8, 0);
                                        sliderDotspanel.addView(dots[i], params);
                                    } catch (Exception e) {

                                    }
                                }
                                try {
                                    dots[0].setImageDrawable(getResources().getDrawable(R.drawable.active_dot));
                                } catch (Exception e) {

                                }

                                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                    @Override
                                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                                    }

                                    @Override
                                    public void onPageSelected(int position) {
                                        try {
                                            for (int i = 0; i < dotscount; i++) {
                                                dots[i].setImageDrawable(getResources().getDrawable(R.drawable.non_active_dot));
                                            }
                                            dots[position].setImageDrawable(getResources().getDrawable(R.drawable.active_dot));
                                        } catch (Exception e) {

                                        }
                                    }

                                    @Override
                                    public void onPageScrollStateChanged(int state) {

                                    }
                                });
                                if (bannerList.size() > 1) {
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            handler.postDelayed(this, 5000);
                                            if (viewPager.getCurrentItem() == bannerList.size() - 1) {
                                                viewPager.setCurrentItem(0);
                                            } else {
                                                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                                            }
                                        }
                                    }, 5000);
                                }
                            }
                        }
                    } else {
                        Toast.makeText(OTPVerify.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
//jsonObject.getString("error_code");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //  Toast.makeText(LoginActivity.this, response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(OTPVerify.this, "Please Check your internet connection...", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(OTPVerify.this);
        requestQueue.add(stringRequest);

    }

}
