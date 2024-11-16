package Practice1;

import static org.testng.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class LoginTest {
	 static /*
	 * Open the Website 
	 * Verify the PageTittle is correctly opened or not
	 * click in Seachbar and enter "Login" in the Search box + click on enter
	 * click on Test Login page
	 * Verify the Tittle page correctly
	 * Verify the Page 
	 * Highlight the Username and password filed
	 * Find the Username & password credentials in the same page and type it Usename textbox
	 * use explict wait for find + then use Displayed + click on login
	 */
	WebDriver driver;
	WebDriverWait wait;
	private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);
	public static final long GLOBAL_DELAY = 1;
	 private static final String HUB_URL = "http://localhost:4444/wd/hub";
 /*   @BeforeTest
    void Setpage()  {
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--incognito");
    options.setExperimentalOption("excludeSwitches", new String[] {"enable-automation"});
    	driver = new ChromeDriver(options);
        driver.manage().deleteAllCookies();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofMinutes(1));
        driver.get("https://practice.expandtesting.com/");
    }*/
	
	 @Parameters({"browser", "incognito"})
	    @BeforeMethod
//	    public void setUp(String browser) throws MalformedURLException {
//	    public void setUp(@Optional("chrome") String browser) throws MalformedURLException {
	    public void setUp(@Optional("chrome") String browser, @Optional("false") String incognito) throws MalformedURLException {
	        // Set up desired capabilities based on the browser parameter
	        DesiredCapabilities capabilities = new DesiredCapabilities();
	        switch (browser.toLowerCase()) {
	            case "chrome":
	            	 ChromeOptions chromeOptions = new ChromeOptions();
	                 if ("true".equalsIgnoreCase(incognito)) {
	                     chromeOptions.addArguments("--incognito"); // Adding Incognito argument if passed
	                     chromeOptions.setExperimentalOption("excludeSwitches", new String[] {"enable-automation"});
	                 }
	                 capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
	                 break;
//	                capabilities.setBrowserName("chrome");
//	                break;
	            case "firefox":
	                capabilities.setBrowserName("firefox");
	                break;
	            case "edge":
	                capabilities.setBrowserName("MicrosoftEdge");
	                break;
	            default:
	                throw new IllegalArgumentException("Unsupported browser: " + browser);
	        }

	        // Initialize WebDriver with Grid Hub URL and desired capabilities
	        driver = new RemoteWebDriver(new URL(HUB_URL), capabilities);
//	        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
	        driver.manage().deleteAllCookies();
	        driver.manage().window().maximize();
//	        driver.manage().timeouts().implicitlyWait(Duration.ofMinutes(1));
	        driver.get("https://practice.expandtesting.com/");
	   
	        }
    @Test
    void Teststart() throws InterruptedException{
         // Search for 'Login'
        driver.findElement(By.xpath("//input[@placeholder='Search an example...']")).sendKeys("Login");
        driver.findElement(By.xpath("//button[normalize-space()='Search']")).click();
        Thread.sleep(GLOBAL_DELAY);
        

        // Find and highlight the element
        WebElement element = driver.findElement(By.xpath("//a[normalize-space()='Test Login Page']"));
        highlightElement(driver, element); // Highlight the element
//        Thread.sleep(5000); // Wait for 5 seconds

        // Click the highlighted element using JavaScript
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        Thread.sleep(GLOBAL_DELAY);

        // Validate the page title
        assert driver.getTitle().equals("Test Login Page for Automation Testing Practice");
        System.out.println(driver.getTitle());
        isTextPresent("Register");
        isTextPresent("You can use this login page for practicing with Selenium or other tools like Playwright, Cypress, etc.");
        System.out.println("iavaga");
        String username = driver.findElement(By.xpath("(//div[@class='col-9']//b)[2]")).getText();
        System.out.println(username);
        String password = driver.findElement(By.xpath("(//div[@class='col-9']//b)[3]")).getText();
        System.out.println(password);
        SendText("(//label[text()='Username']/following::input)[1]" , username);
        SendText("//label[text()='Password']/following::input", password);
        waitForVisibilityThenClick("//button[text()='Login']");
        System.out.println("done");
        String profile_name = driver.findElement(By.xpath("//div[@class='container']//h3[1]")).getText();
        assertEquals(profile_name, "Hi, "+username+"!");
        waitForVisibilityThenClick("//a[contains(.,'Logout')]");
        System.out.println("done");
     
   }

    // Highlight function to change the element's background and border
    private static void highlightElement(WebDriver driver, WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        // Change the element's background to yellow and border to red
        js.executeScript("arguments[0].style.border='3px solid red'; arguments[0].style.backgroundColor='yellow';", element);
    }
   
    public boolean isTextPresent(String text) {
        try {
            boolean isPresent = driver.getPageSource().contains(text);
          System.out.println(isPresent);
            if (isPresent) {
                System.out.println("Text '" + text + "' is present on the page.");
            } else {
                System.out.println("Text '" + text + "' is NOT present on the page.");
            }
            return isPresent;
        } catch (Exception e) {
            System.out.println("Error while verifying text: " + e.getMessage());
            return false;
        }
    
    }
    public static void waitForVisibilityThenClick(String xpath) {
    	try {
      
    	WebDriverWait wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);

         // Create the By locator using the full XPath string
         By locator = By.xpath(xpath);

         // Wait for the visibility of the element
         WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
         highlightElement(driver, element);
        Thread.sleep(3000);
         // Click the element once it's visible
        try { element.click();
//        Thread.sleep(1000);
					} catch (Exception e) {
			// TODO: handle exception
			  ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
//		        Thread.sleep(1000);
		}
         System.out.println("Element clicked: " + xpath);
     } catch (Exception e) {
         throw new RuntimeException("Error while waiting for visibility and clicking element: " + xpath, e);
     }

    }
    
    public static void SendText(String xpath, String value) {
        try {
            // Locate the textbox element
            WebElement textbox = driver.findElement(By.xpath(xpath));
            
            // Highlight the textbox element
            highlightElement(driver, textbox);

            // Set the value into the textbox
            textbox.clear();  // Clear any existing text
            Thread.sleep(1000);
            textbox.sendKeys(value);  // Enter the new value
        } catch (Exception e) {
            System.out.println("Error while interacting with the textbox: " + e.getMessage());
        }}
    
    @AfterTest
    void Closetest() {
    	
    	driver.quit();
    }
}
