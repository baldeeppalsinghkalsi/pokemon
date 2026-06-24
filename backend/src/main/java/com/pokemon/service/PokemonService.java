package com.pokemon.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.HttpClientErrorException;
@Service
public class PokemonService {
    private final RestClient restClient;

    private static final String POKEAPI_BASE_URL = "https://pokeapi.co/api/v2";

    public PokemonService() {
        this.restClient = RestClient.builder()
                .baseUrl(POKEAPI_BASE_URL)
                .build();
    }

    public String getPokemonRawByName(String name) {
        try {
            return restClient.get()
                    .uri("/pokemon/{name}", name.toLowerCase())
                    .retrieve()
                    .body(String.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new IllegalArgumentException("Pokemon not found: " + name);
        }
    }

}