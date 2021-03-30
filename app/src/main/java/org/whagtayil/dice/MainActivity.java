package org.whagtayil.dice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.whagtayil.dice.ui.main.MainFragment;
import org.whagtayil.dice.ui.main.MainViewModel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MainActivity extends AppCompatActivity {

    private static final String LOGTAG = "DICE:MainActivity";

    private MainViewModel mViewModel;

    ///////////////////////////////////////////////////////////////
    // Persistence serialization game state data
    private final String saveDataFileName = "svdt";

    public void readSaveData() {
        Log.d(LOGTAG, "readSaveData()");

        Context context = getApplicationContext();
        try {
            FileInputStream fis = context.openFileInput(saveDataFileName);
            ObjectInputStream ois = new ObjectInputStream(fis);

            mViewModel.readFromFile(ois);

            fis.close();
            ois.close();
        } catch (FileNotFoundException e) {
            Log.d(LOGTAG, "readSaveData() FileNotFound", e);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (BuildConfig.DEBUG)
            mViewModel.log(LOGTAG);
    }

    public void writeSaveData() {
        Log.d(LOGTAG, "writeSaveData()");

        Context context = getApplicationContext();
        try {
            FileOutputStream fos = context.openFileOutput(saveDataFileName, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            mViewModel.writeToFile(oos);

            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (BuildConfig.DEBUG)
            mViewModel.log(LOGTAG);
    }
    // ^ Save data (game state)
    ///////////////////////////////////////////////////////////////
    
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOGTAG, "onCreate()");
        setContentView(R.layout.main_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow();
        }
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        readSaveData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOGTAG, "onStop()");

        writeSaveData();
    }

    public void showStartScreen() {
        Log.d(LOGTAG, "showStartScreen()");

        mViewModel.setState(MainViewModel.GameState.START);
        writeSaveData();

        StartFragment fragment = new StartFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void onButtonMainRoll(View v) {
        Log.d(LOGTAG, "onButtonMainRoll()");

        mViewModel.setState(MainViewModel.GameState.ROLLING);
        writeSaveData();

        RollingFragment fragment = new RollingFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void onButtonRollingContinue(View v) {
        Log.d(LOGTAG, "onButtonRollingContinue()");

        writeSaveData();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
    }

    public void onButtonStartGo(View v) {
        Log.d(LOGTAG, "onButtonStartGo()");

        mViewModel.startGame();
        writeSaveData();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
    }
}