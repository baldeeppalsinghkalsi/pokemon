package com.pokemon.controller;

import com.pokemon.model.Pokemon;
import com.pokemon.service.PokemonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @RestController = @Controller + @ResponseBody combined. It tells Spring:
 *   1. This class handles incoming HTTP requests (like [ApiController])
 *   2. Method return values should be written directly to the HTTP response
 *      body (serialized to JSON automatically), not resolved to a view/page.
 *
 * @RequestMapping at the class level sets a common URL prefix for every
 * endpoint in this controller - equivalent to [Route("api/pokemon")] in
 * ASP.NET Core.
 */
@RestController
@RequestMapping("/api/pokemon")
public class PokemonController {

    private final PokemonService pokemonService;

    /**
     * Constructor injection again: Spring sees this controller needs a
     * PokemonService, finds the one bean we marked with @Service, and
     * passes it in automatically when creating PokemonController.
     * No [FromServices] or manual resolution needed - this wiring is
     * Spring's core "Inversion of Control" container at work.
     */
    public PokemonController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    /**
     * GET /api/pokemon/{name}
     *
     * Notice the return type is now ResponseEntity<Pokemon> instead of
     * ResponseEntity<String>. Spring + Jackson automatically SERIALIZE our
     * Pokemon object back into JSON for the HTTP response - the mirror
     * image of what happened when PokemonService deserialized PokeAPI's
     * response into PokemonRawResponse. We never manually touch JSON text
     * anywhere in this codebase; Jackson handles both directions for us.
     */
    @GetMapping(value = "/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getPokemonByName(@PathVariable String name) {
        if (name == null || name.isBlank()) {
            return ResponseEntity.badRequest().body("{\"error\": \"name must not be blank\"}");
        }

        try {
            Pokemon pokemon = pokemonService.getPokemonByName(name);
            return ResponseEntity.ok(pokemon);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"" + ex.getMessage() + "\"}");
        }
    }

}