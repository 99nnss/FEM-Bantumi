package es.upm.miw.bantumi;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.upm.miw.bantumi.model.BantumiViewModel;
import es.upm.miw.bantumi.model.ResultEntity;
import es.upm.miw.bantumi.model.ResultRoomDatabase;

public class MainActivity extends AppCompatActivity {

    protected final String LOG_TAG = "MiW";
    JuegoBantumi juegoBantumi;
    BantumiViewModel bantumiVM;
    int numInicialSemillas;
    private boolean partidaModificada = false;
    private ResultRoomDatabase resultRoomDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instancia el ViewModel y el juego, y asigna observadores a los huecos
        numInicialSemillas = getResources().getInteger(R.integer.intNumInicialSemillas);
        bantumiVM = new ViewModelProvider(this).get(BantumiViewModel.class);
        juegoBantumi = new JuegoBantumi(bantumiVM, JuegoBantumi.Turno.turnoJ1, numInicialSemillas);
        resultRoomDatabase = ResultRoomDatabase.getDatabase(this);
        crearObservadores();
    }

    /**
     * Crea y subscribe los observadores asignados a las posiciones del tablero.
     * Si se modifica el contenido del tablero -> se actualiza la vista.
     */
    private void crearObservadores() {
        for (int i = 0; i < JuegoBantumi.NUM_POSICIONES; i++) {
            int finalI = i;
            bantumiVM.getNumSemillas(i).observe(    // Huecos y almacenes
                    this,
                    new Observer<Integer>() {
                        @Override
                        public void onChanged(Integer integer) {
                            mostrarValor(finalI, juegoBantumi.getSemillas(finalI));
                        }
                    });
        }
        bantumiVM.getTurno().observe(   // Turno
                this,
                new Observer<JuegoBantumi.Turno>() {
                    @Override
                    public void onChanged(JuegoBantumi.Turno turno) {
                        marcarTurno(juegoBantumi.turnoActual());
                    }
                }
        );
    }

    /**
     * Indica el turno actual cambiando el color del texto
     *
     * @param turnoActual turno actual
     */
    private void marcarTurno(@NonNull JuegoBantumi.Turno turnoActual) {
        TextView tvJugador1 = findViewById(R.id.tvPlayer1);
        TextView tvJugador2 = findViewById(R.id.tvPlayer2);
        switch (turnoActual) {
            case turnoJ1:
                tvJugador1.setTextColor(getColor(R.color.white));
                tvJugador1.setBackgroundColor(getColor(android.R.color.holo_blue_light));
                tvJugador2.setTextColor(getColor(R.color.black));
                tvJugador2.setBackgroundColor(getColor(R.color.white));
                break;
            case turnoJ2:
                tvJugador1.setTextColor(getColor(R.color.black));
                tvJugador1.setBackgroundColor(getColor(R.color.white));
                tvJugador2.setTextColor(getColor(R.color.white));
                tvJugador2.setBackgroundColor(getColor(android.R.color.holo_blue_light));
                break;
            default:
                tvJugador1.setTextColor(getColor(R.color.black));
                tvJugador2.setTextColor(getColor(R.color.black));
        }
    }

    /**
     * Muestra el valor <i>valor</i> en la posición <i>pos</i>
     *
     * @param pos posición a actualizar
     * @param valor valor a mostrar
     */
    private void mostrarValor(int pos, int valor) {
        String num2digitos = String.format(Locale.getDefault(), "%02d", pos);
        // Los identificadores de los huecos tienen el formato casilla_XX
        int idBoton = getResources().getIdentifier("casilla_" + num2digitos, "id", getPackageName());
        if (0 != idBoton) {
            TextView viewHueco = findViewById(idBoton);
            viewHueco.setText(String.valueOf(valor));
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.opciones_menu, menu);
        return true;
    }


    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.opcAjustes: // @todo Preferencias
