/******************************************************************************
 *                    Copyright (c) 2024.                                     *
 *  Filmovy tabor Centrumdeti.cz & Roboticky tabor Centrumdeti.cz             *
 ******************************************************************************/

package cz.centrumdeti.filmovytabor.robosoutez.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {
	private static final Logger log = LoggerFactory.getLogger(SocketServer.class);
	private final Thread execThread;
	private final Roboserver rs;
	private ServerSocket serverSocket;

	public SocketServer(int port, Roboserver rs) {
		this.rs = rs;
		execThread = new Thread(() -> {
			try {
				serverSocket = new ServerSocket(port);
				while(true) new SocketHandler(serverSocket.accept(), this).start();
			} catch(IOException e) {
				log.error("SocketServer exec thread", e);
			}
		});
	}

	public void start() {
		execThread.start();
	}

	public void stop() throws IOException {
		serverSocket.close();
		execThread.interrupt();
	}

	private static class SocketHandler extends Thread {
		private final Socket clientSocket;
		private final SocketServer parent;

		public SocketHandler(Socket socket, SocketServer parent) {
			this.clientSocket = socket;
			this.parent = parent;
		}

		public void run() {
			try(ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {
				Object output = parent.rs.socketExecutor.executeRequest(in.readObject());
				out.writeObject(output);
			} catch(IOException | ClassNotFoundException e) {
				log.error("SocketHandler process", e);
			} finally {
				try {
					clientSocket.close();
				} catch(IOException e) {
					log.error("SocketHandler shutdown", e);
				}
			}
		}
	}
}
