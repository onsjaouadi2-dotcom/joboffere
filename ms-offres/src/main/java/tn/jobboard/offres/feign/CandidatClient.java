package tn.jobboard.offres.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@FeignClient(name = "ms-candidats")
public interface CandidatClient {

    @GetMapping("/candidats")
    List<Map> getAllCandidats();

    @GetMapping("/candidats/{id}")
    Map getCandidatById(@PathVariable String id);

    @GetMapping("/candidats/offre/{offreId}")
    List<Map> getCandidatsByOffreId(@PathVariable Long offreId);
}