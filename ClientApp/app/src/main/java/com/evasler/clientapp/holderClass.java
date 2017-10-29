package com.evasler.clientapp;

import java.io.Serializable;
import java.util.Set;

public class holderClass implements Serializable{

    Set<ClientResult> topResults;

    public void setTopResults(Set<ClientResult> topResults) {
        this.topResults =topResults;

    }

    public Set<ClientResult> getTopResults() {

        return topResults;

    }

}
