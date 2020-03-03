package seanpai.dinnersystem

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import org.jetbrains.anko.centerInParent


class ProgressBarHandler(context: Context) {
    private var mProgressBar: ProgressBar
    private var mIndicatorView: View

    init {
        val layout = (context as Activity).findViewById<ViewGroup>(android.R.id.content).rootView as ViewGroup
        mProgressBar = ProgressBar(context, null, android.R.attr.progressBarStyle)
        mProgressBar.isIndeterminate = true
        mIndicatorView = View(context)
        mIndicatorView.setBackgroundResource(R.color.colorPrimaryDark)
        val viewParam = RelativeLayout.LayoutParams(-1, -1)
        viewParam.centerInParent()
        mIndicatorView.layoutParams = viewParam
        val layoutParams: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        val rl = RelativeLayout(context)
        rl.gravity = Gravity.CENTER
        rl.addView(mProgressBar)
        layout.addView(rl, layoutParams)
        layout.addView(mIndicatorView)
        rl.bringToFront()
        hide()
    }

    fun show(){
        mProgressBar.visibility = View.VISIBLE
        mIndicatorView.visibility = View.VISIBLE
        mProgressBar.bringToFront()
    }

    fun hide(){
        mProgressBar.visibility = View.INVISIBLE
        mIndicatorView.visibility = View.INVISIBLE
    }
}