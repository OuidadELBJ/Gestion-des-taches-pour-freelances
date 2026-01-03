package data.modele;

public class Payment {

    public String id;
    public String projectId;

    public double amount;
    public long dateMillis;     // date du paiement
    public long dueDateMillis;  // échéance (pour "bientôt/en retard")
    public boolean paid;        // payé ou non

    public String note;

    public Payment(String id, String projectId, double amount, long dateMillis, long dueDateMillis, boolean paid, String note) {
        this.id = id;
        this.projectId = projectId;
        this.amount = amount;
        this.dateMillis = dateMillis;
        this.dueDateMillis = dueDateMillis;
        this.paid = paid;
        this.note = note;
    }
}
