import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe utility per il parsing del JSON di input per il progetto V-Shuttle.
 */
public class JsonToSituations {

    // ObjectMapper è thread-safe dopo la configurazione iniziale, ottima pratica istanziarlo una volta sola
    private final ObjectMapper objectMapper;

    public JsonToSituations() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Legge un file JSON e lo mappa in una lista di situazioni.
     *
     * @param nomeFileJson Il percorso del file JSON da parsare.
     * @return Una lista di mappe rappresentanti le situazioni.
     */
    public List<Map<String, Object>> toSimulations(String nomeFileJson) {
        List<Map<String, Object>> simulations = new ArrayList<>();
        File jsonFile = new File(nomeFileJson);

        // Fail-fast: controllo immediato sull'esistenza del file
        if (!jsonFile.exists() || !jsonFile.isFile()) {
            throw new IllegalArgumentException("Il file specificato non esiste o non è valido: " + nomeFileJson);
        }

        try {
            JsonNode rootArray = objectMapper.readTree(jsonFile);
            
            if (rootArray.isArray()) {
                for (JsonNode scenarioNode : rootArray) {
                    simulations.add(parseSingleScenario(scenarioNode));
                }
            } else {
                throw new IllegalArgumentException("Il file JSON non contiene un array radice valido.");
            }
        } catch (IOException e) {
            // Gestione dell'eccezione: in un vero ambiente enterprise si logga con SLF4J/Logback
            System.err.println("Errore di I/O durante la lettura del file: " + e.getMessage());
            throw new RuntimeException("Impossibile processare il file JSON delle simulazioni.", e);
        }

        return simulations;
    }

    /**
     * Metodo di supporto per parsare il singolo nodo "scenario".
     */
    private Map<String, Object> parseSingleScenario(JsonNode scenarioNode) {
        Map<String, Object> scenarioMap = new HashMap<>();

        // Mappatura "id_scenario" in "id"
        if (scenarioNode.has("id_scenario")) {
            scenarioMap.put("id", scenarioNode.get("id_scenario").asInt());
        }

        // Delegazione del parsing dei sensori
        if (scenarioNode.has("sensori")) {
            scenarioMap.put("sensori", parseSensori(scenarioNode.get("sensori")));
        }

        // Inclusi per rispettare i criteri B.5 e B.6 del PDF
        if (scenarioNode.has("orario_rilevamento") && !scenarioNode.get("orario_rilevamento").isNull()) {
            scenarioMap.put("orario_rilevamento", scenarioNode.get("orario_rilevamento").asText());
        }
        if (scenarioNode.has("giorno_settimana") && !scenarioNode.get("giorno_settimana").isNull()) {
            scenarioMap.put("giorno_settimana", scenarioNode.get("giorno_settimana").asText());
        }

        return scenarioMap;
    }

    /**
     * Metodo di supporto per parsare il blocco "sensori".
     */
    private Map<String, Map<String, Object>> parseSensori(JsonNode sensoriNode) {
        Map<String, Map<String, Object>> sensoriMap = new HashMap<>();
        String[] nomiSensori = {"camera_frontale", "camera_laterale", "V2I_receiver"};

        for (String nomeSensore : nomiSensori) {
            if (sensoriNode.has(nomeSensore)) {
                sensoriMap.put(nomeSensore, parseSingleSensor(sensoriNode.get(nomeSensore)));
            }
        }

        return sensoriMap;
    }

    /**
     * Metodo di supporto per estrarre dati dal singolo sensore, con gestione dei valori nulli.
     */
    private Map<String, Object> parseSingleSensor(JsonNode sensorNode) {
        Map<String, Object> dataMap = new HashMap<>();

        // Gestione sicura dei null per il criterio "Sensori Offline" (B.3)
        if (sensorNode.has("testo") && !sensorNode.get("testo").isNull()) {
            dataMap.put("testo", sensorNode.get("testo").asText());
        } else {
            dataMap.put("testo", null);
        }

        if (sensorNode.has("confidenza") && !sensorNode.get("confidenza").isNull()) {
            dataMap.put("confidenza", sensorNode.get("confidenza").asDouble());
        } else {
            dataMap.put("confidenza", null);
        }

        return dataMap;
    }
}