package org.whagtayil.dice.ui.main;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import org.whagtayil.dice.BuildConfig;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class MainViewModel extends ViewModel /*implements Parcelable*/ {

    // .DATE (5) .HOUR (10) .MINUTE (12) .SECOND (13)
    private static int chastityTimeUnit = Calendar.SECOND;
    private static int chastityTimeDuration = 10;

    private static int NUM_DICE = 2;
    private static int[] dice = {5, 1};
    private static Calendar nextRollDate = Calendar.getInstance();
    private static Calendar startDate = Calendar.getInstance();
    public enum GameState { START, WAITING, ROLLING, ROLLED, FINISH }
    private static GameState currentState = GameState.WAITING;

    private static final Random rnd = new Random();

    public MainViewModel() { }

/*    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        for (int i=0; i < boxes.length; ++i)
            out.writeInt(boxes[i]);
        out.writeLong(nextBoxDate.getTimeInMillis());
        out.writeLong(startDate.getTimeInMillis());
        out.writeInt(numBoxesOpen);
        out.writeInt(currentState.ordinal());
    }

    public static final Parcelable.Creator<MainViewModel> CREATOR
            = new Parcelable.Creator<MainViewModel>() {
        public MainViewModel createFromParcel(Parcel in) {
            return new MainViewModel(in);
        }

        public MainViewModel[] newArray(int size) {
            return new MainViewModel[size];
        }
    };

    private MainViewModel(Parcel in) {
        for (int i=0; i < boxes.length; ++i)
            boxes[i] = in.readInt();
        long l = in.readLong();
        nextBoxDate.setTimeInMillis(l);
        l = in.readLong();
        startDate.setTimeInMillis(l);
        numBoxesOpen = in.readInt();
        int i = in.readInt();
        currentState = GameState.values()[i];
    }
*/

    public void readFromFile(ObjectInputStream ois) throws IOException {
        dice = new int[NUM_DICE];
        for (int i=0; i < dice.length; ++i)
            dice[i] = ois.readInt();
        long l = ois.readLong();
        nextRollDate.setTimeInMillis(l);
        l = ois.readLong();
        startDate.setTimeInMillis(l);
        chastityTimeDuration = ois.readInt();
        chastityTimeUnit = ois.readInt();
        int i = ois.readInt();
        currentState = GameState.values()[i];
    }

    public void writeToFile(ObjectOutputStream oos) throws IOException {
        for (int i=0; i < dice.length; ++i)
            oos.writeInt(dice[i]);
        oos.writeLong(nextRollDate.getTimeInMillis());
        oos.writeLong(startDate.getTimeInMillis());
        oos.writeInt(chastityTimeDuration);
        oos.writeInt(chastityTimeUnit);
        oos.writeInt(currentState.ordinal());
    }


    private void setNextRollDate(int numUnits) {
        nextRollDate = Calendar.getInstance();
        nextRollDate.add(chastityTimeUnit, chastityTimeDuration * numUnits);
    }
    public void setNextRollDate() { setNextRollDate(1); }

/*
    public void setBoxes(int totalBoxes, int[] numberOfBoxes) {
        boxes = new int[totalBoxes];
        for (int i=0; i < totalBoxes; ++i) {
            boxes[i] = 0;
        }

        numBoxesOpen = 0;
        while (numBoxesOpen < totalBoxes) {
            for (int i = 0; i < 8; ++i) {
                if (numberOfBoxes[i] > 0) {
                    boxes[numBoxesOpen++] = i + 1;
                    numberOfBoxes[i] -= 1;
                }
            }
            if (numberOfBoxes[8] > 0) {
                boxes[numBoxesOpen++] = 0;
                numberOfBoxes[8] -= 1;
            }
        }
    }


    public void startGame() {
        currentState = GameState.PLAY;

        // Shuffle boxes
        for (int i = boxes.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            int a = boxes[index];
            boxes[index] = boxes[i];
            boxes[i] = a;
        }
        numBoxesOpen = 0;

        startDate = Calendar.getInstance();
        setNextBoxDate();

        log("BOXES:MainViewModel.startGame()");
    }

    public int openBox() {
        int contents = boxes[numBoxesOpen++];

        if (contents == 0)
            currentState = GameState.FINISH;
        else if (contents == 8)
            currentState = GameState.INFINITY;
        else {
            currentState = GameState.PLAY;
            setNextBoxDate(contents);
        }

        return contents;
    }
*/


    public GameState getState() {
        return currentState;
    }

    public String getStartTime() {
        return getTimeString(startDate);
    }
    public String getStartDate() {
        return getDateString(startDate);
    }

    public long getTimeToNextRoll() {
        Calendar now = Calendar.getInstance();
        return nextRollDate.getTimeInMillis() - now.getTimeInMillis();
    }

    public int getChastityTimeDuration() { return chastityTimeDuration; }
    public int getChastityTimeUnit() { return chastityTimeUnit; }
    public void setChastityTime(int unit, int duration) {
        chastityTimeUnit = unit;
        chastityTimeDuration = duration;
    }

/*
    public int getNumBoxes() {
        return boxes.length;
    }
    public int getNumBoxesOpen() {
        return numBoxesOpen;
    }

    public int peekBox(int i) {
        return boxes[i];
    }
    public int peekNextBox() {
        return peekBox(numBoxesOpen);
    }
*/


    public void log(String LOGTAG) {
        if (BuildConfig.DEBUG) {
            StringBuilder s = new StringBuilder("dice:- " + dice[0] + ", " + dice[1]);
            Log.v(LOGTAG, s.toString());
            Log.v(LOGTAG, "Start - " + getStartTime() + " " + getStartDate());
            Log.v(LOGTAG, " Next - " + getTimeString(nextRollDate) + " " + getDateString(nextRollDate));
            Log.v(LOGTAG, "delta - " + chastityTimeDuration + " [5/10/12/13]=" + chastityTimeUnit);
            Log.v(LOGTAG, "State - " + currentState.name());
        }
    }


    private String getTimeString(Calendar calendar) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.US);
        return formatter.format(calendar.getTime());
    }

    private String getDateString(Calendar calendar) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy", Locale.US);
        return formatter.format(calendar.getTime());
    }
}