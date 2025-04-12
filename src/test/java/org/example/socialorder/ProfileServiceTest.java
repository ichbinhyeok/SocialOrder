package org.example.socialorder;


import org.example.socialorder.service.BandProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringJUnitConfig
public class ProfileServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BandProfileService profileService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetUserProfile() {
        // 테스트 데이터 준비
        String accessToken = "test-access-token";

        // API 응답 모의 객체 생성
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("user_key", "test-user-key");
        resultData.put("name", "테스트 사용자");
        resultData.put("profile_image_url", "http://example.com/profile.jpg");
        resultData.put("is_app_member", true);
        resultData.put("message_allowed", true);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("result_code", 1);
        responseBody.put("result_data", resultData);

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        // RestTemplate 모의 설정
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(responseEntity);

        // 테스트 실행
        Map<String, Object> profileData = profileService.getUserProfile(accessToken);

        // 결과 검증
        assertNotNull(profileData);
        assertEquals("test-user-key", profileData.get("user_key"));
        assertEquals("테스트 사용자", profileData.get("name"));
        assertEquals("http://example.com/profile.jpg", profileData.get("profile_image_url"));
        assertTrue((Boolean) profileData.get("is_app_member"));
        assertTrue((Boolean) profileData.get("message_allowed"));
    }

    @Test
    public void testFormatProfileData() {
        // 테스트 데이터 준비
        Map<String, Object> profileData = new HashMap<>();
        profileData.put("user_key", "test-user-key");
        profileData.put("name", "테스트 사용자");
        profileData.put("profile_image_url", "http://example.com/profile.jpg");
        profileData.put("is_app_member", true);
        profileData.put("message_allowed", true);

        // 테스트 실행
        Map<String, Object> formattedProfile = profileService.formatProfileData(profileData);

        // 결과 검증
        assertNotNull(formattedProfile);
        assertEquals("test-user-key", formattedProfile.get("사용자 ID"));
        assertEquals("테스트 사용자", formattedProfile.get("이름"));
        assertEquals("http://example.com/profile.jpg", formattedProfile.get("프로필 이미지"));
        assertTrue((Boolean) formattedProfile.get("앱 멤버 여부"));
        assertTrue((Boolean) formattedProfile.get("메시지 수신 허용"));
    }
}