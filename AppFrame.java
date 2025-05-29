package com.example.base;
import com.example.utils.SharedUtils;
import com.example.data.StationError;
import com.example.data.AppFrameData;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import javax.swing.table.DefaultTableModel;
import java.util.Enumeration;
import java.util.*;

public class AppFrame {
    JFrame frame;
    // === VÙNG 1: Thông tin tàu ===
    JPanel trainInfoPanel;
    JComboBox<String> trainNameTypeBox;
    JComboBox<String> timeBox;
    JComboBox<String> trainTypeBox;
    JPanel datePanel;
    JButton prevDay;
    JButton nextDay;
    JLabel dateLabel;
    JButton runButton;
    JTable controller1Table; // dummy
    JTable controller2Table; // dummy
    JTable controller3Table; // dummy
    JTable controller4Table; // dummy

    JScrollPane scrollPane1;
    JScrollPane scrollPane2;

    JSplitPane splitPane;
    JPanel mainPanel;
    DefaultTableModel controller1Model;
    DefaultTableModel controller2Model;
    DefaultTableModel controller3Model;
    DefaultTableModel controller4Model;
    ActionListener listener;
    List<Runnable> runEventList = new ArrayList<>();
    AppFrameData appFrameData;
    private Map<String, String> timeDisplayToRaw = new HashMap<>();
    String[] columnNames = {"駅", "到着時刻", "系統・駒番号", "エラー", "エラー発生回数", "詳細エラー", "原因"};
	String[] newHeaders = {"駅", "到着時刻", "系統・駒番号", "A", "B", "C", "D"};

