package es.upm.miw.bantumi.model;
import androidx.annotation.NonNull;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(tableName = "Result")
public class ResultEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private Integer id;
    @ColumnInfo(name = "nombre_jugador_1")
    private String nombreJugador1;

    @ColumnInfo(name = "nombre_jugador_2")
    private String nombreJugador2;

    @ColumnInfo(name = "fecha")
    private String fecha;

    @ColumnInfo(name = "puntuacion_almacen_1")
    private int puntuacionAlmacen1;

    @ColumnInfo(name = "puntuacion_almacen_2")
    private int puntuacionAlmacen2;

    // Constructor, getters y setters

    public ResultEntity(Integer id, String nombreJugador1, String nombreJugador2, String fecha, int puntuacionAlmacen1, int puntuacionAlmacen2) {
        this.id = id;
        this.nombreJugador1 = nombreJugador1;
        this.nombreJugador2 = nombreJugador2;
        this.fecha = fecha;
        this.puntuacionAlmacen1 = puntuacionAlmacen1;
        this.puntuacionAlmacen2 = puntuacionAlmacen2;
    }

    @NonNull
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombreJugador1() {
        return nombreJugador1;
    }

    public void setNombreJugador1(String nombreJugador1) {
        this.nombreJugador1 = nombreJugador1;
    }

    public String getNombreJugador2() {
        return nombreJugador2;
    }

    public void setNombreJugador2(String nombreJugador2) {
        this.nombreJugador2 = nombreJugador2;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getPuntuacionAlmacen1() {
        return puntuacionAlmacen1;
    }

    public void setPuntuacionAlmacen1(int puntuacionAlmacen1) {
        this.puntuacionAlmacen1 = puntuacionAlmacen1;
    }

    public int getPuntuacionAlmacen2() {
        return puntuacionAlmacen2;
    }

    public void setPuntuacionAlmacen2(int puntuacionAlmacen2) {
        this.puntuacionAlmacen2 = puntuacionAlmacen2;
    }
}