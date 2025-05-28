import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
public class test {
    public static void main(String[] args) {
        System.out.println(adjustDateLine("日付: 2025-05-28", "cộng")); // 日付: 2025-05-29
        System.out.println(adjustDateLine("日付: 2025-05-28", "trừ"));  // 日付: 2025-05-27
    }
    public static String adjustDateLine(String input, String mode) {
        // Giả định input có định dạng: "日付: yyyy-MM-dd"
        String[] parts = input.split(": ");
        if (parts.length != 2) return input; // không đúng định dạng thì trả lại nguyên

        String prefix = parts[0]; // "日付"
        String dateStr = parts[1]; // "2025-05-28"

        // Parse thành LocalDate
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Cộng hoặc trừ
        if ("add".equals(mode)) {
            date = date.plusDays(1);
        } else if ("subtract".equals(mode)) {
            date = date.minusDays(1);
        }

        // Định dạng lại
        String result = prefix + ": " + date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return result;
    }
}