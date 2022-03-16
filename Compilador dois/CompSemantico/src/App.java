public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("primeiro compilador");


        SintaticoProgramaV2 S = new SintaticoProgramaV2("inputV2.txt");
        //SintaticoPrograma S = new SintaticoPrograma("input.txt");
        S.Analisador();
    }
}
