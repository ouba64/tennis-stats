package odds.portal.enums;

public enum Constants {
	LOGIN_URL ("https://www.oddsportal.com/login/"),
	ARCHIVED_RESULTS("https://www.oddsportal.com/results/#soccer"),
    ACCUSE_RECEPTION("unconfirmed_overview"),
    PAGE_0  ("/sch/m.html?_nkw=&_armrs=1&_ipg=&_from=&_ssn=zoomici2&_sop=10"),
    XPATH_RESULTAT_MOITIE_1("//*[@id=\"ResultSetItems\"]/table["),
    XPATH_RESULTAT_MOITIE_2("]/tbody/tr/td[1]/div/div/div/a"),
    APPLICATION_DIR_NAME("production"), 
    BASE_URL("https://admin.booking.com");

    private final String string;   
    Constants(String string) {
    	this.string = string;
    }
	public String texte() {
		return string;
	}
}
