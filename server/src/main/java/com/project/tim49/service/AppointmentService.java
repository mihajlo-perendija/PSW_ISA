package com.project.tim49.service;

import com.project.tim49.dto.AppointmentDTO;
import com.project.tim49.dto.DoctorDTO;
import com.project.tim49.dto.PatientDTO;
import com.project.tim49.model.*;
import com.project.tim49.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;
import java.util.*;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private ClinicRepository clinicRepository;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private TypeOfExaminationRepository typeOfExaminationRepository;
    @Autowired
    private OrdinationRepository ordinationRepository;
    @Autowired
    private PatientRepository patientRepository;

    public ArrayList<AppointmentDTO> getClinicAvailableAppointments(Long clinic_id){
        Clinic clinic = clinicRepository.findById(clinic_id).orElseGet(null);
        if (clinic == null){
            throw new NoSuchElementException();
        }

        ArrayList<Appointment> appointments = appointmentRepository.getByClinicAndPatientNullAndDeletedFalse(clinic);
        ArrayList<AppointmentDTO> appointmentDTOs = new ArrayList<>();
        for (Appointment app: appointments){
            appointmentDTOs.add(new AppointmentDTO(app));
        }
        return appointmentDTOs;
    }

    public AppointmentDTO startAppointment(AppointmentDTO appointmentDTO) {
        Appointment appointment = setAppointmentData(appointmentDTO);

        Patient patient = patientRepository.findById(appointmentDTO.getPatient().getId()).get();
        appointment.setPatient(patient);
        List<DoctorDTO> doctorDTOS = appointmentDTO.getDoctors();
        Set<Doctor> doctors = new HashSet<>();
        for(DoctorDTO d: doctorDTOS) {
            Doctor doc = doctorRepository.findById(d.getId()).get();
            doctors.add(doc);
        }
        appointment.setDoctors(doctors);
        Appointment saved = appointmentRepository.save(appointment);

        patient.getPendingAppointments().add(saved);
        patientRepository.save(patient);

        return new AppointmentDTO(saved);
    }

    public void createAvailableAppointment(AppointmentDTO appointmentDTO) {
        Appointment appointment = setAppointmentData(appointmentDTO);
        appointment.setPatient(null);
        List<DoctorDTO> doctorDTOS = appointmentDTO.getDoctors();
        Set<Doctor> doctors = new HashSet<>();
        for(DoctorDTO d: doctorDTOS) {
            Doctor doc = doctorRepository.findById(d.getId()).get();
            doctors.add(doc);
        }
        appointment.setDoctors(doctors);

        appointmentRepository.save(appointment);
    }

    Appointment setAppointmentData(AppointmentDTO appointmentDTO){
        Appointment appointment = new Appointment();

        appointment.setStartingDateAndTime(appointmentDTO.getStartingDateAndTime());
        appointment.setEndingDateAndTime(appointmentDTO.getStartingDateAndTime() + appointmentDTO.getDuration()/1000);
        appointment.setDuration(appointmentDTO.getDuration());
        appointment.setPrice(appointmentDTO.getPrice());
        appointment.setDiscount(appointmentDTO.getDiscount());
        appointment.setCompleted(false);

        TypeOfExamination type = typeOfExaminationRepository.findById(appointmentDTO.getTypeOfExamination().getId()).get();
        appointment.setTypeOfExamination(type);

        Clinic clinic = clinicRepository.findById(appointmentDTO.getClinic().getId()).get();
        appointment.setClinic(clinic);

        Ordination ordination = ordinationRepository.findById(appointmentDTO.getOrdination().getId()).get();
        appointment.setOrdination(ordination);

        return appointment;
    }

    // readOnly = false -- modifikujemo appointment jer mu dodajemo pacijenta
    // propagation = requires_new -- za svaki poziv metode se pokrece nova transakcija
    // isolation = read_committed -- resava i unrepeatable read jer se za objekat cuva u L1 memoriji pri queriju istog objekta
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public AppointmentDTO choseAvailableAppointment(Long appointment_id, Long patient_id) throws PessimisticLockingFailureException {
        Optional<Patient> patient = patientRepository.findById(patient_id);
        if (!patient.isPresent()) {
            throw new ValidationException("No patient with that ID!");
        }
        Appointment appointment = appointmentRepository.findOneByIdAndLock(appointment_id);
        if (appointment == null) {
            throw new ValidationException("Appointment does not exist");
        }
        if (appointment.getPatient() != null) {
            throw new ValidationException("Appointment already taken");
        }
        // Za testiranje konkurentnog pristupa
        // try { Thread.sleep(7000); } catch (InterruptedException e) { }

        appointment.setPatient(patient.get());
        Appointment saved = appointmentRepository.save(appointment);
        patient.get().getPendingAppointments().add(saved);
        patientRepository.save(patient.get());

        return new AppointmentDTO(saved);
    }

    public void deleteAppointment(Long id) {
        Optional<Appointment> appointment = appointmentRepository.findById(id);
        if (!appointment.isPresent()) {
            throw new ValidationException("No appointment with that ID!");
        }
        Appointment forDelete = getReference(id);
        try {
            forDelete.setDeleted(true);
            appointmentRepository.save(forDelete);
        } catch (Exception e) {
            throw new NoSuchElementException();
        }
    }

    private Appointment getReference(Long id) {
        return appointmentRepository.getOne(id);
    }

    public Appointment save(Appointment appointment){
        return appointmentRepository.save(appointment);
    }
}
