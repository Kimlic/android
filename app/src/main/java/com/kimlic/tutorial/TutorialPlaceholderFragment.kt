package com.kimlic.tutorial

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class TutorialPlaceholderFragment : Fragment() {

    //Variables

    private var layout: Int = 0

    // Companion

    companion object {
        fun newInstance(bundle: Bundle = Bundle(), layout: Int): TutorialPlaceholderFragment {
            val fragment = TutorialPlaceholderFragment()
            fragment.arguments = bundle
            fragment.layout = layout
            return fragment
        }
    }

    // Life

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layout, container, false)
    }

}