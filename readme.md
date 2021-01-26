# Description
Ce programme permet de récupérer des statistiques sur des matchs de Tennis joués depuis une certaine date
du site oddsportal.com. C'est donc un scrapper extremement avancé car il gère des actions javascript.
Les statistiques sont ensuite déposée dans un fichier excel.

C'est un projet Eclipse
Pour rouler le programme, il faut faire maven install
Puis on execute Gui.java
Il ne faut oublier de corriger le chemin dans la dependance "jdk.tools" dans le pom.xml et l'adapter à sa propre machine. 
Si votre eclipse démarre le jdk au lieu du jre, vous pouvez carrément enlever cette dépendance:
Valeur actuelle : <systemPath>C:/Program Files/Java/jdk1.8.0_20/lib/tools.jar</systemPath>