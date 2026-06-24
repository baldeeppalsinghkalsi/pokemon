package com.pokemon.service;

import com.pokemon.dto.PokemonRawResponse;
import com.pokemon.model.Pokemon;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.HttpClientErrorException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Service marks this as a Spring-managed "bean" - a class whose lifecycle
 * (creation, dependency injection) Spring handles for you. This is directly
 * equivalent to registering a class with builder.Services.AddScoped<T>() or
 * AddSingleton<T>() in ASP.NET Core - except here, the annotation alone is
 * enough; Spring finds it via the @ComponentScan from @SpringBootApplication.
 *
 * Convention in layered architecture (same idea as your Phoenix BaseApiController
 * work): Controller -> Service -> (later) Repository/external API.
 * Controllers should stay thin and just handle HTTP concerns; business logic
 * and external calls belong here in the Service.
 */
@Service
public class PokemonService {

    // RestClient is Spring's modern, fluent HTTP client - the rough equivalent
    // of HttpClient in .NET. (You'll also see RestTemplate in older tutorials -
    // that's the legacy/deprecated version, avoid it for new code.)
    private final RestClient restClient;

    private static final String POKEAPI_BASE_URL = "https://pokeapi.co/api/v2";

    /**
     * This constructor is "constructor injection" - Spring sees PokemonService
     * needs to be built, finds no dependencies to inject here (we're building
     * the RestClient ourselves), and instantiates it once. We build the
     * RestClient with a base URL so every call below just appends a path.
     */
    public PokemonService() {
        this.restClient = RestClient.builder()
                .baseUrl(POKEAPI_BASE_URL)
                .build();
    }

    /**
     * Fetches a Pokemon by name and returns OUR clean model, not PokeAPI's
     * raw shape. The caller (the controller) never needs to know or care
     * what PokeAPI's JSON actually looks like.
     *
     * @param name the pokemon name, e.g. "pikachu"
     * @return a populated Pokemon
     * @throws IllegalArgumentException if no pokemon with that name exists
     */
    public Pokemon getPokemonByName(String name) {
        PokemonRawResponse raw;
        try {
            // .body(PokemonRawResponse.class) is where the magic happens:
            // Jackson reads the JSON response and builds a PokemonRawResponse
            // object from it automatically, matching fields by name (and by
            // our @JsonProperty annotations where names differ). This is the
            // same idea as JsonSerializer.Deserialize<T>(json) in .NET - the
            // mapping is just configured via annotations instead of being
            // mostly automatic by convention.
            raw = restClient.get()
                    .uri("/pokemon/{name}", name.toLowerCase())
                    .retrieve()
                    .body(PokemonRawResponse.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new IllegalArgumentException("Pokemon not found: " + name);
        }

        return mapToCleanModel(raw);
    }

    /**
     * Converts PokeAPI's nested raw shape into our flat, friendly Pokemon
     * model. Keeping this mapping logic in one private method (rather than
     * scattering it across the codebase) means if we ever need to change
     * what "interesting attributes" we expose, there's exactly one place
     * to look.
     */
    private Pokemon mapToCleanModel(PokemonRawResponse raw) {
        Pokemon pokemon = new Pokemon();

        pokemon.setId(raw.getId());
        pokemon.setName(raw.getName());
        pokemon.setHeightDecimetres(raw.getHeight());
        pokemon.setWeightHectograms(raw.getWeight());

        // raw.getTypes() is a List<TypeEntry>, where each TypeEntry wraps a
        // NamedResource that finally has the .getName() we want. We use a
        // Java Stream here - the rough equivalent of LINQ in C#: .map() is
        // like .Select(), and .collect(Collectors.toList()) finalizes it
        // into a List, similar to .ToList().
        List<String> typeNames = raw.getTypes().stream()
                .map(entry -> entry.getType().getName())
                .collect(Collectors.toList());
        pokemon.setTypes(typeNames);

        List<String> abilityNames = raw.getAbilities().stream()
                .map(entry -> entry.getAbility().getName())
                .collect(Collectors.toList());
        pokemon.setAbilities(abilityNames);

        // LinkedHashMap preserves insertion order (PokeAPI always returns
        // stats in the same order: hp, attack, defense, etc.), so the
        // frontend gets a predictable, stable ordering instead of whatever
        // a regular HashMap happens to produce.
        Map<String, Integer> stats = new LinkedHashMap<>();
        for (PokemonRawResponse.StatEntry statEntry : raw.getStats()) {
            stats.put(statEntry.getStat().getName(), statEntry.getBaseStat());
        }
        pokemon.setBaseStats(stats);

        if (raw.getSprites() != null) {
            pokemon.setSpriteUrl(raw.getSprites().getFrontDefault());
        }

        return pokemon;
    }

}