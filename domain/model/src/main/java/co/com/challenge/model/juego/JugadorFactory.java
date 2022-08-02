package co.com.challenge.model.juego;

import co.com.challenge.model.juego.value.Alias;
import co.com.challenge.model.juego.value.JugadorId;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class JugadorFactory {

    public static JugadorFactory instance;
    private Set<Jugador> jugadores;

    public JugadorFactory() {
        this.jugadores = new HashSet<>();
    }

    public static JugadorFactory getInstance(){
        if (Objects.isNull(instance)){
            instance = new JugadorFactory();
            return instance;
        }
        return instance;
    }

    public JugadorFactory add(String id, String alias){
        jugadores.add(new Jugador(JugadorId.of(id),new Alias(alias)));
        return this;
    }

    public Set<Jugador> jugadores() {
        return jugadores;
    }

}
