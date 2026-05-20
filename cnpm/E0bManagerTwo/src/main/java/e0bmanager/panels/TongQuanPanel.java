package e0bmanager.panels;

import com.toedter.calendar.JDateChooser;
import e0bmanager.client.UserClient;
import e0bmanager.dto.DashboardDTO;
import e0bmanager.dto.DashboardStatsDTO;
import com.formdev.flatlaf.FlatClientProperties;

// Các thư viện của XChart
import e0bmanager.dto.RevenuechartDTO;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.Styler;
import e0bmanager.dto.RevenuechartDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

public class TongQuanPanel extends JPanel {
    private JFrame parentFrame;
    private ActionListener navigationListener;
    private UserClient userClient = new UserClient();

    // Các nhãn hiển thị dữ liệu Nhân sự
    private JLabel lblTotalEmp, lblShiftsToday, lblEmpWorking, lblShiftsMonth;

    // Các nhãn hiển thị dữ liệu Doanh thu
    private JLabel lblDayRevenue, lblMonthRevenue, lblOrderCount;

    // Biểu đồ XChart (cần giữ tham chiếu để cập nhật dữ liệu)
    private CategoryChart revenueChart;
    private XChartPanel<CategoryChart> chartPanel;

    private JDateChooser datePicker;

    public TongQuanPanel(JFrame parentFrame, ActionListener navigationListener) {
        this.parentFrame = parentFrame;
        this.navigationListener = navigationListener;
        initComponents();
        loadAllData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250)); // Nền xám nhạt hiện đại

        // --- 1. HEADER & DATE FILTER (Cố định ở trên cùng) ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);
        pnlHeader.setBorder(new EmptyBorder(25, 30, 10, 30));

        JLabel lblTitle = new JLabel("TỔNG QUAN HỆ THỐNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(30, 41, 59));
        pnlHeader.add(lblTitle, BorderLayout.WEST);

        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlFilter.setOpaque(false);

        datePicker = new JDateChooser(new java.util.Date());
        datePicker.setPreferredSize(new Dimension(160, 35));
        datePicker.setDateFormatString("dd-MM-yyyy");
        datePicker.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        datePicker.addPropertyChangeListener("date", evt -> loadAllData());

        JLabel lblDateTitle = new JLabel("Ngày xem báo cáo: ");
        lblDateTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblDateTitle.setForeground(new Color(100, 116, 139));
        pnlFilter.add(lblDateTitle);
        pnlFilter.add(datePicker);
        pnlHeader.add(pnlFilter, BorderLayout.EAST);

        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. MAIN CONTENT (Có thể cuộn lên xuống) ---
        JPanel pnlMainContent = new JPanel();
        pnlMainContent.setLayout(new BoxLayout(pnlMainContent, BoxLayout.Y_AXIS));
        pnlMainContent.setOpaque(false);
        pnlMainContent.setBorder(new EmptyBorder(10, 30, 30, 30));

        // KHU VỰC 1: NHÂN SỰ
        pnlMainContent.add(createSectionTitle("KIỂM SOÁT NHÂN SỰ"));
        pnlMainContent.add(Box.createVerticalStrut(15));

        JPanel pnlStaffCards = new JPanel(new GridLayout(1, 4, 20, 0));
        pnlStaffCards.setOpaque(false);
        pnlStaffCards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120)); // Cố định chiều cao
        lblTotalEmp = new JLabel("...");
        lblShiftsToday = new JLabel("...");
        lblEmpWorking = new JLabel("...");
        lblShiftsMonth = new JLabel("...");

        pnlStaffCards.add(createStatCard("Tổng nhân viên", lblTotalEmp, "🧑‍💼"));
        pnlStaffCards.add(createStatCard("Ca làm hôm nay", lblShiftsToday, "📅"));
        pnlStaffCards.add(createStatCard("Đang làm việc", lblEmpWorking, "🔥"));
        pnlStaffCards.add(createStatCard("Tổng ca tháng", lblShiftsMonth, "📋"));
        pnlMainContent.add(pnlStaffCards);

        pnlMainContent.add(Box.createVerticalStrut(35)); // Khoảng cách giữa 2 khu vực

        // KHU VỰC 2: DOANH THU
        pnlMainContent.add(createSectionTitle("THỐNG KÊ DOANH THU"));
        pnlMainContent.add(Box.createVerticalStrut(15));

        JPanel pnlRevenueCards = new JPanel(new GridLayout(1, 3, 20, 0));
        pnlRevenueCards.setOpaque(false);
        pnlRevenueCards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        lblDayRevenue = new JLabel("...");
        lblMonthRevenue = new JLabel("...");
        lblOrderCount = new JLabel("...");

        pnlRevenueCards.add(createStatCard("Doanh thu ngày", lblDayRevenue, "💵"));
        pnlRevenueCards.add(createStatCard("Doanh thu tháng này", lblMonthRevenue, "💎"));
        pnlRevenueCards.add(createStatCard("Số hóa đơn chốt", lblOrderCount, "🧾"));
        pnlMainContent.add(pnlRevenueCards);

        pnlMainContent.add(Box.createVerticalStrut(35));

        // KHU VỰC 3: BIỂU ĐỒ DOANH THU (XCHART)
        pnlMainContent.add(createSectionTitle("BIỂU ĐỒ DOANH THU 7 NGÀY GẦN NHẤT"));
        pnlMainContent.add(Box.createVerticalStrut(15));

        // Gọi hàm tạo biểu đồ XChart
        JPanel pnlChart = createRevenueChart();
        pnlMainContent.add(pnlChart);

        // Bọc MainContent vào JScrollPane để vuốt lên xuống
        JScrollPane scrollPane = new JScrollPane(pnlMainContent);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(245, 247, 250));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Cuộn chuột mượt mà hơn
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

    }

    // Hàm tạo tiêu đề cho từng khu vực
    private JPanel createSectionTitle(String text) {
        // Dùng FlowLayout căn trái, không có khoảng cách
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT); // Lệnh chốt hạ để ép lề trái trong BoxLayout

        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(new Color(71, 85, 105)); // Màu xám xanh

        panel.add(label);
        return panel;
    }
    /**
     * Tạo thẻ thống kê bo góc (Sử dụng FlatLaf)
     */
    private JPanel createStatCard(String title, JLabel lblVal, String iconStr) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        // Bật tính năng bo góc của FlatLaf
        card.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc: 25;"
                + "border: 1,1,1,1, #E2E8F0,, 25;"); // Viền mỏng bo góc 25
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Phần text (Tiêu đề + Giá trị)
        JPanel pnlText = new JPanel(new GridLayout(2, 1, 0, 5));
        pnlText.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(new Color(100, 116, 139));

        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblVal.setForeground(new Color(30, 41, 59));

        pnlText.add(lblTitle);
        pnlText.add(lblVal);
        card.add(pnlText, BorderLayout.CENTER);

        // Phần Icon trang trí
        JLabel lblIcon = new JLabel(iconStr);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        card.add(lblIcon, BorderLayout.EAST);

        return card;
    }

    /**
     * Tạo biểu đồ XChart với dữ liệu mặc định (placeholder).
     * Dữ liệu thật sẽ được nạp vào trong loadAllData() sau khi gọi API.
     */
    private JPanel createRevenueChart() {
        revenueChart = new CategoryChartBuilder()
                .width(800).height(300)
                .title("Thống kê doanh thu 7 ngày qua")
                .xAxisTitle("Ngày")
                .yAxisTitle("Doanh thu (VNĐ)")
                .build();

        // Tùy chỉnh giao diện
        revenueChart.getStyler().setChartBackgroundColor(Color.WHITE);
        revenueChart.getStyler().setPlotBackgroundColor(Color.WHITE);
        revenueChart.getStyler().setPlotBorderVisible(false);
        revenueChart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        revenueChart.getStyler().setToolTipsEnabled(true);
        revenueChart.getStyler().setSeriesColors(new Color[]{ new Color(59, 130, 246) });
        revenueChart.getStyler().setChartFontColor(new Color(71, 85, 105));
        revenueChart.getStyler().setAxisTickLabelsColor(new Color(100, 116, 139));

        // Dữ liệu placeholder — sẽ được thay bằng dữ liệu thật trong updateChart()
        revenueChart.addSeries("Doanh thu thực tế",
                Arrays.asList("--", "--", "--", "--", "--", "--", "--"),
                Arrays.asList(0, 0, 0, 0, 0, 0, 0));

        chartPanel = new XChartPanel<>(revenueChart);
        chartPanel.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc: 25;"
                + "border: 1,1,1,1, #E2E8F0,, 25;");
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel.setPreferredSize(new Dimension(0, 350));
        chartPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 350));

        return chartPanel;
    }

    /**
     * Cập nhật biểu đồ với dữ liệu thực tế từ Server.
     */
    private void updateChart(List<RevenuechartDTO> data) {
        if (data == null || data.isEmpty()) return;

        List<String> xData = new java.util.ArrayList<>();
        List<Number> yData = new java.util.ArrayList<>();

        for (RevenuechartDTO item : data) {
            xData.add(item.getNgay());
            yData.add(item.getDoanhThu());
        }

        // Cập nhật series dữ liệu trong biểu đồ
        revenueChart.updateCategorySeries("Doanh thu thực tế", xData, yData, null);
        chartPanel.revalidate();
        chartPanel.repaint();
    }
    private void loadAllData() {
        if (datePicker.getDate() == null) return;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(datePicker.getDate());

        lblDayRevenue.setText("Đang tải...");
        lblTotalEmp.setText("Đang tải...");

        new Thread(() -> {
            try {
                DashboardDTO staffData = userClient.getStats();
                DashboardStatsDTO revenueData = userClient.getStatsByDate(dateStr);
                List<RevenuechartDTO> chartData = userClient.getChartData(dateStr);

                SwingUtilities.invokeLater(() -> {
                    // Cập nhật thẻ nhân sự
                    if (staffData != null) {
                        lblTotalEmp.setText(staffData.getTotalEmployees() + " người");
                        lblShiftsToday.setText(staffData.getShiftsToday() + " ca");
                        lblEmpWorking.setText(staffData.getEmployeesWorkingToday() + " người");
                        lblShiftsMonth.setText(staffData.getShiftsInMonth() + " ca");
                    } else {
                        setStaffError();
                    }

                    // Cập nhật thẻ doanh thu
                    if (revenueData != null) {
                        lblDayRevenue.setText(String.format("%,.0f đ", revenueData.getDoanhThuDaRut()));
                        lblMonthRevenue.setText(String.format("%,.0f đ", revenueData.getTongDoanhThu()));
                        lblOrderCount.setText(revenueData.getSoHdDaRut() + " Đơn");
                    } else {
                        setRevenueError();
                    }

                    // Cập nhật biểu đồ với dữ liệu thật từ server
                    if (chartData != null && !chartData.isEmpty()) {
                        updateChart(chartData);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    setStaffError();
                    setRevenueError();
                });
            }
        }).start();
    }

    private void setStaffError() {
        lblTotalEmp.setText("Lỗi");
        lblShiftsToday.setText("---");
        lblEmpWorking.setText("---");
        lblShiftsMonth.setText("---");
    }

    private void setRevenueError() {
        lblDayRevenue.setText("Lỗi");
        lblMonthRevenue.setText("---");
        lblOrderCount.setText("---");
    }
}