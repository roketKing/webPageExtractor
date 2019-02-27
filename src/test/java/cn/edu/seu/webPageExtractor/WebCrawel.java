package cn.edu.seu.webPageExtractor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WebCrawel {
    private WebDriver driver;
    String baseUrl;
    private boolean acceptNextAlert = true;
    private StringBuffer verificationErrors = new StringBuffer();

    @Before
    public void setUp() throws Exception {
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        driver = new ChromeDriver();
        //baseUrl ="https://list.tmall.com/search_product.htm?q=%BB%AA%CE%AA%CA%D6%BB%FA&type=p&vmarket=&spm=875.7931836%2FB.a2227oh.d100&xl=huawei+_2&from=mallfp..pc_1_suggest";
        baseUrl = "https://www.baidu.com/s?ie=utf-8&f=3&rsv_bp=0&rsv_idx=1&tn=baidu&wd=%E6%9C%AB%E6%97%A5%E5%AD%A4%E8%88%B0%E7%AC%AC%E4%BA%94%E5%AD%A3&rsv_pq=89727747000299f4&rsv_t=5a3agj9pGdeVKF5ncfVW%2FGM1FHyYZNjHXjJ08j4c0vK%2F5o9H0yXw3jGEz%2BI&rqlang=cn&rsv_enter=1&rsv_sug3=1&rsv_sug1=1&rsv_sug7=001&rsv_sug2=1&rsp=0&rsv_sug9=es_1_1&rsv_sug4=1562&rsv_sug=9";
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    @Test
    public void testUntitledTestCase() throws Exception {
        driver.get(baseUrl);
        driver.manage().window().maximize();
        WebElement element = driver.findElement(By.partialLinkText("下一页"));
        while (element != null) {
            List<WebElement> aLinkList = driver.findElements(By.partialLinkText("末日孤舰第五季"));
            List<String> linkResult = new ArrayList<>();
            for (WebElement e : aLinkList) {
                linkResult.add(e.getAttribute("href"));
            }
            element.click();
            element = driver.findElement(By.partialLinkText("下一页"));

        }
        ////*[@id="content"]/div[2]/div[8]/div/b[1]/a[3]
        //*[@id="content"]/div[2]/div[8]/div/b[1]/a[3]
        Actions actions = new Actions(driver);

        actions.click(element).perform();


        System.out.println("----over---");
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }
    }

    private boolean isElementPresent(By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private boolean isAlertPresent() {
        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }

    private String closeAlertAndGetItsText() {
        try {
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            if (acceptNextAlert) {
                alert.accept();
            } else {
                alert.dismiss();
            }
            return alertText;
        } finally {
            acceptNextAlert = true;
        }
    }
}

