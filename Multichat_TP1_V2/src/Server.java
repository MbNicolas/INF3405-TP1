import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.ArrayList;

//Classe principale du programme qui creer une instance de la classe ServerInstance
public class Server extends Validator {
	
 	

	/* Application */

	public static void main(String[] args) throws Exception
	{
		
		
		//Demander � l'utilisateur d'entrer les valeurs pour l'adresse et le port
		String serverAddress;
		int serverPort;
		Scanner scanner = new Scanner(System.in);
		do {
			System.out.println("Enter the server IP address: ");
			serverAddress = scanner.nextLine();
			if (!isValidIpAddress(serverAddress)) { //TODO : Revoir si le message d'erreur est adequat ici
				System.out.println("Invalid IP address. Try again.");
			}
		} while (!isValidIpAddress(serverAddress));

		do {
			System.out.println("Enter the server port: ");
			serverPort = scanner.nextInt();
			if (!isValidPort(serverPort)) { //TODO : Revoir si le message d'erreur est adequat ici
				System.out.println("Invalid Port. Try again.");
			}
		} while (!isValidPort(serverPort));

		//Creation d'un new ServerInstance
		ServerInstance server = new ServerInstance(serverAddress, serverPort);

		//Demarrage du thread serverInstance
		server.start();
	}

}
