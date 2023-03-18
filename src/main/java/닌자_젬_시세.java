import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

@Slf4j
public class 닌자_젬_시세 {
    private final static long TICK_TIME = 300;

    public static void main(String[] args) {

        crawlSkillGem(true);
    }

    private static void crawlSkillGem(boolean isAll) {
        ChromeOptions options = new ChromeOptions();
        WebDriver driver = new ChromeDriver(options);

        try {
            String url = "https://poe.ninja/challenge/skill-gems";
            driver.get(url);

            if (isAll) {
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(TICK_TIME);

                    try {
                        WebElement button = driver.findElement(By.cssSelector(".css-3pd7rn"));
                        button.sendKeys("\n");
                    } catch (NoSuchElementException e) {
                        break;
                    }
                }
            }

            Thread.sleep(TICK_TIME);

            Document doc = Jsoup.parse(driver.getPageSource());
            Elements elements = doc.select(".cursor-pointer");

            log.debug("검색 건수: {}", elements.size());
            SkillGemPriceRepository repository = new SkillGemPriceRepository();
            for (Element element : elements) {
                SkillGem skillGem = new SkillGem();
                skillGem.setName(getName(element.select(".css-106k4h2").text().trim()));
                skillGem.setLevel(getLevel(element.select("td:eq(1)").text().trim()));
                skillGem.setQuality(getQuality(element.select("td:eq(2)").text().trim()));
                skillGem.setCorrupt(isCorrupt(element.select("td:eq(3)").text().trim()));
                skillGem.setValueType(getValueType(element.select("td:eq(4) img[title='Divine Orb']").text()));
                skillGem.setValue(getValue(element.select("td:eq(4)").text().trim()));
                skillGem.setLast7Day(getLast7Day(element.select("td:eq(5) span span").text().trim()));
                skillGem.setListed(getListed(element.select("td:eq(6)").text().trim()));
                repository.append(skillGem);
            }

            System.out.println(repository);
            for (SkillGemPriceRepository.GemGroup gemGroup : repository.getSkillGemGroupList()) {
                System.out.println(gemGroup);
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getName(String ele) {
        if (ObjectUtils.isEmpty(ele)) {
            return "";
        }

        return ele.trim();
    }

    private static Integer getLevel(String ele) {
        if (ObjectUtils.isEmpty(ele)) {
            return -1;
        }

        return Integer.valueOf(ele.trim());
    }

    private static String getQuality(String ele) {
        if (ObjectUtils.isEmpty(ele)) {
            return "";
        }

        return ele.trim();
    }

    private static boolean isCorrupt(String ele) {
        return "yes".equalsIgnoreCase(ele);
    }

    private static String getValueType(String ele) {
        return !ObjectUtils.isEmpty(ele) ? "Divine" : "Chaos";
    }

    private static Double getValue(String ele) {
        if (ObjectUtils.isEmpty(ele)) {
            return -1d;
        }

        return Double.parseDouble(ele.trim());
    }

    private static Double getLast7Day(String ele) {
        if (ObjectUtils.isEmpty(ele)) {
            return -1d;
        }

        return Double.parseDouble(ele.replace("%", "").replace("+", "").trim());
    }

    private static String getListed(String ele) {
        ele = ele.replace("~", "");
        if (ele.contains("k")) {
            ele = ele.replace("k", "");
            ele = String.valueOf(Integer.parseInt(ele) * 1000);
        }
        return ele;
    }
}
