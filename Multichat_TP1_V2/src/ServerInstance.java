import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
//import java.util.ArrayList;
import java.util.*;


public class ServerInstance extends Thread {
    private static ServerSocket listener;
    private String serverAddress;
    private int serverPort;

    // Compteur incr�ment� � chaque connexion d'un client au serveur
    private int clientNumber;
    

    //List of user logins pour permettre de garder en memoire les utilisateurs en ligne => liste de clienthandler
    private Set<ClientHandler> clientList = new HashSet<>();
    
 
   

    public ServerInstance(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.clientNumber = 1;
    }

   
              
    public void run() {
        try
        {
            // Cr�ation de la connexion pour communiquer avec les clients
            listener = new ServerSocket();
            listener.setReuseAddress(true);
            InetAddress serverIP = InetAddress.getByName(serverAddress);

            // Association de l'adresse et du port � la connexion
            listener.bind(new InetSocketAddress(serverIP, serverPort));

            System.out.format("The server is running on %s:%d%n", serverAddress, serverPort);
            
           
        } catch (IOException e) {
            e.printStackTrace();
        }

        try
        {
            /* � chaque fois qu'un nouveau client se connecte, on �x�cute la fonction Run() de l'objet ClientHandler*/

            while (true) // Pour continuellement accepter la connection du Client on doit faire une boucle while(true)
            {
                // Important: la fonction accept() est bloquante : attend qu'un prochain client se connecte
                // Une nouvelle connection : on incr�mente le compteur clientNumber
                ClientHandler client = new ClientHandler(this ,listener.accept(), clientNumber++);
                clientList.add(client);
                client.start();
                
              
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally
        {
            // Fermeture de la connexion
            try {
                listener.close();
            }
                                   
            
            catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    
    
    
    //Envoie un message a tout les utilisateurs connecte
    // Le message contient egalement la signature particuliere de l'utilisateur
    public void broadcast(String msg, ClientHandler excludeUser) throws IOException {
        for (ClientHandler client : clientList) {
        	if (client != excludeUser) {
        		client.sendMessage(msg); // Envoie message au Client depuis le ClientHandler 	
        	}
            
        }
    }
    
	
}
