package com.kimlic.verification

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import butterknife.BindViews
import butterknife.ButterKnife
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.db.entity.Document
import com.kimlic.managers.PresentationManager
import com.kimlic.preferences.Prefs
import kotlinx.android.synthetic.main.activity_verify_document.*
import java.io.File

class DocumentVerifyChooseActivity : BaseActivity(), View.OnClickListener {

    // Binding

    @BindViews(R.id.firstBt, R.id.secondBt, R.id.thirdBt, R.id.fourthBt, R.id.fifthBt)
    lateinit var buttonsList: List<@JvmSuppressWildcards Button>

    // Variables

    private lateinit var model: DocumentVerifyChooseViewModel
    private lateinit var types: MutableMap<String, String>

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_document)
        ButterKnife.bind(this)

        setupUI()
    }

    override fun onClick(v: View?) {
        when (v!!.tag) {
            "passport" -> PresentationManager.verifyPassport(this@DocumentVerifyChooseActivity, Prefs.currentId)
            "id" -> PresentationManager.verifyIDCard(this@DocumentVerifyChooseActivity, Prefs.currentId)
            "license" -> PresentationManager.verifyDriverLicence(this@DocumentVerifyChooseActivity, Prefs.currentId)
            "permit" -> PresentationManager.verifyPermit(this@DocumentVerifyChooseActivity, Prefs.currentId)
        }
    }

    // Private

    private fun setupUI() {
        types = mutableMapOf<String, String>(Pair("passport", this.getString(R.string.passport)), Pair("id", this.getString(R.string.id_card)), Pair("license", this.getString(R.string.drivers_license)), Pair("permit", "Life Permit"))
        model = ViewModelProviders.of(this).get(DocumentVerifyChooseViewModel::class.java)
        //setupBackground(model.getUser().portraitFile)

        model.getDocumentsLiveData().observe(this, object : Observer<List<Document>> {
            override fun onChanged(documents: List<Document>?) {
                documents!!.forEach { types.remove(it.type) }

                Log.d("TAGDOCLIST", documents.toString())
                // Log.d("TAGDOCLIST", documents.toString())

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
        val filePath = this.applicationContext.filesDir.toString() + "/" + fileName//UserPhotos.portraitFilePath.fileName
        val file = File(filePath)

        rootIv.scaleType = ImageView.ScaleType.CENTER_CROP
        if (file.exists()) rootIv.setImageBitmap(croped(fileName))
    }

    private fun croped(fileName: String): Bitmap {
        val bitmap = BitmapFactory.decodeFile(this.applicationContext.filesDir.toString() + "/" + fileName)
        val rotated = rotateBitmap(bitmap, -90f)
        val width = rotated.width
        val height = rotated.height
        val bitmapCroped = Bitmap.createBitmap(rotated, (0.25 * width).toInt(), (0.1 * height).toInt(), (0.5 * width).toInt(), (0.7 * height).toInt())
        return bitmapCroped
    }

    private fun rotateBitmap(source: Bitmap, angel: Float): Bitmap {
        val matrix = Matrix(); matrix.postRotate(angel)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

}