    // constructor, to initialize the components
    // with default values.
    public AppFrame(AppFrameData appFrameData)
    {
        this.appFrameData = appFrameData;
        // Font Nhật: Yu Gothic UI, 16pt
        Font japaneseFont = new Font("Yu Gothic UI", Font.PLAIN, 16);
        setUIFont(new javax.swing.plaf.FontUIResource(japaneseFont));

        frame = new JFrame("Vehicle Information Tool");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Bắt đầu ở chế độ toàn màn hình
        frame.setMinimumSize(new Dimension(1990, 1500));

        mainPanel = new JPanel(new BorderLayout());
        frame.setContentPane(mainPanel);


        // === VÙNG 1: Thông tin tàu ===
        trainInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        trainInfoPanel.add(new JLabel("Vehicle Number:"));
        String[] trainNames = {"", "5202", "3704", "582"};
        trainNameTypeBox = new JComboBox<>(trainNames);
        trainNameTypeBox.setSelectedIndex(0); // Chọn mặc định là 1156
        appFrameData.setChooseTrainName((String) trainNameTypeBox.getSelectedItem());
        trainNameTypeBox.addActionListener(e -> {
            String selectedTrain = (String) trainNameTypeBox.getSelectedItem();
            System.err.println("Selected Train: " + selectedTrain);
            appFrameData.setChooseTrainName(selectedTrain);
            runTimesRunEvent();
        });
        trainInfoPanel.add(trainNameTypeBox);

        trainInfoPanel.add(new JLabel("Vehice Type:"));
        String[] trainTypes = {"単車", "連接車", "バス"};
        trainTypeBox = new JComboBox<>(trainTypes);
        trainTypeBox.setSelectedIndex(0); // Chọn mặc định là "単車"
        appFrameData.setTrainType((String) trainTypeBox.getSelectedItem());
        trainTypeBox.addActionListener(e -> {
            String selectedType = (String) trainTypeBox.getSelectedItem();
            System.err.println("Selected Train Type: " + selectedType);
            appFrameData.setTrainType(selectedType);
            runTimesRunEvent();
            // appFrameData.setChooseTrainType(selectedType); // Nếu cần lưu loại tàu
        });
        trainInfoPanel.add(trainTypeBox);
        trainInfoPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(173, 216, 230), 2), // Light blue border
            //BorderFactory.createEmptyBorder(10, 10, 50, 10)), // top, left, bottom, right
            "車両情報", // Tiêu đề vùng
            TitledBorder.LEFT, TitledBorder.TOP,
            trainInfoPanel.getFont(),
            new Color(0, 102, 204) // Màu chữ tiêu đề (xanh dương)
        ));


        mainPanel.add(trainInfoPanel, BorderLayout.NORTH);

        // === VÙNG 1.0: Ngày ===
        datePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        prevDay = new JButton("< 前");
        prevDay.addActionListener(e -> {
            System.err.println("PREVIOUS PAGE");
            dateLabel.setText(SharedUtils.adjustDateLine(dateLabel.getText(), "subtract"));
            appFrameData.setDate(dateLabel.getText().replace("日付: ", ""));
            runTimesRunEvent();
            System.err.println("Date updated to: " + dateLabel.getText());
        });
        nextDay = new JButton("後 >");
        nextDay.addActionListener(e -> {
            System.err.println("NEXT PAGE");
            dateLabel.setText(SharedUtils.adjustDateLine(dateLabel.getText(), "add"));
            appFrameData.setDate(dateLabel.getText().replace("日付: ", ""));
            runTimesRunEvent();
        });
        dateLabel = new JLabel(SharedUtils.getDateNow());
        dateLabel.setText(SharedUtils.getDateNow());
        appFrameData.setDate(dateLabel.getText().replace("日付: ", ""));
        datePanel.add(prevDay);
        datePanel.add(dateLabel);
        datePanel.add(nextDay);
        trainInfoPanel.add(datePanel);
        // === VÙNG 1.0: RUN ===
        trainInfoPanel.add(new JLabel("Detail Time:"));
        String[] timeOptions = {"", "0000"};
        timeBox = new JComboBox<>(timeOptions);
        timeBox.setSelectedIndex(0); // Chọn mặc định là "1"
        timeBox.addActionListener(e -> {
            String selectedDisplay = (String) timeBox.getSelectedItem();
            String rawValue = timeDisplayToRaw.getOrDefault(selectedDisplay, "");

            System.out.println("Giá trị thực: " + rawValue);
            appFrameData.setTime(rawValue);
            //runTimesRunEvent();
        });
        trainInfoPanel.add(timeBox);
        // === VÙNG 1.0: RUN ===
        runButton = new JButton("更新データ取得中...しばらくお待ちください。");
        runButton.setFont(new Font("Yu Gothic UI", Font.BOLD, 18));
        runButton.setBackground(Color.LIGHT_GRAY); // Xám nhạt (Light Gray)
        runButton.setForeground(Color.WHITE); // chữ trắng
        runButton.setEnabled(false);
        runButton.setFocusPainted(false);
        runButton.addActionListener((actionEvent) -> {
            if (listener != null) {
                listener.actionPerformed(actionEvent);
            }
        });
        trainInfoPanel.add(runButton);
        // === Vung 2 View Option: Radio Buttons ===
        JPanel viewOptionPanel = new JPanel();
        viewOptionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        viewOptionPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(173, 216, 230), 2),
            "View",
            TitledBorder.LEFT, TitledBorder.TOP,
            viewOptionPanel.getFont(),
            new Color(0, 102, 204)
        ));

        JRadioButton errorRadio = new JRadioButton("Error");
		errorRadio.addActionListener((actionEvent) -> {
			System.out.println("→ View mode: Error");
            updateHeaderTable(columnNames);
        });
        JRadioButton otherRadio = new JRadioButton("Others");
		otherRadio.addActionListener((actionEvent) -> {
			System.out.println("→ View mode: Others");
            updateHeaderTable(newHeaders);
        });
        ButtonGroup viewGroup = new ButtonGroup();
        viewGroup.add(errorRadio);
        viewGroup.add(otherRadio);
        errorRadio.setSelected(true);

        viewOptionPanel.add(errorRadio);
        viewOptionPanel.add(otherRadio);
        // === VÙNG 2.1 ~ 2.4: Thông tin các bảng điều khiển ===

        // Tạo model
        controller1Model = new DefaultTableModel(columnNames, 0);
        controller2Model = new DefaultTableModel(columnNames, 0);
        controller3Model = new DefaultTableModel(columnNames, 0);
        controller4Model = new DefaultTableModel(columnNames, 0);

        // Tạo bảng
        controller1Table = new JTable(controller1Model); controller1Table.setRowHeight(30);
        controller2Table = new JTable(controller2Model); controller2Table.setRowHeight(30);
        controller3Table = new JTable(controller3Model); controller3Table.setRowHeight(30);
        controller4Table = new JTable(controller4Model); controller4Table.setRowHeight(30);

        // Tạo các panel độc lập chứa từng bảng
        JPanel panel1 = wrapTablePanel(controller1Table, "メイン制御器");
        JPanel panel2 = wrapTablePanel(controller2Table, "サブ制御器１");
        JPanel panel3 = wrapTablePanel(controller3Table, "サブ制御器２");
        JPanel panel4 = wrapTablePanel(controller4Table, "サブ制御器３");

        // Panel ngang chứa tất cả panel bảng
        JPanel allControllersPanel = new JPanel();
        allControllersPanel.setLayout(new BoxLayout(allControllersPanel, BoxLayout.X_AXIS));
        allControllersPanel.add(panel1);
        allControllersPanel.add(panel2);
        allControllersPanel.add(panel3);
        allControllersPanel.add(panel4);
        

        // Scroll ngang
        JScrollPane scrollPane = new JScrollPane(allControllersPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        // Thêm viền ngoài
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(viewOptionPanel, BorderLayout.NORTH);
        wrapper.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(173, 216, 230), 2),
            "制御器",
            TitledBorder.LEFT, TitledBorder.TOP,
            wrapper.getFont(),
            new Color(0, 102, 204)
        ));
        wrapper.add(scrollPane, BorderLayout.CENTER);

        // Thêm vào mainPanel
        mainPanel.add(wrapper, BorderLayout.CENTER);


        frame.setVisible(true);

    }

    // Cài font mặc định cho toàn bộ UI
    void setUIFont(javax.swing.plaf.FontUIResource f) {
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, f);
            }
        }
    }
	public void updateHeaderTable(String[] newHeaders)
	{
		for (int i = 0; i < newHeaders.length; i++) {
			 controller1Table.getColumnModel().getColumn(i).setHeaderValue(newHeaders[i]);
			 controller2Table.getColumnModel().getColumn(i).setHeaderValue(newHeaders[i]);
			 controller3Table.getColumnModel().getColumn(i).setHeaderValue(newHeaders[i]);
			 controller4Table.getColumnModel().getColumn(i).setHeaderValue(newHeaders[i]);
		}
		controller1Table.getTableHeader().repaint();
		controller2Table.getTableHeader().repaint();
		controller3Table.getTableHeader().repaint();
		controller4Table.getTableHeader().repaint();
	}

    public void updateUIController1(List<StationError> list)
    {
        controller1Model.setRowCount(0); // Xoá hết dữ liệu cũ
        for (StationError log : list) {
            controller1Model.addRow(log.toTableRow());
        }
    }

    public void updateUIController2(List<StationError> list)
    {
        controller2Model.setRowCount(0); // Xoá hết dữ liệu cũ
        for (StationError log : list) {
            controller2Model.addRow(log.toTableRow());
        }
    }
    public void updateUIController3(List<StationError> list)
    {
        controller3Model.setRowCount(0); // Xoá hết dữ liệu cũ
        for (StationError log : list) {
            controller3Model.addRow(log.toTableRow());
        }
    }

    public void updateUIController4(List<StationError> list)
    {
        controller4Model.setRowCount(0); // Xoá hết dữ liệu cũ
        for (StationError log : list) {
            controller4Model.addRow(log.toTableRow());
        }
    }
    public void updateUIController(int id, List<StationError> logs) {
        switch (id) {
            case 1 -> updateUIController1(logs);
            case 2 -> updateUIController2(logs);
            case 3 -> updateUIController3(logs);
            case 4 -> updateUIController4(logs);
            default -> System.err.println("Unknown controller id: " + id);
        }
    }

    public void setButtonText(String text)
    {
        runButton.setBackground(new Color(0, 153, 255)); // xanh dương đậm
        runButton.setForeground(Color.WHITE); // chữ trắng
        runButton.setEnabled(true);
        runButton.setText(text);
    }
    public void updateTrainName(List<String> updatedNames)
    {
        if (updatedNames == null || updatedNames.isEmpty()) {
            System.err.println("No train names available to update.");
            return;
            
        }
        appFrameData.setChooseTrainName((String) updatedNames.get(0));
        trainNameTypeBox.setModel(new DefaultComboBoxModel<>(updatedNames.toArray(new String[0])));
    }
    public void updateTime(List<String> updatedTimes) {
        if (updatedTimes == null || updatedTimes.isEmpty()) {
            appFrameData.setTime("");
            timeBox.setModel(new DefaultComboBoxModel<>(new String[]{"", "0000"}));
            return;
        }

        // Reset map
        timeDisplayToRaw.clear();

        List<String> displayList = new ArrayList<>();
        for (String raw : updatedTimes) {
            String display = raw;
            if (raw.length() == 6) {
                display = raw.substring(0, 2) + ":" + raw.substring(2, 4) + ":" + raw.substring(4, 6);
            }
            timeDisplayToRaw.put(display, raw); // lưu ánh xạ display → raw
            displayList.add(display);
        }

        // Set model với hiển thị đẹp
        timeBox.setModel(new DefaultComboBoxModel<>(displayList.toArray(new String[0])));

        // Gán mặc định vào appFrameData
        String firstDisplay = displayList.get(0);
        appFrameData.setTime(timeDisplayToRaw.get(firstDisplay));
    }

    public void addTimesRunEvent(Runnable runEvent)
    {
        if (runEvent != null) {
            runEventList.add(runEvent);
        } else {
            System.err.println("Run event is null, not added.");
        }
    }
    public void runTimesRunEvent()
    {
        if (runEventList.isEmpty()) {
            System.err.println("No run events to execute.");
            return;
        }
        for (Runnable runEvent : runEventList) {
            runEvent.run();
        }
    }

    public void setRunEvent(ActionListener l)
    {
        this.listener = l;
    }
    JPanel wrapTablePanel(JTable table, String title) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(900, 500));
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(173, 216, 230), 2),
            title,
            TitledBorder.LEFT, TitledBorder.TOP,
            scrollPane.getFont(),
            new Color(0, 102, 204)
        ));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(1000, 550));
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

}
