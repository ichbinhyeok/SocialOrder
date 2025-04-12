package org.example.socialorder.controller;

import org.example.socialorder.service.BandService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/band")
public class BandController {

    private final BandService bandService;

    public BandController(BandService bandService) {
        this.bandService = bandService;
    }

    @GetMapping("/list")
    public String showBandList(HttpSession session, Model model) {
        // 세션에서 액세스 토큰 가져오기
        String accessToken = (String) session.getAttribute("accessToken");

        if (accessToken == null) {
            // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
            return "redirect:/oauth/login";
        }

        try {
            // 밴드 목록 조회
            List<Map<String, Object>> bandList = bandService.getBandList(accessToken);

            // 모델에 밴드 목록 추가
            model.addAttribute("bandList", bandList);

            return "band/list";
        } catch (Exception e) {
            model.addAttribute("error", "밴드 목록을 가져오는 중 오류가 발생했습니다: " + e.getMessage());
            return "error";
        }
    }


    @GetMapping("/posts")
    public String showBandPosts(
            HttpSession session,
            Model model,
            @RequestParam("band_key") String bandKey,
            @RequestParam(value = "after", required = false) String after) {

        // 세션에서 액세스 토큰 가져오기
        String accessToken = (String) session.getAttribute("accessToken");

        if (accessToken == null) {
            // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
            return "redirect:/oauth/login";
        }

        try {
            // 밴드 글 목록 조회
            Map<String, Object> postsData;
            if (after != null && !after.isEmpty()) {
                postsData = bandService.getBandPosts(accessToken, bandKey, "ko_KR", after, null);
            } else {
                postsData = bandService.getBandPosts(accessToken, bandKey);
            }

            // 데이터 로깅 추가
            System.out.println("Posts Data: " + postsData);

            // 포스트 항목의 구조를 상세히 로깅 (첫 번째 항목만)
            if (postsData.get("items") != null && !((List<?>)postsData.get("items")).isEmpty()) {
                Object firstPost = ((List<?>)postsData.get("items")).get(0);
                System.out.println("First Post Structure: " + firstPost);
            }

            // 모델에 데이터 추가
            model.addAttribute("bandKey", bandKey);
            model.addAttribute("posts", postsData.get("items"));
            model.addAttribute("paging", postsData.get("paging"));

            // 업데이트된 포스트 템플릿 사용
            return "band/posts";
        } catch (Exception e) {
            e.printStackTrace(); // 스택 트레이스 출력
            model.addAttribute("error", "밴드 글 목록을 가져오는 중 오류가 발생했습니다: " + e.getMessage());
            return "error";
        }
    }
}