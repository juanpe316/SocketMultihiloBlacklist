package es.ubu.lsi.server;

import java.net.*;
import java.io.*;

public class EchoServer {

	public static void main(String[] args) throws IOException {

		if (args.length != 1) {
			System.err.println("Falta el puerto <puerto numero>");
			System.exit(1);
		}

		int[] blacklist = { 2000, 49530 ,50000 }; // Ejemplos de puertos que se van a bloquear
		
		int portNumber = Integer.parseInt(args[0]);
		System.out.println("Escuchando por puerto: " + portNumber);

		try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));) {
			while (true) {
				Socket clientSocket = serverSocket.accept();

				int puertoOrigen = clientSocket.getPort();
				boolean bloqueado = false;

				System.out.println("Nuevo Cliente: " + clientSocket.getInetAddress() + "/" + puertoOrigen);

				for (int numero : blacklist) {
					if (numero == puertoOrigen) {
						bloqueado = true;
						break;
					}
				}
				if (bloqueado) {
					System.out.println("Conexión bloqueada al puerto: " + puertoOrigen);
					clientSocket.close();
				} else {
					Thread hilonuevocliente = new ThreadServerHandler(clientSocket);
					hilonuevocliente.start();
				}
			}

		} catch (IOException e) {
			System.out.println("No se puede escuchar por el puerto: " + portNumber );
			System.out.println(e.getMessage());
		}
	}
}

class ThreadServerHandler extends Thread {

	private Socket clientSocket;

	public ThreadServerHandler(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public void run() {
		try {
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				System.out.println(clientSocket.getPort() + ":" + inputLine);
				out.println(inputLine);
			}
		} catch (IOException e) {
			System.out.println("Excepción en el Thread");
			System.out.println(e.getMessage());
		}
	}
}