package com.victorjunior.bloopy;

/**
 * Created by Victor on 10/11/2015
 */

import android.graphics.Bitmap;

public class Farynor extends Sprites
{
    static final int BLINK_COLUMNS = 1;
    static final int BLINK_ROWS = 5;
    static final int DEATH_COLUMNS = 1;
    static final int DEATH_ROWS = 6;
    static final int RUN_COLUMNS = 1;
    static final int RUN_ROWS = 8;

    static final double WIDTH_MULTIPLIER = 0.714;
    static final double BLINK_WIDTH_MULTIPLIER = 0.08;
    static final double DEATH_WIDTH_MULTIPLIER = 1.08;
    static final double BLINK_HEIGHT_MULTIPLIER = 0.0987;
    static final double DEATH_HEIGHT_MULTIPLIER = 0.7895;

    Bitmap frame;
    double deltaHeight;     // The height between the top of the rectangle and the top of the character
    int screenW, screenH;

    Farynor(int screenW, int screenH)
    {
        myName = "farynor";
        reverseAnimate = false;
        hasMouth = false;
        unlocked = false;
        myFPS = 25;
        myPeriod = 1000 / myFPS;

        mySWidth = screenW / 3;
        mySWBlink = (int)(mySWidth * BLINK_WIDTH_MULTIPLIER);
        mySWDeath = (int)(mySWidth * DEATH_WIDTH_MULTIPLIER);

        mySHeight = (int)(mySWidth * WIDTH_MULTIPLIER * RUN_ROWS);
        mySHBlink = (int)(mySHeight * BLINK_HEIGHT_MULTIPLIER);
        mySHDeath = (int)(mySHeight * DEATH_HEIGHT_MULTIPLIER);

        this.screenW = screenW;
        this.screenH = screenH;

        runFrames = new Bitmap[RUN_ROWS];
        deathFrames = new Bitmap[DEATH_ROWS];
        blinkFrames = new Bitmap[BLINK_ROWS];
        detectHeight = new double[RUN_ROWS];
        blinkXPos = new int[RUN_ROWS];
        blinkYPos = new int[RUN_ROWS];
    }

    public void setArrays(Bitmap run, Bitmap blink, Bitmap death)
    {
        int startHeight = 0, holdHeight;

        runSheet = run;
        blinkSheet = blink;
        deathSheet = death;

        holdHeight = runSheet.getHeight() / RUN_ROWS;
        for(int j = 0; j < RUN_ROWS; j++)
        {
            frame = Bitmap.createBitmap(runSheet, 0, startHeight, runSheet.getWidth(), runSheet.getHeight() / RUN_ROWS);
            if (j < RUN_ROWS)
            {
                runFrames[j] = frame;
            }
            startHeight = (holdHeight * (j + 1));

            blinkXPos[j] = (int) (myXPos + (frame.getWidth() * 0.7524259832)); //+ (mySWidth * 0.8024259832));
            blinkYPos[j] = (int) (myYPos + (frame.getHeight() * 0.3954936402)); //+ (mySHeight / 0.4354936402));
            switch(j)
            {
                case 0:
                    deltaHeight = 12.758;
                    break;
                case 1:
                    deltaHeight = 14.282;
                    break;
                case 2:
                    deltaHeight = 13.1;
                    break;
                case 3:
                    deltaHeight = 12.189;
                    break;
                case 4:
                    deltaHeight = 11.073;
                    break;
                case 5:
                    deltaHeight = 13.802;
                    break;
                case 6:
                    deltaHeight = 15.578;
                    break;
                case 7:
                    deltaHeight = 15.413;
                    break;
            }
            detectHeight[j] = (deltaHeight*1.5) / frame.getHeight();
        }

        startHeight = 0;
        holdHeight = blinkSheet.getHeight() / BLINK_ROWS;
        for(int j = 0; j < BLINK_ROWS; j++)
        {
            frame = Bitmap.createBitmap(blinkSheet, 0, startHeight, blinkSheet.getWidth(), blinkSheet.getHeight() / BLINK_ROWS);
            if (j < BLINK_ROWS)
            {
                blinkFrames[j] = frame;
            }
            startHeight = (holdHeight * (j + 1));
        }

        startHeight = 0;
        holdHeight = deathSheet.getHeight() / DEATH_ROWS;
        for(int j = 0; j < DEATH_ROWS; j++)
        {
            frame = Bitmap.createBitmap(deathSheet, 0, startHeight, deathSheet.getWidth(), (deathSheet.getHeight() / DEATH_ROWS));
            if (j < DEATH_ROWS)
            {
                deathFrames[j] = frame;
            }
            startHeight = holdHeight + (holdHeight * j);
        }

        myRDWidth = runFrames[0].getWidth();
        myRDHeight = runFrames[0].getHeight();
        myBlinkWidth = blinkFrames[0].getWidth();
        myBlinkHeight = blinkFrames[0].getHeight();
        blinkXMulti = 0.7524259832;
        blinkYMulti = 0.3954936402;
        deathNo = DEATH_ROWS;
    }
}
