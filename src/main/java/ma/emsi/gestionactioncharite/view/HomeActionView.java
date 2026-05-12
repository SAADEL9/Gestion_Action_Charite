package ma.emsi.gestionactioncharite.view;

public record HomeActionView(
        Long id,
        String image,
        String category,
        String title,
        String location,
        int progress,
        String progressLabel,
        String raised,
        String goal
) {
}
