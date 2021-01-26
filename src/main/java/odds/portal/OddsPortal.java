package odds.portal;

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import odds.portal.enums.Col;
import odds.portal.enums.Constants;
import odds.portal.gui.Gui;
import odds.portal.gui.IdDriver;

public class OddsPortal extends Thread {

	public static final int NB_MINUTES = 15;
	private static final String IDENTIFIANT = "nicolas1000";
	private static final String MDP = "nicolas10000";
	// private static final String PROFILE_BOOKING = "ProfileForBooking";
	public static final String NOM_FENETRE = "Robot";
	static final Double RABAIS = 0.999;
	static final Integer TAILLE_IMAGE_MIN = 500; // pixel
	public static final Boolean NE_PAS_PUBLIER = true;
	Integer tourCourant;
	WebDriver[] drivers;
	WebDriver driver;
	Boolean continuerTours;
	List<Appartement> appartements;
	Map<Date, InfoJour> infoJours;
	boolean appartementsObtenus;
	/**
	 * Pour chaque instant de réservation, la réservation correspondante
	 */
	TreeMap<Date, Reservation> recentes;
	/**
	 * La date en étude. J'entame une nouvelle date seulement si la précédente est
	 * finie.
	 */
	Date date;
	private Properties prop;

	// public static File DOSSIER = new File(System.getProperty("user.home") +
	// "\\"
	// + Constants.APPLICATION_DIR_NAME.texte());
	public static File DOS_PROD = new File(Constants.APPLICATION_DIR_NAME.texte());
	public static File DOS_TECH = new File(Constants.APPLICATION_DIR_NAME.texte() + "/tech");
	//public static File EXCEL = new File(Constants.APPLICATION_DIR_NAME.texte() + "/excel");
	static Logger logger = Logger.getLogger(OddsPortal.class.getName());

	private static final boolean DEBUG = false;
	/**
	 * En mode debuggage, à partir de quel index d'années commencer la collecte d'information.
	 */
	private static final int DEBUG_DEBUT_ANNEE = 0;
	/**
	 * En mode debuggage, commbien de matches à prendre en compte avant de passer à l'année suivante ou page suivante.
	 */
	private static final int DEBUG_NB_MATCHES = 3;
	private static final String EXCEL_FILE = DOS_PROD.getPath() + "/matchs.xlsm";
	private static final String PINNACLE = "Pinnacle";
	private static final String TAB = "    ";
	
	/**
	 * Pour chaque match, il faut NB_TENTATIVES de tentatives.
	 * une tentative est réussie si le fichier est ouvert sans exception et fermé
	   sans exception aussi.
	 */
	private static final int NB_TENTATIVES = 2;
	private static final int TAILLE_BATCH = 3;
	private static final String CHROME_DRIVER = "chromedriver87.exe";

	static DecimalFormat decimalFormat;
	/**
	 * Sunday, 17 Feb 2019, 18:10
	 */
	public static SimpleDateFormat sdfmt1;
	/**
	 * Samedi, 28 mars 2015. Mercredi, 05 août 2015
	 */
	public static SimpleDateFormat sdfmt2;
	/**
	 * 23 janvier 2017
	 */
	public static SimpleDateFormat sdfmt3;
	public static SimpleDateFormat sdfDate;
	static {
		Locale locale = new Locale("en", "US");
		String pattern = "###.00";
		decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(locale);
		decimalFormat.applyPattern(pattern);
		sdfmt1 = new SimpleDateFormat("EEEE, dd MMM yyyy, HH:mm", locale);
		sdfmt2 = new SimpleDateFormat("EEEE, dd MMMM yyyy", locale);
		sdfmt3 = new SimpleDateFormat("dd MMMM yyyy", locale);
		sdfDate = new SimpleDateFormat("yyyy-MM-dd", locale);
	}

	private ArrayList<Tournoi> tournois;
	private int nbAnnees = 3;
	XSSFWorkbook workbook;
	/**
	 * le workbook est-il ouvert?
	 */
	boolean ouvert;
	XSSFSheet sheet;
	/**
	 * Ligne courante sur laquelle sont écrits
	 */
	int ligne;
	String TABLE_DETAIL_ODDS = "table.table-main.detail-odds";

	FileInputStream inputStream;
	CellStyle cellStyleForDate;

	// information pour la gestion d'erreurs

	String derniereUrl;
	NiveauErreur dernierNe;
	NiveauErreurMatch dernierNem;

	/**
	 * Textes pour chaque niveau d'erreur
	 */
	String[] tnes = new String[NiveauErreur.values().length];
	/**
	 * Le détail de la dernière erreur de type match
	 */
	String tnem;
	Gui gui;

	public OddsPortal(Gui gui) {
		this.gui = gui;
	}

