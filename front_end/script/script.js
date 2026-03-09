document.addEventListener("DOMContentLoaded", () => {
    // Selezioniamo il bottone di inizio
    const btnStartSim = document.querySelector("#startsim button");

    // Impostiamo la modalità iniziale: Manuale
    impostaModalita("manuale");

    // Event listener per il click
    btnStartSim.addEventListener("click", () => {
        console.log("Passaggio a modalità Simulazione...");
        impostaModalita("sim");
        avviaAscoltoServer();
    });
});

/**
 * Gestisce la visibilità degli elementi in base alla modalità
 * @param {string} modo - 'manuale' o 'sim'
 */
function impostaModalita(modo) {
    const elementiSim = document.querySelectorAll(".sim");
    const elementiManuale = document.querySelectorAll(".manuale");

    if (modo === "manuale") {
        elementiSim.forEach(el => el.classList.add("hidden"));
        elementiManuale.forEach(el => el.classList.remove("hidden"));
    } else {
        elementiSim.forEach(el => el.classList.remove("hidden"));
        elementiManuale.forEach(el => el.classList.add("hidden"));
    }
}

/**
 * Funzione per l'ascolto del server (Esempio WebSocket o Fetch)
 */
function avviaAscoltoServer() {
    console.log("Connessione al server stabilita. In ascolto dati...");
    
    // Esempio di logica WebSocket (commentata)
    /*
    const socket = new WebSocket('ws://indirizzo-tuo-server:8080');
    socket.onmessage = (event) => {
        const data = JSON.parse(event.data);
        console.log("Dati ricevuti:", data);
        // Qui aggiorneresti il div #stato con i dati della macchina
    };
    */
    
    // Simulazione visiva del server che lavora
    const statusDiv = document.querySelector("#stato .sim");
    statusDiv.innerHTML = "📡 Connesso al server... Ricezione dati in corso";
}