package com.example.counter

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CounterWidgetProvider : AppWidgetProvider() {

    data class Widget(
        val widgetId: Int,
        var counterId: Int
    )

    private fun loadWidgetPairs(context: Context): MutableList<Widget> {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("WidgetPairs", Context.MODE_PRIVATE)
        val widgetPairJson = sharedPreferences.getString("widgetPairs", null)
        if (widgetPairJson != null) {
            val gson = Gson()
            val widgetPairType = object : TypeToken<List<Widget>>() {}.type
            return gson.fromJson(widgetPairJson, widgetPairType)
        }
        return mutableListOf()
    }

    private fun saveWidgetPairs(context: Context, widgetPair: MutableList<Widget>) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("WidgetPairs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val widgetPairJson = gson.toJson(widgetPair)
        editor.putString("widgetPairs", widgetPairJson)
        editor.apply()
    }


    private val counterItems: MutableList<MainActivity.Counter> = mutableListOf()

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val widgetPairs = loadWidgetPairs(context)
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("CounterItems", Context.MODE_PRIVATE)
        val counterItemsJson = sharedPreferences.getString("counterItems", null)
        if (counterItemsJson != null) {
            val gson = Gson()
            val counterItemsType = object : TypeToken<List<MainActivity.Counter>>() {}.type
            val loadedItems =
                gson.fromJson<List<MainActivity.Counter>>(counterItemsJson, counterItemsType)
            counterItems.addAll(loadedItems)
        }
        else {
            val counterId = System.currentTimeMillis().toInt()
            counterItems.add(MainActivity.Counter("First Counter", counterId, 0))
            saveCounterItems(context, counterItems)
        }

        // Update all widgets
        Log.d("Initialise", appWidgetIds.toString() + "is initialised")
        for (appWidgetId in appWidgetIds) {
            val widget = widgetPairs.find { it.widgetId == appWidgetId }
            val firstCounter = counterItems[0]
            if (widget == null) {
                widgetPairs.add(Widget(appWidgetId, firstCounter.counterID))
            }
            Log.d("K", widgetPairs.toString() + "Widgetpair")
            val options = appWidgetManager.getAppWidgetOptions(appWidgetId)
            updateAppWidget(context, appWidgetManager, appWidgetId, widgetPairs, options)
        }
        saveWidgetPairs(context, widgetPairs)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val widgetPairs = loadWidgetPairs(context)
        Log.d("Intent", intent.toString() + "jndidhs")

        // Handle increment and decrement actions
        if (intent.action == INCREMENT_ACTION || intent.action == DECREMENT_ACTION) {
            Log.d("Landed here", "Landed here")
            val counterId = intent.getIntExtra("counterId", -1) // Get the counter ID from the intent

            if (counterId != -1) {
                val sharedPreferences: SharedPreferences =
                    context.getSharedPreferences("CounterItems", Context.MODE_PRIVATE)

                val counterItemsJson = sharedPreferences.getString("counterItems", null)
                val gson = Gson()
                val counterItemsType = object : TypeToken<List<MainActivity.Counter>>() {}.type
                val loadedItems =
                    gson.fromJson<List<MainActivity.Counter>>(counterItemsJson, counterItemsType)
                counterItems.addAll(loadedItems) // Add the loaded items to the list

                val counter = counterItems.find { it.counterID == counterId } ?: return

                if (intent.action == INCREMENT_ACTION) {
                    counter.counterValue++
                } else if (intent.action == DECREMENT_ACTION) {
                    counter.counterValue--
                }

                // Save updated counter value
                saveCounterItems(context, counterItems)

                // Update widget UI
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val widget = ComponentName(context, CounterWidgetProvider::class.java)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(widget)
                for (appWidgetId in appWidgetIds) {
                    val widgetCounterId = widgetPairs.find { it.widgetId == appWidgetId}?.counterId
                    if (widgetCounterId == counterId) {
                        val options = appWidgetManager.getAppWidgetOptions(appWidgetId)
                        updateAppWidget(context, appWidgetManager, appWidgetId, widgetPairs, options)
                    }
                }
            }
        }
        else if (intent.action == INCREMENT_INDEX || intent.action == DECREMENT_INDEX) {
            val widgetId = intent.getIntExtra("widgetId", -1) // Get the counter ID from the intent

            if (widgetId != -1) {
                val sharedPreferences: SharedPreferences =
                    context.getSharedPreferences("CounterItems", Context.MODE_PRIVATE)

                val counterItemsJson = sharedPreferences.getString("counterItems", null)
                val gson = Gson()
                val counterItemsType = object : TypeToken<List<MainActivity.Counter>>() {}.type
                val loadedItems =
                    gson.fromJson<List<MainActivity.Counter>>(counterItemsJson, counterItemsType)
                counterItems.addAll(loadedItems) // Add the loaded items to the list

                val currWidget = widgetPairs.find { it.widgetId == widgetId}
                val counterId = currWidget?.counterId

                var counterIndex = counterItems.indexOfFirst { it.counterID == counterId }

                if (intent.action == INCREMENT_INDEX) {
                    counterIndex = (counterIndex +  1) % counterItems.size
                } else if (intent.action == DECREMENT_INDEX) {
                    counterIndex = (counterIndex - 1 + counterItems.size) % counterItems.size
                }

                val newCounterId = counterItems[counterIndex].counterID
                Log.d("Found", "FOUND WIDGET IS " + currWidget.toString())
                Log.d("diojaojd", "Worked?")
                Log.d("blah", widgetPairs.toString() + "BLAH 1")
                if (currWidget != null) {
                    currWidget.counterId = newCounterId
                }
                Log.d("blah", widgetPairs.toString() + "BLAH 2")
                saveWidgetPairs(context, widgetPairs)

                // Update widget UI
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val widget = ComponentName(context, CounterWidgetProvider::class.java)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(widget)
                for (appWidgetId in appWidgetIds) {
                    val options = appWidgetManager.getAppWidgetOptions(appWidgetId)
                    updateAppWidget(context, appWidgetManager, appWidgetId, widgetPairs, options)
                }
            }
        }
        else if (intent.action == UPDATE_ACTION) {
            // Update widget UI
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val widget = ComponentName(context, CounterWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(widget)
            for (appWidgetId in appWidgetIds) {
                val options = appWidgetManager.getAppWidgetOptions(appWidgetId)
                updateAppWidget(context, appWidgetManager, appWidgetId, widgetPairs, options)
            }
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)

        // Get widget dimensions
        val minWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
        val minHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)

        // Select layout based on dimensions
        val layoutId = when {
            minWidth < 200 || minHeight < 110 -> R.layout.widget_counter_small
            else -> R.layout.widget_counter
        }

        // Update the widget
        val views = RemoteViews(context.packageName, layoutId)
        val widgetPairs = loadWidgetPairs(context)

        appWidgetManager.updateAppWidget(appWidgetId, views)
        updateAppWidget(
            context,
            appWidgetManager,
            appWidgetId,
            widgetPairs,
            newOptions
        )
    }


    private fun saveCounterItems(context: Context, counterItems: MutableList<MainActivity.Counter>) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("CounterItems", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val counterItemsJson = gson.toJson(counterItems)
        editor.putString("counterItems", counterItemsJson)
        editor.apply()
    }



    companion object {
        const val INCREMENT_ACTION = "com.example.counter.INCREMENT_ACTION"
        const val DECREMENT_ACTION = "com.example.counter.DECREMENT_ACTION"
        const val INCREMENT_INDEX = "com.example.counter.INCREMENT_INDEX"
        const val DECREMENT_INDEX = "com.example.counter.DECREMENT_INDEX"
        const val UPDATE_ACTION = "com.example.counter.UPDATE_ACTION"

//        private fun saveWidgetCounterId(context: Context, appWidgetId: Int, counterId: Int) {
//            val sharedPreferences = context.getSharedPreferences("CounterWidgets", Context.MODE_PRIVATE)
//            sharedPreferences.edit()
//                .putInt("widget_$appWidgetId", counterId)
//                .apply()
//        }

//        private fun getWidgetCounterId(widgetPair: MutableList<Widget>, appWidgetId: Int): Int? {
//            return widgetPair.find { it.widgetId == appWidgetId }?.counterId ?: 0
////            val sharedPreferences = context.getSharedPreferences("CounterWidgets", Context.MODE_PRIVATE)
////            return if (sharedPreferences.contains("widget_$appWidgetId")) {
////                sharedPreferences.getInt("widget_$appWidgetId", -1) // Use -1 as a fallback value
////            } else {
////                null
////            }
//        }

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            widgetPair: MutableList<Widget>,
            newOptions: Bundle
        ) {
            val counterItems: MutableList<MainActivity.Counter> = mutableListOf()
            // Retrieve counter value from SharedPreferences
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("CounterItems", Context.MODE_PRIVATE)
//            var counter = sharedPreferences.getInt("counterItems", 0)

            val counterItemsJson = sharedPreferences.getString("counterItems", null)
            val gson = Gson()
            val counterItemsType = object : TypeToken<List<MainActivity.Counter>>() {}.type
            val loadedItems = gson.fromJson<List<MainActivity.Counter>>(counterItemsJson, counterItemsType)
                counterItems.addAll(loadedItems) // Add the loaded items to the list


            val widgetCounterId = widgetPair.find { it.widgetId == appWidgetId }?.counterId ?: return
            val counter = counterItems.find { it.counterID == widgetCounterId } ?: return

//            counter = if (counterItemsJson != null) {
//                val loadedItems = gson.fromJson<List<MainActivity.Counter>>(counterItemsJson, counterItemsType)
//                counterItems.addAll(loadedItems) // Add the loaded items to the list
//                counterItems[0] // CHANGE THIS
//            } else {
//                val counterId = System.currentTimeMillis().toInt()
//                counterItems.add(MainActivity.Counter("First Counter", counterId, 0))
//                counterItems[0]
//            }

            // Construct the RemoteViews object
            val minWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
            val minHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
            val layoutId = when {
                minWidth < 200 || minHeight < 110 -> R.layout.widget_counter_small
                else -> R.layout.widget_counter
            }

            val views = RemoteViews(context.packageName, layoutId)
            views.setTextViewText(R.id.widget_counter_title, counter.counterName)
            views.setTextViewText(R.id.widget_counter_value, counter.counterValue.toString())

            // Set up intent for increment button
            val incrementIntent = Intent(context, CounterWidgetProvider::class.java).apply {
                action = INCREMENT_ACTION
                putExtra("counterId", widgetCounterId) // Pass the counter ID
            }
            val incrementPendingIntent = PendingIntent.getBroadcast(
                context,
                widgetCounterId, // Use a unique request code for each counter ID
                incrementIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            views.setOnClickPendingIntent(R.id.widget_btn_increment, incrementPendingIntent)
            views.setOnClickPendingIntent(R.id.widget_counter_value, incrementPendingIntent)

            // Set up intent for decrement button
            val decrementIntent = Intent(context, CounterWidgetProvider::class.java).apply {
                action = DECREMENT_ACTION
                putExtra("counterId", widgetCounterId) // Pass the counter ID
            }
            val decrementPendingIntent = PendingIntent.getBroadcast(
                context,
                widgetCounterId, // Use a unique request code for each counter ID
                decrementIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            views.setOnClickPendingIntent(R.id.widget_btn_decrement, decrementPendingIntent)


            // Set up intent for decrement button
            val decrementIndexIntent = Intent(context, CounterWidgetProvider::class.java).apply {
                action = DECREMENT_INDEX
                putExtra("widgetId", appWidgetId) // Pass the counter ID
            }
            val decrementIndexPendingIntent = PendingIntent.getBroadcast(
                context,
                appWidgetId, // Use a unique request code for each counter ID
                decrementIndexIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            views.setOnClickPendingIntent(R.id.left_button, decrementIndexPendingIntent)

            // Set up intent for decrement button
            val incrementIndexIntent = Intent(context, CounterWidgetProvider::class.java).apply {
                action = INCREMENT_INDEX
                putExtra("widgetId", appWidgetId) // Pass the counter ID
            }
            val incrementIndexPendingIntent = PendingIntent.getBroadcast(
                context,
                appWidgetId, // Use a unique request code for each counter ID
                incrementIndexIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            views.setOnClickPendingIntent(R.id.right_button, incrementIndexPendingIntent)



            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)

        val widgetPairs = context?.let { loadWidgetPairs(it) }

        // Remove the widget entries for the deleted widget IDs
        appWidgetIds?.forEach { appWidgetId ->
            widgetPairs?.removeIf { it.widgetId == appWidgetId }
        }

        if (context != null) {
            if (widgetPairs != null) {
                saveWidgetPairs(context, widgetPairs)
            }
        }
    }
}
