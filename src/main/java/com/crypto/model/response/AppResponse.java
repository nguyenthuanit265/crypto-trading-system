package com.crypto.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppResponse {
    private String path;
    private String error;
    private String message;
    private long timestamp;
    private int status;
    private Object data;

    public static AppResponse buildResponse(String error, String path, String message, int statusCode, Object data) {
        AppResponse res = new AppResponse();
        res.setTimestamp(System.currentTimeMillis());
        res.setStatus(statusCode);
        res.setPath(path);
        res.setError(error);
        res.setData(data);
        res.setMessage(message);
        return res;
    }
}
