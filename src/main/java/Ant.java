import java.util.ArrayList;

public class Ant {
    private ArrayList<Integer> tabuList;
    private ArrayList<Double> allEdgesValues;
    private double sumOfPathLength;
    private double recentPathLength;
    private int curVertex;

    public Ant(Integer startPosition) {
       initAnt(startPosition);
    }

    public void addTabuEdge(AntEdge path, Integer edge){
        if (!tabuList.contains(edge)) {
            tabuList.add(edge);
            sumOfPathLength += path.getPathLength();
            recentPathLength = path.getPathLength();
            allEdgesValues.add(recentPathLength);
            curVertex = edge;
        }
    }

    public int getLastTabuListElem(){
        return tabuList.get(tabuList.size() - 1);
    }

    public void addDistanceToSum(double distance){
        sumOfPathLength += distance;
        allEdgesValues.add(distance);
    }

    public void initAnt(Integer startPos){
        tabuList = new ArrayList<>();
        allEdgesValues = new ArrayList<>();
        tabuList.add(startPos);
        sumOfPathLength = 0.0;
        curVertex = startPos;
    }

    public void deleteTabuEdge(AntEdge path, Integer edge){
        if (tabuList.contains(edge))
            sumOfPathLength -= path.getPathLength();
        tabuList.remove(edge);
    }

    public int getCurVertex() {
        return curVertex;
    }

    public ArrayList<Integer> getTabuList() {
        return tabuList;
    }

    public double getSumOfPathLength() {
        return sumOfPathLength;
    }

    public void clear(){
        sumOfPathLength = 0.0;
        tabuList.clear();
        tabuList = new ArrayList<>();
        allEdgesValues.clear();
        recentPathLength = 0;
        curVertex = -1;
    }

    public double getRecentPathLength() {
        return recentPathLength;
    }

    /*public void setRecentPathLength(double recentPathLength) {
        this.recentPathLength = recentPathLength;
    }*/

    public ArrayList<Double> getAllEdgesValues() {
        return allEdgesValues;
    }

    public boolean isEnd(int amountOfVertexes){
        return  tabuList.size() == amountOfVertexes;
    }

    public String getPath(ArrayList<Vertex> vertexes){
        if (tabuList.isEmpty())
            return "НИ ОДНОГО УЗЛА НЕ ПОСЕЩЕНО";
        if (tabuList.size() == 1)
            return "НЕ БЫЛО СОВЕРШЕННО ПУТЕШЕСТВИЙ.\nНачальный узел: " + vertexes.get(tabuList.get(0));
        StringBuilder path = new StringBuilder();
        for (int i = 0; i < tabuList.size(); i++){
            path.append(vertexes.get(tabuList.get(i)).getName()).append(" -> ");
        }
        path.append( vertexes.get(tabuList.get(0)).getName());
        return path.toString();
    }

    public static String getPathByTabu(ArrayList<Vertex> vertexes, ArrayList<Integer> tabuList){
        if (tabuList.isEmpty())
            return "НИ ОДНОГО УЗЛА НЕ ПОСЕЩЕНО";
        if (tabuList.size() == 1)
            return "НЕ БЫЛО СОВЕРШЕННО ПУТЕШЕСТВИЙ.\nНачальный узел: " + vertexes.get(tabuList.get(0));
        StringBuilder path = new StringBuilder();
        for (int i = 0; i < tabuList.size(); i++){
            path.append(vertexes.get(tabuList.get(i)).getName()).append(" -> ");
        }
        path.append( vertexes.get(tabuList.get(0)).getName());
        return path.toString();
    }

    public double getPathLength(AntEdge[][] edges){
        if (tabuList.size() <= 1)
            return 0;
        else{
            double sum = 0;
            for (int i = 0; i < tabuList.size()-1; i++) {
                sum += edges[tabuList.get(i)][tabuList.get(i+1)].getPathLength();
            }
            return sum;
        }
    }

}
