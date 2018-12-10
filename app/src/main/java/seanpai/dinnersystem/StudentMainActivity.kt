package seanpai.dinnersystem

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_student_main.*
import kotlinx.android.synthetic.main.fragment_order.*

class StudentMainActivity : AppCompatActivity() {
    val manager = supportFragmentManager
    val transaction = manager.beginTransaction()
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        when (item.itemId) {
            R.id.navigation_order -> {
                setTitle(R.string.stu_title_order)
                transaction.replace(R.id.container, order_frag).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_history -> {
                setTitle(R.string.stu_title_history)

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_more -> {
                setTitle(R.string.stu_title_more)

                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_main)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

    }






}
