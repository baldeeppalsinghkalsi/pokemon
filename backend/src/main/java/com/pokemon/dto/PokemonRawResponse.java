package com.pokemon.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PokemonRawResponse {

    private int id;
    private String name;
    private int height;
    private int weight;

    @JsonProperty("base_experience")
    private int baseExperience;

    private List<TypeEntry> types;
    private List<AbilityEntry> abilities;
    private List<StatEntry> stats;
    private Sprites sprites;


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }

    public int getBaseExperience() { return baseExperience; }
    public void setBaseExperience(int baseExperience) { this.baseExperience = baseExperience; }

    public List<TypeEntry> getTypes() { return types; }
    public void setTypes(List<TypeEntry> types) { this.types = types; }

    public List<AbilityEntry> getAbilities() { return abilities; }
    public void setAbilities(List<AbilityEntry> abilities) { this.abilities = abilities; }

    public List<StatEntry> getStats() { return stats; }
    public void setStats(List<StatEntry> stats) { this.stats = stats; }

    public Sprites getSprites() { return sprites; }
    public void setSprites(Sprites sprites) { this.sprites = sprites; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TypeEntry {
        private NamedResource type;
        public NamedResource getType() { return type; }
        public void setType(NamedResource type) { this.type = type; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AbilityEntry {
        private NamedResource ability;
        public NamedResource getAbility() { return ability; }
        public void setAbility(NamedResource ability) { this.ability = ability; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StatEntry {
        @JsonProperty("base_stat")
        private int baseStat;
        private NamedResource stat;

        public int getBaseStat() { return baseStat; }
        public void setBaseStat(int baseStat) { this.baseStat = baseStat; }
        public NamedResource getStat() { return stat; }
        public void setStat(NamedResource stat) { this.stat = stat; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NamedResource {
        private String name;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sprites {
        @JsonProperty("front_default")
        private String frontDefault;
        public String getFrontDefault() { return frontDefault; }
        public void setFrontDefault(String frontDefault) { this.frontDefault = frontDefault; }
    }

}