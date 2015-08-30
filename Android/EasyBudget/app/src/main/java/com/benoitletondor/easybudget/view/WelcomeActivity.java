package com.benoitletondor.easybudget.view;

import android.animation.Animator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewAnimationUtils;

import com.benoitletondor.easybudget.R;
import com.benoitletondor.easybudget.helper.CompatHelper;
import com.benoitletondor.easybudget.view.welcome.Onboarding1Fragment;
import com.benoitletondor.easybudget.view.welcome.Onboarding2Fragment;
import com.benoitletondor.easybudget.view.welcome.OnboardingFragment;

/**
 * Welcome screen activity
 *
 * @author Benoit LETONDOR
 */
public class WelcomeActivity extends AppCompatActivity
{
    /**
     * Value used for the {@link com.benoitletondor.easybudget.helper.ParameterKeys#ONBOARDING_STEP} when completed
     */
    public final static int STEP_COMPLETED = Integer.MAX_VALUE;

    public final static String ANIMATE_TRANSITION_KEY = "animate";
    public final static String CENTER_X_KEY = "centerX";
    public final static String CENTER_Y_KEY = "centerY";

    /**
     * Intent broadcasted by pager fragments to go next
     */
    public final static String PAGER_NEXT_INTENT     = "welcome.pager.next";
    /**
     * Intent broadcasted by pager fragments to go previous
     */
    public final static String PAGER_PREVIOUS_INTENT = "welcome.pager.previous";

// ------------------------------------------>

    /**
     * The view pager
     */
    private ViewPager         pager;
    /**
     * Broadcast receiver for intent sent by fragments
     */
    private BroadcastReceiver receiver;

// ------------------------------------------>

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        pager = (ViewPager) findViewById(R.id.welcome_view_pager);
        pager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager())
        {
            @Override
            public Fragment getItem(int position)
            {
                switch (position)
                {
                    case 0:
                        return new Onboarding1Fragment();
                    case 1:
                        return new Onboarding2Fragment();
                }

                return null;
            }

            @Override
            public int getCount()
            {
                return 2;
            }
        });
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                OnboardingFragment fragment = (OnboardingFragment) ((FragmentStatePagerAdapter) pager.getAdapter()).getItem(position);
                CompatHelper.setStatusBarColor(WelcomeActivity.this, fragment.getStatusBarColor());
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });
        pager.setOffscreenPageLimit(pager.getAdapter().getCount()); // preload all fragments for transitions smoothness

        IntentFilter filter = new IntentFilter();
        filter.addAction(PAGER_NEXT_INTENT);
        filter.addAction(PAGER_PREVIOUS_INTENT);

        receiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                if( PAGER_NEXT_INTENT.equals(intent.getAction()) && pager.getCurrentItem() < pager.getAdapter().getCount()-1 )
                {
                    if( intent.getBooleanExtra(ANIMATE_TRANSITION_KEY, false) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP )
                    {
                        // get the center for the clipping circle
                        int cx = intent.getIntExtra(CENTER_X_KEY, (int) pager.getX() + pager.getWidth() / 2);
                        int cy = intent.getIntExtra(CENTER_Y_KEY, (int) pager.getY() + pager.getHeight() / 2);

                        // get the final radius for the clipping circle
                        int finalRadius = Math.max(pager.getWidth(), pager.getHeight());

                        // create the animator for this view (the start radius is zero)
                        Animator anim = ViewAnimationUtils.createCircularReveal(pager, cx, cy, 0, finalRadius);

                        // make the view visible and start the animation
                        pager.setCurrentItem(pager.getCurrentItem() + 1, false);
                        anim.start();
                    }
                    else
                    {
                        pager.setCurrentItem(pager.getCurrentItem() + 1, true);
                    }
                }
                else if( PAGER_PREVIOUS_INTENT.equals(intent.getAction()) && pager.getCurrentItem() > 0 )
                {
                    pager.setCurrentItem(pager.getCurrentItem()-1, true);
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        // Init first status Bar color
        OnboardingFragment fragment = (OnboardingFragment) ((FragmentStatePagerAdapter) pager.getAdapter()).getItem(0);
        CompatHelper.setStatusBarColor(this, fragment.getStatusBarColor());
    }

    @Override
    protected void onDestroy()
    {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);

        super.onDestroy();
    }

    @Override
    public void onBackPressed()
    {
        if( pager.getCurrentItem() > 0 )
        {
            pager.setCurrentItem(pager.getCurrentItem()-1, true);
        }

        // Prevent back to leave activity
    }
}
