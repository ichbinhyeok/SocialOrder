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
import java.util.List;
import java.util.Map;

@Service
public class BandService {

    private final RestTemplate restTemplate;
    private final String BAND_LIST_API_URL = "https://openapi.band.us/v2.1/bands";
    private final String BAND_POSTS_API_URL = "https://openapi.band.us/v2/band/posts";


    public BandService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 로그인 사용자가 가입한 밴드 목록을 조회합니다.
     *
     * @param accessToken 액세스 토큰
     * @return 밴드 목록이 담긴 Map
     */
    public List<Map<String, Object>> getBandList(String accessToken) {
        // 헤더 설정 - Bearer 토큰 인증
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // API 호출
            ResponseEntity<Map> response = restTemplate.exchange(
                    BAND_LIST_API_URL,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            // 응답 검증
            Map<String, Object> responseBody = response.getBody();
            Integer resultCode = (Integer) responseBody.get("result_code");

            if (resultCode != null && resultCode == 1) {
                // 성공적인 응답
                Map<String, Object> resultData = (Map<String, Object>) responseBody.get("result_data");
                return (List<Map<String, Object>>) resultData.get("bands");
            } else {
                // 오류 응답 처리
                throw new RuntimeException("밴드 API 오류: " + responseBody);
            }
        } catch (Exception e) {
            // 예외 처리
            throw new RuntimeException("밴드 목록을 가져오는 중 오류 발생: " + e.getMessage(), e);
        }
    }


    public Map<String, Object> getBandPosts(String accessToken, String bandKey,
                                            String locale, String after, Integer limit) {
        // 헤더 설정 - Bearer 토큰 인증
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // URL 및 파라미터 설정
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BAND_POSTS_API_URL)
                .queryParam("band_key", bandKey)
                .queryParam("locale", locale != null ? locale : "ko_KR");

        // 페이징 파라미터 추가
        if (after != null && !after.isEmpty()) {
            builder.queryParam("after", after);
        }

        if (limit != null) {
            builder.queryParam("limit", limit);
        }

        String url = builder.toUriString();
        System.out.println("API URL: " + url);

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
            System.out.println("API Response: " + responseBody);

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
     * 특정 밴드의 글 목록을 첫 페이지부터 조회합니다.
     */
    public Map<String, Object> getBandPosts(String accessToken, String bandKey) {
        return getBandPosts(accessToken, bandKey, "ko_KR", null, null);
    }
}