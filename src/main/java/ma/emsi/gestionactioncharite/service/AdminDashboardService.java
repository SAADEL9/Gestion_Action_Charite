package ma.emsi.gestionactioncharite.service;

import ma.emsi.gestionactioncharite.dto.AdminDashboardView;

public interface AdminDashboardService {
    AdminDashboardView getDashboardData(String adminEmail);
}
