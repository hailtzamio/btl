package map.util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomUtil {
    public static String getWord(int len) {
        String word = "";
        for (int i = 0; i < len; i++) {
            int v = 1 + (int) (Math.random() * 26);
            char c = (char) (v + (i == 0 ? 'A' : 'a') - 1);
            word += c;
        }
        return word;
    }

    public static List<String> getWords(int minWordLen, int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> RandomUtil.getWord(minWordLen + (i / 10)))
                .collect(Collectors.toList());
    }

    public static List<String> getAddress() {

        List<String> data = new ArrayList<>();
        data.add("Vo Van Tan");
        data.add("Vo Van Nam");
        data.add("Vo Van Huy");
        data.add("Nguyen Thi Minh Khai");
        data.add("Le Thi Minh Khai");
        data.add("Pham Thi Minh Khai");
        data.add("Chung Cu Phu My Quan 8");
        data.add("Chung Cu Phu My Quan 7");
        data.add("Chung Cu Phu My Quan 6");
        data.add("Dai La");
        data.add("Dai Ly");
        data.add("Tran Dai Nghia");
        data.add("Tran Dai Quang");

        data.add("Trường Đại học Tôn Đức Thắng");
        data.add("Trường Đại học An Giang");
        data.add("Trường Đại học Bách khoa");
        data.add("Trường Đại học Công nghệ Thông tin");
        data.add("Trường Đại học Khoa học Tự nhiên");
        data.add("Trường Đại học Khoa học Xã hội và Nhân văn");
        data.add("Trường Đại học Kinh tế – Luật");
        data.add("Trường Đại học Quốc tế");
        data.add("Phân hiệu Đại học Quốc gia Thành phố Hồ Chí Minh tại Bến Tre");


        return data;
    }


    public static int getInt(int start, int end) {
        return ThreadLocalRandom.current()
                .nextInt(start, end);
    }

    public static LocalDate getDate(int startYear, int endYear) {
        int day = getInt(1, 28);
        int month = getInt(1, 12);
        int year = getInt(startYear, endYear);
        return LocalDate.of(year, month, day);
    }

    public static String getName() {
        int i = getInt(0, NAMES.length);
        return NAMES[i];
    }

    public static String getFullName() {
        int i = getInt(0, NAMES.length);
        int j = getInt(0, NAMES.length);

        return NAMES[i] + " " + NAMES[j];
    }

    public static String getAnyOf(String... strings) {
        if (strings == null || strings.length == 0) {
            return null;
        }
        return strings[getInt(0, strings.length)];
    }

    private static final String[] NAMES = {"Florene", "Mckinnon", "Gonzalo", "Shade", "Britany",
            "Villanueva", "Rae", "Dow", "Maragaret", "Mcneely", "Carmelo", "Soares", "Rosita", "Slone",
            "Stan", "Healy", "Samuel", "Dangelo", "Sharron", "Landers", "Hallie", "Weston", "Hollie",
            "Andres", "Steven", "Tang", "Lulu", "Vue", "Claudie", "Hein", "Man", "Singletary", "Ciara",
            "Conover", "Richie", "Stearns", "Sharan", "Free", "Diego", "Hughey", "Kylie", "Batten", "Lady",
            "Belanger", "Ezra", "Ennis", "Denese", "Combs", "Dorinda", "Martindale"};
}