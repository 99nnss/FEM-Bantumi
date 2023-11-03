package es.upm.miw.bantumi.model;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Database(entities = {ResultEntity.class}, version = 1, exportSchema = false)
public abstract class ResultRoomDatabase extends RoomDatabase {
    public static Executor databaseWriteExecutor = Executors.newSingleThreadExecutor();

    public abstract ResultDAO resultDao();

    private static volatile ResultRoomDatabase INSTANCE;

    public static ResultRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ResultRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    ResultRoomDatabase.class, "result_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
