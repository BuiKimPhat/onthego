package com.leobkdn.onthego;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.button.MaterialButton;
import com.leobkdn.onthego.ui.intro.IntroAdapter;
import com.leobkdn.onthego.ui.intro.IntroItem;
import com.leobkdn.onthego.ui.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private IntroAdapter introAdapter;
    private LinearLayout introIndicators;
    private MaterialButton buttonIntroAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // check if intro opened before
        if (restorePrefsData()) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        setContentView(R.layout.activity_main);

        setupIntroItems();
        final ViewPager2 introViewPager = findViewById(R.id.introViewPager);
        introViewPager.setAdapter(introAdapter);

        buttonIntroAction = findViewById(R.id.buttonIntroAction);
        introIndicators = findViewById(R.id.introIndicators);
        setupIntroIndicators();
        setCurrentIntroIndicator(0);

        introViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIntroIndicator(position);
            }
        });

        buttonIntroAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (introViewPager.getCurrentItem() + 1 < introAdapter.getItemCount()) {
                    introViewPager.setCurrentItem(introViewPager.getCurrentItem() + 1);
                } else {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    savePrefsData();
                    finish();
                }
            }
        });
    }

    private void savePrefsData() {
        // save if intro opened
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("userPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isIntroOpened", true);
        editor.commit();
    }

    private boolean restorePrefsData() {
        // check if intro opened
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("userPrefs", MODE_PRIVATE);
        Boolean introOpened = prefs.getBoolean("isIntroOpened", false);
        return introOpened;
    }

    private void setupIntroItems() {
        List<IntroItem> introItemList = new ArrayList<>();

        IntroItem itemIntroduction = new IntroItem();
        itemIntroduction.setTitle("Chào mừng đến với On The Go");
        itemIntroduction.setDescription("Ứng dụng gợi ý dành cho những người thích đi du lịch");
        itemIntroduction.setImage(R.drawable.onthego_logo);

        IntroItem itemDestination = new IntroItem();
        itemDestination.setTitle("Gợi ý điểm đến, lập lộ trình");
        itemDestination.setDescription("Không cần phải băn khoăn nên đi đâu, tự động gợi ý chuyến đi tốt nhất");
        itemDestination.setImage(R.drawable.destinations);

        IntroItem itemFlight = new IntroItem();
        itemFlight.setTitle("Tự động tìm chuyến bay phù hợp");
        itemFlight.setDescription("Giúp bạn chọn những chuyến bay tốt nhất");
        itemFlight.setImage(R.drawable.flight);

        IntroItem itemMoney = new IntroItem();
        itemMoney.setTitle("Chi tiêu hợp lí");
        itemMoney.setDescription("Tự lập lộ trình, hoạt động du lịch theo số tiền mà bạn có");
        itemMoney.setImage(R.drawable.money);

        introItemList.add(itemIntroduction);
        introItemList.add(itemDestination);
        introItemList.add(itemFlight);
        introItemList.add(itemMoney);

        introAdapter = new IntroAdapter(introItemList);
    }

    private void setupIntroIndicators() {
        //array of indicator images (Views)
        ImageView[] indicators = new ImageView[introAdapter.getItemCount()];
        // set attributes for Views in LinearLayout ViewGroup
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 0, 8, 0);

        //
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.intro_indicators_inactive
            ));
            indicators[i].setLayoutParams(layoutParams);
            introIndicators.addView(indicators[i]);
        }
    }

    private void setCurrentIntroIndicator(int index) {
        int childCount = introIndicators.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) introIndicators.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.intro_indicator_active)
                );
            } else {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.intro_indicators_inactive)
                );
            }
        }
        // set button text
        if (index == introAdapter.getItemCount() - 1) {
            buttonIntroAction.setText("Bắt đầu");
        } else {
            buttonIntroAction.setText("Tiếp tục");
        }
    }
}