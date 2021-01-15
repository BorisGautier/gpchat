package cm.tbg.gpchat.activities.main

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import cm.tbg.gpchat.R
import cm.tbg.gpchat.activities.*
import cm.tbg.gpchat.activities.settings.SettingsActivity
import cm.tbg.gpchat.adapters.ViewPagerAdapter
import cm.tbg.gpchat.common.ViewModelFactory
import cm.tbg.gpchat.common.extensions.findFragmentByTagForViewPager
import cm.tbg.gpchat.events.ExitUpdateActivityEvent
import cm.tbg.gpchat.fragments.BaseFragment
import cm.tbg.gpchat.interfaces.FragmentCallback
import cm.tbg.gpchat.interfaces.StatusFragmentCallbacks
import cm.tbg.gpchat.job.DailyBackupJob
import cm.tbg.gpchat.job.SaveTokenJob
import cm.tbg.gpchat.job.SetLastSeenJob
import cm.tbg.gpchat.model.realms.User
import cm.tbg.gpchat.services.*
import cm.tbg.gpchat.utils.*
import cm.tbg.gpchat.utils.network.FireManager
import cm.tbg.gpchat.views.dialogs.IgnoreBatteryDialog
import com.bumptech.glide.Glide
import com.droidninja.imageeditengine.ImageEditor
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.vanniktech.emoji.EmojiTextView
import de.hdodenhof.circleimageview.CircleImageView
import org.greenrobot.eventbus.EventBus
import java.util.*


class MainActivity : BaseActivity(), FabRotationAnimation.RotateAnimationListener, FragmentCallback, StatusFragmentCallbacks, NavigationView.OnNavigationItemSelectedListener {
    private var isInSearchMode = false

    private lateinit var fab: FloatingActionButton
    private lateinit var textStatusFab: FloatingActionButton

    private lateinit var toolbar: Toolbar
    private lateinit var tvSelectedChatCount: TextView
    private lateinit var searchView: SearchView
    private lateinit var viewPager: ViewPager
   // private lateinit var tabLayout: TabLayout
    private lateinit var meowBottomNavigation: MeowBottomNavigation

    var toggle: ActionBarDrawerToggle? = null



    var nameNav: EmojiTextView? = null
    var statueNav:EmojiTextView? = null

    var avatarNav: CircleImageView? = null
    var editBNav: Button? = null
    var hlal: ImageView? = null

    private var users: List<User>? = null
    private var user : User? = null
    private var fireListener: FireListener? = null
    private var adapter: ViewPagerAdapter? = null
    private lateinit var rotationAnimation: FabRotationAnimation

    var root: DrawerLayout? = null

    private var currentPage = 0

    private lateinit var viewModel: MainViewModel

    private var ignoreBatteryDialog: IgnoreBatteryDialog? = null



    override fun enablePresence(): Boolean {
        return true
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()

        setSupportActionBar(toolbar)

        toggle = ActionBarDrawerToggle(
                this, root, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)



        root!!.addDrawerListener(toggle!!)
        toggle!!.syncState()

        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        if (shared()!!.getBoolean("dark" + uid, false)) {
            hlal?.setImageDrawable(resources.getDrawable(R.drawable.hlal_fill))

            //store choice
         //   AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)


        } else {
            hlal?.setImageDrawable(resources.getDrawable(R.drawable.hlal))
            //store choice
          //  AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        viewModel = ViewModelProvider(this, ViewModelFactory(this.application)).get(MainViewModel::class.java)




        rotationAnimation = FabRotationAnimation(this)

        fireListener = FireListener()
        startServices()


        users = RealmHelper.getInstance().listOfUsers
        user = RealmHelper.getInstance().getUser(uid)


        //dark mode
        hlal?.setOnClickListener {
            val isNightTheme = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            when (isNightTheme) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    hlal?.setImageDrawable(resources.getDrawable(R.drawable.hlal))
                    editSharePrefs()!!.putBoolean("dark" + uid, false)
                    editSharePrefs()!!.apply()
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }

                Configuration.UI_MODE_NIGHT_NO -> {
                    hlal?.setImageDrawable(resources.getDrawable(R.drawable.hlal_fill))
                    //store choice
                    editSharePrefs()!!.putBoolean("dark" + uid, true)
                    editSharePrefs()!!.apply()
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }

            }

        }

        nameNav?.setText(user?.properUserName)
        statueNav?.setText(user?.status)


