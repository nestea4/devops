package com.example.busservice.service;/*
    @author User
    @project lab4
    @class BusService
    @version 1.0.0
    @since 28.04.2025 - 15.48 
*/

import com.example.busservice.exeption.DuplicateResourceException;
import com.example.busservice.exeption.InvalidStateException;
import com.example.busservice.exeption.ResourceNotFoundException;
import com.example.busservice.model.Bus;
import com.example.busservice.repository.BusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class BusService {

    private final BusRepository busRepository;

    @Autowired
    public BusService(BusRepository busRepository) {
        this.busRepository = busRepository;
    }

    public List<Bus> findAll() {
        return busRepository.findAll();
    }

    public Bus findById(Long id) {
        return busRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found with id: " + id));
    }

    public List<Bus> findByType(Bus.BusType type) {
        List<Bus> buses = busRepository.findByType(type);
        if (buses.isEmpty()) {
            throw new ResourceNotFoundException("No buses found with type: " + type);
        }
        return buses;
    }

    public Bus findByNumber(String number) {
        Bus bus = busRepository.findByNumber(number);
        if (bus == null) {
            throw new ResourceNotFoundException("Bus not found with number: " + number);
        }
        return bus;
    }

    public Bus save(Bus bus) {
        validateBus(bus);

        Bus existingBus = busRepository.findByNumber(bus.getNumber());
        if (existingBus != null && (bus.getId() == null || !bus.getId().equals(existingBus.getId()))) {
            throw new DuplicateResourceException("Bus with number " + bus.getNumber() + " already exists");
        }

        return busRepository.save(bus);
    }

    public Bus update(Long id, Bus bus) {
        if (!busRepository.existsById(id)) {
            throw new ResourceNotFoundException("Bus not found with id: " + id);
        }

        validateBus(bus);

        Bus existingBus = busRepository.findByNumber(bus.getNumber());
        if (existingBus != null && !existingBus.getId().equals(id)) {
            throw new DuplicateResourceException("Bus with number " + bus.getNumber() + " already exists");
        }

        bus.setId(id);
        return busRepository.save(bus);
    }

    public void deleteById(Long id) {
        if (!busRepository.existsById(id)) {
            throw new ResourceNotFoundException("Bus not found with id: " + id);
        }

        busRepository.deleteById(id);
    }

    private void validateBus(Bus bus) {
        if (bus.getNumber() == null || bus.getNumber().trim().isEmpty()) {
            throw new InvalidStateException("Bus number cannot be empty");
        }

        if (bus.getType() == null) {
            throw new InvalidStateException("Bus type must be specified");
        }
    }

    public Bus.BusType getBusTypeByName(String typeName) {
        try {
            return Bus.BusType.valueOf(typeName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidStateException("Invalid bus type: " + typeName + ". Valid types are: " +
                    Arrays.toString(Bus.BusType.values()));
        }
    }
}