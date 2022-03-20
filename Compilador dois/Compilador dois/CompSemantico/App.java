public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Segundo compilador");


        SintaticoProgramaV2 S = new SintaticoProgramaV2("inputV2.txt");
        
        //S.Analisador();
        Executor exe = new Executor();

        exe.executor("output.txt");

        

   
        
    }

    

    
}
