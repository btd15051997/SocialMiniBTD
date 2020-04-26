package com.example.socialminibtd.View.Activity.Language;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.example.socialminibtd.Adapter.ImageAdapter;
import com.example.socialminibtd.Adapter.SpinnerLanguageAdapter;
import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.PreferenceHelper;
import com.example.socialminibtd.View.Activity.Dashbroad.DashboardActivity;
import com.example.socialminibtd.View.Activity.Login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.Locale;

public class LanguageActivity extends AppCompatActivity implements ILanguageView, View.OnClickListener {

    private Button btn_wellcome_confirm;
    private Spinner spin_country_reg;
    private ViewPager viewpager_getstarted;
    private DotsIndicator dots_indicator_getstarted;
    private SpinnerLanguageAdapter spinnerLanguageAdapter;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        setContentView(R.layout.activity_language);

        onMappingView();

        onGetPermission();

        onGetAdapterLanguage();

    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {

            startActivity(new Intent(LanguageActivity.this, DashboardActivity.class));
            finish();

        }
    }

    @Override
    public void onMappingView() {

        btn_wellcome_confirm = findViewById(R.id.btn_wellcome_confirm);
        spin_country_reg = findViewById(R.id.spin_country_reg);
        viewpager_getstarted = findViewById(R.id.viewpager_getstarted);
        dots_indicator_getstarted = findViewById(R.id.dots_indicator_getstarted);

        ImageAdapter adapter = new ImageAdapter(this);

        viewpager_getstarted.setAdapter(adapter);
        viewpager_getstarted.setCurrentItem(0);
        viewpager_getstarted.setOffscreenPageLimit(3);
        dots_indicator_getstarted.setViewPager(viewpager_getstarted);

        btn_wellcome_confirm.setOnClickListener(this);

        if (!TextUtils.isEmpty(new PreferenceHelper(this).getLanguage())) {
            Locale myLocale = null;
            switch (new PreferenceHelper(this).getLanguage()) {
                case "":

                    myLocale = new Locale("en");
                    break;

                case "en":

                    myLocale = new Locale("en");
                    break;

                case "vi":
                    myLocale = new Locale("vi");

                    break;

            }

            Locale.setDefault(myLocale);
            Configuration config = new Configuration();
            config.locale = myLocale;
            this.getResources().updateConfiguration(config,
                    this.getResources().getDisplayMetrics());
        }

    }

    @Override
    public void onIntentLogin() {

        startActivity(new Intent(LanguageActivity.this, LoginActivity.class));

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_wellcome_confirm:

                onIntentLogin();

                break;

        }
    }

    @Override
    public void onGetPermission() {

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;

        if (currentapiVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {

            String[] permissions_dummy = new String[6];
            int i = 0;

            String permission = "android.permission.WRITE_EXTERNAL_STORAGE";
            int res = checkCallingOrSelfPermission(permission);

            if (res != PackageManager.PERMISSION_GRANTED) {


                permissions_dummy[i] = permission;
                i = i + 1;


            }

            permission = "android.permission.READ_CONTACTS";
            res = checkCallingOrSelfPermission(permission);
            if (res != PackageManager.PERMISSION_GRANTED) {


                permissions_dummy[i] = permission;
                i = i + 1;


            }
            permission = "android.permission.CAMERA";
            res = checkCallingOrSelfPermission(permission);
            if (res != PackageManager.PERMISSION_GRANTED) {


                permissions_dummy[i] = permission;
                i = i + 1;


            }
            permission = "android.permission.ACCESS_FINE_LOCATION";
            res = checkCallingOrSelfPermission(permission);
            if (res != PackageManager.PERMISSION_GRANTED) {


                permissions_dummy[i] = permission;
                i = i + 1;


            }

            permission = "android.permission.READ_PHONE_STATE";
            res = checkCallingOrSelfPermission(permission);
            if (res != PackageManager.PERMISSION_GRANTED) {


                permissions_dummy[i] = permission;
                i = i + 1;

            }

            permission = "android.permission.READ_SMS";
            res = checkCallingOrSelfPermission(permission);
            if (res != PackageManager.PERMISSION_GRANTED) {


                permissions_dummy[i] = permission;
                i = i + 1;


            }


            String[] permissions = new String[i];

            for (int j = 0; j < i; j++) {

                permissions[j] = permissions_dummy[j];

            }


            int yourRequestId = 1;
            if (i != 0) {

                // Do something for lollipop and above versions
                requestPermissions(permissions, yourRequestId);
            }

        }

    }

    @Override
    public void onsetLocale(String lang) {

        Locale myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        Configuration config = new Configuration();
        config.locale = myLocale;
        this.getResources().updateConfiguration(config,
                this.getResources().getDisplayMetrics());

        Intent refresh = new Intent(this, LanguageActivity.class);
        startActivity(refresh);
        this.overridePendingTransition(0, 0);

    }

    @Override
    public void onGetAdapterLanguage() {

        String[] stringslanguage = getResources().getStringArray(R.array.language);
        Integer[] imglanguage = {null, R.drawable.img_us, R.drawable.img_us};

        spinnerLanguageAdapter = new SpinnerLanguageAdapter(this, R.layout.layout_spinner_reg, stringslanguage, imglanguage);
        spin_country_reg.setAdapter(spinnerLanguageAdapter);

        if (!TextUtils.isEmpty(new PreferenceHelper(this).getLanguage())) {

            switch (new PreferenceHelper(this).getLanguage()) {
                case "":
                    spin_country_reg.setSelection(0, false);

                    break;
                case "en":

                    spin_country_reg.setSelection(1, false);
                    break;

                case "vi":

                    spin_country_reg.setSelection(2, false);

                    break;


            }
        }

        spin_country_reg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                switch (position) {
                    case 0:

                        new PreferenceHelper(LanguageActivity.this).putLanguage("");
                        break;

                    case 1:

                        new PreferenceHelper(LanguageActivity.this).putLanguage("en");
                        onsetLocale("en");

                        break;

                    case 2:

                        new PreferenceHelper(LanguageActivity.this).putLanguage("vi");
                        onsetLocale("vi");

                        break;
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


}

