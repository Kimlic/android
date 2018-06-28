package com.kimlic.tutorial

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.managers.PresentationManager
import com.kimlic.preferences.Prefs
import com.kimlic.terms.TermsActivity
import kotlinx.android.synthetic.main.activity_tutorial.*

class TutorialActivity : BaseActivity() {

    // Constants content

    private val TERMS_ACCEPT_REQUEST_CODE = 101

    // Variables

    private lateinit var pager: ViewPager
    private lateinit var tabLayout: TabLayout
    private var adapter: TutorialPagerAdapter = TutorialPagerAdapter(supportFragmentManager)
    private val content: List<Int> = listOf(R.layout.fragment_tutorial1, R.layout.fragment_tutorial2, R.layout.fragment_tutorial3)

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)

        setupUI()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            TERMS_ACCEPT_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    Prefs.termsAccepted = true
                    PresentationManager.phoneNumber(this@TutorialActivity)
                } else
                    PresentationManager.signupRecovery(this@TutorialActivity)
            }
        }
    }

    override fun onBackPressed() {
        if (pager.currentItem == 0)
            super.onBackPressed()
        else
            pager.setCurrentItem(pager.getCurrentItem() - 1)
    }

    // Private

    private fun setupUI() {
        pager = pagerVp
        tabLayout = tabsTL

        pager.adapter = adapter
        adapter.setContent(content)

        tabLayout.setupWithViewPager(pager, true)
        setupPageChangeListner(pager)

        skipTv.setOnClickListener { termsToAccept(TERMS_ACCEPT_REQUEST_CODE) }
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
                        //Go next activity
                        Prefs.isTutorialShown = true
                        termsToAccept(TERMS_ACCEPT_REQUEST_CODE)
                    }
                    counterPageScroll++
                } else
                    counterPageScroll = 0

                if (position == 0 && positionOffset == 0f && !isFirstPageSwiped) {
                    if (counterPageScrollL != 0) {
                        isFirstPageSwiped = true
                        //Go previous activity
                    }
                    counterPageScrollL++
                } else
                    counterPageScrollL = 0
            }
        })
    }

    private fun termsToAccept(requestCode: Int) {
        val intent = Intent(this, TermsActivity::class.java)
        intent.putExtra("action", "accept")
        intent.putExtra("content", "terms")
        startActivityForResult(intent, requestCode)
    }
}
