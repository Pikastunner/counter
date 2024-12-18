package com.example.counter

import android.content.Context
import android.content.Intent
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
import android.os.PersistableBundle
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

class SettingsActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager
            .beginTransaction()
            .replace(android.R.id.content, SettingsFragment())
            .commit()
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



    private var mCounter: Int
        get() = sharedPreferences.getInt("counter", 0)
        set(value) = sharedPreferences.edit().putInt("counter", value).apply()

    /*private fun saveCounterItems(counterItems: List<String>) {
        val sharedPreferences = getSharedPreferences("CounterItems", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("counterItems", HashSet(counterItems))
        editor.apply()
    }*/

    private val counterItems: MutableList<Counter> = mutableListOf()
    private var lastClickedCounter: Int ?= null

//    private fun saveLastClickedCounter(counter: Counter) {
//        val sharedPreferences = getSharedPreferences("CounterItems", MODE_PRIVATE)
//        val gson = Gson()
////        val clickedTitle = menuItem.title.toString()
////        val pattern = "(\\w+) \\((\\d+)\\)".toRegex()
////        val matchResult = pattern.find(clickedTitle)
////        val counterName = matchResult?.groupValues?.get(1)
////        matchResult?.groupValues?.forEachIndexed { index, value ->
////            Log.d("fdoijfdoij", "DEBUG LINE 61 $index: $value")
////        }
////        val counterValue = matchResult?.groupValues?.get(2)?.toIntOrNull()
////        Log.d("fdoijfdoij", "STRING AND VALUE $counterName $counterValue")
//        val lastClickedCounterJson = gson.toJson(counter)
//        val editor = sharedPreferences.edit()
//        editor.putString("lastClickedCounter", lastClickedCounterJson)
//        editor.apply()
//    }

    private fun saveLastClickedCounter(counter: Counter) {
        val sharedPreferences = getSharedPreferences("CounterItems", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        Log.i("fjdiosjf", "FUCKING COUNTER ID" + counter)
        editor.putInt("lastClickedCounterID", counter.counterID)
        editor.apply()
    }

    private fun loadLastClickedCounter() {
        val sharedPreferences = getSharedPreferences("CounterItems", MODE_PRIVATE)
        val savedCounterID = sharedPreferences.getInt("lastClickedCounterID", -1)

        // If there's a saved ID, find the corresponding Counter object
        lastClickedCounter = if (savedCounterID != -1) {
            val counter = counterItems.find { it.counterID == savedCounterID }
            lastClickedCounter = counter!!.counterID
            savedCounterID
        } else {
            // Set a default if no saved ID found
            lastClickedCounter = counterItems.first()!!.counterID
            lastClickedCounter
        }

        Log.d("Loaded Counter", "Counter loaded: $lastClickedCounter")
    }

    private fun updateCounterItems(counterID: Int) {
        val existingCounterItem = counterItems.find {
            it.counterID == counterID
        }
        if (existingCounterItem != null) {
//            counterItems.remove(existingCounterItem)
//            counterItems.add(counterName to mCounter)
            existingCounterItem.counterValue = mCounter
        }
        saveCounterItems(counterItems)

        val updateAction = "com.example.counter.UPDATE_ACTION"

        val updateWidgetsIntent = Intent(applicationContext, CounterWidgetProvider::class.java).apply {
            action = updateAction
            putExtra("counterId", counterID)
        }
        applicationContext.sendBroadcast(updateWidgetsIntent)

        Log.d("", "Line 92 $counterItems")
    }

    private fun loadCounterItems() {
        val sharedPreferences = getSharedPreferences("CounterItems", MODE_PRIVATE)
        val counterItemsJson = sharedPreferences.getString("counterItems", null)
        val gson = Gson()
        val counterItemsType = object : TypeToken<List<Counter>>() {}.type


        /*val counterItems = if (counterItemsJson != null) {
            gson.fromJson<MutableList<Pair<String, Int>>>(counterItemsJson, counterItemsType)
        } else {
            mutableListOf()
        }*/

        counterItems.clear() // Clear the existing items in the list

        if (counterItemsJson != null) {
            val loadedItems = gson.fromJson<List<Counter>>(counterItemsJson, counterItemsType)
            counterItems.addAll(loadedItems) // Add the loaded items to the list
        }
        else {
            val counterId = System.currentTimeMillis().toInt()
            counterItems.add(Counter("First Counter", counterId, 0))
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

    private fun createNewItem(counterName: String, counterValue: Int, navigationView: NavigationView, counterItems: MutableList<Counter>) {
        //val counterItems = mutableListOf<String>()
        val counterId = System.currentTimeMillis().toInt()
        counterItems.add(Counter(counterName, counterId, counterValue))
        val counterValueVar = AtomicInteger(counterValue)
        //val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        val menu = navigationView.menu
        //val newCounterID = View.generateViewId()

        val menuItem = menu.add(R.id.group_counters, counterId, Menu.NONE, counterName).apply {
            isCheckable = true
//            title = "$counterName ($counterValue)"
            title = counterName
        }

        //val newCounterItem = menu.findItem(newCounterID)
        //newCounterItem
        saveCounterItems(counterItems)
        menuItem.setOnMenuItemClickListener { MenuItem ->
            //counterValueVar.incrementAndGet()
            //menuItem.title = "$counterName ($counterValue)"

            val ncounterValue: Int
            val existingCounterItem = counterItems.find {
                it.counterID == menuItem.itemId
            }
            if (existingCounterItem != null) {
                ncounterValue = existingCounterItem.counterValue
            }
            else {
                ncounterValue = counterValue
            }

            if (existingCounterItem != null) {
                lastClickedCounter = existingCounterItem.counterID
            }//counterName to ncounterValue
            Log.d("Clicked", "line 148 BLAH BLAH MenuItem: $MenuItem")
            setTitle("$counterName")
            mCounter = ncounterValue
            txv.text = mCounter.toString()
            Log.d("clicked_shit", "Changed value: $counterValue")
            if (existingCounterItem != null) {
                saveLastClickedCounter(existingCounterItem)
            }
            true
        }
    }


    data class Counter(
        val counterName: String,
        val counterID: Int,
        var counterValue: Int,
    )

    private fun saveCounterItems(counterItems: MutableList<Counter>) {
        val sharedPreferences = getSharedPreferences("CounterItems", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val counterItemsJson = gson.toJson(counterItems)
        editor.putString("counterItems", counterItemsJson)
        editor.apply()
        Log.d("2", "counterItems_toJson: $counterItemsJson")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        if (isSettingsPage) {
////            menuInflater.inflate(R.menu.settings_menu, menu)
//        }
//        else {
        menuInflater.inflate(R.menu.action_buttons, menu)
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
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)



        add_btn = findViewById(R.id.add)
        minus_btn = findViewById(R.id.minus)
        reset = findViewById(R.id.reset)

        sharedPreferences = getSharedPreferences("CounterItems", MODE_PRIVATE)
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


        val defaultCounter = counterItems.find { it.counterID == lastClickedCounter }
        val counterName = defaultCounter!!.counterName
        mCounter = defaultCounter.counterValue
        txv.text = mCounter.toString()


        title = "$counterName"
        Log.i("a", "LAST CLICKED  iufhdsiuhfiudhs $defaultCounter")
        Log.d("ALL ", "hello" + counterItems.toString())
        // UN-COMMENT THIS
        /*if (defaultCounter != null) {
            val defaultMenuItem = navMenu.findItem(defaultCounter.first.hashCode())
            defaultMenuItem?.isChecked = true
            defaultMenuItem?.itemId = R.id.first_counter
        }*/


        Log.i("HELLLO", "BEFORE FUNCTION")
        Log.d("Counter", "counterItems: $counterItems")
        counterItems.forEach { counter ->
            val newCounterId = counter.counterID//View.generateViewId()
            Log.d("", "line 223 $newCounterId")
            navMenu.add(R.id.group_counters, newCounterId, Menu.NONE, counter.counterName)
                .setCheckable(true)
                .setOnMenuItemClickListener { MenuItem ->
                    val ncounterValue: Int
                    val existingCounterItem = counterItems.find {
                        it.counterID == MenuItem.itemId
                    }
//                    if (existingCounterItem != null) {
//                        ncounterValue = existingCounterItem.counterValue
//                    }
//                    else {
//                        ncounterValue = counter.counterValue
//                    }
                    ncounterValue = existingCounterItem!!.counterValue
                    setTitle("$counterName")
                    mCounter = ncounterValue
                    txv.text = mCounter.toString()
                    title = existingCounterItem.counterName

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
                    saveLastClickedCounter(existingCounterItem)
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

        lastClickedCounter?.let { counter ->
            val defaultMenuItem = navMenu.findItem(counter)
            defaultMenuItem?.isChecked = true
        }

        txv.setOnClickListener {
            Log.d("w", "WHAT IS MCOUNTER" + mCounter)
            mCounter++
            txv.text = mCounter.toString()
//            loadLastClickedCounter()
            lastClickedCounter?.let { it1 -> updateCounterItems(it1) }
        }

        add_btn.setOnClickListener {
            mCounter++
            txv.text = mCounter.toString()
//            loadLastClickedCounter()
            lastClickedCounter?.let { it1 -> updateCounterItems(it1) }
        }
        minus_btn.setOnClickListener {
            mCounter--
            txv.text = mCounter.toString()
//            loadLastClickedCounter()
            lastClickedCounter?.let { it1 -> updateCounterItems(it1) }
        }
        reset.setOnClickListener {
            mCounter = 0
            txv.text = mCounter.toString()
//            loadLastClickedCounter()
            lastClickedCounter?.let { it1 -> updateCounterItems(it1) }
        }


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

    private fun updateUIAfterDeletion(prev: Counter) {

        val lastClickedCounterObj = counterItems.find{it.counterID == lastClickedCounter}
        if (lastClickedCounterObj != null) {
            title = lastClickedCounterObj.counterName
        }
        if (lastClickedCounterObj != null) {
            txv.text = lastClickedCounterObj.counterValue.toString()
        }


        val navigationView = findViewById<NavigationView>(R.id.navigation_view)

        // Find the corresponding MenuItem based on the counter ID
        val menu = navigationView.menu

        // Find the MenuItem with the same counterID (use View.generateViewId for unique ID)
        val menuItemToRemove = menu.findItem(prev.counterID)

        // Remove the MenuItem from the menu
        if (menuItemToRemove != null) {
            menu.removeItem(menuItemToRemove.itemId)
        }


        Log.d("UI Update", "Deleted Counter: ${prev.counterName}")

        lastClickedCounter?.let { counter ->
            val defaultMenuItem = menu.findItem(counter)
            defaultMenuItem?.isChecked = true
        }

    }


    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Confirm Delete")
            .setMessage("Do you want to delete the current counter?")
            .setPositiveButton("Delete") { dialog, _ ->

                val length = counterItems.size
                if (length != 1) {
                    val prevClickedCounter = counterItems.find { it.counterID == lastClickedCounter }
                    Log.d("hello", prevClickedCounter.toString())

//                    val index = counterItems.indexOf(lastClickedCounter).let { if (it == 0) 0 else it - 1 }

                    val index = counterItems.indexOfFirst { it.counterID == lastClickedCounter }.let { if (it == 0) 0 else it - 1 }

                    Log.d("index value", "Index value of new value" + index.toString())
                    Log.d("counteritems", "The entire list before deleting" + counterItems.toString())
//                    counterItems.remove(lastClickedCounter)
                    counterItems.removeIf { it.counterID == lastClickedCounter }
                    saveCounterItems(counterItems)

                    Log.d("counteritems", "The entire list after deleting" + counterItems.toString())

                    lastClickedCounter = counterItems[index].counterID
                    saveLastClickedCounter(counterItems[index])
                    mCounter = counterItems[index].counterValue

                    if (prevClickedCounter != null) {
                        updateUIAfterDeletion(prevClickedCounter)
                    }

                }
                else {
                    AlertDialog.Builder(this)
                        .setTitle("Cannot Delete")
                        .setMessage("You cannot delete the last remaining item.")
                        .setPositiveButton("OK") { innerDialog, _ ->
                            innerDialog.dismiss()
                        }
                        .show()
                }
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
            val counterId = lastClickedCounter!!
            updateCounterItems(counterId)
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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.action == "com.example.counter.UPDATE_ACTION") {
            val counterId = intent.getIntExtra("counterId", -1)
            if (counterId != -1) {
                // Handle the intent, e.g., update UI or perform logic
                val existingCounterItem = counterItems.find {
                    it.counterID == counterId
                }
                if (existingCounterItem != null) {
                    existingCounterItem.counterValue = mCounter
                }
                saveCounterItems(counterItems)
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        saveCounterItems(counterItems)
        saveLastClickedCounter(counterItems.find { it.counterID == lastClickedCounter }!!)
    }
}

