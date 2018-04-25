package com.example.android.mygarden;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.mygarden.provider.PlantContract;
import com.example.android.mygarden.utils.PlantUtils;


public class GridWidgetService extends RemoteViewsService{

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext());
    }
}

class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    Context context; // needed to access the contentResolver
    Cursor cursor;

    public GridRemoteViewsFactory(Context applicationContext){
        this.context = applicationContext;
    }

    @Override
    public void onCreate() {

    }


    // called when the remoteViewsFactory is created, and each time it is notified to update data
    @Override
    public void onDataSetChanged() {
        Uri URI = PlantContract.BASE_CONTENT_URI.buildUpon().appendPath(PlantContract.PATH_PLANTS)
                .build();
        if (cursor != null) cursor.close();
        cursor = context.getContentResolver().query(
                URI,
                null,
                null,
                null,
                PlantContract.PlantEntry.COLUMN_CREATION_TIME);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (cursor == null) return 0;
        return cursor.getCount();
    }

    // Equivalent to adapters onBindViewHolder
    @Override
    public RemoteViews getViewAt(int position) {
        if (cursor == null || cursor.getCount() == 0) return null;

        cursor.moveToPosition(position);

        int idIndex = cursor.getColumnIndex(PlantContract.PlantEntry._ID);
        int creationTimeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_CREATION_TIME);
        int wateredAtIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME);
        int typeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_PLANT_TYPE);

        long plantId = cursor.getLong(idIndex);
        int plantType = cursor.getInt(typeIndex);
        long lastWatered = cursor.getLong(wateredAtIndex);
        long creationTime = cursor.getLong(creationTimeIndex);

        long timeNow = System.currentTimeMillis();
        long plantAge = timeNow - creationTime;
        long timeSinceWatered = timeNow - lastWatered;

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.plant_widget);
        int imgResource = PlantUtils.getPlantImageRes(context, plantAge, timeSinceWatered, plantType);
        remoteViews.setImageViewResource(R.id.widget_plant_image, imgResource);
        remoteViews.setTextViewText(R.id.widget_plant_name, String.valueOf(plantId));
        remoteViews.setViewVisibility(R.id.widget_water_button, View.GONE);

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
