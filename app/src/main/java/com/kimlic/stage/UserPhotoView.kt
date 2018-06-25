package com.kimlic.stage

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.kimlic.KimlicApp
import com.kimlic.R


class UserPhotoView : View {

    // Variables

    private val hexPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val progressPaint = Paint()
    private var path: Path? = Path()
    private var userPhotoBitmap: Bitmap? = null
    private var userPhotoBackBitmap: Bitmap? = null
    private val pathEffect = CornerPathEffect(35f)
    private var progress = 0
    private var hexCanvas: Canvas? = null
    private var bitmapToShow: Bitmap? = null
        set
    private var bitmapGradient: Bitmap? = null
    private var viewWidth = 0
    private var viewHeight = 0

    // Constants

    private val progressColor = context.getColor(R.color.progreesYellow)
    private val gradientCenterColor = context.getColor(R.color.lightBlue)
    private val gradientEdgeColor = context.getColor(R.color.darkBlue)
    private val gradientRadius = 300f
    private val cornerWidth = 10f
    private val strokeWidth = 30f
    // Aagel value of 60 degree in radians
    private val angel: Double = 1.0472

    // Constructor

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize()
    }

    constructor(context: Context) : super(context) {
        initialize()
    }

    // Live

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measuredWidth
        setMeasuredDimension(width, (0.9 * width).toInt())

        viewWidth = measuredWidth
        viewHeight = (0.9 * width).toInt()

        bitmapGradient = blueGradient(viewWidth, viewHeight)
        bitmapToShow = bitmapGradient
    }


    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        val bounds = RectF()
        path!!.addPath(hexagonPath())
        path!!.computeBounds(bounds, true)

        userPhotoBitmap = Bitmap.createBitmap(bounds.width().toInt(), bounds.height().toInt(), Bitmap.Config.ARGB_8888)

        hexCanvas = Canvas(userPhotoBitmap!!)
        hexCanvas!!.drawPath(path!!, hexPaint)

        userPhotoBackBitmap = bitmapToShow

        val q = Paint(Paint.ANTI_ALIAS_FLAG)
        setLayerType(View.LAYER_TYPE_HARDWARE, q)
        canvas.drawBitmap(userPhotoBackBitmap, 0f, 0f, q)
        q.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)

        canvas.drawBitmap(userPhotoBitmap, 0f, 0f, q)
        q.xfermode = null
        q.color = Color.WHITE
        q.strokeWidth = strokeWidth
        q.style = Paint.Style.STROKE
        q.pathEffect = pathEffect

        canvas.drawPath(hexagonPath(cornerWidth), q)
        showProgress(canvas, progressPaint, progress)
    }

    // Public

    fun setProgress(progress: Int = 0) {
        this.progress = progress
        invalidate()
    }

    fun showUserPhoto(fileName: String) {
        this.bitmapToShow = userPhotoBitmap(fileName)
        invalidate()
    }

    fun showBlueScreen() {
        this.bitmapToShow = bitmapGradient
        invalidate()
    }

    //Private

    private fun blueGradient(width: Int, height: Int): Bitmap {
        val gradient = RadialGradient((width / 2).toFloat(), (height / 2).toFloat(), gradientRadius, gradientCenterColor, gradientEdgeColor, android.graphics.Shader.TileMode.CLAMP)
        val p = Paint()
        p.isDither = true
        p.shader = gradient

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(bitmap)
        c.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), width.toFloat(), p)
        return bitmap
    }

    private fun showProgress(canvas: Canvas, paint: Paint, progress: Int) {
        val centerX = width / 2
        val centerY = height / 2
        val step = width * 0.25
        val path = Path()
        path.reset()

        if (progress >= 17) {
            path.moveTo((centerX - step + 75).toFloat(), cornerWidth)
            path.lineTo((centerX + step - 75).toFloat(), cornerWidth)
            path.lineTo((centerX + step).toFloat(), cornerWidth)
            path.lineTo((centerX + step + 45).toFloat(), (cornerWidth + 45) * Math.tan(angel).toFloat())
        }

        if (progress >= 35) {
            path.lineTo(width - cornerWidth, (centerY).toFloat())
            path.lineTo(width - cornerWidth - 45, centerY + 48 * Math.tan(angel).toFloat())

        }

        if (progress >= 50) {
            path.lineTo((centerX + step).toFloat(), height - cornerWidth)
            path.lineTo((centerX + step - 75).toFloat(), height - cornerWidth)
        }

        if (progress >= 66) {
            path.lineTo((centerX - step).toFloat(), height - cornerWidth); path.lineTo((centerX - step - 45).toFloat(), height - cornerWidth - 48 * Math.tan(angel).toFloat())
        }

        if (progress >= 83) {
            path.lineTo(cornerWidth, (centerY).toFloat()); path.lineTo(cornerWidth + 45, centerY - 48 * Math.tan(angel).toFloat())
        }

        if (progress >= 100) {
            path.lineTo((centerX - step).toFloat(), cornerWidth); path.close()
        }

        canvas.drawPath(path, paint)
    }

    private fun initialize() {
        hexPaint.color = Color.BLUE
        hexPaint.style = Paint.Style.FILL_AND_STROKE
        hexPaint.isAntiAlias = true
        hexPaint.pathEffect = pathEffect

        progressPaint.isAntiAlias = true
        progressPaint.style = Paint.Style.STROKE
        progressPaint.strokeWidth = strokeWidth
        progressPaint.color = progressColor
        progressPaint.pathEffect = pathEffect
    }

    private fun userPhotoBitmap(fileName: String): Bitmap {
        val bitmap = BitmapFactory.decodeFile(KimlicApp.applicationContext().filesDir.toString() + "/" + fileName)
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        return getResizedBitmap(mutableBitmap, 800, 600, -90f, true)
    }

    private fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int, angel: Float, isNecessaryToKeepOrig: Boolean): Bitmap {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)
        matrix.postRotate(angel)
        // "RECREATE" THE NEW BITMAP
        val resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false)
        if (!isNecessaryToKeepOrig) {
            bm.recycle()
        }
        return resizedBitmap
    }

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