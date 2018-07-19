package com.kimlic.stage

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.View
import com.kimlic.KimlicApp
import com.kimlic.R
import java.io.File

class UserPhotoView : View {

    // Variables

    private val hexPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    // private val progressPaint = Paint()
    private var path: Path? = Path()
    private var userPhotoBitmap: Bitmap? = null
    private var userPhotoBackBitmap: Bitmap? = null
    private val pathEffect = CornerPathEffect(50f)
    private var hexCanvas: Canvas? = null
    private var bitmapToShow: Bitmap? = null
    private var bitmapGradient: Bitmap? = null
    private var viewWidth = 0
    private var viewHeight = 0
    private var fileName = ""

    // Constants

    private val gradientColorStart = context.getColor(R.color.startGradient)
    private val gradientColorEnd = context.getColor(R.color.endGradient)
    private val gradientRadius = 400f
    // Aagel value of 60 degree in radians
    private val angel: Double = 1.0472

    // Constructor

    //    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
//        initialize()
//    }
//
//    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
//        initialize()
//    }
//
    constructor(context: Context, fileName: String) : super(context) {
        initialize(fileName)
        this.fileName = fileName
    }

    // Live

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measuredWidth
        setMeasuredDimension(width, (0.9 * width).toInt())

        viewWidth = measuredWidth
        viewHeight = (0.9 * width).toInt()

        bitmapGradient = blueGradient(viewWidth, viewHeight)
        // userPhotoBackBitmap = bitmapGradient//bitmapToShow // to remove
        if(getUserPhotoBitmap(fileName = fileName)!=null) bitmapToShow = getUserPhotoBitmap(fileName) else

        bitmapToShow = bitmapGradient// Removed
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        val bounds = RectF()
        path!!.addPath(hexagonPath())
        path!!.computeBounds(bounds, true)

        userPhotoBitmap = Bitmap.createBitmap(bounds.width().toInt(), bounds.height().toInt(), Bitmap.Config.ARGB_4444)

        hexCanvas = Canvas(userPhotoBitmap!!)
        hexCanvas!!.drawPath(path!!, hexPaint)

        userPhotoBackBitmap = bitmapToShow

        val q = Paint(Paint.ANTI_ALIAS_FLAG)
        setLayerType(View.LAYER_TYPE_HARDWARE, q)
        canvas.save()
        canvas.scale(1.5f,1.5f)
        canvas.drawBitmap(userPhotoBackBitmap, 0f, 0f, q)
        canvas.restore()
        q.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)

        canvas.drawBitmap(userPhotoBitmap, 0f, 0f, q)
        q.xfermode = null
    }

    // Public

//    fun showUserPhoto(fileName: String) {
//        if (File(KimlicApp.applicationContext().filesDir.toString() + "/" + fileName).exists()) {
//            this.bitmapToShow = userPhotoBitmap(fileName)
//        }
//    }
//    fun showBlueScreen() {
//        this.bitmapToShow = bitmapGradient
//        invalidate()
//    }

    //Private

    private fun blueGradient(width: Int, height: Int): Bitmap {
        val gradient = RadialGradient((width / 2).toFloat(), (height / 2).toFloat(), gradientRadius, gradientColorEnd, gradientColorStart, android.graphics.Shader.TileMode.CLAMP)
        // Linear gradient
        //val gradient = LinearGradient(0f, 0f, 0f, height.toFloat(), gradientColorStart, gradientColorEnd, android.graphics.Shader.TileMode.CLAMP)
        val p = Paint()
        p.isDither = true
        p.shader = gradient

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(bitmap)
        c.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), width.toFloat(), p)
        return bitmap
    }

    private fun initialize(fileName: String) {
        Log.d("TAGUSERPHOTO", "onInitialize!!!!!!!")
//        bitmapToShow = bitmapGradient // Test - works
//        bitmapToShow = getUserPhotoBitmap(fileName) // Test - works
//        if (File(KimlicApp.applicationContext().filesDir.toString() + "/" + fileName).exists()) {
//            bitmapToShow = blueGradient(width, height)//getUserPhotoBitmap(fileName)
//        } else bitmapToShow = blueGradient(width, height)
        hexPaint.color = Color.WHITE
        hexPaint.style = Paint.Style.FILL_AND_STROKE
        hexPaint.isAntiAlias = true
        hexPaint.pathEffect = pathEffect
    }

    private fun getUserPhotoBitmap(fileName: String): Bitmap? {
        Log.d("TAGUSERPHOTO", "userPhotoBitmap")
        val bitmap = BitmapFactory.decodeFile(KimlicApp.applicationContext().filesDir.toString() + "/" + fileName)
        //val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        //return getResizedBitmap(mutableBitmap, 1100, 800, -90f, true)
        return bitmap

    }

//    private fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int, angel: Float, isNecessaryToKeepOrig: Boolean): Bitmap {
//        val width = bm.width
//        val height = bm.height
//        val scaleWidth = newWidth.toFloat() / width
//        val scaleHeight = newHeight.toFloat() / height
//        // CREATE A MATRIX FOR THE MANIPULATION
//        val matrix = Matrix()
//        // RESIZE THE BIT MAP
//        matrix.postScale(scaleWidth, scaleHeight)
//        matrix.postRotate(angel)
//        // "RECREATE" THE NEW BITMAP
//        val resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false)
//        if (!isNecessaryToKeepOrig) {
//            bm.recycle()
//        }
//        return resizedBitmap
//    }

    private fun hexagonPath(cornerWidth: Float = 0f): Path {
        val centerX = width / 2
        val centerY = height / 2
        val step = width * 0.25
        val path = Path()
        path.reset()

        path.moveTo((centerX - step).toFloat(), cornerWidth)
        path.lineTo((centerX + step).toFloat(), cornerWidth)
        path.lineTo((width - cornerWidth).toFloat(), (centerY).toFloat())
        path.lineTo((centerX + step).toFloat(), height - cornerWidth)
        path.lineTo((centerX - step).toFloat(), height - cornerWidth)
        path.lineTo(cornerWidth, (centerY).toFloat())
        path.lineTo((centerX - step).toFloat(), cornerWidth)
        path.close()
        return path
    }
}