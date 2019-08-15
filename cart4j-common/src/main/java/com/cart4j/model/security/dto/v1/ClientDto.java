package com.cart4j.model.security.dto.v1;

import lombok.*;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class ClientDto {
    private Long id;
    private String clientUniqueId;
    private String clientSecret;
    private String grantTypes;
    private List<ResourceDto> resources;
}
