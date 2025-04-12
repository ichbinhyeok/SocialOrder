package org.example.socialorder.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Base64;
import java.util.Map;

@Controller
@RequestMapping("/oauth")
public class BandOAuthController {

    @Value("${band.client.id}")
    private String clientId;

    @Value("${band.client.secret}")
    private String clientSecret;

    @Value("${band.redirect.uri}")
    private String redirectUri;

    private RestTemplate restTemplate = new RestTemplate();

    // 1. 사용자를 밴드 로그인 페이지로 리다이렉트
    @GetMapping("/login")
    public RedirectView bandLogin() {
        String authUrl = "https://auth.band.us/oauth2/authorize" +
                "?response_type=code" +
                "&client_id=" + clientId +
                "&redirect_uri=" + redirectUri;

        return new RedirectView(authUrl);
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // 세션에서 토큰 정보 삭제
        session.removeAttribute("accessToken");
        session.removeAttribute("refreshToken");
        session.removeAttribute("userKey");

        // 로그인 페이지로 리다이렉트
        return "redirect:/oauth/login";
    }

    @GetMapping("/callback")
    @ResponseBody
    public String bandCallback(@RequestParam("code") String code, HttpSession session) {
        // 액세스 토큰 요청 URL
        String tokenUrl = "https://auth.band.us/oauth2/token" +
                "?grant_type=authorization_code" +
                "&code=" + code;

        // 인증 헤더 생성 - Basic Auth with client_id:client_secret
        String auth = clientId + ":" + clientSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + encodedAuth);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 토큰 요청 보내기
        ResponseEntity<Map> response = restTemplate.exchange(
                tokenUrl,
                HttpMethod.GET,
                entity,
                Map.class
        );

        // 응답에서 액세스 토큰 및 기타 정보 추출
        Map<String, Object> tokenInfo = response.getBody();
        String accessToken = (String) tokenInfo.get("access_token");
        String refreshToken = (String) tokenInfo.get("refresh_token");
        String userKey = (String) tokenInfo.get("user_key");

        // 세션에 토큰 정보 저장
        session.setAttribute("accessToken", accessToken);
        session.setAttribute("refreshToken", refreshToken);
        session.setAttribute("userKey", userKey);

        return "액세스 토큰이 성공적으로 발급되었습니다: " + accessToken;
    }
}