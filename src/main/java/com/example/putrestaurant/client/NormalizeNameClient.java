package com.example.putrestaurant.client;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/* eventuale per contattare normalizename, in tal caso da aggiungere dipendenza feign
 @FeignClient(name = "normalizerClient", url = "http://localhost:8081")*/
public interface NormalizeNameClient {
    @PostMapping("/api/normalize")
    String normalize(@RequestBody String input);
}
