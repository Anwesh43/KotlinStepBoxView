package com.example.anweshmishra.kotlinstepboxview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.stepboxview.StepBoxView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StepBoxView.create(this)
    }
}
