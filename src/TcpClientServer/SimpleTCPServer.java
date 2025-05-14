package TcpClientServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ScatteringByteChannel;
import java.util.Scanner;

public class SimpleTCPServer {

    private static volatile boolean running = true;

    public static void main(String[] args) throws IOException {
        int port = 8080;
        ServerSocket serverSocket = new ServerSocket(port);

        Thread consoleThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (running) {
                String command = scanner.nextLine();
                if ("stop".equalsIgnoreCase(command.trim())) {
                    running = false;
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            scanner.close();
        });
        consoleThread.start();

        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected from " + clientSocket.getInetAddress());

                new Thread(() -> {
                    try {
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                        String inputLine;

                        while ((inputLine = in.readLine()) != null) {
                            System.out.println("Received from client: " + inputLine);
                            out.println("Echo from server: " + inputLine);
                        }

                        System.out.println("Client disconnected: " + clientSocket.getInetAddress());
                        clientSocket.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();

            } catch (IOException e) {
                if (running) {
                    e.printStackTrace();
                } else {
                    System.out.println("Server stopped.");
                }
            }
        }
    }
}