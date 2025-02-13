package Client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Test {
	public static void uplati(int iznos) {
		int uplaceno = 0;
		try (BufferedReader reader = new BufferedReader(new FileReader("UkupanNovac.txt"))) {
			String line = reader.readLine();
			while((line=reader.readLine())!=null) {
				String[] korisnik = line.split(",");
				//OVAJ IF MISLIM DA MI NE RADI
				if(korisnik[0]=="bodzanson" && korisnik[1]=="bokifrizer") {
					System.out.println("Odaberite opciju: \n 1.Uplata novca \n" + " 2.Kolicina uplacenih sredstava: \n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try(BufferedWriter writer = new BufferedWriter(new FileWriter("UkupanNovac.txt"))){
			writer.write(String.valueOf(uplaceno));
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static void createTextFile(String fileName, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	 public static void upisiUFajl(String imeFajla, String sadrzaj) {
	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(imeFajla))) {
	        // Upisivanje sadržaja u fajl
	        writer.write(sadrzaj);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	 }
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Test nova= new Test();
		upisiUFajl("Fajl2.txt", "sadrzkkkaj ovog fajla");
		System.out.println("Kraj");
	}

}
