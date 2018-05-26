package com.kimlic.tutorial

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class TutorialPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    // Variables

    private var contentList: List<String> = listOf()

    // Life

    override fun getItem(position: Int): Fragment {
        val bundle = Bundle()
        bundle.putString("string", contentList[position])

        return TutorialPlaceholderFragment.newInstance(bundle)
    }

    override fun getCount() = contentList.size

    // Public

    fun setContent(content: List<String>) {
        contentList = content
        notifyDataSetChanged()
    }
}