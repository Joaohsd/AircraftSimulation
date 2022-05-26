package com.api.aircraftsimulationapi.controller;

import com.api.aircraftsimulationapi.model.entities.Aircraft;
import com.api.aircraftsimulationapi.model.entities.Parameter;
import com.api.aircraftsimulationapi.model.helpers.dto.AircraftDTO;
import com.api.aircraftsimulationapi.model.services.AircraftService;
import com.api.aircraftsimulationapi.model.services.ParameterService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/aircrafts")
public class AircraftController {
    private final AircraftService aircraftService;
    private final ParameterService parameterService;

    public AircraftController(AircraftService aircraftService, ParameterService parameterService) {
        this.aircraftService = aircraftService;
        this.parameterService = parameterService;
    }

    //Register ONE AIRCRAFT
    @PostMapping
    public ResponseEntity<Object> saveAircraft(@RequestBody @Valid AircraftDTO aircraftDTO){
        if(aircraftService.existsByAircraftCode(aircraftDTO.getAircraftCode()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("ERROR: Aircraft Code already exists");
        var aircraft = new Aircraft();
        BeanUtils.copyProperties(aircraftDTO,aircraft);
        aircraft.setAicraftCode(aircraftDTO.getAircraftCode());
        aircraft.setNumParameters(0);
        return ResponseEntity.status(HttpStatus.CREATED).body(aircraftService.save(aircraft));
    }

    //Get ALL AIRCRAFTS
    @GetMapping
    public ResponseEntity<Object> getAllAircrafts(){
        return ResponseEntity.status(HttpStatus.OK).body(aircraftService.getAllAircrafts());
    }

    //Get ONE AIRCRAFT
    @GetMapping("/{aircraftCode}")
    public ResponseEntity<Object> getOneAircraft(@PathVariable(value = "aircraftCode") String aircraftCode){
        Optional<Aircraft> aircraftOptional = aircraftService.findByAircraftCode(aircraftCode);
        if(!aircraftOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aircraft not found");
        }
        return ResponseEntity.status(HttpStatus.FOUND).body(aircraftOptional.get());
    }

    //Get ALL PARAMETERS from ONE AIRCRAFT
    @GetMapping("/{aircraftCode}/parameters")
    public ResponseEntity<Object> getAllParametersFromOneAircraft(@PathVariable(value = "aircraftCode") String aircraftCode){
        Optional<Aircraft> aircraftOptional = aircraftService.findByAircraftCode(aircraftCode);
        if(!aircraftOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aircraft not found");
        }
        return ResponseEntity.status(HttpStatus.FOUND).body(aircraftOptional.get().getParameters());
    }

    //Get ONE PARAMETER from ONE AIRCRAFT
    @GetMapping("/{aircraftCode}/parameters/{parameterCode}")
    public ResponseEntity<Object> getOneParameterFromAircraft(@PathVariable(value = "aircraftCode") String aircraftCode,
                                                    @PathVariable(value= "parameterCode") String parameterCode){
        Optional<Aircraft> aircraftOptional = aircraftService.findByAircraftCode(aircraftCode);
        if(!aircraftOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aircraft not found");
        }
        if(!parameterService.existsByParameterCodeAndAircraft(parameterCode,aircraftOptional.get())){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parameter not found");
        }
        return ResponseEntity.status(HttpStatus.FOUND).body(aircraftService.findByParameterCodeAndAircraft(parameterCode,aircraftOptional.get()));
    }
}