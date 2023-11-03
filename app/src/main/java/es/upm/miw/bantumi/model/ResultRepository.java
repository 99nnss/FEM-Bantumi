package es.upm.miw.bantumi.model;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;

public class ResultRepository {
    private ResultDAO resultDao;
    private LiveData<List<ResultEntity>> allResults;

    public ResultRepository(Application application) {
        ResultRoomDatabase database = ResultRoomDatabase.getDatabase(application);
        resultDao = database.resultDao();
        allResults = (LiveData<List<ResultEntity>>) resultDao.getAllResults();
    }

    public LiveData<List<ResultEntity>> getAllResults() {
        return allResults;
    }

    public void insertResult(ResultEntity result) {
        ResultRoomDatabase.databaseWriteExecutor.execute(() -> {
            resultDao.insertResult(result);
        });
    }

    // Agrega otros métodos según tus necesidades, como para actualizar o eliminar resultados
}
