package com.victorjunior.bloopy;

/**
 * Created by Victor on 29/10/2015
 */

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.ads.AdListener;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class GameActivity extends Activity implements ValuesInter,// ComponentCallbacks2,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    private static final int RC_UNUSED = 5001;
    private static final int RC_SIGN_IN = 9001;
    private static final String MY_PREFS_FILE = "myPrefsFile";
    private static final String CONNECTING = "isConnecting";

    private boolean[] isAdLoaded = new boolean[2];

    private boolean resolvingConnectionFailure = false;
    private boolean autoStartSignInflow = true;
    private boolean signInClicked = false;
    private boolean openedLeaderboards = false;

    SharedPreferences connectedPrefs;
    GameSurface bloopView;
    GoogleApiClient myApiClient;

    AdView myAdView, myGameAdView;
    AdRequest.Builder myAdBuild = new AdRequest.Builder();
    AdRequest adRequest;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.bloopout);
        bloopView = (GameSurface) findViewById(R.id.bloopy);
        bloopView.setKeepScreenOn(true);
        bloopView.setLeaderListener(this);

        buildApiClient();
        connectedPrefs = getSharedPreferences(MY_PREFS_FILE, MODE_PRIVATE);
        myAdView = (AdView) findViewById(R.id.adView);
        myGameAdView = (AdView) findViewById(R.id.adViewGame);
        myAdBuild.addTestDevice(getResources().getString(R.string.myDeviceID));
        myAdBuild.addTestDevice(getResources().getString(R.string.nexusDeviceID));
        adRequest = myAdBuild.build();
        if(!myAdView.isLoading()) {myAdView.loadAd(adRequest);}
        if(!myGameAdView.isLoading()) {myGameAdView.loadAd(adRequest);}
        myAdView.setVisibility(View.GONE);
        myGameAdView.setVisibility(View.GONE);

        myGameAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                isAdLoaded[0] = true;
                Log.d("Ad Loaded", "Ad Loaded is " + isAdLoaded[0]);
            }
        });
        myAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                isAdLoaded[1] = true;
                Log.d("Ad Loaded","Ad Loaded is " + isAdLoaded[1]);
            }
        });
    }

    @Override
    public void onStart()
    {
        super.onStart();
        //startMusic(MUSIC_TROPICAL);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        bloopView.unPauseThread();
        myAdView.resume();
        myGameAdView.resume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (!openedLeaderboards)
        {
            bloopView.pauseThread();
        }
        myAdView.pause();
        myGameAdView.pause();
        openedLeaderboards = false;
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if (!openedLeaderboards) {bloopView.pauseThread();}
        if(myApiClient.isConnected()){myApiClient.disconnect();}
        //pauseMusic(MUSIC_TROPICAL);
        openedLeaderboards = false;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        myAdView.destroy();
        myGameAdView.destroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent)
    {
        switch(keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
                finish();
                break;
        }
        return super.onKeyDown(keyCode, keyEvent);
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        connectedPrefs.edit().putBoolean(CONNECTING,true).apply();
        myApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("Connceted", "API Client Connected");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        if (!resolvingConnectionFailure)
        {
            if (signInClicked || autoStartSignInflow)
            {
                signInClicked = false;
                autoStartSignInflow = false;
                resolvingConnectionFailure = (!BaseGameUtils.resolveConnectionFailure(this, myApiClient, connectionResult, RC_SIGN_IN,
                        getString(R.string.login_error)));
                Log.d("Login Error", "Connection Failed");
                myApiClient.disconnect();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == RC_SIGN_IN)
        {
            signInClicked = false;
            resolvingConnectionFailure = false;
            if (resultCode == RESULT_OK)
            {
                autoStartSignInflow = true;
                connectedPrefs.edit().putBoolean(CONNECTING,true).apply();
                myApiClient.connect();
            }
            else
            {
                BaseGameUtils.showActivityResultError(this,requestCode,resultCode,R.string.login_error);
                myApiClient.disconnect();
                Log.d("Login Error", "Activity Result");
            }
        }
    }

    @Override
    public boolean isAdLoaded(int ad)
    {
        return isAdLoaded[ad];
    }

    @Override
    public void onAutomaticConnect()
    {
        myApiClient.connect();
    }

    @Override
    public void onNewHighScore(int score)
    {
        if (myApiClient.isConnected())
        {
            Games.Leaderboards.submitScore(myApiClient, getString(R.string.leaderboardID), score);
        }
    }

    @Override
    public void showAdverts(final boolean showable, final int ad)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (showable)
                {
                    switch(ad)
                    {
                        case ValuesInter.HOME_AD:
                            myAdView.resume();
                            myAdView.setVisibility(View.VISIBLE);
                            break;
                        case ValuesInter.GAME_AD:
                            myGameAdView.resume();
                            myGameAdView.setVisibility(View.VISIBLE);
                            break;
                    }
                }
                else
                {
                    switch(ad)
                    {
                        case ValuesInter.HOME_AD:
                            myAdView.setVisibility(View.GONE);
                            myAdView.pause();
                            break;
                        case ValuesInter.GAME_AD:
                            myGameAdView.setVisibility(View.GONE);
                            myGameAdView.pause();
                    }
                }
            }
        });
    }

    @Override
    public void showLeaderboards()
    {
        try
        {
            if (myApiClient.isConnected()) {
                startActivityForResult(Games.Leaderboards.getLeaderboardIntent(myApiClient, getString(R.string.leaderboardID)), RC_UNUSED);
                bloopView.gameState = GameSurface.STATE_HOLD;
                openedLeaderboards = true;
                Log.d("Connected", "Connected is True");
            }
            else {
                Log.d("Connected", "Connected is False");
                myApiClient.connect();
                signInClicked = true;
                //BaseGameUtils.makeSimpleDialog(this, getString(R.string.login_error));
            }
        }
        catch (SecurityException e)
        {
            Log.d("Exception", "Security Exception Thrown");
            myApiClient.disconnect();
            myApiClient.connect();
        }
    }

    @Override
    public void onFirstGame()
    {
        Games.Achievements.unlock(myApiClient, getResources().getString(R.string.achFirstGame));
    }

    @Override
    public void onNewGame()
    {
        Games.Achievements.increment(myApiClient, getResources().getString(R.string.achSmallAddict),1);
        Games.Achievements.increment(myApiClient, getResources().getString(R.string.achOutside), 1);
    }

    @Override
    public void unlockFarynor()
    {
        Games.Achievements.unlock(myApiClient, getResources().getString(R.string.achFarynor));
    }

    @Override
    public void unlockBiggie()
    {
        Games.Achievements.increment(myApiClient, getResources().getString(R.string.achBiggie), 1);
    }

    public void buildApiClient()
    {
        myApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .setViewForPopups(findViewById(R.id.bloopy))
                .build();
    }
}

