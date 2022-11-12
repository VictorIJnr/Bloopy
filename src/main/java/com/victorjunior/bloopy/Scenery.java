package com.victorjunior.bloopy;

/**
 * Created by Victor on 04/09/2015
 */

import android.graphics.Bitmap;
import android.graphics.Paint;

public class Scenery
{
    Bitmap myBitmap;
    Bitmap myScaledBitmap, noSoundBitmap;
    boolean fingerMoving;
    int mySWidth = 0, myXPos;
    int mySHeight = 0, myYPos;
    int screenH, screenW;
    int totalHeight;
    int fingerSpeed, fingerWaitTime;
    Paint myPaint;
    String myName;
    String sceneType;

    public Scenery(String name, int screenW, int screenH)
    {
        myName = name;
        this.screenH = screenH;
        this.screenW = screenW;

        switch(myName)
        {
            case "bluelayer": case "orangelayer": case "skylayer": case "yellowlayer":
                sceneType = "bck_";
                break;
            case "pause": case "upleaders":case "upplay":case "upreplay":case "uphome":
            case "pleaders":case "pplay":case "preplay":case "phome":case "speakers":
                sceneType = "btn_";
                break;
            case "finger":case "locked":
                sceneType = "ext_";
                break;
        }

        switch (sceneType)
        {
            case "bck_":
                switch (myName)
                {
                    case "bluelayer":
                        mySWidth = (screenW);
                        mySHeight = (int)(mySWidth * 0.186878);
                        break;
                    case "orangelayer":
                        mySWidth = (screenW);
                        mySHeight = (int)(mySWidth * 0.210592);
                        break;
                    case "yellowlayer":
                        mySWidth = (screenW);
                        mySHeight = (int)(mySWidth * 0.134414);
                        break;
                    case "skylayer":
                        mySWidth = (int) (screenW * 1.25);
                        mySHeight = (screenH);
                        break;
                }
                break;
            case "btn_":
                switch(myName)
                {
                    case "pause":
                        mySWidth = (int) (screenW / 12.5);  // Must be equal to each other
                        mySHeight = (int) (screenW / 12.5);
                        break;
                    case "speakers":
                        mySWidth = (screenW / 10);
                        mySHeight = (int) (mySWidth * 1.875);
                        break;
                    case "pplay":case "pleaders":case "preplay":case "phome":
                    case "upplay":case "upleaders":case "upreplay":case "uphome":
                        mySWidth = (screenW / 5);
                        mySHeight = (int) (mySWidth * 0.7);
                        break;
                }
                break;
            case "ext_":
                switch(myName)
                {
                    case "finger":
                        mySWidth = (screenW / 5);
                        mySHeight = (int)(mySWidth * 1.283);
                        fingerSpeed = mySWidth / 25;
                        fingerWaitTime = 1000;
                        myPaint = new Paint();
                        myPaint.setAntiAlias(true);
                        myPaint.setAlpha(255);
                        break;
                    case "locked":
                        mySWidth = (int) (screenW / 7.5);
                        mySHeight = (int) (mySWidth * 1.22);
                        break;
                }
                break;
        }
    }

    public void setSpeakers()
    {
        Bitmap tempBmp = Bitmap.createBitmap(myBitmap,0,myBitmap.getHeight() / 2, myBitmap.getWidth(), myBitmap.getHeight() / 2);
        myBitmap = Bitmap.createBitmap(myBitmap,0,0,myBitmap.getWidth(), myBitmap.getHeight() / 2);
        noSoundBitmap = Bitmap.createScaledBitmap(tempBmp, mySWidth, mySHeight, true);
    }

    public void setCoords(int tHeight)
    {
        // Setting the starting co-ordinates for all of the background images
        totalHeight = tHeight;
        switch (myName)
        {
            case "bluelayer":
                myYPos = screenH - myScaledBitmap.getHeight();
                totalHeight = myYPos;
                break;
            case "orangelayer":
                myYPos = (int)(tHeight / 1.02395070613); // Do not change the unnamed constant
                totalHeight = myYPos;
                break;
            case "yellowlayer":
                myYPos = (int)(tHeight / 1.03205390028); // 1.06205390028); // Do not change the unnamed constant
                totalHeight = myYPos;
                break;
            case "skylayer":
                myYPos = 0;
                myXPos = screenW - myScaledBitmap.getWidth();
                break;
            case "pause":
                myYPos = (int) (myScaledBitmap.getHeight() * 0.5);
                myXPos = (int) (screenW - myScaledBitmap.getWidth()*1.5);
                break;
            case "pplay": case "upplay":
            myYPos = (int) (screenH * 0.6);
            myXPos = (int) ((screenW - myScaledBitmap.getWidth()) * 0.3);
            break;
            case "pleaders": case "upleaders":
            myYPos = (int) (screenH * 0.6);
            myXPos = (int) ((screenW - myScaledBitmap.getWidth()) * 0.7);
            break;
            case "preplay":case "upreplay":
            myYPos = (int) (screenH * 0.5);
            myXPos = (int) ((screenW - myScaledBitmap.getWidth()) * 0.3);
            break;
            case "phome":case "uphome":
            myYPos = (int) (screenH * 0.5);
            myXPos = (int) ((screenW - myScaledBitmap.getWidth()) * 0.7);
            break;
            case "speakers":
                myYPos = (int) (screenH * 0.7);
                myXPos = (int) ((screenW - (myScaledBitmap.getWidth() / 2)) * 0.5);
                break;
            case "finger":
                myYPos = (int) (mySHeight * 1.5);
                myXPos = 0;
                break;
            case "locked":
                myYPos = (int) ((screenH - myScaledBitmap.getHeight()) * 0.5);
                myXPos = (int) ((screenW - (myScaledBitmap.getWidth() / 2)) * 0.5);
                break;
        }
    }

    public void setMyBitmap(Bitmap bmp)
    {
        myBitmap = bmp;
    }
    public void setMyScaledBitmap(Bitmap bmp)
    {
        myScaledBitmap = bmp;
    }

    public int getTotalHeight()
    {
        return totalHeight;
    }
    public String getSceneType()
    {
        return sceneType;
    }
}
