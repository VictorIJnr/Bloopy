package com.victorjunior.bloopy;

/**
 * Created by Victor on 20/12/2015
 */
public interface ValuesInter
{
    int GAME_AD = 0;
    int HOME_AD = 1;

    boolean isAdLoaded(int ad);

    void onAutomaticConnect();
    void onNewHighScore(int score);
    void showAdverts(boolean showable, int ad);
    void showLeaderboards();

    void onFirstGame();
    void onNewGame();
    void unlockFarynor();
    void unlockBiggie();
}
