package com.itnic.thanhson.readbooktuthulanhdaothuatxuthe;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import Model.Admob;

public class MainActivity extends Admob
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView txtNameBook, txtAuthor, txtContent;
    ScrollView scrollView;
    ImageView imgJacketBook;

    NavigationView navigationView;
    String[][] _Category;
    boolean isFullScreen = false;
    int idSelected = 0;
    Toolbar toolbar;
    SearchView searchView;
    CountDownTimer autoScroll;
    int timerScroll = 15;
    boolean isScrollToBottom = false;
    boolean isStartingAutoScroll = false;

    SharedPref sharedpref;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.println(Log.ERROR,"debug", "1");
        setContentView(R.layout.activity_main);
        Log.println(Log.ERROR,"debug", "2");
        sharedpref = new SharedPref(this);
        if(sharedpref.loadNightModeState()==true) {
            setTheme(R.style.darktheme);
        }
        else
            setTheme(R.style.AppTheme);
        Log.println(Log.ERROR,"debug","3");
        setContentView(R.layout.activity_main);
        Log.println(Log.ERROR,"debug","4");
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        addControls();
        addEvents();

        copyDatabaseFromAccessToSystem();
        DatabaseBook();

        loadContent();

        startAdmob(MainActivity.this);
        readCountinues();
    }

    private void readCountinues() {
        if (getReading() > 0) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (sharedpref.loadNightModeState() == true)
                        builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                    else
                        builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(MainActivity.this);
                }
                builder.setTitle(getName())
                        .setMessage(_Category[getReading()][0])
                        .setPositiveButton("Đọc tiếp", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                scrollView.setScrollY(getReadingLine());
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
        }

    }

    private void AutoScroll(final int secondStep, final int second) {
        autoScroll = new CountDownTimer(second, secondStep) {
            @Override
            public void onTick(long l) {
                startScroll(1000, 2000);
            }

            @Override
            public void onFinish() {
                if (!isScrollToBottom)
                    StartAutoScroll();
            }
        };
    }

    private void StartAutoScroll() {
        autoScroll.start();
        isStartingAutoScroll = true;
    }

    private void StopAutoScroll() {
        if (isStartingAutoScroll)
            autoScroll.cancel();
        isStartingAutoScroll = false;
    }

    @Override
    protected void onResume() {
        loadConfig();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (isFullScreen || isStartingAutoScroll) {
            full_Screen(false);//close full screen
            StopAutoScroll();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        saveReading();
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            saveReading();
        return super.onKeyDown(keyCode, event);
    }

    private void saveReading() {
        setReadingLine(scrollView.getScrollY());
        setReading(idSelected);
    }

    private void loadConfig() {
        readUser();
        String gads[][] = getCategory();
        toolbar.setTitle(gads[getReading()][1] + "-" + gads[getReading()][0]);
        txtContent.setText(readContent(getReading()));

        timerScroll = getTimerScroll();
        StopAutoScroll();
        AutoScroll(timerScroll * 1000, 180000);
//        theme(getThemeId());
        txtContent.setTextSize(getTextSize() < 5 ? 5 : getTextSize());
    }

    private void loadContent() {
        txtNameBook.setText(getName());
        txtAuthor.setText(getAuthor());
        imgJacketBook.setImageBitmap(getJacket());
        txtContent.setFocusable(false);

        Menu menu = navigationView.getMenu();
        _Category = getCategory();
        for (int i = 0; i < _Category.length; i++) {
            menu.add(R.id.nav_seemore, Menu.NONE, 0, i + "- " + _Category[i][0]);
        }

        //txtContent.setText(readContent(getReading()));
        idSelected = getReading();
    }

    private void addControls() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        txtNameBook = navigationView.getHeaderView(0).findViewById(R.id.txtNameBook);
        txtAuthor = navigationView.getHeaderView(0).findViewById(R.id.txtAuthor);
        imgJacketBook = navigationView.getHeaderView(0).findViewById(R.id.imgJacket);
        scrollView = findViewById(R.id.scrollViewContent);

        txtContent = findViewById(R.id.txtContent);

        //toolbarHeader = findViewById(R.id.toolbar);
    }

    int countAdmob = 0;

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void addEvents() {
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                if (scrollView.getChildAt(0).getBottom() <= (scrollView.getHeight() + scrollView.getScrollY())) {
                    isScrollToBottom = true;
                    StopAutoScroll();
                } else {
                    if (isSeeAdmod() && i1 >= 50000 && countAdmob < 1)
                        if (showInterstitial())
                            countAdmob++;
                    isScrollToBottom = false;
                }
            }
        });

        txtContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (isStartingAutoScroll) {
                    StopAutoScroll();
                    Toast.makeText(MainActivity.this, "Đã tắt tự động cuộn trang", Toast.LENGTH_SHORT).show();
                } else {
                    StartAutoScroll();
                    Toast.makeText(MainActivity.this, "Đã bật tự động cuộn trang", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }

    int idsearching = 0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);


        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.btnSearch));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                idSelected = idsearching;
                saveReading();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    if (newText == "")
                        return true;
                    int id = getIdWordSearch(newText)[0];
                    if (id >= 0 && id != idsearching) {
                        txtContent.setText(readContent(id));
                        idsearching = id;
                        toolbar.setTitle(_Category[id][0]);
                    }
                    setScrollViewY(newText);
                } catch (Exception ex) {

                }
                return true;
            }
        });

        searchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {
                saveReading();
            }

            @Override
            public void onViewDetachedFromWindow(View view) {
                txtContent.setText(readContent(getReading()));
                scrollView.setScrollY(getReadingLine());
                toolbar.setTitle(_Category[idSelected][0]);
            }
        });
        return true;
    }

    private void setScrollViewY(String textToFind) {
        final int index = txtContent.getText().toString().indexOf(textToFind);
        int line = txtContent.getLayout().getLineForOffset(index);
        int y = txtContent.getLayout().getLineTop(line);
        scrollView.scrollTo(0, y);
    }

    private void startScroll(final int step, final int time) {
        if (!isScrollToBottom) {
            CountDownTimer countDownTimerScroll = new CountDownTimer(time, 20) {
                @Override
                public void onTick(long l) {
                    scrollView.scrollTo(0, scrollView.getScrollY() + step / (time / 20));
                }

                @Override
                public void onFinish() {

                }
            };
            countDownTimerScroll.start();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_fullscreen) {
            full_Screen(true);
            return true;
        } else if (id == R.id.action_autoscroll) {
            StartAutoScroll();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void full_Screen(boolean isFull) {
        ActionBar actionBar = getSupportActionBar();
        if (isFull) {
            if (actionBar != null)
                actionBar.hide();

            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            isFullScreen = true;
        } else {
            if (actionBar != null)
                actionBar.show();
            isFullScreen = false;
        }
    }

    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        String itemTitle = item.getTitle().toString();
        if (item.getItemId() == R.id.nav_vote) {
            launchMarket();
        } else if (item.getItemId() == R.id.nav_seemore) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:Phan Thanh Sơn")));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/developer?id=Phan+Thanh+Sơn")));
            }
        } else if (item.getItemId() == R.id.nav_fb) {
            Intent intent;
            try {
                ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo("com.facebook.katana", 0);
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + getPageFB()));
            } catch (PackageManager.NameNotFoundException e) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + getPageFB()));
            }
            startActivity(intent);
        } else {
            scrollView.fullScroll(View.FOCUS_UP);
            int id = Integer.parseInt(itemTitle.split("-")[0]);
            idSelected = id;
            toolbar.setTitle(itemTitle);
            txtContent.setText(readContent(id));
            setReading(id);
            if(isSeeAdmod())
                showInterstitial();
            countAdmob = 0;
        }
// com.itnic.thanhson.readbooktuthulanhdaothuatxuthe
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
