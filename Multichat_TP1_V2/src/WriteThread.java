import java.io.*;
import java.net.Socket;

public class WriteThread extends Thread {
    //Lit le input du Client et l'envoie au serveur.
    //Roule a l'infinie tant que l'utilisateur ne tappe pas /quit

    private PrintWriter writer;
    private Socket socket;
    private Client client;

    public WriteThread(Socket socket, Client client) {
        this.socket = socket;
        this.client = client;

        try {
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
        } catch (IOException ex) {
            System.out.println("Error getting output stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void run() {
        try {
        	
       
        	BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); 
         
            
            
            String text;
            do {
                text = br.readLine();
                writer.println(text);
            } while (!text.equals("/quit"));

            try {
                socket.close();
            } catch (IOException ex) {

                System.out.println("Error writing to server: " + ex.getMessage());
            }
        } catch (IOException e) {
            System.out.println("Error writing to server: " + e.getMessage());
        }

    }
}