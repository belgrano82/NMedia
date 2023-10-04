package ru.netology.nmedia.util

import android.annotation.SuppressLint
import android.widget.ImageView
import com.bumptech.glide.Glide
import ru.netology.nmedia.R

@SuppressLint("CheckResult")
fun ImageView.load(url: String, isCircular: Boolean = false) {

    val glideRequest = Glide.with(this)
        .load(url)
        .timeout(10_000)
        .error(R.drawable.ic_error_100dp)
        .placeholder(R.drawable.ic_loading_100dp)

    if (isCircular) {
        glideRequest.circleCrop()
    }

    glideRequest.into(this)

}