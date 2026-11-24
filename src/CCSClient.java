import java.io.*;
import java.net.*;
import java.util.Random;

public class CCSClient {
    private static final String DISCOVER_MESSAGE = "CCS DISCOVER";
    private static final String FOUND_MESSAGE = "CCS FOUND";

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Użycie: java CCSClient <port>");
            return;
        }

        int port = Integer.parseInt(args[0]);

        try {
            // Odkrywanie serwera za pomocą UDP
            InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255");
            DatagramSocket udpSocket = new DatagramSocket();
            udpSocket.setBroadcast(true);

            byte[] discoverData = DISCOVER_MESSAGE.getBytes();
            DatagramPacket discoverPacket = new DatagramPacket(discoverData, discoverData.length, broadcastAddress, port);

            System.out.println("Wysyłanie wiadomości DISCOVER...");
            udpSocket.send(discoverPacket);

            byte[] buffer = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);

            udpSocket.receive(responsePacket);
            String response = new String(responsePacket.getData(), 0, responsePacket.getLength());

            if (!response.equals(FOUND_MESSAGE)) {
                System.out.println("Niepoprawna odpowiedź od serwera: " + response);
                return;
            }

            InetAddress serverAddress = responsePacket.getAddress();
            System.out.println("Serwer znaleziony pod adresem: " + serverAddress.getHostAddress());
            udpSocket.close();

            // Połączenie z serwerem za pomocą TCP
            Socket tcpSocket = new Socket(serverAddress, port);
            PrintWriter out = new PrintWriter(tcpSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));

            Random random = new Random();

            // Wysyłanie operacji
            for (int i = 0; i < 10; i++) { // Wyślij 10 operacji
                String operation = generateRandomOperation(random);
                System.out.println("Wysyłanie: " + operation);
                out.println(operation);

                String responseFromServer = in.readLine();
                System.out.println("Odpowiedź od serwera: " + responseFromServer);

                // Losowe opóźnienie między operacSjami
                Thread.sleep(random.nextInt(2000) + 500);
            }

            tcpSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String generateRandomOperation(Random random) {
        String[] operations = {"ADD", "SUB", "MUL", "DIV"};
        String operation = operations[random.nextInt(operations.length)];
        int arg1 = random.nextInt(100);
        int arg2 = random.nextInt(100);
        return operation + " " + arg1 + " " + arg2;
    }
}