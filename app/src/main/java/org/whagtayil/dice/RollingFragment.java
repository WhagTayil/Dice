package org.whagtayil.dice;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.whagtayil.dice.ui.main.MainViewModel;

import java.util.Random;

import static java.lang.Integer.max;

public class RollingFragment extends Fragment {
    private static final String LOGTAG = "DICE:RollingFragment";

    private MainViewModel mViewModel;

    ImageView imageViewDice01 = null;
    ImageView imageViewDice02 = null;
    TextView textViewResult = null;
    Button buttonContinue = null;

    private static final int animationNumberofSteps = 25;
    private final ValueAnimator animation = ValueAnimator.ofInt(0, animationNumberofSteps);
    private static final long animationDuration = 10000;
    private static final float animationAlpha = 0.3f;
    private static final float animationDecelerationFactor = 1.2f;

    private final int[] mImageIDs = {
            R.drawable.ic_looks_one_black_48dp_2tone,
            R.drawable.ic_looks_two_black_48dp_2tone,
            R.drawable.ic_looks_3_black_48dp_2tone,
            R.drawable.ic_looks_4_black_48dp_2tone,
            R.drawable.ic_looks_5_black_48dp_2tone,
            R.drawable.ic_looks_6_black_48dp_2tone
    };
    private static final Random rnd = new Random();
    int diceRoll01 = 0, diceRoll02 = 5;

    private static int animationStepCounter;



    public static RollingFragment newInstance() {
        RollingFragment fragment = new RollingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(LOGTAG, "onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOGTAG, "onCreateView()");

        return inflater.inflate(R.layout.rolling_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(LOGTAG, "onViewCreated()");

        mViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        FragmentActivity activity = getActivity();
        imageViewDice01 = activity.findViewById(R.id.imageViewRollingDice01);
        imageViewDice02 = activity.findViewById(R.id.imageViewRollingDice02);
        textViewResult = activity.findViewById(R.id.textViewRollingResult);
        buttonContinue = activity.findViewById(R.id.buttonRollingContinue);

        diceRoll01 = mViewModel.getDice(0);
        diceRoll02 = mViewModel.getDice(1);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOGTAG, "onStart()");

        imageViewDice01.setImageResource(mImageIDs[diceRoll01]);
        imageViewDice02.setImageResource(mImageIDs[diceRoll02]);

        textViewResult.setText(null);
        buttonContinue.setEnabled(false);
        buttonContinue.setOnClickListener(onClickButtonContinue);

        MainActivity activity = (MainActivity) getActivity();
        switch (mViewModel.getState()) {
            case ROLLING:
                startDiceAnimation();
                break;
            case ROLLED:
                setUIPostRoll();
                break;
            default:
                // error
        }
    }

    public final View.OnClickListener onClickButtonContinue = new View.OnClickListener() {
        public void onClick(View v) {
            onButtonContinue(v);
        }
    };

    public void onButtonContinue(View v) {
        Log.d(LOGTAG, "onButtonContinue()");

        mViewModel.setState(MainViewModel.GameState.WAITING);

        if ((diceRoll01 == 0) && (diceRoll02 == 0))
            mViewModel.setNextRollDate(7);
        else if ((diceRoll01 == 0) || (diceRoll02 == 0))
            mViewModel.setNextRollDate(Integer.max(diceRoll01, diceRoll02) + 1);
        else if (diceRoll01 != diceRoll02)
            mViewModel.setNextRollDate();
        else
            mViewModel.setState(MainViewModel.GameState.FINISH);

        MainActivity activity = (MainActivity) getActivity();
        activity.onButtonRollingContinue(buttonContinue);
    }

    private void setUIPostRoll() {
        if ((diceRoll01 == 0) && (diceRoll02 == 0))
            textViewResult.setText(R.string.text_rolling_result_double_one);
        else if ((diceRoll01 == 0) || (diceRoll02 == 0))
            textViewResult.setText(R.string.text_rolling_result_one);
        else if (diceRoll01 != diceRoll02)
            textViewResult.setText(R.string.text_rolling_result_fail);
        else
            textViewResult.setText(R.string.text_rolling_result_double);

        buttonContinue.setEnabled(true);
    }

    private void startDiceAnimation() {
        animationStepCounter = 0;
        imageViewDice01.setAlpha(animationAlpha);
        imageViewDice02.setAlpha(animationAlpha);
        animation.setDuration(animationDuration);

        animation.setInterpolator(new DecelerateInterpolator(animationDecelerationFactor));
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                int animatedValue = (int)updatedAnimation.getAnimatedValue();
                if ((animationStepCounter != animatedValue) && (animatedValue != animationNumberofSteps)) {
                    diceRoll01 = rnd.nextInt(6);
                    imageViewDice01.setImageResource(mImageIDs[diceRoll01]);
                    diceRoll02 = rnd.nextInt(6);
                    imageViewDice02.setImageResource(mImageIDs[diceRoll02]);

                    animationStepCounter = animatedValue;
                }
            }
        });
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                imageViewDice01.setAlpha(1.0f);
                imageViewDice02.setAlpha(1.0f);

                super.onAnimationEnd(animation);
                Log.d(LOGTAG, " anim end");

                mViewModel.setState(MainViewModel.GameState.ROLLED);
                mViewModel.setDice(diceRoll01, diceRoll02);

                setUIPostRoll();
            }
        });
        animation.start();
        Log.d(LOGTAG, "anim start");
    }
}