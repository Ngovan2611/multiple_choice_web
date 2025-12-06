const contactBtn = document.getElementById("contactBtn");
const contactPopup = document.getElementById("contactPopup");

const studySupportBtn = document.getElementById("studySupportBtn");
const otherSupportBtn = document.getElementById("otherSupportBtn");

const qrOverlay = document.getElementById("qrOverlay");
const closeQr = document.getElementById("closeQr");

contactBtn.addEventListener("click", () => {
    contactPopup.classList.toggle("active");
});

studySupportBtn.addEventListener("click", () => {
    window.open("https://gemini.google.com", "_blank");
});

otherSupportBtn.addEventListener("click", () => {
    qrOverlay.style.display = "flex";
    contactPopup.classList.remove("active");
});

closeQr.addEventListener("click", () => {
    qrOverlay.style.display = "none";
});

qrOverlay.addEventListener("click", (e) => {
    if (e.target === qrOverlay) {
        qrOverlay.style.display = "none";
    }
});