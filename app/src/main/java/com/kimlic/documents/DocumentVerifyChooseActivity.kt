package com.kimlic.documents

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import butterknife.BindViews
import butterknife.ButterKnife
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.db.entity.Document
import com.kimlic.db.entity.User
import com.kimlic.managers.PresentationManager
import com.kimlic.model.ProfileViewModel
import com.kimlic.utils.mappers.FileNameTxtBase64ToBitmap
import kotlinx.android.synthetic.main.activity_verify_document.*

class DocumentVerifyChooseActivity : BaseActivity(), View.OnClickListener {

    // Binding

    @BindViews(R.id.firstBt, R.id.secondBt, R.id.thirdBt, R.id.fourthBt, R.id.fifthBt)
    lateinit var buttonsList: List<@JvmSuppressWildcards Button>

    // Variables

    private lateinit var types: MutableMap<String, String>
    private lateinit var model: ProfileViewModel

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_document)
        ButterKnife.bind(this)

        setupUI()
    }

    override fun onClick(v: View?) {
        when (v!!.tag) {
            "passport" -> PresentationManager.verifyPassport(this@DocumentVerifyChooseActivity)
            "id" -> PresentationManager.verifyIDCard(this@DocumentVerifyChooseActivity)
            "license" -> PresentationManager.verifyDriverLicence(this@DocumentVerifyChooseActivity)
            "permit" -> PresentationManager.verifyPermit(this@DocumentVerifyChooseActivity)
        }
    }

    // Private

    private fun setupUI() {
        model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        model.getUserLive().observe(this, object : Observer<User> {
            override fun onChanged(user: User?) {
                setupBackground(user!!.portraitFile)
            }
        })

        model.getUserDocumentsLive().observe(this, object : Observer<List<Document>> {
            override fun onChanged(documents: List<Document>?) {
                types = mutableMapOf(
                        Pair("passport", getString(R.string.passport)),
                        Pair("id", getString(R.string.id_card)),
                        Pair("license", getString(R.string.drivers_license)),
                        Pair("permit", getString(R.string.life_permit)))

                documents!!.forEach { types.remove(it.type) }
                buttonsList.forEach { it.visibility = View.GONE }

                when (types.size) {
                    1 -> {
                        with(buttonsList) {
                            last().text = types.entries.elementAt(0).value; last().tag = types.entries.elementAt(0).key; last().visibility = View.VISIBLE
                        }
                    }
                    2 -> {
                        with(buttonsList) {
                            first().text = types.entries.elementAt(0).value; first().tag = types.entries.elementAt(0).key; first().visibility = View.VISIBLE
                            elementAt(3).text = types.entries.elementAt(1).value; elementAt(3).tag = types.entries.elementAt(0).key; elementAt(3).visibility = View.VISIBLE
                        }
                    }
                    3 -> {
                        with(buttonsList) {
                            first().text = types.entries.elementAt(0).value; first().tag = types.entries.elementAt(0).key; first().visibility = View.VISIBLE
                            elementAt(1).text = types.entries.elementAt(1).value; elementAt(1).tag = types.entries.elementAt(1).key; elementAt(1).visibility = View.VISIBLE
                            elementAt(3).text = types.entries.elementAt(2).value; elementAt(3).tag = types.entries.elementAt(2).key; elementAt(3).visibility = View.VISIBLE
                        }
                    }
                    4 -> {
                        with(buttonsList) {
                            first().text = types.entries.elementAt(0).value; first().tag = types.entries.elementAt(0).key; first().visibility = View.VISIBLE
                            elementAt(1).text = types.entries.elementAt(1).value; elementAt(1).tag = types.entries.elementAt(1).key; elementAt(1).visibility = View.VISIBLE
                            elementAt(2).text = types.entries.elementAt(2).value; elementAt(2).tag = types.entries.elementAt(2).key; elementAt(2).visibility = View.VISIBLE
                            elementAt(3).text = types.entries.elementAt(3).value; elementAt(3).tag = types.entries.elementAt(3).key; elementAt(3).visibility = View.VISIBLE
                        }
                    }
                }
            }
        })
        buttonsList.forEach { it.setOnClickListener(this@DocumentVerifyChooseActivity) }
        backBt.setOnClickListener { finish() }
    }

    // Private helpers

    private fun setupBackground(fileName: String) {
        val bitmap = FileNameTxtBase64ToBitmap().transform(fileName)
        bitmap?.let { rootIv.setImageBitmap(croped(bitmap)); rootIv.scaleType = ImageView.ScaleType.CENTER_CROP }
    }

    private fun croped(bitmap: Bitmap): Bitmap {
        val rotated = rotateBitmap(bitmap, -90f)
        val width = rotated.width
        val height = rotated.height
        val bitmapCroped = Bitmap.createBitmap(rotated, (0.25 * width).toInt(), (0.1 * height).toInt(), (0.5 * width).toInt(), (0.7 * height).toInt())
        return bitmapCroped
    }

}