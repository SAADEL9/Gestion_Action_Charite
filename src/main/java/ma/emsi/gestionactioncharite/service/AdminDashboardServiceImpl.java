package ma.emsi.gestionactioncharite.service;

import lombok.RequiredArgsConstructor;
import ma.emsi.gestionactioncharite.dto.AdminDashboardView;
import ma.emsi.gestionactioncharite.entity.*;
import ma.emsi.gestionactioncharite.repository.ActionChariteRepository;
import ma.emsi.gestionactioncharite.repository.Donrepository;
import ma.emsi.gestionactioncharite.repository.Participationrepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final UserService userService;
    private final OrganisationService organisationService;
    private final ActionChariteRepository actionChariteRepository;
    private final Donrepository donrepository;
    private final Participationrepository participationrepository;

    @Override
    public AdminDashboardView getDashboardData(String adminEmail) {
        User admin = userService.findEntityByEmail(adminEmail);
        
        if (admin.getRole() == Role.ADMIN) {
            return getGlobalDashboardData(admin);
        }
        
        Organisation organisation = organisationService.findFirstByAdminId(admin.getId())
                .orElse(null);

        if (organisation == null) {
            return AdminDashboardView.builder()
                    .adminFullName(buildAdminName(admin))
                    .organisationId(null)
                    .organisationName("Aucune organisation liee")
                    .organisationLogo(null)
                    .organisationStatus("PENDING")
                    .totalActions(0)
                    .totalDonationsAmount(0.0)
                    .totalParticipants(0)
                    .pendingDonations(0)
                    .recentActions(List.of())
                    .recentDonations(List.of())
                    .build();
        }

        long totalActions = actionChariteRepository.countByOrganisationId(organisation.getId());
        double totalDonationsAmount = safeDouble(
                donrepository.sumMontantByOrganisationIdAndStatus(organisation.getId(), StatutDon.CONFIRME)
        );
        long totalParticipants = participationrepository.countByActionChariteOrganisationId(organisation.getId());
        long pendingDonations = donrepository.countByActionChariteOrganisationIdAndStatus(
                organisation.getId(),
                StatutDon.EN_ATTENTE
        );

        List<AdminDashboardView.ActionSummary> recentActions = actionChariteRepository
                .findTop5ByOrganisationIdOrderByDateCreationDescIdDesc(organisation.getId())
                .stream()
                .map(this::toActionSummary)
                .toList();

        List<AdminDashboardView.DonationSummary> recentDonations = donrepository
                .findTop5ByActionChariteOrganisationIdOrderByDateDonDescIdDesc(organisation.getId())
                .stream()
                .map(this::toDonationSummary)
                .toList();

        return AdminDashboardView.builder()
                .adminFullName(buildAdminName(admin))
                .organisationId(organisation.getId())
                .organisationName(defaultText(organisation.getNom(), "Organisation"))
                .organisationLogo(organisation.getLogo())
                .organisationStatus(organisation.getStatus() != null ? organisation.getStatus().name() : "UNKNOWN")
                .totalActions(totalActions)
                .totalDonationsAmount(totalDonationsAmount)
                .totalParticipants(totalParticipants)
                .pendingDonations(pendingDonations)
                .recentActions(recentActions)
                .recentDonations(recentDonations)
                .build();
    }

    private AdminDashboardView getGlobalDashboardData(User admin) {
        long totalActions = actionChariteRepository.count();
        double totalDonationsAmount = safeDouble(donrepository.sumAllMontantByStatus(StatutDon.CONFIRME));
        long totalParticipants = participationrepository.count();
        long pendingDonations = donrepository.countByStatus(StatutDon.EN_ATTENTE);

        List<AdminDashboardView.ActionSummary> recentActions = actionChariteRepository.findAll().stream()
                .sorted((a1, a2) -> {
                    int dateCmp = (a2.getDateCreation() != null ? a2.getDateCreation() : LocalDate.MIN)
                            .compareTo(a1.getDateCreation() != null ? a1.getDateCreation() : LocalDate.MIN);
                    if (dateCmp != 0) return dateCmp;
                    return a2.getId().compareTo(a1.getId());
                })
                .limit(5)
                .map(this::toActionSummary)
                .toList();

        List<AdminDashboardView.DonationSummary> recentDonations = donrepository.findAll().stream()
                .sorted((d1, d2) -> {
                    int dateCmp = (d2.getDateDon() != null ? d2.getDateDon() : LocalDate.MIN)
                            .compareTo(d1.getDateDon() != null ? d1.getDateDon() : LocalDate.MIN);
                    if (dateCmp != 0) return dateCmp;
                    return d2.getId().compareTo(d1.getId());
                })
                .limit(5)
                .map(this::toDonationSummary)
                .toList();

        return AdminDashboardView.builder()
                .adminFullName(buildAdminName(admin))
                .organisationId(null)
                .organisationName("Plateforme Globale")
                .organisationLogo(null)
                .organisationStatus("ADMIN")
                .totalActions(totalActions)
                .totalDonationsAmount(totalDonationsAmount)
                .totalParticipants(totalParticipants)
                .pendingDonations(pendingDonations)
                .recentActions(recentActions)
                .recentDonations(recentDonations)
                .build();
    }

    private AdminDashboardView.ActionSummary toActionSummary(ActionCharite action) {
        double goalAmount = safeDouble(action.getObjectifCollect());
        double collectedAmount = safeDouble(action.getSommeActuelle());
        double progress = goalAmount > 0 ? Math.min((collectedAmount / goalAmount) * 100, 100) : 0;

        return AdminDashboardView.ActionSummary.builder()
                .id(action.getId())
                .title(defaultText(action.getTitre(), "Action sans titre"))
                .category(action.getCategorie() != null ? defaultText(action.getCategorie().getNom(), "-") : "-")
                .date(action.getDateCreation() != null ? action.getDateCreation().format(DATE_FORMATTER) : "-")
                .goalAmount(goalAmount)
                .collectedAmount(collectedAmount)
                .progressPercentage(progress)
                .status(action.getStatus() != null ? action.getStatus().name() : "UNKNOWN")
                .build();
    }

    private AdminDashboardView.DonationSummary toDonationSummary(Don don) {
        return AdminDashboardView.DonationSummary.builder()
                .donorName(buildDonorName(don))
                .amount(safeDouble(don.getMontant()))
                .date(don.getDateDon() != null ? don.getDateDon().format(DATE_FORMATTER) : "-")
                .paymentMethod(don.getMethodePaiement() != null ? don.getMethodePaiement().name() : "-")
                .status(don.getStatus() != null ? don.getStatus().name() : "UNKNOWN")
                .build();
    }

    private String buildAdminName(User admin) {
        String fullName = (defaultText(admin.getPrenom(), "") + " " + defaultText(admin.getNom(), "")).trim();
        return fullName.isBlank() ? admin.getEmail() : fullName;
    }

    private String buildDonorName(Don don) {
        if (don.getUser() == null) {
            return "Donateur inconnu";
        }
        String fullName = (defaultText(don.getUser().getPrenom(), "") + " " + defaultText(don.getUser().getNom(), "")).trim();
        return fullName.isBlank() ? defaultText(don.getUser().getEmail(), "Donateur inconnu") : fullName;
    }

    private String defaultText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private double safeDouble(Double value) {
        return value != null ? value : 0.0;
    }
}
