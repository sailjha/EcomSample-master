package com.allandroidprojects.sdhecom;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MyProfileActivity extends Fragment {
    private EditText name_et,email_et,mobile_et,add_et;
    private Button btn_verifyotp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.activity_my_profile, null);
        // TextView but = (TextView) root.findViewById(R.id.text);
        name_et=(EditText)root.findViewById(R.id.name_et);
        email_et=(EditText)root.findViewById(R.id.email_et);
        mobile_et=(EditText)root.findViewById(R.id.mobile_et);
        add_et=(EditText)root.findViewById(R.id.add_et);
        btn_verifyotp=(Button)root.findViewById(R.id.btn_verifyotp);

        btn_verifyotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name_et.getText().toString().equalsIgnoreCase("")){
                    name_et.setError("Fileds can't be blank!");
                    name_et.requestFocus();
                }
                else if (email_et.getText().toString().equalsIgnoreCase("")){
                    email_et.setError("Fields can't be blank!");
                    email_et.requestFocus();
                }
                else if (mobile_et.getText().toString().equalsIgnoreCase("")){
                    mobile_et.setError("Fields can't be blank!");
                    mobile_et.requestFocus();
                }
                else if (add_et.getText().toString().equalsIgnoreCase("")){
                    add_et.requestFocus();
                }
                else {
                    Toast.makeText(getContext(),"Save Successfully",Toast.LENGTH_LONG).show();
                }
            }
        });
        return root;
    }

    }

