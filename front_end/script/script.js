/**
 * Scambia la visibilità tra il div `#action` e il div `#controlli`.
 * Se viene passato `true` esplicito mostra il `#action` e nasconde i controlli.
 * Se viene passato `false` fa l'opposto. Se non viene passato alcun argomento
 * esegue un toggle basato sull'attuale stato (utile da console).
 *
 * @param {boolean} [mostraAction] - Facoltativo. Se true mostra GO, se false mostra
 *                                   i pulsanti manuali. Se omesso viene invertito lo stato corrente.
 */
function toggleVisibility(mostraAction) {
    const actionElement = document.getElementById('action');
    const controlliElement = document.getElementById('controlli');

    // se non è specificato, inverti lo stato corrente
    if (typeof mostraAction === 'undefined') {
        mostraAction = actionElement.classList.contains('hidden');
    }

    if (mostraAction) {
        // mostra il "GO" e nasconde i pulsanti di override
        actionElement.classList.remove('hidden');
        controlliElement.classList.add('hidden');
    } else {
        // nasconde il "GO" e mostra i pulsanti di override
        actionElement.classList.add('hidden');
        controlliElement.classList.remove('hidden');
    }
    console.log('action classes:', actionElement.classList);
    console.log('controlli classes:', controlliElement.classList);
}

// WebSocket client for simulazione
let ws;

function initWebSocket() {
    if (ws && (ws.readyState === WebSocket.OPEN || ws.readyState === WebSocket.CONNECTING)) {
        return; // già connesso o in connessione
    }

    // imposta l'URL corretto in base all'host corrente
    const host = window.location.hostname || 'localhost';
    const port = 8080; // deve corrispondere al server back-end
    const url = `ws://${host}:${port}/`;

    ws = new WebSocket(url);

    ws.addEventListener('open', () => {
        console.log('WebSocket aperto su', url);
        appendStatus('Connesso al server');
    });

    ws.addEventListener('message', event => {
        console.log('Messaggio ricevuto:', event.data);
        appendStatus(event.data);
    });

    ws.addEventListener('close', () => {
        console.log('WebSocket chiuso');
        appendStatus('Connessione chiusa');
    });

    ws.addEventListener('error', err => {
        console.error('Errore WebSocket', err);
        appendStatus('Errore di connessione');
    });
}

/**
 * Invia il comando di inizio simulazione al server.
 * Se la connessione non è aperta, la apre prima.
 */
function startSimulation() {
    if (!ws || ws.readyState !== WebSocket.OPEN) {
        initWebSocket();
        // attendo la connessione prima di inviare
        ws.addEventListener('open', () => {
            ws.send('inizioSimulazione');
        }, { once: true });
    } else {
        ws.send('inizioSimulazione');
    }
}

/**
 * Aggiunge un messaggio nella sezione #stato (sovrascrive o accoda a piacere)
 */
function appendStatus(text) {
    const stato = document.getElementById('stato');
    // sostituisco il contenuto esistente con il nuovo messaggio
    stato.textContent = text;
}

// apertura automatica al caricamento della pagina
window.addEventListener('load', () => {
    initWebSocket();
    const actionBtn = document.getElementById('action');
    if (actionBtn) {
        actionBtn.addEventListener('click', startSimulation);
    }
});


