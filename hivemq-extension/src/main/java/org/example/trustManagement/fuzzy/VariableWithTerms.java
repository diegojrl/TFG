package org.example.trustManagement.fuzzy;

import fuzzy4j.flc.Term;
import fuzzy4j.flc.Variable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class VariableWithTerms {
    private final Variable variable;
    private final List<Term> terms;

    public VariableWithTerms(final Variable variable, Term... terms) {
        this(variable, Arrays.asList(terms));
    }

    public VariableWithTerms(final Variable variable, List<Term> terms) {
        this.variable = variable;
        this.terms = terms;
    }

    public Variable getVariable() {
        return variable;
    }

    public Term getTermByName(final String name) {
        Term result = null;
        for (Term term : terms) {
            if (term.name.equals(name)) {
                result = term;
                break;
            }
        }
        return result;
    }

    public String getVariableName() {
        return variable.toString();
    }

    @Override
    public String toString() {
        return variable.toString() + " " + terms.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        VariableWithTerms that = (VariableWithTerms) o;
        return variable.equals(that.variable);
    }

    @Override
    public int hashCode() {
        return variable.hashCode();
    }
}
