package org.example.trustManagement.fuzzyLogic;

import fuzzy4j.flc.IfThenRule;
import fuzzy4j.flc.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static fuzzy4j.flc.ExpressionFactory.is;

public final class RulesFile {
    private static final Logger log = LoggerFactory.getLogger(RulesFile.class);

    public static List<IfThenRule> readRules(final Path file, final Map<String, VariableWithTerms> variablesTerms) throws IOException {
        final List<String> lines = Files.readAllLines(file);
        final List<IfThenRule> rules = new ArrayList<>(lines.size());

        for (String line : lines) {
            String ln = line.trim();
            if (!ln.isBlank()) {
                rules.add(parseLine(ln, variablesTerms));
            }
        }

        return rules;
    }



    //Line format: IF (var) (value) THEN (var) (value)
    private static IfThenRule parseLine(final String line, final Map<String, VariableWithTerms> variablesTerms) throws IOException {
        try(final Scanner scanner = new Scanner(line)) {
            scanner.useDelimiter("[ ]");
            //Skip IF
            if (!scanner.next().equalsIgnoreCase("if"))
                return null;
            //Get terms for variable
            VariableWithTerms v1 = variablesTerms.get(scanner.next().toLowerCase());
            if (v1 == null)
                return null;

            Term term1 = v1.getTermByName(scanner.next());
            if (term1 == null)
                return null;

            //Skip THEN
            if (!scanner.next().equalsIgnoreCase("then"))
                return null;

            VariableWithTerms v2 = variablesTerms.get(scanner.next().toLowerCase());
            if (v2 == null)
                return null;

            Term term2 = v2.getTermByName(scanner.next());
            if (term2 == null)
                return null;

            return new IfThenRule(is(v1.getVariable(), term1), is(v2.getVariable(), term2));
        }catch (NoSuchElementException e) {
            log.error("Error reading rules file, line: {}  -  error: {}",line, e.getMessage());
            throw new IOException(e);
        }
    }


}
