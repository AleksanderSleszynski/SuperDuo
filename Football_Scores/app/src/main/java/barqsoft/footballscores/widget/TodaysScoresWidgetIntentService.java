package barqsoft.footballscores.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;

public class TodaysScoresWidgetIntentService extends IntentService {

    private static final String[] TODAY__SCORES_PROJECTION =new String[] {
            DatabaseContract.scores_table.AWAY_COL,
            DatabaseContract.scores_table.AWAY_GOALS_COL,
            DatabaseContract.scores_table.HOME_COL,
            DatabaseContract.scores_table.HOME_GOALS_COL

    };

    // these indices must match the projection
    private static final int INDEX_AWAY = 0;
    private static final int INDEX_AWAY_GOALS = 1;
    private static final int INDEX_HOME = 2;
    private static final int INDEX_HOME_GOALS = 3;

    private AppWidgetManager mAppWidgetManager;
    private int[] mAppWidgetIds;
    private String[] fragmentDateArray = new String[1];

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public TodaysScoresWidgetIntentService(String name) {
        super(name);
    }

    public TodaysScoresWidgetIntentService(){
        super("Wiggie");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        mAppWidgetManager = AppWidgetManager.getInstance(this);
        mAppWidgetIds = mAppWidgetManager.getAppWidgetIds(new ComponentName(
                this, TodaysScoresWidgetProvider.class));
        final int N = mAppWidgetIds.length;

        Date fragmentDate = new Date(System.currentTimeMillis());
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");
        fragmentDateArray[0] = mFormat.format(fragmentDate);

        Context context = getApplicationContext();

        Uri scoreWithDate = DatabaseContract.scores_table.buildScoreWithDate();

        Cursor cursor = context.getContentResolver().query(scoreWithDate ,
                TODAY__SCORES_PROJECTION,
                null,
                fragmentDateArray,
                null);

        if(cursor.moveToFirst()){
            String awayTeam = cursor.getString(INDEX_AWAY);
            String awayGoals = cursor.getString(INDEX_AWAY_GOALS);
            String homeTeam = cursor.getString(INDEX_HOME);
            String homeGoals = cursor.getString(INDEX_HOME_GOALS);


            for(int i = 0; i < N; i++){
                int appWidgetId = mAppWidgetIds[i];
                int layOutIdWidget;
                Bundle options = mAppWidgetManager.getAppWidgetOptions(appWidgetId);
                if(options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH) <= 110){
                    layOutIdWidget = R.layout.widget_today_scores;
                } else if (options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH) >= 220){
                    layOutIdWidget = R.layout.widget_today_scores_large;
                } else {
                    layOutIdWidget = R.layout.widget_today_scores_med;
                }

                RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), layOutIdWidget);
                remoteViews.setImageViewResource(R.id.widget_icon, R.drawable.ic_launcher);

                setRemoteContentDescription(remoteViews, homeTeam + " vs " + awayTeam);

                remoteViews.setTextViewText(R.id.home_name, homeTeam);
                remoteViews.setTextViewText(R.id.score_textview, homeGoals + " : " + awayGoals);
                remoteViews.setTextViewText(R.id.away_name, awayTeam);

                Intent fartIntent = new Intent(context, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context,0, fartIntent, 0);
                remoteViews.setOnClickPendingIntent(R.id.widget, pendingIntent);

                mAppWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            }
        }
    }

    private void setRemoteContentDescription(RemoteViews views, String description) {
        views.setContentDescription(R.id.widget_icon, description);
    }

}
