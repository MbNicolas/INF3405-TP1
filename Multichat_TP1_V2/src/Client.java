import java.net.Socket;
import java.util.Scanner;
import java.io.*;

public class Client extends Validator {
	
	private static Socket socket = null;
	private static Scanner msgScanner;
	 public static Boolean newUser = true; // Indique s'il s'agit d'un nouvel utilisateur au niveau de la base de donn�e

	/* Application */

	public Client(Socket socket, Scanner msgScanner) {
		this.socket = socket;
		this.msgScanner = msgScanner;
	}

	public static void main(String[] args) throws Exception
	{
		//Demander au client utilisateur d'entrer les valeurs pour l'adresse et le port
		String serverAddress;
		int port;

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
			port = scanner.nextInt();
			if (!isValidPort(port)) { //TODO : Revoir si le message d'erreur est adequat ici ou si on prefere ajouter des exceptions
				System.out.println("Invalid Port. Try again.");
			}
		} while (!isValidPort(port));

		// Cr�ation d'une nouvelle connexion avec le serveur
		
		socket = new Socket(serverAddress, port);

		//Creation d'un message scanner pour envoyer les messages de la console Client � celle du serveur
		msgScanner = new Scanner(System.in);

		Client client = new Client(socket, msgScanner);

		System.out.format("The server is running on %s:%d%n", serverAddress, port);
		
		// Cr�ation d'un canal entrant pour recevoir les messages envoy�s par le serveur
		DataInputStream in = new DataInputStream(client.socket.getInputStream());

		// Attente de la r�ception d'un message envoy� par le serveur sur le canal
		String helloMessageFromServer = in.readUTF();
		System.out.println(helloMessageFromServer);
		
	

		//Cr�ation d'un canal d'envoie de message au serveur
		DataOutputStream out = new DataOutputStream(client.socket.getOutputStream());

	//V�rifier si c'est une premi�re connexion
		
	        
	System.out.println("Is this your first time on the Tchat? ");
	String firstConnection;  //variable receuillant la r�ponse (yes or no)
	firstConnection = scanner.nextLine();
	
	//System.out.println("That was the value before you started :" + firstConnection);
	
	//Tant que l'utilisateur ne r�pond pas par yes or no, la question est repos�e.	
	while 	( !firstConnection.equals("yes") && !firstConnection.equals ("no") ) {
		
		System.out.println("Valid options are :yes/no.");
		
		firstConnection = scanner.nextLine();
		
			}
	
// Permet d'indiquer si l'utilisateur se connecte pour al premi�re fois au server
	
	out.writeUTF(firstConnection); //envoie la r�ponse au server
	

							
		
		//Attente de la r�ception d'un message envoy� par le serveur pour le username
		String usernameConnectionMsg = in.readUTF();
		System.out.println(usernameConnectionMsg);
		
		//Envoie Username Client
		String usernameMsg = client.msgScanner.nextLine();
		out.writeUTF(usernameMsg);
		System.out.println("Username " + usernameMsg + " was sent to the server");

		//Attente de la r�ception d'un message envoy� par le serveur pour le password
		String passwordConnectionMsg = in.readUTF();
		System.out.println(passwordConnectionMsg);

		//Envoie Password Client
		String passwordMsg = client.msgScanner.nextLine();
		out.writeUTF(passwordMsg);
		System.out.println("Password " + passwordMsg +  " was sent to the server");
		
		
		//Attente du message de r�sultat du login (success/failed)
		String loginResult = in.readUTF();
		System.out.println("login result was:" + loginResult);

		
		
	      
		client.execute();
	}


	public void execute() {
		// pour recevoir et envoyer les messages.
		new ReadThread(socket, this).start();
		new WriteThread(socket, this).start();
	}


}
