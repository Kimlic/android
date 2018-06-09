package com.kimlic.tutorial

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kimlic.R
import kotlinx.android.synthetic.main.fragment_tutorial_placeholder.view.*

class TutorialPlaceholderFragment : Fragment() {

    //Variables

    private var layout: Int = 0

    // Companion

    companion object {
        fun newInstance(bundle: Bundle = Bundle() , layout: Int): TutorialPlaceholderFragment {
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

    // Private

    private fun assigneContent(tempText: String, view: View) {
        view.tvContent.text = tempText
    }
}