        avatarNav?.let {
            Glide.with(applicationContext).load(user?.photo)
                    .placeholder(AppCompatResources.getDrawable(applicationContext, R.drawable.user_img_wrapped))
                    .into(it)
        }

      /*  avatarNav?.let {
            Glide.with(this).load(user?.photo)
                    .placeholder(AppCompatResources.getDrawable(this, R.drawable.user_img_wrapped))
                    .into(it)
        }*/




        fab.setOnClickListener {
            when (currentPage) {
                0 -> startActivity(Intent(this@MainActivity, NewChatActivity::class.java))
                1 -> startCamera()

                2 -> startActivity(Intent(this@MainActivity, NewCallActivity::class.java))
            }
        }

        textStatusFab.setOnClickListener { startActivityForResult(Intent(this, TextStatusActivity::class.java), REQUEST_CODE_TEXT_STATUS) }


        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            //onSwipe or tab change
            override fun onPageSelected(position: Int) {
                currentPage = position
                if (isInSearchMode)
                    exitSearchMode()

                when (position) {


                    //add margin to fab when tab is changed only if ads are shown
                    //animate fab with rotation animation also
                    0 -> {
                        getFragmentByPosition(0)?.let { fragment ->
                            val baseFragment = fragment as BaseFragment
                            addMarginToFab(false)
                        }

                        animateFab(R.drawable.ic_chat)
                    }
                    1 -> {
                        getFragmentByPosition(1)?.let { fragment ->
                            val baseFragment = fragment as BaseFragment
                            addMarginToFab(false)
                        }
                        animateFab(R.drawable.ic_photo_camera)
                    }

                    else -> {

                        getFragmentByPosition(2)?.let { fragment ->
                            val baseFragment = fragment as BaseFragment
                            addMarginToFab(false)
                        }
                        animateFab(R.drawable.ic_phone)
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {


            }
        })

        //revert status fab to starting position
        textStatusFab.addOnHideAnimationListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
                textStatusFab.animate().y(fab.y).start()

            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        })
        //save app ver if it's not saved before
        if (!SharedPreferencesManager.isAppVersionSaved()) {
            FireConstants.usersRef.child(FireManager.uid).child("ver").setValue(AppVerUtil.getAppVersion(this)).addOnSuccessListener { SharedPreferencesManager.setAppVersionSaved(true) }
        }

        if (!SharedPreferencesManager.isSinchConfigured()) {
            val serviceIntent = Intent(this, CallingService::class.java)
            serviceIntent.putExtra(IntentUtils.START_SINCH, true)
            startService(serviceIntent)
        }




        if (!SharedPreferencesManager.hasAgreedToPrivacyPolicy()) {
            showPrivacyAlertDialog()
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            try {
                val pkg = packageName
                val pm = getSystemService(PowerManager::class.java)
                if (!pm.isIgnoringBatteryOptimizations(pkg) && !SharedPreferencesManager.isDoNotShowBatteryOptimizationAgain()) {
                  //  showBatteryOptimizationDialog()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        viewModel.deleteOldMessagesIfNeeded()
        viewModel.checkForUpdate().subscribe({ needsUpdate ->
            if (needsUpdate) {
                startUpdateActivity()
            } else {
                EventBus.getDefault().post(ExitUpdateActivityEvent())
            }
        }, {

        })


    }

    override fun goingToUpdateActivity() {
        ignoreBatteryDialog?.dismiss()
        super.goingToUpdateActivity()
    }




    //for users who updated the app
    private fun showPrivacyAlertDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setPositiveButton(R.string.agree_and_continue) { dialog, which ->
            SharedPreferencesManager.setAgreedToPrivacyPolicy(true)
        }

        alertDialog.setNegativeButton(R.string.cancel) { dialog, which ->
            finish()
        }

        alertDialog.show()
    }

   /* private fun showBatteryOptimizationDialog() {

        ignoreBatteryDialog = IgnoreBatteryDialog(this)
        ignoreBatteryDialog?.setOnDialogClickListener(object : IgnoreBatteryDialog.OnDialogClickListener {

            override fun onCancelClick(checkBoxChecked: Boolean) {
                SharedPreferencesManager.setDoNotShowBatteryOptimizationAgain(checkBoxChecked)
            }

            override fun onOk() {
                try {
                    val intent = Intent()
                    intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "could not open Battery Optimization Settings", Toast.LENGTH_SHORT).show();
                }

            }

        })
        ignoreBatteryDialog?.show()
    }*/


    //start CameraActivity
    private fun startCamera() {

        val intent = Intent(this, CameraActivity::class.java)
        intent.putExtra(IntentUtils.CAMERA_VIEW_SHOW_PICK_IMAGE_BUTTON, true)
        intent.putExtra(IntentUtils.IS_STATUS, true)
        startActivityForResult(intent, CAMERA_REQUEST)


    }

    //animate FAB with rotation animation
    @SuppressLint("RestrictedApi")
    private fun animateFab(drawable: Int) {
        val animation = rotationAnimation.start(drawable)
        fab.startAnimation(animation)
    }

    private fun animateTextStatusFab() {
        val show = viewPager.currentItem == 1
        if (show) {
            textStatusFab.show()
            textStatusFab.animate().y(fab.top - DpUtil.toPixel(70f, this)).start()
        } else {
            textStatusFab.hide()
            textStatusFab.layoutParams = fab.layoutParams
        }
    }


    override fun fetchStatuses() {
        users?.let {
            viewModel.fetchStatuses(it)
        }
    }


    private fun startServices() {
        if (!Util.isOreoOrAbove()) {
            startService(Intent(this, NetworkService::class.java))
            startService(Intent(this, InternetConnectedListener::class.java))
            startService(Intent(this, FCMRegistrationService::class.java))

        } else {
            if (!SharedPreferencesManager.isTokenSaved())
                SaveTokenJob.schedule(this, null)

            SetLastSeenJob.schedule(this)
            UnProcessedJobs.process(this)
        }

        //sync contacts for the first time
        if (!SharedPreferencesManager.isContactSynced()) {
            syncContacts()
        } else {
            //sync contacts every day if needed
            if (SharedPreferencesManager.needsSyncContacts()) {
                syncContacts()
            }
        }

        //schedule daily job to backup messages
        DailyBackupJob.schedule()


    }

    private fun syncContacts() {
        disposables.add(ContactUtils.syncContacts().subscribe({

        }, { throwable ->

        }))
    }


    private fun init() {
        fab = findViewById(R.id.open_new_chat_fab)
        toolbar = findViewById(R.id.toolbar)
        tvSelectedChatCount = findViewById(R.id.tv_selected_chat)
        viewPager = findViewById(R.id.view_pager)
      //  tabLayout = findViewById(R.id.tab_layout)
        meowBottomNavigation = findViewById(R.id.meownav)
        textStatusFab = findViewById(R.id.text_status_fab)
        root = findViewById(R.id.root)



        ///////////Navigation Inflate/////////////
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)
        val hView = navigationView.getHeaderView(0)

        nameNav = hView.findViewById(R.id.nameNav)
        statueNav = hView.findViewById(R.id.statueNav)
        avatarNav = hView.findViewById(R.id.profimgNav)
        hlal = hView.findViewById<ImageView>(R.id.hlal)



        initTabLayout()

        //prefix for a bug in older APIs
        fab.bringToFront()
    }





    private fun initTabLayout() {






        meowBottomNavigation.add(MeowBottomNavigation.Model(0, R.drawable.ic_chat))
        meowBottomNavigation.add(MeowBottomNavigation.Model(1, R.drawable.ic_baseline_amp_stories_24))
        meowBottomNavigation.add(MeowBottomNavigation.Model(2, R.drawable.ic_phone))
        meowBottomNavigation.show(0, true)


      //  tabLayout.setupWithViewPager(viewPager)
        adapter = ViewPagerAdapter(supportFragmentManager, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 1
     //   setTabsTitles(3)

        viewPager.setCurrentItem(0)
        viewPager.addOnPageChangeListener(MainActivity.PageChange(meowBottomNavigation))

        meowBottomNavigation.setOnClickMenuListener { model: MeowBottomNavigation.Model ->
            viewPager.setCurrentItem(model.id)
            null
        }
    }

    class PageChange(val meowBottomNavigation: MeowBottomNavigation) : OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
        override fun onPageSelected(position: Int) {
            when (position) {
                0 -> meowBottomNavigation.show(0, true)
                1 -> meowBottomNavigation.show(1, true)
                2 -> meowBottomNavigation.show(2, true)
            }
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val drawer = findViewById<View>(R.id.root) as DrawerLayout
        // Handle navigation view item clicks here.
        val id = item.itemId
         if (id == R.id.nav_setting) {
            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
        } else if (id == R.id.qr) {
             Toast.makeText(this@MainActivity, "Bientot Disponible", Toast.LENGTH_SHORT).show();
           // startActivity(Intent(this@MainActivity, Qr::class.java))
        }  else if (id == R.id.channel) {
             Toast.makeText(this@MainActivity, "Bientot Disponible", Toast.LENGTH_SHORT).show();
             // startActivity(Intent(this@MainActivity, Qr::class.java))
         } else if (id == android.R.id.home) {
            drawer.openDrawer(Gravity.LEFT);
         }
         /* else if (id == R.id.nav_signout) {
            FirebaseAuth.getInstance().signOut()
             startActivity(Intent(this@MainActivity, AuthenticationActivity::class.java))
        }*/


        drawer.closeDrawer(GravityCompat.START)
        return true
    }




    override fun onPause() {
        super.onPause()
        ignoreBatteryDialog?.dismiss()
        fireListener?.cleanup()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val menuItem = menu.findItem(R.id.search_item)
        searchView = menuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {

                return false
            }

            //submit search for the current active fragment
            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.onQueryTextChange(newText)
                return false
            }

        })
        //revert back to original adapter
        searchView.setOnCloseListener {
            exitSearchMode()
            true
        }

        menuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(menuItem: MenuItem): Boolean {
                return true
            }

            //exit search mode on searchClosed
            override fun onMenuItemActionCollapse(menuItem: MenuItem): Boolean {
                exitSearchMode()
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.settings_item -> settingsItemClicked()

            R.id.search_item -> searchItemClicked()

            R.id.new_group_item -> createGroupClicked()


            R.id.invite_item -> startActivity(IntentUtils.getShareAppIntent(this@MainActivity))

            R.id.new_broadcast_item -> {
                val intent = Intent(this@MainActivity, NewGroupActivity::class.java)
                intent.putExtra(IntentUtils.IS_BROADCAST, true)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }


    private fun createGroupClicked() {
        startActivity(Intent(this, NewGroupActivity::class.java))
    }

    private fun searchItemClicked() {
        isInSearchMode = true
    }


    private fun settingsItemClicked() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }


    override fun onBackPressed() {
        val drawer = findViewById<View>(R.id.root) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
        if (isInSearchMode)
            exitSearchMode()
        else {
            if (viewPager.currentItem != CHATS_TAB_INDEX) {
                viewPager.setCurrentItem(CHATS_TAB_INDEX, true)
            } else {
                super.onBackPressed()
            }
        }

    }


    fun exitSearchMode() {
        isInSearchMode = false
    }


   /* private fun setTabsTitles(tabsSize: Int) {
        for (i in 0 until tabsSize) {
            when (i) {

                0 -> tabLayout.getTabAt(i)?.setText(R.string.chats)

                1 -> tabLayout.getTabAt(i)?.setText(R.string.status)

                2 -> tabLayout.getTabAt(i)?.setText(R.string.calls)
            }
        }

    }*/


    override fun onRotationAnimationEnd(drawable: Int) {
        fab?.setImageResource(drawable)
        animateTextStatusFab()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST || requestCode == ImageEditor.RC_IMAGE_EDITOR || requestCode == REQUEST_CODE_TEXT_STATUS) {
            viewModel.onActivityResult(requestCode, resultCode, data)

        }

    }


    override fun addMarginToFab(isAdShowing: Boolean) {
        val layoutParams = fab.layoutParams as CoordinatorLayout.LayoutParams
        val v = if (isAdShowing) DpUtil.toPixel(95f, this) else resources.getDimensionPixelSize(R.dimen.fab_margin).toFloat()


        layoutParams.bottomMargin = v.toInt()

        fab.layoutParams = layoutParams

        fab.clearAnimation()
        fab.animation?.cancel()

        animateTextStatusFab()

    }


    override fun openCamera() {
        startCamera()
    }

    override fun startTheActionMode(callback: ActionMode.Callback) {
        startActionMode(callback)
    }

    private fun getFragmentByPosition(position: Int): Fragment? {
        return viewPager.currentItem?.let { supportFragmentManager.findFragmentByTagForViewPager(position, it) }
    }


    companion object {
        const val CAMERA_REQUEST = 9514
        const val REQUEST_CODE_TEXT_STATUS = 9145
        private const val CHATS_TAB_INDEX = 0

    }


}