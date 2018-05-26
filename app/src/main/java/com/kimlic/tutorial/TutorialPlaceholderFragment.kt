package com.kimlic.tutorial

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kimlic.R
import kotlinx.android.synthetic.main.fragment_tutorial_placeholder.view.*

class TutorialPlaceholderFragment : Fragment() {

    // Companion

    companion object {
        fun newInstance(bundle: Bundle?): TutorialPlaceholderFragment {
            val fragment = TutorialPlaceholderFragment()
            fragment.arguments = bundle

            return fragment
        }
    }

    // Life

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val content: String = arguments!!.getString("string")
        val fragment = inflater.inflate(R.layout.fragment_tutorial_placeholder, container, false)
        assigneContent(content, fragment)
        return fragment
    }

    // Private

    private fun assigneContent(tempText: String, view: View) {
        view.tvContent.text = tempText
    }
}