package com.example.counter

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.content.SharedPreferences
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import androidx.core.content.ContextCompat
import android.text.style.StyleSpan
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.view.size
import android.util.Log
import java.util.concurrent.atomic.AtomicInteger
import android.view.View
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceFragmentCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
/*
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add

@Composable
fun AddIcon() {
    Icon(
        imageVector = Icons.Default.Add,
        contentDescription = "Add Icon"
    )
}*/


/*class SettingsFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
        //super.onCreateView(inflater, container, savedInstanceState)
    }
}*/
class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }
}



class MainActivity : AppCompatActivity() {
    //private var mCounter = 0
    private lateinit var add_btn: Button
    private lateinit var minus_btn: Button
    private lateinit var reset: Button
    private lateinit var txv: TextView
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var drawerLayout: DrawerLayout
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private var isSettingsPage: Boolean = false



    private var mCounter: Int
        get() = sharedPreferences.getInt("counter", 0)
        set(value) = sharedPreferences.edit().putInt("counter", value).apply()

    /*private fun saveCounterItems(counterItems: List<String>) {
        val sharedPreferences = getSharedPreferences("CounterItems", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("counterItems", HashSet(counterItems))
        editor.apply()
    }*/

    private val counterItems: MutableList<Pair<String, Int>> = mutableListOf()
    private var lastClickedCounter: Pair<String, Int> ?= null

    private fun saveLastClickedCounter(menuItem: MenuItem) {
        val sharedPreferences = getSharedPreferences("CounterItems", MODE_PRIVATE)
        val gson = Gson()
        val clickedTitle = menuItem.title.toString()
        val pattern = "(\\w+) \\((\\d+)\\)".toRegex()
        val matchResult = pattern.find(clickedTitle)
        val counterName = matchResult?.groupValues?.get(1)
        matchResult?.groupValues?.forEachIndexed { index, value ->
            Log.d("fdoijfdoij", "DEBUG LINE 61 $index: $value")
        }
        val counterValue = matchResult?.groupValues?.get(2)?.toIntOrNull()
        Log.d("fdoijfdoij", "STRING AND VALUE $counterName $counterValue")
        val lastClickedCounterJson = gson.toJson(Pair(counterName, counterValue))
        val editor = sharedPreferences.edit()
        editor.putString("lastClickedCounter", lastClickedCounterJson)
        editor.apply()
    }

    private fun loadLastClickedCounter() {
        val sharedPreferences = getSharedPreferences("CounterItems", MODE_PRIVATE)
        val lastClickedCounterJson = sharedPreferences.getString("lastClickedCounter", null)
        val gson = Gson()
        lastClickedCounter = if (lastClickedCounterJson != null) {
            gson.fromJson(lastClickedCounterJson, object: TypeToken<Pair<String, Int>?>() {}.type)
        } else {
            null
        }
    }

    private fun updateCounterItems(counterName: String) {
        val existingCounterItem = counterItems.find {
            it.first == counterName
        }
        if (existingCounterItem != null) {
            counterItems.remove(existingCounterItem)
            counterItems.add(counterName to mCounter)
        }
        saveCounterItems(counterItems)
        Log.d("", "Line 92 $counterItems")
    }

    private fun loadCounterItems() {
        val sharedPreferences = getSharedPreferences("CounterItems", MODE_PRIVATE)
        val counterItemsJson = sharedPreferences.getString("counterItems", null)
        val gson = Gson()
        val counterItemsType = object : TypeToken<MutableList<Pair<String, Int>>>() {}.type


        /*val counterItems = if (counterItemsJson != null) {
            gson.fromJson<MutableList<Pair<String, Int>>>(counterItemsJson, counterItemsType)
        } else {
            mutableListOf()
        }*/

        counterItems.clear() // Clear the existing items in the list

        if (counterItemsJson != null) {
            val loadedItems = gson.fromJson<MutableList<Pair<String, Int>>>(counterItemsJson, counterItemsType)
            counterItems.addAll(loadedItems) // Add the loaded items to the list
        }
        //return counterItems
        /*
        counterItems.clear()
        sharedPreferences.getStringSet("counterItems", emptySet())?.forEach { counter ->
            val parts = counter.split(":")
            if (parts.size == 2) {
                val counterName = parts[0]
                val counterValue = parts[1].toIntOrNull()
                if (counterValue != null) {
                    counterItems.add(counterName to counterValue)
                }
            }
        }*/
    }

