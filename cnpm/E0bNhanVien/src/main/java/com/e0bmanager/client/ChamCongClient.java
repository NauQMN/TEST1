package com.e0bmanager.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Client giao tiếp với Python Flask server (main.py) tại localhost:5050.
 * Có retry tự động và timeout hợp lý để tránh lỗi kết nối không ổn định.
 */
public class ChamCongClient {

    private static final String BASE_URL      = "http://localhost:5050";
    private static final int    MAX_RETRY      = 3;
    private static final long   RETRY_DELAY_MS = 600;

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(4))
            .build();
    private final Gson gson = new Gson();

    // ── Bắt đầu chấm công (có retry) ─────────────────────────────────────
    public boolean startCheckin() throws Exception {
        Exception lastEx = null;
        for (int i = 0; i < MAX_RETRY; i++) {
            try {
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/start-checkin"))
                        .timeout(Duration.ofSeconds(5))
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();
                HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
                // 200 = ok, 409 = đang chạy rồi (cũng coi là ok)
                return res.statusCode() == 200 || res.statusCode() == 409;
            } catch (ConnectException ex) {
                lastEx = ex;
                Thread.sleep(RETRY_DELAY_MS);
            }
        }
        throw new Exception(
                "Không thể kết nối tới server MediaPipe (localhost:5050).\n" +
                        "Hãy chắc chắn đã chạy: python main.py", lastEx);
    }

    // ── Lấy trạng thái hiện tại (có retry) ───────────────────────────────
    public CheckinStatus getStatus() throws Exception {
        Exception lastEx = null;
        for (int i = 0; i < MAX_RETRY; i++) {
            try {
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/status"))
                        .timeout(Duration.ofSeconds(4))
                        .GET()
                        .build();
                HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
                JsonObject obj = gson.fromJson(res.body(), JsonObject.class);

                CheckinStatus s    = new CheckinStatus();
                s.status           = getStr(obj, "status",        "idle");
                s.faceCount        = getInt(obj, "face_count",    0);
                s.confidence       = getDbl(obj, "confidence",    0.0);
                s.holdProgress     = getDbl(obj, "hold_progress", 0.0);
                s.message          = getStr(obj, "message",       "");
                return s;
            } catch (ConnectException ex) {
                lastEx = ex;
                Thread.sleep(RETRY_DELAY_MS);
            }
        }
        throw new Exception("Mất kết nối tới server MediaPipe.", lastEx);
    }

    // ── Dừng camera (fire-and-forget, không throw) ────────────────────────
    public void stop() {
        new Thread(() -> {
            for (int i = 0; i < 2; i++) {
                try {
                    HttpRequest req = HttpRequest.newBuilder()
                            .uri(URI.create(BASE_URL + "/stop"))
                            .timeout(Duration.ofSeconds(3))
                            .POST(HttpRequest.BodyPublishers.noBody())
                            .build();
                    http.send(req, HttpResponse.BodyHandlers.ofString());
                    return;
                } catch (Exception ignored) {}
            }
        }).start();
    }

    // ── Kiểm tra server có đang chạy không ───────────────────────────────
    public boolean isServerRunning() {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/status"))
                    .timeout(Duration.ofSeconds(2))
                    .GET().build();
            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
            return res.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    // ── Helpers JSON ──────────────────────────────────────────────────────
    private String getStr(JsonObject o, String key, String def) {
        return o.has(key) && !o.get(key).isJsonNull() ? o.get(key).getAsString() : def;
    }
    private int getDbl_int(JsonObject o, String key, int def) {
        return o.has(key) && !o.get(key).isJsonNull() ? o.get(key).getAsInt() : def;
    }
    private int getInt(JsonObject o, String key, int def) {
        try { return o.has(key) ? o.get(key).getAsInt() : def; } catch (Exception e) { return def; }
    }
    private double getDbl(JsonObject o, String key, double def) {
        try { return o.has(key) ? o.get(key).getAsDouble() : def; } catch (Exception e) { return def; }
    }

    // ── DTO trạng thái ────────────────────────────────────────────────────
    public static class CheckinStatus {
        public String status;       // idle | detecting | confirmed | timeout | error
        public int    faceCount;
        public double confidence;
        public double holdProgress; // 0.0 → 1.0
        public String message;

        public boolean isConfirmed() { return "confirmed".equals(status); }
        public boolean isEnded() {
            return "confirmed".equals(status)
                    || "timeout".equals(status)
                    || "error".equals(status);
        }
    }
}