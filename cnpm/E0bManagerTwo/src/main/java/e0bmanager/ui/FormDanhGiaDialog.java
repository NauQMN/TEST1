package e0bmanager.ui;

import e0bmanager.client.UserClient;
import e0bmanager.dto.DanhGiaDTO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class FormDanhGiaDialog extends JDialog {

    private int nhanVienId, thang, nam;
    private String hoTen, chucVu;
    private Map<String, Integer> trongSo = new LinkedHashMap<>();
    private Map<String, JSpinner> diemMap = new LinkedHashMap<>();
    private JLabel lblHeSo;
    private JPanel pnlCenter;
    private UserClient userClient = new UserClient();

    public FormDanhGiaDialog(JFrame parent, int nvId, String ten, String cv, int th, int nm) {
        super(parent, "Đánh giá năng lực nhân viên", true);
        this.nhanVienId = nvId;
        this.hoTen = ten;
        this.chucVu = cv;
        this.thang = th;
        this.nam = nm;

        initComponents();
        loadTieuChi();

        setSize(550, 500);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // --- TOP (Thông tin nhân viên) ---
        JPanel pnlTop = new JPanel(new GridLayout(3, 2, 10, 5));
        pnlTop.setBorder(new EmptyBorder(15, 15, 10, 15));
        pnlTop.setBackground(new Color(245, 246, 250));

        pnlTop.add(new JLabel("Nhân viên:"));
        JLabel lblName = new JLabel(hoTen);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pnlTop.add(lblName);

        pnlTop.add(new JLabel("Chức vụ:"));
        pnlTop.add(new JLabel(chucVu));

        pnlTop.add(new JLabel("Kỳ đánh giá:"));
        pnlTop.add(new JLabel("Tháng " + thang + " năm " + nam));
        add(pnlTop, BorderLayout.NORTH);

        // --- CENTER (Tiêu chí chấm điểm) ---
        pnlCenter = new JPanel(new GridLayout(0, 3, 10, 10));
        pnlCenter.setBorder(BorderFactory.createTitledBorder("Chi tiết tiêu chí (Thang điểm 10)"));
        pnlCenter.setBackground(Color.WHITE);
        add(new JScrollPane(pnlCenter), BorderLayout.CENTER);

        // --- BOTTOM (Kết quả & Nút lưu) ---
        JPanel pnlBottom = new JPanel(new BorderLayout(10, 10));
        pnlBottom.setBorder(new EmptyBorder(10, 15, 15, 15));

        JLabel note = new JLabel("<html><i>Quy đổi: &ge;9: +10% | &ge;8: +5% | &ge;7: 0% | &ge;6: -5% | &lt;6: -10%</i></html>");
        note.setFont(new Font("Segoe UI", Font.ITALIC, 11));

        lblHeSo = new JLabel("Hệ số lương dự kiến: 0%");
        lblHeSo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHeSo.setForeground(new Color(41, 128, 185));

        JButton btnLuu = new JButton("Xác nhận & Lưu");
        btnLuu.setPreferredSize(new Dimension(150, 35));
        btnLuu.setBackground(new Color(46, 204, 113));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.addActionListener(e -> luuDanhGia());

        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlActions.add(lblHeSo);
        pnlActions.add(Box.createHorizontalStrut(20));
        pnlActions.add(btnLuu);

        pnlBottom.add(note, BorderLayout.NORTH);
        pnlBottom.add(pnlActions, BorderLayout.SOUTH);
        add(pnlBottom, BorderLayout.SOUTH);
    }

    private void loadTieuChi() {
        pnlCenter.removeAll();
        trongSo.clear();
        diemMap.clear();

        // Phân bổ tiêu chí dựa trên chức vụ (Giữ nguyên logic cũ)
        if (chucVu.equals("Quản lý")) {
            addTC("Điều hành nhân sự", 30);
            addTC("Quản lý doanh thu", 20);
            addTC("Xử lý khiếu nại", 20);
            addTC("Trách nhiệm", 15);
            addTC("Sáng kiến", 15);
        } else if (chucVu.equals("Pha chế")) {
            addTC("Chất lượng đồ uống", 30);
            addTC("Vệ sinh an toàn", 25);
            addTC("Tốc độ ra món", 20);
            addTC("Bảo quản NL", 15);
            addTC("Thái độ phối hợp", 10);
        } else {
            addTC("Thái độ phục vụ", 30);
            addTC("Đúng quy trình", 25);
            addTC("Vệ sinh/Chính xác", 20);
            addTC("Chuyên cần", 15);
            addTC("Hỗ trợ đồng đội", 10);
        }

        for (String tc : trongSo.keySet()) {
            pnlCenter.add(new JLabel(tc));
            pnlCenter.add(new JLabel(trongSo.get(tc) + "%"));

            JSpinner sp = new JSpinner(new SpinnerNumberModel(7, 0, 10, 1));
            sp.addChangeListener(e -> capNhatHeSo());
            diemMap.put(tc, sp);
            pnlCenter.add(sp);
        }
        capNhatHeSo();
    }

    private void addTC(String ten, int ts) { trongSo.put(ten, ts); }

    // Chuyển đổi điểm số sang chuỗi hiển thị và lưu trữ (+10%, -5%...)
    private String getHeSoString(double tong) {
        if (tong >= 9) return "+10%";
        if (tong >= 8) return "+5%";
        if (tong >= 7) return "0%";
        if (tong >= 6) return "-5%";
        return "-10%";
    }

    private void capNhatHeSo() {
        double tong = 0;
        for (String tc : diemMap.keySet()) {
            int diem = (int) diemMap.get(tc).getValue();
            tong += diem * trongSo.get(tc) / 100.0;
        }
        lblHeSo.setText("Hệ số lương: " + getHeSoString(tong));
    }

    private void luuDanhGia() {
        double tong = 0;
        for (String tc : diemMap.keySet()) {
            int diem = (int) diemMap.get(tc).getValue();
            tong += diem * trongSo.get(tc) / 100.0;
        }

        // Tạo DTO để gửi lên Server
        DanhGiaDTO dto = new DanhGiaDTO();
        dto.setNhanVienId(nhanVienId);
        dto.setThang(thang);
        dto.setNam(nam);
        dto.setDiemTong(tong);
        dto.setHeSoLuong(getHeSoString(tong));

        // Gọi API qua UserClient
        if (userClient.updateDanhGia(dto)) {
            JOptionPane.showMessageDialog(this, "Đã cập nhật đánh giá thành công!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu đánh giá lên máy chủ!");
        }
    }
}