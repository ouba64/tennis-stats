package odds.portal;

public class Tournoi {
	String url;
	String nom;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public Tournoi(String url, String nom) {
		super();
		this.url = url;
		this.nom = nom;
	}

}
