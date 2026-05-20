package e0bmanager.utils;

import e0bmanager.client.UserClient;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Quản lý và kiểm tra tất cả loại thông báo trong hệ thống.
 * Mỗi thông báo có: loại, nội dung, mức độ ưu tiên và thời gian tạo.
 */
public class NotificationManager {

    public enum Priority { HIGH, MEDIUM, LOW }
    public enum Type     { SCHEDULE, REMINDER, REVENUE, SYSTEM }

    public static class Notification {
        public final Type     type;
        public final Priority priority;
        public final String   title;
        public final String   message;
        public final String   icon;
        public final LocalDate date;
        public boolean        isRead = false;

        public Notification(Type type, Priority priority,
                            String title, String message, String icon) {
            this.type     = type;
            this.priority = priority;
            this.title    = title;
            this.message  = message;
            this.icon     = icon;
            this.date     = LocalDate.now();
        }
    }

    private final UserClient userClient;
    private final List<Notification> notifications = new ArrayList<>();

    public NotificationManager(UserClient userClient) {
        this.userClient = userClient;
    }

    /**
     * Làm mới tất cả thông báo — gọi định kỳ từ MainForm.
     */
    public void refresh() {
        notifications.clear();
        checkPendingSchedules();
        checkWeekendReminder();
        checkRevenueReminder();
    }

    public List<Notification> getAll()   { return new ArrayList<>(notifications); }
    public int  getUnreadCount()         { return (int) notifications.stream().filter(n -> !n.isRead).count(); }
    public void markAllRead()            { notifications.forEach(n -> n.isRead = true); }

    // ── 1. Kiểm tra lịch chờ duyệt ──────────────────────────────────────
    private void checkPendingSchedules() {
        try {
            List<?> pending = userClient.getPendingSchedules();
            int count = (pending != null) ? pending.size() : 0;
            if (count > 0) {
                notifications.add(new Notification(
                        Type.SCHEDULE, Priority.HIGH,
                        "Lịch chờ duyệt",
                        count + " nhân viên đã đăng ký lịch làm việc và đang chờ bạn duyệt.",
                        "📋"
                ));
            }
        } catch (Exception ignored) {}
    }

    // ── 2. Nhắc phân ca cuối tuần ────────────────────────────────────────
    private void checkWeekendReminder() {
        LocalDate today = LocalDate.now();
        DayOfWeek dow = today.getDayOfWeek();

        // Nhắc vào Thứ 5 hoặc Thứ 6 để chuẩn bị lịch cuối tuần
        if (dow == DayOfWeek.THURSDAY || dow == DayOfWeek.FRIDAY) {
            LocalDate saturday = today.with(DayOfWeek.SATURDAY);
            LocalDate sunday   = today.with(DayOfWeek.SUNDAY);
            notifications.add(new Notification(
                    Type.REMINDER, Priority.MEDIUM,
                    "Nhắc nhở: Lịch cuối tuần",
                    "Sắp đến cuối tuần (" + saturday + " & " + sunday +
                            "). Hãy kiểm tra và phân ca cho nhân viên.",
                    "📅"
            ));
        }

        // Nhắc vào Thứ 7 và Chủ nhật nếu chưa phân ca
        if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
            notifications.add(new Notification(
                    Type.REMINDER, Priority.HIGH,
                    "Hôm nay là cuối tuần!",
                    "Đừng quên kiểm tra lịch làm việc hôm nay và ngày mai.",
                    "⚠️"
            ));
        }
    }

    // ── 3. Nhắc báo cáo doanh thu cuối ngày ─────────────────────────────
    private void checkRevenueReminder() {
        LocalTime now = LocalTime.now();
        // Nhắc từ 20:00 trở đi
        if (now.getHour() >= 20) {
            notifications.add(new Notification(
                    Type.REVENUE, Priority.MEDIUM,
                    "Báo cáo doanh thu",
                    "Đã " + now.getHour() + " giờ. Hãy kiểm tra và chốt doanh thu ngày " +
                            LocalDate.now() + " trong mục Tổng quan.",
                    "💰"
            ));
        }
        // Nhắc đầu ngày kiểm tra doanh thu hôm qua
        if (now.getHour() >= 8 && now.getHour() < 10) {
            notifications.add(new Notification(
                    Type.REVENUE, Priority.LOW,
                    "Nhìn lại hôm qua",
                    "Hãy xem lại báo cáo doanh thu ngày " +
                            LocalDate.now().minusDays(1) + " trong mục Tổng quan.",
                    "📊"
            ));
        }
    }
}