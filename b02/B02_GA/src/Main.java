/**
 * Simple Genetic Algorithm implementation for two toy problems:
 * - Map coloring (6 regions, 5 colors)
 * - 8-queens
 */

import java.util.*;

public class Main {


    public static final long RNG_SEED = 42;//not sooo random to reconstruate with different parameters //System.nanoTime(); Seed for reproducibility, random

    public static void main(String[] args) {
        Random rng = new Random(RNG_SEED);

        // GA parameters
        int populationSize = 1000;//200;
        int maxGenerations = 1000;//200;
        double crossoverRate = 0.9;//0.8;
        double mutationRate = 0.1;//0.01; // per-gene mutation
        int tournamentSize = 10;//3;

        // Experiment settings
        int runsPerSetting = 100; // do at least 100 runs as requested

        // Problems
        Problem mapProblem = new MapColoringProblem(rng);
        Problem queensProblem = new EightQueensProblem(rng);

        System.out.println("=== GA experiments: Map coloring ===");
        GeneticAlgorithm ga1 = new GeneticAlgorithm(mapProblem, populationSize, maxGenerations,
                crossoverRate, mutationRate, tournamentSize, rng);
        ga1.runMany(runsPerSetting);

        System.out.println("\n=== GA experiments: 8-Queens ===");
        GeneticAlgorithm ga2 = new GeneticAlgorithm(queensProblem, populationSize, maxGenerations,
                crossoverRate, mutationRate, tournamentSize, rng);
        ga2.runMany(runsPerSetting);

        System.out.println("\nDone. Edit Main.main() to change parameters or operators.");
    }

    /**************************************************************************
     * Generic GA implementation (keeps code short and explicit)
     **************************************************************************/
    public static class GeneticAlgorithm {
        private final Problem problem;
        private final int popSize;
        private final int maxGens;
        private final double cxRate;
        private final double mutRate;
        private final int tourSize;
        private final Random rng;

        public GeneticAlgorithm(Problem problem, int popSize, int maxGens,
                                double cxRate, double mutRate, int tourSize, Random rng) {
            this.problem = problem;
            this.popSize = popSize;
            this.maxGens = maxGens;
            this.cxRate = cxRate;
            this.mutRate = mutRate;
            this.tourSize = tourSize;
            this.rng = rng;
        }

        /** Run GA once and return results. */
        public RunResult runOnce() {
            Individual[] population = new Individual[popSize];
            for (int i = 0; i < popSize; i++) population[i] = problem.randomIndividual();

            Individual best = null;
            int generation = 0;

            while (generation < maxGens) {
                // evaluate & find best
                for (Individual ind : population) ind.fitness = problem.fitness(ind);
                Arrays.sort(population, Comparator.comparingDouble((Individual a) -> a.fitness).reversed());
                if (best == null || population[0].fitness > best.fitness) best = population[0].copy();

                if (problem.isOptimal(best)) {
                    return new RunResult(true, generation, best);
                }

                // create new population
                Individual[] next = new Individual[popSize];
                int idx = 0;
                while (idx < popSize) {
                    // selection: tournament
                    Individual parent1 = tournament(population);
                    Individual parent2 = tournament(population);

                    Individual child1 = parent1.copy();
                    Individual child2 = parent2.copy();

                    // crossover
                    if (rng.nextDouble() < cxRate) {
                        Individual[] off = problem.crossover(parent1, parent2);
                        child1 = off[0];
                        child2 = off[1];
                    }

                    // mutation
                    problem.mutate(child1, mutRate);
                    problem.mutate(child2, mutRate);

                    next[idx++] = child1;
                    if (idx < popSize) next[idx++] = child2;
                }

                population = next;
                generation++;
            }

            // reached max generations
            // final evaluation
            for (Individual ind : population) ind.fitness = problem.fitness(ind);
            Arrays.sort(population, Comparator.comparingDouble((Individual a) -> a.fitness).reversed());
            if (best == null || population[0].fitness > best.fitness) best = population[0].copy();
            return new RunResult(problem.isOptimal(best), maxGens, best);
        }

