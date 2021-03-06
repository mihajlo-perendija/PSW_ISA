package com.project.tim49.controller;

import com.project.tim49.dto.OrdinationAvailabilityDTO;
import com.project.tim49.dto.OrdinationDTO;
import com.project.tim49.service.OrdinationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.ValidationException;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value = "api/ordinations")
public class OrdinationController {

    @Autowired
    OrdinationService ordinationService;

    @GetMapping(path="/{id}")
    @PreAuthorize("hasAuthority('ADMINC') or hasAuthority('DOCTOR')")
    public ResponseEntity getOrdinations(@PathVariable("id") Long clinic_id) {
        List<OrdinationDTO> ordinations = ordinationService.getAll(clinic_id);
        return new ResponseEntity(ordinations, HttpStatus.OK);
    }

    @PostMapping(path = "/{id}", consumes = "application/json", produces =
            "application/json")
    @PreAuthorize("hasAuthority('ADMINC')")
    public ResponseEntity addOrdination(@PathVariable("id") Long clinic_id,
                                        @RequestBody OrdinationDTO ordinationDTO) {
        if(ordinationDTO != null && ordinationDTO.getName() != null && ordinationDTO.getNumber() != null) {

            try{
                ordinationService.createNewOrdination(ordinationDTO, clinic_id);
                return new ResponseEntity<>(ordinationDTO, HttpStatus.CREATED);
            } catch (ValidationException e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
            }
        }
        return new ResponseEntity<>("Invalid input data", HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @DeleteMapping(path="/delete/{id}")
    @PreAuthorize("hasAuthority('ADMINC')")
    public ResponseEntity deleteOrdination(@PathVariable("id") Long id) {

        if (id != null) {
            try{
                ordinationService.deleteOrdination(id);
                return new ResponseEntity<>(id, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }
        return new ResponseEntity<>("Invalid ID!", HttpStatus.BAD_REQUEST);
    }

    @PutMapping(path="/change", consumes = "application/json", produces=
            "application/json")
    @PreAuthorize("hasAuthority('ADMINC')")
    public ResponseEntity modifyOrdination(@RequestBody OrdinationDTO ordinationDTO) {

        if(ordinationDTO != null && ordinationDTO.getId() != null) {
            try{
                OrdinationDTO editOrd =
                        ordinationService.changeOrdination(ordinationDTO);
                return new ResponseEntity<>(editOrd, HttpStatus.CREATED);

            } catch (ValidationException e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }
            return new ResponseEntity<>("Invalid input data", HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @GetMapping("/search_ordinations")
    @PreAuthorize("hasAuthority('ADMINC')")
    public ResponseEntity getAllByQuery(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "number", required = false) String number,
            @RequestParam(value = "clinic_id", required = false) Long clinic_id
    ) {
        List<OrdinationDTO> ordinations = ordinationService.getByQuery(name, number, clinic_id);
        return new ResponseEntity<>(ordinations, HttpStatus.OK);
    }

    @GetMapping("/search_ordinations_with_date")
    @PreAuthorize("hasAuthority('ADMINC')")
    public ResponseEntity searchOrdinationsWithDate(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "number", required = false) String number,
            @RequestParam(value = "date", required = false) long date,
            @RequestParam(value = "clinic_id", required = false) Long clinic_id
    ) {
        List<OrdinationAvailabilityDTO> ordinations = ordinationService.getOrdinationsAvailability(name, number, date, clinic_id);
        return new ResponseEntity<>(ordinations, HttpStatus.OK);
    }
}
