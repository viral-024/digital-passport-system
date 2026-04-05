package com.passport.system.service;

import com.passport.system.entity.Appointment;
import com.passport.system.entity.PassportApplication;
import com.passport.system.repository.AppointmentRepository;
import com.passport.system.repository.ApplicationRepository;
import org.springframework.stereotype.Service;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ApplicationRepository applicationRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              ApplicationRepository applicationRepository) {
        this.appointmentRepository = appointmentRepository;
        this.applicationRepository = applicationRepository;
    }

    public Appointment scheduleAppointment(Long appId, Appointment appointment) {
        PassportApplication app = applicationRepository.findById(appId).orElseThrow();
        appointment.setApplication(app);
        return appointmentRepository.save(appointment);
    }

    public Appointment updateAppointment(Long id, Appointment appointment) {
        Appointment existing = appointmentRepository.findById(id).orElseThrow();

        existing.setAppointmentDate(appointment.getAppointmentDate());
        existing.setTimeSlot(appointment.getTimeSlot());

        if (appointment.getStatus() != null) {
            existing.setStatus(appointment.getStatus());
        }

        return appointmentRepository.save(existing);
    }
}
