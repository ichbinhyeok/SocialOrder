package org.example.socialorder.controller;

import org.example.socialorder.service.BandCommentService;
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
@RequestMapping("/band/comments")
public class CommentController {

    private final BandCommentService commentService;

    public CommentController(BandCommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * 특정 글의 댓글 목록을 화면에 표시합니다.
     */
    @GetMapping("/list")
    public String showComments(
            HttpSession session,
            Model model,
            @RequestParam("band_key") String bandKey,
            @RequestParam("post_key") String postKey,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "after", required = false) String after) {

        // 세션에서 액세스 토큰 가져오기
        String accessToken = (String) session.getAttribute("accessToken");

        if (accessToken == null) {
            // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
            return "redirect:/oauth/login";
        }

        try {
            // 댓글 목록 조회
            Map<String, Object> commentsData = commentService.getPostComments(
                    accessToken, bandKey, postKey, sort, after);

            // 모델에 데이터 추가
            model.addAttribute("bandKey", bandKey);
            model.addAttribute("postKey", postKey);
            model.addAttribute("comments", commentsData.get("items"));
            model.addAttribute("paging", commentsData.get("paging"));
            model.addAttribute("sort", sort);  // sort != null ? sort : "+created_at" 대신
            return "band/comments";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "댓글 목록을 가져오는 중 오류가 발생했습니다: " + e.getMessage());
            return "error";
        }
    }

    /**
     * 특정 글의 댓글 목록을 JSON 형태로 반환합니다 (API 형태로 사용 가능).
     */
    @GetMapping("/api")
    @ResponseBody
    public Map<String, Object> getCommentsApi(
            HttpSession session,
            @RequestParam("band_key") String bandKey,
            @RequestParam("post_key") String postKey,
            @RequestParam(value = "sort", required = false) String sort) {

        String accessToken = (String) session.getAttribute("accessToken");

        if (accessToken == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "인증되지 않은 요청입니다. 로그인이 필요합니다.");
            return error;
        }

        try {
            return commentService.getPostComments(accessToken, bandKey, postKey, sort, null);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return error;
        }
    }
}