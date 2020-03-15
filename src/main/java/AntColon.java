import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class AntColon {

    public static final int SCALE = 13;
    public static double SCALE_NUM = 1;
    public static double EPS;
    public static final int MAX_VERTEXES = 27;
    public static final int MAX_ANTS = 30;
    public static final int MAX_DISTANCE = 200;
    public static double MAX_TOURS;
    public static double MAX_TIME;
    public static double best;
    public static int bestIndex;
    public static ArrayList<Double> allEdgesValues;
    public static boolean isBestChanged;
    public static String bestPath;

    public static final double ALPHA = 1.0;
    public static final double BETA	= 5.0;
    public static final double RHO = 0.5;	/* Интенсивность / Испарение */
    public static final double Q = 100;
    public static double INIT_PHEROMONE;

    private ArrayList<Ant> ants;
    private ArrayList<Vertex> vertexes;
    private AntEdge[][] edges;
    private String names[] = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
            "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    public AntColon(int amountOfVertex, int amountOfAnts) {
        SCALE_NUM = 1;
        for (int i = 0; i < SCALE; i++)
            SCALE_NUM *= 10;

        EPS = (1.0 / SCALE_NUM) * 10;
        isBestChanged = false;
        amountOfVertex =  Math.min(amountOfVertex, MAX_VERTEXES);
        amountOfAnts = Math.min(amountOfAnts, MAX_ANTS);

        MAX_TOURS = MAX_ANTS * MAX_DISTANCE;
        best = MAX_TOURS;
        MAX_TIME = MAX_VERTEXES * MAX_TOURS;
        edges = new AntEdge[amountOfVertex][amountOfVertex];
        vertexes = new ArrayList<>();
        for (int i = 0; i < amountOfVertex; i++)
            vertexes.add(new Vertex((int)(Math.random() * MAX_DISTANCE), (int)(Math.random() * MAX_DISTANCE), names[i]));
        INIT_PHEROMONE = (1.0 / vertexes.size());
        for (int from = 0; from < amountOfVertex; from++) {
            for (int to = 0; to < amountOfVertex; to++) {
                if (from == to)
                    edges[from][to] = null;
                else
                    if ( edges[from][to] == null || edges[from][to].getPathLength() == 0){
                        int xd = Math.abs(vertexes.get(from).getX() - vertexes.get(to).getX());
                        int yd = Math.abs(vertexes.get(from).getY() - vertexes.get(to).getY());
                        edges[from][to] = new AntEdge(BigDecimal.valueOf(Math.round(Math.pow(xd * xd + yd * yd, (1.0 / 2.0)) * SCALE_NUM) / SCALE_NUM).setScale(SCALE, RoundingMode.HALF_UP).doubleValue(), INIT_PHEROMONE);
                        edges[to][from] = new AntEdge(edges[from][to]);
                    }
            }
        }
        ants = new ArrayList<>();
        int vertexIndex = 0;
        for (int i = 0; i < amountOfAnts; i++){
            if (vertexIndex >= vertexes.size()) vertexIndex = 0;
            ants.add(new Ant(vertexIndex++));
        }
        showVertexesMatrix();
        //showVertexesWithoutRepeat();
    }

    public int simulateAnts(){
        int moving = 0;
        for  (int antNum = 0; antNum < ants.size(); antNum++) {

            /* Убедиться, что муравью есть куда идти */
            if (ants.get(antNum).getTabuList().size() < vertexes.size()) {
                int nextVertex = selectNextCity(antNum);
                ants.get(antNum).addTabuEdge(edges[ants.get(antNum).getCurVertex()][nextVertex], nextVertex);

                /* Обработка окончания путешествия (из последнего города в первый */
                if (ants.get(antNum).getTabuList().size() == vertexes.size()) {
                    ants.get(antNum).addDistanceToSum(edges[ants.get(antNum).getLastTabuListElem()][ants.get(antNum).getTabuList().get(0)].getPathLength());
                }
                moving++;
            }
        }

        return moving;
    }


    public void restartAnts(){

        int to = 0;
        for (int antNum = 0; antNum < ants.size(); antNum++) {

            if (ants.get(antNum).getSumOfPathLength() < best) {
                best = ants.get(antNum).getSumOfPathLength();
                bestIndex = antNum;
                isBestChanged = true;
                bestPath = ants.get(antNum).getPath(vertexes);
                allEdgesValues = new ArrayList<>(ants.get(antNum).getAllEdgesValues());
            }

            ants.get(antNum).clear();
            ants.get(antNum).initAnt(to++);
            if (to >= vertexes.size())
                to = 0;
        }
    }


    public double antProduct( int from, int to ){
        /*System.out.println("EDGE_PHE = " + edges[from][to].getAmountOfPheromones());
        System.out.println("1/PATH = " + (1.0 / edges[from][to].getPathLength()) );*/
        //System.out.println("PRODUCT = " + product);
        return Math.pow( edges[from][to].getAmountOfPheromones(), ALPHA ) *
                Math.pow( (1.0 / edges[from][to].getPathLength()), BETA );
    }


    public int selectNextCity( int ant ){
        int from, to;
        double denom = 0.0;

        /* Выбрать следующий город */
        from = ants.get(ant).getCurVertex();

        /* Расчет знаменателя */
        for (to = 0 ; to < vertexes.size(); to++) {
            //System.out.println("FROM = " + from + " | TO = " + to + "\n");
            if (!ants.get(ant).getTabuList().contains(to)) {
                denom += antProduct(from, to);
            }
        }

        assert(denom != 0.0) : "ОШИБКА С ВЫДЕЛЕНИЕМ ФЕРОМОНОВ";
        if (denom == 0.0)
            throw new RuntimeException("DENOM MUST BE LARGER THAN 0.0");
        do {
            double p;

            to++;
            if (to >= vertexes.size())
                to = 0;

            if (!ants.get(ant).getTabuList().contains(to)) {
                //System.out.println("DENOM = " + denom);
                p = antProduct(from, to)/denom;
                //System.out.println("P = " + p);
                //Симулируем вероятность попадения, чем больше будет число P, тем больше будет шанс, что муравей пойдёт сюда,
                // а соответственно и больше феромонов будет на дороге. Такая цепочка должна выявить самый короткий путь
                if (Math.random() < p ) break;
            }
        } while (true);
        return to;
    }

    public void updateTrails(){
        int from, to;
        /* Испарение фермента */
        for (from = 0 ; from < vertexes.size() ; from++) {
            for (to = 0 ; to < vertexes.size() ; to++) {
                if (to != from) {
                    edges[from][to].multiplyPheromones( (1.0 - RHO) );
                    if (edges[from][to].getAmountOfPheromones() <= EPS /*(INIT_PHEROMONE/4)*/)
                        edges[from][to].setAmountOfPheromones(INIT_PHEROMONE/*/4*/);
                }
            }
        }

        /* Нанесение нового фермента */
        /* Для пути каждого муравья */
        for (Ant ant : ants) {
            /* Обновляем каждый шаг пути */
            for (int i = 0 ; i < vertexes.size() ; i++) {

                if (i < vertexes.size()-1) {
                    from = ant.getTabuList().get(i);
                    to = ant.getTabuList().get(i+1);
                } else {
                    from = ant.getTabuList().get(i);
                    to = ant.getTabuList().get(0);
                }

                //формула 2.2 из методички Q/L(t)
                edges[from][to].addPheromones((Q / ant.getSumOfPathLength()));
                edges[to][from].setAmountOfPheromones(edges[from][to].getAmountOfPheromones());

            }
        }
        Arrays.stream(edges).flatMap(Arrays::stream).filter(Objects::nonNull).forEachOrdered(edge -> edge.multiplyPheromones(RHO));
        /*for (from = 0 ; from < vertexes.size(); from++) {
            for (to = 0 ; to < vertexes.size(); to++) {
                //if (to > from)
                if (edges[from][to] != null)
                    edges[from][to].multiplyPheromones(RHO);
            }
        }*/
    }

    private class BrutalBestInside{
        public Double sum;
        public ArrayList<Integer> tabuList;

        public BrutalBestInside(Double sum, ArrayList<Integer> tabuList) {
            this.sum = sum;
            this.tabuList = tabuList;
        }
    }

    public double checkBESTByBrutForce(){
        double brutalBest = MAX_TOURS;
        ArrayList<Integer> bestTabuList = new ArrayList<>();
        ArrayList<Integer> tabuList = new ArrayList<>();
        /*ArrayList<Double> listOfEdges = new ArrayList<>();
        ArrayList<Double> bestListOfEdges = new ArrayList<>();
        ArrayList<BrutalBestInside> paths;*/
        for (int i = 0; i < vertexes.size(); i++) {
            tabuList.add(i);
            BrutalBestInside minPath = checkBESTRecursive(tabuList).stream().reduce((path1, path2)->path1.sum < path2.sum ? path1 : path2).get();
            if (minPath.sum < brutalBest){
                brutalBest = minPath.sum;
                bestTabuList = new ArrayList<>(minPath.tabuList);
            }
            tabuList.clear();
        }
        System.out.println("BRUTAL_PATH = " + Ant.getPathByTabu(vertexes, bestTabuList) + "\n");
        //double sumOfEdges = bestListOfEdges.stream().reduce(Double::sum).orElse(0.0); //Подсчёт суммы списка с гранями с помощью Java Stream API
        /*double sumOfEdges = 0;
        for (Double d : bestListOfEdges){
            //System.out.println(String.format("BRUTAL_Knot = %."+ AntColon.SCALE + "f",  d));
            sumOfEdges += d;
        }
        System.out.println(String.format("BRUTAL_REPEAT_SUM = %."+ AntColon.SCALE + "f", sumOfEdges));*/
        return brutalBest;
    }

    private ArrayList<BrutalBestInside> checkBESTRecursive(ArrayList<Integer> tabuList/*, ArrayList<Double> listOfEdges*/){
        ArrayList<Double> sum = new ArrayList<>();
        ArrayList<ArrayList<Integer>> listOfTabuList = new ArrayList<>();
        if (tabuList.size() == vertexes.size()) {
            sum.add(edges[tabuList.get(tabuList.size()-1)][tabuList.get(0)].getPathLength());
            listOfTabuList.add(tabuList);
        }else {
            for (int j = 0; j < vertexes.size(); j++) {
                if (!tabuList.contains(j)) {
                    double localSum = 0.0;
                    ArrayList<Integer> localTL = new ArrayList<>(tabuList);
                    localSum += edges[localTL.get(localTL.size() - 1)][j].getPathLength();
                    localTL.add(j);
                    ArrayList<BrutalBestInside> tt = checkBESTRecursive(localTL);
                    for (BrutalBestInside brutalBestInside : tt) {
                        sum.add(localSum + brutalBestInside.sum);
                        listOfTabuList.add(brutalBestInside.tabuList);
                    }
                }
            }
        }
        ArrayList<BrutalBestInside> bBPaths = new ArrayList<>();
        for (int i = 0; i < sum.size(); i++)
            bBPaths.add(new BrutalBestInside(sum.get(i), listOfTabuList.get(i)));
        return bBPaths;
    }

    public void showVertexesWithoutRepeat() {
        //System.out.println("ВЕРШИНЫ: ");
        for (int i = 0; i < vertexes.size(); i++){
            System.out.println(vertexes.get(i) + "\n------------------------------------------------------------------");
            for (int j = 0; j < vertexes.size(); j++)
                if (j > i)
                    System.out.println(vertexes.get(j) + "\t\t\t| Длина пути: " + edges[i][j].getPathLength());
            System.out.println("\n\n");
        }
    }

    public void showVertexesMatrix() {
        int maxLength = 3;
        for (int i = 0; i < vertexes.size(); i++)
            for (int j = 0; j < vertexes.size(); j++)
                if (edges[i][j] != null && Double.toString(edges[i][j].getPathLength()).length() > maxLength)
                    maxLength = String.format("%."+ SCALE + "f",edges[i][j].getPathLength()).length();
        maxLength++;
        System.out.println("МАТРИЦА СМЕЖНОСТИ\n");
        StringBuilder titleRow = new StringBuilder("  |");
        StringBuilder subTitleRow = new StringBuilder("\n__|");
        for (Vertex v : vertexes){
            int localLength = maxLength - v.getName().length();
            int localLengthL = localLength/2;
            int localLengthR = (localLength - localLengthL);
            for (int k = 0; k < localLengthL+1; k++)
                titleRow.append(" ");
            titleRow.append(String.format("%s", v.getName()));
            for (int k = 0; k < localLengthR; k++)
                titleRow.append(" ");
            titleRow.append("|");
            for (int k = 0; k < maxLength+1; k++)
                subTitleRow.append("_");
            subTitleRow.append("|");
        }
        titleRow.append(subTitleRow);
        System.out.println(titleRow);
        for (int i = 0; i < vertexes.size(); i++){
            titleRow = new StringBuilder(vertexes.get(i).getName() + " |");
            subTitleRow = new StringBuilder("__|");
            for (int j = 0; j < vertexes.size(); j++) {
                String value = "";
                if (j != i)
                    value = String.format("%."+ SCALE + "f",edges[i][j].getPathLength());
                else
                    value = "-";
                int localLength = maxLength - value.length();
                int localLengthL = localLength/2;
                int localLengthR = (localLength - localLengthL);
                for (int k = 0; k < localLengthL; k++)
                    titleRow.append(" ");
                titleRow.append(" ").append(value);
                for (int k = 0; k < localLengthR; k++)
                    titleRow.append(" ");
                titleRow.append("|");
                for (int k = 0; k < maxLength+1; k++)
                    subTitleRow.append("_");
                subTitleRow.append("|");
            }
            titleRow.append("\n").append(subTitleRow);
            System.out.println(titleRow);
        }
        System.out.println("\n");
    }

    public void emitDataFile(int ant){
        FileWriter fW = null;
        //FileOutputStream fOS;
        try {
            //fOS = new FileOutputStream(file);
            fW = new FileWriter(new File("vertexes.dat"));
        }catch (IOException ex){
            ex.printStackTrace();
            return;
        }finally {
            try {
                if (fW != null)
                    fW.close();
            }catch (IOException IOex){
                IOex.printStackTrace();
                return;
            }
        }

        for (Vertex v : vertexes) {
            try {
                fW.write(String.format("%d %d\n", v.getX(), v.getY()));
            }catch (IOException exIO){
                exIO.printStackTrace();
            }finally {
                try {
                    fW.close();
                }catch (IOException exIO){
                    exIO.printStackTrace();
                    return;
                }
            }
        }

        try {
            fW = new FileWriter(new File("solution.dat"));
        }catch (IOException exIO){
            exIO.printStackTrace();
        }finally {
            try {
                fW.close();
            }catch (IOException exIO){
                exIO.printStackTrace();
                return;
            }
        }

        try {
            for (int vertexI = 0; vertexI < vertexes.size(); vertexI++) {
                fW.write(String.format("%d %d\n",
                        vertexes.get(ants.get(ant).getTabuList().get(vertexI)).getX(),
                        vertexes.get(ants.get(ant).getTabuList().get(vertexI)).getY())
                );
            }
            fW.write(String.format("%d %d\n",
                    vertexes.get(ants.get(ant).getTabuList().get(0)).getX(),
                    vertexes.get(ants.get(ant).getTabuList().get(0)).getY())
            );
            fW.close();
        }catch (IOException IOex){
            IOex.printStackTrace();
        }
    }
    /*
    public void emitTable() {
        int from, to;

        for (from = 0 ; from < vertexes.size() ; from++) {
            for (to = 0 ; to < vertexes.size() ; to++) {
                printf("%5.2g ", pheromone[from][to]);
            }
            printf("\n");
        }
        printf("\n");
    }
*/
}
