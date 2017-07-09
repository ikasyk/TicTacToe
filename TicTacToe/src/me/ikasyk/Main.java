package me.ikasyk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    static final int PORT = 1235;
    static final String SERVER_ADDRESS = "127.0.0.1";

    public static void main(String[] args) throws IOException, InterruptedException {
        PlayerSessionClient playerSession = new PlayerSessionClient();
        playerSession.connect();
    }

    private static class PlayerSessionClient {
        private BufferedReader in;
        private PrintWriter out;

        public void connect() throws IOException, InterruptedException {
            Socket socket = new Socket(SERVER_ADDRESS, PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            out.println("/hello");
            socketMessagePrint();

            // To show notifications in real time
            ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);
            scheduledThreadPool.scheduleAtFixedRate(() -> {
                try {
                    socketMessagePrint();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }, 0, 1, TimeUnit.SECONDS);

            while (true) {
                out.println(userInput.readLine());
            }
        }

        private void socketMessagePrint() throws IOException, InterruptedException {
            int lines = in.read();

            if (lines > 0) {
                StringBuilder sb = new StringBuilder("");
                for (int i = 0; i < lines; i++) {
                    sb.append(in.readLine());
                    if (lines - 1 != i)
                        sb.append("\n");
                }
                System.out.println(sb.toString());
            }
        }

    }
}
