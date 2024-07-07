package cz.centrumdeti.filmovytabor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CasparController {
    private final String localAddress;
    Socket socket;
    Socket socket2;
    PrintWriter pw1;
    PrintWriter pw2;
    public CasparController(String address, int port,
                            String address2, int port2,
                            String localAddress) throws IOException {
        this.localAddress = localAddress;
        socket = new Socket(address, port);
        socket2 = new Socket(address2, port2);
        Thread t1 = new Thread(()-> {
            BufferedReader in;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                pw1 = new PrintWriter(socket.getOutputStream(),true);
                String inputLine;
                while (true) {
                    try {
                        if ((inputLine = in.readLine()) == null) break;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println(inputLine);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Thread t2 = new Thread(()-> {
            BufferedReader in;
            try {
                in = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
                pw2 = new PrintWriter(socket2.getOutputStream(),true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String inputLine;
            while (true) {
                try {
                    if ((inputLine = in.readLine()) == null) break;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(inputLine);
            }
        });
        t1.start();
        t2.start();
        Runtime.getRuntime().addShutdownHook(new Thread(()-> {
            t1.interrupt();
            t2.interrupt();
        }));
    }

    public void resetAll() {
        pw1.println("CLEAR 1");
        pw2.println("CLEAR 1");
    }

    public void loadbg() {
        pw1.println("LOADBG 1 [HTML] \"http://" + localAddress + "/stream\"");
        pw2.println("LOADBG 1 [HTML] \"http://" + localAddress + "/tv1\"");
    }

    public void play() {
        pw1.println("PLAY 1");
        pw2.println("PLAY 1");
    }
}
