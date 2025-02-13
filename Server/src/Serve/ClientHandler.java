package Serve;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.regex.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

//U ovom slucaju NIT smo realizovali nasledjivanjem klase THREAD
public class ClientHandler extends Thread {

	BufferedReader clientInput = null;
	PrintStream clientOutput = null;
	Socket soketZaKomunikaciju = null;


	// Konstruktor za NIT

	public ClientHandler(Socket soket) {
		soketZaKomunikaciju = soket;
	}

	public static boolean proveriFormat(String string, String format) {
		// Zamenjujemo "x" u formatu sa "\\d" kako bismo odgovarali bilo kojoj cifri
		String regex = format.replaceAll("x", "\\\\d");

		// Kreiramo Pattern objekat sa regex-om
		Pattern pattern = Pattern.compile(regex);

		// Koristimo Matcher da uporedimo string sa određenim formatom
		Matcher matcher = pattern.matcher(string);

		// Vraćamo true ako string odgovara formatu, inače false
		return matcher.matches();
	}

	public static boolean validatorKartica(String brojKartice, String cvv) {
		try (BufferedReader reader = new BufferedReader(new FileReader("BazaKartica.txt"))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(", ");
				String kartica = parts[0].split(":")[1];
				String cvvBroj = parts[1].split(":")[1];
				if (brojKartice.equals(kartica) && cvv.equals(cvvBroj)) {
					return true; // Kartica i CVV broj su pronađeni
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {

			e.printStackTrace();
		}

		return false; // Kartica ili CVV broj nisu pronađeni
	}
	public static boolean validatorSamoBroj(String brojKartice) {
		try (BufferedReader reader = new BufferedReader(new FileReader("BazaKartica.txt"))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(", ");
				String kartica = parts[0].split(":")[1];
				if (brojKartice.equals(kartica)) {
					return true; // Kartica je pronađena
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {

			e.printStackTrace();
		}
		return false;
	}
	public static String pronadjiCvv(String brojKartice) {
		try (BufferedReader reader = new BufferedReader(new FileReader("BazaKartica.txt"))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(", ");
				String kartica = parts[0].split(":")[1];
				String cvvBroj = parts[1].split(":")[1];
				if (brojKartice.equals(kartica)) {
					return cvvBroj;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {

			e.printStackTrace();
		}
		return "Broj";
	}
	public static void uplati(int iznos) {
		int uplaceno = 0;
		try (BufferedReader reader = new BufferedReader(new FileReader("UkupanNovac.txt"))) {
			String line = reader.readLine();
			int kolicina = Integer.parseInt(line);
			uplaceno = kolicina + iznos;
		} catch (IOException e) {
			e.printStackTrace();
		}
		try(BufferedWriter writer = new BufferedWriter(new FileWriter("UkupanNovac.txt"))){
			writer.write(String.valueOf(uplaceno));
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void upisiUplatu(String ime, String prezime, int iznos) {
		try(BufferedWriter writer = new BufferedWriter(new FileWriter("Uplate.txt",true))){
			LocalDateTime datum = LocalDateTime.now();
			DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			String formattedDate = datum.format(myFormatObj);
			writer.write(ime+","+prezime+","+formattedDate+","+iznos);
			writer.newLine();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean daLiVecPostoji(String username) {
		try (BufferedReader reader = new BufferedReader(new FileReader("Korisnici.txt"))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if(line.startsWith(username)) {
					return true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	// Metoda run u kojoj se izvrsava NIT

	@Override
	public void run() {

		try {

			// Inicijalizacija ulazno/izlaznih tokova

			clientInput = new BufferedReader(new InputStreamReader(soketZaKomunikaciju.getInputStream()));
			clientOutput = new PrintStream(soketZaKomunikaciju.getOutputStream());

			boolean isValid = false;

			String opcija1;
			
			clientOutput.println("Uspesno ste se povezali.\n");

			do {
				//String opcija1;
				clientOutput.println("Odaberite opciju: \n 1.Uplata novca \n" + " 2.Kolicina uplacenih sredstava: \n 3.Registracija\n"
						+ " 4.Prijava\n 5.Za izlazak upisite 5");
				opcija1 = clientInput.readLine();
				if(opcija1.equals("5")) {
				
					break;
				}
				int opcija;
				try {
					if(Integer.parseInt(opcija1)>5 || Integer.parseInt(opcija1)<1) {
						clientOutput.println("Izbor je od 1 do 4\n");
					}
					opcija = Integer.parseInt(opcija1);
					if (opcija == 1) {
						String linija;						
						int iznos;
						int cvv;
						String ime;
						String prezime;
						String adresa;
						String broj_kartice;
						clientOutput.println("Unesite podatke u formatu: \n"
								+ "ime, prezime, adresa, broj platne kartice, CVV broj, iznos.");
						String tekst = clientInput.readLine();
						String[] podaci = tekst.split(",\\s*");
						ime = podaci[0];
						prezime = podaci[1];
						adresa = podaci[2];
						broj_kartice = podaci[3];
						cvv = Integer.parseInt(podaci[4]);
						iznos = Integer.parseInt(podaci[5]);
						LocalDateTime datum = LocalDateTime.now();
						DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

						String formattedDate = datum.format(myFormatObj);
						String cvvS = Integer.toString(cvv);

						if (iznos > 200 && proveriFormat(broj_kartice, "xxxx-xxxx-xxxx-xxxx") && cvvS.length() == 3
								&& validatorKartica(broj_kartice, cvvS )) {
							clientOutput.println("Korisnik " +ime + " " + prezime + ", adresa: " + adresa + " je " + formattedDate
									+ " uplatio/la " + iznos + " dinara.");
							uplati(iznos);
							upisiUplatu(ime, prezime, iznos);

						} else if (iznos <= 200) {
							clientOutput.println("Iznos mora biti veci od 200 dinara");
						}

						if (proveriFormat(broj_kartice, "xxxx-xxxx-xxxx-xxxx") == false) {
							clientOutput.println("Broj kartice nije u dobrom formatu\n");
						}
						if (cvvS.length() != 3) {
							clientOutput.println("CVV mora imati tacno tri cifre");
						}
						if (!validatorKartica(broj_kartice, cvvS)) {
							clientOutput.println("Kartica i CVV ne postoje u bazi");
						}
					}
					if (opcija == 2) {
						BufferedReader reader = new BufferedReader(new FileReader("UkupanNovac.txt"));
						clientOutput.println("Do sada je prikupljeno " + reader.readLine() + " dinara");
					}
					String ime;
					String prezime;
					String broj_kartice;
					if(opcija==3) {
						
						clientOutput.println("Za registraciju su nam potrebni sledeci podaci u formatu: \n"
								+ "username,password,ime, prezime, JMBG, broj platne kartice, email");
						String tekst = clientInput.readLine();
						String[] podaci = tekst.split(",\\s*");
						String username = podaci[0];
						String password=podaci[1];
						ime= podaci[2];
						prezime= podaci[3];
						String jmbg= podaci[4];
						broj_kartice= podaci[5];
						String email= podaci[6];
						
						if(daLiVecPostoji(username)) {
							clientOutput.println("Username vec postoji");
							continue;
						}
						if(!proveriFormat(broj_kartice,"xxxx-xxxx-xxxx-xxxx")) {
							clientOutput.println("Broj kartice nije u dobrom formatu");
							continue;
						}
						if(!validatorSamoBroj(broj_kartice)) {
							clientOutput.println("Kartica ne postoji");
							continue;
						}
						try(BufferedWriter writer = new BufferedWriter(new FileWriter("Korisnici.txt",true))){
							
							writer.write(username+","+password+","+ime+","+prezime+","+jmbg+","+broj_kartice+","+email);
							writer.newLine();
						
							clientOutput.println("Uspesna registracija");
						}catch (IOException e) {
							e.printStackTrace();
						}
						
					}
					if(opcija==4) {
						clientOutput.println("Prijavite se upisivanjem username-a i password-a u formatu: \nusername,password");
						boolean valid=false;
							String linija;
							try(BufferedReader reader= new BufferedReader(new FileReader("Korisnici.txt"))){
								String unpw=clientInput.readLine();
								String[] podaci=unpw.split(",\\s*");
								String username=podaci[0];
								String password=podaci[1];
								boolean flag=false;
								while((linija=reader.readLine())!=null) {
									String[] korisnik = linija.split(",");
									ime=korisnik[2];
									prezime=korisnik[3];
									broj_kartice=korisnik[5];
									//
									if(korisnik[0].equals(username) && korisnik[1].equals(password)) {
										do {
											flag=true;
											clientOutput.println("Odaberite opciju: \n 1.Uplata novca \n" + " 2.Kolicina uplacenih sredstava \n"
													+ " 3.Pogledaj poslednjih 10 uplata\n"
													+ " Za povratak  u glavni meni upisite 'nazad' ");
											try {
												String opcija2 = clientInput.readLine();
												if(opcija2.equals("nazad")) {
													break;
												}
												if(Integer.parseInt(opcija2)>3 || Integer.parseInt(opcija2)<1) {
													clientOutput.println("Izbor je od 1 do 3\n");
												}
												int bropcije;
												bropcije=Integer.parseInt(opcija2);
												if(bropcije==1) {
													clientOutput.println("Unesite CVV broj i iznos u fomratu: cvv,iznos\n");
													String pod=clientInput.readLine();
													String[] cvvizn=pod.split(",\\s*");
													String cvv=cvvizn[0];
													String iznoss=cvvizn[1];
													int iznos= Integer.parseInt(iznoss);
													LocalDateTime datum = LocalDateTime.now();
													DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
													String formattedDate = datum.format(myFormatObj);
													if (iznos > 200  && cvv.length() == 3 && cvv.equals(pronadjiCvv(broj_kartice)) ) {
														clientOutput.println("Korisnik " +ime + " " + prezime + " je " + formattedDate
																+ " uplatio/la " + iznos + " dinara.");
														uplati(iznos);
														upisiUplatu(ime, prezime, iznos);

													} else if (iznos <= 200) {
														clientOutput.println("Iznos mora biti veci od 200 dinara");
													}
													if (cvv.length() != 3) {
														clientOutput.println("CVV mora imati tacno tri cifre");
													}
													if (!cvv.equals(pronadjiCvv(broj_kartice))) {
														clientOutput.println("CVV nije dobar");
													}
													
												}
												if (bropcije == 2) {
													BufferedReader reader1 = new BufferedReader(new FileReader("UkupanNovac.txt"));
													clientOutput.println("Do sada je prikupljeno " + reader1.readLine() + " dinara");
												}
												if(bropcije==3) {
													int brojac=0;
													BufferedReader citac= new BufferedReader(new FileReader("Uplate.txt"));
													while((linija=citac.readLine())!=null && brojac!=10) {
														clientOutput.println(linija);
														brojac++;
													}													
												}
											} catch (NumberFormatException e) { // TODO Auto-generated catch block
												clientOutput.println("Niste uneli dobar izbor, pokusajte ponovo.");
											}
										}while(!valid);
									}
								}
								if(!flag) {
									clientOutput.println("Ne postoji korisnik " );
								}
							}
					}
				}
				catch (NumberFormatException e) { // TODO Auto-generated catch block
				clientOutput.println("Niste uneli dobar izbor!"); 
				}
				catch (ArrayIndexOutOfBoundsException e) {
				clientOutput.println("Niste dobro uneli podatke");
				}


			} while (!isValid);


			//System.out.println("OVDEEE2");
			
			String message;
			while (true) {
				 
              //message = clientInput.readLine();

              // Ako poruka sadrzi niz karaktera koji ukazuju na izlaz, izlazi se iz petlje, korisnik se izbacuje
              // iz liste online usera na serveru i obavestavaju se ostali da je doticni napustio chat

              if (opcija1.startsWith("5")) {
            	  clientOutput.println(">>>Hvala na humanosti");
                  break;

              }
			}
			
			// Ovde je definisano primanje poruka od korisnika
			// Korisnik se izbacuje iz liste sa servera

			Server.onlineUsers.remove(this);

			// Zatvaramo soket za komunikaciju

			soketZaKomunikaciju.close();

			// Ovde je obradjen izuzetak u slucaju da korisnik nasilno napusti chat. U
			// smislu da ne otkuca ***quit nego da samo ugasi
			// klijentsku aplikaciju. Ili da mu nestane struje npr.

		} catch (IOException e) {

			clientOutput.println("Niste uneli dobar izbor");

		} catch (NullPointerException e) {
			// Handle NullPointerException
			System.err.println("Pogresan izbor");
		}

	}

}

