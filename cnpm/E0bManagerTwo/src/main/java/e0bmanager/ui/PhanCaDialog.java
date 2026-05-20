package e0bmanager.ui;

import com.toedter.calendar.JDateChooser;
import com.formdev.flatlaf.FlatClientProperties;
import e0bmanager.client.UserClient;
import e0bmanager.dto.LichLamViecDTO;
import e0bmanager.dto.NhanVienDTO;
import e0bmanager.panels.WorkSchedulePanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class PhanCaDialog extends JDialog {

    private JComboBox<NhanVienDTO> cbNhanVien;
    private JDateChooser jdNgayLam;

    // Tên ca (tùy chọn hoặc custom)
    private JComboBox<String> cbTenCa;

    // Giờ vào / ra — dạng spinner hoặc text
    private JSpinner spGioVao, spGioRa;
    private JLabel lblSoGio;

    private WorkSchedulePanel parentPanel;
    private UserClient userClient = new UserClient();

    private static final String[] TEN_CA_PRESET = {
            "Ca Sáng", "Ca Chiều", "Ca Tối", "Ca Tùy Chỉnh"
    };
    // Giờ mặc định tương ứng
    private static final String[][] GIO_PRESET = {
            {"08:00", "14:00"},   // Ca Sáng  (6h)
            {"14:00", "20:00"},   // Ca Chiều (6h)
            {"18:00", "23:00"},   // Ca Tối   (5h)
            {"08:00", "12:00"},   // Custom   (mặc định ban đầu)
    };

    public PhanCaDialog(WorkSchedulePanel parent) {
        super((JFrame) SwingUtilities.getWindowAncestor(parent), "Phân ca làm việc", true);
        this.parentPanel = parent;
        initComponents();
        loadEmployees();
        pack();
        setMinimumSize(new Dimension(420, 0));
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        JPanel main = new JPanel(new BorderLayout(0, 18));
        main.setBackground(Color.WHITE);
        main.setBorder(new EmptyBorder(25, 28, 22, 28));

        // ── Tiêu đề ──────────────────────────────────────────────────────
        JLabel lblTitle = new JLabel("Thêm ca làm việc");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(30, 41, 59));
        main.add(lblTitle, BorderLayout.NORTH);

        // ── Form ──────────────────────────────────────────────────────────
        JPanel form = new JPanel(new GridLayout(0, 2, 14, 14));
        form.setOpaque(false);

        // Nhân viên
        form.add(createLabel("Nhân viên:"));
        cbNhanVien = new JComboBox<>();
        cbNhanVien.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        form.add(cbNhanVien);

        // Ngày làm
        form.add(createLabel("Ngày làm việc:"));
        jdNgayLam = new JDateChooser(new java.util.Date());
        jdNgayLam.setDateFormatString("dd/MM/yyyy");
        jdNgayLam.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        form.add(jdNgayLam);

        // Tên ca
        form.add(createLabel("Loại ca:"));
        cbTenCa = new JComboBox<>(TEN_CA_PRESET);
        cbTenCa.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbTenCa.addActionListener(e -> onCaChanged());
        form.add(cbTenCa);

        // Giờ vào
        form.add(createLabel("Giờ vào (HH:mm):"));
        spGioVao = createTimeSpinner("08:00");
        spGioVao.addChangeListener(e -> updateSoGio());
        form.add(spGioVao);

        // Giờ ra
        form.add(createLabel("Giờ ra (HH:mm):"));
        spGioRa = createTimeSpinner("14:00");
        spGioRa.addChangeListener(e -> updateSoGio());
        form.add(spGioRa);

        // Số giờ tự tính
        form.add(createLabel("Số giờ làm:"));
        lblSoGio = new JLabel("6.0 giờ");
        lblSoGio.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblSoGio.setForeground(new Color(16, 185, 129));
        form.add(lblSoGio);

        main.add(form, BorderLayout.CENTER);

        // ── Nút ──────────────────────────────────────────────────────────
        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlBtns.setOpaque(false);

        JButton btnHuy = new JButton("Hủy");
        btnHuy.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnHuy.addActionListener(e -> dispose());

        JButton btnLuu = new JButton("✔  Lưu lịch");
        btnLuu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLuu.putClientProperty(FlatClientProperties.STYLE,
                "background: #10B981; foreground: #FFFFFF;" +
                        "arc: 12; borderWidth: 0; focusWidth: 0;" +
                        "hoverBackground: #059669; margin: 6,18,6,18;");
        btnLuu.addActionListener(e -> saveSchedule());

        pnlBtns.add(btnHuy);
        pnlBtns.add(btnLuu);
        main.add(pnlBtns, BorderLayout.SOUTH);

        add(main);
        onCaChanged(); // Áp giờ mặc định cho ca đầu tiên
    }

    /** Khi đổi loại ca → tự điền giờ mặc định */
    private void onCaChanged() {
        int idx = cbTenCa.getSelectedIndex();
        if (idx < 0 || idx >= GIO_PRESET.length) return;
        boolean isCustom = (idx == GIO_PRESET.length - 1);
        // Với custom, để user tự sửa; với preset thì set sẵn
        setSpinnerValue(spGioVao, GIO_PRESET[idx][0]);
        setSpinnerValue(spGioRa,  GIO_PRESET[idx][1]);
        spGioVao.setEnabled(isCustom || true); // Luôn cho sửa
        spGioRa.setEnabled(isCustom || true);
        updateSoGio();
    }

    /** Tính và hiển thị số giờ */
    private void updateSoGio() {
        try {
            double h = tinhSoGio(getSpinnerValue(spGioVao), getSpinnerValue(spGioRa));
            if (h <= 0) {
                lblSoGio.setText("⚠ Giờ ra phải sau giờ vào");
                lblSoGio.setForeground(new Color(239, 68, 68));
            } else {
                lblSoGio.setText(String.format("%.1f giờ", h));
                lblSoGio.setForeground(new Color(16, 185, 129));
            }
        } catch (Exception ex) {
            lblSoGio.setText("---");
        }
    }

    private void loadEmployees() {
        List<NhanVienDTO> list = userClient.getAllEmployees();
        if (list != null) list.forEach(cbNhanVien::addItem);
    }

    private void saveSchedule() {
        NhanVienDTO nv = (NhanVienDTO) cbNhanVien.getSelectedItem();
        if (nv == null) { JOptionPane.showMessageDialog(this, "Chọn nhân viên!"); return; }
        if (jdNgayLam.getDate() == null) { JOptionPane.showMessageDialog(this, "Chọn ngày làm!"); return; }

        String gioVao = getSpinnerValue(spGioVao);
        String gioRa  = getSpinnerValue(spGioRa);
        double soGio  = tinhSoGio(gioVao, gioRa);
        if (soGio <= 0) {
            JOptionPane.showMessageDialog(this, "Giờ ra phải sau giờ vào!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        java.time.LocalDate localDate = jdNgayLam.getDate().toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDate();

        String tenCa = cbTenCa.getSelectedItem() != null ? cbTenCa.getSelectedItem().toString() : "Ca Tùy Chỉnh";

        LichLamViecDTO dto = new LichLamViecDTO();
        dto.setNhanVienId(nv.getId());
        dto.setNgayLam(localDate);
        dto.setCaLam(tenCa);
        dto.setGioVao(gioVao);
        dto.setGioRa(gioRa);
        dto.setSoGio(soGio);
        dto.setNguonTao("QUAN_LY");

        String result = userClient.addSchedule(dto);
        switch (result) {
            case "SUCCESS":
                JOptionPane.showMessageDialog(this, "Đã phân ca thành công!");
                if (parentPanel != null) parentPanel.loadScheduleFromDB();
                dispose();
                break;
            case "DUPLICATE":
                JOptionPane.showMessageDialog(this, "Nhân viên này đã có lịch trùng ca!", "Trùng lịch", JOptionPane.WARNING_MESSAGE);
                break;
            default:
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu lịch!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    private JSpinner createTimeSpinner(String defaultVal) {
        SpinnerListModel model = new SpinnerListModel(generateTimeList());
        JSpinner spinner = new JSpinner(model);
        spinner.setValue(defaultVal);
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
        editor.getTextField().setHorizontalAlignment(JTextField.CENTER);
        return spinner;
    }

    private java.util.List<String> generateTimeList() {
        java.util.List<String> times = new java.util.ArrayList<>();
        for (int h = 0; h < 24; h++)
            for (int m = 0; m < 60; m += 30)
                times.add(String.format("%02d:%02d", h, m));
        return times;
    }

    private void setSpinnerValue(JSpinner spinner, String value) {
        try { spinner.setValue(value); } catch (Exception ignored) {}
    }

    private String getSpinnerValue(JSpinner spinner) {
        return spinner.getValue().toString();
    }

    private double tinhSoGio(String gioVao, String gioRa) {
        try {
            String[] v = gioVao.split(":");
            String[] r = gioRa.split(":");
            int pv = Integer.parseInt(v[0]) * 60 + Integer.parseInt(v[1]);
            int pr = Integer.parseInt(r[0]) * 60 + Integer.parseInt(r[1]);
            return Math.max(0, (pr - pv) / 60.0);
        } catch (Exception e) { return 0; }
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(71, 85, 105));
        return lbl;
    }
}