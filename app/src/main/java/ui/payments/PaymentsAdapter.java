package ui.payments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freelance.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import data.modele.Payment;
import ui.payments.PaymentsViewModel;

import com.example.freelance.data.local.repository.ProjetRepository;
import com.example.freelance.data.local.entity.Projet;

public class PaymentsAdapter extends RecyclerView.Adapter<PaymentsAdapter.VH> {

    private final List<Payment> items = new ArrayList<>();

    // ✅ ViewModel : version utilitaire (format money/date/status)
    private final PaymentsViewModel vm;

    // ✅ Room : récupérer Projet (email/tel)
    private ProjetRepository projetRepo;

    // ✅ exécuteur pour éviter Room sur le main thread
    private final Executor io = Executors.newSingleThreadExecutor();
    private final Handler main = new Handler(Looper.getMainLooper());

    public PaymentsAdapter(Context context) {
        this.vm = new PaymentsViewModel(context.getApplicationContext());
        this.projetRepo = new ProjetRepository(context.getApplicationContext());
    }

    public void submit(List<Payment> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Payment pay = items.get(position);

        h.tvAmount.setText(vm.money(pay.amount));
        h.tvDate.setText("Payé le: " + vm.formatDate(pay.dateMillis));
        h.tvDue.setText("Échéance: " + vm.formatDate(pay.dueDateMillis));
        h.tvStatus.setText(vm.status(pay));
        h.tvNote.setText(pay.note == null ? "" : pay.note);

        // On met des valeurs par défaut le temps que Room réponde
        String fallbackProjectName = "votre projet";
        String fallbackSubject = "Rappel paiement";
        String fallbackMsg = buildReminderMessage(fallbackProjectName, pay);

        // Copier (marche même sans Projet)
        h.btnCopy.setOnClickListener(v -> copyToClipboard(v.getContext(), fallbackMsg));

        // SMS / Email : on tente quand même, mais sans contact -> toast
        h.btnSms.setOnClickListener(v -> PaymentContactHelper.openSms(v.getContext(), null, fallbackMsg));
        h.btnEmail.setOnClickListener(v -> PaymentContactHelper.openEmail(v.getContext(), null, fallbackSubject, fallbackMsg));

        // ✅ Room : charger Projet (clientEmail/clientPhone + nom)
        io.execute(() -> {
            Projet proj = projetRepo.getByIdSync(pay.projectId); // méthode sync (DAO)

            final String projectName = (proj == null || proj.getName() == null || proj.getName().trim().isEmpty())
                    ? "votre projet"
                    : proj.getName();

            final String msg = buildReminderMessage(projectName, pay);
            final String subject = "Rappel paiement - " + projectName;

            final String phone = (proj == null) ? null : proj.getClientPhone();
            final String email = (proj == null) ? null : proj.getClientEmail();

            main.post(() -> {
                // Remplacer les listeners avec les bonnes infos
                h.btnCopy.setOnClickListener(v -> copyToClipboard(v.getContext(), msg));

                h.btnSms.setOnClickListener(v -> PaymentContactHelper.openSms(v.getContext(), phone, msg));

                h.btnEmail.setOnClickListener(v -> PaymentContactHelper.openEmail(v.getContext(), email, subject, msg));
            });
        });
    }

    private void copyToClipboard(Context c, String msg) {
        ClipboardManager cm = (ClipboardManager) c.getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            cm.setPrimaryClip(ClipData.newPlainText("relance", msg));
            android.widget.Toast.makeText(c, "Message copié ✅", android.widget.Toast.LENGTH_SHORT).show();
        } else {
            android.widget.Toast.makeText(c, "Clipboard indisponible", android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    private String buildReminderMessage(String projectName, Payment pay) {
        return "Bonjour,\n\n"
                + "Petit rappel : le paiement de " + vm.money(pay.amount)
                + " pour le projet \"" + projectName + "\" est attendu.\n"
                + "Échéance : " + vm.formatDate(pay.dueDateMillis) + "\n\n"
                + "Merci d’avance.\n";
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvAmount, tvDate, tvDue, tvStatus, tvNote;
        Button btnCopy, btnSms, btnEmail;

        VH(@NonNull View v) {
            super(v);
            tvAmount = v.findViewById(R.id.tvPayAmount);
            tvDate = v.findViewById(R.id.tvPayDate);
            tvDue = v.findViewById(R.id.tvPayDue);
            tvStatus = v.findViewById(R.id.tvPayStatus);
            tvNote = v.findViewById(R.id.tvPayNote);

            btnCopy = v.findViewById(R.id.btnCopyReminder);
            btnSms = v.findViewById(R.id.btnSmsReminder);
            btnEmail = v.findViewById(R.id.btnEmailReminder);
        }
    }
}
