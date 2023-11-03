package com.ruanbianca.redesocial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public class PostagemAvancada extends Postagem {

    private final Integer numeroPadraoVisualizacoesRestantes = 10;

    private Integer _visualizacoesRestantes;
    private ArrayList<String> _hashtags;
    
    public PostagemAvancada(String texto, Perfil perfil, ArrayList<String> hashtags) {
        
        super(texto, perfil);
        this._visualizacoesRestantes = numeroPadraoVisualizacoesRestantes;
        this._hashtags = (Optional.ofNullable(hashtags).isEmpty()) ? new ArrayList<>() : hashtags;
    }

    public PostagemAvancada(String texto, Perfil perfil, String ... hashtags) {

        this(texto, perfil, new ArrayList<>(Arrays.asList(hashtags)));
    }

    public void adicionarHashtag(String hashtag) {
        if(Optional.ofNullable(hashtag).isPresent())
            _hashtags.add(hashtag);
    }

    public boolean ehExibivel(){
        return _visualizacoesRestantes > 0;
    }

    public boolean existeHashtag(String hashtag) {
        
        Stream <String> hashs = _hashtags.stream();

        return hashs.anyMatch(h -> h.equals(hashtag));
    }

    public void decrementarVisualizacoes() {
        if(_visualizacoesRestantes>0)
            _visualizacoesRestantes--;
    }

    public ArrayList<String> getHashtags (){
        return _hashtags;
    }

    public Integer getVisualizacoesRestantes() {
        return _visualizacoesRestantes;
    }

}
