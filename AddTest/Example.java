import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Data {
        public String date;         // "20250527"
        public String time;         // "060750"
        public String hour;         // "060750"
        public String minute;       // "060750"
        //String trainRaw;     // "00811"
        public String controllerRaw; // "001"
        //Map<String, Data> map; // "00811"
        public String getHour() {
            return hour;
        }
        public void setHour(String hour) {
            this.hour = hour;
        }
        public String getMinute() {
            return minute;
        }
        public void setMinute(String minute) {
            this.minute = minute;
        }
        public String getDate() {
            return date;
        }
        public void setDate(String date) {
            this.date = date;
        }
        public String getTime() {
            return time;
        }
        
        public void setTime(String time) {
            this.time = time;
        }
    public String print() {
        StringBuilder ret = new StringBuilder();
        for (var field : this.getClass().getFields()) {
            try {
                var val = field.get(this);
                var valStr = val != null ? val.toString() : "null";
                ret.append(field.getName()).append(": ").append(valStr).append(",");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret.toString();
    }

        // public String getTrainRaw() {
        //     return trainRaw;
        // }
        // public void setTrainRaw(String trainRaw) {
        //     this.trainRaw = trainRaw;
        // }
        public String getControllerRaw() {
            return controllerRaw;
        }
        public void setControllerRaw(String controllerRaw) {
            this.controllerRaw = controllerRaw;
        }

        public static void analyzeData(Map<String, Set<Data>> m, String filename) {
            Data data = new Data();
            // Kiểm tra định dạng tên file
            // if (filename == null || !filename.matches("\\d{8}_\\p{L}+_\\d{5}_\\d{4}")) {
            //     System.out.println("Tên file không đúng định dạng");
            //     return;
            // }
            String[] parts = filename.split("_");
            if (parts.length != 5) {
                System.out.println("Tên file không đúng định dạng");
                return;
            }

            String date = parts[1];         // "20250527"
            String time = parts[2];         // "060750"
            String trainRaw = parts[3];     // "00811"
            String controllerRaw = parts[4]; // "001"

            int hour = Integer.parseInt(time.substring(0, 2));   // 06
            data.setDate(date);
            data.setTime(time);
            data.setHour(String.valueOf(hour)); // "06"
            int minute = Integer.parseInt(time.substring(2, 4)); // 07
            data.setMinute(String.valueOf(minute)); // "07"

            String trainName = String.valueOf(Integer.parseInt(trainRaw));         // 811
            String controllerId = String.valueOf(Integer.parseInt(controllerRaw)); // 1
            data.setControllerRaw(controllerId);

            // ✅ In ra
            System.out.println("Ngày: " + date);
            System.out.println("Giờ: " + hour + " giờ");
            System.out.println("Phút: " + minute + " phút");
            System.out.println("Tên tàu: " + trainName);
            System.out.println("Controller ID: " + controllerId);
            m.computeIfAbsent(trainName, k -> new HashSet<>()).add(data);
            m.get(trainName).add(data);

        }
}

public class Example {
    public static void analyzeData(Map<String, Set<Data>> m, String filename) {
        Data.analyzeData(m, filename);
    }
    public static void main(String[] args) {
        Path sourceRoot = Paths.get("\\\\lecip_dc2\\LCP\\H-▼LCP品質保証本部\\H 【LCP品証 各部データ受渡専用】\\202505\\広島電鉄\\20250422_広電電車_582_1156（対策ソフトの先行展開結果確認）");   // thư mục chứa các thư mục như 1156, 3704,...
        //Path sourceRoot = Paths.get("path");
        Path targetRoot = Paths.get("input");
        Set<String> skipNames = Set.of("確認済み", "済み");
        Map<String, Set<Data>> m = new HashMap<>();
        try {
            List<Path> folderList = Files.list(sourceRoot)
                                         .filter(Files::isDirectory)
                                         .collect(Collectors.toList());


            for (Path folder : folderList) {
                String folderName = folder.getFileName().toString();             


                try (Stream<Path> stream = Files.walk(folder)) {
                    List<Path> tarFiles = stream
                        .filter(Files::isRegularFile)
                        .filter(p -> !p.toString().endsWith(".xlsx"))
                        .filter(p -> {
                            Path parent = p.getParent();
                            Path grandParent = parent != null ? parent.getParent() : null;

                            return !(skipNames.contains(p.getFileName().toString())
                                  || (parent != null && skipNames.contains(parent.getFileName().toString()))
                                  || (grandParent != null && skipNames.contains(grandParent.getFileName().toString())));
                        })
                        .collect(Collectors.toList());

                    for (Path file : tarFiles) {
                        analyzeData(m, file.getParent().getParent().getFileName().toString());
                        // Lấy đường dẫn tương đối từ thư mục gốc folder
                        Path relativePath = folder.relativize(file);
                        Path targetFolder = targetRoot.resolve(folderName).resolve(relativePath.getParent() != null ? relativePath.getParent() : Paths.get(""));
                        Files.createDirectories(targetFolder);
                        Path target = targetFolder.resolve(file.getFileName());

                        Files.copy(file, target, StandardCopyOption.REPLACE_EXISTING);
                        System.out.println("Copied: " + file + " → " + target);
                    }

                } catch (IOException e) {
                    System.err.println("Exception when move: " + folder + " → " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("Main Error: " + e.getMessage());
        } finally {
            // In ra kết quả
            System.out.println("Kết quả phân tích dữ liệu:");
            for (Map.Entry<String, Set<Data>> entry : m.entrySet()) {
                String trainName = entry.getKey();
                Set<Data> dataSet = entry.getValue();
                System.out.println("Tên tàu: " + trainName);
                for (Data data : dataSet) {
                    System.out.println(data.print());
                }
            }
        }
    }
}
