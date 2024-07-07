package com.example.sw_planet_api.service;

import com.example.sw_planet_api.domain.Planet;
import com.example.sw_planet_api.domain.PlanetRepository;
import com.example.sw_planet_api.domain.QueryBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;

import java.util.*;

import static com.example.sw_planet_api.common.PlanetConstantes.INVALIDPLANET;
import static com.example.sw_planet_api.common.PlanetConstantes.PLANET;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlanetServiceTest {


    @InjectMocks
    private PlanetService planetService;

    @Mock
    private PlanetRepository planetRepository;

    @Test
    public void createPlanet_WithValidData_ReturnsPlanet() {
        when(planetRepository.save(PLANET)).thenReturn(PLANET);
        Planet sut = planetService.create(PLANET);
        assertThat(sut).isEqualTo(PLANET);
    }

    @Test
    public void createPlanet_WithInvalidData_ThrowsException() {
        when(planetRepository.save(INVALIDPLANET)).thenThrow(RuntimeException.class);
        assertThatThrownBy(() -> planetService.create(INVALIDPLANET)).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void getPlanet_ByExistingId_ReturnsPlanet() {
        when(planetRepository.findById(1L)).thenReturn(Optional.of(PLANET));
        Optional<Planet> planet = planetService.getPlanet(1L);

        assertThat(planet.get()).isEqualTo(PLANET);
    }

    @Test
    public void getPlanet_ByUnexistingId_ReturnsEmpty() {
        when(planetRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<Planet> planet = planetService.getPlanet(1L);
        assertThat(planet).isEmpty();
    }

    @Test
    public void getPlanet_ByExistingName_ReturnsPlanet() {
        when(planetRepository.findByName(PLANET.getName())).thenReturn(Optional.of(PLANET));

        Optional<Planet> planet = planetService.getPlanetByName(PLANET.getName());

        assertThat(planet).isNotEmpty();
        assertThat(planet.get()).isEqualTo(PLANET);
    }

    @Test
    public void getPlanet_ByUnexistingName_ReturnsEmpty() {

        var nomeInexistente = "NOME INEXISTENTE";

        when(planetRepository.findByName(nomeInexistente)).thenReturn(Optional.empty());
        Optional<Planet> planet = planetService.getPlanetByName(nomeInexistente);

        assertThat(planet).isEmpty();
    }


    @Test
    public void listPlanets_WithValidData_ReturnsPlanets() {
        List<Planet> planets = new ArrayList<>(){
            {
                add(PLANET);
            }
        };

        Example<Planet> query = QueryBuilder.makeQuery(new Planet(PLANET.getClimate(), PLANET.getTerrain()));
        when(planetRepository.findAll(query)).thenReturn(planets);

        List<Planet> sut = planetService.listPlanets(PLANET.getTerrain(),PLANET.getClimate());

        assertThat(sut).isNotEmpty();
        assertThat(sut).hasSize(1);
        assertThat(sut.getFirst()).isEqualTo(PLANET);
    }

    @Test
    public void listPlanets_ReturnsNoPlanet() {
        when(planetRepository.findAll(any())).thenReturn(Collections.emptyList());

        List<Planet> sut = planetService.listPlanets(PLANET.getTerrain(), PLANET.getClimate());

        assertThat(sut).isEmpty();
    }

    @Test
    public void removePlanet_WithExistingId_doesNotTrowAnyException() {
        assertThatCode(() -> planetService.remove(1L)).doesNotThrowAnyException();
    }

    @Test
    public void removePlanet_WithUnexistingId_throwsException() {
        doThrow(new RuntimeException()).when(planetRepository).deleteById(99L);
        assertThatThrownBy(() -> planetService.remove(99L)).isInstanceOf(RuntimeException.class);
    }

}