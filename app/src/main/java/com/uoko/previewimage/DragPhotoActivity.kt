package com.uoko.previewimage


import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.ImageView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_drag_photo.*

class DragPhotoActivity : AppCompatActivity() ,ViewPager.OnPageChangeListener{
    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        img_count.text = "${position+1}/${mPhotoViews.size}"
    }

    lateinit var mPhotoViews: MutableList<DragImageView>

    internal var mOriginLeft: Int = 0
    internal var mOriginTop: Int = 0
    internal var mOriginHeight: Int = 0
    internal var mOriginWidth: Int = 0
    internal var mOriginCenterX: Int = 0
    internal var mOriginCenterY: Int = 0
    private var mTargetHeight: Float = 0.toFloat()
    private var mTargetWidth: Float = 0.toFloat()
    private var mIndex = 0
    private var mScaleX: Float = 0.toFloat()
    private var mScaleY: Float = 0.toFloat()
    private var mTranslationX: Float = 0.toFloat()
    private var mTranslationY: Float = 0.toFloat()


    companion object {
        const val IMAG_PATH_LIST = "uoko_image_path_list" //图片路径集合
        const val IMAGE_LIST_TITLE = "uoko_image_path_title" //图片标题集合
        const val LEFT = "left"
        const val TOP = "top"
        const val HEIGHT = "height"
        const val WIDTH = "width"
        const val INDEX = "index"

        fun previewImage(ac: Activity, imag:ImageView, arrayList: ArrayList<String>, title:String,index:Int){
            val location = IntArray(2)
            imag.getLocationOnScreen(location)
            val intent = Intent(ac, DragPhotoActivity::class.java)
            intent.putExtra(DragPhotoActivity.LEFT, location[0])
            intent.putExtra(DragPhotoActivity.TOP, location[1])
            intent.putExtra(DragPhotoActivity.HEIGHT, imag.height)
            intent.putExtra(DragPhotoActivity.WIDTH, imag.width)
            intent.putStringArrayListExtra(DragPhotoActivity.IMAG_PATH_LIST,arrayList)
            intent.putExtra(DragPhotoActivity.IMAGE_LIST_TITLE,title)
            intent.putExtra(DragPhotoActivity.INDEX,index)
            ac.startActivity(intent)
            ac.overridePendingTransition(0, 0)
        }


    }

    private  var mImagelist:List<String>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_drag_photo)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = resources.getColor(R.color.colorPrimary)
        }


        if(intent.hasExtra(IMAG_PATH_LIST)){
            mImagelist = intent.getStringArrayListExtra(IMAG_PATH_LIST)
        }

        if(intent.hasExtra(IMAGE_LIST_TITLE)){
            title_txt.text = intent.getStringExtra(IMAGE_LIST_TITLE)
        }
        if(intent.hasExtra(IMAGE_LIST_TITLE))
        mIndex = intent.getIntExtra(INDEX,0)


        mPhotoViews = mutableListOf()



        mImagelist?.let {

            for (i in 0 until it.size) {
                mPhotoViews.add(DragImageView(this))
                Glide.with(this).load(it[i]).into( mPhotoViews[i])
                mPhotoViews[i].setOnExitListener { view, translateX, translateY, w, h ->
                    finish()
                    overridePendingTransition(0, 0)
                }

            }

        }

        img_count.text = "${mIndex+1}/${mPhotoViews.size}"

        viewpager?.adapter = object : PagerAdapter() {
            override fun getCount(): Int {
                return mImagelist?.size?:0
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                container.addView(mPhotoViews[position])
                return mPhotoViews[position]
            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                container.removeView(mPhotoViews[position])
            }

            override fun isViewFromObject(view: View, `object`: Any): Boolean {
                return view === `object`
            }
        }
        viewpager?.currentItem = mIndex
        viewpager?.addOnPageChangeListener(this)


        viewpager?.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        viewpager!!.viewTreeObserver.removeOnGlobalLayoutListener(this)

                        mOriginLeft = intent.getIntExtra(LEFT, 0)
                        mOriginTop = intent.getIntExtra(TOP, 0)
                        mOriginHeight = intent.getIntExtra(HEIGHT, 0)
                        mOriginWidth = intent.getIntExtra(WIDTH, 0)

                        mOriginCenterX = mOriginLeft + mOriginWidth / 2
                        mOriginCenterY = mOriginTop + mOriginHeight / 2

                        val location = IntArray(2)

                        val photoView = mPhotoViews[mIndex]
                        photoView.getLocationOnScreen(location)

                        mTargetHeight = photoView.height.toFloat()
                        mTargetWidth = photoView.width.toFloat()
                        mScaleX = mOriginWidth.toFloat() / mTargetWidth
                        mScaleY = mOriginHeight.toFloat() / mTargetHeight


                        val targetCenterX = location[0] + mTargetWidth / 2
                        val targetCenterY = location[1] + mTargetHeight / 2

                        mTranslationX = mOriginCenterX - targetCenterX
                        mTranslationY = mOriginCenterY - targetCenterY
                        photoView.translationX = mTranslationX
                        photoView.translationY = mTranslationY

                        photoView.scaleX = mScaleX
                        photoView.scaleY = mScaleY

                        enterAnimation()
                    }
                })
    }

    private fun enterAnimation() {
        val photoView = mPhotoViews[mIndex]
        val translateXAnimator = ValueAnimator.ofFloat(photoView.getX(), 0.0f)
        translateXAnimator.addUpdateListener { valueAnimator -> photoView.x = valueAnimator.animatedValue as Float }
        translateXAnimator.duration = 300
        translateXAnimator.start()

        val translateYAnimator = ValueAnimator.ofFloat(photoView.getY(), 0.0f)
        translateYAnimator.addUpdateListener { valueAnimator -> photoView.y = valueAnimator.animatedValue as Float }
        translateYAnimator.duration = 300
        translateYAnimator.start()

        val scaleYAnimator = ValueAnimator.ofFloat(mScaleY, 1.0f)
        scaleYAnimator.addUpdateListener { valueAnimator -> photoView.scaleY = valueAnimator.animatedValue as Float }
        scaleYAnimator.duration = 300
        scaleYAnimator.start()

        val scaleXAnimator = ValueAnimator.ofFloat(mScaleX, 1.0f)
        scaleXAnimator.addUpdateListener { valueAnimator -> photoView.scaleX = valueAnimator.animatedValue as Float }
        scaleXAnimator.duration = 300
        scaleXAnimator.start()
    }




    override fun onBackPressed() {
    }
}
