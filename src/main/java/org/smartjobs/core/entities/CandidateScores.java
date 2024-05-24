package org.smartjobs.core.entities;

import java.util.List;
import java.util.Objects;

public final class CandidateScores {
    private final long id;
    private final String name;
    private final List<ScoredCriteria> scored;
    private Double percentage = null;


    public CandidateScores(long id, String name, List<ScoredCriteria> scored) {
        this.id = id;
        this.name = name;
        this.scored = scored;
    }

    public double percentage() {
        if (percentage == null) {
            double score = scored.stream().mapToDouble(ScoredCriteria::score).sum();
            int max = scored.stream().mapToInt(ScoredCriteria::maxScore).sum();
            percentage = (score / max) * 100;
        }
        return percentage;
    }

    public long id() {
        return id;
    }

    public String name() {
        return name;
    }

    public List<ScoredCriteria> scoredCriteria() {
        return scored;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (CandidateScores) obj;
        return this.id == that.id &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.scored, that.scored);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, scored);
    }

    @Override
    public String toString() {
        return "CandidateScores[" +
                "id=" + id + ", " +
                "name=" + name + ", " +
                "scoringCriteria=" + scored + ']';
    }

}
