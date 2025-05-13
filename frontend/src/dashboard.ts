import 'bootstrap/dist/css/bootstrap.min.css';
import '@fortawesome/fontawesome-free/css/all.min.css';

import checkToken from './util/checkToken';
import initSkinViewer from './skin/skin';

async function init() {
    if (!checkToken(
        localStorage.getItem("token") || "",
        localStorage.getItem("username") || ""
    )) {
        console.error("Invalid token or username.");
        window.location.href = "/login.html";
        return;
    }

    initSkinViewer();

    const username = localStorage.getItem("username");

    const usernameSpan = document.getElementById("username");
    usernameSpan!.textContent = username || "Unknown User";
}

document.addEventListener('DOMContentLoaded', init);
