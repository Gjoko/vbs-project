package edu.finki.gjoko.services.services;

import edu.finki.gjoko.services.dtos.Artist;
import org.apache.jena.query.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MagnatuneSPARQLService {
    private int id = 1;

    private static final String SPARQLEndpoint = "http://dbtune.org/magnatune/sparql/";

    private static final String FETCH_ARTISTS_QUERY = "prefix ns: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
            "prefix mo: <http://purl.org/ontology/mo/>" +
            "select ?artist where {" +
            "   ?artist ns:type mo:MusicArtist." +
            "} ORDER BY ASC(?artist) LIMIT 5 OFFSET <offset>";

    private static final String ARTIST_INFO_QUERY = "prefix foaf: <http://xmlns.com/foaf/0.1/>" +
            "prefix dc: <http://purl.org/dc/elements/1.1/>" +
            "prefix ns4: <http://purl.org/vocab/bio/0.1/>" +
            "SELECT * WHERE {" +
            "<artist> ?p ?o ." +
            "FILTER (?p in (foaf:name,foaf:homepage, foaf:img, dc:description, ns4:olb))" +
            "}";

    public List<Artist> fetchInfoAboutArtists(int offset) {
        return fetchArtistsNames(offset).stream().map(this::fetchInfoAboutArtist).collect(Collectors.toList());
    }

    public Artist fetchInfoAboutArtist(String artistName) {
        Artist artist = new Artist();
        try (QueryExecution qexec = QueryExecutionFactory.sparqlService(SPARQLEndpoint, ARTIST_INFO_QUERY.replace("artist", artistName))) {
            ResultSet resultSet = qexec.execSelect();
            while (resultSet.hasNext()) {
                QuerySolution querySolution = resultSet.nextSolution();
                String predicate = querySolution.get("p").toString();
                switch (predicate) {
                    case "http://purl.org/dc/elements/1.1/description":
                        artist.setDescription(querySolution.get("o").toString());
                        break;
                    case "http://purl.org/vocab/bio/0.1/olb":
                        artist.setAbout(querySolution.get("o").toString());
                        break;
                    case "http://xmlns.com/foaf/0.1/name":
                        artist.setName(querySolution.get("o").toString());
                        break;
                    case "http://xmlns.com/foaf/0.1/img":
                        artist.setImage(querySolution.get("o").toString());
                        break;
                    case "http://xmlns.com/foaf/0.1/homepage":
                        artist.setHomepage(querySolution.get("o").toString());
                        break;
                    default:
                        break;
                }
            }
        }
        return artist;
    }

    public List<String> fetchArtistsNames(int offset) {
        List<String> artists = new ArrayList<>();

        Query query = QueryFactory.create(FETCH_ARTISTS_QUERY.replace("<offset>", String.valueOf(offset)));

        try (QueryExecution qexec = QueryExecutionFactory.sparqlService(SPARQLEndpoint, query)) {
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution solution = results.nextSolution();
                artists.add(solution.get("artist").toString());
            }
        }
        return artists;
    }
}
