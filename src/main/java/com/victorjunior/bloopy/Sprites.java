package com.victorjunior.bloopy;

/**
 * Created by Victor on 02/09/2015
 */

import android.graphics.Bitmap;

public class Sprites
{

    private int columnNo;
    private int rowNo;
    private Bitmap[] frames;
    private Bitmap[] runFrameSheet;
    private Bitmap blinkSpriteSheet, scaledBlinkSpriteSheet;
    private Bitmap deathSpriteSheet, scaledDeathSpriteSheet;

    int myWidth;
    int myHeight;
    int myXPos, myYPos, lastX, lastY;
    int[] blinkXPos, blinkYPos, mouthXPos, mouthYPos;
    Bitmap spriteMap, scaledSpriteMap; // refers to the character's bitmap
    Bitmap spriteSheet, scaledSpriteSheet; // refers to the running spritesheet
    String myName;

    Bitmap runSheet, deathSheet, blinkSheet, mouthSheet;
    Bitmap[] runFrames;
    Bitmap[] deathFrames;
    Bitmap[] blinkFrames;
    Bitmap[] mouthFrames;

    double[] detectHeight;
    double blinkXMulti, blinkYMulti, mouthYMulti, mouthXMulti;

    boolean playerControlled, reverseAnimate, unlocked, hasMouth;
    int myPeriod, myFPS;
    int deathNo;
    int screenW, mySWidth, mySWBlink, mySWDeath, mySWMouth, myRDWidth, myBlinkWidth;
    int screenH, mySHeight, mySHBlink, mySHDeath, mySHMouth, myRDHeight, myBlinkHeight;

    private Bitmap holderSS, holderSSS;
    private String animation = "Run";

    public Sprites(String myName, int screenW, int screenH)
    {
        this.myName = myName;
        this.screenW = screenW;
        this.screenH = screenH;
        /*switch (myName)
        {
            case "farynor":
                mySWidth = (int) (screenW / 5);
                mySHeight = (int) (mySWidth * 0.714 * 5);
                playerControlled = true;
                break;
        }*/
    }

    Sprites()
    {}

    public void setScaled()
    {
        switch(myName)
        {
            case "farynor":
                mySWidth = (int) (screenW / 2.5);
                mySHeight = (int) (mySWidth * 0.714 * 5);
                break;
        }
    }

    public void setNumbers()
    {
        switch (myName)
        {
            case "farynor":
                switch (animation)
                {
                    case "Blink":
                        columnNo = 1;
                        rowNo = 5;
                        break;
                    case "Run":
                        columnNo = 1;
                        rowNo = 8;
                        break;
                    case "Death":
                        columnNo = 1;
                        rowNo = 6;
                        break;
                }
                break;
        }
        myWidth = spriteMap.getWidth() / columnNo;
        myHeight = spriteMap.getHeight() / rowNo;
        frames = new Bitmap[rowNo];
        runFrameSheet = new Bitmap[rowNo];
        blinkXPos = new int[rowNo];
        blinkYPos = new int[rowNo];
    }

    public Bitmap animateCharacter(String animationType,int i)
    {
        animation = animationType;
        Bitmap mySpriteSheet;
        int startHeight = 0, holdHeight;
        switch(animation)
        {
            case "Run":
                spriteSheet = holderSS;
                scaledSpriteSheet = holderSSS;
                /*for(int j = 0; j < rowNo; j++)
                {
                    mySpriteSheet = Bitmap.createBitmap(spriteSheet,0, startHeight,spriteSheet.getWidth(), spriteSheet.getHeight() / rowNo);
                    runFrameSheet[j] = mySpriteSheet;
                    holdHeight = spriteSheet.getHeight() / rowNo;
                    startHeight = (spriteSheet.getHeight() / rowNo) + (holdHeight * j);
                }*/
                break;
            case "Blink":
                spriteSheet = blinkSpriteSheet;
                scaledSpriteSheet = scaledBlinkSpriteSheet;
                break;
            case "Death":
                spriteSheet = deathSpriteSheet;
                scaledSpriteSheet = scaledDeathSpriteSheet;
                break;
        }
        setNumbers();
        for(int j = 0; j < rowNo; j++)
        {
            mySpriteSheet = Bitmap.createBitmap(scaledSpriteSheet,0, startHeight,scaledSpriteSheet.getWidth(), scaledSpriteSheet.getHeight() / rowNo);
            if (j < frames.length)
            {frames[j] = mySpriteSheet;}
            holdHeight = scaledSpriteSheet.getHeight() / rowNo;
            startHeight = (scaledSpriteSheet.getHeight() / rowNo) + (holdHeight * j);

            if (animation.equals("Blink"))
            {
                switch (j) {
                    case 0:
                    case 1:
                        blinkXPos[j] = (int) (myXPos + (mySWidth * 0.8024259832));
                        blinkYPos[j] = (int) (myYPos + (mySHeight / 0.4354936402));
                        break;
                    case 2:
                        blinkXPos[j] = (int) (myXPos + (mySWidth * 0.8024259832));
                        blinkYPos[j] = (int) (myYPos + (mySHeight / 0.4354936402));
                        break;
                    case 3:
                        blinkXPos[j] = (int) (myXPos + (mySWidth * 0.8024259832));
                        blinkYPos[j] = (int) (myYPos + (mySHeight / 0.4354936402));
                        break;
                    case 4:
                        blinkXPos[j] = (int) (myXPos + (mySWidth * 0.8024259832));
                        blinkYPos[j] = (int) (myYPos + (mySHeight / 0.4354936402));
                        break;
                    case 5:
                        blinkXPos[j] = (int) (myXPos + (mySWidth * 0.8024259832));
                        blinkYPos[j] = (int) (myYPos + (mySHeight / 0.4354936402));
                        break;
                }
            }
        }
        return frames[i];
    }

    public void setSpriteMap(Bitmap myBitmap) {
        spriteMap = myBitmap;
    }
    public void setScaledSpriteMap (Bitmap bmp)
    {
        scaledSpriteMap = bmp;
    }
    public void setSpriteSheet(Bitmap myBitmap) {
        spriteSheet = myBitmap;
        holderSS = spriteSheet;
    }
    public void setScaledSpriteSheet (Bitmap bmp)
    {
        scaledSpriteSheet = bmp;
        holderSSS = scaledSpriteSheet;
    }

    public void setBlinkSpriteSheet(Bitmap bmp) {
        blinkSpriteSheet = bmp;
    }
    public void setDeathSpriteSheet(Bitmap bmp)
    {
        deathSpriteSheet = bmp;
    }
    public void setScaledBlinkSpriteSheet (Bitmap bmp)
    {
        scaledBlinkSpriteSheet = bmp;
    }
    public void setScaledDeathSpriteSheet(Bitmap bmp)
    {
        scaledDeathSpriteSheet = bmp;
    }
    public void setName (String myName){
        this.myName = myName;
    }
    public String getMyName()
    {
        return myName;
    }
}