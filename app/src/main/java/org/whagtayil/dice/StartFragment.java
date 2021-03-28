package org.whagtayil.dice;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.whagtayil.dice.ui.main.MainViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StartFragment extends Fragment {

    private static final String LOGTAG = "DICE:StartFragment";

    private MainViewModel mViewModel;

    public static StartFragment newInstance() {
        StartFragment fragment = new StartFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.start_fragment, container, false);
    }



    private View mButton;
    private boolean mChecked = false;

    public final View.OnClickListener onClickCheckBox = new View.OnClickListener() {
        public void onClick(View v) {
            onCheckBox(v);
        }
    };
    public void onCheckBox(View v) {
        mChecked = !mChecked;
        mButton.setEnabled(mChecked);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        Activity activity = getActivity();
        mButton = activity.findViewById(R.id.buttonStartGo);
        mButton.setEnabled(false);
        mButton.setOnClickListener(onClickButton);

        View checkBox = activity.findViewById(R.id.checkBoxStartReady);
        checkBox.setOnClickListener(onClickCheckBox);
    }

    public final View.OnClickListener onClickButton = new View.OnClickListener() {
        public void onClick(View v) {
            onButtonGo(v);
        }
    };

    public void onButtonGo(View v) {
        Log.d(LOGTAG, "onButtonGo()");

        mViewModel.startGame();

        MainActivity activity = (MainActivity) getActivity();
        activity.onButtonStartGo(mButton);
    }
}