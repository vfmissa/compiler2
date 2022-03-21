public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Segundo compilador - codigoHIP e Executor , by Victor Missa RGA 201611310053");


        SintaticoProgramaV2 S = new SintaticoProgramaV2("correto.lalg-v2.txt");
        
        S.Analisador();  //vai pega o codigo e tranforma em codigohip, armazena em output.txt


        Executor exe = new Executor();//classe que executa o codigohip
        exe.executor("output.txt");

        

   
        
    }

    

    
}
