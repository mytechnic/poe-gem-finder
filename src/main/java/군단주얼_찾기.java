import org.apache.commons.lang3.ObjectUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class 군단주얼_찾기 {
    public static void main(String[] args) {
        String source = "C:\\Users\\STAR\\Downloads\\source.txt";
        String result = "C:\\Users\\STAR\\Downloads\\result.txt";

        List<FindItem> sourceItemList = getFileRead(source).stream().map(군단주얼_찾기::getSourceFindItem).toList();
        System.out.println("소스: " + sourceItemList.size() + "건");
        List<ResultItem> resultItemList = getResultFilterItemList(getFileRead(result));
        System.out.println("대상: " + resultItemList.size() + "건");

        for (ResultItem r : resultItemList) {
            for (FindItem f : sourceItemList) {
                if (r.getOpt().contains("데카라 " + f.getName() + "명")) {
                    System.out.println(r);
                    System.out.println(f);
                }
            }
        }
    }

    private static List<ResultItem> getResultFilterItemList(List<String> result) {
        List<String> result2 = new ArrayList<>();

        boolean isStart = false;
        for (String s : result) {
            if (!isStart && s.contains("검색 결과")) {
                isStart = true;
                continue;
            } else if (isStart && s.contains("©")) {
                break;
            }
            if (!isStart) {
                continue;
            }
            result2.add(s);
        }

        List<ResultItem> resultItemList = new ArrayList<>();
        ResultItem item = new ResultItem();
        for (String str : result2) {
            if (str.contains("발바라")) {
                item = new ResultItem();
                item.setOpt(str);
            } else if (str.contains("정가")) {
                item.setPrice(str);
            } else if (str.contains("제시 가격") && ObjectUtils.isEmpty(item.getPrice())) {
                item.setPrice(str);
            } else if (str.toLowerCase().contains("ign")) {
                item.setIgn(str);
                resultItemList.add(item);
            }
        }

        return resultItemList;
    }

    private static List<String> getFileRead(String path) {
        try {
            List<String> itemList = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String str;
            while ((str = reader.readLine()) != null) {
                str = str.trim();
                if (ObjectUtils.isEmpty(str)) {
                    continue;
                }
                itemList.add(str);
            }
            reader.close();
            return itemList;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private static FindItem getSourceFindItem(String str) {
        String regex = "[0-9]{1,}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        int i = 0;

        FindItem findItem = new FindItem();
        while (matcher.find()) {
            if (i == 0) {
                findItem.setName(matcher.group());
            } else if (i == 1) {
                findItem.setCount(matcher.group());
            }
            i++;
        }
        return findItem;
    }
}