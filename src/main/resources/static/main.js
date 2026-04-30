
function applySavedMode() {
    const savedMode = localStorage.getItem("mode");

    if (savedMode === "dark") {
        document.body.classList.add("dark");
    }
}

function toggleMode() {
    document.body.classList.toggle("dark");

    const isDark = document.body.classList.contains("dark");
    localStorage.setItem("mode", isDark ? "dark" : "light");

    updateModeIcon();
}

function updateModeIcon() {
    const btn = document.getElementById("modeBtn");
    if (!btn) return;

    const isDark = document.body.classList.contains("dark");
    btn.innerText = isDark ? "☀️" : "🌙";
}

document.addEventListener("DOMContentLoaded", () => {
    applySavedMode();
    updateModeIcon();
});

function authHeaders() {
    const token = localStorage.getItem("token");
    return {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`
    };
}


function isLiked(skillId) {
    const likes = JSON.parse(localStorage.getItem("likedSkills") || "[]");
    return likes.includes(skillId);
}

function saveLike(skillId) {
    const likes = JSON.parse(localStorage.getItem("likedSkills") || "[]");
    if (!likes.includes(skillId)) {
        likes.push(skillId);
        localStorage.setItem("likedSkills", JSON.stringify(likes));
    }
}

async function likeSkill(skillId) {
    if (isLiked(skillId)) {
        alert("You already liked this!");
        return;
    }

    const token = localStorage.getItem("token");
    if (!token) {
        alert("Please login to like!");
        navigate('login.html');
        return;
    }

    try {
        const res = await fetch(`http://localhost:8080/api/skills/${skillId}/increase`, {
            method: "PUT",
            headers: authHeaders()
        });

        if (res.ok) {
            const updated = await res.json();
            document.getElementById("popCount").textContent = updated.popularity || 0;
            saveLike(skillId);


            const likeBtn = document.getElementById("likeBtn");
            if (likeBtn) {
                likeBtn.disabled = true;
                likeBtn.style.opacity = "0.5";
            }
        }
    } catch (e) {
        console.error("Like failed", e);
    }

}
