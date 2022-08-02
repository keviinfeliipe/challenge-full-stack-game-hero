package co.com.challenge.model.juego;


import co.com.challenge.model.juego.event.*;
import co.com.challenge.model.juego.value.Alias;
import co.com.challenge.model.juego.value.JugadorId;
import co.com.challenge.model.juego.value.RondaNumero;
import co.com.sofka.domain.generic.EventChange;

import java.util.HashSet;

public class JuegoChange extends EventChange {
    public JuegoChange(Juego juego) {

        apply((JuegoCreado event)->{
            juego.jugadores = new HashSet<>();
            juego.jugadores.add(new Jugador(JugadorId.of(event.getJugadorId()), new Alias(event.getAlias())));
            juego.mazo = new Mazo();
            juego.jugando=false;
            juego.cartasTemporales=new HashSet<>();
        });

        apply((JugadorCreado event)->{
            if(juego.jugadores.size()>5){
                throw new IllegalArgumentException("Solo se puede maximo 6 jugadores");
            }
            juego.jugadores.addAll(event.getJugadorFactory().jugadores());
        });

        apply((JuegoIniciado event)-> juego.jugando=true);

        apply((TableroCreado event)-> juego.tablero= new Tablero());

        apply((RondaCreada event)-> juego.ronda = new Ronda(event.getJugadorIds(),new RondaNumero(event.getJugadorIds().size())));

        apply((RondaDeDesempateCreada event)->{
            juego.cartasTemporales.addAll(juego.tablero.cartaMap().values());
            juego.tablero.cartaMap().values().removeAll(juego.cartasTemporales);
        });

        apply((CronometroRestablecido event)-> juego.tablero().restablecerTiempo());

        apply((TableroHabilitado event)-> juego.cambiarEstadoDelTablero(true));


        apply((TiempoDescontado event)-> juego.tablero.descontarTiempo());

        apply((TableroDeshabilitado event)-> juego.cambiarEstadoDelTablero(false));

        apply((CartaAlAzarSeleccionada event)->{

        });

        apply((GanadorDeRondaDeterminado event)->{
            var jugadorActivo= juego.buscarJugadorPorId(event.getJugadorId()).isPresent();
            var cartaFactory = new CartaFactory();
            event.getFactory().cartas().forEach(cartaFactory::add);
            juego.cartasTemporales.forEach(cartaFactory::add);
            juego.cartasTemporales.clear();
            juego.tablero=new Tablero();
            if (jugadorActivo){
                var jugador = juego.buscarJugadorPorId(event.getJugadorId()).orElseThrow();
                jugador.agragarPuntajeAJugador();
                cartaFactory.cartas().forEach(jugador::agregarCartaAJugador);
            }else{
                juego.agregarCartasMazoPrincipal(cartaFactory);
            }

        });

        apply((GanadorDeJuegoDeterminado event)-> {
            juego.ganador=event.getGanador();
            juego.jugando=false;
        });

        apply((CartaJugada event)->{
            var jugador = juego.buscarJugadorPorId(JugadorId.of(event.getJugadorId())).orElseThrow(() -> {
                throw new IllegalArgumentException("El jugado no se encuntra en la sala.");
            });

            if (!juego.tablero.habilitado().value()){
                throw new IllegalArgumentException("El tablero está bloqueado.");
            }

            if (!juego.ronda.jugadores().contains(jugador.identity())){
                throw new IllegalArgumentException("El jugador no está en la ronda");
            }

            var carta = jugador.mazo().cartas().stream()
                    .filter(carta1 -> carta1.identity().value().equals(event.getCartaId()))
                    .findFirst()
                    .orElseThrow();
            if(Boolean.TRUE.equals(juego.tablero.habilitado().value())){
                jugador.quitarCartaAJugador(carta);
                juego.tablero.cartaMap().put(jugador.identity(),carta);
            }
        });

        apply((CartasAgregadasAJugador event)->{
            var jugador = juego.buscarJugadorPorId(event.getJugadorId()).orElseThrow(() -> {
                throw new IllegalArgumentException("Jugador no encontrado");
            });;

            event.getCartaFactory().cartas().forEach(jugador::agregarCartaAJugador);
        });

        apply((CartaQuitada event)->{
            var jugador = juego.buscarJugadorPorId(event.getJugadorId()).orElseThrow(() -> {
                throw new IllegalArgumentException("Jugador no encontrado");
            });
            jugador.quitarCartaAJugador(event.getCarta());
        });

        apply((CartaAgregadaAlTablero event)->{
            var jugador = juego.buscarJugadorPorId(event.getJugadorId()).orElseThrow(() -> {
                throw new IllegalArgumentException("Jugador no encontrado");
            });
            juego.tablero.cartaMap().put(jugador.identity(),event.getCarta());
        });

        apply((CartasApostadasMostradas event)-> juego.tablero.cartaMap().forEach((jugadorId, carta) -> carta.mostrarCarta()));

        apply((CartasDelTableroDeshabilitadas event)-> juego.tablero().cartaMap().values().forEach(Carta::deshabilitarCarta));

        apply((JugadorRetirado event)->{
            var jugador = juego.buscarJugadorPorId(event.getJugadorId()).orElseThrow(() -> {
                throw new IllegalArgumentException("Jugador no encontrado");
            });
            if (juego.jugando){
                var cartaFactory = new CartaFactory();
                jugador.mazo().cartas().forEach(cartaFactory::add);
                juego.ronda.jugadores().remove(jugador.identity());
                juego.agregarCartasMazoPrincipal(cartaFactory);
            }
            juego.jugadores.remove(jugador);
        });

        apply((EmpateTecnicoCreado event)->{
            var cartaFactory = new CartaFactory();
            juego.cartasTemporales.forEach(cartaFactory::add);
            juego.cartasTemporales.clear();
            juego.agregarCartasMazoPrincipal(cartaFactory);
        });

    }


}
