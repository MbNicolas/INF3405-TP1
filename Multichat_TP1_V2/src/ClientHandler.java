import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public  class ClientHandler extends Thread
{
    private Socket socket;
    private int clientNumber;
    private final ServerInstance server;
    
    private String username;
    private String password;
    private String resultLogin;
    private Boolean first;
   public Map<String, String> login = new HashMap<>(); // hashmap pour stocker les username et password
   private String messageDb = "Data.txt"; // nom de la base de donnée de stockage des messages
   private String UserDb = "User.ser"; // nom de la base de donnée de stockage (username/password)

    private DataOutputStream out;
    private DataInputStream in;
    private PrintWriter writer;

    public ClientHandler (ServerInstance server, Socket socket, int clientNumber)
    {
        this.server = server;
        this.socket = socket;
        this.clientNumber = clientNumber;
        System.out.println("New connection with client# " + clientNumber + "at " + socket);
       
    }

    /* Une thread qui se charge d'envoyer au client un message de bienvenue */

    public void run()
    {
        try
        {
            // Crï¿½ation d'un canal sortant pour envoyer des messages au client
            this.out = new DataOutputStream(socket.getOutputStream());

            //Pour ecrire a la console du Client
            this.writer = new PrintWriter(this.out, true);

            // Envoie d'un message au client
            out.writeUTF(("Hello from server - you are client #" + clientNumber));

            //Crï¿½ation d'un canal entrant pour recevoir des messages du client
            this.in = new DataInputStream(socket.getInputStream());

            //Creation d'un reader
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.in));

      
        	
        	//Chargement de la base de donnée (username/password) si elle existe déjà

			  File fichier = new File(UserDb);
				if(fichier.isFile())
				{ 
				 System.out.println("Data base Loaded");
				 loadLogin(UserDb);
				}
             
			
        
         //Vérifie s'il s'agit d'un utilisateur déjà inscrit
            
            String firstUser;
            firstUser =in.readUTF();
            
            if (firstUser.equals("yes")) {

            	this.first = true;
            	System.out.println("First login to the Tchat Server, please register your new Username and Password!");
            	         	            	
            	} else { this.first = false; System.out.println("Trying to login..");}
            
            
            //Demander les tokens au Client qui essaye de se connecter
        
            out.writeUTF("Please enter your username: ");
            this.username = in.readUTF();
            System.out.println("Client username is: " + this.username);
            
            
            out.writeUTF("Please enter your password: ");
            this.password = in.readUTF();
            System.out.println("Client password is: " + this.password);
                    
                    
			if(HandleLogin(this.username, this.password, this.first, this.login )) {
                System.out.println("Login of " + this.username + " success!\n"); 
                
              //Ajout du nouveau username et du password associé dans la base de donnée si inexistant
    			login.put(this.username,this.password);
    			out.writeUTF(this.resultLogin);
            } else {
            	 System.out.println("Login of " + this.username + " failed!\n");
            	 out.writeUTF(this.resultLogin); //envoie de la réponse du gestionnaire de login au client
            	
            	
            } 

		//TODO Ajouter les messages de résultats de login au Client.
			
			 
			
			Path path = Paths.get("Database.txt"); // récupère le path du fichier "String.txt" contenant les messages 
			 
			 
			  // vérifie si le fichier de stockage des messages textes existe déjà et le crée sinon
			 
			  File f = new File(messageDb);
				if(!f.isFile())
				{ 
				 System.out.println("New Database created");
				 saveMessage("",path );
				}
			  
			
      		List<String> s = Files.readAllLines(path); // recopie tous les messages stocké dans une liste
      		int n = 15; // Nombre de derniers messages affichés
      		
            
              	printLast(s, n, this.writer); // Affiche les n derniers messages de la liste s
            
            //TODO broadcast les 15 derniers messages aux clients
             //Partie salle de clavardage
         	
             
       
            
              String clientMsg;
          
            do {
                //TODO: Store le message dans le fichier texte
                clientMsg = reader.readLine();
                String serverMessage = constructMessage(clientMsg);//format le message à envoyer au client
                // sauvegarde le message
                saveMessage(serverMessage,path); // Sauvegarde le message dans le fichier indiqué par le path => "save.txt"
              
                server.broadcast(serverMessage, this);
                
             

            } while (!clientMsg.equals("quit")); 

        System.out.println ("The user "+ username + " has left the tchat.");
          //TODO: Remove user from array

            // fermeture de la connexion avec le client
    
       
       
        
    } catch (IOException ex) {
        System.out.println("Error in UserThread: " + ex.getMessage());
        ex.printStackTrace();
    }
}
        
 
    //TODO: Tester cette mÃ©thode
    private String getClientAddress() {
        return socket.getInetAddress().toString() + ":" + socket.getPort();
    }

    //Retourne le temps et la date auquel le message a Ã©tÃ© envoyÃ©
    private String getTimestamp() {
    	
    	Date date = new Date(); // This object contains the current date value
    	SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy @ HH:mm:ss");
    	System.out.println(formatter.format(date));
       // return java.time.LocalDate.now().toString() +  "@"  + java.time.LocalTime.now().toString();
       return formatter.format(date);
    }

   
    public String constructMessage(String msg) {
           	
    	return "[" + this.username + " - " + getClientAddress() + " - " + getTimestamp() + "]: " + msg;
             
    }

    //Envoie un message au printer du Client associÃ©
    public void sendMessage(String msg) {
       
    	writer.println( msg);
    	
    }
    
    //Sauvegarde les messages dans un fichier texte
      public void saveMessage(String message, Path savelink) throws IOException {
   	  
       	  String formatsave = "\n" + message; //format le message à sauvegarder
          Files.write(savelink, formatsave.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND );
    	//il faut encoder en utf8
          
         
    }
      
   // Methode pour afficher les t derniers messages de la liste s
  	static void printLast(List<String> s, int t, PrintWriter write) 
  	{ 
  	
  		if (s.size() == 0) // cas où le fichier était vide
  		{ 
  			System.out.println("le fichier est vide");
  			
  			return; 
  		} 
  		
  		
  		if (s.size()>= t) //Si le fichier de sauvegarde est inférieur à 15 lignes, on affiche toutes les lignes
  		{ 
  			for (int i = s.size() - t; i < s.size(); i++) 
  			{ 
  				//System.out.println(s.get(i));
  			 write.println(s.get(i)); //affiche les 15 derniers messages au client
  			} 
  		} 
  		else //Si le fichier de sauvegarde contient plus de 15 lignes, on affiche les 15 dernières
  		{ 
  			for (int i = 0; i < s.size(); i++) 
  			{ 
  				//System.out.println(s.get(i)); 
  				 write.println(s.get(i)); //affiche les 15 derniers messages au client
  			} 
  		}}
    
    //Détermine si il s'agit d'une première connexion
    public void setNewuser (boolean first) {
		this.first = first;
	}
    
    public boolean HandleLogin (String username, String password, boolean first, Map<String,String> login) {
    	
    	boolean good = false; //indique si le login s'est bien déroulé => false = problème de login.
    	Scanner scr1 = new Scanner (System.in); // Création d'un scanner qui va lire la prochaine ligne
    
    if (first == true ) {   // Inscription d'un nouvel utilisateur
    	
    	if (login.containsKey(username)) {
    		System.out.println ("This username is already used.\n");// vérifie si l'utilisateur est déjà utilisé
    		this.resultLogin = "The username "+ username + " is already used.\n";
    		
    		// fermer la connexion ou redemander un username
    		//sortir de la boucle break?
    		
    		
    	}else {
    		login.put(username,password);
    		good = true;
    		
    		this.resultLogin = "Registration success! You can start to tchat!\n";
    		}
 
    	
    } else if (first == false) //login d'un utilisateur déjà enregistré dans la base de donnée
    	
    {
    	/**
    	
    	On compare le username du hasmap avec le username pour savoir si l'identifiant existe
    	Le password associé à l'identifiant est ensuite comparé avec celui renseigné par l'utilisateur
    	 */
    	if (login.containsKey(username) & password.equals(login.get(username)))
    		
    			{
    		System.out.println("Welcome back " + username + "!");
    		good = true;
    		this.resultLogin = "Login success! You can start to tchat!\n";
    			}
    	else {
    		System.out.println("Erreur dans la saisie du mot de passe!" );
    		
    		this.resultLogin = "Erreur dans la saisie du mot de passe!";
    	}
    }
          
	
	System.out.println("La base de donnée contient actuellement: \n" + login);
    System.out.println("Les nouveaux utilisateurs sont :\n" + login.keySet());

	scr1.close(); 
	
	if (good==true) 
	{saveLogin(UserDb, login);} // sauvegarde des logins dans la base de données
	return good;
	
			
    }
    
  //Sauvegarde de la base de donnée (username/password) au format .ser
    public void saveLogin (String save, Map<String,String> login) { 
    	
    	try (FileOutputStream fos = new FileOutputStream (save); 
    			ObjectOutputStream oos = new ObjectOutputStream (fos)) {
    		oos.writeObject(login);
    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    			e.printStackTrace();
    } 
    		
    }
    
  //Chargement de la base de donnée (username/password) dans hashmap
    
    public void loadLogin (String load) {
    try (FileInputStream fis = new FileInputStream (load);  
			ObjectInputStream ois = new ObjectInputStream (fis)) {
		this.login = (Map<String, String>) ois.readObject();
		
		System.out.println("Les login contennnent :" + this.login);
		
		} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
			e.printStackTrace();
} catch (ClassNotFoundException e) {
		
		e.printStackTrace();
		
		
	} 
    }
   
 

           
}


