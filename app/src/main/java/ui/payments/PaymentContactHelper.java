package ui.payments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class PaymentContactHelper {

    // ✅ Ouvre l’app SMS PAR DÉFAUT avec numéro + message déjà remplis
    public static void openSms(Context c, String phone, String message) {
        if (phone == null || phone.trim().isEmpty()) {
            Toast.makeText(c, "Numéro client manquant", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri uri = Uri.parse("smsto:" + Uri.encode(phone));
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", message);

        try {
            c.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(c, "Impossible d’ouvrir l’app Messages", Toast.LENGTH_SHORT).show();
        }
    }

    // ✅ Ouvre l’app Mail PAR DÉFAUT avec email + sujet + message déjà remplis
    public static void openEmail(Context c, String email, String subject, String body) {
        if (email == null || email.trim().isEmpty()) {
            Toast.makeText(c, "Email client manquant", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + Uri.encode(email)));
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);

        try {
            c.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(c, "Impossible d’ouvrir l’app Mail", Toast.LENGTH_SHORT).show();
        }
    }
}
