package com.leobkdn.onthego;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private IntroAdapter introAdapter;
    private LinearLayout introIndicators;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupIntroItems();
        ViewPager2 introViewPager = findViewById(R.id.introViewPager);
        introViewPager.setAdapter(introAdapter);

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
        ImageView[] indicators = new ImageView[introAdapter.getItemCount()];
        //dunno
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 0, 8, 0);
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
    }
}