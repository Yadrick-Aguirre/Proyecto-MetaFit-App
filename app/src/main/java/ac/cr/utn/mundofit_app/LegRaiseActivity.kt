package ac.cr.utn.mundofit_app

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class LegRaiseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_leg_raise)

        findViewById<ImageView>(R.id.gifLegRaise).setImageResource(R.drawable.leg_raise)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
    }
}