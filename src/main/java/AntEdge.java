public class AntEdge {

    private double pathLength;
    private double amountOfPheromones;

    public AntEdge() {
        this.pathLength = 0;
        this.amountOfPheromones = 0;
    }

    public AntEdge(AntEdge antEdge){
        this.pathLength = antEdge.getPathLength();
        this.amountOfPheromones = antEdge.getAmountOfPheromones();
    }

    public AntEdge(double pathLength, double amountOfPheromones) {
        this.pathLength = pathLength;
        this.amountOfPheromones = amountOfPheromones;
    }

    public double getPathLength() {
        return pathLength;
    }

    public void setPathLength(double pathLength) {
        this.pathLength = pathLength;
    }

    public double getAmountOfPheromones() {
        return amountOfPheromones;
    }

    public void setAmountOfPheromones(double amountOfPheromones) {
        this.amountOfPheromones = amountOfPheromones;
    }

    public void addPheromones(double amountOfPheromones){
        this.amountOfPheromones += amountOfPheromones;
    }

    public void multiplyPheromones(double amountOfPheromones){
        this.amountOfPheromones *= amountOfPheromones;
    }
}
