package demo;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;
import java.util.logging.Level;

import demo.wrappers.Wrappers;

/**
 * TestCases class contains test methods for automated testing of Flipkart website.
 */
public class TestCases {

    ChromeDriver driver;

    /**
     * Setup method to initialize ChromeDriver and configure options before tests.
     */
    @BeforeTest
    public void startBrowser() {
        // Configure logging properties
        System.setProperty("java.util.logging.config.file", "logging.properties");

        // Configure Chrome options for WebDriver
        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logs = new LoggingPreferences();
        logs.enable(LogType.BROWSER, Level.ALL);
        logs.enable(LogType.DRIVER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logs);
        options.addArguments("--remote-allow-origins=*");

        // Set ChromeDriver log file location
        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "build/chromedriver.log");

        // Initialize ChromeDriver with configured options
        driver = new ChromeDriver(options);

        // Maximize browser window
        driver.manage().window().maximize();
    }

    /**
     * Test case to search for products on Flipkart and validate star ratings.
     *
     * @throws InterruptedException if thread sleep is interrupted
     */
    @Test
    public void testCase01() throws InterruptedException {
        System.out.println("Start Test case: testCase01");
        driver.get("https://www.flipkart.com/");

        // Delay to wait for page to load
        Thread.sleep(5000);

        // Search for products using Wrappers utility class
        WebElement searchForProducts = driver.findElement(By.xpath("//input[@class='Pke_EE']"));
        Wrappers.sendKeys(driver, searchForProducts, "Washing Machine");
        WebElement searchButton = driver.findElement(By.xpath("//button[@type='submit']"));
        Wrappers.click(driver, searchButton);

        // Wait for and interact with the "Popularity" element
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        WebElement popularity = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[text()='Popularity']")));
        Wrappers.click(driver, popularity);

        // Handle dynamic search results
        List<WebElement> searchResults = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='XQDdHH']")));
        int count = 0;

        // Loop through search results and count products with <= 4 stars
        for (int i = 0; i < searchResults.size(); i++) {
            try {
                searchResults = driver.findElements(By.xpath("//div[@class='XQDdHH']"));
                WebElement res = searchResults.get(i);
                String starRatingStr = res.getText();
                double starRatingDou = Double.parseDouble(starRatingStr);
                if (starRatingDou <= 4) {
                    count++;
                }
            } catch (Exception e) {
                System.out.println("Exception caught. Re-locating the element.");
                searchResults = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='XQDdHH']")));
                i--;
            }
        }
        System.out.println("The count of products with less than or equal to 4 stars is: " + count);

        System.out.println("End Test case: testCase01");
    }

    /**
     * Test case to search for iPhone on Flipkart and validate discount percentages.
     *
     * @throws InterruptedException if thread sleep is interrupted
     */
    @Test
    public void testCase02() throws InterruptedException {
        System.out.println("Starting testCase02");
        driver.get("https://www.flipkart.com/");

        // Delay to wait for page to load
        Thread.sleep(5000);

        // Close login popup using Wrappers utility class
        WebElement closeLogin = driver.findElement(By.xpath("//span[@role='button']"));
        Wrappers.click(driver, closeLogin);

        // Search for products using Wrappers utility class
        WebElement searchBox = driver.findElement(By.xpath("//input[contains(@title,'Search for Products')]"));
        Wrappers.sendKeys(driver, searchBox, "iPhone");
        String enteredText = searchBox.getAttribute("value");
        Assert.assertEquals(enteredText, "iPhone", "The text 'iPhone' was not entered correctly in the search box.");

        // Click on search button using Wrappers utility class
        WebElement searchClick = driver.findElement(By.xpath("//button[contains(@aria-label,'Search')]"));
        Wrappers.click(driver, searchClick);

        // Wait for product cards to load
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='yKfJKb row']")));

        // Loop through product cards and validate discount percentages
        List<WebElement> productCards = driver.findElements(By.xpath("//div[@class='tUxRFH']"));
        for (WebElement cards : productCards) {
            try {
                WebElement titleOfProduct = cards.findElement(By.xpath("//div[@class='KzDlHZ']"));
                String title = titleOfProduct.getText();
                WebElement discountElement = cards.findElement(By.xpath("//div[@class='UkUFwK']"));
                String discountText = discountElement.getText();
                String discountValueStr = discountText.replaceAll("[^0-9]", "");
                int discountValue = Integer.parseInt(discountValueStr.trim());
                if (discountValue > 17) {
                    System.out.println("title =" + title);
                    System.out.println("Discount percent=" + discountValue);
                }
            } catch (Exception e) {
                System.out.println("Exception caught while processing product card.");
            }
        }

        System.out.println("End testCase02");
    }

    /**
     * Test case to search for Coffee Mug on Flipkart and interact with review filters.
     *
     * @throws InterruptedException if thread sleep is interrupted
     */
    @Test
    public void testCase03() throws InterruptedException {
        System.out.println("Start testcase 03");
        driver.get("https://www.flipkart.com/");
        Thread.sleep(2000);

        // Search for products using Wrappers utility class
        WebElement searchForProducts = driver.findElement(By.xpath("//input[@class='Pke_EE']"));
        Wrappers.sendKeys(driver, searchForProducts, "Coffee Mug");
        WebElement searchButton = driver.findElement(By.xpath("//button[@type='submit']"));
        Wrappers.click(driver, searchButton);

        // Wait for and interact with the "4★ & above" checkbox
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        try {
            WebElement fourStarAndAbove = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[text()='4★ & above']")));
            System.out.println("Found 4-star checkbox - Displayed: " + fourStarAndAbove.isDisplayed() + ", Enabled: " + fourStarAndAbove.isEnabled() + ", Location: " + fourStarAndAbove.getLocation());

            // Scroll to view and click the "4★ & above" checkbox
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", fourStarAndAbove);
            if (fourStarAndAbove.isDisplayed() && fourStarAndAbove.isEnabled()) {
                Wrappers.click(driver, fourStarAndAbove);
                System.out.println("Clicked on 4-star and above checkbox");
            } else {
                System.out.println("4-star and above checkbox is not clickable");
            }
        } catch (Exception e) {
            System.out.println("Failed to interact with the 4-star and above checkbox: " + e.getMessage());
            throw e;
        }

        // Process review ratings
        List<WebElement> reviews = driver.findElements(By.xpath("//span[@class='Wphh3N']"));
        for (int i = 0; i < reviews.size(); i++) {
            WebElement rev = reviews.get(i);
            String reviewString = rev.getText();
            String revNum = reviewString.replaceAll("[^0-9]", "");
            int revNumInt = Integer.parseInt(revNum);
            System.out.println(revNumInt);
        }

        System.out.println("End testcase 03");
    }

    /**
     * Cleanup method to close the browser after tests.
     */
    @AfterTest
    public void endTest() {
        driver.close();
        driver.quit();
    }
}
