package db;

import java.util.Objects;

public final class Opinion {
    public final String sourceId;
    public final float opinion;
    public final float trust;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Opinion opinion1)) return false;

        return Float.compare(opinion, opinion1.opinion) == 0 && Float.compare(trust, opinion1.trust) == 0 && Objects.equals(sourceId, opinion1.sourceId);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(sourceId);
        result = 31 * result + Float.hashCode(opinion);
        result = 31 * result + Float.hashCode(trust);
        return result;
    }

    public Opinion(String sourceId, float opinion, float reputation) {
        this.sourceId = sourceId;
        this.opinion = opinion;
        this.trust = reputation;
    }
}
