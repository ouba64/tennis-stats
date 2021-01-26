package odds.portal;

import java.util.Date;

public class Reservation {
	// 34
	Long	id;
	Date	dateReservation;
	Date	arrivee;
	Date	depart;

	// 1
	String	nom;
	String	addresse;
	// 11
	String	email;
	// 13
	String	tel;
	// 2
	int		nbPersonnes;
	// 4
	int		nbNuits;
	// 7
	float	menage;
	// 6
	float	nuitee;
	// 35
	String	questions;
	// 5
	float	prix;
	// 29
	String	langue;

	// 12
	String	payementPartielCollecte;

	public Reservation() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDateReservation() {
		return dateReservation;
	}

	public void setDateReservation(Date dateReservation) {
		this.dateReservation = dateReservation;
	}

	public Date getArrivee() {
		return arrivee;
	}

	public void setArrivee(Date arrivee) {
		this.arrivee = arrivee;
	}

	public Date getDepart() {
		return depart;
	}

	public void setDepart(Date depart) {
		this.depart = depart;
	}



	public int getNbPersonnes() {
		return nbPersonnes;
	}

	public void setNbPersonnes(int nbPersonnes) {
		this.nbPersonnes = nbPersonnes;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getAddresse() {
		return addresse;
	}

	public void setAddresse(String addresse) {
		this.addresse = addresse;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}




	public int getNbNuits() {
		return nbNuits;
	}

	public void setNbNuits(int nbNuits) {
		this.nbNuits = nbNuits;
	}

	public float getMenage() {
		return menage;
	}

	public void setMenage(float menage) {
		this.menage = menage;
	}

	public float getNuitee() {
		return nuitee;
	}

	public void setNuitee(float nuitee) {
		this.nuitee = nuitee;
	}

	public String getQuestions() {
		return questions;
	}

	public void setQuestions(String questions) {
		this.questions = questions;
	}

	public float getPrix() {
		return prix;
	}

	public void setPrix(float prix) {
		this.prix = prix;
	}

	public String getLangue() {
		return langue;
	}

	public void setLangue(String langue) {
		this.langue = langue;
	}

	public String format(Date date){
		String res = OddsPortal.sdfDate.format(date);
		return res;
	}


	@Override
	public String toString() {
		return "id=" + id + "|| dateReservation=" + dateReservation + "|| arrivee=" + format(arrivee) + "|| depart="
				+ format(depart) + "|| nom=" + nom + "|| addresse=" + addresse + "|| email=" + email + "|| tel=" + tel
				+ "|| nbPersonnes=" + nbPersonnes + "|| nbNuits=" + nbNuits + "|| menage=" + menage + "|| nuitee=" + nuitee
				+ "|| questions=" + questions + "|| prix=" + prix + "|| langue=" + langue + "|| payementPartielCollecte="
				+ payementPartielCollecte ;
	}

	public String getPayementPartielCollecte() {
		return payementPartielCollecte;
	}

	public void setPayementPartielCollecte(String payementPartielCollecte) {
		this.payementPartielCollecte = payementPartielCollecte;
	}
}
