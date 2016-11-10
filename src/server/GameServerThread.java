package server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;

/**
 * Created by steve on 2016-11-09.
 */
public class GameServerThread extends Thread {

    protected DatagramSocket datagramSocket;
    public GameServerThread() throws IOException {
        this("GameServerThread");
    }
    public GameServerThread(String name) throws IOException {
        super(name);
        datagramSocket = new DatagramSocket(4445);
    }

    public void run() {
        System.out.println("NEW SERVER THREAD RUNNING");

            try {
                byte[] buf = new byte[256];

                // Receive request
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                datagramSocket.receive(packet);

                // Figure out response
                String dString = "FROM SERVER THREAD";
                buf = dString.getBytes();

                // Send the response to the client at "address" and "port"
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                datagramSocket.send(packet);

            } catch (IOException e) {
                e.printStackTrace();
            }

        datagramSocket.close();
    }


}
