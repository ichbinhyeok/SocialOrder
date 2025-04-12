package org.example.socialorder.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Service
public class BandProfileService {

    private final RestTemplate restTemplate;
    private final String PROFILE_API_URL = "https://openapi.band.us/v2/profile";

    // 생성자 주입 추가
    public BandProfileService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 사용자 프로필 정보를 가져옵니다.
     *
     * @param accessToken 액세스 토큰
     * @param bandKey (선택) 밴드 식별자, null일 경우 기본 프로필로 조회
     * @return 사용자 프로필 정보가 담긴 Map
     */
    public Map<String, Object> getUserProfile(String accessToken, String bandKey) {
        // 헤더 설정 - Bearer 토큰 인증
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // URL 및 파라미터 설정
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(PROFILE_API_URL);

        // bandKey가 있으면 쿼리 파라미터에 추가
        if (bandKey != null && !bandKey.isEmpty()) {
            builder.queryParam("band_key", bandKey);
        }

        String url = builder.toUriString();

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
            Integer resultCode = (Integer) responseBody.get("result_code");

            if (resultCode != null && resultCode == 1) {
                // 성공적인 응답
                return (Map<String, Object>) responseBody.get("result_data");
            } else {
                // 오류 응답 처리
                throw new RuntimeException("밴드 API 오류: " + responseBody);
            }
        } catch (Exception e) {
            // 예외 처리
            throw new RuntimeException("프로필 정보를 가져오는 중 오류 발생: " + e.getMessage(), e);
        }
    }

    /**
     * 기본 사용자 프로필 정보를 가져옵니다.
     *
     * @param accessToken 액세스 토큰
     * @return 사용자 프로필 정보가 담긴 Map
     */
    public Map<String, Object> getUserProfile(String accessToken) {
        return getUserProfile(accessToken, null);
    }

    /**
     * 프로필 정보를 가공하여 사용자에게 표시하기 좋은 형태로 반환합니다.
     *
     * @param profileData API 응답에서 받은 프로필 데이터
     * @return 가공된 프로필 정보
     */
    public Map<String, Object> formatProfileData(Map<String, Object> profileData) {
        Map<String, Object> formattedProfile = new HashMap<>();

        formattedProfile.put("사용자 ID", profileData.get("user_key"));
        formattedProfile.put("이름", profileData.get("name"));
        formattedProfile.put("프로필 이미지", profileData.get("profile_image_url"));
        formattedProfile.put("앱 멤버 여부", profileData.get("is_app_member"));
        formattedProfile.put("메시지 수신 허용", profileData.get("message_allowed"));

        return formattedProfile;
    }
}