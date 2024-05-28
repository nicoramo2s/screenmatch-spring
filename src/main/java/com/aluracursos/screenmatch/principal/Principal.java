package com.aluracursos.screenmatch.principal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;

import com.aluracursos.screenmatch.model.Categoria;
import com.aluracursos.screenmatch.model.DatosSerie;
import com.aluracursos.screenmatch.model.DatosTemporadas;
import com.aluracursos.screenmatch.model.Episodio;
import com.aluracursos.screenmatch.model.Serie;
import com.aluracursos.screenmatch.repository.SerieRepository;
import com.aluracursos.screenmatch.service.ConsumoApi;
import com.aluracursos.screenmatch.service.ConvierteDatos;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private final String URL_BASE = "https://www.omdbapi.com/?t=";
    @Value("${API_KEY_OMDB}")
    private String API_KEY_OMDB;
    private final String API_KEY = "&apikey=888407a5";
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<Serie> series;
    private Optional<Serie> serieBuscada;

    private SerieRepository serieRepository;

    public Principal(SerieRepository repository) {
        this.serieRepository = repository;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """

                    1 - Buscar series
                    2 - Buscar episodios
                    3 - Mostrar series buscadas
                    4 - Buscar series por titulo
                    5 - Top 5 mejores series
                    6 - Buscar series por categoria
                    7 - Filtrar series
                    8 - Buscar Episodios por titulo
                    9 - Top 5 Episodios por serie

                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    mostrarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriesPorTitulo();
                    break;
                case 5:
                    buscarTop5Series();
                    break;
                case 6:
                    buscarSeriesPorCategorias();
                    break;
                case 7:
                    filtrarSeriesPorTemporadaYEvaluacion();
                    break;
                case 8:
                    buscarEpisodioPorTitulo();
                    break;
                case 9:
                    buscarTop5Episodios();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }

    private DatosSerie getDatosSerie() {
        System.out.println("Escribe el nombre de la serie que deseas buscar");
        var nombreSerie = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + API_KEY);
        System.out.println(json);
        DatosSerie datos = conversor.obtenerDatos(json, DatosSerie.class);
        return datos;
    }

    private void buscarEpisodioPorSerie() {
        mostrarSeriesBuscadas();
        System.out.println("\nEscribe el nombre de la serie de la cual quieres ver y guardar los episodios");
        var nombreSerie = teclado.nextLine();

        Optional<Serie> serie = series.stream()
                .filter(s -> s.getTitulo().toLowerCase().contains(nombreSerie.toLowerCase()))
                .findFirst();

        if (serie.isPresent()) {
            var serieEncontrada = serie.get();
            List<DatosTemporadas> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumoApi.obtenerDatos(
                        URL_BASE + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DatosTemporadas datosTemporada = conversor.obtenerDatos(json, DatosTemporadas.class);
                temporadas.add(datosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            serieRepository.save(serieEncontrada);
        }
    }

    private void buscarSerieWeb() {
        DatosSerie datos = getDatosSerie();
        Serie serie = new Serie(datos);
        serieRepository.save(serie);
        System.out.println(datos);
    }

    private void mostrarSeriesBuscadas() {
        series = serieRepository.findAll();

        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriesPorTitulo() {
        System.out.println("\nEscribe el nombre de la serie que deseas buscar");
        var nombreSerie = teclado.nextLine();
        serieBuscada = serieRepository.findByTituloContainsIgnoreCase(nombreSerie);

        if (serieBuscada.isPresent()) {
            System.out.println("La serie buscada es: " + serieBuscada.get());
        } else {
            System.err.println("Serie no encontrada");
        }
    }

    private void buscarTop5Series() {
        List<Serie> topSeries = serieRepository.findTop5ByOrderByEvaluacionDesc();
        topSeries.forEach(s -> System.out.println("Serie: " + s.getTitulo() + " Evaluacion: " + s.getEvaluacion()+ "\n"));
    }

    private void buscarSeriesPorCategorias() {
        System.out.println("Escriba el Genero/Categoria de la serie que desea buscar");
        String generoBuscado = teclado.nextLine();
        Categoria categoria = Categoria.fromEspanol(generoBuscado);
        List<Serie> seriesPorCategoria = serieRepository.findByGenero(categoria);
        System.out.println("Series de la categoria: " + generoBuscado);
        seriesPorCategoria.forEach(System.out::println);
    }

    private void filtrarSeriesPorTemporadaYEvaluacion() {
        System.out.println("¿Filtrar séries con cuántas temporadas? ");
        var totalTemporadas = teclado.nextInt();
        teclado.nextLine();

        System.out.println("¿Con evaluación apartir de cuál valor? ");
        var evaluacion = teclado.nextDouble();
        teclado.nextLine();

        List<Serie> filtroSeries = serieRepository.seriesPortemporadaYEvaluacion(totalTemporadas, evaluacion);

        System.out.println("*** Series filtradas ***");
        filtroSeries.forEach(s -> System.out.println(s.getTitulo() + "  - evaluacion: " + s.getEvaluacion()));
    }

    private void buscarEpisodioPorTitulo() {
        System.out.println("\nEscribe el nombre del Episodio que deseas buscar");
        String nombreEpisodio = teclado.nextLine();

        List<Episodio> episodiosEncontrados = serieRepository.episodioPorNombre(nombreEpisodio);
        episodiosEncontrados.forEach(e -> System.out.printf("Serie: %s, Temporada: %s, Episodio: %s, Evaluacion: %s\n",
                e.getSerie(), e.getTemporada(), e.getNumeroEpisodio(), e.getEvaluacion()));
    }

    private void buscarTop5Episodios() {
        buscarSeriesPorTitulo();
        if (serieBuscada.isPresent()) {
            Serie serie = serieBuscada.get();
            List<Episodio> top5Episodios = serieRepository.top5Episodios(serie);
            top5Episodios.forEach(e -> System.out.printf("Serie: %s, Temporada: %s, Titulo del Episodio: %s, Evaluacion: %s\n",
            e.getSerie().getTitulo(), e.getTemporada(), e.getTitulo(), e.getEvaluacion()));
        }
    }
}
