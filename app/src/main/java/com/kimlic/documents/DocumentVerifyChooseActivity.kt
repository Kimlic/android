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
import com.kimlic.utils.AppDoc
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

        model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        ButterKnife.bind(this)
        setupUI()
    }

    override fun onClick(v: View?) = PresentationManager.verifyDocument(this, v!!.tag.toString())

    // Private

    private fun setupUI() {
        model.userLive().observe(this, Observer<User> { user -> setupBackground(user!!.portraitFile) })

        model.userDocumentsLive().observe(this, Observer<List<Document>> { documents ->
            types = mutableMapOf(
                    Pair(AppDoc.PASSPORT.type, getString(R.string.passport)),
                    Pair(AppDoc.ID_CARD.type, getString(R.string.id_card)),
                    Pair(AppDoc.DRIVERS_LICENSE.type, getString(R.string.drivers_license)),
                    Pair(AppDoc.RESIDENCE_PERMIT_CARD.type, getString(R.string.life_permit)))

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
                0 -> finish()
            }
        })
        buttonsList.forEach { it.setOnClickListener(this@DocumentVerifyChooseActivity) }
        backBt.setOnClickListener { finish() }
    }

    // Private helpers

    private fun setupBackground(fileName: String) {
        val bitmap = FileNameTxtBase64ToBitmap().transform(fileName)
        bitmap?.let { rootIv.setImageBitmap(cropped(bitmap)); rootIv.scaleType = ImageView.ScaleType.CENTER_CROP }
    }

    private fun cropped(bitmap: Bitmap): Bitmap {
        val rotated = rotateBitmap(bitmap, -90f)
        val width = rotated.width
        val height = rotated.height
        return Bitmap.createBitmap(rotated, (0.25 * width).toInt(), (0.1 * height).toInt(), (0.5 * width).toInt(), (0.7 * height).toInt())
    }
}