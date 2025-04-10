package com.example.demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class SlugifierTest {

    @Test
    void testSlugify() {
        String input = "Esempio di Input";
        String expected = "esempio-di-input";
        String result = Slugifier.slugify(input);
        assertEquals(expected, result);
    }
}
