# Description
Ce programme permet de r�cup�rer des statistiques sur des matchs de Tennis jou�s depuis une certaine date
du site oddsportal.com. C'est donc un scrapper extremement avanc� car il g�re des actions javascript.
Les statistiques sont ensuite d�pos�e dans un fichier excel.

C'est un projet Eclipse
Pour rouler le programme, il faut faire maven install
Puis on execute Gui.java
Il ne faut oublier de corriger le chemin dans la dependance "jdk.tools" dans le pom.xml et l'adapter � sa propre machine. 
Si votre eclipse d�marre le jdk au lieu du jre, vous pouvez carr�ment enlever cette d�pendance:
Valeur actuelle : <systemPath>C:/Program Files/Java/jdk1.8.0_20/lib/tools.jar</systemPath>