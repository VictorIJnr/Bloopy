package com.victorjunior.bloopy;

/**
 * Created by Victor on 02/01/2016
 */

/**
 ***********The Bloopy Change-Log***********
 ****************27/01/2016****************
 * Modified the subroutine animateTitle, wasting CPU time
 * Removed the mySounds Soundpool object
 * Removed the variables used for collision detection of circles
 * Removed the threads :    -adsThread
 *                          -musicThread
 *                          -collisionThread
 *                          -animation
 *                          -runGameThrad
 *                          -updateGameThread
 * Too many threads hindered the app's performance
 * Moved the collision detection back into the gameScreenThread, improves collision detection
 * Created the thread:  -extrasThread
 * This thread practically combines the music, ads and title threads into one thread
 * Changed the time to wait for obstacles to 2.5 seconds from 2 seconds
 * Changed the wait-time for a new obstacle to a max of 1 second from 0.5 seconds
 * Changed the wait-time for refreshed obstacles to a max of 0.75 seconds from 0.5 seconds
 *
 ****************28/01/2016****************
 * Modified the obThread:   -The thread is created both as a regular variable and when surfaceCreated is called
 *                          -The thread now gets destroyed along with myThread
 * Removed the unnecessary variable isPlayable
 * Fixed a collision bug, where obstacles that should not damage you, end the game
 * Updated the Version Code to 3
 * Reduced the amount of ads that appear on the game screen from 1 in 3 plays, to 1 in 4 plays
 * Removed the READ_PHONE_STATE permission from the manifest
 *
 ****************29/01/2016****************
 * Added the method isAdLoaded(int ad) to the ValuesInter
 * Ads are checked to see if they are loaded with isAdLoaded(int ad) before they are shown
 * Updated the Version Code to 4
 *
 ****************30/01/2016****************
 * Added the "Tutorial" shows the player how to move their character
 * Added (and later commented out) code to make character animations consistent
 * Added (and later commented out) code to rotate the obstacles
 * Started (but have not completed) code which locks characters
 *
 ****************31/01/2016****************
 * Completed code to unlock and lock characters
 * Fixed bugs that involved the finger not moving or spawning correctly
 * Started work on the bloopiplier
 *
 ****************04/02/2016****************
 * Finished work on the bloopiplier
 * Added achievements into the game
 * Sorted out bugs with the score being displayed incorrectly in the tutorial
 * Improved the sizing of the obstacles when they are influenced by the bloopiplier
 * Updated Version Code to 5
 **/

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameSurface extends SurfaceView implements SurfaceHolder.Callback
{
    List<Obstacles> obstacleList = new ArrayList<>();
    List<Sprites> sprite = new ArrayList<>();
    List<Scenery> sceneryList = new ArrayList<>();

    static final double TITLE_HEIGHT_MULTIPLIER = 0.35;

    static final int DELTA_ALPHA = 15;
    static final int FRAMES = 25;
    static final int DIRECTION_VERTICAL = 0, DIRECTION_HORIZONTAL = 1;
    static final int LISTTYPE_SCENERY = 0, LISTTYPE_SPRITES = 1;
    static final int MINIMUM_OBSTACLE_TIME = 2500;
    static final int NUMBER_OF_OBSTACLES = 9;
    static final int STATE_RUNNING = 1, STATE_PAUSED = 2, STATE_HOLD = 3;
    static final int BLUE_SPEED = 3;
    static final int ORANGE_SPEED = 2;
    static final int YELLOW_SPEED = 1;

    static final String UNLOCKED_FARYNOR = "lockedFarynor";

    static final String MY_PREFERENCES = "myPrefsFile";
    static final String MY_HIGHSCORE = "myHighScore";
    static final String MY_ADCOUNT = "myAdNumber";
    static final String MY_MUSIC = "myMusic";

    int gameState;

    int blueX, blueCloneX;
    int orangeX, orangeCloneX;
    int yellowCloneX, yellowX;
    int titleX, scoreY, multiplierY, multiX;
    int spriteTitleX, spriteTitleY;
    int lastBlueSpeed, lastOrangeSpeed, lastYellowSpeed;

    Paint textPaint, titlePaint, highPaint;
    Scenery skyLayer;
    Scenery blueLayer, orangeLayer, yellowLayer;
    Scenery blueClone, orangeClone, yellowClone;
    Scenery pPlay, upPlay;
    Scenery pLeaders, upLeaders;
    Scenery pReplay, upReplay;
    Scenery pHome, upHome;
    Scenery speakers, finger;
    Scenery locked;

    Matrix rotationMatrix = new Matrix(); // Matrix to rotate each obstacle

    Paint multiPaint = new Paint(); // Paint used for the multiplier

    Paint spritePaint = new Paint(); // Paint used for the character sprite
    Paint lockedPaint = new Paint(); // Paint used for the locked characters
    Paint bloopyPaint = new Paint(); // Paint used for the sprite when replay is pressed
    Paint buttonPaint = new Paint(); // Paint used for the play and leader buttons
    Paint replayPaint = new Paint(); // Paint used for the replay and home buttons

    boolean firstTime = true; //Causes the sprite to spawn in the right place
    boolean seenTutorial = false, runRefresh = true; //runRefresh checks to see if obstacles spawned
    boolean doneAchievements = false;
    boolean running, homeAdLoaded, gameAdLoaded, apiConnected;
    boolean bitmapsLoaded, scoredPoint, musicEnabled, soundOn, clicked;
    boolean reverseBlink, increaseAlpha = false, firstAlpha = true, reverseMouth, reverseMulti;
    boolean onTitle = true, onEnd = false, onGameScreen = false, onLeaderboards = false;
    boolean isLocked = false, isTransitioning = true;
    boolean endAnimation, stayDead, isDead, endGame = false, isCollision = false, titleCoords = true, fingerMove = false;
    boolean playPressed, leadersPressed, replayPressed, homePressed, changeSprite, soundPressed, screenTapped;
    boolean replaying, pseudoReplay, returningHome, pseudoHome;

    float titleY, textScale;

    int numberAdsSeen;
    int multiSize = 30, multiColour;
    int runFrameNo, runBlinkNo, runDeathNo, runMouthNo;
    int titleAlpha, buttonsAlpha, spriteAlpha, scoreAlpha,
            highscoreAlpha, replayAlpha,
            multiAlpha, bloopyAlpha = 255;
    int runFrames = 0;
    int screenW = 1, screenH = 1;
    int score = 0, myHighScore;
    int obSleepTimer = 1;
    int spriteID = 0;
    int spriteX, spriteY;

    int bloopiplier = 1;

    int noFrames = 0;
    int FPS = 0;
    long firstFrameTime = 0;
    long totalFrameTime = 0;

    long interpolation, lastTime, currentTime, lastUpdate, lastFUpdate, lastMulti;

    Context myContext, tempContext;
    String title, myBloopiplier;
    SharedPreferences savedInfo;

    Sprites mySprite;
    Bloopy bloopy;
    Farynor farynor;

    MediaPlayer scoreEffect, clickEffect, deathEffect;
    SurfaceHolder myHolder, tempHolder;
    SurfaceThread myThread;
    ValuesInter myListener;

    Random randMulti = new Random();

    Thread obThread = new Thread()
    {
        @Override
        public void run()
        {
            while(running)
            {
                if(screenTapped && !seenTutorial)
                {
                    seenTutorial = true;
                    fingerMove = false;
                }
                if(onGameScreen && !onEnd && seenTutorial)
                {
                    try
                    {
                        sleep(1);
                        obSleepTimer++;
                    }
                    catch (InterruptedException e){}
                }
            }
        }
    };


    public GameSurface(Context context, AttributeSet aSet)
    {
        super(context, aSet);
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        tempHolder = holder;
        tempContext = context;

        myThread = new SurfaceThread(holder, context, new Handler(){
            @Override
            public void handleMessage(Message msg){}
        });

        setFocusable(true);
    }

    /**Last updated*/
    public class SurfaceThread extends Thread implements GameEvents
    {
        SurfaceThread(SurfaceHolder holder, Context context, Handler handler)
        {
            myHolder = holder;
            myContext = context;
            savedInfo = myContext.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
            myHighScore = savedInfo.getInt(MY_HIGHSCORE, 0);

            Typeface textFace = Typeface.createFromAsset(myContext.getAssets(), "fonts/komikatitle.ttf");
            textScale = getResources().getDisplayMetrics().density;

            multiPaint.setAntiAlias(true);
            multiPaint.setColor(Color.YELLOW);
            multiPaint.setTextSize(30 * textScale);
            multiPaint.setTypeface(textFace);
            multiPaint.setTextAlign(Paint.Align.CENTER);

            textPaint = new Paint();
            textPaint.setAntiAlias(true);
            textPaint.setColor(Color.YELLOW);
            textPaint.setTextSize(60 * textScale);
            textPaint.setTypeface(textFace);
            textPaint.setTextAlign(Paint.Align.CENTER);
            highPaint = new Paint();
            highPaint.setAntiAlias(true);
            highPaint.setColor(Color.YELLOW);
            highPaint.setTextSize(60 * textScale);
            highPaint.setTypeface(textFace);
            highPaint.setTextAlign(Paint.Align.CENTER);
            titlePaint = new Paint();
            titlePaint.setAntiAlias(true);
            titlePaint.setColor(Color.YELLOW);
            titlePaint.setTextSize(60 * textScale);
            titlePaint.setTypeface(textFace);
            titlePaint.setTextAlign(Paint.Align.CENTER);
        }

        @Override
        public void onCloseCall()
        {
            if (multiPaint.getTextSize() >= (textScale * 45))
            {
                reverseMulti = true;
            }
            else if(multiPaint.getTextSize() <= textScale * 30)
            {
                multiPaint.setTextSize(textScale * 30);
                reverseMulti = false;
            }

            if (reverseMulti) {multiSize -= 3;} // 3 is an arbitrary factor of 15
            else {multiSize += 3;}
        }

        @Override
        public void onEndScreen()
        {
            if (obSleepTimer < MINIMUM_OBSTACLE_TIME)
            {
                endGame = false;
                onEnd = false;
            }
            else
            {
                endGame = true;
                onEnd = true;
            }
        }

        @Override
        public void onHomeClicked()
        {
            multiSize = 30;
            bloopiplier = 1;
            for (Obstacles obstacle : obstacleList)
            {
                obstacle.circlePaint.setAlpha(0);
                obstacle.scoreable = true;
                obstacle.damageable = true;
            }
            displayObstacles(false);
            firstTime = true;
            doneAchievements = false;
            onEnd = false;
            seenTutorial = false;
            endGame = false;
            score = 0;
            obSleepTimer = 0;
            bloopiplier = 1;
            finger.myXPos = 0;
            finger.myYPos = (2 * mySprite.myRDHeight);
            textPaint.setColor(Color.YELLOW);
            scoreAlpha = 0;
            stayDead = true;
            increaseAlpha = false;
            pseudoHome = true;
            returningHome = false;
            onTitle = true;
            onGameScreen = false;
            myListener.showAdverts(false, ValuesInter.GAME_AD);
        }

        @Override
        public void onReplayClicked()
        {
            multiSize = 30;
            bloopiplier = 1;
            for (Obstacles obstacle : obstacleList)
            {
                obstacle.refreshRandom();
                obstacle.circlePaint.setAlpha(0);
                obstacle.scoreable = true;
                obstacle.damageable = true;
            }
            displayObstacles(false);
            score = 0;
            obSleepTimer = 0;
            finger.myXPos = 0;
            finger.myYPos = (2 * mySprite.myRDHeight);
            textPaint.setColor(Color.YELLOW);
            stayDead = true;
            doneAchievements = false;
            onEnd = false;
            endGame = false;
            seenTutorial = false;
            increaseAlpha = false;
            firstTime = true;

            myListener.showAdverts(false, ValuesInter.GAME_AD);
            gameAdLoaded = false;

            pseudoReplay = true;
            replaying = false;
        }

        @Override
        public void onPlayClicked()
        {
            clicked = true;
            if(musicEnabled && !clickEffect.isPlaying())
            {
                clickEffect.start();
            }
            if(mySprite.unlocked)
            {   onTitle = false;
                onGameScreen = true;
                increaseAlpha = false;
                firstTime = true;
            }
            else
            {
                onTitle = true; /**THIS LINE IS 100% USELESS*/
                /**MAYBE A MESSAGE OR SOMETHING*/
            }
        }

        @Override
        public void onLeadersClicked()
        {
            clicked = true;
            if(musicEnabled && !clickEffect.isPlaying())
            {
                clickEffect.start();
            }
            myListener.showLeaderboards();
        }

        @Override
        public void run()
        {
            lastTime = System.currentTimeMillis();

            if(obThread.getState() == State.NEW)
            {
                obThread.start();
            }

            if(!bitmapsLoaded)
            {
                setObstacles();
                setScenes();
                setSounds();
                setSprites();
                bitmapsLoaded = true;
            }

            displayObstacles(false);

            skyLayer = sceneryList.get(find(LISTTYPE_SCENERY, "skylayer"));
            blueLayer = sceneryList.get(find(LISTTYPE_SCENERY, "bluelayer"));
            orangeLayer = sceneryList.get(find(LISTTYPE_SCENERY, "orangelayer"));
            yellowLayer = sceneryList.get(find(LISTTYPE_SCENERY, "yellowlayer"));
            blueClone = sceneryList.get(find(LISTTYPE_SCENERY, "bluelayer"));
            orangeClone = sceneryList.get(find(LISTTYPE_SCENERY, "orangelayer"));
            yellowClone = sceneryList.get(find(LISTTYPE_SCENERY, "yellowlayer"));

            pPlay = sceneryList.get(find(LISTTYPE_SCENERY, "pplay"));
            upPlay = sceneryList.get(find(LISTTYPE_SCENERY, "upplay"));
            pLeaders = sceneryList.get(find(LISTTYPE_SCENERY, "pleaders"));
            upLeaders = sceneryList.get(find(LISTTYPE_SCENERY, "upleaders"));
            pReplay = sceneryList.get(find(LISTTYPE_SCENERY, "preplay"));
            upReplay = sceneryList.get(find(LISTTYPE_SCENERY, "upreplay"));
            pHome = sceneryList.get(find(LISTTYPE_SCENERY, "phome"));
            upHome = sceneryList.get(find(LISTTYPE_SCENERY, "uphome"));

            speakers = sceneryList.get(find(LISTTYPE_SCENERY, "speakers"));
            finger = sceneryList.get(find(LISTTYPE_SCENERY, "finger"));
            locked = sceneryList.get(find(LISTTYPE_SCENERY, "locked"));

            bloopyPaint.setAntiAlias(true);
            buttonPaint.setAntiAlias(true);
            replayPaint.setAntiAlias(true);
            spritePaint.setAntiAlias(true);
            lockedPaint.setAntiAlias(true);

            blueX = blueLayer.myXPos;
            blueCloneX = screenW;
            orangeX = orangeLayer.myXPos;
            orangeCloneX = screenW;
            yellowX = yellowLayer.myXPos;
            yellowCloneX = screenW;

            titleX = screenW/2;
            titleY = (int)(screenH * TITLE_HEIGHT_MULTIPLIER);
            scoreY = (int) textPaint.getTextSize();

            numberAdsSeen = savedInfo.getInt(MY_ADCOUNT, 0);
            musicEnabled = savedInfo.getBoolean(MY_MUSIC, true);

            mySprite = sprite.get(spriteID); // Move when character choice is implemented
            title = mySprite.myName;

            while(running)
            {
                while (onTitle)
                {
                    runTitle();
                }
                while (onGameScreen)
                {
                    runGame();
                }
            }
        }

        void drawGame(Canvas myCanvas)
        {
            synchronized (myHolder)
            {
                try{
                    spriteX = mySprite.myXPos;
                    spriteY = mySprite.myYPos;
                    mySprite.myXPos = spriteX;
                    mySprite.myYPos = spriteY;

                    buttonPaint.setAlpha(buttonsAlpha);
                    replayPaint.setAlpha(replayAlpha);
                    spritePaint.setAlpha(spriteAlpha);
                    lockedPaint.setAlpha(128);
                    bloopyPaint.setAlpha(bloopyAlpha);
                    titlePaint.setAlpha(titleAlpha);
                    textPaint.setAlpha(scoreAlpha);
                    multiPaint.setAlpha(scoreAlpha);
                    highPaint.setAlpha(highscoreAlpha);

                    if (!(myCanvas == null)) {
                        myCanvas.drawColor(Color.WHITE);

                        /******* Drawing the Sky *******/

                        myCanvas.drawBitmap(skyLayer.myScaledBitmap,
                                skyLayer.myXPos, skyLayer.myYPos, null);

                        myCanvas.drawBitmap(yellowLayer.myScaledBitmap,
                                yellowX, yellowLayer.myYPos, null);
                        myCanvas.drawBitmap(yellowClone.myScaledBitmap,
                                yellowCloneX, yellowClone.myYPos, null); // Draw the yellow

                        myCanvas.drawBitmap(orangeLayer.myScaledBitmap,
                                orangeX, orangeLayer.myYPos, null);
                        myCanvas.drawBitmap(orangeClone.myScaledBitmap,
                                orangeCloneX, orangeClone.myYPos, null); // Draw the orange

                        myCanvas.drawBitmap(blueLayer.myScaledBitmap,
                                blueX, blueLayer.myYPos, null);
                        myCanvas.drawBitmap(blueClone.myScaledBitmap,
                                blueCloneX, blueClone.myYPos, null); // Draw the blue

                        /**** End of Drawing the Sky ****/


                        /****** Drawing the Obstacles ******/

                        for (Obstacles obstacle : obstacleList)
                        {
                            myCanvas.drawBitmap(obstacle.myBitstacle, obstacle.myXPos, obstacle.myRandYPos, obstacle.circlePaint);
                        }

                        /**** End of Drawing the Obstacles ****/


                        /**Drawing the character*/

                        if (stayDead)
                        {
                            myCanvas.drawBitmap(mySprite.deathFrames[runDeathNo],
                                    spriteX, spriteY, bloopyPaint);
                            spritePaint.setAlpha(0);
                            animateEnd();
                            endAnimation = true;
                        }

                        if (onEnd && runFrameNo == 0)
                        {
                            if(runDeathNo == mySprite.deathNo)
                            {
                                runDeathNo = mySprite.deathNo - 1;
                            }
                            myCanvas.drawBitmap(mySprite.deathFrames[runDeathNo],
                                    spriteX, spriteY, spritePaint);
                            animateEnd();
                            endAnimation = true;
                        }
                        else if (!onEnd || !endAnimation)
                        {
                            myCanvas.drawBitmap(mySprite.runFrames[runFrameNo],
                                    spriteX, spriteY, spritePaint);
                            myCanvas.drawBitmap(mySprite.blinkFrames[runBlinkNo],
                                    mySprite.blinkXPos[1],
                                    mySprite.blinkYPos[1], spritePaint);
                            if (mySprite.hasMouth) {
                                myCanvas.drawBitmap(mySprite.mouthFrames[runMouthNo],
                                        mySprite.mouthXPos[1],
                                        mySprite.mouthYPos[1], spritePaint);
                            }
                        }
                        /** End of character drawings*/


                        /******* Drawing the Text *******/

                        if (!replaying) {myCanvas.drawText(title, titleX, titleY, titlePaint);}
                        myCanvas.drawText(Integer.toString(score), titleX, scoreY, textPaint);
                        myCanvas.drawText(Integer.toString(myHighScore), screenW/2, (int)(screenH *0.4), highPaint);
                        if (bloopiplier > 1 && seenTutorial)
                        {
                            myCanvas.drawText(myBloopiplier, screenW/2,
                                    scoreY + multiPaint.getTextSize(), multiPaint);
                        }

                        /***** End of Drawing the Text *****/


                        /** Button Drawings */
                        if (replayPressed)
                        {
                            myCanvas.drawBitmap(pReplay.myScaledBitmap,
                                    pReplay.myXPos,
                                    pReplay.myYPos, replayPaint);
                        }
                        else
                        {
                            myCanvas.drawBitmap(upReplay.myScaledBitmap,
                                    upReplay.myXPos,
                                    upReplay.myYPos, replayPaint);
                        }

                        if (homePressed)
                        {
                            myCanvas.drawBitmap(pHome.myScaledBitmap,
                                    pHome.myXPos,
                                    pHome.myYPos, replayPaint);
                        }
                        else
                        {
                            myCanvas.drawBitmap(upHome.myScaledBitmap,
                                    upHome.myXPos,
                                    upHome.myYPos, replayPaint);
                        }

                        /** End of Button Drawings */

                        /******** Tutorial Drawing ********/
                        if(onGameScreen && !seenTutorial
                                && spriteAlpha == 255 && !pseudoReplay
                                && !pseudoHome && increaseAlpha
                                && !onEnd
                                )
                        {
                            myCanvas.drawBitmap(finger.myScaledBitmap, finger.myXPos,
                                    finger.myYPos, finger.myPaint);
                        }
                        /**** End of Tutorial Drawing ****/
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        void drawTitle(Canvas myCanvas)
        {
            synchronized (myHolder)
            {
                try
                {
                    spriteX = spriteTitleX;
                    spriteY = spriteTitleY;
                    mySprite.myXPos = spriteTitleX;
                    mySprite.myYPos = spriteTitleY;

                    buttonPaint.setAlpha(buttonsAlpha);
                    spritePaint.setAlpha(spriteAlpha);
                    lockedPaint.setAlpha(128);
                    titlePaint.setAlpha(titleAlpha);

                    if (!(myCanvas == null))
                    {
                        myCanvas.drawColor(Color.WHITE);

                        /******* Drawing the Sky *******/

                        myCanvas.drawBitmap(skyLayer.myScaledBitmap,
                                skyLayer.myXPos, skyLayer.myYPos, null);

                        myCanvas.drawBitmap(yellowLayer.myScaledBitmap,
                                yellowX, yellowLayer.myYPos, null);
                        myCanvas.drawBitmap(yellowClone.myScaledBitmap,
                                yellowCloneX, yellowClone.myYPos, null); // Draw the yellow

                        myCanvas.drawBitmap(orangeLayer.myScaledBitmap,
                                orangeX, orangeLayer.myYPos, null);
                        myCanvas.drawBitmap(orangeClone.myScaledBitmap,
                                orangeCloneX, orangeClone.myYPos, null); // Draw the orange

                        myCanvas.drawBitmap(blueLayer.myScaledBitmap,
                                blueX, blueLayer.myYPos, null);
                        myCanvas.drawBitmap(blueClone.myScaledBitmap,
                                blueCloneX, blueClone.myYPos, null); // Draw the blue

                        /**** End of Drawing the Sky ****/


                        /**Drawing the character*/

                        if (!onEnd || !endAnimation)
                        {
                            if(mySprite.unlocked)
                            {
                                myCanvas.drawBitmap(mySprite.runFrames[runFrameNo], //screenW/2, screenH/2, null);  //CHANGED FROM ANIMATECHARACTER
                                        spriteX, spriteY, spritePaint);
                                myCanvas.drawBitmap(mySprite.blinkFrames[runBlinkNo], //screenW/2, screenH/2, null); //CHANGED FROM ANIMATECHARACTER
                                        mySprite.blinkXPos[1],
                                        mySprite.blinkYPos[1], spritePaint);
                                if (mySprite.hasMouth) {
                                    myCanvas.drawBitmap(mySprite.mouthFrames[runMouthNo],
                                            mySprite.mouthXPos[1],
                                            mySprite.mouthYPos[1], spritePaint);
                                }
                            }
                            else
                            {
                                myCanvas.drawBitmap(mySprite.runFrames[runFrameNo],
                                        spriteX, spriteY, lockedPaint);
                                myCanvas.drawBitmap(mySprite.blinkFrames[runBlinkNo],
                                        mySprite.blinkXPos[1],
                                        mySprite.blinkYPos[1], lockedPaint);
                                if (mySprite.hasMouth) {
                                    myCanvas.drawBitmap(mySprite.mouthFrames[runMouthNo],
                                            mySprite.mouthXPos[1],
                                            mySprite.mouthYPos[1], lockedPaint);
                                }
                                myCanvas.drawBitmap(locked.myScaledBitmap,
                                        locked.myXPos,
                                        locked.myYPos,null);
                            }
                        }
                        /** End of character drawings*/


                        /******* Drawing the Text *******/

                        if (!replaying) {myCanvas.drawText(title, titleX, titleY, titlePaint);}

                        /***** End of Drawing the Text *****/


                        /** Button Drawings */
                        if (playPressed)
                        {
                            myCanvas.drawBitmap(pPlay.myScaledBitmap,
                                    pPlay.myXPos,
                                    pPlay.myYPos, buttonPaint);
                        }
                        else
                        {
                            myCanvas.drawBitmap(upPlay.myScaledBitmap,
                                    upPlay.myXPos,
                                    upPlay.myYPos, buttonPaint);
                        }

                        if (leadersPressed)
                        {
                            myCanvas.drawBitmap(pLeaders.myScaledBitmap,
                                    pLeaders.myXPos,
                                    pLeaders.myYPos, buttonPaint);
                        }
                        else
                        {
                            myCanvas.drawBitmap(upLeaders.myScaledBitmap,
                                    upLeaders.myXPos,
                                    upLeaders.myYPos, buttonPaint);
                        }

                        if (musicEnabled)
                        {
                            myCanvas.drawBitmap(speakers.myScaledBitmap,
                                    speakers.myXPos,
                                    speakers.myYPos, buttonPaint);
                        }
                        else
                        {
                            myCanvas.drawBitmap(speakers.noSoundBitmap,
                                    speakers.myXPos,
                                    speakers.myYPos, buttonPaint);
                        }
                        /** End of Button Drawings */
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        void draw(Canvas myCanvas)
        {
            synchronized (myHolder)
            {
                try{
                    spriteX = (titleCoords) ? spriteTitleX : mySprite.myXPos;
                    spriteY = (titleCoords) ? spriteTitleY : mySprite.myYPos;
                    mySprite.myXPos = spriteX;
                    mySprite.myYPos = spriteY;

                    buttonPaint.setAlpha(buttonsAlpha);
                    replayPaint.setAlpha(replayAlpha);
                    spritePaint.setAlpha(spriteAlpha);
                    lockedPaint.setAlpha(128);
                    bloopyPaint.setAlpha(bloopyAlpha);
                    titlePaint.setAlpha(titleAlpha);
                    textPaint.setAlpha(scoreAlpha);
                    multiPaint.setAlpha(scoreAlpha);
                    highPaint.setAlpha(highscoreAlpha);

                    if (!(myCanvas == null)) {
                        myCanvas.drawColor(Color.WHITE);

                        /******* Drawing the Sky *******/

                        myCanvas.drawBitmap(skyLayer.myScaledBitmap,
                                skyLayer.myXPos, skyLayer.myYPos, null);

                        myCanvas.drawBitmap(yellowLayer.myScaledBitmap,
                                yellowX, yellowLayer.myYPos, null);
                        myCanvas.drawBitmap(yellowClone.myScaledBitmap,
                                yellowCloneX, yellowClone.myYPos, null); // Draw the yellow

                        myCanvas.drawBitmap(orangeLayer.myScaledBitmap,
                                orangeX, orangeLayer.myYPos, null);
                        myCanvas.drawBitmap(orangeClone.myScaledBitmap,
                                orangeCloneX, orangeClone.myYPos, null); // Draw the orange

                        myCanvas.drawBitmap(blueLayer.myScaledBitmap,
                                blueX, blueLayer.myYPos, null);
                        myCanvas.drawBitmap(blueClone.myScaledBitmap,
                                blueCloneX, blueClone.myYPos, null); // Draw the blue

                        /**** End of Drawing the Sky ****/


                        /****** Drawing the Obstacles ******/

                        for (Obstacles obstacle : obstacleList) {
                            //myCanvas.drawCircle(obstacle.myXPos, obstacle.myRandYPos, obstacle.myRandRadius, obstacle.circlePaint);
                            myCanvas.drawBitmap(obstacle.myBitstacle, obstacle.myXPos, obstacle.myRandYPos, obstacle.circlePaint);
                        }

                        /**** End of Drawing the Obstacles ****/


                        /**Drawing the character*/

                        if (stayDead)
                        {
                            myCanvas.drawBitmap(mySprite.deathFrames[runDeathNo], //screenW/2, screenH/2, null);
                                    spriteX, spriteY, bloopyPaint);
                            spritePaint.setAlpha(0);
                            animateEnd();
                            endAnimation = true;
                        }

                        if (onEnd && runFrameNo == 0)
                        {
                            if(runDeathNo == mySprite.deathNo)
                            {
                                runDeathNo = mySprite.deathNo - 1;
                            }
                            myCanvas.drawBitmap(mySprite.deathFrames[runDeathNo], //screenW/2, screenH/2, null);    //CHANGED FROM ANIMATECHARACTER
                                    spriteX, spriteY, spritePaint);
                            animateEnd();
                            endAnimation = true;
                        }
                        else if (!onEnd || !endAnimation)
                        {
                            if(mySprite.unlocked)
                            {
                                myCanvas.drawBitmap(mySprite.runFrames[runFrameNo], //screenW/2, screenH/2, null);  //CHANGED FROM ANIMATECHARACTER
                                        spriteX, spriteY, spritePaint);
                                myCanvas.drawBitmap(mySprite.blinkFrames[runBlinkNo], //screenW/2, screenH/2, null); //CHANGED FROM ANIMATECHARACTER
                                        mySprite.blinkXPos[1],
                                        mySprite.blinkYPos[1], spritePaint);
                                if (mySprite.hasMouth) {
                                    myCanvas.drawBitmap(mySprite.mouthFrames[runMouthNo],
                                            mySprite.mouthXPos[1],
                                            mySprite.mouthYPos[1], spritePaint);
                                }
                            }
                            else
                            {
                                myCanvas.drawBitmap(mySprite.runFrames[runFrameNo],
                                        spriteX, spriteY, lockedPaint);
                                myCanvas.drawBitmap(mySprite.blinkFrames[runBlinkNo],
                                        mySprite.blinkXPos[1],
                                        mySprite.blinkYPos[1], lockedPaint);
                                if (mySprite.hasMouth) {
                                    myCanvas.drawBitmap(mySprite.mouthFrames[runMouthNo],
                                            mySprite.mouthXPos[1],
                                            mySprite.mouthYPos[1], lockedPaint);
                                }
                                myCanvas.drawBitmap(locked.myScaledBitmap,
                                        locked.myXPos,
                                        locked.myYPos,null);
                            }
                        }
                        /** End of character drawings*/


                        /******* Drawing the Text *******/

                        if (!replaying) {myCanvas.drawText(title, titleX, titleY, titlePaint);}
                        if (onGameScreen)
                        {
                            myCanvas.drawText(Integer.toString(score), titleX, scoreY, textPaint);
                            myCanvas.drawText(Integer.toString(myHighScore), screenW/2, (int)(screenH *0.4), highPaint);
                            if (bloopiplier > 1
                                    && seenTutorial)
                            {
                                myCanvas.drawText(myBloopiplier, screenW/2,
                                        scoreY + multiPaint.getTextSize(), multiPaint);
                            }
                        }

                        /***** End of Drawing the Text *****/


                        /** Button Drawings */
                        if (playPressed) {
                            myCanvas.drawBitmap(pPlay.myScaledBitmap,
                                    pPlay.myXPos,
                                    pPlay.myYPos, buttonPaint);
                        } else {
                            myCanvas.drawBitmap(upPlay.myScaledBitmap,
                                    upPlay.myXPos,
                                    upPlay.myYPos, buttonPaint);
                        }

                        if (leadersPressed) {
                            myCanvas.drawBitmap(pLeaders.myScaledBitmap,
                                    pLeaders.myXPos,
                                    pLeaders.myYPos, buttonPaint);
                        } else {
                            myCanvas.drawBitmap(upLeaders.myScaledBitmap,
                                    upLeaders.myXPos,
                                    upLeaders.myYPos, buttonPaint);
                        }

                        if (replayPressed){
                            myCanvas.drawBitmap(pReplay.myScaledBitmap,
                                    pReplay.myXPos,
                                    pReplay.myYPos, replayPaint);
                        } else{
                            myCanvas.drawBitmap(upReplay.myScaledBitmap,
                                    upReplay.myXPos,
                                    upReplay.myYPos, replayPaint);
                        }

                        if (homePressed){
                            myCanvas.drawBitmap(pHome.myScaledBitmap,
                                    pHome.myXPos,
                                    pHome.myYPos, replayPaint);
                        } else{
                            myCanvas.drawBitmap(upHome.myScaledBitmap,
                                    upHome.myXPos,
                                    upHome.myYPos, replayPaint);
                        }

                        if (musicEnabled)
                        {
                            myCanvas.drawBitmap(speakers.myScaledBitmap,
                                    speakers.myXPos,
                                    speakers.myYPos, buttonPaint);
                        }
                        else
                        {
                            myCanvas.drawBitmap(speakers.noSoundBitmap,
                                    speakers.myXPos,
                                    speakers.myYPos, buttonPaint);
                        }
                        /** End of Button Drawings */

                        /******** Tutorial Drawing ********/
                        if(onGameScreen && !seenTutorial
                                && spriteAlpha == 255 && !pseudoReplay
                                && !pseudoHome && increaseAlpha
                                && !onEnd
                                )
                        {
                            myCanvas.drawBitmap(finger.myScaledBitmap, finger.myXPos,
                                    finger.myYPos, finger.myPaint);
                        }
                        /**** End of Tutorial Drawing ****/

                        if(noFrames > 350)
                            myCanvas.drawText(Integer.toString(FPS), screenW / 1.5f, screenH / 1.5f, titlePaint);
                        if(noFrames > 350)
                            myCanvas.drawText(Integer.toString(FPS), screenW / 1.5f, screenH / 1.5f, textPaint);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        void updateGame()
        {
            synchronized (myHolder)
            {
                interpolation = (System.currentTimeMillis() + FRAMES - lastTime) / FRAMES;
                lastTime = System.currentTimeMillis();
                multiplierY = scoreY + (int) multiPaint.getTextSize();

                myBloopiplier = "Bloopiplier x" + bloopiplier;

                animateSky();
                animateSprite();
                animateTitle();
                animateButtons();

                if (gameState == STATE_RUNNING)
                {
                    /**START OF GAME SCREEN THREAD*/
                    animateFinger();
                    animateMulti();

                    if (isTransitioning && spriteAlpha <= 15)
                    {
                        titleCoords = false;
                    }

                    if (spriteAlpha == 255)
                    {
                        lockObstacleSize();
                        animateObstacles();
                        generateObstacles();
                    }
                    else if(spriteAlpha == 0
                            && !onEnd)
                    {
                        spriteAlpha = 255;
                    }
                    if (!onEnd)
                    {
                        if (spriteAlpha == 255)
                        {
                            calculateScore();
                            if ((obSleepTimer >= MINIMUM_OBSTACLE_TIME) && !pseudoReplay)
                            {
                                displayObstacles(true);
                            }
                            else
                            {
                                displayObstacles(false);
                            }
                        }
                        if(!seenTutorial)
                        {
                            displayObstacles(false);
                            for (Obstacles obstacle : obstacleList) {
                                obstacle.refreshRandom();
                                obstacle.circlePaint.setAlpha(0);
                                obstacle.scoreable = true;
                                obstacle.damageable = true;
                            }
                        }
                    }

                    if (onEnd)
                    {
                        mySprite.playerControlled = false;
                        if (replaying)
                        {
                            this.onReplayClicked();
                        }

                        if (returningHome)
                        {
                            this.onHomeClicked();
                        }

                        if (score > myHighScore)
                        {
                            savedInfo.edit().putInt(MY_HIGHSCORE, score).apply();
                            myHighScore = savedInfo.getInt(MY_HIGHSCORE, score);
                        }
                        myListener.onNewHighScore(score);

                        if(!pseudoReplay) {textPaint.setColor(Color.RED);}
                    }
                    else{highscoreAlpha = 0;}

                    if (!increaseAlpha)
                    {
                        if (spriteAlpha > 0)
                        {
                            spriteAlpha -= DELTA_ALPHA;
                            isTransitioning = (spriteAlpha != 0);
                        }
                        if (bloopyAlpha > 0)
                        {
                            bloopyAlpha -= DELTA_ALPHA;
                        }
                        if (scoreAlpha > 0)
                        {
                            scoreAlpha -= DELTA_ALPHA;
                        }
                        if (highscoreAlpha > 0)
                        {
                            highscoreAlpha -= DELTA_ALPHA;
                        }
                        if (spriteAlpha == 0)
                        {
                            increaseAlpha = true;
                            runDeathNo = 0; // Will not animate death on a second time if omitted
                            endAnimation = false;
                            stayDead = false;
                        }
                    }

                    else if (increaseAlpha)
                    {
                        if (spriteAlpha < 255)
                        {
                            spriteAlpha += DELTA_ALPHA;
                            isTransitioning = (spriteAlpha != 255);
                        }
                        if (bloopyAlpha < 255)
                        {
                            bloopyAlpha += DELTA_ALPHA;
                        }
                        if (scoreAlpha < 255)
                        {
                            scoreAlpha += DELTA_ALPHA;
                        }
                        if ((highscoreAlpha < 255) && onEnd)
                        {
                            highscoreAlpha += DELTA_ALPHA;
                        }
                    }
                    /**END OF GAME SCREEN THREAD*/

                    if(homeAdLoaded)
                    {
                        myListener.showAdverts(false, ValuesInter.HOME_AD);
                        homeAdLoaded = false;
                    }
                    if (onEnd)
                    {
                        if(score >= 999)
                        {
                            savedInfo.edit().putBoolean(UNLOCKED_FARYNOR, true).apply();
                            farynor.unlocked = true;
                        }

                        if(!gameAdLoaded && myListener.isAdLoaded(ValuesInter.GAME_AD))
                        {
                            if (numberAdsSeen % 3 == 0) // The number after the modulus sign depicts ad frequency
                            {
                                numberAdsSeen = 1;
                            }
                            else
                            {
                                numberAdsSeen++;
                            }

                            if (numberAdsSeen == 1)
                            {
                                myListener.showAdverts(true, ValuesInter.GAME_AD);
                            }
                            savedInfo.edit().putInt(MY_ADCOUNT, numberAdsSeen).apply();
                            gameAdLoaded = true;
                            Log.d("Ad Count", "Total Ads:\t" + numberAdsSeen);
                        }
                    }
                    if (!checkObstacles())
                    {
                        if (obSleepTimer > MINIMUM_OBSTACLE_TIME
                                && obSleepTimer < MINIMUM_OBSTACLE_TIME * 1.5)
                        {
                            lockObstacleSize();
                            animateObstacles();
                            generateObstacles();
                            if (runRefresh) {
                                refreshObstacles();
                            }
                            runRefresh = false;
                        }
                        else if(obSleepTimer == 1
                                && (obThread.getState() == State.NEW
                                || !obThread.isAlive()))
                        {
                            try
                            {
                                obThread.start();
                            }
                            catch(IllegalThreadStateException e)
                            {
                                destroyObstacleThread();
                                setObstacleThread();
                                obThread.start();
                            }
                        }
                    }

                    if (spriteAlpha == 255)
                    {
                        isCollision = detectCollision();
                        if (isCollision || endGame)
                        {
                            this.onEndScreen();
                        }
                        else
                        {
                            textPaint.setColor(Color.YELLOW);
                        }

                        if(isCollision &&
                                !deathEffect.isPlaying()
                                && scoreEffect.isPlaying())
                        {
                            scoreEffect.pause();
                            deathEffect.start();
                        }

                        if (!onEnd && seenTutorial)
                        {
                            mySprite.playerControlled = true;
                        }

                        if (!seenTutorial)
                        {
                            obSleepTimer = 0;
                        }
                    }
                    else if (spriteAlpha == 0 && !pseudoHome)
                    {
                        //titleAlpha = 0;
                        firstTime = true;
                        if (mySprite.myXPos != 0
                                && mySprite.myYPos != (int)(multiplierY + multiPaint.getTextSize())
                                && !pseudoReplay
                                && !onEnd)
                        {
                            mySprite.playerControlled = true;
                            mySprite.myXPos = 0;
                            mySprite.myYPos = mySprite.myRDHeight;
                        }
                    }

                    if (firstAlpha && !firstTime)   //Makes sure the title does not disappear
                    {                               //and reappear when replaying
                        titleAlpha = 0;
                    }

                    if(pseudoReplay){textPaint.setColor(Color.YELLOW);}
                    pseudoReplay = false;

                    if (onEnd && !gameAdLoaded && myListener.isAdLoaded(ValuesInter.GAME_AD)) {
                        if (numberAdsSeen % 3 == 0) // The number after the modulus sign depicts ad frequency
                        {
                            numberAdsSeen = 1;
                        } else {
                            numberAdsSeen++;
                        }

                        if (numberAdsSeen == 1) {
                            myListener.showAdverts(true, ValuesInter.GAME_AD);
                        }
                        savedInfo.edit().putInt(MY_ADCOUNT, numberAdsSeen).apply();
                        gameAdLoaded = true;
                        Log.d("Ad Count", "Total Ads:\t" + numberAdsSeen);
                    }
                }
                title = mySprite.myName;
            }
        }

        void updateTitle()
        {
            interpolation = (System.currentTimeMillis() + FRAMES - lastTime) / FRAMES;
            lastTime = System.currentTimeMillis();

            animateButtons();
            animateTitle();
            animateSprite();
            animateSky();

            if (gameState == STATE_RUNNING)
            {
                if (isTransitioning && spriteAlpha <= 15)
                {
                    titleCoords = true;
                    scoreAlpha = 0;
                }

                if (spriteAlpha == 255 && !apiConnected)
                {
                    myListener.onAutomaticConnect();
                    apiConnected = true;
                    firstAlpha = true;
                }

                if (spriteAlpha < 255 && !pseudoHome)
                {
                    spriteAlpha += DELTA_ALPHA;
                    isTransitioning = (spriteAlpha != 255);
                }

                if (changeSprite)
                {
                    mySprite = sprite.get(spriteID);
                }

                textPaint.setColor(Color.YELLOW);
                switch (mySprite.myName)
                {
                    case "farynor":
                        runFrames = Farynor.RUN_ROWS - 1;
                        break;
                    case "bloopy":
                        runFrames = Bloopy.RUN_ROWS - 1;
                        break;
                }

                if (!homeAdLoaded || gameAdLoaded)
                {
                    if (titleAlpha == 255)
                    {
                        if (!homeAdLoaded && myListener.isAdLoaded(ValuesInter.HOME_AD))
                        {
                            myListener.showAdverts(true, ValuesInter.HOME_AD);
                            homeAdLoaded = true;
                        }
                    }
                    else
                    {
                        if (gameAdLoaded)
                        {
                            myListener.showAdverts(false, ValuesInter.GAME_AD);
                            gameAdLoaded = false;
                        }
                    }
                }

                if (musicEnabled)
                {
                    if (scoredPoint
                            && !scoreEffect.isPlaying())
                    {
                        scoreEffect.start();
                        scoredPoint = false;
                    }

                    if (clicked
                            && !clickEffect.isPlaying())
                    {
                        clickEffect.start();
                        clicked = false;
                    }

                    if (isDead
                            && !deathEffect.isPlaying()
                            && scoreEffect.isPlaying())
                    {
                        scoreEffect.pause();
                        deathEffect.start();
                        isDead = false;
                    }
                }

                if (soundOn && !scoreEffect.isPlaying())
                {
                    scoreEffect.start();
                    soundOn = false;
                }
            }
        }

        private void runTitle()
        {
            Canvas canvas = null;
            try {
                synchronized (myHolder)
                {
                    //Log.d("Obstacle Timer", "Time is: " + obSleepTimer);
                    canvas = myHolder.lockCanvas();
                    currentTime = System.currentTimeMillis();
                    calculateFrames();
                    updateTitle();
                    if(canvas != null)
                    {
                        draw(canvas);
                    }
                }
            }
            catch (IllegalArgumentException e)
            {
                e.printStackTrace();
                if(isLocked)
                {
                    myHolder.unlockCanvasAndPost(canvas);
                    isLocked = false;
                }
            }
            finally{
                if(canvas!=null)
                {
                    myHolder.unlockCanvasAndPost(canvas);
                    isLocked = false;
                }
            }
        }

        private void runGame()
        {
            Canvas canvas = null;
            try {
                synchronized (myHolder)
                {
                    //Log.d("Obstacle Timer", "Time is: " + obSleepTimer);
                    canvas = myHolder.lockCanvas();
                    currentTime = System.currentTimeMillis();
                    calculateFrames();
                    updateGame();
                    if(canvas != null)
                    {
                        draw(canvas);
                    }
                }
            }
            catch (IllegalArgumentException e)
            {
                e.printStackTrace();
                if(isLocked)
                {
                    myHolder.unlockCanvasAndPost(canvas);
                    isLocked = false;
                }
            }
            finally{
                if(canvas!=null)
                {
                    myHolder.unlockCanvasAndPost(canvas);
                    isLocked = false;
                }
            }
        }

        void calculateFrames()
        {
            noFrames++;
            firstFrameTime = (noFrames == 1) ? System.currentTimeMillis() : firstFrameTime;
            totalFrameTime = (long)((System.currentTimeMillis() - firstFrameTime) * 0.001);
            if (totalFrameTime != 0)FPS = (int)(noFrames / totalFrameTime);
        }

        boolean doTouchEvent(MotionEvent mEvent) {
            synchronized (myHolder) {
                int eventID = mEvent.getAction();
                int eventX = (int) mEvent.getX();
                int eventY = (int) mEvent.getY();
                int startX, endX, deltaX;
                int startY, endY, deltaY;
                startX = (int) (mEvent.getRawX());
                startY = (int) (mEvent.getRawY());

                //Bitmap upPlay = sceneryList.get(find(LISTTYPE_SCENERY, "upplay")).myScaledBitmap;
                //Bitmap upLeaders = sceneryList.get(find(LISTTYPE_SCENERY, "upleaders")).myScaledBitmap;
                //Bitmap upReplay = sceneryList.get(find(LISTTYPE_SCENERY, "upreplay")).myScaledBitmap;
                // Bitmap upHome = sceneryList.get(find(LISTTYPE_SCENERY, "uphome")).myScaledBitmap;

                switch (eventID) {
                    case MotionEvent.ACTION_UP:

                        if (onTitle) {runTitleMotion(eventX, eventY, "Up");}

                        else if (!onTitle && !onLeaderboards){
                            endX = eventX;
                            endY = eventY;
                            if (mEvent.getHistorySize() > 0) {
                                startX = (int) (mEvent.getHistoricalX(0));
                                startY = (int) (mEvent.getHistoricalY(0));
                            }
                            deltaX = endX - startX;
                            deltaY = endY - startY;
                            runGameMotion(eventX, eventY, deltaX, deltaY, "Up");
                        }

                        break;
                    case MotionEvent.ACTION_MOVE:

                        if(onTitle){runTitleMotion(eventX, eventY, "Move");}
                        else if (!onTitle && !onLeaderboards ){
                            endX = eventX;
                            endY = eventY;
                            if (mEvent.getHistorySize() > 0) {
                                startX = (int) (mEvent.getHistoricalX(0));
                                startY = (int) (mEvent.getHistoricalY(0));
                            }
                            deltaX = endX - startX;
                            deltaY = endY - startY;
                            runGameMotion(eventX, eventY, deltaX, deltaY, "Move");
                        }
                        break;
                    case MotionEvent.ACTION_DOWN:

                        if (onTitle) {runTitleMotion(eventX, eventY, "Down");}
                        else if (!onTitle && !onLeaderboards ){
                            endX = eventX;
                            endY = eventY;
                            if (mEvent.getHistorySize() > 0) {
                                startX = (int) (mEvent.getHistoricalX(0));
                                startY = (int) (mEvent.getHistoricalY(0));
                            }
                            deltaX = endX - startX;
                            deltaY = endY - startY;
                            runGameMotion(eventX, eventY, deltaX, deltaY, "Down");
                        }
                        break;
                }
                invalidate();
                return true;
            }
        }

        void setSurfaceSize(int width, int height)
        {
            if (width == 0 || height == 0)
            {
                surfaceDestroyed(myHolder);
                Intent i = new Intent(myContext, GameActivity.class);
                myContext.startActivity(i);
            }
            synchronized (myHolder){
                screenW = width;
                screenH = height;
            }
        }

        void setRunning(boolean bool)
        {
            running = bool;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent mEvent)
    {
        if (bitmapsLoaded)
        {
            return myThread.doTouchEvent(mEvent);
        }
        return false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        myThread.setSurfaceSize(width, height);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        if (myThread.getState() == Thread.State.TERMINATED)
        {
            myThread = new SurfaceThread(tempHolder, tempContext, new Handler (){
                @Override
                public void handleMessage(Message msg){}
            });
        }

        if (obThread.getState() == Thread.State.TERMINATED) {
            setObstacleThread();
        }

        myThread.start();
        obThread.start();
        myThread.setRunning(true);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        boolean retry = true;
        myThread.setRunning(false);
        while (retry)
        {
            try
            {
                myThread.join();
                Log.d("Destroyed", "Thread has been destroyed");
                retry = false;
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        destroyObstacleThread();
    }

    private boolean checkMenu(int x, int y)
    {
        /**Checks to see if the menu button has been pressed.*/
        return (x >= (screenW - sceneryList.get(find(LISTTYPE_SCENERY, "pause")).mySWidth * 1.5)
                && x <= (screenW - sceneryList.get(find(LISTTYPE_SCENERY, "pause")).mySWidth * 1.5) + sceneryList.get(find(LISTTYPE_SCENERY, "pause")).mySWidth
                && y >= (sceneryList.get(find(LISTTYPE_SCENERY, "pause")).mySHeight* 0.5)
                && y <= (sceneryList.get(find(LISTTYPE_SCENERY, "pause")).mySHeight* 0.5) + (sceneryList.get(find(LISTTYPE_SCENERY, "pause")).mySHeight)
        );
    }

    private boolean detectCollision()   // Works great
    {                                   // Alter collision for different parts of the selected character
        // i.e. front of farynor needs higher y value
        for (Obstacles obstacle : obstacleList)
        {
            try {if (improvedObstacles(obstacle) && obstacle.damageable) {return true;}}
            catch (IllegalArgumentException e){}
        }
        return false;
    }

    private boolean improvedObstacles(Obstacles currentObs)
    {
        Rect spriteBounds = new Rect(mySprite.myXPos, mySprite.myYPos,
                mySprite.myXPos + mySprite.myRDWidth, mySprite.myYPos + mySprite.myRDHeight);
        Rect obsBounds = new Rect(currentObs.myXPos, currentObs.myRandYPos,
                currentObs.myXPos + currentObs.myWidth, currentObs.myRandYPos + currentObs.myHeight);

        if (Rect.intersects(spriteBounds, obsBounds))
        {
            Rect collisionBounds = collisionBounds(spriteBounds, obsBounds);
            for (int i = collisionBounds.left; i < collisionBounds.right; i++)
            {
                for (int j = collisionBounds.top; j < collisionBounds.bottom; j++)
                {
                    if(spriteBounds.left < obsBounds.left)
                    {
                        int spritePixel = mySprite.runFrames[runFrameNo]
                                .getPixel(i - mySprite.myXPos, j - mySprite.myYPos);    //Often generates an IllegalArgumentException
                        int obsPixel = currentObs.myBitstacle.getPixel(i - currentObs.myXPos,
                                j - currentObs.myRandYPos);     //Often generates an IllegalArgumentException
                        boolean spriteTrans = spritePixel != Color.TRANSPARENT;
                        boolean obsTrans = obsPixel != Color.TRANSPARENT;
                        if(((!spriteTrans && !obsTrans) || (spriteTrans != obsTrans))
                                && currentObs.multipliable
                                && !onEnd)
                        {
                            myThread.onCloseCall();
                            multiAlpha = 255;
                            currentObs.multipliable = false;
                            if(bloopiplier != 10) {bloopiplier++;}
                            Log.d("Bloopiplier", "Called Me");
                        }
                        if(spriteTrans && obsTrans)
                        {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private Rect collisionBounds(Rect rect1, Rect rect2)
    {
        int left = Math.max(rect1.left, rect2.left);
        int top = Math.max(rect1.top, rect2.top);
        int right = Math.min(rect1.right, rect2.right);
        int bottom = Math.min(rect1.bottom, rect2.bottom);

        return new Rect(left, top, right, bottom);
    }

    private void calculateScore()
    {
        if ((score > 0 || bloopiplier > 1)
                && (obSleepTimer < MINIMUM_OBSTACLE_TIME
                || !seenTutorial))
        {
            bloopiplier = 1;
            score = 0;
        }

        for (Obstacles obstacle : obstacleList)
        {
            try {
                if ((obstacle.myXPos <= (mySprite.myXPos + (mySprite.myRDWidth * 0.6)))
                        && (obstacle.myXPos >= (mySprite.myXPos + (mySprite.myRDWidth * 0.4)))
                        && obstacle.damageable
                        && obSleepTimer > MINIMUM_OBSTACLE_TIME
                        && seenTutorial)
                {
                    score += bloopiplier;
                    if(musicEnabled)
                    {
                        if(!scoreEffect.isPlaying())
                        {
                            scoreEffect.start();
                        }
                    }
                    scoredPoint = true;
                    obstacle.scoreable = false;
                    obstacle.damageable = false;
                }
                else if (obstacle.myXPos > (mySprite.myXPos + (mySprite.myRDWidth * 0.6))
                        && obstacle.scoreable
                        || replaying)
                {
                    obstacle.damageable = true;
                }
                else if (obstacle.myXPos <= mySprite.myXPos
                        && obstacle.damageable
                        && obstacle.scoreable
                        && obSleepTimer > MINIMUM_OBSTACLE_TIME
                        && seenTutorial)
                {
                    score += bloopiplier;
                    scoredPoint = true;
                    obstacle.scoreable = false;
                    obstacle.damageable = false;
                }
            }
            catch (NullPointerException e){e.printStackTrace();}

            if (!obstacle.damageable)
            {
                obstacle.circlePaint.setAlpha(64);
            }
        }
    }

    private boolean checkObstacles()
    {
        boolean isTrue = false;
        for (Obstacles obstacle : obstacleList)
        {
            isTrue = (obstacle.onScreen);
        }
        return isTrue;
    }

    public int find(int list, String name)
    {
        int i = 0;
        List<Scenery> myScene = sceneryList;
        //List<Sprites> mySprite = sprite;
        if (list == LISTTYPE_SCENERY)
        {
            for (Scenery scene : myScene)
            {
                //(scene.getMyName().equals(name)) ? break : i++
                if (scene.myName.equals(name)){ return i;}
                else {i++;}
            }
        }
        else
        {
            for (Sprites character : sprite)
            {
                //(scene.getMyName().equals(name)) ? break : i++
                if (character.myName.equals(name)){ return i;}
                else {i++;}
            }
        }
        return i;
    }

    public Bitmap flipBmp(Bitmap src, int direction)
    {
        Matrix matrix = new Matrix();
        if (direction == DIRECTION_HORIZONTAL){matrix.preScale(-1.0f, 1.0f);}
        else if (direction == DIRECTION_VERTICAL){matrix.preScale(1.0f, -1.0f);}
        else{return src;}

        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }


    private void runTitleMotion(int eventX, int eventY, String action)
    {
        switch (action)
        {
            case "Move":
                playPressed = (eventX > upPlay.myXPos
                        && eventX < upPlay.myXPos + upPlay.mySWidth
                        && eventY > upPlay.myYPos
                        && eventY < upPlay.myYPos + upPlay.mySHeight
                        && buttonsAlpha == 255);

                leadersPressed = (eventX > upLeaders.myXPos
                        && eventX < upLeaders.myXPos + upLeaders.mySWidth
                        && eventY > upLeaders.myYPos
                        && eventY < upLeaders.myYPos + upLeaders.mySHeight
                        && buttonsAlpha == 255);
                break;

            case "Down":
                playPressed = (eventX > upPlay.myXPos
                        && eventX < upPlay.myXPos + upPlay.mySWidth
                        && eventY > upPlay.myYPos
                        && eventY < upPlay.myYPos + upPlay.mySHeight
                        && buttonsAlpha == 255);

                leadersPressed = (eventX > upLeaders.myXPos
                        && eventX < upLeaders.myXPos + upLeaders.mySWidth
                        && eventY > upLeaders.myYPos
                        && eventY < upLeaders.myYPos + upLeaders.mySHeight
                        && buttonsAlpha == 255);

                changeSprite = (eventX > mySprite.myXPos
                        && eventX < mySprite.myXPos + mySprite.myRDWidth
                        && eventY > mySprite.myYPos
                        && eventY < mySprite.myYPos + mySprite.myRDHeight);

                soundPressed = (eventX > speakers.myXPos
                        && eventX < speakers.myXPos + speakers.myScaledBitmap.getWidth()
                        && eventY > speakers.myYPos
                        && eventY < speakers.myYPos + speakers.myScaledBitmap.getHeight());
                break;
            case "Up":
                if (playPressed)
                {
                    myThread.onPlayClicked();
                }
                else if (leadersPressed)
                {
                    myThread.onLeadersClicked();
                }
                else if (changeSprite)
                {
                    clicked = true;
                    if(musicEnabled && !clickEffect.isPlaying())
                    {
                        clickEffect.start();
                    }
                    if (spriteID == sprite.size() - 1)
                    {
                        spriteID = 0;
                    }
                    else
                    {
                        spriteID++;
                    }
                    mySprite = sprite.get(spriteID);
                }
                else if(soundPressed)
                {
                    musicEnabled = !musicEnabled;
                    savedInfo.edit().putBoolean(MY_MUSIC, musicEnabled).apply();
                    if(musicEnabled) soundOn = true;
                }
                changeSprite = false;
                leadersPressed = false;
                playPressed = false;
                soundPressed = false;
                break;
        }
    }

    private void runGameMotion(int eventX, int eventY, int deltaX, int deltaY, String action)
    {
        switch(action)
        {
            case "Move":
                if (!onEnd) {seenTutorial = true;}
                if (mySprite.playerControlled && !onEnd)
                {
                    if (mySprite.myXPos > (screenW - mySprite.mySWidth)) {
                        mySprite.myXPos = screenW - mySprite.mySWidth;
                    }
                    else if (mySprite.myXPos < 0) {
                        mySprite.myXPos = 0;
                        //glide to 0
                    }
                    else if (mySprite.myYPos < 0) {
                        mySprite.myYPos = 0;
                        // glide to 0
                    }
                    else if (mySprite.myYPos >
                            (int) (screenH - (blueLayer.mySHeight * 1.2))) {
                        mySprite.myYPos = (int) (screenH - (blueLayer.mySHeight * 1.2));
                    }
                    else {
                        mySprite.myYPos += deltaY;
                        mySprite.myXPos += deltaX;
                        // CHANGE NEEDED
                    }
                }
                else if(!mySprite.playerControlled && onEnd)
                {
                    replayPressed = (eventX > upReplay.myXPos
                            && eventX < upReplay.myXPos + upReplay.mySWidth
                            && eventY > upReplay.myYPos
                            && eventY < upReplay.myYPos + upReplay.mySHeight
                            && replayAlpha == 255);

                    homePressed = (eventX > upHome.myXPos
                            && eventX < upHome.myXPos + upHome.mySWidth
                            && eventY > upHome.myYPos
                            && eventY < upHome.myYPos + upHome.mySHeight
                            && replayAlpha == 255);
                }
                break;

            case "Down":
                if (!onEnd) {seenTutorial = true;}
                if (!mySprite.playerControlled && onEnd)
                {
                    replayPressed = (eventX > upReplay.myXPos
                            && eventX < upReplay.myXPos + upReplay.mySWidth
                            && eventY > upReplay.myYPos
                            && eventY < upReplay.myYPos + upReplay.mySHeight
                            && replayAlpha == 255);

                    homePressed = (eventX > upHome.myXPos
                            && eventX < upHome.myXPos + upHome.mySWidth
                            && eventY > upHome.myYPos
                            && eventY < upHome.myYPos + upHome.mySHeight
                            && replayAlpha == 255);
                }

                if(spriteAlpha == 255 && onGameScreen) {screenTapped = true;}

                break;

            case "Up":
                if (!mySprite.playerControlled && onEnd)
                {
                    if(replayPressed)
                    {
                        clicked = true;
                        if(musicEnabled && !clickEffect.isPlaying())
                        {
                            clickEffect.start();
                        }
                        replaying = true;
                        textPaint.setColor(Color.YELLOW);
                        /**Causes obstacles to fly across the screen*/
                        //onEnd = false;
                        //firstTime = true;
                    }
                    if(homePressed)
                    {
                        clicked = true;
                        if(musicEnabled && !clickEffect.isPlaying())
                        {
                            clickEffect.start();
                        }
                        returningHome = true;
                        firstAlpha = false;
                        textPaint.setColor(Color.YELLOW);
                    }
                    if(screenTapped)
                    {
                        seenTutorial = true;
                    }
                    replayPressed = false;
                    screenTapped = false;
                    homePressed = false;
                }
                break;
        }
    }

    private void setSprites()
    {
        Bitmap tempBmp = null, tempBlinkSheet, tempDeathSheet, tempRunSheet, tempMouthSheet;
        Bitmap scaledRun, scaledBlink, scaledDeath, scaledMouth = null;
        boolean testChar;
        int scaledW = 0, scaledH = 0;
        int resourceID;
        Sprites tempSprite;
        String[] names = {"bloopy","farynor"};
        for (String name : names)
        {
            //testChar = (name.equals("biggie"));
            tempSprite = new Sprites(name, screenW, screenH);
            //int resourceID = getResources().getIdentifier(name, "drawable", myContext.getPackageName());
            //tempBmp = BitmapFactory.decodeResource(getResources(), resourceID);
            resourceID = getResources().getIdentifier(name + "run", "drawable", myContext.getPackageName());
            tempRunSheet = BitmapFactory.decodeResource(getResources(), resourceID);
            resourceID = getResources().getIdentifier(name + "blink", "drawable", myContext.getPackageName());
            tempBlinkSheet = BitmapFactory.decodeResource(getResources(), resourceID);
            resourceID = getResources().getIdentifier(name + "death", "drawable", myContext.getPackageName());
            tempDeathSheet = BitmapFactory.decodeResource(getResources(), resourceID);

            switch(name)
            {
                case "farynor":
                    farynor = new Farynor(screenW,screenH);
                    farynor.unlocked = savedInfo.getBoolean(UNLOCKED_FARYNOR, false);
                    scaledW = farynor.mySWidth;
                    scaledH = farynor.mySHeight;
                    tempSprite = farynor;
                    break;
                case "bloopy":
                    bloopy = new Bloopy(screenW, screenH);
                    scaledW = bloopy.mySWidth;
                    scaledH = bloopy.mySHeight;
                    tempSprite = bloopy;
                    break;
            }

            if (tempSprite.hasMouth)
            {
                resourceID = getResources().getIdentifier(name + "mouth", "drawable", myContext.getPackageName());
                tempMouthSheet = BitmapFactory.decodeResource(getResources(), resourceID);
                scaledMouth = Bitmap.createScaledBitmap(tempMouthSheet, tempSprite.mySWMouth,
                        tempSprite.mySHMouth, true);
            }

            //Bitmap scaledBmp = Bitmap.createScaledBitmap(tempBmp, tempSprite.mySWidth, tempSprite.mySHeight, false);
            scaledRun = Bitmap.createScaledBitmap(tempRunSheet, scaledW, scaledH, true);
            scaledBlink = Bitmap.createScaledBitmap(tempBlinkSheet, tempSprite.mySWBlink, tempSprite.mySHBlink, true);
            scaledDeath = Bitmap.createScaledBitmap(tempDeathSheet, tempSprite.mySWDeath, tempSprite.mySHDeath, true);


            switch(name)
            {   /**In order of when they were added*/
                case "farynor":
                    farynor.setArrays(scaledRun, scaledBlink, scaledDeath);
                    tempSprite = farynor;
                    break;
                case "bloopy":
                    bloopy.setArrays(scaledRun, scaledBlink, scaledDeath, scaledMouth);
                    tempSprite = bloopy;
                    break;
            }

            tempSprite.myXPos = (screenW - tempSprite.myRDWidth) / 2;
            tempSprite.myYPos = (screenH - tempSprite.myRDHeight)/ 2;
            spriteTitleX = tempSprite.myXPos;
            spriteTitleY = tempSprite.myYPos;

            sprite.add(tempSprite);
        }
    }

    private void setObstacles()
    {
        Bitmap tempBitstacle = BitmapFactory.decodeResource(getResources(), R.drawable.obstacles);
        Bitmap addBitstacle;
        Bitmap[] bitstacleArray = new Bitmap[Obstacles.NUMBER_OBSTACLE_COLOURS];
        int startHeight, holdHeight;
        startHeight = 0;
        holdHeight = tempBitstacle.getHeight() / Obstacles.NUMBER_OBSTACLE_COLOURS;

        for (int i = 0; i < Obstacles.NUMBER_OBSTACLE_COLOURS; i++)
        {
            addBitstacle = Bitmap.createBitmap(tempBitstacle, 0, startHeight,
                    tempBitstacle.getWidth(), holdHeight);
            bitstacleArray[i] = addBitstacle;
            startHeight = holdHeight * (i + 1);
        }

        for (int i = 0; i < NUMBER_OF_OBSTACLES; i++)
        {
            Obstacles tempObstacle = new Obstacles(screenW, screenH, bitstacleArray);
            tempObstacle.myXPos = (screenW*2)+tempObstacle.myRandRadius + 500;
            obstacleList.add(tempObstacle);
        }
    }

    private void setScenes()
    {
        // Can be converted to an arraylist if the array is too large LOLWUT 2.147 Billion Values
        boolean imBlue = true;
        int myHeight = 0;
        String[] names = {"bluelayer", "orangelayer", "skylayer", "yellowlayer",
                "upleaders", "upplay", "upreplay", "uphome",
                "pleaders", "pplay", "preplay", "phome",
                "speakers", "finger", "locked"};
        for (String name : names)
        {
            Scenery tempScene = new Scenery(name, screenW, screenH);
            Log.d("Scene",name);
            int resourceID = getResources().getIdentifier(tempScene.getSceneType() + name, "drawable", myContext.getPackageName());
            Bitmap tempBmp = BitmapFactory.decodeResource(getResources(), resourceID);
            tempScene.setMyBitmap(tempBmp);
            if(name.equals("speakers")) tempScene.setSpeakers();
            Bitmap scaledBmp = Bitmap.createScaledBitmap(tempScene.myBitmap, tempScene.mySWidth, tempScene.mySHeight, true);
            tempScene.setMyScaledBitmap(scaledBmp);
            tempScene.setCoords(myHeight);
            myHeight = (imBlue) ? tempScene.getTotalHeight() : myHeight;
            imBlue = false;
            sceneryList.add(tempScene);
        }
    }

    private void setSounds()
    {
        scoreEffect = MediaPlayer.create(myContext, R.raw.eff_score);
        clickEffect = MediaPlayer.create(myContext, R.raw.eff_button);
        deathEffect = MediaPlayer.create(myContext, R.raw.eff_death);
        scoreEffect.setVolume(0.3f, 0.3f);
        clickEffect.setVolume(0.25f, 0.25f);
        deathEffect.setVolume(0.3f, 0.3f);
    }

    private void animateButtons()
    {
        if(onTitle){if (buttonsAlpha < 255) {buttonsAlpha += DELTA_ALPHA;}}
        else{if (buttonsAlpha > 0) {buttonsAlpha -= DELTA_ALPHA;}}

        if(onEnd){if (replayAlpha < 255) {replayAlpha += DELTA_ALPHA;}}
        else{if (replayAlpha > 0) {replayAlpha -= DELTA_ALPHA;}}
    }

    private void animateEnd()
    {
        mySprite.playerControlled = false;
        //if(currentTime > lastUpdate + mySprite.myPeriod) everything up to end of method in if statement
        if (runDeathNo == mySprite.deathNo - 1) {
            runDeathNo = mySprite.deathNo - 1;
        } else {
            runDeathNo += 1;
        }
    }

    private void animateFinger()
    {
        if(!seenTutorial)
        {
            score = 0;
            if(finger.myXPos >= mySprite.myXPos
                    && finger.myXPos < screenW / 2
                    && finger.myYPos >= mySprite.myYPos
                    && (((currentTime - lastFUpdate) >= 1000)
                    || finger.fingerMoving))
            {
                finger.myXPos += (int) (finger.fingerSpeed * interpolation);
                finger.myYPos += (int) (finger.fingerSpeed * interpolation);
                finger.fingerMoving = true;
                fingerMove = true;
            }
            else if(finger.myXPos >= screenW / 2)
            {
                finger.myXPos = mySprite.myXPos;
                finger.myYPos = mySprite.myYPos + mySprite.myRDHeight;
                lastFUpdate = currentTime;
                fingerMove = false;
                finger.fingerMoving = false;
            }

            if(checkObstacles())
            {
                seenTutorial = true;
            }

            if((currentTime - lastFUpdate) >= 1000)
            {
                fingerMove = true;
            }

            if(!finger.fingerMoving && fingerMove)
            {
                finger.myXPos += (int) (finger.fingerSpeed * interpolation);
                finger.myYPos += (int) (finger.fingerSpeed * interpolation);
                finger.fingerMoving = true;
                fingerMove = true;
            }
        }
        if (firstTime && spriteAlpha < 25) //25 is a small arbitrary number
        {
            finger.myXPos = mySprite.myXPos;
            finger.myYPos = mySprite.myYPos + mySprite.myRDHeight;
        }
    }

    private void animateMulti()
    {
        multiColour = randMulti.nextInt(9);
        if (currentTime - lastMulti >= 2000 / bloopiplier)
        {
            lastMulti = System.currentTimeMillis();
            switch(multiColour)
            {
                case 0:
                    multiPaint.setARGB(scoreAlpha, 139, 83, 255);
                    break;
                case 1:
                    multiPaint.setARGB(scoreAlpha, 255, 0, 255);
                    break;
                case 2:
                    multiPaint.setARGB(scoreAlpha, 255, 1, 126);
                    break;
                case 3:
                    multiPaint.setARGB(scoreAlpha, 83, 255, 83);
                    break;
                case 4:
                    multiPaint.setARGB(scoreAlpha, 255, 83, 255);
                    break;
                case 5:
                    multiPaint.setARGB(scoreAlpha, 255, 255, 83);
                    break;
                case 6:
                    multiPaint.setARGB(scoreAlpha, 255, 165, 83);
                    break;
                case 7:
                    multiPaint.setARGB(scoreAlpha, 83, 255, 255);
                    break;
                case 8:
                    multiPaint.setARGB(scoreAlpha, 255, 83, 83);
                    break;
            }
        }

        multiPaint.setTextSize(textScale * multiSize);
    }

    private void animateSky()
    {
        lastBlueSpeed = (int)(BLUE_SPEED * interpolation);
        lastOrangeSpeed = (int)(ORANGE_SPEED * interpolation);
        lastYellowSpeed = (int)(YELLOW_SPEED * interpolation);

        if(blueX <= -blueLayer.mySWidth) {
            //blueX = screenW - BLUE_SPEED;
            blueX = blueCloneX + blueLayer.mySWidth - lastBlueSpeed;
        }
        else{
            blueX -= (int)(BLUE_SPEED * interpolation);
        }
        if(blueCloneX <= -blueClone.mySWidth) {
            //blueCloneX = screenW - BLUE_SPEED;
            blueCloneX = blueX + blueClone.mySWidth - lastBlueSpeed;
        }
        else{
            blueCloneX -= (int)(BLUE_SPEED * interpolation);
        }

        if(orangeX <= -orangeLayer.mySWidth) {
            //orangeX = screenW - ORANGE_SPEED;
            orangeX = orangeCloneX + orangeLayer.mySWidth - lastOrangeSpeed;
        }
        else{
            orangeX -= (int)(ORANGE_SPEED * interpolation);
        }
        if(orangeCloneX <= -orangeClone.mySWidth) {
            //orangeCloneX = screenW - ORANGE_SPEED;
            orangeCloneX = orangeX + orangeClone.mySWidth - lastOrangeSpeed;
        }
        else{
            orangeCloneX -= (int)(ORANGE_SPEED * interpolation);
        }

        if(yellowX <= -yellowLayer.mySWidth) {
            //yellowX = screenW - YELLOW_SPEED;
            yellowX = yellowCloneX + yellowLayer.mySWidth - lastYellowSpeed;
        }
        else{
            yellowX -= (int)(YELLOW_SPEED * interpolation);
        }
        if(yellowCloneX <= -yellowClone.mySWidth) {
            //yellowCloneX = screenW - YELLOW_SPEED;
            yellowCloneX = yellowX + yellowClone.mySWidth - lastYellowSpeed;
        }
        else{
            yellowCloneX -= (int)(YELLOW_SPEED * interpolation);
        }
    }

    private void animateSprite()
    {
        //if(currentTime > lastUpdate + mySprite.myPeriod) Everything up to if firstTime in if statement
        //lastUpdate = currentTime;
        if (endAnimation)
        {
            runFrameNo = 0;
        }
        else
        {
            if (runFrameNo == runFrames)
            {
                runFrameNo = 0;
            }
            else if (runFrameNo == 3 && mySprite.myName.equals("farynor"))
            {
                runFrameNo = 4;
            }
            else
            {
                runFrameNo += 1;
            }

            if (runBlinkNo == 4)
            {
                reverseBlink = true;
            }
            else if (runBlinkNo == 0)
            {
                reverseBlink = false;
            }

            if (reverseBlink)
            {
                runBlinkNo -= 1;
            }
            else
            {
                runBlinkNo += 1;
            }

            if (mySprite.hasMouth)
            {
                if (runMouthNo == mySprite.mouthFrames.length - 1)
                {
                    reverseMouth = true;
                }
                else if (runMouthNo == 0)
                {
                    reverseMouth = false;
                }

                if (reverseMouth)
                {
                    runMouthNo -= 1;
                }
                else
                {
                    runMouthNo += 1;
                }
            }
        }

        if (firstTime && spriteAlpha == 0)
        {
            mySprite.myXPos = 0;
            mySprite.myYPos = mySprite.myRDHeight;
            firstTime = false;
        }
        mySprite.blinkXPos[1] = (int) (mySprite.myXPos + (mySprite.myRDWidth * mySprite.blinkXMulti));
        mySprite.blinkYPos[1] = (int) (mySprite.myYPos + (mySprite.myRDHeight * mySprite.blinkYMulti));
        if(mySprite.hasMouth)
        {
            mySprite.mouthXPos[1] = (int) (mySprite.myXPos + (mySprite.myRDWidth * mySprite.mouthXMulti));
            mySprite.mouthYPos[1] = (int) (mySprite.myYPos
                    + (mySprite.myRDHeight * mySprite.mouthYMulti));
        }
    }

    private void animateTitle()
    {
        if (onTitle) {if (titleAlpha < 255) {titleAlpha += DELTA_ALPHA;}}// += must be a factor of 255 e.g 1, 3, 5
        else if (!onTitle){if (titleAlpha > 0) {titleAlpha -= DELTA_ALPHA;}}
    }

    private void lockObstacleSize()
    {
        Obstacles obstacle;
        for (Iterator<Obstacles> obIterator = obstacleList.iterator(); obIterator.hasNext(); )
        {
            obstacle = obIterator.next();
            if (obstacleList.indexOf(obstacle) > NUMBER_OF_OBSTACLES - 1)
            {
                obIterator.remove();
            }
        }
    }

    private void generateObstacles()
    {
        if(seenTutorial)
        {
            for (Obstacles obstacle : obstacleList)
            {
                if (obSleepTimer > MINIMUM_OBSTACLE_TIME)
                {
                    if (obSleepTimer % obstacle.myWaitTime == 0 || obstacle.moving)
                    {
                        obstacle.moving = true;
                        obstacle.myXPos -= (obstacle.circleSpeed * interpolation);
                    }

                    if (obstacle.myXPos <= 0 - obstacle.myRandRadius * 2)
                    {
                        if (!onEnd)
                        {
                            obstacle.myXPos = screenW + obstacle.myRandRadius;
                            obstacle.moving = false;
                            if (bloopiplier > 1) {
                                obstacle.refreshRandom(bloopiplier);}
                            else {obstacle.refreshRandom();}
                            Log.d("Radius", "My Radius: " + obstacle.myRandRadius);
                        }
                        else if (onEnd)
                        {
                            obstacle.myXPos = 0 - obstacle.myRandRadius * 2;
                            obstacle.circlePaint.setAlpha(0);
                        }
                    }

                }
                else if (obSleepTimer < MINIMUM_OBSTACLE_TIME)
                {
                    if (obstacle.myXPos < screenW)
                    {
                        obstacle.myXPos = screenW + obstacle.myRandRadius * 8;
                    }
                }

                obstacle.onScreen = (obstacle.myXPos >= 0 && obstacle.myXPos <= screenW - obstacle.myRandRadius * 2);
            }
        }
    }

    private void animateObstacles()
    {
        if(seenTutorial)
        {
            for (Obstacles obstacle : obstacleList)
            {
                obstacle.myXPos -= (obstacle.circleSpeed * interpolation);
                /** FIX THIS SOMEHOW
                 * rotationMatrix.postRotate(obstacle.myRandAngle);
                 obstacle.myBitstacle = Bitmap.createBitmap(obstacle.myBitstacle, 0, 0,
                 obstacle.myDiameter, obstacle.myDiameter,
                 rotationMatrix, true);
                 ** MAKES THE OBSTACLES ROTATE
                 */
            }
        }
    }

    private void refreshObstacles()
    {
        for (Obstacles obstacle : obstacleList)
        {
            obstacle.refreshRandom();
        }
    }

    private void displayObstacles(boolean show)
    {
        if (show)
        {
            for (Obstacles obstacle : obstacleList) {
                if (obstacle.damageable)
                {
                    obstacle.circlePaint.setAlpha(255);
                }
            }
        }
        else if (!show)
        {
            for (Obstacles obstacle : obstacleList)
            {
                obstacle.circlePaint.setAlpha(0);
            }
        }
    }

    private void destroyObstacleThread()
    {
        boolean retry = true;
        while (retry)
        {
            try
            {
                obThread.join();
                Log.d("Destroyed","Obstacles thread has been destroyed");
                retry = false;
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void setObstacleThread()
    {
        obThread = new Thread()
        {
            @Override
            public void run()
            {
                while(running)
                {
                    if(onGameScreen && !onEnd)
                    {
                        try
                        {
                            sleep(1);
                            obSleepTimer++;
                        }
                        catch (InterruptedException e){}
                    }
                }
            }
        };
    }

    void setLeaderListener(ValuesInter valuesInter)
    {
        myListener = valuesInter;
    }

    void pauseThread()
    {
        gameState = STATE_PAUSED;
    }

    void unPauseThread()
    {
        gameState = STATE_RUNNING;
    }

}

