package Serve;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
 
public class Server {
 
    // Definisemo i cuvamo listu online korisnika koji su se zakacili na server
 
    public static LinkedList<ClientHandler> onlineUsers = new LinkedList<>();
 
    public static void main(String[] args) {
 
 
        // Def. port za osluskivanje, ServerSocket za prihvatanje novih korisnika i soket za komunikaciju sa istima
 
        int port = 9000;
        ServerSocket serverSoket = null;
        Socket soketZaKomunikaciju = null;
 
 
        try {
 
            serverSoket = new ServerSocket(port);
 
            // Prihvatamo nove korisnike/klijente i za svakog pokrecemo novu NIT ClientHandler
            // koja preuzima dalju komunikciju sa njima.
            // Kao parametar prosledjujemo svakoj niti soketZaKomunikaciju
 
            while (true) {
 
                System.out.println("Cekam na konekciju...");
                soketZaKomunikaciju = serverSoket.accept();
                System.out.println("Doslo je do konekcije!");
 
                ClientHandler klijent = new ClientHandler(soketZaKomunikaciju);
 
 
                // Dodavanje klijenta/korisnika u listu online usera
 
                onlineUsers.add(klijent);
 
                // Pokrecemo NIT
 
                klijent.start();
 
            }
 
 
        } catch (IOException e) {
 
            System.out.println("Greska prilikom pokretanja servera!");
 
        } catch (NullPointerException e) {
            // Handle NullPointerException
            System.err.println("Pogresan izbor");
        } 
 
    }
 
}
