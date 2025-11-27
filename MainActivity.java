package com.example.myapplication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int TOTAL_PAIRS = 8;
    private List<Integer> cardValues;
    private List<ImageView> cardViews;
    private int[] cardImages = {
            R.drawable.img1,
            R.drawable.img2,
            R.drawable.img3,
            R.drawable.img4,
            R.drawable.img5,
            R.drawable.img6,
            R.drawable.img7,
            R.drawable.img8
    };

    private int firstCardIndex = -1;
    private int secondCardIndex = -1;
    private boolean isProcessing = false;
    private int matchedPairs = 0;

    private GridLayout gridLayout;
    private Button restartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridLayout = findViewById(R.id.gridLayout);
        restartButton = findViewById(R.id.restartBtn);
        restartButton.setOnClickListener(v -> initGame());

        initGame();
    }

    private void initGame() {
        gridLayout.removeAllViews();
        cardViews = new ArrayList<>();
        cardValues = new ArrayList<>();
        firstCardIndex = -1;
        secondCardIndex = -1;
        matchedPairs = 0;
        isProcessing = false;

        for (int i = 0; i < TOTAL_PAIRS; i++) {
            cardValues.add(i);
            cardValues.add(i);
        }
        Collections.shuffle(cardValues);

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int cardWidth = screenWidth / 4;
        int cardHeight = (int) (cardWidth * 1.5f); // прямоугольник 3:2

        for (int i = 0; i < TOTAL_PAIRS * 2; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(R.drawable.back);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = cardWidth;
            params.height = cardHeight;
            params.setMargins(2, 2, 2, 2);
            imageView.setLayoutParams(params);

            imageView.setTag(i);
            imageView.setOnClickListener(this::onCardClick);
            gridLayout.addView(imageView);
            cardViews.add(imageView);
        }
    }

    private void onCardClick(View view) {
        if (isProcessing) return;

        int index = (int) view.getTag();
        if (firstCardIndex == index || secondCardIndex == index) return;

        ImageView clickedCard = (ImageView) view;
        int cardId = cardValues.get(index);
        clickedCard.setImageResource(cardImages[cardId]);

        if (firstCardIndex == -1) {
            firstCardIndex = index;
        } else if (secondCardIndex == -1) {
            secondCardIndex = index;
            isProcessing = true;

            if (cardValues.get(firstCardIndex).equals(cardValues.get(secondCardIndex))) {
                // ✅ Совпадение: пауза → исчезновение
                new Handler().postDelayed(() -> {
                    cardViews.get(firstCardIndex).setVisibility(View.INVISIBLE);
                    cardViews.get(secondCardIndex).setVisibility(View.INVISIBLE);

                    matchedPairs++;
                    firstCardIndex = -1;
                    secondCardIndex = -1;
                    isProcessing = false;

                    if (matchedPairs == TOTAL_PAIRS) {
                        showWinDialog();
                    }
                }, 1000);
            } else {
                new Handler().postDelayed(() -> {
                    cardViews.get(firstCardIndex).setImageResource(R.drawable.back);
                    cardViews.get(secondCardIndex).setImageResource(R.drawable.back);
                    firstCardIndex = -1;
                    secondCardIndex = -1;
                    isProcessing = false;
                }, 1000);
            }
        }
    }

    private void showWinDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Победа!")
                .setMessage("Вы нашли все пары!")
                .setPositiveButton("Новая игра", (dialog, which) -> initGame())
                .setCancelable(false)
                .show();
    }
}