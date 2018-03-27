package com.cokt.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.cokt.R
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.intentFor

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_asyn.setOnClickListener {
            startActivity(intentFor<AsynActivity>())
        }
    }

}
