package org.whagtayil.dice.ui.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.whagtayil.dice.BuildConfig;
import org.whagtayil.dice.MainActivity;
import org.whagtayil.dice.R;

import java.util.Locale;

public class MainFragment extends Fragment {

    private static final String LOGTAG = "DICE:MainFragment";

    private MainViewModel mViewModel;

    private Button buttonMainRoll = null;
    private TextView textViewMainEnjoy = null;
    private TextView textViewMainStartTime = null;
    private TextView textViewMainRoll = null;
    private TextView textViewMainEnded = null;
    private TextView textViewMainEndTime = null;

    private ImageView imageViewDice01 = null;
    private ImageView imageViewDice02 = null;
    private final int[] mImageIDs = {
            R.drawable.ic_looks_one_black_48dp_2tone,
            R.drawable.ic_looks_two_black_48dp_2tone,
            R.drawable.ic_looks_3_black_48dp_2tone,
            R.drawable.ic_looks_4_black_48dp_2tone,
            R.drawable.ic_looks_5_black_48dp_2tone,
            R.drawable.ic_looks_6_black_48dp_2tone
    };



    ///////////////////////////////////////////////////////////////
    // Background "thread" for countdown to Open
    final Handler handler = new Handler();
    final Runnable run = new Runnable() {
        @Override
        public void run() {
            MainViewModel.GameState currentState = mViewModel.getState();
            if (currentState == MainViewModel.GameState.WAITING) {
                long deltaMillis = mViewModel.getTimeToNextRoll();

                final long secondsInMilli = 1000;
                final long minutesInMilli = secondsInMilli * 60;
                final long hoursInMilli = minutesInMilli * 60;

                String s = getString(R.string.button_main_roll_now);
                if (deltaMillis > secondsInMilli) {
                    long hours = deltaMillis / hoursInMilli;
                    deltaMillis = deltaMillis % hoursInMilli;
                    long minutes = deltaMillis / minutesInMilli;
                    deltaMillis = deltaMillis % minutesInMilli;
                    long seconds = deltaMillis / secondsInMilli;
                    s = String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);

                    handler.postDelayed(this, 500);
                } else {
                    textViewMainRoll.setText(R.string.text_main_roll_now);
                    buttonMainRoll.setEnabled(true);

                    if (BuildConfig.DEBUG)
                        Log.v(LOGTAG, " no more bg runnable");
                }
                buttonMainRoll.setText(s);
            }
        }
    };
    // ^ Background "thread"
    ///////////////////////////////////////////////////////////////



    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(LOGTAG, "onCreateView()");

        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(LOGTAG, "onActivityCreated()");

        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mViewModel.log(LOGTAG);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(LOGTAG, "onViewCreated()");

        FragmentActivity activity = getActivity();
        buttonMainRoll = activity.findViewById(R.id.buttonMainRoll);
        buttonMainRoll.setOnClickListener(onClickButtonRoll);

        textViewMainEnjoy = activity.findViewById(R.id.textViewMainEnjoy);
        textViewMainStartTime = activity.findViewById(R.id.textViewMainStartTime);
        textViewMainRoll = activity.findViewById(R.id.textViewMainRoll);
        imageViewDice01 = activity.findViewById(R.id.imageViewMainDice01);
        imageViewDice02 = activity.findViewById(R.id.imageViewMainDice02);
        textViewMainEnded = activity.findViewById(R.id.textViewMainEnded);
        textViewMainEndTime = activity.findViewById(R.id.textViewMainEndTime);
    }

    private void setEndDate() {
        String s = mViewModel.getEndTime() + getString(R.string.text_main_start_on) + mViewModel.getEndDate();
        textViewMainEndTime.setText(s);
    }
    private void setStartDate() {
        String s = mViewModel.getStartTime() + getString(R.string.text_main_start_on) + mViewModel.getStartDate();
        textViewMainStartTime.setText(s);
    }
    private void setDice() {
        int n = mViewModel.getDice(0);
        imageViewDice01.setImageResource(mImageIDs[n]);
        n = mViewModel.getDice(1);
        imageViewDice02.setImageResource(mImageIDs[n]);
    }
    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOGTAG, "onStart()");

        MainActivity activity = (MainActivity) getActivity();
        switch (mViewModel.getState()) {
            case START:
                activity.showStartScreen();
                break;
            case WAITING:
                textViewMainEnjoy.setText(R.string.text_main_enjoy);
                textViewMainRoll.setText(R.string.text_main_roll_in);
                buttonMainRoll.setEnabled(false);
                setStartDate();
                setDice();
                textViewMainEnded.setVisibility(View.INVISIBLE);
                textViewMainEndTime.setVisibility(View.INVISIBLE);

                handler.postDelayed(run, 0);
                if (BuildConfig.DEBUG)
                    Log.v(LOGTAG, " launch bg runnable from onStart()");
                break;
            case ROLLED:
                activity.onButtonMainRoll(buttonMainRoll);
                break;
            case FINISH:
                textViewMainEnjoy.setText(R.string.text_main_enjoyed);
                textViewMainRoll.setText(R.string.text_main_roll_click);
                buttonMainRoll.setEnabled(true);
                setStartDate();
                setDice();
                textViewMainEnded.setVisibility(View.VISIBLE);
                setEndDate();
                textViewMainEndTime.setVisibility(View.VISIBLE);

                buttonMainRoll.setText(R.string.button_main_roll_ok);
                break;
            default:
                // design error
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(LOGTAG, "onStop()");

        handler.removeCallbacks(run);
        if (BuildConfig.DEBUG)
            Log.v(LOGTAG, " remove runnable callbacks in onStop()");
    }

    public final View.OnClickListener onClickButtonRoll = new View.OnClickListener() {
        public void onClick(View v) {
            onButtonRoll(v);
        }
    };

    public void onButtonRoll(View v) {
        Log.d(LOGTAG, "onButtonRoll()");

        MainActivity activity = (MainActivity) getActivity();
        if (mViewModel.getState() == MainViewModel.GameState.WAITING) {
            activity.onButtonMainRoll(buttonMainRoll);
        }
        else if (mViewModel.getState() == MainViewModel.GameState.FINISH) {
            activity.showStartScreen();
        }
    }
}
