package barqsoft.footballscores.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.service.myFetchService;

public class TodaysScoresWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        for(int i = 0; i < N; i++){
            int appWidgetid = appWidgetIds[i];
            int layOutIdWidge;
            Bundle options =  appWidgetManager.getAppWidgetOptions(appWidgetid);
            if(options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)<= 110){
                layOutIdWidge = R.layout.widget_today_scores;
            } else if (options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)>=220){
                layOutIdWidge = R.layout.widget_today_scores_large;
            } else {
                layOutIdWidge = R.layout.widget_today_scores_med;
            }

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), layOutIdWidge);
            context.startService(new Intent(context, TodaysScoresWidgetIntentService.class));

            Intent clickIntent = new Intent(context, MainActivity.class);
            PendingIntent clickPendingIntent = PendingIntent.getActivity(context,0,clickIntent,0);
            remoteViews.setOnClickPendingIntent(R.id.widget, clickPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetid, remoteViews);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        context.startService(new Intent(context, TodaysScoresWidgetIntentService.class));
    }

    @Override
    public  void onReceive( Context context,Intent intent){
        super.onReceive(context, intent);

        if (myFetchService.ACTION_DATA_UPDATED.equals(intent.getAction())){
            context.startService(new Intent(context,TodaysScoresWidgetIntentService.class));
        }

    }
}