//                startActivity(new Intent(this, BantumiPrefs.class));
//                return true;
            case R.id.opcGuardarPartida:
                guardarPartida();
                return true;
            case R.id.opcReiniciarPartida:
                mostrarRestartDialog();
                return true;
            case R.id.opcRecuperarPartida:
                // Recuperar la partida
                mostrarRestoreDialog();
                return true;
            case R.id.opcAcercaDe:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.aboutTitle)
                        .setMessage(R.string.aboutMessage)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                return true;

            // @TODO!!! resto opciones

            default:
                Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.txtSinImplementar),
                        Snackbar.LENGTH_LONG
                ).show();
        }
        return true;
    }

    public void cargarPartidaGuardada() {
        // Verifica si hay una partida guardada previamente
        String partidaGuardada = leerPartidaGuardadaDesdeArchivo();

        if (partidaGuardada != null && !partidaGuardada.isEmpty()) {
            // Si se encuentra una partida guardada, deserializa y carga el juego
            juegoBantumi.deserializa(partidaGuardada);

            // Actualiza la vista para reflejar el juego cargado
            actualizarVistaSegunJuego();

            // Restablece la bandera de partida modificada
            partidaModificada = false;
        }
    }

    private void actualizarVistaSegunJuego() {
        // Actualiza la vista para reflejar el turno actual
        marcarTurno(juegoBantumi.turnoActual());

        // Actualiza la representación de las semillas en el tablero
        for (int i = 0; i < JuegoBantumi.NUM_POSICIONES; i++) {
            mostrarValor(i, juegoBantumi.getSemillas(i));
        }
    }

    private String leerPartidaGuardadaDesdeArchivo() {
        String partidaGuardada = null;
        try {
            FileInputStream fileInputStream = openFileInput("partida_guardada.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            partidaGuardada = sb.toString();
        } catch (IOException e) {
            // Maneja errores al leer el archivo
            e.printStackTrace();
        }
        return partidaGuardada;
    }


    public void reiniciarJuego() {
        // limpiar tablero
        for (int i = 0; i < JuegoBantumi.NUM_POSICIONES; i++) {
            juegoBantumi.setSemillas(i, 0);
        }

        juegoBantumi.inicializar(JuegoBantumi.Turno.turnoJ1);

        marcarTurno(JuegoBantumi.Turno.turnoJ1);
        for (int i = 0; i < JuegoBantumi.NUM_POSICIONES; i++) {
            mostrarValor(i, juegoBantumi.getSemillas(i));
        }
    }
    private void mostrarRestartDialog() {
        RestartAlertDialog restartDialog = new RestartAlertDialog();
        restartDialog.show(getSupportFragmentManager(), "RESTART_DIALOG");

    }

    private void mostrarRestoreDialog() {
        RestoreAlertDialog RestoreAlertDialog = new RestoreAlertDialog();
        RestoreAlertDialog.show(getSupportFragmentManager(), "RESTORE_DIALOG");

    }

    /**
     * Acción que se ejecuta al pulsar sobre cualquier hueco
     *
     * @param v Vista pulsada (hueco)
     */
    public void huecoPulsado(@NonNull View v) {
        String resourceName = getResources().getResourceEntryName(v.getId()); // pXY
        int num = Integer.parseInt(resourceName.substring(resourceName.length() - 2));
        Log.i(LOG_TAG, "huecoPulsado(" + resourceName + ") num=" + num);

        switch (juegoBantumi.turnoActual()) {
            case turnoJ1:
                juegoBantumi.jugar(num);
                partidaModificada = true; // Marcar partida como modificada
                break;
            case turnoJ2:
                juegaComputador();
                partidaModificada = true; // Marcar partida como modificada
                break;
            default: // JUEGO TERMINADO
                finJuego();
        }

        if (juegoBantumi.juegoTerminado()) {
            finJuego();
        }
    }

    /**
     * Elige una posición aleatoria del campo del jugador2 y realiza la siembra
     * Si mantiene turno -> vuelve a jugar
     */
    void juegaComputador() {
        while (juegoBantumi.turnoActual() == JuegoBantumi.Turno.turnoJ2) {
            int pos = 7 + (int) (Math.random() * 6);    // posición aleatoria [7..12]
            Log.i(LOG_TAG, "juegaComputador(), pos=" + pos);
            if (juegoBantumi.getSemillas(pos) != 0 && (pos < 13)) {
                juegoBantumi.jugar(pos);
            } else {
                Log.i(LOG_TAG, "\t posición vacía");
            }
        }
    }

    /**
     * El juego ha terminado. Volver a jugar?
     */
    private void finJuego() {
        int puntuacionJugador1 = juegoBantumi.getSemillas(6);
        int puntuacionJugador2 = juegoBantumi.getSemillas(13);

        String texto = (juegoBantumi.getSemillas(6) > 6 * numInicialSemillas)
                ? "Gana Jugador 1"
                : "Gana Jugador 2";
        if (juegoBantumi.getSemillas(6) == 6 * numInicialSemillas) {
            texto = "¡¡¡ EMPATE !!!";
        }
        Snackbar.make(
                        findViewById(android.R.id.content),
                        texto,
                        Snackbar.LENGTH_LONG
                )
                .show();

        guardarPuntuaciones(puntuacionJugador1, puntuacionJugador2);

        Snackbar.make(
                findViewById(android.R.id.content),
                "Se ha guardado el resultado",
                Snackbar.LENGTH_LONG
        ).show();

        // 结束游戏
        new FinalAlertDialog().show(getSupportFragmentManager(), "ALERT_DIALOG");
    }

    public void guardarPuntuaciones(int puntuacionJugador1, int puntuacionJugador2) {

        if (ResultRoomDatabase.databaseWriteExecutor != null) {
            Log.d("Database", "ResultRoomDatabase is not null");
            ResultEntity resultado = new ResultEntity(1, "Jugador 1", "Jugador 2", getCurrentDateTime(), puntuacionJugador1, puntuacionJugador2);
            ResultRoomDatabase.databaseWriteExecutor.execute(() -> {
                resultRoomDatabase.resultDao().insertResult(resultado);
            });
        } else {
            Log.e("Database", "ResultRoomDatabase is null");
        }
    }

    private String getCurrentDateTime() {
        String formato = "yyyy-MM-dd HH:mm:ss";

        // Obtén la fecha y hora actual
        Date fechaHoraActual = new Date();

        // Crea un objeto SimpleDateFormat con el formato deseado
        SimpleDateFormat sdf = new SimpleDateFormat(formato);

        // Formatea la fecha y hora actual según el formato
        return sdf.format(fechaHoraActual);
    }

    private void guardarPartida() {
        String juegoSerializado = juegoBantumi.serializa();

        try {
            FileOutputStream fos = openFileOutput("partida_guardada.txt", Context.MODE_PRIVATE);
            fos.write(juegoSerializado.getBytes());
            fos.close();
            Snackbar.make(
                    findViewById(android.R.id.content),
                    "Partida guardada exitosamente",
                    Snackbar.LENGTH_SHORT
            ).show();
        } catch (IOException e) {
            e.printStackTrace();
            Snackbar.make(
                    findViewById(android.R.id.content),
                    "Error al guardar la partida",
                    Snackbar.LENGTH_SHORT
            ).show();
        }
    }

}