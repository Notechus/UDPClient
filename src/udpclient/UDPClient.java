package udpclient;

import networking.Packet;
import gui.AppWindow;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author notechus
 */
public class UDPClient {

    private AppWindow wind;
    static final Logger log = Logger.getLogger(UDPClient.class.getName());
    private DatagramSocket sock = null;
    private final int port = 7777;
    private final InetAddress host = InetAddress.getByName("notechus.ddns.net");

    public UDPClient() throws IOException {
        wind = new AppWindow(this);
        wind.setVisible(true);
        FileHandler fh = new FileHandler("UDPLog.log", true);
        log.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
        log.setUseParentHandlers(false);
        echo("Started application");

    }

    public void send() throws Exception {
        //DatagramSocket sock = null;
        //int port = 7777;
        String s;

        try {
            sock = new DatagramSocket();

            //take input and send the packet
            echo("Enter message to send: ");
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Packet p = null;

            p = new Packet(wind.getTypeInfo(), wind.getQuery());
            echo("Will be sending " + p);

            ObjectOutputStream os = new ObjectOutputStream(outputStream);
            os.writeObject(p);
            byte[] b = outputStream.toByteArray();
            DatagramPacket db = new DatagramPacket(b, b.length, host, port);
            sock.send(db);

            //now receive reply
            //buffer to receive incoming data
            byte[] buffer = new byte[65536];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            sock.receive(reply);

            byte[] data = reply.getData();
            s = new String(data, 0, reply.getLength());

            //echo the details of incoming data - client ip : client port - client message
            echo(reply.getAddress().getHostAddress() + " : " + reply.getPort() + " - " + s);

        } catch (IOException e) {
            System.err.println("IOException " + e);
        } finally {
            sock.close();
            echo("Socket closed");
        }
    }

//simple function to echo data to terminal
    public static void echo(String msg) {
        //System.out.println(msg);
        log.info(msg);
        AppWindow.updateLog(msg);
    }
}
