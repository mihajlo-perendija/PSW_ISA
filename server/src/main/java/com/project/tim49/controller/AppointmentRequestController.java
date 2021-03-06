package com.project.tim49.controller;

import com.project.tim49.dto.AppointmentDTO;
import com.project.tim49.service.AppointmentRequestService;
import com.project.tim49.service.AppointmentService;
import com.project.tim49.service.DoctorService;
import com.project.tim49.service.EmailService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.ValidationException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value = "api/appointmentRequest")
public class AppointmentRequestController {

    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private AppointmentRequestService appointmentRequestService;
    @Autowired
    private EmailService emailService;

    @GetMapping(path="/getClinicAppointmentRequests/{clinic_id}" ,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMINC')")
    public ResponseEntity getClinicAppointmentRequests(@PathVariable Long clinic_id) {
        try {
            ArrayList<AppointmentDTO> appointmentRequests  = appointmentRequestService.getClinicAppointmentRequests(clinic_id);
            return new ResponseEntity<>(appointmentRequests, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Invalid clinic", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(path="/scheduleNewAppointment/{role}" ,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('DOCTOR') or hasAuthority('PATIENT')")
    public ResponseEntity scheduleNewAppointment(@PathVariable String role,@RequestBody AppointmentDTO appointmentDTO) throws InterruptedException{
        if (appointmentDTO == null){
            return new ResponseEntity<>("Invalid appointment", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (role == null || (!role.equals("doctor") && !role.equals("patient")) ){
            return new ResponseEntity<>("Invalid request", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if(appointmentDTO.getStartingDateAndTime() == 0 || appointmentDTO.getPatient() == null
                || appointmentDTO.getDoctors() == null){
            return new ResponseEntity<>("Invalid appointment", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        try {
            AppointmentDTO returnValue = appointmentRequestService.scheduleNewAppointment(appointmentDTO);
            this.emailService.sendNewAppointmentScheduled(role, returnValue.getPatient(), returnValue.getDoctors().get(0), new DateTime(appointmentDTO.getStartingDateAndTime()*1000));

            return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (ValidationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (NumberFormatException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (PessimisticLockingFailureException e) {
            return new ResponseEntity<>("This time slot has already been taken by somebody else.", HttpStatus.CONFLICT);
        }
    }

    @PostMapping(path="/approveAppointmentRequest" ,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMINC')")
    public ResponseEntity approveAppointmentRequest(@RequestBody AppointmentDTO appointmentDTO) throws InterruptedException {
        if (appointmentDTO == null){
            return new ResponseEntity<>("Invalid appointment", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if(appointmentDTO.getDoctors() == null
                || appointmentDTO.getStartingDateAndTime() == 0 || appointmentDTO.getDuration() == 0
                || appointmentDTO.getPatient() == null){
            return new ResponseEntity<>("Invalid appointment", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        try {
            AppointmentDTO returnValue = appointmentRequestService.approveAppointmentRequest(appointmentDTO);
            if (returnValue.isConfirmed()){
                this.emailService.sendAppointmentRequestApproved(returnValue);
            } else {
                this.emailService.sendAppointmentRequestApprovedPatientConfirmation(returnValue);
            }

            return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
        } catch (ValidationException | NoSuchElementException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (PessimisticLockingFailureException e) {
            return new ResponseEntity<>("This request might have been already approved, please reload the page with all requests", HttpStatus.CONFLICT);
        }
    }

    @GetMapping(path = "/confirm_appointment/{appointment_id}")
    public ResponseEntity patientConfirmAppointment(@PathVariable Long appointment_id) throws InterruptedException{
        try {
            AppointmentDTO appointment = this.appointmentService.patientConfirmAppointment(appointment_id);
            this.emailService.sendAppointmentRequestApproved(appointment);

            String myUrl = "http://oettinger.herokuapp.com/#/";
            URI myURI = new URI(myUrl);
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(myURI);
            return new ResponseEntity<>("redirect:http://oettinger.herokuapp.com/#/" , headers, HttpStatus.PERMANENT_REDIRECT);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Appointment not found" , HttpStatus.NOT_FOUND);
        } catch (SecurityException e) {
            return new ResponseEntity<>(e.getMessage() , HttpStatus.FORBIDDEN);
        } catch (URISyntaxException e) {
            return new ResponseEntity<>("Uri not valid" , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/reject_appointment/{appointment_id}")
    public ResponseEntity patientRejectAppointment(@PathVariable Long appointment_id) {
        try {
            AppointmentDTO appointment = this.appointmentService.patientRejectAppointment(appointment_id);

            String myUrl = "http://oettinger.herokuapp.com/#/";
            URI myURI = new URI(myUrl);
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(myURI);
            return new ResponseEntity<>("redirect:http://oettinger.herokuapp.com/#/" , headers, HttpStatus.PERMANENT_REDIRECT);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Appointment not found" , HttpStatus.NOT_FOUND);
        } catch (SecurityException e) {
            return new ResponseEntity<>(e.getMessage() , HttpStatus.FORBIDDEN);
        } catch (URISyntaxException e) {
            return new ResponseEntity<>("Uri not valid" , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(path = "/rejectAppointmentRequest/{id}")
    @PreAuthorize("hasAuthority('ADMINC')")
    public ResponseEntity rejectAppointmentRequest(@PathVariable Long id,@RequestBody String message) {
        AppointmentDTO deleted = new AppointmentDTO();
        try{
            deleted = appointmentRequestService.rejectRequest(id);
            this.emailService.sendAppointmentRequestRejected(deleted, message);
        }catch(NoSuchElementException e){
            return new ResponseEntity<>("Appointment request deletion failed, request not found!",
                    HttpStatus.NOT_FOUND);
        }catch(SecurityException e){
            return new ResponseEntity<>("Appointment request already approved!",
                    HttpStatus.NOT_ACCEPTABLE);
        }catch(InterruptedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }
        return new ResponseEntity<>(deleted, HttpStatus.OK);
    }
}
