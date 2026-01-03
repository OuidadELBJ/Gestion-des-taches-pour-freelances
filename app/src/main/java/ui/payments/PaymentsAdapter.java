package ui.payments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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

import data.fake.FakeProjectStore;
import data.modele.Payment;
import data.modele.Project;

public class PaymentsAdapter extends RecyclerView.Adapter<PaymentsAdapter.VH> {

    private final List<Payment> items = new ArrayList<>();
    private final PaymentsViewModel vm = new PaymentsViewModel();

    public void submit(List<Payment> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull @Override
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

        Project proj = FakeProjectStore.get().getById(pay.projectId);

        // ✅ message complet (pour copie + SMS + mail)
        String msg = buildReminderMessage(proj, pay);
        String subject = "Rappel paiement - " + (proj == null ? "" : proj.name);

        // Copier
        h.btnCopy.setOnClickListener(v -> {
            ClipboardManager cm = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setPrimaryClip(ClipData.newPlainText("relance", msg));
            android.widget.Toast.makeText(v.getContext(), "Message copié ✅", android.widget.Toast.LENGTH_SHORT).show();
        });

        // SMS
        h.btnSms.setOnClickListener(v -> {
            String phone = (proj == null) ? "" : proj.clientPhone;
            PaymentContactHelper.openSms(v.getContext(), phone, msg);
        });

        // Email
        h.btnEmail.setOnClickListener(v -> {
            String email = (proj == null) ? "" : proj.clientEmail;
            PaymentContactHelper.openEmail(v.getContext(), email, subject, msg);
        });
    }

    private String buildReminderMessage(Project proj, Payment pay) {
        String projectName = (proj == null) ? "votre projet" : proj.name;

        return "Bonjour,\n\n"
                + "Petit rappel : le paiement de " + vm.money(pay.amount)
                + " pour le projet \"" + projectName + "\" est attendu.\n"
                + "Échéance : " + vm.formatDate(pay.dueDateMillis) + "\n\n"
                + "Merci d’avance.\n";
    }

    @Override public int getItemCount() { return items.size(); }

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
