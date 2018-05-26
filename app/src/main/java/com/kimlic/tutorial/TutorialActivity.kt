package com.kimlic.tutorial

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.widget.Toast
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.managers.PresentationManager
import com.kimlic.preferences.Prefs
import kotlinx.android.synthetic.main.activity_tutorial.*

class TutorialActivity : BaseActivity() {

    // Constants content

    private val mContent: List<String> = listOf("Tutorial 1", "Tutorial 2", "Tutorial 3", "Tutorial 4")

    // Variables

    private lateinit var mPager: ViewPager
    private lateinit var mTabLayout: TabLayout
    private var mAdapter: TutorialPagerAdapter = TutorialPagerAdapter(supportFragmentManager)

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)

        setupUI()
    }

    override fun onBackPressed() {
        if (mPager.currentItem == 0)
            super.onBackPressed()
        else
            mPager.setCurrentItem(mPager.getCurrentItem() - 1)
    }

    // Private

    private fun setupUI() {
        mPager = vpPager
        mTabLayout = tlTabs

        mPager.adapter = mAdapter
        mAdapter.setContent(mContent)

        mTabLayout.setupWithViewPager(mPager, true)
        setupPageChangeListner(mPager)
    }

    private fun setupPageChangeListner(pager: ViewPager) {
        pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            var counterPageScroll: Int = 0
            var counterPageScrollL: Int = 0
            var isLastPageSwiped: Boolean = false
            var isFirstPageSwiped: Boolean = false

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if (position == pager.adapter!!.count - 1 && positionOffset == 0f && !isLastPageSwiped) {
                    if (counterPageScroll != 0) {
                        isLastPageSwiped = true
                        Toast.makeText(applicationContext, "go to the next activity", Toast.LENGTH_LONG).show()
                        PresentationManager.loginActivity(this@TutorialActivity)
                    }
                    counterPageScroll++
                } else
                    counterPageScroll = 0

                if (position == 0 && positionOffset == 0f && !isFirstPageSwiped) {
                    if (counterPageScrollL != 0) {
                        isFirstPageSwiped = true
                        Toast.makeText(applicationContext, "go to the previous activity", Toast.LENGTH_LONG).show()
                        Prefs.isTutorialShown = true
                    }
                    counterPageScrollL++
                } else
                    counterPageScrollL = 0
            }
        })

    }
}
