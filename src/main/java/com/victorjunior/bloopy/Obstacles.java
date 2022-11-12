package com.victorjunior.bloopy;

/*
 * Created by Victor on 04/10/2015.
*/

import android.graphics.Bitmap;
import android.graphics.Paint;

import java.util.Random;

public class Obstacles {

    static final int NUMBER_OBSTACLE_COLOURS = 9;

    Bitmap[] bitstacle = new Bitmap[NUMBER_OBSTACLE_COLOURS];
    Bitmap myBitstacle;
    boolean damageable = true, scoreable = true, multipliable = true;
    boolean moving = false, onScreen = false;
    boolean addition = true;
    int myRandColor, myRandYPos, myRandRadius, myRandAngle;
    int actualScaledRadius, scaledRadius;
    int myDiameter;
    int myWaitTime;
    int myXPos;
    int circleSpeed;
    int myHeight, myWidth;
    int screenH, screenW;
    Paint circlePaint;

    private Random randY = new Random();
    private Random randAngle = new Random();
    private Random randSpeed = new Random();
    private Random randColour = new Random();
    private Random randRadius = new Random();
    private Random randAddition = new Random();
    private Random randWaitTime = new Random();

    Obstacles(int w, int h, Bitmap[] bmpObstacles)
    {
        screenH = h;
        screenW = w;
        bitstacle = bmpObstacles;

        addition = randAddition.nextBoolean();
        myRandColor = randColour.nextInt(7);

        scaledRadius = screenW / 50;
        myRandRadius = (addition) ? randRadius.nextInt(scaledRadius) + (screenW / 55)
                : Math.abs(randRadius.nextInt(scaledRadius) - (screenW / 50));
        if (myRandRadius <= screenW / 125)
        {
            myRandRadius = scaledRadius;
        }
        actualScaledRadius = screenW / 60;
        scaledRadius = screenW / 60;
        myDiameter = myRandRadius * 2;

        myWaitTime = randWaitTime.nextInt(901) + 100;
        myRandAngle = randAngle.nextInt(45) + 1; // Maximum 45 degree turn per animation
        myRandYPos = randY.nextInt(screenH - myDiameter);
        circleSpeed = randSpeed.nextInt(scaledRadius / 2) + (myRandRadius / 2);

        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        myBitstacle = bitstacle[myRandColor];
        myBitstacle = Bitmap.createScaledBitmap(myBitstacle,myDiameter, myDiameter, true);
        myHeight = myBitstacle.getHeight();
        myWidth = myBitstacle.getWidth();
    }

    void refreshRandom()
    {
        damageable = true;
        scoreable = true;
        multipliable = true;
        moving = false;
        onScreen = false;

        addition = randAddition.nextBoolean();
        myRandColor = randColour.nextInt(7);

        scaledRadius = screenW / 60;

        myRandRadius = (addition) ? randRadius.nextInt(scaledRadius) + (screenW / 45)
                : Math.abs(randRadius.nextInt(scaledRadius) - (screenW / 50));
        if (myRandRadius <= screenW / 100)
        {
            myRandRadius = scaledRadius;
        }
        myDiameter = myRandRadius * 2;

        myWaitTime = randWaitTime.nextInt(651) + 100;
        myRandAngle = randAngle.nextInt(45) + 1; // Maximum 45 degree turn per animation
        myRandYPos = randY.nextInt(screenH - myDiameter);
        circleSpeed = randSpeed.nextInt(scaledRadius / 2) + (myRandRadius / 2);

        myBitstacle = bitstacle[myRandColor];
        myBitstacle = Bitmap.createScaledBitmap(myBitstacle, myDiameter, myDiameter, true);
        myHeight = myBitstacle.getHeight();
        myWidth = myBitstacle.getWidth();
    }

    void refreshRandom(int multi)
    {
        damageable = true;
        scoreable = true;
        multipliable = true;
        moving = false;
        onScreen = false;

        addition = randAddition.nextBoolean();
        myRandColor = randColour.nextInt(7);

        scaledRadius = actualScaledRadius + (actualScaledRadius * (multi / 5));

        myRandRadius = (addition) ? randRadius.nextInt(scaledRadius) + (screenW / 45)
                : Math.abs(randRadius.nextInt(scaledRadius) - (screenW / 50));
        if (myRandRadius <= screenW / 100)
        {
            myRandRadius = scaledRadius;
        }
        myDiameter = myRandRadius * 2;

        myWaitTime = randWaitTime.nextInt(651) + 100;
        myRandAngle = randAngle.nextInt(45) + 1; // Maximum 45 degree turn per animation
        myRandYPos = randY.nextInt(screenH - myDiameter);
        circleSpeed = randSpeed.nextInt(scaledRadius / 2) + (myRandRadius / 2);

        myBitstacle = bitstacle[myRandColor];
        myBitstacle = Bitmap.createScaledBitmap(myBitstacle, myDiameter, myDiameter, true);
        myHeight = myBitstacle.getHeight();
        myWidth = myBitstacle.getWidth();
    }

}
