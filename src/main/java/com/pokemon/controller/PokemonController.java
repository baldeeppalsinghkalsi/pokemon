package com.pokemon.controller;

import com.pokemon.service.PokemonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pokemon")
public class PokemonController {

    private final PokemonService pokemonService;

    public PokemonController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    @GetMapping(value = "/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPokemonByName(@PathVariable String name) {
        if (name == null || name.isBlank()) {
            return ResponseEntity.badRequest().body("{\"error\": \"name must not be blank\"}");
        }

        try {
            String rawJson = pokemonService.getPokemonRawByName(name);
            return ResponseEntity.ok(rawJson);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"" + ex.getMessage() + "\"}");
        }
    }

}