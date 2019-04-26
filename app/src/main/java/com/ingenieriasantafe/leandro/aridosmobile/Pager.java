package com.ingenieriasantafe.leandro.aridosmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Pager extends AppCompatActivity {

    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;

    private TextView[] mDots;
    private SliderAdapter sliderAdapter;

    private Button salir;
    private Button siguiente;

    private int mCurrentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pager);
        mSlideViewPager = (ViewPager) findViewById(R.id.slideView);
        mDotLayout = (LinearLayout) findViewById(R.id.DotLayout);

        salir = (Button)findViewById(R.id.preview);
        siguiente = (Button)findViewById(R.id.next);

        sliderAdapter = new SliderAdapter(this);
        mSlideViewPager.setAdapter(sliderAdapter);

        addDotsIndicator(0);
        mSlideViewPager.addOnPageChangeListener(viewListener);

        siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mSlideViewPager.setCurrentItem(mCurrentPage +1);
            }
        });

        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Pager.this, Menu.class);
                startActivity(i);
            }
        });
    }

    public void addDotsIndicator(int position){
        mDots = new TextView[8];
        mDotLayout.removeAllViews();

        for(int i = 0; i<mDots.length; i++){

            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorAccent));
            mDotLayout.addView(mDots[i]);
        }

        if(mDots.length > 0){
            mDots[position].setTextColor(getResources().getColor(R.color.white));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            addDotsIndicator(i);
            mCurrentPage = i;

            if(i == 0){
                siguiente.setEnabled(true);
                salir.setEnabled(false);

                siguiente.setText("Siguiente");
                salir.setText("Salir");

            }else if(i == mDots.length - 1){

                siguiente.setEnabled(true);
                salir.setEnabled(true);

                siguiente.setText("Finalizar");
                siguiente.setVisibility(View.INVISIBLE);
                salir.setText("Finalizar");
            }else {
                siguiente.setEnabled(true);
                salir.setEnabled(true);

                siguiente.setText("Siguiente");
                salir.setText("Salir");
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };
}
