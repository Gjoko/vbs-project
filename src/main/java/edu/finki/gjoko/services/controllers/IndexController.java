package edu.finki.gjoko.services.controllers;

import edu.finki.gjoko.services.services.MagnatuneSPARQLService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class IndexController {

    private final MagnatuneSPARQLService magnatuneSPARQLService;


    public IndexController(MagnatuneSPARQLService magnatuneSPARQLService) {
        this.magnatuneSPARQLService = magnatuneSPARQLService;
    }

    @GetMapping
    public String getMapping(Model model) {
        model.addAttribute("artists", magnatuneSPARQLService.fetchInfoAboutArtists(0));
        model.addAttribute("page", 0);
        return "Index";
    }


    @GetMapping("/{page}")
    public String getMapping(Model model, @PathVariable(name = "page") int page) {
        model.addAttribute("artists", magnatuneSPARQLService.fetchInfoAboutArtists(page * 5));
        model.addAttribute("page", page);
        return "Index";
    }

}
