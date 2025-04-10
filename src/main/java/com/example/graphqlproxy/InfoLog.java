package com.example.graphqlproxy;

import lombok.Getter;

/**
 * Rappresenta l'oggetto JSON ricevuto in input,
 * simile a "StartProcess-input" in TIBCO.
 */
@Getter
public class InfoLog {
    private PersistedQuery persistedQuery;

    public void setPersistedQuery(PersistedQuery persistedQuery) {
        this.persistedQuery = persistedQuery;
    }
}