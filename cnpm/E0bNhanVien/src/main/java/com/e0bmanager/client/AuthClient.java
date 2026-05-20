package com.e0bmanager.client;


import com.e0bmanager.dto.AccountDTO;
import com.e0bmanager.dto.LoginRequest;
import com.e0bmanager.dto.RegisterRequestDTO;
import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class AuthClient {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private final String BASE_URL = "https://chasmal-lindsey-mediately.ngrok-free.dev/api/auth";

        public AccountDTO login(String username, String password) throws Exception {
        Map<String, String> credentials = Map.of(
                "username", username,
                "password", password
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(credentials)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), AccountDTO.class);
        } else if (response.statusCode() == 403) {
            throw new Exception("Tài khoản đã bị khóa!");
        } else {
            throw new Exception("Sai tài khoản hoặc mật khẩu!");
        }
    }

    public boolean register(RegisterRequestDTO requestData) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/register")) // Endpoint gọi sang Spring Boot
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(requestData)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Nếu server trả về 200 OK thì thành công
        if (response.statusCode() == 200) {
            return true;
        } else {
            throw new Exception("Lỗi từ server: " + response.body());
        }
    }
}