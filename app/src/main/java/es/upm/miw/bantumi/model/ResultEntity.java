package es.upm.miw.bantumi.model;
import androidx.annotation.NonNull;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(tableName = "Result")
public class ResultEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "nombre_jugador")
    private String nombreJugador;

    @ColumnInfo(name = "fecha")
    private String fecha;

    @ColumnInfo(name = "puntuacion_almacen_1")
    private int puntuacionAlmacen1;

    @ColumnInfo(name = "puntuacion_almacen_2")
    private int puntuacionAlmacen2;

    // Constructor, getters y setters
}