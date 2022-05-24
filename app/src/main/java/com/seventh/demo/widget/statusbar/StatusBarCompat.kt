package com.seventh.demo.widget.statusbar

import android.annotation.TargetApi
import android.app.Activity
import android.os.Build
import androidx.annotation.ColorInt
import androidx.appcompat.widget.Toolbar
import com.seventh.demo.widget.statusbar.util.StatusBarHelper
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout

object StatusBarCompat {

    /**
     * set statusBarColor
     * @param statusColor color
     * @param alpha       0 - 255
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun setStatusBarColor(activity: Activity, statusColor: Int, alpha: Int) {
        setStatusBarColor(activity, StatusBarHelper.calculateStatusBarColor(statusColor, alpha))
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun setStatusBarColor(activity: Activity, statusColor: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StatusBarCompatLollipop.setStatusBarColor(activity, statusColor)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            StatusBarCompatKitKat.setStatusBarColor(activity, statusColor)
        }
    }

    /**
     * change to full screen mode
     *
     * @param hideStatusBarBackground hide status bar alpha Background when SDK > 21, true if hide it
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @JvmOverloads
    fun translucentStatusBar(activity: Activity, hideStatusBarBackground: Boolean = false) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StatusBarCompatLollipop.translucentStatusBar(activity, hideStatusBarBackground)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            StatusBarCompatKitKat.translucentStatusBar(activity)
        }
    }

    fun setStatusBarColorForCollapsingToolbar(
        activity: Activity,
        appBarLayout: AppBarLayout,
        collapsingToolbarLayout: CollapsingToolbarLayout,
        toolbar: Toolbar, @ColorInt statusColor: Int
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StatusBarCompatLollipop.setStatusBarColorForCollapsingToolbar(
                activity,
                appBarLayout,
                collapsingToolbarLayout,
                toolbar,
                statusColor
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            StatusBarCompatKitKat.setStatusBarColorForCollapsingToolbar(
                activity,
                appBarLayout,
                collapsingToolbarLayout,
                toolbar,
                statusColor
            )
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun setStatusBarLightMode(activity: Activity) {
        StatusBarMarshmallow.setStatusBarLightMode(activity)
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun setStatusBarDarkMode(activity: Activity) {
        StatusBarMarshmallow.setStatusBarDarkMode(activity)
    }
}
