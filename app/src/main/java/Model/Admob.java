package Model;

import android.annotation.SuppressLint;
import android.content.Context;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.itnic.thanhson.readbooktuthulanhdaothuatxuthe.R;

/**
 * Created by ThanhSon on 3/16/2018.
 */

public class Admob extends DatabaseBook {
    private InterstitialAd mInterstitialAd;

    @SuppressLint("MissingPermission")
    public void startAdmob(Context context)
    {
        MobileAds.initialize(this, getString(R.string.admob_app_id));
        mInterstitialAd = newInterstitialAd(context);
        loadInterstitial();
    }

    private InterstitialAd newInterstitialAd(final Context context) {
        InterstitialAd interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.admob_id));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

            }

            @Override
            public void onAdClosed() {
                loadInterstitial();
            }
        });
        return interstitialAd;
    }

    public boolean showInterstitial() {
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
            return true;
        } else {
            return false;
            //Toast.makeText(this, "You need connect to the internet!"+(mInterstitialAd == null? "null":"!null"), Toast.LENGTH_LONG).show();
        }
    }

    private void loadInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        mInterstitialAd.loadAd(adRequest);
    }
}
