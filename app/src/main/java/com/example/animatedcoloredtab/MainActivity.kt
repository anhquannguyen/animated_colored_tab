package com.example.animatedcoloredtab

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.doOnLayout
import androidx.viewpager.widget.ViewPager
import com.example.animatedcoloredtab.databinding.ActivityMainBinding
import com.example.animatedcoloredtab.ui.main.SectionsPagerAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import kotlin.math.hypot


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isOpen = false

    companion object {
        private val TAG = MainActivity::class.simpleName

        private val TAB_COLORS =
            listOf(
                R.color.purple_500,
                R.color.yellow,
                R.color.teal_700,
                R.color.red,
                R.color.blue
            )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        initToolbarDimension()

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)
        val fab: FloatingActionButton = binding.fab

        val pagesCount = viewPager.adapter?.count!!
        var tabWidth = 0f
        var centerY = 0f

        tabs.doOnLayout {
            tabWidth = tabs.measuredWidth / pagesCount * 1f
        }
        viewPager.doOnLayout {
            centerY =
                binding.appbar.measuredHeight - (tabs.y - tabs.measuredHeight) / 2
        }

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                val centerX: Float = tabWidth * (position + 1) - tabWidth / 2

                reveal(position, centerX, centerY)
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })

        fab.setOnClickListener {
            fabClick()
        }

    }

    private fun initToolbarDimension() {
        val params = binding.toolbar.layoutParams as ConstraintLayout.LayoutParams
        params.setMargins(0, getStatusBarHeight(), 0, 0)
        binding.toolbar.layoutParams = params
    }

    private fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    private fun reveal(i: Int, rawX: Float, rawY: Float) {
        binding.revealView.visibility = View.VISIBLE
        val revealViewX: Int = binding.revealView.width
        val revealViewY: Int = binding.revealView.height

        val radius = revealViewX.coerceAtLeast(revealViewY) * 1.2f
        val reveal = ViewAnimationUtils
            .createCircularReveal(binding.revealView, rawX.toInt(), rawY.toInt(), 0f, radius)

        reveal.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animator: Animator) {
                val resource: Int = TAB_COLORS[i]
                setBackgroundColor(binding.backgroundView, resource)
                binding.revealView.visibility = View.INVISIBLE
            }
        })

        val resource: Int = TAB_COLORS[i]
        setBackgroundColor(binding.revealView, resource)
        reveal.start()
    }

    private fun setBackgroundColor(view: View, resId: Int) {
        val color = ResourcesCompat.getColor(
            resources, resId,
            theme
        )
        view.setBackgroundColor(color)
    }

    private fun fabClick() {
        val layoutContent = binding.viewPager
        val layoutButtons = binding.layoutButton

        isOpen = if (!isOpen) {
            val x: Int = layoutContent.right
            val y: Int = layoutContent.bottom
            val startRadius = 0
            val endRadius =
                hypot(layoutContent.width.toDouble(), layoutContent.height.toDouble()).toInt()

            val reveal = ViewAnimationUtils.createCircularReveal(
                layoutButtons,
                x,
                y,
                startRadius.toFloat(),
                endRadius.toFloat()
            )
            layoutButtons.visibility = View.VISIBLE
            reveal.start()
            true
        } else {
            val x: Int = layoutButtons.right
            val y: Int = layoutButtons.bottom
            val startRadius: Int = layoutContent.width.coerceAtLeast(layoutContent.height)
            val endRadius = 0

            val reveal = ViewAnimationUtils.createCircularReveal(
                layoutButtons,
                x,
                y,
                startRadius.toFloat(),
                endRadius.toFloat()
            )
            reveal.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {}
                override fun onAnimationEnd(animator: Animator) {
                    layoutButtons.visibility = View.GONE
                }

                override fun onAnimationCancel(animator: Animator) {}
                override fun onAnimationRepeat(animator: Animator) {}
            })
            reveal.start()
            false
        }
    }
}