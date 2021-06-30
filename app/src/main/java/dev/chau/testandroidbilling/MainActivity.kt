package dev.chau.testandroidbilling

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<CardView>(R.id.cardPur).setOnClickListener { goToPur() }
        findViewById<CardView>(R.id.cardSubs).setOnClickListener { goToSub() }
    }
    private fun goToPur(){
        startActivity(Intent(this, PurchaseActivity::class.java))
    }
    private fun goToSub(){
        startActivity(Intent(this, SubscribeActivity::class.java))
    }
}