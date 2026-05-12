document.addEventListener("DOMContentLoaded", () => {
    const body = document.body;
    const sidebar = document.querySelector(".app-sidebar");
    const overlay = document.querySelector("[data-sidebar-overlay]");
    const toggleButton = document.querySelector("[data-sidebar-toggle]");

    const closeSidebar = () => body.classList.remove("sidebar-open");
    const openSidebar = () => body.classList.add("sidebar-open");

    if (toggleButton && sidebar && overlay) {
        toggleButton.addEventListener("click", () => {
            if (body.classList.contains("sidebar-open")) {
                closeSidebar();
                return;
            }
            openSidebar();
        });

        overlay.addEventListener("click", closeSidebar);
        window.addEventListener("resize", () => {
            if (window.innerWidth >= 1200) {
                closeSidebar();
            }
        });
    }

    document.querySelectorAll(".amount-chip").forEach((button) => {
        button.addEventListener("click", () => {
            document.querySelectorAll(".amount-chip").forEach((chip) => chip.classList.remove("is-active"));
            button.classList.add("is-active");
        });
    });
});
