package com.pokemon.service;

import com.pokemon.dto.PokemonRawResponse;
import com.pokemon.exception.PokemonNotFoundException;
import com.pokemon.exception.UpstreamServiceException;
import com.pokemon.model.Pokemon;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.cache.annotation.Cacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.pokemon.config.CacheConfig.POKEMON_CACHE;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PokemonService {

    private static final Logger logger = LoggerFactory.getLogger(PokemonService.class);

    private final RestClient restClient;

    private static final String POKEAPI_BASE_URL = "https://pokeapi.co/api/v2";

    public PokemonService() {
        this.restClient = RestClient.builder()
                .baseUrl(POKEAPI_BASE_URL)
                .build();
    }

    @Cacheable(value = POKEMON_CACHE, key = "#name.toLowerCase()")
    public Pokemon getPokemonByName(String name) {

        logger.info("Cache MISS - fetching '{}' from PokeAPI", name);

        PokemonRawResponse raw;
        try {
            raw = restClient.get()
                    .uri("/pokemon/{name}", name.toLowerCase())
                    .retrieve()
                    .body(PokemonRawResponse.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new PokemonNotFoundException(name);
        } catch (HttpClientErrorException ex) {
            throw new UpstreamServiceException("Upstream API error: " + ex.getStatusText(), ex);
        } catch (RestClientException ex) {
            throw new UpstreamServiceException("Unable to fetch Pokémon data at this time", ex);
        }

        if (raw == null) {
            throw new UpstreamServiceException("Received an empty response from upstream API");
        }

        return mapToCleanModel(raw);
    }

    private Pokemon mapToCleanModel(PokemonRawResponse raw) {
        Pokemon pokemon = new Pokemon();

        pokemon.setId(raw.getId());
        pokemon.setName(raw.getName());
        pokemon.setHeightDecimetres(raw.getHeight());
        pokemon.setWeightHectograms(raw.getWeight());

        List<String> typeNames = raw.getTypes() == null ? Collections.emptyList() :
                raw.getTypes().stream()
                        .filter(Objects::nonNull)
                        .map(PokemonRawResponse.TypeEntry::getType)
                        .filter(Objects::nonNull)
                        .map(PokemonRawResponse.NamedResource::getName)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
        pokemon.setTypes(typeNames);

        List<String> abilityNames = raw.getAbilities() == null ? Collections.emptyList() :
                raw.getAbilities().stream()
                        .filter(Objects::nonNull)
                        .map(PokemonRawResponse.AbilityEntry::getAbility)
                        .filter(Objects::nonNull)
                        .map(PokemonRawResponse.NamedResource::getName)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
        pokemon.setAbilities(abilityNames);

        Map<String, Integer> stats = new LinkedHashMap<>();
        if (raw.getStats() != null) {
            for (PokemonRawResponse.StatEntry statEntry : raw.getStats()) {
                if (statEntry == null || statEntry.getStat() == null || statEntry.getStat().getName() == null) {
                    continue;
                }
                stats.put(statEntry.getStat().getName(), statEntry.getBaseStat());
            }
        }
        pokemon.setBaseStats(stats);

        if (raw.getSprites() != null) {
            pokemon.setSpriteUrl(raw.getSprites().getFrontDefault());
        }

        return pokemon;
    }

}
