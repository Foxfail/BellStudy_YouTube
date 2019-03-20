
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.awt.*;
import java.util.List;
import java.awt.datatransfer.StringSelection;
import java.util.concurrent.TimeUnit;

public class YouTubeTest {

    private static RemoteWebDriver driver;
    private static Actions actions;
    String login = "ivanovivanbmw@lenta.ru";
    String password = "Test1234567890";

    @BeforeClass
    public static void beforeClass() {
        System.out.println("Начинаю тест YouTube");
        //открываю браузер

        try {
            System.out.print("Загружаю драйвер...");

//            закоменченные ниже строки работают если запускать драйвер вручную
//            URL chromeDriverUrl = new URL("http://localhost:9515");
//            driver = new RemoteWebDriver(chromeDriverUrl, new ChromeOptions());

            System.setProperty("webdriver.chrome.driver", "lib/chromedriver.exe");
            driver = new ChromeDriver();
            System.out.println("Загружено");
        } catch (Exception e) {
            System.out.println("Ошибка!!!");
            e.printStackTrace();
        }

        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);

        actions = new Actions(driver);
    }

    @Test
    public void test() throws InterruptedException {
        System.out.println("1. Перейдите на сайт https://www.youtube.com/");
        driver.get("https://www.youtube.com/");

        System.out.println("2.\tВыполните авторизацию на сервисе");
        WebElement loginBtn = driver.findElementByXPath("//*[@id=\"buttons\"]/ytd-button-renderer/a");
        // поскольку мы используем тествое ПО то авторизация каждый раз должна проходить заново,
        // поэтому это if не является необходимым
        if (loginBtn.getText().toLowerCase().equals("войти")) {
            loginBtn.click();
            driver.findElementByXPath("//*[@id=\"identifierId\"]").sendKeys(login);

            actions.moveToElement(
                    driver.findElementByXPath("//*[@id=\"identifierNext\"]")
            ).click().perform();

            driver.findElementByXPath("//*[@id=\"password\"]/div[1]/div/div[1]/input").sendKeys(password);
//            actions.moveToElement(
            Thread.sleep(1000);
            driver.findElementByXPath("//*[@id=\"passwordNext\"]/content/span").click();
//            ).click().perform();

            // в первый раз требуется пройти проверку по e-mail с введением проверочного кда с почты
            // это тоже возможно реализовать автотестом, но я уже прошел проверку и больше оно не высвечивается
        }


        System.out.println("3.  Нажмите на кнопку «Добавить видео»");
        driver.findElementByXPath("//*[@aria-label=\"Создать видео или запись\"]").click();
        driver.findElementByXPath("//*[text() = 'Добавить видео']").click();
        actions.moveToElement(
                driver.findElementByXPath("//*[@id=\"start-upload-button-single\"]/button")
        ).click().perform();


        System.out.println("4.  В открывшейся форме добавьте  медиа файл для загрузки");
        // В этой секции кода я вставляю текст в буфер обмена,
        // далее робот тыкает на клавиши и вставляет текст в окно отправки файла
        // и нажимает enter
        StringSelection s = new StringSelection("C:\\Program Files\\Common Files\\microsoft shared\\ink\\en-US\\boxed-join.avi");
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(s, null);
        try {
            Thread.sleep(1000);
            Robot robot = new Robot();
            robot.keyPress(java.awt.event.KeyEvent.VK_ENTER);
            robot.keyRelease(java.awt.event.KeyEvent.VK_ENTER);
            robot.keyPress(java.awt.event.KeyEvent.VK_CONTROL);
            robot.keyPress(java.awt.event.KeyEvent.VK_V);
            robot.keyRelease(java.awt.event.KeyEvent.VK_CONTROL);
            Thread.sleep(3000);
            robot.keyPress(java.awt.event.KeyEvent.VK_ENTER);
        } catch (AWTException e) {
            e.printStackTrace();
        }
        // на этом моменте мы попадаем на страницу опубликования видео (название, описание и т.д.)
        WebElement videoNameInputWebElement = driver.findElementByXPath("//*[@id=\"upload-item-0\"]/div[3]/div[2]/div/div/div[1]/div[3]/form/div[1]/fieldset[1]/div/label[1]/span/input");
        videoNameInputWebElement.clear(); // очищаем поле перед тем как туда вбить что-либо
        videoNameInputWebElement.sendKeys(String.valueOf(s.hashCode())); // вбиваем какую нибудь последовательность знаков
        Thread.sleep(10000);
        driver.findElementByXPath("//*[@id=\"upload-item-0\"]/div[3]/div[1]/div[1]/div/div/button").click();


        System.out.println("5.  Дождитесь пока медиа файл загрузится и обработается на сервисе");
        Thread.sleep(5000);

        System.out.println("6.  Добавьте необходимое описание и tags (в дальнейшем по ним необходимо будет выполнить поиск)");
        // вообще теги можно добавить на четвертом шаге, но буду строго придерживаться задания
        driver.findElementByXPath("//*[@id=\"upload-item-0\"]/div[3]/div[1]/button").click();
        WebElement descriptionInputWebElement = driver.findElementByXPath("//*[@id=\"upload-item-0\"]/div[3]/div[2]/div/div/div[1]/div[3]/form/div[1]/fieldset[1]/div/label[2]/span/textarea");
        descriptionInputWebElement.sendKeys("Automation test");
        WebElement tagsInputWebElement = driver.findElementByXPath("//*[@id=\"upload-item-0\"]/div[3]/div[2]/div/div/div[1]/div[3]/form/div[1]/fieldset[1]/div/div/span/div/span/input");
        tagsInputWebElement.sendKeys("test, automation, selenium");

        System.out.println("7.  Опубликуйте медиа");
        driver.findElementByXPath("//*[@id=\"upload-item-0\"]/div[3]/div[1]/div[1]/div/div/button").click();


        System.out.println("8.  Перейдите на главную страницу сервиса");
        // Надо пододждать чтобы изменения применились
        Thread.sleep(10000);
        driver.findElementByXPath("//*[@id=\"logo-container\"]").click();


        System.out.println("9.  Выполните поиск по параметрам загруженного ранее видео");
        // надо подождать прежде чем видео появится в результах поиска
        Thread.sleep(20000);
        WebElement searchInputWebElement = driver.findElementByXPath("//*[@id=\"search\"]");
        searchInputWebElement.sendKeys(String.valueOf(s.hashCode()));
        driver.findElementByXPath("//*[@id=\"search-icon-legacy\"]").click();

        System.out.println("10. Проверьте, что в результатах поиска есть видео, загруженное вами.");
        // получаем список, содержащий контейнеры с thumbnails, заголовками, именем загрузившего
        Thread.sleep(1000);
        List<WebElement> videoItemsList = driver.findElementsByXPath("//*[@id=\"contents\"]/ytd-video-renderer");
        for (WebElement element : videoItemsList){
            WebElement webElement = element.findElement(By.xpath("//*[@id=\"video-title\"]"));
            webElement.getText();
        }
    }


    @AfterClass
    public static void afterClass() {
//        driver.close();
    }
}
