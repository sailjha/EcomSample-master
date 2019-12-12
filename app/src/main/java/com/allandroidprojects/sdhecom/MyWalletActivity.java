package com.allandroidprojects.sdhecom;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


public class MyWalletActivity extends Fragment {
    private Button btn_verifyotp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.activity_my_wallet, null);
       // TextView but = (TextView) root.findViewById(R.id.text);
        btn_verifyotp=(Button)root.findViewById(R.id.btn_verifyotp);
        btn_verifyotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"Send  Button clicked",Toast.LENGTH_LONG).show();
            }
        });

        return root;
    }
}
