package net.wildfyre.client.android

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager

import kotlinx.android.synthetic.main.activity_main.*

import net.wildfyre.api.WildFyre
import net.wildfyre.users.*
import kotlin.concurrent.thread

fun getAuthSPName()="auth"
fun getAuthSPKey()="authToken"

class MainActivity : AppCompatActivity() {


    private var me: LoggedUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        fab.setOnClickListener {}


        wildcardlist.adapter = CardAdapter()
        wildcardlist.layoutManager = LinearLayoutManager(this)
        val helper = ItemTouchHelper(CardSwipeCallback(this, wildcardlist))
        helper.attachToRecyclerView(wildcardlist)


        val sharedPreferences = getSharedPreferences(getAuthSPName(), Context.MODE_PRIVATE)
        val authToken = sharedPreferences.getString(getAuthSPKey(), "")
        if (authToken == "") {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            thread(start = true) {
                me = WildFyre.connect(authToken)
            }.join()
        }
        Log.e("WildFyre", me.toString())
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
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

}
