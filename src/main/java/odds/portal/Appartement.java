package odds.portal;

import java.util.HashMap;
import java.util.Map;

public class Appartement {
	Long id;
	String nom;
	Integer nbReservationsEnAttente;
	Map<Long, Reservation> reservations;
	String url;
	boolean cliquerPourMoteurRechercheReservation;
	boolean reservationEstEnHaut;
	
	/**
	 * Type de l'interface graphique: 1=ancienne (avant), 0=nouvelle (après)
	 */
	int type=0;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public Integer getNbReservationsEnAttente() {
		return nbReservationsEnAttente;
	}
	public void setNbReservationsEnAttente(Integer nbReservationsEnAttente) {
		this.nbReservationsEnAttente = nbReservationsEnAttente;
	}
	@Override
	public String toString() {
		return "Appartement [id=" + id + ", nom=" + nom + ", nbReservationsEnAttente=" + nbReservationsEnAttente + "]";
	}
	public Map<Long, Reservation> getReservations() {
		return reservations;
	}
	public void setReservations(Map<Long, Reservation> reservations) {
		this.reservations = reservations;
	}
	
	
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void ajouterReservation(Reservation reservation){
		if(reservations == null){
			reservations = new HashMap<Long, Reservation>();
		}
		reservations.put(reservation.getId(), reservation);
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public boolean isCliquerPourMoteurRechercheReservation() {
		return cliquerPourMoteurRechercheReservation;
	}
	public void setCliquerPourMoteurRechercheReservation(boolean cliquerPourMoteurRechercheReservation) {
		this.cliquerPourMoteurRechercheReservation = cliquerPourMoteurRechercheReservation;
	}
	public boolean isReservationEstEnHaut() {
		return reservationEstEnHaut;
	}
	public void setReservationEstEnHaut(boolean reservationEstEnHaut) {
		this.reservationEstEnHaut = reservationEstEnHaut;
	}
	
	
	
}
