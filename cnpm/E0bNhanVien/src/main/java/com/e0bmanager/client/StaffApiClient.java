package com.e0bmanager.client;

import com.e0bmanager.dto.GiaoViecDTO;
import com.e0bmanager.dto.LichLamViecDTO;
import com.e0bmanager.dto.LuongDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class StaffApiClient {

    private final HttpClient http = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder().create();
    private static final String BASE = "https://chasmal-lindsey-mediately.ngrok-free.dev/api";

    // ─── Lịch làm việc ───────────────────────────────────────────────────────

    /** Lấy tất cả lịch của nhân viên trong tuần hiện tại (7 ngày từ hôm nay) */
    public List<LichLamViecDTO> getLichTheoNgay(String date) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/lich/ngay/" + date))
                .GET().build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() == 200) {
            return gson.fromJson(res.body(), new TypeToken<List<LichLamViecDTO>>(){}.getType());
        }
        throw new Exception("Lỗi tải lịch: " + res.statusCode());
    }

    // ─── Lương ───────────────────────────────────────────────────────────────

    /** Tính lương theo nvId, tháng, năm */
    public LuongDTO tinhLuong(int nvId, int thang, int nam) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/luong/calculate/" + nvId + "/" + thang + "/" + nam))
                .GET().build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() == 200) {
            return gson.fromJson(res.body(), LuongDTO.class);
        }
        throw new Exception("Lỗi tính lương: " + res.statusCode());
    }

    // ─── Công việc ───────────────────────────────────────────────────────────

    /** Lấy công việc theo ngày */
    public List<GiaoViecDTO> getCongViecTheoNgay(String date) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/giaoviec/ngay/" + date))
                .GET().build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() == 200) {
            return gson.fromJson(res.body(), new TypeToken<List<GiaoViecDTO>>(){}.getType());
        }
        throw new Exception("Lỗi tải công việc: " + res.statusCode());
    }

    /** Cập nhật trạng thái công việc */
    public boolean capNhatTrangThaiCongViec(int id, String trangThai) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/giaoviec/status/" + id + "?status=" + trangThai))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return res.statusCode() == 200;
    }

    // ─── Đổi mật khẩu ────────────────────────────────────────────────────────

    /**
     * Đổi mật khẩu: dùng endpoint login để xác minh mật khẩu cũ,
     * sau đó PUT /api/users/update với password mới.
     * AccountDTO có id (Long) nên truyền vào.
     */
    public boolean doiMatKhau(Long accountId, String matKhauMoi) throws Exception {
        String body = gson.toJson(Map.of(
                "id", accountId,
                "password", matKhauMoi
        ));
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users/update"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() == 200) return true;
        throw new Exception("Không thể đổi mật khẩu: " + res.body());
    }

    // ─── Xác minh mật khẩu cũ ────────────────────────────────────────────────

    public boolean xacMinhMatKhau(String username, String password) throws Exception {
        String body = gson.toJson(Map.of("username", username, "password", password));
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/pda/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return res.statusCode() == 200;
    }
}