import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.bcel.generic.Select;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.server.handler.SendKeys;


public class Domains {
	
	public static void main(String[] args) throws InterruptedException, IOException, SQLException{
		checkWhois();		
	}
	
	public static void checkWhois() throws SQLException, InterruptedException{
		DB db = new DB();
		
		// Navigate to web site
		WebDriver driver = new FirefoxDriver();		
		driver.get("https://hostmaster.ua/?domadv");
		Thread.sleep(3000);
						
		WebElement blockDomains = driver.findElement(By.name("ua_public_domains"));
		List<WebElement>listDomains = blockDomains.findElements(By.tagName("option"));
		ArrayList<String> accepted_paths = new ArrayList<String>();
		for(WebElement listDomain : listDomains){
			accepted_paths.add(listDomain.getAttribute("value"));
		}
		
		ArrayList<String> domains = db.getData();
		for(String domain : domains){			
			Map<String, String> domain_info = getInfoAboutDomain(domain);
			// Domain input
			WebElement name_input = driver.findElement(By.name("domainname"));
			org.openqa.selenium.support.ui.Select domain_input = new org.openqa.selenium.support.ui.Select(driver.findElement(By.name("ua_public_domains")));
			WebElement checkButton = driver.findElement(By.cssSelector("input[value='Проверить']"));
						
			if(domain_info != null){				
				if(accepted_paths.contains(domain_info.get("path"))){
					name_input.sendKeys(domain_info.get("name"));
					domain_input.selectByValue(domain_info.get("path"));					
					Thread.sleep(1000);
					checkButton.click();
					Thread.sleep(2000);
					WebElement whois_table = driver.findElement(By.id("whois"));
					String whois_data = whois_table.getAttribute("innerHTML");
					whenExpires(whois_data, domain_info.get("path"));
					break;
				}								
			}			
		}
		
		driver.quit();
	}
	
	public static String whenExpires(String data, String path){
		String expires = null;
		System.out.println(data);
		return expires;
	}
	
	public static Map<String, String> getInfoAboutDomain(String domain){
		Map<String, String> domain_info = new HashMap<String, String>();
		if(domain.indexOf(".") != -1){
			String domain_path = "";
			String[] domain_parts = domain.split("\\.");				
			String domain_name = null;
			for(String dom : domain_parts){
				domain_name = dom;
				break;
			}
			
			int p_size = domain_parts.length;
			
			for (int i = 1; i < p_size; i++) {
				domain_path += domain_parts[i];
				if(i < (p_size-1)){
					domain_path += ".";
				}
			}			
			
			domain_info.put("name", domain_name);
			domain_info.put("path", domain_path);
		}
		return domain_info;
	}
	
	public static void getDomains(String keyword) throws InterruptedException, SQLException{
		WebDriver driver = new FirefoxDriver();
		
		DB db = new DB();
		
		driver.get("http://yandex.ua/");
		Thread.sleep(2000);
		
		WebElement search_place = driver.findElement(By.id("text"));
		search_place.sendKeys(keyword);
		Thread.sleep(1000);
		WebElement but = driver.findElement(By.className("suggest2-form__button"));
		but.click();
		
		Thread.sleep(3000);
		
		ArrayList<String> res_domains = new ArrayList<String>();
		
		
		for (int i = 1; i < 90; i++) {
			
			ArrayList<WebElement> domains = (ArrayList<WebElement>) driver.findElements(By.cssSelector(".serp-url__link"));
			for(WebElement domain : domains){
				String line = domain.getAttribute("href");
				if(line.indexOf("yabs") == -1 && line.indexOf("yandex") == -1){
					if(line.indexOf(".ua") == -1){
						continue;
					}
					if(line.indexOf("//") != -1){
						line = line.split("//")[1];					
					}
					if(line.indexOf("www.") != -1){
						line = line.split("www.")[1];					
					}
					if(line.indexOf("/") != -1){
						line = line.split("/")[0];
					}
					
					res_domains.add(line);
				}
				
			}
			
			Thread.sleep(1000);
			
			String next_page = Keys.chord(Keys.LEFT_CONTROL, Keys.ARROW_RIGHT);
			WebElement next_area = driver.findElement(By.tagName("body"));
			next_area.sendKeys(next_page);			
			
			Thread.sleep(2000);
		}
		
		// Record to DataBase
		
		HashSet<String> res_domains_hash = new HashSet<String>();
		res_domains_hash.addAll(res_domains);
		res_domains.clear();
		res_domains.addAll(res_domains_hash);
		db.record(res_domains);
		
		res_domains.clear();
		
		driver.quit();
	}

}
