package allocator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.remote.DesiredCapabilities;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.cucumber.core.api.Scenario;

public class WebConnector<V> {

	public static WebDriver driver=null;
	public static Properties prop = new Properties();
	ReusableMethods RM= new ReusableMethods();

	public WebConnector(){
		try {
			prop.load( new FileInputStream("./src/test/config/application.properties") );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setUpDriver() throws Exception{
		String browser = prop.getProperty("browser");
		if (browser == null) {
			browser = "chrome";
		}
		switch (browser) {
        case "chrome":
        	System.setProperty("webdriver.chrome.driver","./src/test/lib/chromedriver.exe");
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("start-maximized");
            driver = new ChromeDriver(chromeOptions);
            break;
        case "firefox":
        	System.setProperty("webdriver.gecko.driver","./src/test/lib/geckodriver.exe");
            driver = new FirefoxDriver();
            driver.manage().window().maximize();
             //session = ((FirefoxDriver)driver).getSessionId();
            break;
        default:
            throw new IllegalArgumentException("Browser \"" + browser + "\" isn't supported.");
    }
}

public void closeDriver(Scenario scenario){
    if(scenario.isFailed()){
      // saveScreenshotsForScenario(scenario);
    }
    driver.close();
}}
