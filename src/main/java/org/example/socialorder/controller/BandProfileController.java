package org.example.socialorder.controller;


import org.example.socialorder.service.BandProfileService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/band/profile")
public class BandProfileController {

    private final BandProfileService profileService;

    // 생성자 주입
    public BandProfileController(BandProfileService profileService) {
        this.profileService = profileService;
    }

    /**
     * 사용자 프로필 정보를 화면에 표시합니다.
     */
    @GetMapping("/show")
    public String showProfile(HttpSession session, Model model) {
        // 세션에서 액세스 토큰 가져오기
        String accessToken = (String) session.getAttribute("accessToken");

        if (accessToken == null) {
            // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
            return "redirect:/oauth/login";
        }

        try {
            // 사용자 프로필 정보 가져오기
            Map<String, Object> profileData = profileService.getUserProfile(accessToken);

            // 가공된 프로필 정보 생성
            Map<String, Object> formattedProfile = profileService.formatProfileData(profileData);

            // 모델에 데이터 추가
            model.addAttribute("profile", profileData);
            model.addAttribute("formattedProfile", formattedProfile);

            return "band/profile";
        } catch (Exception e) {
            model.addAttribute("error", "프로필 정보를 가져오는 중 오류가 발생했습니다: " + e.getMessage());
            return "error";
        }
    }

    /**
     * 사용자 프로필 정보를 JSON 형태로 반환합니다 (API 형태로 사용 가능).
     */
    @GetMapping("/api")
    @ResponseBody
    public Map<String, Object> getProfileApi(
            HttpSession session,
            @RequestParam(value = "band_key", required = false) String bandKey) {

        String accessToken = (String) session.getAttribute("accessToken");

        if (accessToken == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "인증되지 않은 요청입니다. 로그인이 필요합니다.");
            return error;
        }

        try {
            return profileService.getUserProfile(accessToken, bandKey);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return error;
        }
    }
}