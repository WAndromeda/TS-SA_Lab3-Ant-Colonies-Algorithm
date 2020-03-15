public class Main {
    public static void main(String[] args) {
        int amountOfIter = 1;
        int [] success = new int[amountOfIter];
        for (int i = 0; i < amountOfIter; i++){
            success[i] = main2(null);
        }
        System.out.println("\n");
        for (int s: success){
            if (s == 1)
                System.out.println("МУРАВЬИНЫЙ СПРАВИЛСЯ");
            else
                System.out.println("ПРОВАЛ. НЕ СПРАВИЛСЯ") ;
        }
    }

    public static int main2(String[] args){
        menu();
        int curTime = 0;
        AntColon antColon = new AntColon(11, 30);
        //Один цикл времени будет равен amountOfVertex
        while (curTime++ < AntColon.MAX_TIME*2){
            if (antColon.simulateAnts() == 0){
                antColon.updateTrails();
                if (curTime != AntColon.MAX_TIME*2)
                    antColon.restartAnts();
                if (AntColon.isBestChanged)
                    System.out.println(String.format("ВРЕМЯ %d (%."+ AntColon.SCALE + "f)", curTime, AntColon.best));
                /*else
                    System.out.println(String.format("ВРЕМЯ %d (SAME AS PREVIOUS)", curTime));*/
                AntColon.isBestChanged = false;
            }
        }
        System.out.println(String.format("\n\nBEST = %."+ AntColon.SCALE + "f\n",  AntColon.best));
        System.out.println(String.format("BEST_INDEX = %d\n",  AntColon.bestIndex));
        System.out.println(String.format("BEST_PATH = %s\n",  AntColon.bestPath));
        /*for (Double d : AntColon.allEdgesValues){
            System.out.println(String.format("Knot = %."+ AntColon.SCALE + "f",  d));
        }*/
        System.out.println("\n");
        double brutForce = antColon.checkBESTByBrutForce();
        if (Math.abs(AntColon.best - brutForce) < AntColon.EPS ){
            System.out.println("УСПЕХ!!!");
            return 1;
        }else{
            System.out.println(String.format("ПРОВАЛ. ЗНАЧЕНИЕ РАВНО: %."+ AntColon.SCALE + "f", brutForce));
            return 0;
        }
    }

    private static void menu(){
        System.out.println("\n\nЛабораторная работа №3 | Алгоритм Муравьиной Колонии | Николаев Н.С. | ИКБО-13-17\n");
            /*cout << "1. Создать граф случайным образом" << endl;
            cout << "2. Ввести граф вручную" << endl;
            cout << "3. Вывести все вершины из которых можно попасть в любую вершину, проходя не более 100 км" << endl;
            cout << "0. Выход\n" << endl;
            cout << "\nВведите команду: ";*/
    }
}
