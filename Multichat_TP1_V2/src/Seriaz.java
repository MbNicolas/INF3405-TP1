


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Seriaz {
	
	public static void main(String [] args) throws ClassNotFoundException {
		
		Map<String, String> map = new HashMap<>();
		
		
		map.put("France","Paris");
		map.put("Italie","Rome");
		map.put("Perou","Lima");
		
	/**Serialisation du hashmap	
		try (FileOutputStream fos = new FileOutputStream ("Check.ser"); 
				ObjectOutputStream oos = new ObjectOutputStream (fos)) {
			oos.writeObject(map);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
				e.printStackTrace();
	} */

	//Deserialisation du hashmap contenu dans le fichier citie
		
		Map<String, String> back = new HashMap<>();
		
		
		try (FileInputStream fis = new FileInputStream ("Check.ser"); 
				ObjectInputStream ois = new ObjectInputStream (fis)) {
			back = (Map<String, String>) ois.readObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
				e.printStackTrace();
	}
		
		System.out.println(back);
	
	}
	

}
