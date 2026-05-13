package ma.emsi.gestionactioncharite.view;

public record HomeOrganisationView(
        Long id,
        String name,
        String description,
        String logo,
        String coverImage,
        String city,
        long actionsCount
) {
}
