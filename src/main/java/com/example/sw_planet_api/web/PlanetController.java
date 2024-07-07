package com.example.sw_planet_api.web;

import com.example.sw_planet_api.domain.Planet;
import com.example.sw_planet_api.service.PlanetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/planets")
public class PlanetController {

    @Autowired
    private PlanetService service;

    @PostMapping
    public ResponseEntity<Planet> create(@RequestBody @Valid Planet planet) {
        var planetCreated = service.create(planet);
        return ResponseEntity.status(HttpStatus.CREATED).body(planetCreated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Planet> get(@PathVariable Long id) {

        return service.getPlanet(id).
                map(planet ->ResponseEntity.ok(planet))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Planet> getByName(@PathVariable String name) {

        return service.getPlanetByName(name).
                map(planet ->ResponseEntity.ok(planet))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("list")
    public ResponseEntity<List<Planet>> listPlanets(
            @RequestParam(name = "terrain", required = false) String terrain,
            @RequestParam(name = "climate", required = false) String climate
    ) {
        List<Planet> planets = service.listPlanets(terrain, climate);
        return ResponseEntity.ok(planets);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.remove(id);
        return ResponseEntity.noContent().build();
    }




}
