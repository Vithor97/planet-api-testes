package com.example.sw_planet_api.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.example.sw_planet_api.common.PlanetConstantes.PLANET;
import static com.example.sw_planet_api.common.PlanetConstantes.TATOOINE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest // já possui um banco H2 para testes
public class PlanetRepositoryTest {

    @Autowired
    private PlanetRepository planetRepository;


    /**
     * Injetar uma classe de teste chamada EntityManager para os testes.
     * <p>
     * Esta classe vai servir para testar se de fato apos o save no banco o objeto foi persistido. Usaremos para uma consulta
     * ao banco de dados e ver se o objeto foi inserido. Esta classe vai ser usada pelo {@link TestEntityManager}.
     * <p>
     *
     * @autor Vitor Miranda
     * @data 02/07/2024
     */
    @Autowired
    private TestEntityManager testEntityManager;

    @AfterEach
    public void tearDown() {
        PLANET.setId(null);
    }

    @Test
    public void createPlanet_WithValidData_ReturnsPlanet() {
        Planet planet = planetRepository.save(PLANET);

        Planet sut = testEntityManager.find(Planet.class, planet.getId());

        assertThat(sut).isNotNull();
        assertThat(sut.getName()).isEqualTo(PLANET.getName());
        assertThat(sut.getClimate()).isEqualTo(PLANET.getClimate());
        assertThat(sut.getTerrain()).isEqualTo(PLANET.getTerrain());

    }

    @ParameterizedTest
    @MethodSource("provideInvalidPlanet")
    public void createPlanet_WithInvalidData_ThrowsException(Planet planet) {
        assertThatThrownBy(() -> planetRepository.save(planet)).isInstanceOf(RuntimeException.class);

    }

    private static Stream<Arguments> provideInvalidPlanet() {
        return Stream.of(
                Arguments.of(new Planet(null, "climate", "terrain")),
                Arguments.of(new Planet("name", null, "terrain")),
                Arguments.of(new Planet("name", "climate", null)),
                Arguments.of(new Planet(null, null, null)),
                Arguments.of(new Planet("", "climate", "terrain")),
                Arguments.of(new Planet("name", "", "terrain")),
                Arguments.of(new Planet("name", "climate", "")),
                Arguments.of(new Planet("", "", ""))
        );
    }

    @Test
    public void createPlanet_WithExistingName_ThrowsException() {
        Planet planet = testEntityManager.persistAndFlush(PLANET);
        testEntityManager.detach(planet);
        planet.setId(null);

        assertThatThrownBy(() -> planetRepository.save(planet)).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void getPlanet_ByExistingId_ReturnsPlanet() {
        Planet planet = testEntityManager.persistAndFlush(PLANET);

        Optional<Planet> byId = planetRepository.findById(planet.getId());
        assertThat(byId).isNotEmpty();
        assertThat(byId.get()).isEqualTo(planet);
    }

    @Test
    public void getPlanet_ByUnexistentId_ReturnsNull() {
        Optional<Planet> byId = planetRepository.findById(1L);
        assertThat(byId).isEmpty();
    }

    @Test
    public void getPlanet_ByExistingName_ReturnsPlanet() {
        Planet planet = testEntityManager.persistAndFlush(PLANET);

        Optional<Planet> byName = planetRepository.findByName(planet.getName());
        assertThat(byName).isNotEmpty();
        assertThat(byName.get()).isEqualTo(planet);
    }

    @Test
    public void getPlanet_ByUnexistingName_ReturnsNull() {
        Optional<Planet> byName = planetRepository.findByName("name");
        assertThat(byName).isEmpty();
    }

    @Sql(scripts = "/import_planets.sql")//script para já deixar pronto uma lista de planetas antes de rodar o teste
    @Test
    public void listPlanets_ReturnsFilteredPlanets() {
        Example<Planet> queryWithoutFilters = QueryBuilder.makeQuery(new Planet());
        Example<Planet> queryWithFilters = QueryBuilder.makeQuery(new Planet(TATOOINE.getClimate(), TATOOINE.getTerrain()));

        List<Planet> responseWithoutFilters = planetRepository.findAll(queryWithoutFilters);
        List<Planet> responseWithFilters = planetRepository.findAll(queryWithFilters);

        assertThat(responseWithoutFilters).isNotEmpty();
        assertThat(responseWithoutFilters).hasSize(3);
        assertThat(responseWithFilters).isNotEmpty();
        assertThat(responseWithFilters).hasSize(1);
        assertThat(responseWithFilters.get(0)).isEqualTo(TATOOINE);
    }

    @Test
    public void listPlanets_ReturnsNoPlanets() {
        Example<Planet> query = QueryBuilder.makeQuery(new Planet());

        List<Planet> response = planetRepository.findAll(query);

        assertThat(response).isEmpty();
    }

    @Test
    public void removePlanet_WithExistingId_RemovesPlanetFromDatabase() {
        Planet planet = testEntityManager.persistFlushFind(PLANET);

        planetRepository.deleteById(planet.getId());

        Planet removedPlanet = testEntityManager.find(Planet.class, planet.getId());
        assertThat(removedPlanet).isNull();
    }

//    @Test
//    public void removePlanet_WithUnexistingId_ThrowsException() {
//        //planetRepository.deleteById(1L);
//        assertThatThrownBy(() -> planetRepository.deleteById(99L)).isInstanceOf(EmptyResultDataAccessException.class);
//    }

    @Test
    @Sql(scripts = "/import_planets.sql")
    public void removePlanet_WithNonExistingId_DoestNotChangePlanetList() {
        planetRepository.deleteById(4L);
        assertThat(testEntityManager.find(Planet.class, 1L)).isInstanceOf(Planet.class);
        assertThat(testEntityManager.find(Planet.class, 2L)).isInstanceOf(Planet.class);
        assertThat(testEntityManager.find(Planet.class, 3L)).isInstanceOf(Planet.class);
    }
}
