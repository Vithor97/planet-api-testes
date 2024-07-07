package com.example.sw_planet_api.web;

import com.example.sw_planet_api.common.PlanetConstantes;
import com.example.sw_planet_api.domain.Planet;
import com.example.sw_planet_api.service.PlanetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PlanetController.class)
public class PlanetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private PlanetService planetService;

    @Test
    public void createPlanet_WithValidData_ReturnsCreated() throws Exception {

        when(planetService.create(PlanetConstantes.PLANET)).thenReturn(PlanetConstantes.PLANET);

        mockMvc.perform(
                post("/planets")
                        .content(objectMapper.writeValueAsString(PlanetConstantes.PLANET))
                        .contentType(MediaType.APPLICATION_JSON)
                )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$").value(PlanetConstantes.PLANET));
    }

    @Test
    public void createPlanet_WithInvalidData_ReturnsBadRequest() throws Exception {

        Planet emptyPlanet = new Planet();
        Planet invalidPlanet = new Planet("", "", "");

        mockMvc.perform(
                        post("/planets")
                                .content(objectMapper.writeValueAsString(emptyPlanet))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnprocessableEntity());

        mockMvc.perform(
                        post("/planets")
                                .content(objectMapper.writeValueAsString(invalidPlanet

                                ))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void createPlanet_WithExistingName_ReturnsConflict() throws Exception{
        when(planetService.create(any())).thenThrow(DataIntegrityViolationException.class);


        mockMvc.perform(
                        post("/planets")
                                .content(objectMapper.writeValueAsString(PlanetConstantes.PLANET

                                ))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isConflict());
    }

    @Test
    public void getPlanet_ByExistingId_ReturnsPlanet() throws Exception{
        when(planetService.getPlanet(1L)).thenReturn(Optional.of(PlanetConstantes.PLANET));
        mockMvc.perform(
                        get("/planets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(PlanetConstantes.PLANET));
    }

    @Test
    public void getPlanet_ByUnexistingId_ReturnsNotFound() throws Exception{

        mockMvc.perform(get("/planets/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getPlanet_ByExistingName_ReturnsPlanet() throws Exception{
        when(planetService.getPlanetByName("name")).thenReturn(Optional.of(PlanetConstantes.PLANET));
        mockMvc.perform(
                        get("/planets/name/name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(PlanetConstantes.PLANET));
    }

    @Test
    public void getPlanet_ByUnexistingName_ReturnsNotFound() throws Exception{
        mockMvc.perform(get("/planets/name/name"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void listPlanets_ReturnsNoPlanets() throws Exception {
        when(planetService.listPlanets(null, null)).thenReturn(Collections.emptyList());

        mockMvc
                .perform(
                        get("/planets/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void removePlanet_WithExistingId_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/planets/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void removePlanet_WithUnexistingId_ReturnsNotFound() throws Exception {
        final Long planetId = 1L;

        //usado ao contrario do when por que o doThrow é usado para metodos void
        doThrow(new EmptyResultDataAccessException(1)).when(planetService).remove(planetId);

        mockMvc.perform(delete("/planets/" + planetId))
                .andExpect(status().isNotFound());
    }
}
