package org.whagtayil.dice.ui.main;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.whagtayil.dice.BuildConfig;
import org.whagtayil.dice.MainActivity;
import org.whagtayil.dice.R;
import org.whagtayil.dice.StartFragment;

import java.util.Locale;

public class MainFragment extends Fragment {
    private static final String LOGTAG = "DICE:MainFragment";

    private MainViewModel mViewModel;

    private Button buttonMainRoll = null;
    private TextView textViewMainStartTime = null;
    private TextView textViewMainRoll = null;




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
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentActivity activity = getActivity();
        buttonMainRoll = activity.findViewById(R.id.buttonMainRoll);
        textViewMainStartTime = activity.findViewById(R.id.textViewMainStartTime);
    }

    private void setStartDate() {
        String s = mViewModel.getStartTime() + getString(R.string.text_main_start_on) + mViewModel.getStartDate();
        textViewMainStartTime.setText(s);
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
                mViewModel.setNextRollDate();
                buttonMainRoll.setEnabled(false);
                //buttonMainRoll.setOnClickListener(onClickButtonRoll);
                setStartDate();

                handler.postDelayed(run, 0);
                if (BuildConfig.DEBUG)
                    Log.v(LOGTAG, " launch bg runnable from onStart()");
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


}
