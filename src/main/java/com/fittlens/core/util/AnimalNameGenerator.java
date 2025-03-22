package com.fittlens.core.util;

import org.springframework.stereotype.Component;
import java.util.Random;

@Component
public class AnimalNameGenerator {
    private static final String[] ANIMALS = {"Lion", "Tiger", "Bear", "Wolf", "Fox", "Eagle", "Hawk"};
    private static final Random random = new Random();

    public String generateName() {
        String animal = ANIMALS[random.nextInt(ANIMALS.length)];
        int number = random.nextInt(1000);
        return String.format("%s%d", animal, number);
    }
}