package com.pokemon.model;

import java.util.List;
import java.util.Map;

/**
 * This is OUR API's response shape - what the frontend actually receives.
 * It's deliberately simple and flat compared to PokeAPI's raw response.
 *
 * This separation (raw DTO -> clean model) is a form of the "anti-corruption
 * layer" pattern: our frontend's contract with us shouldn't be hostage to
 * however a third-party vendor (PokeAPI) decides to shape their JSON.
 */
public class Pokemon {

    private int id;
    private String name;
    private int heightDecimetres;
    private int weightHectograms;
    private List<String> types;
    private List<String> abilities;
    private Map<String, Integer> baseStats; // e.g. "hp" -> 35, "attack" -> 55
    private String spriteUrl;

    // A no-arg constructor + setters (the "JavaBean" pattern) is one common
    // way to build these objects step by step. We'll look at a more concise
    // alternative (Java "records") later once you're comfortable with this.
    public Pokemon() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getHeightDecimetres() { return heightDecimetres; }
    public void setHeightDecimetres(int heightDecimetres) { this.heightDecimetres = heightDecimetres; }

    public int getWeightHectograms() { return weightHectograms; }
    public void setWeightHectograms(int weightHectograms) { this.weightHectograms = weightHectograms; }

    public List<String> getTypes() { return types; }
    public void setTypes(List<String> types) { this.types = types; }

    public List<String> getAbilities() { return abilities; }
    public void setAbilities(List<String> abilities) { this.abilities = abilities; }

    public Map<String, Integer> getBaseStats() { return baseStats; }
    public void setBaseStats(Map<String, Integer> baseStats) { this.baseStats = baseStats; }

    public String getSpriteUrl() { return spriteUrl; }
    public void setSpriteUrl(String spriteUrl) { this.spriteUrl = spriteUrl; }
}