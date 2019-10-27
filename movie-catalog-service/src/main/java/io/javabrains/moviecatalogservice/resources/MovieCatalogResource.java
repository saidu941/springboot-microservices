package io.javabrains.moviecatalogservice.resources;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import io.javabrains.moviecatalogservice.models.CatalogItem;
import io.javabrains.moviecatalogservice.models.Movie;
import io.javabrains.moviecatalogservice.models.UserRating;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

	// get all rated movie IDs
	// for each movie ID, call movie info service and get details
	// put them all together
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private WebClient.Builder webClientBuilder;
	
	@RequestMapping("/{userId}")
	public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {
		
		UserRating ratings = restTemplate.getForObject("http://ratings-data-service/ratings" + userId, UserRating.class);
		return ratings.getUserRatings().stream().map(rating ->{
					Movie movie = restTemplate.getForObject("http://movie-info-service/movies/"+rating.getMovieId(), Movie.class);
					return new CatalogItem(movie.getName(), "desc", rating.getRating());
				})
				.collect(Collectors.toList());
		
	}
	
	
	/* // REACTIVE way of getting/making the call
	 * Movie movie = webClientBuilder.build()
									.get()
									.uri("http://localhost:8081/movies/" + rating.getMovieId())
									.retrieve() //ResponseSpec: go do the fetch
									.bodyToMono(Movie.class) //Mono<Movie> // converting the body into instance of Movie class // Mono: reactive way of saying that you'll get object later, promising now for the future
									.block();
	
	 */
}
