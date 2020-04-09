package me.vinachiong.sample.datepopuppager

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        date_popup_pager.initDateModel("201605", "201905", "201801", true)
        date_popup_pager.addOnDateSelectedChangedListener{
            Snackbar.make(date_popup_pager, "选中${it.label()}", Snackbar.LENGTH_LONG)
                .show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_switch_year -> {
                date_popup_pager.switchToYearMode()
                true
            }
            R.id.action_switch_month -> {
                date_popup_pager.switchToMonthMode("201801")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
