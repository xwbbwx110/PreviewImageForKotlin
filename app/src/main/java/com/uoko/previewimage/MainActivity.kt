package com.uoko.previewimage

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Glide.with(this).load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542628722793&di=9978fdca750c9bd4403f96e483d1e13c&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F010865567d063c32f8759f04e301e3.jpg").into(img1)
        Glide.with(this).load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542628722793&di=02bb96fb88d94c67859f488f079fca8a&imgtype=0&src=http%3A%2F%2Fi2.hdslb.com%2Fbfs%2Farchive%2F61179f0786aa8e5b4d180856a2340e361b80ab78.jpg").into(img2)
        Glide.with(this).load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1543484543465&di=727b742b863aab9e210b63270e9af683&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F018f9e554285cc0000019ae9b4094b.jpg%402o.jpg").into(img3)

    img1.setOnClickListener {

        startPhotoActivity(this,img1,0)
    }


        img2.setOnClickListener {
            startPhotoActivity(this,img2,1)

        }



        img3.setOnClickListener {
            startPhotoActivity(this,img3,2)

        }

    }


    fun startPhotoActivity(context: Context, imageView: ImageView,index:Int = 0) {


        val imagelist = arrayListOf<String>(
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542628722793&di=9978fdca750c9bd4403f96e483d1e13c&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F010865567d063c32f8759f04e301e3.jpg"
        ,"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542628722793&di=02bb96fb88d94c67859f488f079fca8a&imgtype=0&src=http%3A%2F%2Fi2.hdslb.com%2Fbfs%2Farchive%2F61179f0786aa8e5b4d180856a2340e361b80ab78.jpg"
                ,"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1543484543465&di=727b742b863aab9e210b63270e9af683&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F018f9e554285cc0000019ae9b4094b.jpg%402o.jpg")


        DragPhotoActivity.previewImage(this,imageView,imagelist,"看图标题",index)

    }
}
