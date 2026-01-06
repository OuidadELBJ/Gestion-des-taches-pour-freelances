package com.example.freelance.data.mapper;

import com.example.freelance.data.local.entity.Paiement;

import java.util.Date;

import data.modele.Payment;

public class PaymentMapper {

    public static Payment toModel(Paiement e) {
        if (e == null) return null;

        long dateMs = (e.getDate() != null) ? e.getDate().getTime() : 0L;
        long dueMs  = (e.getDueDate() != null) ? e.getDueDate().getTime() : 0L;

        return new Payment(
                e.getIdPaiement(),
                e.getProjectId(),
                e.getAmount(),
                dateMs,
                dueMs,
                e.isPaid(),
                e.getNote()
        );
    }

    public static Paiement toEntity(Payment m) {
        if (m == null) return null;

        Date date = (m.dateMillis > 0) ? new Date(m.dateMillis) : null;
        Date due  = (m.dueDateMillis > 0) ? new Date(m.dueDateMillis) : null;

        // Champs "extra" (email/phone/invoiceRef) => null si tu ne les utilises pas encore
        return new Paiement(
                m.id,
                m.projectId,
                m.amount,
                date,
                null,        // method
                m.note,
                new Date(),  // lastUpdated
                false,       // isSynced
                due,
                m.paid,
                null,        // clientEmail
                null,        // clientPhone
                null         // invoiceRef
        );
    }
}
