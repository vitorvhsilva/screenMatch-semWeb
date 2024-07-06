package br.alura.screenmatch.principal;

import br.alura.screenmatch.model.DadosEpisodio;
import br.alura.screenmatch.model.DadosSerie;
import br.alura.screenmatch.model.DadosTemporada;
import br.alura.screenmatch.service.ConsumoAPI;
import br.alura.screenmatch.service.ConverteDados;
import org.springframework.boot.CommandLineRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));
    }
}
