package barqsoft.footballscores.widget;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;


public class ScoresListRemoteViewsService extends RemoteViewsService {

    @Override
    public FootballRemoteViewsFactory onGetViewFactory(Intent intent) {
        return new FootballRemoteViewsFactory(getApplicationContext()) ;
    }

    public class FootballRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
        
        private Context mContext;
        private Cursor mCursor;
        
        public FootballRemoteViewsFactory(Context context) {
            this.mContext = context;
        }

        @Override
        public void onCreate() {}

        @Override
        public void onDataSetChanged() {
            if(mCursor != null ){mCursor.close();}
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String[] date = new String[1];
            date[0] = format.format(new Date(System.currentTimeMillis()));
            mCursor = mContext.getContentResolver().query(
                    DatabaseContract.scores_table.buildScoreWithDate(),
                    null,
                    null,
                    date,
                    null
            );
        }

        @Override
        public void onDestroy() {
            mCursor.close();
        }

        @Override
        public int getCount() {
            return mCursor == null ? 0 : mCursor.getCount();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);

            if(mCursor.moveToPosition(position)){

                views.setTextViewText(R.id.home_name, mCursor.getString(
                        mCursor.getColumnIndex(DatabaseContract.scores_table.HOME_COL)));

                views.setImageViewResource(R.id.home_crest, Utilies.getTeamCrestByTeamName(
                        mCursor.getString(
                                mCursor.getColumnIndex(DatabaseContract.scores_table.HOME_COL))));

                views.setTextViewText(R.id.away_name, mCursor.getString(
                        mCursor.getColumnIndex(DatabaseContract.scores_table.AWAY_COL)));

                views.setTextViewText(R.id.score_textview,
                        Utilies.getScores(
                                mCursor.getInt(mCursor.getColumnIndex(DatabaseContract.scores_table.HOME_GOALS_COL)),
                                mCursor.getInt(mCursor.getColumnIndex(DatabaseContract.scores_table.AWAY_GOALS_COL))));

                views.setTextViewText(R.id.data_textview,
                        mCursor.getString(
                                mCursor.getColumnIndex(DatabaseContract.scores_table.TIME_COL)));
            } else {
                views.setTextViewText(R.id.home_name, "");
                views.setImageViewResource(R.id.home_crest, Utilies.getTeamCrestByTeamName("none"));
                views.setTextViewText(R.id.away_name, "");
                views.setImageViewResource(R.id.away_crest, Utilies.getTeamCrestByTeamName("none"));
                views.setTextViewText(R.id.score_textview, "");
                views.setTextViewText(R.id.data_textview, "");

            }

            views.setOnClickFillInIntent(R.id.widget_list_item, new Intent());

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            if(mCursor == null || !mCursor.moveToPosition(position)){
                return 0;
            } else {
                return mCursor.getLong(mCursor.getColumnIndex(DatabaseContract.scores_table.MATCH_ID));
            }
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
