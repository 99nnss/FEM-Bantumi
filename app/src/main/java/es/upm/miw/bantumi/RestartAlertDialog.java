package es.upm.miw.bantumi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class RestartAlertDialog extends DialogFragment {
    @NonNull
    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {
        final MainActivity main = (MainActivity) requireActivity();

        assert main != null;
        AlertDialog.Builder builder = new AlertDialog.Builder(main);
        builder
                .setTitle("Confirmación Reinicio de Juego")
                .setMessage("¿Desea reiniciar el juego?")
                .setPositiveButton(
                        "Sí",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                main.reiniciarJuego(); // Llamar al método para reiniciar el juego
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
