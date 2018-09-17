package virtuzo.abhishek.community.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.location.LocationListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import virtuzo.abhishek.community.AppUtils;
import virtuzo.abhishek.community.BuildConfig;
import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.adapter.NavItem;
import virtuzo.abhishek.community.adapter.NavMenuAdapter;
import virtuzo.abhishek.community.config.UrlConfig;
import virtuzo.abhishek.community.custom.SelectGeoDialog;
import virtuzo.abhishek.community.custom.Tasks;
import virtuzo.abhishek.community.fragment.CircularListFragment;
import virtuzo.abhishek.community.fragment.CityPortalFragment;
import virtuzo.abhishek.community.fragment.EventListFragment;
import virtuzo.abhishek.community.fragment.GulakFragment;
import virtuzo.abhishek.community.fragment.MainPostFragment;
import virtuzo.abhishek.community.fragment.PrarangFragment;
import virtuzo.abhishek.community.utils.BaseUtils;
import virtuzo.abhishek.community.utils.Lang;
import virtuzo.abhishek.community.utils.Network;
import virtuzo.abhishek.community.utils.Permission;
import virtuzo.abhishek.community.utils.network.Multipart;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends LangSupportBaseActivity implements View.OnClickListener, Multipart.Listener, Tasks.Listener, LocationListener, NavMenuAdapter.Listener, Network.Listener, ViewPager.OnPageChangeListener, CityPortalFragment.OnFragmentInteractionListener {
    private AdView mAdView;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ArrayList<String> tabs = new ArrayList<>();
    private RecyclerView navRecyclerView;
    private NavMenuAdapter navMenuAdapter;
    private List<NavItem> menuList = new ArrayList<>();
    private LinearLayout spinnerLayout;
    private int[] tabIcons = {
            R.drawable.ic_tab_home,
            R.drawable.ic_tab_event_list,
            R.drawable.ic_tab_bookmark,
            R.drawable.ic_tab_circular
    };

    private SelectGeoDialog selectGeoDialog;
    private BaseUtils baseUtils;
    private AppUtils appUtils;
    private Network network;
    private Permission permission;
    private Tasks tasks;
    private Multipart multipart;

    private CircleImageView profileImage;
    private FrameLayout editProfileButton;
    private TextView userName, userCity;
    private MainPostFragment mainPostFragment;
    private EventListFragment eventListFragment;
    private CircularListFragment circularListFragment;

    private PrarangFragment prarangFragment;
    private GulakFragment gulakFragment;
    private CityPortalFragment cityPortalFragment;
    private FirebaseAnalytics firebaseAnalytics;

    private String geographyid = null;
    private LocationManager locationManager;

    private static final int INDEX_Profile = 0;
    private static final int INDEX_AboutUs = 1;
    private static final int INDEX_Messages = 2;
    private static final int INDEX_ImportantNumbers = 3;
    private static final int INDEX_OfficeBearers = 4;
    private static final int INDEX_BlockResidents = 5;
    private static final int INDEX_Help = 6;
    private static final int INDEX_Complaints = 7;
    private static final int INDEX_WebLinks = 8;
    private static final int INDEX_Map = 9;
    private static final int INDEX_ChangeLanguage = 10;
    private static final int INDEX_Panchang = 11;
    private static final int INDEX_Sync = 12;
    private static final int INDEX_Tutorials = 13;

    private void initToolbar() {

        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

//        LayoutInflater mInflater = LayoutInflater.from(this);
//        View mCustomView = mInflater.inflate(R.layout.layout_filtertoolbarview, null);
//        toolbar.addView(mCustomView, new Toolbar.LayoutParams(Gravity.CENTER_VERTICAL | Gravity.RIGHT));
//
//        mCustomView.setOnClickListener(new View.OnClickListener() {

//            @Override
//            public void onClick(View v) {
//                selectGeoDialog = new SelectGeoDialog(HomeActivity.this);
//                selectGeoDialog.show();
//            }

//        });

        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        mainPostFragment = new MainPostFragment();
//        prarangFragment = new PrarangFragment();
        eventListFragment = new EventListFragment();
        gulakFragment = new GulakFragment();
        circularListFragment = new CircularListFragment();
//        cityPortalFragment= new CityPortalFragment();
        adapter.addFrag(mainPostFragment, tabs.get(0));
        adapter.addFrag(eventListFragment, tabs.get(1));
//        adapter.addFrag(prarangFragment, tabs.get(1));
//        adapter.addFrag(cityPortalFragment, tabs.get(2));
        adapter.addFrag(gulakFragment, tabs.get(2));
        adapter.addFrag(circularListFragment, tabs.get(3));
//        adapter.addFrag(gulakFragment, tabs.get(3));



        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(this);
    }

    private void initTabLayout() {
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        for (int i = 0; i < tabs.size(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            TextView title = (TextView) view.findViewById(R.id.title);
            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            title.setText(tabs.get(i));

            icon.setImageResource(tabIcons[i]);
            tabLayout.getTabAt(i).setCustomView(view);
        }
    }

    private void initAdmobBanner() {
        mAdView = (AdView) findViewById(R.id.adView);
        mAdView.setVisibility(View.GONE);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void loadProfileData(boolean loadProfileData) {
        if (loadProfileData) {
            Glide.with(HomeActivity.this).load(appUtils.getProfileUrl()).placeholder(R.drawable.ic_userblank).dontAnimate().into(profileImage);
            userName.setText(appUtils.getName());
        }
        userCity.setText(AppUtils.getInstance(this).getNumber());
//        String[] geoNames = appUtils.getGeographyName().split(",");
//        userCity.setText(geoNames[0]);
    }

    private void initNavMenu() {
        profileImage = (CircleImageView) findViewById(R.id.profileImage);
        editProfileButton = (FrameLayout) findViewById(R.id.editProfileButton);

        profileImage.setOnClickListener(this);
        editProfileButton.setOnClickListener(this);

        userName = (TextView) findViewById(R.id.userName);
        userCity = (TextView) findViewById(R.id.userCity);
        loadProfileData(true);

        navMenuAdapter = new NavMenuAdapter(this, menuList, this);
        menuList.clear();
        navRecyclerView = (RecyclerView) findViewById(R.id.navRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        navRecyclerView.setLayoutManager(mLayoutManager);
        navRecyclerView.setItemAnimator(new DefaultItemAnimator());
        navRecyclerView.setAdapter(navMenuAdapter);

//        menuList.add(new NavItem(getResources().getString(R.string.text_menu_branches_of_organisation),null,R.drawable.branch_of_organization,0));
        menuList.add(new NavItem(getResources().getString(R.string.text_menu_profile), null, R.drawable.ic_profile, 0, 0));
        menuList.add(new NavItem(getResources().getString(R.string.text_menu_aboutus), null, R.drawable.about_us, 0, 0));
        menuList.add(new NavItem(getResources().getString(R.string.text_menu_messages), null, R.drawable.ic_messages, 0, 0));
        menuList.add(new NavItem(getResources().getString(R.string.text_menu_importantnumbers), null, R.drawable.important_number, 0, 0));
        menuList.add(new NavItem(getResources().getString(R.string.text_menu_officebearers), null, R.drawable.ic_office_bearers, 0, 0));
        menuList.add(new NavItem(getResources().getString(R.string.text_menu_blockresidents), null, R.drawable.home_black, 0, 0));
        menuList.add(new NavItem(getResources().getString(R.string.text_menu_help),null,R.drawable.ic_bell,0, 1));
        menuList.add(new NavItem(getResources().getString(R.string.text_menu_complaints), null, R.drawable.complaint, 0, 0));
        menuList.add(new NavItem(getResources().getString(R.string.text_menu_weblinks), null, R.drawable.web_link, 0, 0));
        menuList.add(new NavItem(getResources().getString(R.string.text_menu_map), null, R.drawable.ic_maps, 0, 0));
        menuList.add(new NavItem(getResources().getString(R.string.text_menu_changelang), null, R.drawable.ic_languagechange, 0, 0));
        menuList.add(new NavItem(getResources().getString(R.string.text_menu_panchang),null,R.drawable.calendar,0, 0));
        menuList.add(new NavItem(getResources().getString(R.string.text_menu_sync),null,R.drawable.sync,0, 0));
        menuList.add(new NavItem(getResources().getString(R.string.text_menu_apptour), null, R.drawable.tutorial, 0, 0));

        navMenuAdapter.notifyDataSetChanged();
    }

    private void loadActivity() {
        tabs.clear();
        tabs.add(getResources().getString(R.string.tab_home));
//        tabs.add(getResources().getString(R.string.tab_patra));
        tabs.add(getResources().getString(R.string.tab_event_list));
//        tabs.add(getResources().getString(R.string.tab_city_portals));
        tabs.add(getResources().getString(R.string.tab_bookmark));
//        tabs.add(getResources().getString(R.string.tab_gulak));
        tabs.add(getResources().getString(R.string.tab_circular_list));


        permission = new Permission(this);
        initToolbar();
        initViewPager();
        initTabLayout();
        initAdmobBanner();
        initNavMenu();

        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // default
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permission.requestPermissions(Manifest.permission.ACCESS_FINE_LOCATION, null);
        } else {
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                onLocationChanged(location);
            }
        }
        // Obtain the Firebase Analytics instance.
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString("id", appUtils.getSubscriberId());
        bundle.putString("name", appUtils.getName());
        if (appUtils.getSavedLocation() != null) {
            bundle.putString("location", appUtils.getSavedLocation());
        }
        firebaseAnalytics.logEvent("PRARANG", bundle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle(getResources().getString(R.string.title_home));
        baseUtils = new BaseUtils(this);
        appUtils = AppUtils.getInstance(this);
        network = new Network(this, this);
        tasks = new Tasks(this, this);
        multipart = new Multipart(this, this);

        //Clear data
        //baseUtils.setStringData("citypost", null);
        //baseUtils.setStringData("regionpost", null);
        //baseUtils.setStringData("countrypost", null);

        HashMap<String, String> map = new HashMap<>();
        map.put("id", getPackageName());
        network.makeRequest(map, UrlConfig.playStoreURL);

        loadActivity();

        if (!appUtils.isFirstLaunch()) {
            appUtils.setFirstLaunch(true);
//            startActivity(new Intent(this, AppTourActivity.class));
        }
    }

    private boolean shouldExit = false;
    private Handler exitHandler;
    private Runnable exitRunnable;

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (shouldExit) {
                if (exitHandler != null) {
                    exitHandler.removeCallbacks(exitRunnable);
                }
                super.onBackPressed();
            } else {
                shouldExit = true;
                Toast.makeText(this, getResources().getString(R.string.msg_exit), Toast.LENGTH_SHORT).show();
                exitHandler = new Handler();
                exitRunnable = new Runnable() {
                    @Override
                    public void run() {
                        shouldExit = false;
                    }
                };
                exitHandler.postDelayed(exitRunnable, 3000);
            }
//            if (viewPager.getCurrentItem() != 0) {
//                viewPager.setCurrentItem(0);
//            } else {
//                super.onBackPressed();
//            }
        }
    }


    @Override
    public void onClickNavMenu(View view, int position) {

        Intent intent;
        //TODO change nav
        switch (position) {
            case INDEX_Profile:
                if (baseUtils.isNetworkAvailable()) {
                    intent = new Intent(this, ProfileActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, getResources().getText(R.string.msg_nointernet), Toast.LENGTH_SHORT).show();
                }
                break;
            case INDEX_AboutUs:
                intent = new Intent(this, AboutUsActivity.class);
                startActivity(intent);
                break;
            case INDEX_Messages:
                intent = new Intent(this, MessageListActivity.class);
                startActivity(intent);
                break;
            case INDEX_ImportantNumbers:
                intent = new Intent(this, ContactCategoryActivity.class);
                ArrayList<String> CategoryList = new ArrayList<>();
                CategoryList.add(getResources().getString(R.string.text_menu_importantnumbers));

                Bundle bundle = new Bundle();
                bundle.putInt("CategoryId", 0);
                bundle.putStringArrayList("CategoryList", CategoryList);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case INDEX_OfficeBearers:
                intent = new Intent(this, OfficeBearerListActivity.class);
                startActivity(intent);
                break;
            case INDEX_BlockResidents:
                intent = new Intent(this, ResidentListActivity.class);
                startActivity(intent);

//                intent = new Intent(this, ResidentBlockListActivity.class);
//                CategoryList = new ArrayList<>();
//                CategoryList.add(getResources().getString(R.string.text_menu_blocks));
//
//                bundle = new Bundle();
//                bundle.putStringArrayList("CategoryList", CategoryList);
//                intent.putExtras(bundle);
//                startActivity(intent);
                break;
            case INDEX_Complaints:
                intent = new Intent(this, ComplaintListActivity.class);
                startActivity(intent);
                break;
            case INDEX_WebLinks:
                intent = new Intent(this, WebLinksActivity.class);
                startActivity(intent);
                break;
            case INDEX_Map:
//                intent = new Intent(this, ComingSoonActivity.class);
//                startActivity(intent);
                Uri gmmIntentUri = Uri.parse("https://goo.gl/maps/ikKXFnNgSju");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
                break;
            case INDEX_ChangeLanguage:
                intent = new Intent(this, SelectLangActivity.class);
                intent.putExtra("ChangeLanguage", true);
                startActivity(intent);
                break;
            case INDEX_Tutorials:
//                startActivity(new Intent(this, AppTourActivity.class));
                startActivity(new Intent(this, ComingSoonActivity.class));
                break;
            case INDEX_Panchang:
                startActivity(new Intent(this, PanchangActivity.class));
                break;
            case INDEX_Sync:
                intent = new Intent(this, SyncDataActivity.class);
                startActivity(intent);
                break;
            case INDEX_Help:
                intent = new Intent(this, HelpActivity.class);
                startActivity(intent);
                break;
//            case 4:
//                startActivity(new Intent(this, BranchesOfOrganisationActivity.class));
//                break;

        }
    }

    private void loadAllData() {
        if (geographyid != null) {
            AppUtils.getInstance(this).setGeoFilterId(geographyid);
        }
        navMenuAdapter.notifyDataSetChanged();
        loadProfileData(false);
        if (viewPager.getCurrentItem() == 0) {
            mainPostFragment.makeNetworkCall();
        } else {
            viewPager.setCurrentItem(0, true);
        }
//        prarangFragment.loadFragmentData(true);
        eventListFragment.loadFragmentData(true);
        circularListFragment.loadFragmentData(true);
    }

    @Override
    public void onNetworkSuccess(String response, String url) {
        if (url.indexOf(UrlConfig.playStoreURL) != -1) {
            if (response != null) {
                String key = "softwareVersion";
                int index = response.indexOf(key);
                if (index != -1) {
                    index = index + key.length() + 2;
                    String newVersion = response.substring(index, index + 8);
                    newVersion = newVersion.trim();
                    int status = baseUtils.compareVersionNames(BuildConfig.VERSION_NAME,newVersion);
                    if (status != 0) {
                        AlertDialog.Builder builder =
                                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
                        builder.setTitle("Update available");
                        builder.setMessage(getResources().getString(R.string.app_name) + " version " + newVersion + " is available on playstore.");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                baseUtils.rateApp();
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton("Later", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }
                }
            }
            return;
        }
        if (response != null) {
            try {
                response = response.substring(response.indexOf("{"), response.length());
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getString("responseCode").equals("1")) {
                    loadAllData();
                } else {
                    Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Toast.makeText(this, e.getMessage() + "", Toast.LENGTH_LONG).show();
            }
        } else {
            //Toast.makeText(this, getResources().getString(R.string.msg_common), Toast.LENGTH_LONG).show();
            loadAllData();
        }
    }

    @Override
    public void onNetworkError(String error, String url) {
        if (url.indexOf(UrlConfig.playStoreURL) != -1) {
            return;
        }
        loadAllData();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(final int position) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                switch (position) {
                    case 0:
                        mainPostFragment.makeNetworkCall();
                        break;
                    case 1:
//                        prarangFragment.loadFragmentData(true);
                        eventListFragment.loadFragmentData(true);
                        break;
                    case 2:
                        gulakFragment.loadFragmentData();
                        break;
                    case 3:
                        circularListFragment.loadFragmentData(true);
                          break;
                }
            }
        });
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onLocationChanged(Location location) {
        appUtils.setSavedLocation(location.getLatitude() + "," + location.getLongitude());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profileImage:
                //TODO
                ArrayList<String> list = new ArrayList<>();
                list.add(appUtils.getProfileUrl());
                Intent intent = new Intent(this, PostImagesActivity.class);
                intent.putStringArrayListExtra("images", list);
                startActivity(intent);
                break;
            case R.id.editProfileButton:
                if (!permission.chckSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    permission.setRequestCode(1001);
                    permission.requestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, null);
                } else {
                    openGallery();
                }
                break;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void makeReqCall(String geoIds) {
        geographyid = geoIds;
        HashMap<String, String> map = new HashMap();
        map.put("name", appUtils.getName());
        map.put("mobile", appUtils.getNumber());
        map.put("gcmKey", appUtils.getFirebaseToken());
        map.put("languageCode", new Lang(this).getAppLanguage());
        map.put("otpToBeSend", "0");
        if (geographyid == null) {
            map.put("geographyid", appUtils.getGeoFilterId());
        } else {
            map.put("geographyid", geographyid);
            AppUtils.getInstance(this).setGeoFilterId(geographyid);
        }
        network.setShowProgress(true);
        network.makeRequest(map, UrlConfig.login);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        network.cancelRequest();
    }

    boolean isFirstResume = true;

    @Override
    protected void onResume() {
        super.onResume();
        if (isFirstResume) {
            isFirstResume = false;
        } else {
            if (mainPostFragment != null) {
                //mainPostFragment.makeNetworkCall();
            }
        }
        // for sync
        if (AppUtils.getInstance(this).isSynced() == false) {
            startActivity(new Intent(HomeActivity.this, SyncDataActivity.class));
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), 1111);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //If permission is granted
        if (grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (requestCode == 1001) {
                    openGallery();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1111:
                    CropImage.activity(data.getData()).setAllowRotation(false).setAllowFlipping(false).setFixAspectRatio(true).setActivityTitle("Profile")
                            .start(this);
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    tasks.executeCompressImage(result.getUri());
                    break;
            }
        }
    }

    @Override
    public void onTaskPostExecute(Object object) {
        byte[] bytes = (byte[]) object;
        if (bytes == null) {
            Toast.makeText(this, getResources().getString(R.string.msg_common), Toast.LENGTH_SHORT).show();
        } else {
            HashMap<String, String> map = new HashMap<>();
            map.put("subscriberId", appUtils.getSubscriberId());
            multipart.setShowProgress(true);
            multipart.setMap(map);
            multipart.setUrl(UrlConfig.profilePic);
            multipart.setBytes(bytes, "profilePic");
            multipart.execute();
        }
    }

    @Override
    public void onMultipartSuccess(String response, String url) {
        try {
            Log.e("Response", response);
            JSONObject json = new JSONObject(response);
            if (json.getInt("responseCode") == 1) {
                if (json.has("Payload")) {
                    json = json.getJSONObject("Payload");
                    String profilePicUrl = json.getString("profilePicUrl");
                    appUtils.setProfileUrl(profilePicUrl);
                    Glide.with(this).load(profilePicUrl).placeholder(R.drawable.ic_userblank).dontAnimate().into(profileImage);
                } else {
                    Toast.makeText(this, "" + json.getString("message"), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "" + json.getString("message"), Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onMultipartProgress(int progress) {

    }

    @Override
    public void onMultipartError(String error, String url) {
        Toast.makeText(this, error + "", Toast.LENGTH_LONG).show();
    }


    public void onRampurClick(View view){

        Intent rampurIntent = new Intent(Intent.ACTION_VIEW);
        rampurIntent.setData(Uri.parse(getString(R.string.rampur_portal_links)));

        startActivity(rampurIntent);
    }
    public void onMeerutClick(View view){

        Intent meerutIntent = new Intent(Intent.ACTION_VIEW);
        meerutIntent.setData(Uri.parse(getString(R.string.meerut_portal_links)));
        startActivity(meerutIntent);
    }

    public void onLucknowClick(View view){

        Intent lucknowIntent = new Intent(Intent.ACTION_VIEW);
        lucknowIntent.setData(Uri.parse(getString(R.string.lucknow_portal_links)));
        startActivity(lucknowIntent);
    }

    public void onJaunpurClick(View view){

        Intent jaunpurIntent = new Intent(Intent.ACTION_VIEW);
        jaunpurIntent.setData(Uri.parse(getString(R.string.jaunpur_portal_links)));
        startActivity(jaunpurIntent);

    }


}
