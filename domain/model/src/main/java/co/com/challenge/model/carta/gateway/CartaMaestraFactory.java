package co.com.challenge.model.carta.gateway;

import co.com.challenge.model.carta.CartaMaestra;
import co.com.challenge.model.juego.Carta;
import co.com.challenge.model.juego.CartaFactory;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class CartaMaestraFactory {

    public static CartaMaestraFactory instance;
    private final Set<CartaMaestra> cartas;

    public CartaMaestraFactory(){
        cartas = new HashSet<>();
    }

    public static CartaMaestraFactory getInstance(){
        if (Objects.isNull(instance)){
            instance = new CartaMaestraFactory();
            return instance;
        }
        return instance;
    }

    public CartaMaestraFactory add(CartaMaestra carta){
        cartas.add(carta);
        return this;
    }

    public Set<CartaMaestra> cartas() {
        return cartas;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CartaFactory{");
        sb.append("cartas=").append(cartas);
        sb.append('}');
        return sb.toString();
    }

}
