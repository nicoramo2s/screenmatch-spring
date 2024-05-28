package com.aluracursos.screenmatch.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aluracursos.screenmatch.dto.EpisodioDTO;
import com.aluracursos.screenmatch.dto.SerieDTO;
import com.aluracursos.screenmatch.service.SerieService;

@RestController
@RequestMapping("/series")
public class SerieController {

    private final SerieService serieService;

    SerieController(SerieService serieService) {
        this.serieService = serieService;
    }

    @GetMapping("/hola")
    public String muestraMensaje() {
        return new String("""
                    <body style="background-color: black">
                    <h1 style="color: white; text-align: center; margin: 10rem auto;">Hola Mundo desde Spring</h1>
                    </body>
                """);
    }

    @GetMapping("/{id}")
    public SerieDTO obetenerPorId(@PathVariable Long id) {
        return serieService.obetenerPorId(id);
    }

    @GetMapping
    public List<SerieDTO> obtenerTodasLasSeries() {
        return serieService.obtenerTodasLasSeries();
    }

    @GetMapping("/top5")
    public List<SerieDTO> obtenerTop5Series() {
        return serieService.obtenerTop5();
    }

    @GetMapping("/lanzamientos")
    public List<SerieDTO> obetenerLanzamientoMasRecientes() {
        return serieService.obtenerLazamientoMasRecientes();
    }

    @GetMapping("/{id}/temporadas/todas")
    public List<EpisodioDTO> obtenerTodasLasTemporadas(@PathVariable Long id) {
        return serieService.obtenerTodasLasTemporadas(id);
    }

    @GetMapping("/{id}/temporadas/{idTemporada}")
    public List<EpisodioDTO> obtenerTemporadasPorId(@PathVariable Long id,
            @PathVariable Long idTemporada) {
        return serieService.obtenerTemporadasPorId(id, idTemporada);
    }

    @GetMapping("/categoria/{nombreGenero}")
    public List<SerieDTO> obtenerSeriePorCategoria(@PathVariable("nombreGenero") String genero) {
        return serieService.obtenerSeriesPorCategoria(genero);
    }

}
