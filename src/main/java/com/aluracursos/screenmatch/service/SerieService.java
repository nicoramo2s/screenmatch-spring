package com.aluracursos.screenmatch.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.aluracursos.screenmatch.dto.EpisodioDTO;
import com.aluracursos.screenmatch.dto.SerieDTO;
import com.aluracursos.screenmatch.model.Categoria;
import com.aluracursos.screenmatch.model.Serie;
import com.aluracursos.screenmatch.repository.SerieRepository;

@Service
public class SerieService {

    private final SerieRepository serieRepository;

    SerieService(SerieRepository serieRepository) {
        this.serieRepository = serieRepository;
    }

    public List<SerieDTO> obtenerTodasLasSeries() {
        return convierteDatosASerieDTO(serieRepository.findAll());
    }

    public List<SerieDTO> obtenerTop5() {
        return convierteDatosASerieDTO(serieRepository.findTop5ByOrderByEvaluacionDesc());
    }

    public List<SerieDTO> obtenerLazamientoMasRecientes() {
        return convierteDatosASerieDTO(serieRepository.lanzamientosMasRecientes());
    }

    public SerieDTO obetenerPorId(Long id) {
        Optional<Serie> serie = serieRepository.findById(id);
        if (serie.isPresent()) {
            Serie s = serie.get();
            return new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getEvaluacion(),
                    s.getPoster(), s.getGenero(), s.getActores(), s.getSinopsis());
        }
        return null;
    }

    public List<EpisodioDTO> obtenerTodasLasTemporadas(Long id) {
        Optional<Serie> serie = serieRepository.findById(id);
        if (serie.isPresent()) {
            Serie s = serie.get();
            return s.getEpisodios().stream()
                    .map(e -> new EpisodioDTO(e.getTemporada(), e.getTitulo(), e.getNumeroEpisodio()))
                    .collect(Collectors.toList());
        }
        return null;
    }

    public List<EpisodioDTO> obtenerTemporadasPorId(Long id, Long idTemporada) {
        return serieRepository.obtenerTemporadasPorId(id, idTemporada).stream()
                .map(e -> new EpisodioDTO(e.getTemporada(), e.getTitulo(), e.getNumeroEpisodio()))
                .collect(Collectors.toList());
    }

    public List<SerieDTO> obtenerSeriesPorCategoria(String genero) {
        Categoria categoria = Categoria.fromEspanol(genero);
        return convierteDatosASerieDTO(serieRepository.findByGenero(categoria));
    }

    public List<SerieDTO> convierteDatosASerieDTO(List<Serie> serie) {
        return serie.stream()
                .map(s -> new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getEvaluacion(),
                        s.getPoster(), s.getGenero(), s.getActores(), s.getSinopsis()))
                .collect(Collectors.toList());
    }
}
