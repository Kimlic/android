package com.kimlic.tutorial

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class TutorialPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    // Variables

    private var contentList: List<Int> = listOf()

    // Life

    override fun getItem(position: Int): Fragment {
        val bundle = Bundle()
        return TutorialPlaceholderFragment.newInstance(bundle, contentList[position])
    }

    override fun getCount() = contentList.size

    // Public

    fun setContent(content: List<Int>) {
        contentList = content
        notifyDataSetChanged()
    }
}