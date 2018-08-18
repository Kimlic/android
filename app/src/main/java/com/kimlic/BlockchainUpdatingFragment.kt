package com.kimlic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.kimlic.utils.AppDuration
import kotlinx.android.synthetic.main.fragment_blockchain_updating.*
import java.lang.ref.WeakReference

class BlockchainUpdatingFragment : BasePopupFragment() {

    // Variables

    private lateinit var blockchainWeakReference: WeakReference<ImageView>
    private var yoyoFadeIn: YoYo.YoYoString? = null
    private var yoyoFadeOut: YoYo.YoYoString? = null

    // Companion

    companion object {

        val FRAGMENT_KEY = this::class.java.simpleName

        fun newInstance(bundle: Bundle? = Bundle()): BlockchainUpdatingFragment {
            val fragment = BlockchainUpdatingFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    // Life

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_blockchain_updating, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        isCancelable = false
        blockchainWeakReference = WeakReference(blockchainIv)
        playAnimation()
    }

    override fun dismiss() {
        yoyoFadeIn!!.stop()
        yoyoFadeIn = null
        yoyoFadeOut!!.stop()
        yoyoFadeOut = null
        super.dismiss()
    }

    private fun playAnimation() {
        yoyoFadeIn = YoYo
                .with(Techniques.FadeOut)
                .duration(AppDuration.BLOCKCHAIN_DURATION.duration)
                .onEnd { secondAnimation() }
                .playOn(blockchainWeakReference.get())
    }

    private fun secondAnimation() {
        yoyoFadeOut = YoYo
                .with(Techniques.FadeIn)
                .duration(AppDuration.BLOCKCHAIN_DURATION.duration)
                .onEnd { playAnimation() }
                .playOn(blockchainWeakReference.get())
    }
}