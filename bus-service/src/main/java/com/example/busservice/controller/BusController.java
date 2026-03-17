package com.example.busservice.controller;/*
    @author User
    @project lab4
    @class BusController
    @version 1.0.0
    @since 28.04.2025 - 15.45 
*/

import com.example.busservice.DTO.BusDTO;
import com.example.busservice.DTO.BusResponseDTO;
import com.example.busservice.mappers.BusMapper;
import com.example.busservice.model.Bus;
import com.example.busservice.service.BusService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buses")
public class BusController {

    private final BusService busService;
    private final BusMapper busMapper;

    @Autowired
    public BusController(BusService busService, BusMapper busMapper) {
        this.busService = busService;
        this.busMapper = busMapper;
    }

    @GetMapping
    public ResponseEntity<List<BusResponseDTO>> getAllBuses() {
        List<Bus> buses = busService.findAll();
        List<BusResponseDTO> busResponseDTOs = busMapper.toBusResponseList(buses);
        return ResponseEntity.ok(busResponseDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusResponseDTO> getBusById(@PathVariable Long id) {
        Bus bus = busService.findById(id);
        return ResponseEntity.ok(busMapper.toBusResponse(bus));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<BusResponseDTO>> getBusesByType(@PathVariable String type) {
        Bus.BusType busType = busService.getBusTypeByName(type);
        List<Bus> buses = busService.findByType(busType);
        List<BusResponseDTO> busResponseDTOs = busMapper.toBusResponseList(buses);
        return ResponseEntity.ok(busResponseDTOs);
    }

    @GetMapping("/number/{number}")
    public ResponseEntity<BusResponseDTO> getBusByNumber(@PathVariable String number) {
        Bus bus = busService.findByNumber(number);
        BusResponseDTO busResponseDTO = busMapper.toBusResponse(bus);
        return ResponseEntity.ok(busResponseDTO);
    }

    @PostMapping
    public ResponseEntity<BusResponseDTO> createBus(@Valid @RequestBody BusDTO busDTO) {
        Bus bus = busMapper.toBus(busDTO);
        Bus savedBus = busService.save(bus);
        BusResponseDTO busResponseDTO = busMapper.toBusResponse(savedBus);
        return ResponseEntity.status(HttpStatus.CREATED).body(busResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BusResponseDTO> updateBus(@PathVariable Long id, @Valid @RequestBody BusDTO busDTO) {
        Bus bus = busMapper.toBus(busDTO);
        Bus updatedBus = busService.update(id, bus);
        BusResponseDTO busResponseDTO = busMapper.toBusResponse(updatedBus);
        return ResponseEntity.ok(busResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBus(@PathVariable Long id) {
        busService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}