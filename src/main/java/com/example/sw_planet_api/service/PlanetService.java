package com.example.sw_planet_api.service;

import com.example.sw_planet_api.domain.Planet;
import com.example.sw_planet_api.domain.PlanetRepository;
import com.example.sw_planet_api.domain.QueryBuilder;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlanetService {

    private PlanetRepository repository;

    public PlanetService(PlanetRepository repository) {
        this.repository = repository;
    }

    public Planet create(Planet planet) {
        return repository.save(planet);
    }

    public Optional<Planet> getPlanet(long l) {
        return repository.findById(l);
    }

    public Optional<Planet> getPlanetByName(String name) {
        return repository.findByName(name);
    }

    public List<Planet> listPlanets(String terrain, String climate) {
        Example<Planet> query = QueryBuilder.makeQuery(new Planet(climate, terrain));
        return repository.findAll(query);
    }
    public void remove(Long id) {
        repository.deleteById(id);
    }
}
