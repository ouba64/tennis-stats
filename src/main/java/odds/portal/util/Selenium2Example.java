package odds.portal.util;


import java.util.ArrayList;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;


public class Selenium2Example  {
    public static void main(String[] args) {
        // Create a new instance of the Firefox driver
        // Notice that the remainder of the code relies on the interface, 
        // not the implementation.
        WebDriver driver = new FirefoxDriver();

        // And now use this to visit Google
        driver.get("http://www.google.com");
        // Alternatively the same thing can be done like this
        // driver.navigate().to("http://www.google.com");

        // Find the text input element by its name
        WebElement element = driver.findElement(By.name("q"));

       // Enter something to search for
        element.sendKeys("Cheese!");

        // Now submit the form. WebDriver will find the form for us from the element
        element.submit();

        // Check the title of the page
        System.out.println("Page title is: " + driver.getTitle());
        
        // Google's search is rendered dynamically with JavaScript.
        // Wait for the page to load, timeout after 10 seconds
        (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getTitle().toLowerCase().startsWith("cheese!");
            }
        });
      
     /*   WebElement element;
        String baseUrl = "http://www.ebay.fr";
        driver.get(baseUrl + "/sch/m.html?_nkw=&_armrs=1&_from=&_ssn=zoomici2&_sop=10&_pgn=8&_skc=350&rt=nc");
       
        
        element = driver.findElement(By.linkText("NouveauDevine : Edition bilingue français-arabe - Editions Marsam - 24pages. NEUF"));
        
        //element = driver.findElement(By.xpath("//*[@id=\"ResultSetItems\"]/table[2]/tbody/tr/td[1]/div/div/div/a/img"));
       // element.click();
        
        String selectAll = Keys.chord(Keys.CONTROL,Keys.RETURN);
        element.sendKeys(selectAll);

        // Should see: "cheese! - Google Search"
        System.out.println("Page title is: " + driver.getTitle());
        
        try {
			Thread.sleep(11000);
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        ArrayList<String> tabs2 = new ArrayList<String> (driver.getWindowHandles());
        driver.switchTo().window(tabs2.get(1));
        driver.close();
        driver.switchTo().window(tabs2.get(0));*/
        //Close the browser
        driver.quit();
    }
}