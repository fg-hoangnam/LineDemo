package com.line.line_demo.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class APIUtils {

    @Value("${line.access-token}")
    private static String ACCESS_TOKEN;

    private static final Logger log = LoggerFactory.getLogger(APIUtils.class);

    public <T> T callApiSendJson(String apiUrl, HttpMethod method, HttpServletRequest extHeaders, MultiValueMap<String, String> additionalHeaders, Map<String, Object> queryParams, Object body, TypeReference<T> reference) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<JsonNode> responseEntity;
            HttpHeaders headers = new HttpHeaders();
            if (!ObjectUtils.isEmpty(extHeaders)) {
                headers.setAll(Collections.list(extHeaders.getHeaderNames())
                        .stream()
                        .collect(Collectors.toMap(h -> h, extHeaders::getHeader)));
                headers.put("accept-encoding", Collections.singletonList("identity"));
            }

            if (!ObjectUtils.isEmpty(additionalHeaders)) {
                headers.addAll(additionalHeaders);
            }

            headers.put("Content-Type", Collections.singletonList("application/json"));
            URI uri = buildUriWithQueryParams(apiUrl, queryParams);
            HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers);
            responseEntity = restTemplate.exchange(uri.toString(), method, requestEntity, JsonNode.class);
            return JsonMapperUtils.convertJsonToObject(JsonMapperUtils.writeValueAsString(responseEntity.getBody()), reference);
        } catch (HttpStatusCodeException e) {
            log.info("[HttpStatusCodeException] Api Exception: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.info("Api Exception: {}", e.getMessage());
            throw new ResourceAccessException(e.getMessage());
        }
    }

    // Build the URI with query parameters
    private URI buildUriWithQueryParams(String baseUrl, Map<String, Object> queryParams) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseUrl);
        if (!Objects.isNull(queryParams)) {
            queryParams.forEach(uriBuilder::queryParam);
        }
        return uriBuilder.build().toUri();
    }

    public static MultiValueMap<String, String> getAdditionalHeader(Map<String, String> headers) {
        MultiValueMap<String, String> additionalHeaders = new LinkedMultiValueMap<>();
        additionalHeaders.add(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", ACCESS_TOKEN));
        if (!CollectionUtils.isEmpty(headers)) {
            headers.forEach(additionalHeaders::add);
        }
        return additionalHeaders;
    }

    public static MultiValueMap<String, String> getAdditionalHeader(String accessToken, Map<String, String> headers) {
        MultiValueMap<String, String> additionalHeaders = new LinkedMultiValueMap<>();
        additionalHeaders.add(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", StringUtils.isEmpty(accessToken) ? ACCESS_TOKEN : accessToken));
        if (!CollectionUtils.isEmpty(headers)) {
            headers.forEach(additionalHeaders::add);
        }
        return additionalHeaders;
    }

}