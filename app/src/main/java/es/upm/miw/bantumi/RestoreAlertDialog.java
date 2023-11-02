package es.upm.miw.bantumi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class RestoreAlertDialog extends DialogFragment {
    @NonNull
    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {
        final MainActivity main = (MainActivity) requireActivity();

        assert main != null;
        AlertDialog.Builder builder = new AlertDialog.Builder(main);
        builder
                .setTitle("Confirmación de Recuperación de Juego")
                .setMessage("¿Desea recuperar el juego guardado? (La partida actual se perderá)")
                .setPositiveButton(
                        "Sí",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Llamar al método para recuperar el juego guardado
                                main.cargarPartidaGuardada();
                            }
                        }
                )
                .setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // No hacer nada, simplemente cerrar el diálogo
                            }
                        }
                );

        return builder.create();
    }
}