        /** Run many experiments and print concise statistics. */
        public void runMany(int runs) {
            int successes = 0;
            int gensSum = 0;
            double bestFitnessOverall = Double.NEGATIVE_INFINITY;
            double[] bestFitnesses = new double[runs];

            for (int i = 0; i < runs; i++) {
                // re-seed per run to keep reproducibility but different runs: use rng.nextLong()
                long runSeed = rng.nextLong();
                Random runRng = new Random(runSeed);
                GeneticAlgorithm ga = new GeneticAlgorithm(problem, popSize, maxGens, cxRate, mutRate, tourSize, runRng);
                RunResult res = ga.runOnce();
                if (res.success) successes++;
                gensSum += res.generations;
                bestFitnesses[i] = res.best.fitness;
                if (res.best.fitness > bestFitnessOverall) bestFitnessOverall = res.best.fitness;
            }

            double avgBest = Arrays.stream(bestFitnesses).average().orElse(Double.NaN);
            System.out.printf("Problem: %s\n", problem.name());
            System.out.printf("Runs: %d, Successes (found optimal): %d, Success rate: %.2f%%\n",
                    runs, successes, 100.0 * successes / runs);
            System.out.printf("Best fitness found: %.4f, Avg best fitness: %.4f\n",
                    bestFitnessOverall, avgBest);
        }

        private Individual tournament(Individual[] population) {
            Individual best = null;
            for (int i = 0; i < tourSize; i++) {
                Individual cand = population[rng.nextInt(population.length)];
                if (best == null || cand.fitness > best.fitness) best = cand;
            }
            return best;
        }
    }

    /**************************************************************************
     * Small utility classes and interfaces
     **************************************************************************/

    /** The interface a problem must implement to be used with the GA. */
    public interface Problem {
        /** Create a random individual following the problem encoding. */
        Individual randomIndividual();

        /** Evaluate and return fitness (higher is better). */
        double fitness(Individual ind);

        /** One-point or problem-specific crossover. Return two offspring. */
        Individual[] crossover(Individual a, Individual b);

        /** Mutate the individual in-place using the provided per-gene mutation probability. */
        void mutate(Individual ind, double perGeneRate);

        /** If this individual is considered an optimal solution. */
        boolean isOptimal(Individual ind);

        /** Human-readable name for printing. */
        String name();
    }

    /** Generic container for an encoded solution. Implementations store an int[] genotype. */
    public static class Individual {
        public int[] genes;
        public double fitness;

        public Individual(int length) { genes = new int[length]; }

        public Individual copy() {
            Individual c = new Individual(genes.length);
            System.arraycopy(this.genes, 0, c.genes, 0, genes.length);
            c.fitness = this.fitness;
            return c;
        }

        @Override
        public String toString() {
            return "Individual{" + "genes=" + Arrays.toString(genes) + ", fitness=" + fitness + '}';
        }
    }

    /** Simple container for run results. */
    public static class RunResult {
        public final boolean success;
        public final int generations;
        public final Individual best;

        public RunResult(boolean success, int generations, Individual best) {
            this.success = success;
            this.generations = generations;
            this.best = best;
        }
    }

    /**************************************************************************
     * Problem: Map coloring (6 regions named A..F, 5 colors)
     * Encoding: gene[0..5] each value in 0..4 stands for a color
     * Fitness: higher is better. We transform the user's desire (minimize conflicts and
     * minimize used colors) into a score that the GA can maximize.
     **************************************************************************/
    public static class MapColoringProblem implements Problem {
        private final Random rng;
        private final int regions = 6;
        private final int colors = 5; // available colors

        // adjacency list (index 0..5 correspond to A..F). Example graph — choose adjacency from task.
        // You should adapt this adjacency to match your exercise if different.
        private final int[][] adj = new int[][]{
                {},    // A adjacent to -
                {2, 3}, // B adjacent to C,D
                {1, 3, 4}, // C adjacent to B,D,E
                {1, 2, 4}, // D adjacent to B,C,E
                {2, 3, 5}, // E adjacent to C,D,F
                {4}     // F adjacent to E
        };

        public MapColoringProblem(Random rng) { this.rng = rng; }

        @Override
        public Individual randomIndividual() {
            Individual ind = new Individual(regions);
            for (int i = 0; i < regions; i++) ind.genes[i] = rng.nextInt(colors);
            return ind;
        }

