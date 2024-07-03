package com.crypto.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HuobiResponse {
    @JsonProperty("data")
    private List<HuobiTicker> data;
}
