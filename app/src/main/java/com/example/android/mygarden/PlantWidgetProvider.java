package com.example.android.mygarden;

/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.example.android.mygarden.provider.PlantContract;
import com.example.android.mygarden.ui.MainActivity;
import com.example.android.mygarden.ui.PlantDetailActivity;

public class PlantWidgetProvider extends AppWidgetProvider {

    // setImageViewResource to update the widget’s image
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int imgRes, int appWidgetId, long plantId, boolean showWaterButton) {
        Log.d("WATER", "updateAppWidget");
        // DONE (3): Set the click handler to open the DetailActivity for plant ID,
        // or the MainActivity if plant ID is invalid
        Intent intent;
        if (plantId == PlantContract.INVALID_PLANT_ID) {
            Log.d("WATER", "id = -1");
            intent = new Intent(context, MainActivity.class);
        } else {
            Log.d("WATER", "id = valid");
            intent = new Intent(context, PlantDetailActivity.class);
            intent.putExtra(PlantDetailActivity.EXTRA_PLANT_ID, plantId);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                0);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.plant_widget);

        // Update image
        views.setImageViewResource(R.id.widget_plant_image, imgRes);

        // Widgets allow click handlers to only launch pending intents
        views.setOnClickPendingIntent(R.id.widget_plant_image, pendingIntent);

        // Add the wateringservice click handler
        Intent wateringIntent = new Intent(context, PlantWateringService.class);
        wateringIntent.setAction(PlantWateringService.ACTION_WATER_PLANT);
        wateringIntent.putExtra("PLANT_ID", plantId);

        PendingIntent wateringPendingIntent = PendingIntent.getService(context, 0,
                wateringIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (showWaterButton){
            views.setViewVisibility(R.id.widget_water_button, View.VISIBLE);
        }
        else {
            views.setViewVisibility(R.id.widget_water_button, View.INVISIBLE);
        }

        views.setOnClickPendingIntent(R.id.widget_water_button, wateringPendingIntent);

        views.setTextViewText(R.id.widget_plant_id, String.valueOf(plantId));

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d("WATER", "onUpdate");
        //Start the intent service update widget action, the service takes care of updating the widgets UI
        PlantWateringService.startActionUpdatePlantWidgets(context);
    }

    // DONE (2): Modify updatePlantWidgets and updateAppWidget to pass the plant ID as well as a boolean
    // to show/hide the water button
    public static void updatePlantWidgets(Context context, AppWidgetManager appWidgetManager,
                                          int imgRes, int[] appWidgetIds, long plantId,
                                          boolean showWaterButton) {
        Log.d("WATER", "updatePlantWidgets");
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, imgRes, appWidgetId, plantId, showWaterButton);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // Perform any action when one or more AppWidget instances have been deleted
    }

    @Override
    public void onEnabled(Context context) {
        // Perform any action when an AppWidget for this provider is instantiated
    }

    @Override
    public void onDisabled(Context context) {
        // Perform any action when the last AppWidget instance for this provider is deleted
    }

}
