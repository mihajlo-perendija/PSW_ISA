package com.project.tim49.Model;

import javax.persistence.*;

@Entity
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_id", referencedColumnName = "id")
    private MedicationDictionary medication;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nurse_id", referencedColumnName = "id")
    private Nurse approves;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", referencedColumnName = "id")
    private Doctor performs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MedicationDictionary getMedication() {
        return medication;
    }

    public void setMedication(MedicationDictionary medication) {
        this.medication = medication;
    }

    public Nurse getApproves() {
        return approves;
    }

    public void setApproves(Nurse approves) {
        this.approves = approves;
    }

    public Doctor getPerforms() {
        return performs;
    }

    public void setPerforms(Doctor performs) {
        this.performs = performs;
    }
}