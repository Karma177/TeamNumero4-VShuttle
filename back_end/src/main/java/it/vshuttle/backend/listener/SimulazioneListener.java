package it.vshuttle.backend.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.vshuttle.backend.util.CalcolatoreConfidenza;
import it.vshuttle.backend.util.JsonToSituations;
import jakarta.websocket.Session;

import java.util.List;
import java.util.Map;

public class SimulazioneListener {

    private static volatile boolean running = false;
    private static final String SIMULATION_FILENAME = "simulazione.json";
    
    private Session currentSession;

    private final JsonToSituations parser;
    private final CalcolatoreConfidenza calcolatore;
    private final ObjectMapper objectMapper;

    public SimulazioneListener() {
        this.parser = new JsonToSituations();
        this.calcolatore = new CalcolatoreConfidenza();
        this.objectMapper = new ObjectMapper();
    }

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
        new Thread(() -> simulationRunner(SIMULATION_FILENAME)).start();
    }

    private void simulationRunner(String filename) {
        try {
            // Otteniamo una lista di situazioni dal parser 
            List<Map<String, Object>> situations = parser.toSimulations(filename);
            
            for (Map<String, Object> situation : situations) {
                if (!running) {
                    break;
                }

                // Calcola la confidenza sulla situazione ricevuta
                Map<String, Double> risultato = calcolatore.calcConfidenza(situation);
                
                // Converte il Map in JSON string "jsonResult" usando Jackson
                try {
                    String jsonResult = objectMapper.writeValueAsString(risultato);
                    // Invia al client WebSocket
                    send(jsonResult);
                } catch (JsonProcessingException ex) {
                    System.err.println("Errore nella serializzazione del JSON: " + ex.getMessage());
                }

                try {
                    Thread.sleep(1000); // Pausa di 1 secondo tra un invio e l'altro
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Errore generale durante la simulazione: " + e.getMessage());
        }
    }

    public void pause() {
        running = false;
    }

    public void enable() {
        running = true;
    }
}
