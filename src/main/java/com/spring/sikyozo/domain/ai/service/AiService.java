package com.spring.sikyozo.domain.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.sikyozo.domain.ai.entity.Ai;
import com.spring.sikyozo.domain.ai.entity.dto.request.AiRequestDto;
import com.spring.sikyozo.domain.ai.repository.AiRepository;
import com.spring.sikyozo.domain.menu.entity.Menu;
import com.spring.sikyozo.domain.menu.exception.MenuNotFoundException;
import com.spring.sikyozo.domain.menu.repository.MenuRepository;
import com.spring.sikyozo.domain.user.entity.User;
import com.spring.sikyozo.domain.user.exception.UserNotFoundException;
import com.spring.sikyozo.domain.user.repository.UserRepository;
import com.spring.sikyozo.global.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AiService {

    private final AiRepository aiRepository;
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;
    private final ObjectMapper objectMapper;
    private final WebClient.Builder webClientBuilder;
    private final SecurityUtil securityUtil;

    @Value("${api.key}")
    private String apiKey;

    public String callApi(UUID menuId, AiRequestDto requestDto) {
        User user = securityUtil.getCurrentUser();

        Menu menu = menuRepository.findById(menuId).orElseThrow(MenuNotFoundException::new);

        try {
            // 요청 본문을 AiRequestDto 객체로 구성
            AiRequestDto aiRequestDto = createAiRequestDto(requestDto);
            String requestBody = objectMapper.writeValueAsString(aiRequestDto);
            String response = callExternalApi(requestBody);

            // 응답에서 텍스트를 추출하고 DB에 저장
            String extractedText = extractTextFromResponse(response);

            // 올바른 텍스트를 저장하기 위해 requestDto에서 직접 추출할 수 없는 경우, 대체값 사용
            String originalText = extractOriginalText(requestDto);

            saveResponse(user, extractedText, originalText,menu);

            return extractedText;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    private AiRequestDto createAiRequestDto(AiRequestDto requestDto) {
        AiRequestDto.Part part = new AiRequestDto.Part();
        if (requestDto.getContents() != null && !requestDto.getContents().isEmpty()) {
            part.setText(requestDto.getContents().get(0).getParts().get(0).getText());
        } else {
            part.setText("default text"); // 기본 텍스트 설정
        }

        AiRequestDto.Content content = new AiRequestDto.Content();
        content.setParts(List.of(part));

        AiRequestDto aiRequestDto = new AiRequestDto();
        aiRequestDto.setContents(List.of(content));

        return aiRequestDto;
    }

    private String extractOriginalText(AiRequestDto requestDto) {
        if (requestDto.getContents() != null && !requestDto.getContents().isEmpty()) {
            AiRequestDto.Content content = requestDto.getContents().get(0);
            if (content.getParts() != null && !content.getParts().isEmpty()) {
                return content.getParts().get(0).getText();
            }
        }
        return "No text available";
    }

    private String callExternalApi(String requestBody) {
        WebClient webClient = webClientBuilder.build();
        return webClient.post()
                .uri("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=" + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block(); // 동기식 호출
    }

    private String extractTextFromResponse(String response) {
        try {
            JsonNode rootNode = objectMapper.readTree(response);

            // 응답 구조에서 텍스트 추출
            JsonNode candidatesNode = rootNode.path("candidates");
            if (candidatesNode.isArray() && candidatesNode.size() > 0) {
                JsonNode contentNode = candidatesNode.get(0).path("content");
                JsonNode partsNode = contentNode.path("parts");
                if (partsNode.isArray() && partsNode.size() > 0) {
                    return partsNode.get(0).path("text").asText();
                }
            }
            return "Error: No content found in response";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Failed to extract text from response";
        }
    }

    private void saveResponse(User user, String responseText, String requestText, Menu menu) {
        Ai ai = new Ai();
        ai.saveAiResponse(user, responseText, requestText,menu);
        aiRepository.save(ai);
    }
}
