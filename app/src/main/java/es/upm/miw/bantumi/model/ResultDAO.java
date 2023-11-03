package es.upm.miw.bantumi.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface ResultDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertResult(ResultEntity result);

    @Query("SELECT * FROM Result ORDER BY id DESC")
    List<ResultEntity> getAllResults();

    // Agrega otras consultas o métodos que puedas necesitar
}