package ma.emsi.gestionactioncharite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class AdminDashboardView {

    private final String adminFullName;
    private final Long organisationId;
    private final String organisationName;
    private final String organisationLogo;
    private final String organisationStatus;
    private final long totalActions;
    private final double totalDonationsAmount;
    private final long totalParticipants;
    private final long pendingDonations;
    private final List<ActionSummary> recentActions;
    private final List<DonationSummary> recentDonations;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ActionSummary {
        private final Long id;
        private final String title;
        private final String category;
        private final String date;
        private final double goalAmount;
        private final double collectedAmount;
        private final double progressPercentage;
        private final String status;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class DonationSummary {
        private final String donorName;
        private final double amount;
        private final String date;
        private final String paymentMethod;
        private final String status;
    }
}