    private fun createNewItem(counterName: String, counterValue: Int, navigationView: NavigationView, counterItems: MutableList<Pair<String, Int>>) {
        //val counterItems = mutableListOf<String>()
        counterItems.add(counterName to counterValue)
        val counterValueVar = AtomicInteger(counterValue)
        //val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        val menu = navigationView.menu
        val newCounterID = View.generateViewId()

        val menuItem = menu.add(R.id.group_counters, newCounterID, Menu.NONE, counterName).apply {
            isCheckable = true
            title = "$counterName ($counterValue)"
        }

        //val newCounterItem = menu.findItem(newCounterID)
        //newCounterItem
        saveCounterItems(counterItems)
        menuItem.setOnMenuItemClickListener { MenuItem ->
            //counterValueVar.incrementAndGet()
            //menuItem.title = "$counterName ($counterValue)"

            val ncounterValue: Int
            val existingCounterItem = counterItems.find {
                it.first == counterName
            }
            if (existingCounterItem != null) {
                ncounterValue = existingCounterItem.second
            }
            else {
                ncounterValue = counterValue
            }

            lastClickedCounter = counterName to ncounterValue
            Log.d("Clicked", "line 148 BLAH BLAH MenuItem: $MenuItem")
            setTitle("$counterName")
            mCounter = ncounterValue
            txv.text = mCounter.toString()
            Log.d("clicked_shit", "Changed value: $counterValue")
            saveLastClickedCounter(MenuItem)
            true
        }
    }

    private fun saveCounterItems(counterItems: MutableList<Pair<String, Int>>) {
        val sharedPreferences = getSharedPreferences("CounterItems", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val counterItemsJson = gson.toJson(counterItems)
        editor.putString("counterItems", counterItemsJson)
        editor.apply()
        Log.d("2", "counterItems_toJson: $counterItemsJson")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (isSettingsPage) {
            menuInflater.inflate(R.menu.settings_menu, menu)
        }
        else {
            menuInflater.inflate(R.menu.action_buttons, menu)}
        return true
    }

    private fun openSettingsFragment() {
        /*
        isSettingsPage = true
        invalidateOptionsMenu()
        setContentView(R.layout.fragment_settings)*/
        /*val fragment = SettingsFragment()
        val fragmentManager = supportFragmentManager
        val currentFragment = fragmentManager.findFragmentById(R.id.main_content)
        Log.d("", "line 220 $currentFragment")*/
        val fragment = SettingsFragment()
        val fragmentManager = supportFragmentManager
        val fragmentList = fragmentManager.fragments
        for (fragment in fragmentList) {
            Log.d("FragmentDebug", "Fragment: ${fragment.javaClass.simpleName}")
        }
        val fragmentListLen = fragmentList.size
        Log.d("", "Line 226 $fragmentListLen")
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)



        add_btn = findViewById(R.id.add)
        minus_btn = findViewById(R.id.minus)
        reset = findViewById(R.id.reset)

        sharedPreferences = getSharedPreferences("CounterPrefs", MODE_PRIVATE)
        txv = findViewById(R.id.num)
        drawerLayout = findViewById(R.id.my_drawer_layout)



        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        // to make the Navigation drawer icon always appear on the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        val menu = findViewById<NavigationView>(R.id.navigation_view)
        val navMenu = menu.menu


        // Below changes the text of the add counter
        val menuNavAccount = menu.menu.findItem(R.id.add_counter)
        val menuSize = menu.size
        Log.d("Counter", "The value of variable is: $menuSize")
        /* UNCOMMENT THIS
        if (menuSize == 1) {
            createNewItem("First Counter", 0, menu, counterItems)
        }*/

        loadCounterItems()
        loadLastClickedCounter()
        val defaultCounter = lastClickedCounter ?: counterItems.firstOrNull()
        val counterName = defaultCounter!!.first
        setTitle("$counterName")
        Log.i("a", "LAST CLICKED $defaultCounter")
        // UN-COMMENT THIS
        /*if (defaultCounter != null) {
            val defaultMenuItem = navMenu.findItem(defaultCounter.first.hashCode())
            defaultMenuItem?.isChecked = true
            defaultMenuItem?.itemId = R.id.first_counter
        }*/


        Log.i("HELLLO", "BEFORE FUNCTION")
        Log.d("Counter", "counterItems: $counterItems")
        counterItems.forEach { (counterName, counterValue) ->
            val newCounterId = View.generateViewId()
            Log.d("", "line 223 $newCounterId")
            navMenu.add(R.id.group_counters, newCounterId, Menu.NONE, "$counterName ($counterValue)")
                .setCheckable(true)
                .setOnMenuItemClickListener { MenuItem ->
                    val ncounterValue: Int
                    val existingCounterItem = counterItems.find {
                        it.first == counterName
                    }
                    if (existingCounterItem != null) {
                        ncounterValue = existingCounterItem.second
                    }
                    else {
                        ncounterValue = counterValue
                    }
                    setTitle("$counterName")
                    mCounter = ncounterValue
                    txv.text = mCounter.toString()

                    /*Log.d("Clicked", "BLAH BLAH MenuItem: $MenuItem")
                    val clickedTitle = MenuItem.title.toString()
                    val pattern = "\\((\\d+)\\)".toRegex()
                    val match = pattern.find(clickedTitle)
                    val value = match?.groupValues?.get(1)?.toIntOrNull()
                    if (value != null) {
                        mCounter = value
                        txv.text = mCounter.toString()
                    }
                    Log.d("clicked_shit", "Changed value: $value")*/
                    saveLastClickedCounter(MenuItem)
                    true
                }
        }


       /* menu.setNavigationItemSelectedListener { MenuItem ->i
            Log.d("Clicked", "MenuItem: $MenuItem")
            val clickedTitle = MenuItem.title.toString()
            val pattern = "\\((\\d+)\\)".toRegex()
            val match = pattern.find(clickedTitle)
            val value = match?.groupValues?.get(1)?.toIntOrNull()
            if (value != null) {
                mCounter = value
            }
            true
        }*/

        menuNavAccount.setOnMenuItemClickListener { menuItem ->
            Log.i("HELLLO", "CLICKED MENUITEM")
            // Inflate the custom layout for the dialog content
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_new_counter, null)

            // Get references to the views in the dialog layout
            val editTextCounterName = dialogView.findViewById<EditText>(R.id.editTextCounterName)
            val editTextCounterValue = dialogView.findViewById<EditText>(R.id.editTextCounterValue)
            val buttonSubmit = dialogView.findViewById<Button>(R.id.buttonSubmit)

            // Create the AlertDialog
            val dialogBuilder = AlertDialog.Builder(this)
                .setTitle("Create New Counter")
                .setView(dialogView)



            // Set click listener for the Submit button
            val dialog = dialogBuilder.show()
            buttonSubmit.setOnClickListener {
                val counterName = editTextCounterName.text.toString()
                val counterValue = editTextCounterValue.text.toString().toIntOrNull() ?: 0
                createNewItem(counterName, counterValue, menu, counterItems)


                // Dismiss the dialog
                dialog.dismiss()

            }



            true
        }


