package com.victorjunior.bloopy;

/**
 * Created by Victor on 30/11/2015
 */

import android.graphics.Bitmap;

public class Bloopy extends Sprites
{
    static final int BLINK_COLUMNS = 1;
    static final int BLINK_ROWS = 5;
    static final int DEATH_COLUMNS = 1;
    static final int DEATH_ROWS = 5;
    static final int MOUTH_COLUMNS = 1;
    static final int MOUTH_ROWS = 5;
    static final int RUN_COLUMNS = 1;
    static final int RUN_ROWS = 8;

    static final double WIDTH_MULTIPLIER = 0.421;       // Height / Width Use values from svg file
    static final double BLINK_WIDTH_MULTIPLIER = 0.06423;
    static final double DEATH_WIDTH_MULTIPLIER = 0.605 * 1.65;      // (Death / Original) * Ratio of frames
    static final double MOUTH_WIDTH_MULTIPLIER = 0.09142857142857142857142857142857;
    static final double BLINK_HEIGHT_MULTIPLIER = 0.165 * 0.625;
    static final double DEATH_HEIGHT_MULTIPLIER = 0.390625 * 1.6;
    static final double MOUTH_HEIGHT_MULTIPLIER = 0.1601525753;

    static final double MOUTH_WIDTH_MULTIPLIER_PNG = 0.28942857142857142857142857142857;
    static final double MOUTH_HEIGHT_MULTIPLIER_PNG = 0.53466892179284352648543685783324;

    static final double BLINK_X_MULTIPLIER = 0.85164465;     // X Pos / Width
    static final double BLINK_Y_MULTIPLIER = 0.9329897 * 0.4;
    static final double MOUTH_X_MULTIPLIER = 0.84239631336405529953917050691244;
    static final double MOUTH_Y_MULTIPLIER = 0.60468415937803692905733722060253; //0.674

    Bitmap frame;
    double deltaHeight;     // The height between the top of the rectangle and the top of the character
    double screenRatio;
    int screenW, screenH;

    Bloopy(int screenW, int screenH)
    {
        myName = "bloopy";
        hasMouth = true;
        reverseAnimate = false;
        unlocked = true;
        myFPS = 25;
        myPeriod = 1000 / myFPS;

        screenRatio = screenH / screenW;

        mySWidth = (int)(screenW / 2.5);
        mySWBlink = (int)(mySWidth * BLINK_WIDTH_MULTIPLIER);
        mySWDeath = (int)(mySWidth * DEATH_WIDTH_MULTIPLIER);
        mySWMouth = (int)(mySWidth * MOUTH_WIDTH_MULTIPLIER);

        mySHeight = (int)(mySWidth * WIDTH_MULTIPLIER * RUN_ROWS);
        mySHBlink = (int)(mySHeight * BLINK_HEIGHT_MULTIPLIER);
        mySHDeath = (int)(mySHeight * DEATH_HEIGHT_MULTIPLIER);
        mySHMouth = (int)(mySHeight * MOUTH_HEIGHT_MULTIPLIER);

        this.screenW = screenW;
        this.screenH = screenH;

        runFrames = new Bitmap[RUN_ROWS];
        deathFrames = new Bitmap[DEATH_ROWS];
        blinkFrames = new Bitmap[BLINK_ROWS];
        mouthFrames = new Bitmap[MOUTH_ROWS];
        detectHeight = new double[RUN_ROWS];
        blinkXPos = new int[RUN_ROWS];
        blinkYPos = new int[RUN_ROWS];
        mouthXPos = new int[RUN_ROWS];
        mouthYPos = new int[RUN_ROWS];
    }

    public void setArrays(Bitmap run, Bitmap blink, Bitmap death, Bitmap mouth)
    {
        int startHeight = 0, holdHeight;

        runSheet = run;
        blinkSheet = blink;
        deathSheet = death;
        mouthSheet = mouth;

        holdHeight = runSheet.getHeight() / RUN_ROWS;
        for(int j = 0; j < RUN_ROWS; j++)
        {
            frame = Bitmap.createBitmap(runSheet, 0, startHeight, runSheet.getWidth(), runSheet.getHeight() / RUN_ROWS);
            if (j < RUN_ROWS)
            {
                runFrames[j] = frame;
            }
            startHeight = (holdHeight * (j + 1));

            blinkXPos[j] = (int) (myXPos + (frame.getWidth() * BLINK_X_MULTIPLIER)); //+ (mySWidth * 0.8024259832));
            blinkYPos[j] = (int) (myYPos + (frame.getHeight() * BLINK_Y_MULTIPLIER)); //+ (mySHeight / 0.4354936402));
            mouthXPos[j] = (int) (myXPos + (frame.getWidth() * MOUTH_X_MULTIPLIER));
            mouthYPos[j] = (int) (myYPos + (frame.getHeight() * MOUTH_Y_MULTIPLIER));
            switch(j)
            {
                case 0:
                    deltaHeight = 11.758;
                    break;
                case 1:
                    deltaHeight = 13.282;
                    break;
                case 2:
                    deltaHeight = 12.1;
                    break;
                case 3:
                    deltaHeight = 11.189;
                    break;
                case 4:
                    deltaHeight = 10.073;
                    break;
                case 5:
                    deltaHeight = 12.802;
                    break;
                case 6:
                    deltaHeight = 14.578;
                    break;
                case 7:
                    deltaHeight = 14.413;
                    break;
            }
            //detectHeight[j] = (deltaHeight*1.5) / frame.getHeight();
            detectHeight[j] = 0;
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

        startHeight = 0;
        holdHeight = mouthSheet.getHeight() / MOUTH_ROWS;
        for(int j = 0; j < MOUTH_ROWS; j++)
        {
            frame = Bitmap.createBitmap(mouthSheet, 0, startHeight, mouthSheet.getWidth(), mouthSheet.getHeight() / MOUTH_ROWS);
            if (j < MOUTH_ROWS)
            {
                mouthFrames[j] = frame;
            }
            startHeight = (holdHeight * (j + 1));
        }

        myRDWidth = runFrames[0].getWidth();
        myRDHeight = runFrames[0].getHeight();
        myBlinkWidth = blinkFrames[0].getWidth();
        myBlinkHeight = blinkFrames[0].getHeight();
        blinkXMulti = BLINK_X_MULTIPLIER;
        blinkYMulti = BLINK_Y_MULTIPLIER;
        mouthXMulti = MOUTH_X_MULTIPLIER;
        mouthYMulti = MOUTH_Y_MULTIPLIER;
        deathNo = DEATH_ROWS;
    }
}