	private void initialiser() {
		try {
			chargerPropertyFile();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		try {
			ligne = 2;
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// créer le repertoire de travail
		if (!DOS_PROD.exists()) {
			DOS_PROD.mkdir();
		}
		if (!DOS_TECH.exists()) {
			DOS_TECH.mkdirs();
		}
		
		// copy source to target using Files Class
		try {
			// copier le fichier des matchs
			InputStream file = getInputStreamFromResources("matchs-template.xlsm");
			Path targetDirectory = Paths.get(EXCEL_FILE);
			java.nio.file.Files.copy(file, targetDirectory, StandardCopyOption.REPLACE_EXISTING);	
		
			Path chromedriverPath = Paths.get(DOS_TECH.getPath(), CHROME_DRIVER);
			if(!(chromedriverPath.toFile().exists())) {
				// copier le fichier de chromedriver
				file = getInputStreamFromResources(CHROME_DRIVER);
				java.nio.file.Files.copy(file, chromedriverPath, StandardCopyOption.REPLACE_EXISTING);	
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		creerDrivers();
		
		/*if (!JAVA.exists()) {
			JAVA.mkdir();
		}
		if (!EXCEL.exists()) {
			EXCEL.mkdir();
		}*/
		// la derniere fonction a executer juste avant que l'application ne quitte
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				quitter();
			}
		}));
	}

	
	// get file from classpath, resources folder
	private File getFileFromResources(String fileName) {
		ClassLoader classLoader = getClass().getClassLoader();
		URL resource = classLoader.getResource(fileName);
		if (resource == null) {
			throw new IllegalArgumentException("file is not found!");
		}
		else {
			return new File(resource.getFile());
		}
	}
	
	public InputStream getInputStreamFromResources(String fileName) {
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream resource = classLoader.getResourceAsStream(fileName);
		return resource;
	}
	
	public boolean ouvrirAuBonMoment(){
		boolean res ;
		if(ligne % TAILLE_BATCH ==2) {
			res = ouvrirFichierExcel();
			return res;
		}
		return true;
	}
	
	public boolean fermerAuBonMoment(){
		boolean res ;
		if(ligne % TAILLE_BATCH ==1) {
			res = fermerFichierExcel();
			return res;
		}
		return true;
	}
	
	private boolean ouvrirFichierExcel() {
		File inputFile = new File(EXCEL_FILE);
		boolean ok = true;
		try {
			inputStream = new FileInputStream(inputFile);
		}
		catch (Exception e) {
			ok = false;
			ecrireErreurSimple(convertStackTraceToString(e));
		}
		if (ok) {
			try {
				ouvert = false;
				workbook = new XSSFWorkbook(inputStream);
				ouvert = true;
			}
			catch (Exception e) {
				ok = false;
				ecrireErreurSimple(convertStackTraceToString(e));
			}
		}
		if (ok) {
			try {
				sheet = workbook.getSheetAt(1);
			}
			catch (Exception e) {
				ok = false;
				ecrireErreurSimple(convertStackTraceToString(e));
			}
		}
		if (ok) {
			cellStyleForDate = workbook.createCellStyle();
			CreationHelper createHelper = workbook.getCreationHelper();
			cellStyleForDate.setDataFormat(createHelper.createDataFormat().getFormat("d/m/yy h:mm"));
		}
		return ok;
	}

	public boolean fermerFichierExcel() {
		boolean ok = true;
		boolean res = true;
		// fermer le fichier en lecture
		try {
			inputStream.close();
		}
		catch (Exception e) {
			ok = false;
			res = false;
			
			ecrireErreurSimple(convertStackTraceToString(e));
		}

		if (ouvert && ok && workbook != null) {
			ok = true;
			FileOutputStream outputStream = null;
			try {
				outputStream = new FileOutputStream(EXCEL_FILE);
			}
			catch (Exception e) {
				ok = false;
				res = false;
				ecrireErreurSimple(convertStackTraceToString(e));
			}
			if (ok) {
				try {
					workbook.write(outputStream);
				}
				catch (Exception e) {
					res = false;
					ecrireErreurSimple(convertStackTraceToString(e));
				}
			}
			try {
				((Closeable) workbook).close();
				ouvert = false;
			}
			catch (Exception e) {
				res = false;
				ecrireErreurSimple(convertStackTraceToString(e));
			}
			if (outputStream != null) {
				try {
					outputStream.close();
				}
				catch (Exception e) {
					res = false;
					ecrireErreurSimple(convertStackTraceToString(e));
				}
			}
		}
		return res;
	}
	
