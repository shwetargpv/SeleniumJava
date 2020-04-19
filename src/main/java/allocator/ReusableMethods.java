package allocator;

import static allocator.WebConnector.driver;
import static allocator.WebConnector.prop;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import io.cucumber.core.api.Scenario;

public class ReusableMethods {
	public static String randomProduct;
	public static String productDetails;
	public void saveScreenshotsForScenario(final Scenario scenario) {
		final byte[] screenshot = ((TakesScreenshot) driver)
				.getScreenshotAs(OutputType.BYTES);
		scenario.embed(screenshot, "image/png");
	}

	public void waitForPageLoad(int timeout){
		ExpectedConditions.jsReturnsValue("return document.readyState==\"complete\";");
	}

	public String getSpecificColumnData(String FilePath, String SheetName, String ColumnName) throws InvalidFormatException, IOException {
		FileInputStream fis = new FileInputStream(FilePath);
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFSheet sheet = workbook.getSheet(SheetName);
		XSSFRow row = sheet.getRow(0);
		int col_num = -1;
		for(int i=0; i < row.getLastCellNum(); i++)
		{
			if(row.getCell(i).getStringCellValue().trim().equals(ColumnName))
				col_num = i;
		}
		row = sheet.getRow(1);
		XSSFCell cell = row.getCell(col_num);
		String value = cell.getStringCellValue();
		fis.close();
		System.out.println("Value of the Excel Cell is - "+ value);    	 
		return value;
	}

	public void setSpecificColumnData(String FilePath, String SheetName, String ColumnName) throws IOException{
		FileInputStream fis;
		fis = new FileInputStream(FilePath);
		FileOutputStream fos = null;
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFSheet sheet = workbook.getSheet(SheetName);
		XSSFRow row = null;
		XSSFCell cell = null;
		XSSFFont font = workbook.createFont();
		XSSFCellStyle style = workbook.createCellStyle();
		int col_Num = -1;
		row = sheet.getRow(0);
		for(int i = 0; i < row.getLastCellNum(); i++)
		{
			if(row.getCell(i).getStringCellValue().trim().equals(ColumnName))
			{
				col_Num = i;
			}
		}
		row = sheet.getRow(1);
		if(row == null)
			row = sheet.createRow(1);
		cell = row.getCell(col_Num);
		if(cell == null)
			cell = row.createCell(col_Num);
		font.setFontName("Comic Sans MS");
		font.setFontHeight(14.0);
		font.setBold(true);
		font.setColor(HSSFColor.WHITE.index);
		style.setFont(font);
		style.setFillForegroundColor(HSSFColor.GREEN.index);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cell.setCellStyle(style);
		cell.setCellValue("PASS");
		fos = new FileOutputStream(FilePath);
		workbook.write(fos);
		fos.close();
	}
	public void generateRandomIndex() {
		Random random = new Random();
		ArrayList<String> ProductsList = new ArrayList<String>();
		String ProductsArr[] = prop.getProperty("ProductList").split(",");
		ProductsList.addAll(Arrays.asList(ProductsArr));
		int index = random.nextInt(ProductsList.size());
		randomProduct = ProductsList.get(index).trim();
	}
	
	public By getElementWithLocator(String AppElement) throws Exception {
		String locatorTypeAndValue = prop.getProperty(AppElement);
		String[] locatorTypeAndValueArray = locatorTypeAndValue.split(",",2);
		String locatorType = locatorTypeAndValueArray[0].trim();
		String locatorValue = locatorTypeAndValueArray[1].trim();
		if(locatorValue.contains("ObjectToken")){
			generateRandomIndex();
			locatorValue = locatorValue.replaceAll("ObjectToken", randomProduct);
		}
		switch (locatorType.toUpperCase()) {
		case "ID":
			return By.id(locatorValue);
		case "NAME":
			return By.name(locatorValue);
		case "XPATH":
			return By.xpath(locatorValue);
		case "CLASS":
			return By.className(locatorValue);
		default:
			return null;
		}
	}

	public WebElement FindAnElement(String AppElement) throws Exception{
		return (MobileElement) driver.findElement(getElementWithLocator(AppElement));
	}

	public String getTextFromElement(String AppElement) throws Exception{
		String elementText;
		 if(FindAnElement(AppElement).getText()!=null){
			 elementText=FindAnElement(AppElement).getText();
		   }
		   else{
			   elementText=FindAnElement(AppElement).getAttribute("text").trim(); 
		   }
		return elementText;
	}
		
	public int numberOfElements(String AppElement) throws Exception
	{
		List<MobileElement> we=driver.findElements(getElementWithLocator(AppElement));
		return we.size();
	}
	
	public void PerformActionOnElement(String AppElement, String Action, String Text) throws Exception {
		switch (Action) {
		case "Click":
			FindAnElement(AppElement).click();
			break;
		case "Type":
			FindAnElement(AppElement).sendKeys(Text);
			Thread.sleep(5000);
			break;
		case "Clear":
			FindAnElement(AppElement).clear();
			break;
		case "WaitForElementDisplay":
			waitForCondition("Presence",AppElement,60);
			break;
		case "WaitForElementClickable":
			waitForCondition("Clickable",AppElement,60);
			break;
		case "ElementNotDisplayed":
			waitForCondition("NotPresent",AppElement,60);
			break;
		default:
			throw new IllegalArgumentException("Action \"" + Action + "\" isn't supported.");
		}
	}
	
	public boolean waitForCondition(String TypeOfWait, String AppElement, int Time){
		boolean conditionMeet = false;
		try {
			Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Time, TimeUnit.SECONDS).pollingEvery(5, TimeUnit.SECONDS).ignoring(Exception.class);
			switch (TypeOfWait)
			{
			case "Clickable":
				wait.until(ExpectedConditions.elementToBeClickable(FindAnElement(AppElement)));
				conditionMeet=true;
				break;
			case "Presence":
				wait.until(ExpectedConditions.presenceOfElementLocated(getElementWithLocator(AppElement)));
				conditionMeet=true;
				break;
			case "Visibility":
				wait.until(ExpectedConditions.visibilityOfElementLocated(getElementWithLocator(AppElement)));
				conditionMeet=true;
				break;
			case "NotPresent":
				wait.until(ExpectedConditions.invisibilityOfElementLocated(getElementWithLocator(AppElement)));
				conditionMeet=true;
				break;
			}
		}
		catch(Exception e)
		{
			throw new IllegalArgumentException("wait For Condition \"" + TypeOfWait + "\" isn't supported.");
		}
		return conditionMeet;
	}
}
