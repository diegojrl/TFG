package authorization.configuration;

import java.util.List;

public class Policy {
    public String topic;
    public List<Rule> rules;

    @Override
    public String toString() {
        return topic + '\n' + rules;
    }
}