        @Override
        public double fitness(Individual ind) {
            // count conflicts
            int conflicts = 0;
            for (int i = 0; i < regions; i++) {
                for (int nb : adj[i]) {
                    if (nb > i && ind.genes[i] == ind.genes[nb]) conflicts++;
                }
            }
            // count distinct colors used
            boolean[] used = new boolean[colors];
            for (int g : ind.genes) used[g] = true;
            int usedCount = 0; for (boolean u : used) if (u) usedCount++;

            // convert to a maximization score. We give heavy penalty to conflicts (weight 10)
            // so that zero-conflict solutions are strongly preferred. Then within zero-conflict
            // solutions we prefer fewer colors.
            double score = 100 - (conflicts * 10) - (usedCount);
            ind.fitness = score;
            return score;
        }

        @Override
        public Individual[] crossover(Individual a, Individual b) {
            // one-point crossover
            int len = regions;
            int point = 1 + rng.nextInt(len - 1);
            Individual c1 = new Individual(len);
            Individual c2 = new Individual(len);
            for (int i = 0; i < len; i++) {
                if (i < point) { c1.genes[i] = a.genes[i]; c2.genes[i] = b.genes[i]; }
                else { c1.genes[i] = b.genes[i]; c2.genes[i] = a.genes[i]; }
            }
            return new Individual[]{c1, c2};
        }

        @Override
        public void mutate(Individual ind, double perGeneRate) {
            for (int i = 0; i < ind.genes.length; i++) {
                if (rng.nextDouble() < perGeneRate) {
                    // pick a different color
                    int old = ind.genes[i];
                    int attempt = rng.nextInt(colors);
                    if (attempt == old) attempt = (old + 1) % colors;
                    ind.genes[i] = attempt;
                }
            }
        }

        @Override
        public boolean isOptimal(Individual ind) {
            // optimal = zero conflicts (color count doesn't need to be globally minimal for acceptance)
            for (int i = 0; i < regions; i++) for (int nb : adj[i]) if (nb > i && ind.genes[i] == ind.genes[nb]) return false;
            return true;
        }

        @Override
        public String name() { return "MapColoring(6 regions, 5 colors)"; }
    }

    /**************************************************************************
     * Problem: 8-Queens
     * Encoding: genes[0..7] where genes[col] = row (0..7). This ensures exactly one queen per column.
     * Fitness: penalize attacking pairs. Higher score for fewer attacks.
     **************************************************************************/
    public static class EightQueensProblem implements Problem {
        private final Random rng;
        private final int n = 8;

        public EightQueensProblem(Random rng) { this.rng = rng; }

        @Override
        public Individual randomIndividual() {
            Individual ind = new Individual(n);
            for (int i = 0; i < n; i++) ind.genes[i] = rng.nextInt(n); // row
            return ind;
        }

        @Override
        public double fitness(Individual ind) {
            int attacks = 0;
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    if (ind.genes[i] == ind.genes[j]) attacks++; // same row
                    if (Math.abs(ind.genes[i] - ind.genes[j]) == Math.abs(i - j)) attacks++; // diagonal
                }
            }
            double score = 1 / (1 + attacks);
            ind.fitness = score;
            return score;
        }

        @Override
        public Individual[] crossover(Individual a, Individual b) {
            // small-point crossover: swap a single gene slice (1-point)
            int len = n;
            int point = 1 + rng.nextInt(len - 1);
            Individual c1 = new Individual(len);
            Individual c2 = new Individual(len);
            for (int i = 0; i < len; i++) {
                if (i < point) { c1.genes[i] = a.genes[i]; c2.genes[i] = b.genes[i]; }
                else { c1.genes[i] = b.genes[i]; c2.genes[i] = a.genes[i]; }
            }
            return new Individual[]{c1, c2};
        }

        @Override
        public void mutate(Individual ind, double perGeneRate) {
            // swap mutation: with probability perGeneRate per gene, swap this gene with another
            for (int i = 0; i < ind.genes.length; i++) {
                if (rng.nextDouble() < perGeneRate) {
                    int j = rng.nextInt(ind.genes.length);
                    int tmp = ind.genes[i]; ind.genes[i] = ind.genes[j]; ind.genes[j] = tmp;
                }
            }
        }

        @Override
        public boolean isOptimal(Individual ind) {
            // zero attacks
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    if (ind.genes[i] == ind.genes[j]) return false;
                    if (Math.abs(ind.genes[i] - ind.genes[j]) == Math.abs(i - j)) return false;
                }
            }
            return true;
        }

        @Override
        public String name() { return "8-Queens"; }
    }
}
