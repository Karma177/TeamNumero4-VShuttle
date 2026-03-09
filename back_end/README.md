# Backend WebSocket Service

Servizio WebSocket locale per ricevere letture da 3 sensori e restituire la confidenza del cartello stradale.

## Requisiti

- Java 17+
- Maven 3.9+

## Avvio locale

1. Spostati nella cartella `back_end`.
2. Avvia con:

```bash
mvn compile exec:java
```

Il server espone il WebSocket su:

```text
ws://localhost:8080/ws/sign-confidence
```

Per cambiare porta:

```bash
WS_PORT=9090 mvn compile exec:java
```

## Payload in ingresso

```json
{
  "signId": "STOP",
  "sensors": [0.91, 0.85, 0.88]
}
```

## Risposta di successo

```json
{
  "type": "confidence_result",
  "signId": "STOP",
  "confidence": 0.8612,
  "timestamp": "2026-03-09T12:00:00Z"
}
```

## Risposta di errore

```json
{
  "type": "error",
  "message": "Il campo sensors deve contenere esattamente 3 valori.",
  "timestamp": "2026-03-09T12:00:00Z"
}
```