package com.ui.rakshakawatch

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class BlankActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_blank)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val calBtn = findViewById<Button>(R.id.calBtn)
        val demoBtn = findViewById<Button>(R.id.demoBtn)
        val ttc = findViewById<Button>(R.id.ttt)
        val demo = findViewById<Button>(R.id.demo)
        val webView = findViewById<Button>(R.id.webview)
        val mainActivity = findViewById<Button>(R.id.mainActivity)

        calBtn.setOnClickListener {
            val intent = Intent(this, CalculatorActivity::class.java)
            startActivity(intent)
        }
        demoBtn.setOnClickListener {
            val intent = Intent(this, DemoActivity::class.java)
            startActivity(intent)
        }
        ttc.setOnClickListener {
            val intent = Intent(this, TicTacToyActivity::class.java)
            startActivity(intent)
        }
        demo.setOnClickListener {
            val intent = Intent(this, DatePickerActivity::class.java)
            startActivity(intent)
        }
        webView.setOnClickListener {
            val intent = Intent(this, webViewActivity::class.java)
            startActivity(intent)
        }
        mainActivity.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}