        txv.setOnClickListener {
            mCounter++
            txv.text = mCounter.toString()
            loadLastClickedCounter()
            val counterName = lastClickedCounter!!.first
            updateCounterItems(counterName)
        }

        add_btn.setOnClickListener {
            mCounter++
            txv.text = mCounter.toString()
            loadLastClickedCounter()
            val counterName = lastClickedCounter!!.first
            updateCounterItems(counterName)
        }
        minus_btn.setOnClickListener {
            mCounter--
            txv.text = mCounter.toString()
            loadLastClickedCounter()
            val counterName = lastClickedCounter!!.first
            updateCounterItems(counterName)
        }
        reset.setOnClickListener {
            mCounter = 0
            txv.text = mCounter.toString()
            loadLastClickedCounter()
            val counterName = lastClickedCounter!!.first
            updateCounterItems(counterName)
        }
        txv.text = mCounter.toString()


        // Get the menu item's title as a SpannableString
        val title = SpannableString(menuNavAccount.title)

        // Apply the color span to the "+" symbol
        val colorSpan = ForegroundColorSpan(ContextCompat.getColor(this, R.color.grey))
        title.setSpan(colorSpan, 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

        // Apply the bold style span to the "Add counter" text
        val boldStyleSpan = StyleSpan(Typeface.BOLD)
        title.setSpan(boldStyleSpan, 4, title.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

        // Set the modified SpannableString as the new title
        menuNavAccount.title = title

    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Confirm Delete")
            .setMessage("Do you want to delete the current counter?")
            .setPositiveButton("Delete") { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _  ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showEditConfirmationDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_counter, null)

        // Get references to the views in the dialog layout
        val editTextCounterValue = dialogView.findViewById<EditText>(R.id.editTextCounterValue)
        val buttonSubmit = dialogView.findViewById<Button>(R.id.buttonSubmit)

        // Create the AlertDialog
        val dialogBuilder = AlertDialog.Builder(this)
            .setTitle("Edit current counter")
            .setView(dialogView)


        // Set click listener for the Submit button
        val dialog = dialogBuilder.show()
        buttonSubmit.setOnClickListener {
            val counterValue = editTextCounterValue.text.toString().toIntOrNull() ?: 0
            mCounter = counterValue
            txv.text = mCounter.toString()
            loadLastClickedCounter()
            val counterName = lastClickedCounter!!.first
            updateCounterItems(counterName)
            // Dismiss the dialog
            dialog.dismiss()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> {
                showEditConfirmationDialog()
                true
            }
            R.id.action_delete -> {
                showDeleteConfirmationDialog()
                true
            }
            R.id.action_settings_item -> {
                openSettingsFragment()
                true
            }
            else -> {
                if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
                    true
                } else super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mCounter = mCounter
    }
}

