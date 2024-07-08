package br.alura.screenmatch.principal;

import br.alura.screenmatch.model.DadosEpisodio;
import br.alura.screenmatch.model.DadosSerie;
import br.alura.screenmatch.model.DadosTemporada;
import br.alura.screenmatch.model.Episodio;
import br.alura.screenmatch.service.ConsumoAPI;
import br.alura.screenmatch.service.ConverteDados;
import org.springframework.boot.CommandLineRunner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitor = new Scanner(System.in);

    private final ConsumoAPI consumoAPI = new ConsumoAPI();

    private final ConverteDados conversor = new ConverteDados();

    private final String ENDERECO = "https://www.omdbapi.com/?t=";

    private final String API_KEY = "&apikey=1b95b0fc";

    public void exibeMenu() {
        System.out.println("Digite o nome da série para a busca");
        String nomeSerie = leitor.nextLine();

        String json = consumoAPI.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dadosSerie = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dadosSerie);

        List<DadosTemporada> temporadas = new ArrayList<>();

		for (int i = 1; i < dadosSerie.totalTemporadas(); i++) {
			json = consumoAPI.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&season=" + i + API_KEY);
			DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
			temporadas.add(dadosTemporada);
		}

//		temporadas.forEach(System.out::println);

//        for (DadosTemporada temporada : temporadas) {
//            List<DadosEpisodio> episodiosTemporadas = temporada.episodios();
//            for (DadosEpisodio episodioTemporada : episodiosTemporadas) {
//                System.out.println(episodioTemporada.titulo());
//            }
//        }

//        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

//        List<DadosEpisodio> dadosEpisodios = temporadas
//                .stream()
//                .flatMap(t -> t.episodios().stream())
//                .collect(Collectors.toList());
//
//        System.out.println("\nTop 5 Episodios");
//        dadosEpisodios
//                .stream()
//                .filter(d -> !d.avaliacao().equalsIgnoreCase("N/A"))
//                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
//                .limit(5)
//                .forEach(System.out::println);

        List<Episodio> episodios = temporadas
                .stream()
                .flatMap(t -> t.episodios().stream()
                .map(e -> new Episodio(t.numeroTemp(), e)))
                .collect(Collectors.toList());

//        episodios.forEach(System.out::println);
//
//        System.out.println("A partir de que ano voce deseja ver os episodios?");
//        Integer ano = leitor.nextInt();leitor.nextLine();
//
//        LocalDate dataBusca = LocalDate.of(ano, 1, 1);
//        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//
//        episodios.
//                stream()
//                .filter(e -> e.getDataEp() != null && e.getDataEp().isAfter(dataBusca))
//                .forEach(e -> System.out.println(
//                        "Temporada: " + e.getTemporada() +
//                        ", Episodio: " + e.getNumeroEp() +
//                        ", Data Lancamento: " + e.getDataEp().format(formatador)
//                ));

//        System.out.println("Digite o trecho do titulo");
//        String trechoTitulo = leitor.nextLine();
//
//        Optional<Episodio> episodioBuscado = episodios.stream()
//                .filter(e -> e.getTitulo().toLowerCase().contains(trechoTitulo.toLowerCase()))
//                .findFirst();
//
//        if(episodioBuscado.isPresent()){
//            System.out.println("Episódio encontrado!");
//            System.out.println("Temporada: " + episodioBuscado.get().getTemporada());
//        } else {
//            System.out.println("Episódio não encontrado!");
//        }

//        Map<Integer, Double> avaliacoesPorTemporada = episodios
//                .stream()
//                .filter(e -> e.getAvaliacao() > 0.0)
//                .collect(Collectors.groupingBy(Episodio::getTemporada,
//                        Collectors.averagingDouble(Episodio::getAvaliacao)));
//
//        System.out.println(avaliacoesPorTemporada);

        DoubleSummaryStatistics est = episodios
                .stream()
                .filter(e -> e.getAvaliacao() > 0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));

        System.out.println("Média: " + est.getAverage());
        System.out.println("Pior episodio: " + est.getMax());
        System.out.println("Melhor episodio: " + est.getMin());
        System.out.println("Quantidade de episodios: " + est.getCount());

    }
}
