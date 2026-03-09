package it.vshuttle.backend.listener;

import jakarta.websocket.Session;
import java.util.Map;

public class SimulazioneListener {

    private static volatile boolean running = false;
    private Session currentSession;

    // Queste dichiarazioni di interfaccia non servono realmente ma le aggiungo
    // finché non creerai la vera classe parser e il vero calcolatore
    // per non far spaccare la compilazione
    public interface ParserMock {
        Map<String, Object> toSituations(String filename);
    }
    public interface CalcolatoreMock {
        String calcConfidenza(Object elemento);
    }

    private ParserMock parser;
    private CalcolatoreMock calcolatoreMock;

    // Ascolta e gestisce la richiesta di inizio simulazione
    public void onInizioSimulazione(Session session) {
        this.currentSession = session;
        startSimulation();
    }

    public void send(String json) {
        if (currentSession != null && currentSession.isOpen()) {
            try {
                currentSession.getBasicRemote().sendText(json);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void startSimulation() {
        running = true;
        // Suppongo un filename di prova 
        String filename = "simulazione.json"; 
        
        new Thread(() -> simulationRunner(filename)).start();
    }

    private void simulationRunner(String filename) {
        if (parser == null || calcolatoreMock == null) {
            System.out.println("Attesa dell'effettiva implementazione di parser/calcolatore");
            return;
        }

        Map<String, Object> situations = parser.toSituations(filename);
        for (Map.Entry<String, Object> entry : situations.entrySet()) {
            if (!running) {
                break;
            }

            // Calcola la confidenza sulla situazione ricevuta
            String jsonResult = calcolatoreMock.calcConfidenza(entry.getValue());
            
            // Invia al client WebSocket
            send(jsonResult);

            try {
                Thread.sleep(4000); // utile per scandire i messaggi, modificalo se ti serve
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void pause() {
        running = false;
    }

    public void enable() {
        running = true;
    }
}
