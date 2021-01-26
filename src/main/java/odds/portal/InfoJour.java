package odds.portal;

import java.util.Date;

public class InfoJour {
	Date date;
	/**
	 * Un jour est fini s'il la recherche a été effectuée après ce jour.
	 */
	boolean fini;
	/**
	 * Un jour est recopié s'il est fini et le fichier excel de recopie comporte toutes les réservations.
	 */
	boolean recopie;
	Date instantRecherche;
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public boolean isFini() {
		return fini;
	}
	public void setFini(boolean fini) {
		this.fini = fini;
	}
	public boolean isRecopie() {
		return recopie;
	}
	public void setRecopie(boolean recopie) {
		this.recopie = recopie;
	}
	public Date getInstantRecherche() {
		return instantRecherche;
	}
	public void setInstantRecherche(Date instantRecherche) {
		this.instantRecherche = instantRecherche;
	}
	
	
}
