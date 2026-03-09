package it.vshuttle.backend;

import it.vshuttle.backend.listener.SimulazioneListener;
import jakarta.websocket.OnMessage;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.glassfish.tyrus.server.Server;
import java.util.concurrent.CountDownLatch;

@ServerEndpoint("/")
public class GuidaAutonomaWS {

    // Riferimento al listener
    private static final SimulazioneListener simulazioneListener = new SimulazioneListener();

    public static void main(String[] args) {
        int port = Integer.parseInt(System.getenv().getOrDefault("WS_PORT", "8080"));
        Server server = new Server("0.0.0.0", port, "/", null, GuidaAutonomaWS.class);

        try {
            server.start();
            System.out.println("Server GuidaAutonomaWS avviato su ws://localhost:" + port + "/");
            System.out.println("Premi CTRL+C per fermare il server.");
            new CountDownLatch(1).await();
        } catch (Exception ex) {
            ex.printStackTrace();
        } 
    }

    @OnMessage
    public void onMessage(String messaggio, Session session) {
        if ("inizioSimulazione".equals(messaggio)) {
            // Deleghiamo l'ascolto di inizioSimulazione al listener
            simulazioneListener.onInizioSimulazione(session);
        } else if ("Override".equals(messaggio)) {
            // Mettiamo in pausa la simulazione e gestiamo l'override
            simulazioneListener.pause();
        } else if ("stopOverride".equals(messaggio)) {
            // Riattiviamo la simulazione
            simulazioneListener.enable();
        }
    }
}