	public void testExcel() {
		int nL = 10;
		int nC = 100;
		
		InputStream file = getInputStreamFromResources("matchs-template.xlsm");
		Path targetDirectory = Paths.get(EXCEL_FILE);
		try {
			java.nio.file.Files.copy(file, targetDirectory, StandardCopyOption.REPLACE_EXISTING);
		}
		catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
		try {
			ligne = 2;
			for(int l=2; l<nL; l++) {
				ouvrirAuBonMoment();
				System.out.println(l);
				// écrire au hasard
				for(int c=0; c<nC; c++) {
					ecrireString(l, c, "a[" + (l+1) + "]["+c+"]");
				}
				fermerAuBonMoment();
			}
			fermerFichierExcel();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String convertStackTraceToString(Throwable e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String sStackTrace = sw.toString();
		return sStackTrace;
	}

	private void creerDrivers() {
		int nDrivers = IdDriver.values().length;
		Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle winSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		double width = winSize.getWidth();
		double height = winSize.getHeight();
		int nx, ny;
		if (drivers != null && drivers[0] != null) {
			drivers[0].quit();
		}
		drivers = new WebDriver[IdDriver.values().length];
		String chromedriverPath;
		File file = new File("./" + DOS_PROD.getName() + "/tech/"+CHROME_DRIVER);
		chromedriverPath = file.getAbsolutePath();
		logger.info("Je vais créer les drivers");
		for (int i = 0; i < nDrivers; i++) {
			nx = (i + 2) / 2;
			ny = i % 2;
			logger.debug("Je vais créer driver" + i);
			System.setProperty("webdriver.chrome.driver", chromedriverPath);
			drivers[i] = new ChromeDriver();

			drivers[i].manage().window().setPosition(new Point((int) (nx * width / 2), (int) (ny * height / 2)));
			// drivers[i].manage().window().setSize(new org.openqa.selenium.Dimension((int)
			// ((2*width) / 3), (int) (height)));
			// drivers[i].manage().window().maximize();
			drivers[i].manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			driver = drivers[i];
		}
		logger.info("Création drivers réussies");
	}

	private void connecter() {
		try {
			driver = drivers[IdDriver.RESULTATS_ER.ordinal()];
			String baseUrl = Constants.LOGIN_URL.texte();
			// https://admin.booking.com/hotel/hoteladmin/login.html
			driver.get(baseUrl + "");

			WebElement wLogin;
			WebElement wPassword;
			wLogin = driver.findElement(By.id("login-username1"));
			wLogin.clear();

			// String identifiant = prop.getProperty("login");
			// String mdp = prop.getProperty("mdp");
			String identifiant = IDENTIFIANT;
			String mdp = MDP;

			wLogin.sendKeys(identifiant);

			wPassword = driver.findElement(By.id("login-password1"));
			wPassword.clear();
			wPassword.sendKeys(mdp);
			cliquerPuisAttendre(".item:nth-child(3) span > span", "#user-header-r2 li:nth-child(5) > a");
		}
		catch (Exception e) {
			logger.info("Erreur lors de la connexion");
		}
	}

	private void traiterSite() {
		connecter();
		// récuperer tous les tournois ATP
		Tournoi tournoi;
		try {
			String baseUrl = Constants.ARCHIVED_RESULTS.texte();
			// https://admin.booking.com/hotel/hoteladmin/login.html
			driver.get(baseUrl + "");
			String competionTable = "#archive-tables > table";
			String elementAAttendre = "#archive-tables > table > tbody > tr:nth-child(800) > td:nth-child(1) > a";
			// cliquer sur l'onglet tennis
			logger.info("Collecte des liens de tournoi");
			cliquerPuisAttendre("#archive_results_tennis > a",elementAAttendre);
			
			logger.info("Collecte des liens de tournoi après chargement page liens tennis");
			// #archive-tables > table > tbody > tr:nth-child(702) > th > a
			// #archive-tables > table > tbody > tr:nth-child(703) > th > a
			//
			// #archive-tables > table > tbody > tr:nth-child(705) > td:nth-child(1) > a

			WebElement element = driver.findElement(By.cssSelector(competionTable));
			List<WebElement> lignes = element.findElements(By.cssSelector("tr>td>a"));
			WebElement ligne = null;

			tournois = new ArrayList<Tournoi>();
			int taille;
			boolean estEntreDansTennis = false;
			// recupérer tous les tournois ATP
			for (int iToutTournoi = 950; iToutTournoi < lignes.size(); iToutTournoi++) {
				if(iToutTournoi%100==0) {
					logger.info("   Collecte du " + iToutTournoi + "eme tournoi");
				}
				try {
					ligne = lignes.get(iToutTournoi);
					// on a trouver un tournoi ATP
					if (isATPTennisCompetition(ligne)) {
						tournoi = new Tournoi(ligne.getAttribute("href"), ligne.getText());
						tournois.add(tournoi);
						estEntreDansTennis = true;
						taille = 20;
						if (DEBUG && iToutTournoi>1000) {
							break;
						}
					}
					else if (estEntreDansTennis) {
						// le suivant aussi est une ligne non-tennis, ce qui fait deux lignes non-tennis
						// consécutives
						if (!isTennisCompetition(lignes.get(iToutTournoi + 1))) {
							break;
						}
						taille = 10;
					}
					else {
						taille = 10;
					}
					logger.debug(String.format("%" + taille + "d | " + ligne.getText(), iToutTournoi));
				}
				catch (Exception e) {
					logger.info("Erreur lors de la récupération du lien de tournoi ATP " + ligne.getText());
				}
			}

		}
		catch (Exception e) {
			logger.info("Erreur lors de la récupération des liens de tous les tournois ATP");
		}
		// traiter chaque tournoi ATP
		traiterTournois();
	}

	private void traiterTournois() {
		WebElement element;
		WebElement wTournamentTable;
		List<WebElement> elements;
		List<WebElement> wParts;
		List<String> urlAnnees;
		WebElement pagination;
		int debutAnnee;
		int limiteAnnee;
		int nPage;
		String annees = "#col-content > div.main-menu2.main-menu-gray > ul";
		WebElement wMatchs;
		WebElement wPageSuiv;
		String urlAnnee;
		int page;
		// pour chaque tournoi ATP
		for (Tournoi tournoi : tournois) {
			tnes[NiveauErreur.Tournoi.ordinal()] = tournoi.getNom();
			try {
				driver.get(tournoi.getUrl());
				dernierNe = NiveauErreur.Tournoi;
				derniereUrl = tournoi.getUrl();
				attendre(annees);
				// trouver les années
				element = driver.findElement(By.cssSelector(annees));
				elements = element.findElements(By.cssSelector("a"));
				debutAnnee = DEBUG ? (DEBUG_DEBUT_ANNEE > nbAnnees-1 ? nbAnnees-1 : DEBUG_DEBUT_ANNEE) : 0;
				// combien d'années à prendre en compte?
				limiteAnnee = elements.size() > nbAnnees ? nbAnnees : elements.size();
				urlAnnees = new ArrayList<String>();
				// prendre les urls
				for (int i = 0; i < limiteAnnee; i++) {
					urlAnnees.add(elements.get(i).getAttribute("href"));
				}
				// traiter les années
				for (int i = debutAnnee; i < limiteAnnee; i++) {
					urlAnnee = urlAnnees.get(i);

					logger.debug(urlAnnees.get(i));
					driver.get(urlAnnees.get(i));
					tnes[NiveauErreur.Annee.ordinal()] = urlAnnee;
					derniereUrl = urlAnnee;
					dernierNe = NiveauErreur.Annee;
					attendre("#tournamentTable");
					wTournamentTable = driver.findElement(By.cssSelector("#tournamentTable"));
					wParts = wTournamentTable.findElements(By.xpath("child::*"));
					wMatchs = wParts.get(0);
					page = 1;
					// il y a plus d'une page
					if (wParts.size() > 1) {
						pagination = wParts.get(1);
						wParts = pagination.findElements(By.cssSelector("a"));
						nPage = getPageNb(wParts.get(wParts.size() - 1));
	
						// traiter la page courante (de l'année courante)
						for (int j = 0; j < nPage; j++) {						
							page = j + 1;
							traiterPageAnnee(tournoi, page);
							if(page<nPage) {
								driver.get(urlAnnees.get(i));
								wTournamentTable = driver.findElement(By.cssSelector("#tournamentTable"));
								wParts = wTournamentTable.findElements(By.xpath("child::*"));
								wMatchs = wParts.get(0);
								pagination = wParts.get(1);
								wParts = pagination.findElements(By.cssSelector("a"));
								wPageSuiv = wParts.get(wParts.size() - 2);
								wPageSuiv.click();
							}
						}
					}
					else {
						traiterPageAnnee(tournoi, page);
					}
				}
			}
			catch (Exception e) {
				ecrireErreur(convertStackTraceToString(e));
			}
		}
		fermerFichierExcel();
	}

	private void traiterPageAnnee(Tournoi tournoi, int page) {
		ArrayList<String> urlMatchs = null;
		String[] parts;
		String part;
		WebElement wAsianHandicap = null;
		WebElement wOU;
		boolean ok = true;
		// on obtient des staleementexception mystérieux, pour éviter cela, rechercher l'élément
		for(int i=0; i<3; i++) {
			ok = true;
			try {
				WebElement wTournamentTable = driver.findElement(By.cssSelector("#tournamentTable"));
				List<WebElement> wParts = wTournamentTable.findElements(By.xpath("child::*"));
				WebElement wMatchs = wParts.get(0);
				
				// trouver toutes les lignes de match
				List<WebElement> wLigneMatchs = wMatchs.findElements(By.cssSelector("tr.deactivate"));
				urlMatchs = new ArrayList<String>();

				// recupérer tous les matchs de la page de l'année
				for (WebElement wLigneMatch : wLigneMatchs) {
					WebElement wMatch = wLigneMatch.findElement(By.cssSelector("a"));
					urlMatchs.add(wMatch.getAttribute("href"));
					logger.debug(wMatch.getAttribute("href"));
				}
			}
			catch(StaleElementReferenceException e) {
				ok = false;
			}
			if(ok) {
				break;
			}
		}
		String cTitre = "#col-content > h1";
		WebElement element;
		String text;
		Date date = null;

		Col col;
		TypeCote typeCote;
		int o;
		List<WebElement> elements;
		WebElement wTableDetailOdds;
		WebElement ahOuGame;
		WebElement ahSet;
		int iG;
		int iS;
		boolean estDejaOuvert;
		String urlMatch ;
		try {
			boolean ouvert;
			boolean ferme = false;
			// pour chaque match
			for (int iMatch = 0; iMatch<urlMatchs.size(); iMatch++) {
				urlMatch = urlMatchs.get(iMatch);
				if(DEBUG) {
					if(iMatch>DEBUG_NB_MATCHES) {
						break;
					}
				}
				// une tentative est réussie si le fichier est ouvert sans exception et fermé
				// sans exception aussi.
				for (int nt = 0; nt < NB_TENTATIVES; nt++) {
					ouvert = ouvrirAuBonMoment();
					if (ouvert) {
						// on va dans la page du match
						allerPuisAttendre(urlMatch, cTitre);
						dernierNe = NiveauErreur.Match;
						try {
							derniereUrl = urlMatch;
							dernierNem = NiveauErreurMatch.Commun;
							tnes[NiveauErreur.Match.ordinal()] = "";

							//
							// écrire no match
							ecrireInt(ligne, Col.NoMatch.getC(), ligne - 1);
							//
							// écrire date
							element = driver.findElement(By.cssSelector("#col-content > p.date.datet"));
							try {
								date = sdfmt1.parse(element.getText());
							}
							catch (ParseException e) {
								ecrireErreur(convertStackTraceToString(e));
							}
							ecrireDate(ligne, Col.Date.getC(), date);
							//
							// écrire le tournoi
							ecrireString(ligne, Col.Tournoi.getC(), tournoi.getNom());
							//
							// récupérer et écrire le nom des deux joueurs
							element = driver.findElement(By.cssSelector("#col-content > h1"));
							text = element.getText();
							parts = text.split("-");
							ecrireString(ligne, Col.NomJ1.getC(), parts[0]);
							ecrireString(ligne, Col.NomJ2.getC(), parts[1]);
						}
						catch (Exception e) {
							ecrireErreur(convertStackTraceToString(e));
						}
						try {
							dernierNem = NiveauErreurMatch.HomeAway;
							//
							// écrire côte joueur 1, joueur 2, que du Pinnacle
							wTableDetailOdds = driver.findElement(By.cssSelector(
									"#odds-data-table > div.table-container > table.table-main.detail-odds"));
							ecrireCotes(Col.CoteJ1, TypeCote.MATCH, wTableDetailOdds, 0);
							//
							// écrire score final (2:0) et scores par set (6:4, 6:3)
							element = driver.findElement(By.cssSelector("#event-status > p"));
							text = element.getAttribute("innerHTML");
							// écrire les score par set d'abord
							String scoresParSet = StringUtils.substringBetween(text, "(", ")");
							if (scoresParSet != null && scoresParSet.length() > 0) {
								scoresParSet = replaceMatching2(scoresParSet, "<span", "</span>");
								while (scoresParSet.contains("<sup>")) {
									scoresParSet = replaceMatching2(scoresParSet, "<sup>", "</sup>");
								}
								parts = scoresParSet.split("[\\(\\,\\)\\:\\s]+");
								col = Col.ScoreSet;
								for (int k = 0; k < parts.length; k++) {
									part = parts[k];
									ecrireInt(ligne, col.getC() + k, parser(part));
								}
								parts = text.split("\\(");
								// écrire score
								if (parts[0].contains(":")) {
									text = replaceMatching2(parts[0], "<span", "</span>");
									text = text.replace("<strong>", "");
									text = text.replace("</strong>", "");
									parts = text.split("[\\(\\,\\)\\:\\s]+");
									for (int k = 0; k < parts.length; k++) {
										part = parts[k];
										ecrireInt(ligne, Col.NombreSet.getC() + k, parser(part));
									}
								}
							}
						}
						catch (Exception e) {
							ecrireErreur(convertStackTraceToString(e));
						}

						// gérer les Asian Handicap et les Over/Under
						try {

							List<WebElement> wBetTypes = driver.findElements(By.cssSelector("#bettype-tabs > ul > li"));
							WebElement[] wOnglets = new WebElement[2];
							int nb= 0;
							for(WebElement wOnglet : wBetTypes) {
								if(wOnglet.getText().equals("AH")) {
									nb++;
									wAsianHandicap = wOnglet;
									wOnglets[0] = wOnglet;
								}
								else if(wOnglet.getText().equals("O/U")) {
									wOU = wOnglet;
									wOnglets[1] = wOnglet;
									nb++;
								}
								if(nb>1) {
									break;
								}
							}
							
							
							// pour chaque onglet
							for (WebElement wOnglet : wOnglets) {
								if(wOnglet!=null) {
									cliquerPuisAttendre(wOnglet, "#odds-data-table");
									// la table des lignes asians handicaps AH (ou OU)
									element = driver.findElement(By.cssSelector("#odds-data-table"));
									// les lignes AH ou OU
									elements = element.findElements(By.cssSelector("#odds-data-table > div"));
	
									ArrayList<WebElement> ahSets = new ArrayList<WebElement>();
									ArrayList<WebElement> ahOuGames = new ArrayList<WebElement>();
									boolean onEstDansSets = true;
									// pour chaque ligne AH ou (OU), séparer les 'sets' des 'games'
									for (WebElement e : elements) {
										if (e.isDisplayed()) {
											if (e.getAttribute("class").equals("table-container")) {
												if (onEstDansSets) {
													ahSets.add(e);
												}
												else {
													ahOuGames.add(e);
												}
											}
											else if (e.getAttribute("class").equals("scoreSeparator")) {
												onEstDansSets = false;
											}
										}
									}
									if (wOnglet == wAsianHandicap) {
										col = Col.AH01Valeur;
										typeCote = TypeCote.AH;
									}
									else {
										col = Col.OverUnder01Valeur;
										typeCote = TypeCote.OU;
									}
									iG = 0;
									int nGames = ahOuGames.size();
									int nSets = ahSets.size();
									int nTotal = nGames + nSets;
									
									if(nTotal < 3) {
										estDejaOuvert = true;
									}
									else if(nTotal == 3){
										if(nGames * nSets == 0) {
											estDejaOuvert = true;
										}
										else {
											estDejaOuvert = false;
										}
									}
									// nTotal > 3
									else {
										estDejaOuvert = false;
									}
									// traiter les 'games' d'abord
									for (int k = 0; k < ahOuGames.size(); k++) {
										ahOuGame = ahOuGames.get(k);
										iG = gererAHOU(ahOuGame, col, typeCote, iG, estDejaOuvert);
									}
									// traiter les 'sets' si AH
									if (wOnglet == wAsianHandicap) {
										iS = 0;
										for (int k = 0; k < ahSets.size(); k++) {
											ahSet = ahSets.get(k);
											iS = gererAHOU(ahSet, Col.Set01Valeur, typeCote, iS, estDejaOuvert);
										}
									}
								}
							}
						}
						catch (Exception e) {
							ecrireErreur(convertStackTraceToString(e));
						}
					}
					if (ouvert) {
						ferme = fermerAuBonMoment();
					}
					// la tentative a réussi, pas besoin d'essayer encore
					if (ouvert && ferme) {
						break;
					}
				}
				ligne++;
			}
		}
		catch (Exception e) {
			ecrireErreur(convertStackTraceToString(e));
		}
	}

	private int gererAHOU(WebElement ahOu, Col col, TypeCote typeCote, int iGS, boolean estDejaOuvert) {
		WebElement wBookmakerLink = ahOu.findElement(By.cssSelector("div > strong > a"));

		if (typeCote == TypeCote.MATCH) {
			dernierNem = NiveauErreurMatch.HomeAway;
		}
		else if (typeCote == TypeCote.AH) {
			dernierNem = NiveauErreurMatch.AH;
		}
		else if (typeCote == TypeCote.OU) {
			dernierNem = NiveauErreurMatch.OU;
		}

		// souvent on se trompe sur l'état d'ouverture des ligne AH ou OU
		for(int i=0; i<2; i++) {
			try {
				if(!estDejaOuvert) {
					cliquerPuisAttendreApparition(wBookmakerLink, ahOu, TABLE_DETAIL_ODDS);
				}
				WebElement wTableDetailOdds = ahOu.findElement(By.cssSelector(TABLE_DETAIL_ODDS));
				iGS = ecrireCotes(col, typeCote, wTableDetailOdds, iGS);
				// cliquer encore pour fermer les lignes
				if(!estDejaOuvert) {
					cliquerPuisAttendreDisparition(wBookmakerLink, ahOu, TABLE_DETAIL_ODDS);
				}
				break;
			}
			catch(Exception e) {
				if(i==0) {
					estDejaOuvert = !estDejaOuvert;
				}
			}
		}

		return iGS;
	}

	enum TypeCote {
		MATCH, AH, OU
	}

	/**
	 * 
	 * @param cStart
	 * @param typeCote
	 * @param wTableDetailOdds La table qui contient les lignes bookmaker puis cotes
	 * @param iAHOU            La position de l'Asian Handicap (ou du Under/Over).
	 *                         En effet, les AH ou OU sont donnés en liste (Asian
	 *                         handicap -1.5 Sets, Asian handicap +1.5 Sets, etc.)
	 */
	private int ecrireCotes(Col cStart, TypeCote typeCote, WebElement wTableDetailOdds, int iAHOU) {
		String sBookmaker = "td:nth-child(1) > div > a.name"; // + (typeCote==TypeCote.OU ? "2": "");
		List<WebElement> wCotes;
		WebElement wValeur;
		List<WebElement> wBookmakerLines = wTableDetailOdds.findElements(By.cssSelector("tbody > tr.lo"));
		WebElement wBookmaker;
		Float cote = null;
		Float valeur = null;
		int o = 0;
		for (WebElement wBookmakerLine : wBookmakerLines) {
			// si on ne trouve pas le <a> de bookmaker dans la ligne courante
			// de AH ou OU, passer à la suivante
			try {
				wBookmaker = wBookmakerLine.findElement(By.cssSelector(sBookmaker));
				if (wBookmaker.getText().equalsIgnoreCase(PINNACLE)) {
					if (typeCote == TypeCote.AH || typeCote == TypeCote.OU) {
						wValeur = wBookmakerLine.findElement(By.cssSelector("td.center"));
						valeur = prendreValeurReelle(wValeur);
					}

					// écrire les cotes
					wCotes = wBookmakerLine.findElements(By.cssSelector("td.odds > div"));
					for (int k = 0; k < 2; k++) {
						try {
							cote = prendreValeurReelle(wCotes.get(k));								
							switch (typeCote) {
							case MATCH:
								o = cStart.getC() + k;
								ecrireDouble(ligne, o, cote);
								break;
							case AH:
								o = cStart.getC() + 4 * iAHOU + 2 * k;
	
								// écrire la valeur
								ecrireDouble(ligne, o, (k == 0 ? 1 : -1) * valeur);
								// écrire la cote
								ecrireDouble(ligne, o + 1, cote);
	
								break;
							case OU:
								o = cStart.getC() + 3 * iAHOU + k;
	
								if (k == 0) {
									ecrireDouble(ligne, o, valeur);
								}
								ecrireDouble(ligne, o + 1, cote);
								break;
							default:
								break;
	
							}
						}
						catch (Exception e) {
							ecrireErreur(convertStackTraceToString(e));
						}
					}
					iAHOU++;
					break;
				}
			}
			catch (Exception e) {
				ecrireErreur(convertStackTraceToString(e));
			}
		}
		return iAHOU;
	}

	enum NiveauErreur {
		Tournoi, Annee, Page, Match
	}

	enum NiveauErreurMatch {
		Commun, HomeAway, AH, OU
	}

	/**
	 * Permet de logger l'erreur en précisant le contexte dans lequel cette erreur a
	 * eu lieu.
	 * 
	 * @param text
	 */
	public void ecrireErreur(String text) {
		// ou
		try {
			String texte = null;
			ecrireLigne("", true);
			String indent = "";
			for (int i = 0; i < dernierNe.ordinal(); i++) {
				indent = StringUtils.repeat(TAB, i);
				texte = indent + NiveauErreur.values()[i].name();// + " : " + tnes[i];
				logger.info(texte);
			}
			if (dernierNe == NiveauErreur.Match) {
				indent = StringUtils.repeat(TAB, NiveauErreur.Match.ordinal() + 1);
				texte = indent + dernierNem.name();// + " : " + tnem;
				logger.info(texte);
			}
			logger.info(indent + TAB + text);
			logger.info(derniereUrl);
			ecrireLigne("", false);
		}
		catch (Exception e) {

		}
	}

	public void ecrireErreurSimple(String texte) {
		ecrireLigne("", true);
		logger.info(texte);
		ecrireLigne("", false);
	}

	private Float prendreValeurReelle(WebElement w) {
		Float cote = null;
		try {
			cote = Float.parseFloat(w.getText());
		}
		catch (Exception e) {
			ecrireErreur(convertStackTraceToString(e));
		}
		return cote;
	}

	private Integer parser(String s) {
		if (s == null) {
			return null;
		}
		Integer res = null;
		try {
			res = Integer.parseInt(s);
		}
		catch (Exception e) {
			ecrireErreur(convertStackTraceToString(e));
		}
		return res;
	}

	public static String replaceMatching2(String input, String lowerBound, String upperBound) {
		String result = input.replaceAll("(.*?)(" + lowerBound + ")" + "(.*?)" + "(" + upperBound + ")(.*)", "$1$5");
		return result;
	}

	private Integer getPageNb(WebElement wPage) {
		String[] parts;
		String nPageS;
		int nPage;
		String s;
		s = wPage.getAttribute("href");
		parts = s.split("/");
		nPageS = parts[parts.length - 1];
		nPage = Integer.valueOf(nPageS);
		return nPage;
	}

	private XSSFCell getCellule(int l, int c) {
		XSSFCell cell = null;
		XSSFRow row = sheet.getRow(l);
		if (row == null) {
			row = sheet.createRow(l);
			cell = row.createCell(c);
		}
		else {
			cell = row.getCell(c);
			if (cell == null) {
				cell = row.createCell(c);
			}
		}
		return cell;
	}

	public void ecrireInt(int l, int c, Integer object) {
		XSSFCell cell = getCellule(l, c);
		object = object == null ? -1 : object;
		cell.setCellValue(object);
	}

	public void ecrireString(int l, int c, String object) {
		XSSFCell cell = getCellule(l, c);
		cell.setCellValue(object);
	}

	public void ecrireDate(int l, int c, Date object) {
		XSSFCell cell = getCellule(l, c);
		cell.setCellValue(object);
		cell.setCellStyle(cellStyleForDate);
	}

	public void ecrireDouble(int l, int c, double object) {
		XSSFCell cell = getCellule(l, c);
		cell.setCellValue(object);
	}

	public void chargerPropertyFile() throws IOException {
		InputStream inputStream = null;
		try {
			prop = new Properties();
			String propFileName = "config.properties";
			inputStream = new FileInputStream(propFileName);
			prop.load(inputStream);
		}
		catch (Exception e) {
			// System.out.println("Exception: " + e);
		}
		finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}

	public Date obtenirJour(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		Date date2 = calendar.getTime();
		return date2;
	}

	@Override
	public void run() {
		demarrer();
		System.exit(0);
	}

	public void demarrer() {
		initialiser();
		traiterSite();
	}

	private void ecrireLigne(String texte, boolean ouverture) {
		String depart = ouverture ? "┌" : "└";
		logger.info(depart + "───" + texte
				+ " ──────────────────────────────────────────────────────────────────────────────────");
	}

	private float convertirEnFloat(String string) {
		float reel = -1;
		if (string != null) {
			string = string.replaceAll(",", ".");
			reel = Float.parseFloat(string);
		}
		return reel;
	}

	private Date parserDate(String input, int format1) {
		java.util.Date date = null;
		try {
			switch (format1) {
			case 0:
				date = sdfmt1.parse(input);
				break;
			case 1:
				date = sdfmt2.parse(input);
				break;
			case 2:
				date = sdfmt3.parse(input);
				break;
			}
		}
		catch (ParseException e) {
			ecrireErreur(convertStackTraceToString(e));
		}
		return date;
	}

	private void quitter() {
		// fermer les navigateurs
		System.out.println("Le logiciel est en train de quitter!");
		fermerFichierExcel();

		for (WebDriver driver : drivers) {
			driver.quit();
		}
	}

	public static String retrait(int nombre) {
		String res;
		res = StringUtils.leftPad("", nombre, "\t");
		return res;
	}

	public static void testGoogleSearch() {
		// Optional, if not specified, WebDriver will search your path for chromedriver.
		System.setProperty("webdriver.chrome.driver", "files\\"+CHROME_DRIVER);
		WebDriver driver = new ChromeDriver();
		driver.get("http://www.google.com/xhtml");
		try {
			Thread.sleep(5000); // Let the user actually see something!
			WebElement searchBox = driver.findElement(By.name("q"));
			searchBox.sendKeys("ChromeDriver");
			searchBox.submit();
			Thread.sleep(5000); // Let the user actually see something!
			driver.quit();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setGui(Gui gui) {
	}

	private void attendre(String elementAAttendre) {
		WebDriverWait wait = new WebDriverWait(driver, 40);
		// wait.until(ExpectedConditions.stalenessOf(element));
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(elementAAttendre)));
	}

	private void cliquerPuisAttendre(String elementACliquer, String elementAAttendre) {
		driver.findElement(By.cssSelector(elementACliquer)).click();
		attendre(elementAAttendre);
	}

	private void cliquerPuisAttendre(WebElement elementACliquer, String elementAAttendre) {
		elementACliquer.click();
		attendre(elementAAttendre);
	}

	private void cliquerPuisAttendreApparition(WebElement elementACliquer, WebElement ahOu, String elementAAttendre) {
		elementACliquer.click();
		WebDriverWait wait = new WebDriverWait(driver, 40);
		WebElement w = ahOu.findElement(By.cssSelector(elementAAttendre));
		wait.until(ExpectedConditions.visibilityOf(w));
	}

	private void cliquerPuisAttendreDisparition(WebElement elementACliquer, WebElement ahOu,
			String elementAAttendreLaDisparition) {
		WebElement w = ahOu.findElement(By.cssSelector(elementAAttendreLaDisparition));
		elementACliquer.click();
		WebDriverWait wait = new WebDriverWait(driver, 40);
		wait.until(ExpectedConditions.invisibilityOf(w));
	}

	private void allerPuisAttendre(String url, String elementAAttendre) {
		driver.get(url);
		attendre(elementAAttendre);
	}

	private boolean isTennisCompetition(WebElement ligne) {
		return ligne.isDisplayed() && ligne.getText() != null && ligne.getText().length() > 0;
	}

	private boolean isATPTennisCompetition(WebElement ligne) {
		return isTennisCompetition(ligne) && ligne.getText().contains("ATP");
	}

	public static void main(String[] args) {
		int activity = 1;
		OddsPortal o = new OddsPortal(null);
		switch (activity) {
		case 0:
			o.demarrer();
			break;
		case 1:

			o.testExcel();
			break;
		}
	}

	public int getNbAnnees() {
		return nbAnnees;
	}

	public void setNbAnnees(int nbAnnees) {
		this.nbAnnees = nbAnnees;
	}
	


}
