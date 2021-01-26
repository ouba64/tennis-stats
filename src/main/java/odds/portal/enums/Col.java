package odds.portal.enums;

public enum Col {
	NoMatch(0),
	Date(1),
	Tournoi(2),
	NomJ1(3),
	NomJ2(4),
	CoteJ1(5),
	ScoreSet(7),
	NombreSet(17),
	AH01Valeur(19),
	Set01Valeur(99),
	OverUnder01Valeur(115), 	
	;
    private final int c; 
	Col(int no){
		this.c = no;
	}
	public int getC() {
		return c;
	}
	
	
}
