package com.passport.system.controller;

import com.passport.system.dto.ResponseDTO;
import com.passport.system.entity.Appointment;
import com.passport.system.entity.Role;
import com.passport.system.entity.User;
import com.passport.system.repository.AppointmentRepository;
import com.passport.system.security.CurrentUserService;
import com.passport.system.service.AppointmentService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AppointmentRepository appointmentRepository;
    private final CurrentUserService currentUserService;

    public AppointmentController(AppointmentService appointmentService,
                                 AppointmentRepository appointmentRepository,
                                 CurrentUserService currentUserService) {
        this.appointmentService = appointmentService;
        this.appointmentRepository = appointmentRepository;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    public ResponseDTO create(@RequestBody Appointment appointment,
                              @RequestParam Long appId) {
        return new ResponseDTO("Scheduled",
                appointmentService.scheduleAppointment(appId, appointment));
    }

    @GetMapping
    public ResponseDTO getAll(Authentication authentication) {
        User currentUser = currentUserService.getCurrentUser(authentication);
        List<Appointment> list = currentUser.getRole() == Role.CITIZEN
                ? appointmentRepository.findByApplicationUserId(currentUser.getId())
                : appointmentRepository.findAll();
        return new ResponseDTO("Appointments", list);
    }

    @PutMapping("/{id}")
    public ResponseDTO update(@PathVariable Long id,
                              @RequestBody Appointment appointment) {
        return new ResponseDTO("Updated",
                appointmentService.updateAppointment(id, appointment));
    }
}
