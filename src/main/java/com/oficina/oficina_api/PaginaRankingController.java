package com.oficina.oficina_api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaginaRankingController {

    @GetMapping("/ranking")
    public String paginaRanking() {
        return "forward:/ranking.html";
    }
}
