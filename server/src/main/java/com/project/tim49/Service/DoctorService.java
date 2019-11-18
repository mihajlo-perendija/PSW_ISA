package com.project.tim49.Service;

import com.project.tim49.Dto.DoctorDTO;
import com.project.tim49.Model.Appointment;
import com.project.tim49.Model.Clinic;
import com.project.tim49.Model.Doctor;
import com.project.tim49.Model.User;
import com.project.tim49.Repository.ClinicRepository;
import com.project.tim49.Repository.DoctorRepository;
import com.project.tim49.Repository.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private ClinicRepository clinicRepository;
    @Autowired
    private LoginRepository userRepository;

    public List<DoctorDTO> getDoctors(){
        List<Doctor> doctors = doctorRepository.findAll();
        List<DoctorDTO> doctorDTOS = new ArrayList<>();

        for (Doctor doctor: doctors) {
            DoctorDTO doctorDTO = new DoctorDTO(doctor);
            doctorDTOS.add(doctorDTO);
        }

        return doctorDTOS;
    }

    public void createNewDoctor(DoctorDTO doctorDTO){
        if (doctorDTO.getClinic_id() == null){
            throw new ValidationException("Invalid clinic ID!");
        }

        Optional<Clinic> clinic = clinicRepository.findById(doctorDTO.getClinic_id());
        if (!clinic.isPresent()){
            throw new ValidationException("No clinic with that ID!");
        }

        User user = userRepository.findOneByEmail(doctorDTO.getEmail());
        if (user != null){
            throw new ValidationException("User with this email already exists!");
        }

        Doctor doctor = docDTOtoReal(doctorDTO);
        doctor.setEmail(doctorDTO.getEmail());
        doctor.setClinic(clinic.get());
        doctor.setPassword("TEMPPASS");
        doctor.setRole("DOCTOR");

        doctorRepository.save(doctor);
    }

    public boolean hasActiveAppointments(Long id){
        if (id == null) {
            throw new ValidationException("Invalid doctor ID!");
        }

        Optional<Doctor> doctor = doctorRepository.findById(id);
        if (!doctor.isPresent()){
            throw new ValidationException("No doctor with that ID!");
        }

        List<Appointment> appointments = doctor.get().getAppointments();
        for (Appointment appointment: appointments) {
            if (!appointment.isCompleted()){
                return true;
            }
        }

        return false;
    }

    public void deleteDoctor(Long id){
        Optional<Doctor> doctor = doctorRepository.findById(id);
        if (!doctor.isPresent()){
            throw new ValidationException("No doctor with that ID!");
        }

        doctorRepository.delete(doctor.get());
    }

    public Doctor docDTOtoReal(DoctorDTO dto){
        Doctor real = new Doctor();
        real.setName(dto.getName());
        real.setSurname(dto.getSurname());
        real.setAddress(dto.getAddress());
        real.setCity(dto.getCity());
        real.setState(dto.getState());
        real.setPhoneNumber(dto.getPhoneNumber());
        real.setUpin(dto.getUpin());
        real.setShiftStart(dto.getShiftStart());
        real.setShiftEnd(dto.getShiftEnd());
        real.setNumberOfStars(dto.getNumberOfStars());
        real.setNumberOfReviews(dto.getNumberOfReviews());
        return real;
    }

    public boolean shiftValid(String shiftStart, String shiftEnd){
        if (shiftStart == null || shiftStart.equals("")){
            return false;
        }
        String[] start = shiftStart.split(":");
        int startHour;
        int startMinute;
        if (start.length != 2 || start[0].length() > 2 || start[1].length() > 2){
            return false;
        }
        try {
            startHour = Integer.parseInt(start[0]);
            startMinute = Integer.parseInt(start[1]);
        } catch (NumberFormatException e){
            return false;
        }

        if (shiftEnd == null || shiftEnd.equals("")){
            return false;
        }
        String[] end = shiftEnd.split(":");
        int endHour;
        int endMinute;
        if (end.length != 2 || end[0].length() > 2 || end[1].length() > 2){
            return false;
        }
        try {
            endHour = Integer.parseInt(end[0]);
            endMinute = Integer.parseInt(end[1]);
        } catch (NumberFormatException e){
            return false;
        }

        if (startHour > endHour){
            return false;
        } else {
            if (startHour == endHour){
                if (startMinute >= endMinute){
                    return false;
                }
            }
        }


        return true;
    }
}
