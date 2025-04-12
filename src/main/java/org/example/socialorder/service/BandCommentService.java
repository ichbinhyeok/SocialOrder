package org.example.socialorder.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class BandCommentService {

    private final RestTemplate restTemplate;
    private final String BAND_COMMENTS_API_URL = "https://openapi.band.us/v2/band/post/comments";

    public BandCommentService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 특정 글의 댓글 목록을 조회합니다.
     *
     * @param accessToken 액세스 토큰
     * @param bandKey 밴드 식별자
     * @param postKey 글 식별자
     * @param sort 정렬 방식 (null: 생성순, '+created_at': 생성순, '-created_at': 최신순)
     * @param after 다음 페이지 조회를 위한 커서값 (null인 경우 첫 페이지)
     * @return 댓글 목록 및 페이징 정보가 담긴 Map
     */
    public Map<String, Object> getPostComments(
            String accessToken,
            String bandKey,
            String postKey,
            String sort,
            String after) {

        // 헤더 설정 - Bearer 토큰 인증
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // URL 및 파라미터 설정
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BAND_COMMENTS_API_URL)
                .queryParam("band_key", bandKey)
                .queryParam("post_key", postKey);

        // 선택적 파라미터 추가
        if (sort != null && !sort.isEmpty()) {
            builder.queryParam("sort", sort);
        }

        if (after != null && !after.isEmpty()) {
            builder.queryParam("after", after);
        }

        String url = builder.toUriString();
        System.out.println("Comment API URL: " + url);

        try {
            // API 호출
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            // 응답 검증
            Map<String, Object> responseBody = response.getBody();
            System.out.println("Comment API Response: " + responseBody);

            Integer resultCode = (Integer) responseBody.get("result_code");

            if (resultCode != null && resultCode == 1) {
                // 성공적인 응답
                Map<String, Object> resultData = (Map<String, Object>) responseBody.get("result_data");

                // 결과 데이터가 비어있으면 기본 구조 생성
                if (resultData == null) {
                    resultData = new HashMap<>();
                    resultData.put("items", Collections.emptyList());
                    resultData.put("paging", null);
                } else if (resultData.get("items") == null) {
                    resultData.put("items", Collections.emptyList());
                }

                return resultData;
            } else {
                // 오류 응답 처리
                throw new RuntimeException("밴드 API 오류: " + responseBody);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 예외 발생 시 기본 응답 리턴
            Map<String, Object> emptyResult = new HashMap<>();
            emptyResult.put("items", Collections.emptyList());
            emptyResult.put("paging", null);
            return emptyResult;
        }
    }

    /**
     * 특정 글의 댓글 목록을 첫 페이지부터 조회합니다.
     */
    public Map<String, Object> getPostComments(String accessToken, String bandKey, String postKey) {
        return getPostComments(accessToken, bandKey, postKey, null, null);
    }

    /**
     * 특정 글의 댓글 목록을 최신순으로 조회합니다.
     */
    public Map<String, Object> getPostCommentsLatest(String accessToken, String bandKey, String postKey) {
        return getPostComments(accessToken, bandKey, postKey, "-created_at", null);
    }
}