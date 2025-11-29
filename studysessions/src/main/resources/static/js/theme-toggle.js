document.addEventListener("DOMContentLoaded", () => {
    const body = document.body;
    const toggleBtn = document.getElementById("themeToggle");

    // Get saved theme from localStorage
    const savedTheme = localStorage.getItem("theme");

    if (savedTheme === "dark") {
        body.classList.add("dark-theme");
        if (toggleBtn) toggleBtn.textContent = "☀️";
    }

    if (!toggleBtn) return;

    toggleBtn.addEventListener("click", () => {
        const isDark = body.classList.toggle("dark-theme");

        if (isDark) {
            localStorage.setItem("theme", "dark");
            toggleBtn.textContent = "☀️";
        } else {
            localStorage.setItem("theme", "light");
            toggleBtn.textContent = "🌙";
        }
    });
});
