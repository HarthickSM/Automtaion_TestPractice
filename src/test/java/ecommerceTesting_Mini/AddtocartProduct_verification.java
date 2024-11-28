package ecommerceTesting_Mini;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class AddtocartProduct_verification {

	WebDriver driver;
	WebDriverWait wait;
	private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);
	public static final long GLOBAL_DELAY = 10000;
	private static final String HUB_URL = "http://localhost:4444/wd/hub";

	@BeforeTest
	void Setpage() {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--incognito");
		options.setExperimentalOption("excludeSwitches", new String[] { "enable-automation" });
		driver = new ChromeDriver(options);
		driver.manage().deleteAllCookies();
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Duration.ofMinutes(10));
//		1. Open the browser and navigate to Amazon/Flipkart
		driver.get("https://www.flipkart.com/");
	}

	@Test(priority = 1)
	void Searchbox() throws InterruptedException {
// 		close the Login popup if it appers
		System.out.println(" flipkart opened");
//		Search for a product (e.g., "laptop")

		try {
			Actions actions = new Actions(driver);
			WebElement searchbox = driver
					.findElement(By.xpath("//input[@placeholder='Search for Products, Brands and More']"));
			if (searchbox.isEnabled()) {
				searchbox.clear();
				searchbox.sendKeys("Laptop");
				System.out.println("text sent to the Search Box");
				actions.sendKeys(searchbox, Keys.ENTER).perform();
			} else {
				System.out.println("text not sent to the Search Box");
			}

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Search Box element is not enabled");
		} 

	}

	@Test(priority = 2)
	void filter() throws InterruptedException {
//		Filter the results by a specific brand
		// click on Brand filer
		driver.findElement(By.xpath("//div[text()='Brand']")).click();
		Thread.sleep(2000);

		// Locate the checkbox element
		WebElement brandfilter = driver.findElement(By.xpath(("//div[text()='HP']")));
		brandfilter.click();
//		System.out.println("---------------" + driver.getPageSource() + "---------------");

		// Check if the text "HP LAPTOP" is present
		boolean isTextPresent = driver.getPageSource().contains("Hp");
		if (isTextPresent) {
			System.out.println("Text 'HP LAPTOP' is present on the page.");
		} else {
			System.out.println("Text 'HP LAPTOP' is not present on the page.");
		}

		// Retrieve & print names and prices of first 5 products = <div class="cPHDOP
		// col-12-12">
		Thread.sleep(5000);
		List<WebElement> productElements = driver
				.findElements(By.xpath("(//div[@class='col col-7-12']//div[@class='KzDlHZ'])"));
		List<WebElement> priceElements = driver
				.findElements(By.xpath("(//div[@class='col col-5-12 BfVC2z']//div[@class='Nx9bqj _4b5DiR'])"));
		// Loop through the first 5 product elements and print their text
		for (int i = 0; i < 5 && i < productElements.size(); i++) {
			String productText = productElements.get(i).getText();
			if (productText.length() > 15) {
				productText = productText.substring(0, 15) + "...";
			}
			String priceElementsText = priceElements.get(i).getText();
			System.out.println("Product Details: " + productText + "| " + "price: " + priceElementsText);
		}
		Thread.sleep(5000);
		// Add the First Product to the Cart
		WebElement FirstProduct = driver
				.findElement(By.xpath("(//div[@class='col col-7-12']//div[@class='KzDlHZ'])[1]"));
		System.out.println(FirstProduct.getText());
		FirstProduct.click();
		Thread.sleep(2000);
		// Switch to the new Tab
		for (String handle : driver.getWindowHandles()) {
			driver.switchTo().window(handle);
		}
		System.out.println(driver.getTitle());
		Thread.sleep(2000);
		WebElement Addtocartbutton = driver.findElement(By.xpath("//button[normalize-space()='Add to cart']"));

		if (Addtocartbutton.isDisplayed()) {
			System.out.println("the Button finded");

		} else {
			System.out.println("the Button not finded");
		}
		// Verify the product is added to the cart
		WebElement Cart = driver.findElement(By.xpath("//span[text()='Cart']"));
		if (Cart.isDisplayed()) {
			System.out.println("Product Successfully added to the cart!");
		} else {
			System.out.println("Product not added to the cart");
		}
		Cart.click();

		Thread.sleep(GLOBAL_DELAY);
		System.out.println("Done , done");
	}

	@AfterTest
	void Closetest() {

		driver.quit();
	}
}
