package e0bmanager.client;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import e0bmanager.dto.*;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserClient {
    private static final String BASE_URL = "http://localhost:8080/api";
    private final HttpClient client;
    private final Gson gson;

    public UserClient() {
        // Cấu hình GSON để hiểu LocalDate từ Server (chuỗi yyyy-MM-dd)
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, type, context) ->
                        LocalDate.parse(json.getAsString()))
                .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, type, context) ->
                        new JsonPrimitive(src.toString()))
                .setDateFormat("yyyy-MM-dd")
                .create();

        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    // --- QUẢN LÝ NHÂN VIÊN ---

    public List<NhanVienDTO> getAllEmployees() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/nhanvien/all"))
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Type listType = new TypeToken<List<NhanVienDTO>>(){}.getType();
                return gson.fromJson(response.body(), listType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public boolean addEmployee(NhanVienDTO nv) {
        return sendPostAndCheckSuccess(BASE_URL + "/nhanvien/add", nv);
    }

    public boolean updateEmployee(int id, NhanVienDTO nv) {
        try {
            String jsonBody = gson.toJson(nv);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/nhanvien/update/" + id))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString()).statusCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteEmployee(int id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/nhanvien/delete/" + id))
                    .DELETE().build();
            return client.send(request, HttpResponse.BodyHandlers.ofString()).statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    // --- QUẢN LÝ LỊCH LÀM VIỆC ---

    public List<LichLamViecDTO> getScheduleByDate(String dateStr) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/lich/ngay/" + dateStr))
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Type listType = new TypeToken<List<LichLamViecDTO>>(){}.getType();
                return gson.fromJson(response.body(), listType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public String addSchedule(LichLamViecDTO dto) {
        try {
            String jsonBody = gson.toJson(dto);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/lich/add"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) return "SUCCESS";
            if (response.statusCode() == 409) return "DUPLICATE";
            return "ERROR";
        } catch (Exception e) {
            return "ERROR";
        }
    }

    public boolean deleteSchedule(int id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/lich/" + id))
                    .DELETE().build();
            return client.send(request, HttpResponse.BodyHandlers.ofString()).statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    // --- HỆ THỐNG & THỐNG KÊ ---

    public DashboardDTO getStats() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/stats/summary"))
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // SỬA LỖI: Dùng this.gson thay vì tạo Gson mới
                return gson.fromJson(response.body(), DashboardDTO.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public UserDTO login(String username, String password) {
        try {
            String jsonBody = gson.toJson(new LoginRequest(username, password));
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/users/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), UserDTO.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Hàm hỗ trợ gửi POST nhanh
    private boolean sendPostAndCheckSuccess(String url, Object bodyObj) {
        try {
            String jsonBody = gson.toJson(bodyObj);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString()).statusCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public List<GiaoViecDTO> getTasksByDate(String dateStr) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/giaoviec/ngay/" + dateStr))
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                Type listType = new TypeToken<List<GiaoViecDTO>>(){}.getType();
                return gson.fromJson(response.body(), listType);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return Collections.emptyList();
    }

    // Cập nhật trạng thái công việc
    public boolean updateTaskStatus(int id, String status) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/giaoviec/status/" + id + "?status=" + status))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString()).statusCode() == 200;
        } catch (Exception e) { return false; }
    }

    public boolean deleteTask(int id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/giaoviec/" + id)) // Khớp với @DeleteMapping ở Server
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Trả về true nếu xóa thành công (Status 200 OK)
            return response.statusCode() == 200;
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa công việc: " + e.getMessage());
            return false;
        }
    }
    public LuongDTO calculateLuong(int nvId, int thang, int nam) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/luong/calculate/" + nvId + "/" + thang + "/" + nam))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), LuongDTO.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean chotLuong(LuongDTO dto) {
        try {
            String jsonPayload = gson.toJson(dto);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/luong/chot-luong"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public List<LuongDTO> getLuongHistory(int thang, int nam) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/luong/history/" + thang + "/" + nam))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), new TypeToken<List<LuongDTO>>(){}.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
    public List<DanhGiaDTO> getDanhGiaList(int thang, int nam) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/danhgia/" + thang + "/" + nam))
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), new TypeToken<List<DanhGiaDTO>>(){}.getType());
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public boolean updateDanhGia(DanhGiaDTO dto) {
        try {
            String json = gson.toJson(dto);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/danhgia/cap-nhat"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json)).build();
            return client.send(request, HttpResponse.BodyHandlers.ofString()).statusCode() == 200;
        } catch (Exception e) { return false; }
    }
    public UserDTO getUserProfile(int id) {
        try {
            // Gửi yêu cầu đến: http://localhost:8080/api/users/1
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/users/" + id))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("JSON từ Server: " + response.body());
                return gson.fromJson(response.body(), UserDTO.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateUserProfile(UserDTO dto) {
        try {
            String json = gson.toJson(dto);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/users/update"))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json)).build();
            return client.send(request, HttpResponse.BodyHandlers.ofString()).statusCode() == 200;
        } catch (Exception e) { return false; }
    }
    public boolean register(UserDTO dto) {
        try {
            String json = gson.toJson(dto);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/users/register"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            return client.send(request, HttpResponse.BodyHandlers.ofString()).statusCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public DashboardStatsDTO getStatsByDate(String dateStr) {
        try {
            // Gọi đến API xử lý doanh thu theo ngày đã chọn
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/dashboard/stats/by-date?date=" + dateStr))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), DashboardStatsDTO.class);
            } else {
                System.err.println("Lỗi Server: " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("Không thể kết nối Server (Doanh thu): " + e.getMessage());
        }
        return null;
    }
    public List<NhanVienDTO> getPendingAccounts() {
        try {
            String url = "http://localhost:8080/api/pda/auth/pending";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                Type listType = new TypeToken<ArrayList<NhanVienDTO>>(){}.getType();
                return gson.fromJson(response.body(), listType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
    public boolean updateAccountStatus(int accountId, int newStatus) {
        try {
            // Đường dẫn API: /api/pda/auth/update-status/{id}/{status}
            String url = "http://localhost:8080/api/pda/auth/update-status/" + accountId + "/" + newStatus;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .method("PUT", HttpRequest.BodyPublishers.noBody()) // Sử dụng PUT để cập nhật
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public List<RevenuechartDTO> getChartData(String dateStr) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/dashboard/chart/7days?date=" + dateStr))
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                Type listType = new TypeToken<List<RevenuechartDTO>>(){}.getType();
                return gson.fromJson(response.body(), listType);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return Collections.emptyList();
    }

    public boolean duyetLich(int id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/lich/duyet/" + id))
                    .PUT(HttpRequest.BodyPublishers.noBody()).build();
            return client.send(request, HttpResponse.BodyHandlers.ofString()).statusCode() == 200;
        } catch (Exception e) { return false; }
    }

    public boolean tuChoiLich(int id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/lich/tu-choi/" + id))
                    .PUT(HttpRequest.BodyPublishers.noBody()).build();
            return client.send(request, HttpResponse.BodyHandlers.ofString()).statusCode() == 200;
        } catch (Exception e) { return false; }
    }

    public List<LichLamViecDTO> getPendingSchedules() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/lich/cho-duyet"))
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                Type listType = new TypeToken<List<LichLamViecDTO>>(){}.getType();
                return gson.fromJson(response.body(), listType);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return Collections.emptyList();
    }

    public int getPendingCount() {
        try {
            String url = "http://localhost:8080/api/pda/auth/pending-count";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return Integer.parseInt(response.body().trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    public boolean rejectRequest(int accountId) {
        try {
            String url = "http://localhost:8080/api/pda/auth/reject/" + accountId;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .DELETE() // Sử dụng phương thức DELETE
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    private static class LoginRequest {
        String username, password;
        LoginRequest(String u, String p) { this.username = u; this.password = p; }
    }
}