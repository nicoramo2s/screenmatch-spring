package com.aluracursos.screenmatch.principal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EjemploFindAnyParallelStream {
    public static void main(String[] args) {
        List<Integer> numeros = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            numeros.add(i);
        }

        // Utilizando parallelStream para encontrar un elemento cualquiera en paralelo
        Optional<Integer> numeroCualquiera = numeros.parallelStream()
                .filter(numero -> numero % 10 == 0) // Filtra los números que son múltiplos de 10
                .findAny();

        if (numeroCualquiera.isPresent()) {
            System.out.println("Encontrado: " + numeroCualquiera.get());
        } else {
            System.out.println("Ningún número encontrado.");
        }
    }
}

