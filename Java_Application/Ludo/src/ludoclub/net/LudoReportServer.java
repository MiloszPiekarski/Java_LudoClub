package ludoclub.net;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.concurrent.*;

public class LudoReportServer {
    public static final int PORT = 5050;
    private static final String RAPORT_FILE = "raporty_serwer.txt";
    private static boolean running = true;

    public static void main(String[] args) {
        ExecutorService pool = Executors.newCachedThreadPool();

        System.out.println("Serwer LUDO CLUB startuje na porcie " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (running) {
                Socket client = serverSocket.accept();
                pool.execute(new ClientHandler(client));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }
    }

    // Klasa wewnętrzna do obsługi klienta (przykład: klasa wewnętrzna + wielowątkowość!)
    private static class ClientHandler implements Runnable {
        private final Socket socket;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            System.out.println("Połączenie od: " + socket.getInetAddress());
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
            ) {
                StringBuilder received = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    received.append(line).append("\n");
                    if (line.equals("END_OF_REPORT")) break;
                }
                String report = received.toString();
                // Dopisz do pliku na serwerze
                saveToFile(report);

                // Potwierdzenie
                out.println("OK: raport odebrany. " + LocalDateTime.now());
                System.out.println("Odebrano raport:\n" + report);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try { socket.close(); } catch (IOException ignored) {}
            }
        }

        private void saveToFile(String report) {
            try (FileWriter fw = new FileWriter(RAPORT_FILE, true)) {
                fw.write("==== Nowy raport: " + LocalDateTime.now() + " ====\n");
                fw.write(report + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}