package org.example.trustManagement.fuzzyLogic;

import fuzzy4j.flc.*;
import fuzzy4j.sets.TrapezoidalFunction;
import org.example.configuration.Configuration;
import org.example.trustData.DeviceTrustAttributes;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static fuzzy4j.flc.Term.term;
import static fuzzy4j.flc.Variable.input;
import static fuzzy4j.flc.Variable.output;

public class FuzzyCtr {
    public static final Map<String, VariableWithTerms> variables = new HashMap<>();
    private static FuzzyCtr instance;

    private static final VariableWithTerms security = createSecurity();
    private static final VariableWithTerms delay = createDelay();
    private static final VariableWithTerms failedPctr = createFailedPctr();
    private static final VariableWithTerms reputation = createReputation();
    private static final VariableWithTerms trust = createTrust();

    public static FuzzyCtr getInstance() throws IOException {
        if (instance == null) {
            instance = new FuzzyCtr(Path.of("/opt/hivemq/conf/trustRules.flc"));
        }
        return instance;
    }

    private final FLC controller;

    private FuzzyCtr(final Path file) throws IOException {

        List<IfThenRule> rules = RulesFile.readRules(file, variables);

        ControllerBuilder controller = ControllerBuilder.newBuilder();

        //Todo
        //controller.defuzzifier(new Maximum(Maximum.MinOfMax));
        controller.activationFunction(fuzzy4j.aggregation.Minimum.INSTANCE);
        controller.accumulationFunction(fuzzy4j.aggregation.ArithmeticMean.INSTANCE);

        for (var rule : rules) {
            controller.addRule(rule);
        }

        this.controller = controller.create();
    }


    public double evaluate(final DeviceTrustAttributes device) {
        final double latency = (device.getLatency() - Configuration.getDelayMin()) /
                (double) (Configuration.getDelayMax() - Configuration.getDelayMin());
        InputInstance input = new InputInstance()
                .is(delay.getVariable(), latency)
                .is(security.getVariable(), device.getSecurity())
                .is(reputation.getVariable(), device.getReputation())
                .is(failedPctr.getVariable(), device.getFailureRate());
        return controller.apply(input).get(trust.getVariable());
    }

    private static VariableWithTerms createSecurity() {
        Term sLow = term("low", new TrapezoidalFunction(0, 0, 0.3, 0.6));
        Term sHigh = term("high", new TrapezoidalFunction(0.5, 0.90, 2, 2));
        Variable security = input("security", sLow, sHigh);
        VariableWithTerms variable = new VariableWithTerms(security, sLow, sHigh);
        variables.put("security", variable);
        return variable;
    }

    private static VariableWithTerms createDelay() {

        Term sLow = term("low", new TrapezoidalFunction(0, 0, 0, 0.5));
        Term sMedium = term("medium", 0, 0.5, 1);
        Term sHigh = new Term("high", new TrapezoidalFunction(0.5, 1, 2, 2));
        Variable security = input("delay", sLow, sMedium, sHigh);
        VariableWithTerms variable = new VariableWithTerms(security, sLow, sMedium, sHigh);
        variables.put("delay", variable);
        return variable;
    }

    private static VariableWithTerms createFailedPctr() {
        Term sLow = term("low", new TrapezoidalFunction(0, 0, 0.05, 0.15));
        Term sMedium = term("medium", 0.1, 0.15, 0.25);
        Term sHigh = new Term("high", new TrapezoidalFunction(0.2, 0.3, 2, 2));
        Variable security = input("failed", sLow, sMedium, sHigh);
        VariableWithTerms variable = new VariableWithTerms(security, sLow, sMedium, sHigh);
        variables.put("failed", variable);
        return variable;
    }

    private static VariableWithTerms createReputation() {
        Term tLow = term("low", new TrapezoidalFunction(0, 0, 0.2, 0.3));
        Term tMedium = term("medium", new TrapezoidalFunction(0.2, 0.3, 0.7, 0.8));
        Term tHigh = term("high", new TrapezoidalFunction(0.7, 0.8, 2, 2));
        Variable trust = output("reputation", tLow, tMedium, tHigh);
        VariableWithTerms variable = new VariableWithTerms(trust, tLow, tMedium, tHigh);
        variables.put("reputation", new VariableWithTerms(trust, tLow, tMedium, tHigh));
        return variable;
    }

    private static VariableWithTerms createTrust() {
        Term tLow = term("low", new TrapezoidalFunction(0, 0, 0.3, 0.45));
        Term tMedium = term("medium", .3, .5, .7);
        Term tHigh = term("high", new TrapezoidalFunction(0.6, 0.90, 2, 2));
        Variable trust = output("trust", tLow, tMedium, tHigh);
        VariableWithTerms variable = new VariableWithTerms(trust, tLow, tMedium, tHigh);
        variables.put("trust", new VariableWithTerms(trust, tLow, tMedium, tHigh));
        return variable;
    }


}
