
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;

public class Executor {
  public void executor(String arquivo) throws IOException {

    // EXECUÇÃO

    ArrayList<Double> pilhaD = new ArrayList<Double>();
    //ArrayList<String> pilhaEndereco = new ArrayList<String>();
    Boolean escopo=true;
    int S = 0; // topo da pilha
    int index = 0; //indice da pilha
    Boolean op_logico=false;



    
    try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
      String line;
      while ((line = br.readLine()) != null) {

        String[] comando = line.split("[\\s+.]");
  
        

        

          
        if (comando[1].toString().equals("INPP")) {

          S=S-1;

          System.out.println(comando[0]+" INPP "+" S="+S+" sizeD= "+pilhaD.size());

        }

        







        if (comando[1].toString().equals("ALME")) {

          pilhaD.add(Double.parseDouble(comando[2])); 
          S=S+1;

          System.out.println(comando[0]+" ALME "+" S="+S+" sizeD= "+pilhaD.size());

        }


        if (comando[1].toString().equals("DSVI") && escopo) {
          index=Integer.parseInt(comando[2]);
          for(int i=Integer.parseInt(comando[0]);i<index-1;i++){
            line=br.readLine();
            //System.out.println(i+"<<i,line>>>"+line);

          }
          escopo=false;
          System.out.println(comando[0]+" DSVI "+index+" S="+S+" sizeD= "+pilhaD.size());
          
        }
        
        if (comando[1].toString().equals("DSVF")) {

          
          index=Integer.parseInt(comando[2]);

          Double temp=pilhaD.get((S));
          if(temp==0){

            index=Integer.parseInt(comando[2]);
           /* while ((line = br.readLine()) != comando[2]) {

              int index2 = Integer.valueOf(comando[2]);
              System.out.println("else linha: "+index2);
              
            }*/
            S=S-1;

            System.out.println(comando[0]+" DSVF "+" S="+S+" sizeD= "+pilhaD.size());
          }

        }

        if (comando[1].toString().equals("CRCT")) {

          
          pilhaD.add(Double.parseDouble(comando[2])); 
          S=S+1;
 
          System.out.println(comando[0]+" CRCT "+" S="+S+" sizeD= "+pilhaD.size());

        }

        if (comando[1].toString().equals("CLVR")) {

          Double temp=pilhaD.get(Integer.parseInt(comando[2]));
          pilhaD.add(temp);
          S=S+1;

          System.out.println(comando[0]+" CRVL "+" S="+S+" sizeD= "+pilhaD.size());

        }

        if (comando[1].toString().equals("SOMA")) {

          Double temp=pilhaD.get((S-1));
          Double temp2=pilhaD.get((S));
          pilhaD.remove((S-1));
          pilhaD.remove((S));
          pilhaD.add((temp+temp2));
          S=S-1;

          System.out.println(comando[0]+" SOMA "+" S="+S+" sizeD= "+pilhaD.size());

        }

        if (comando[1].toString().equals("SUBT")) {

          Double temp=pilhaD.get((S-1));
          Double temp2=pilhaD.get((S));
          pilhaD.remove((S-1));
          pilhaD.remove((S));
          pilhaD.add((temp-temp2));
          S=S-1;
          //System.out.println("SUBT="+temp+" "+temp2);
          System.out.println(comando[0]+" SUBT "+" S="+S+" sizeD= "+pilhaD.size());

        }

        if (comando[1].toString().equals("MULT")) {
          
          Double temp=pilhaD.get((S-1));
          Double temp2=pilhaD.get((S));
          pilhaD.remove((S-1));
          pilhaD.remove((S));
          pilhaD.add((temp*temp2));
          S=S-1;
          //System.out.println("MULT="+temp+" "+temp2);
          System.out.println(comando[0]+" MULT "+" S="+S+" sizeD= "+pilhaD.size());
        }

        if (comando[1].toString().equals("DIVI")) {

          Double temp=pilhaD.get((S-1));
          Double temp2=pilhaD.get((S));
          pilhaD.remove((S-1));
          pilhaD.remove((S));
          pilhaD.add((temp/temp2));
          S=S-1;
         // System.out.println("DIV="+temp+" "+temp2);
         System.out.println(comando[0]+" DIVI "+" S="+S+" sizeD= "+pilhaD.size());
        }

        if (comando[1].toString().equals("INVE")) {

          Double temp=pilhaD.get((S));
          pilhaD.remove((S));
          pilhaD.add((-temp));

          System.out.println(comando[0]+" INVE "+" S="+S+" sizeD= "+pilhaD.size());

        }

        if (comando[1].toString().equals("CPME")) {

          Double temp=pilhaD.get((S-1));
          Double temp2=pilhaD.get((S));
          if(temp<temp2){
            pilhaD.set((S-1),1.0);
            System.out.println("CPME true");
          }else{
            pilhaD.set((S-1),0.0);
            System.out.println("CPME false");
          }
          S=S-1;
    
          System.out.println(comando[0]+" CPME "+" S="+S+" sizeD= "+pilhaD.size());
        }
        if (comando[1].toString().equals("CPMA")) {

          Double temp=pilhaD.get((S-1));
          Double temp2=pilhaD.get((S));
          if(temp>temp2){
            pilhaD.set((S-1),1.0);
            System.out.println("CPMA true");
          }else{
            pilhaD.set((S-1),0.0);
            System.out.println(comando[0]+" CPMA false");
          }
          S=S-1;
         
          System.out.println("CPMA "+" S="+S+" sizeD= "+pilhaD.size());
        }

        if (comando[1].toString().equals("CPIG")) {

          Double temp=pilhaD.get((S-1));
          Double temp2=pilhaD.get((S));
          if(temp==temp2){
            pilhaD.set((S-1),1.0);
            System.out.println("CPIG true");
          }else{
            pilhaD.set((S-1),0.0);
            System.out.println("CPIG false");
          }
          S=S-1;
          System.out.println(comando[0]+" CPIG "+" S="+S+" sizeD= "+pilhaD.size());
        }
        if (comando[1].toString().equals("CDES")) {

          Double temp=pilhaD.get((S-1));
          Double temp2=pilhaD.get((S));
          if(temp!=temp2){
            pilhaD.set((S-1),1.0);
            System.out.println("CDES true");
          }else{
            pilhaD.set((S-1),0.0);
            System.out.println("CDES false");
          }
          S=S-1;
          System.out.println(comando[0]+" CDES "+" S="+S+" sizeD= "+pilhaD.size());
          

        }
        if (comando[1].toString().equals("CPMI")) {

          Double temp=pilhaD.get((S-1));
          Double temp2=pilhaD.get((S));
          if(temp<=temp2){
            pilhaD.set((S-1),1.0);
            System.out.println("CPMI true");
          }else{
            pilhaD.set((S-1),0.0);
            System.out.println("CPMI false");
          }
          S=S-1;
          System.out.println(comando[0]+" CPMI "+" S="+S+" sizeD= "+pilhaD.size());

        }
        if (comando[1].toString().equals("CMAI")) {

          Double temp=pilhaD.get((S-1));
          Double temp2=pilhaD.get((S));
          if(temp>=temp2){
            pilhaD.set((S-1),1.0);
            System.out.println("CMAI true");
          }else{
            pilhaD.set((S-1),0.0);
            System.out.println("CMAI false");
          }
          S=S-1;
          System.out.println(comando[0]+" CMAI "+" S="+S+" sizeD= "+pilhaD.size());
          
        }

        if (comando[1].toString().equals("ARMZ")) {

          index=Integer.parseInt(comando[2]);
      
          Double temp2=pilhaD.get((S));
          
          pilhaD.set(index,temp2);
          S=S-1;
          //System.out.println("ARMZ "+temp2);
          System.out.println(comando[0]+" ARMZ "+" S="+S+" sizeD= "+pilhaD.size());

        }

        if (comando[1].toString().equals("LEIT")) {

          Scanner keyboard = new Scanner(System.in);

          System.out.println("insira numero 5.5");
          //Double userinput =Double.parseDouble(keyboard.next());
          S=S+1;
          pilhaD.add(5.5); 
          keyboard.close();
          System.out.println(comando[0]+" LEIT "+" S="+S+" sizeD= "+pilhaD.size());
        }

        if (comando[1].toString().equals("IMPR")) {

          S=S-1;
          System.out.println(comando[0]+" IMPR "+" S="+S+" sizeD= "+pilhaD.size());
        }

        if (comando[1].toString().equals("PARAM")) {

          Double temp=pilhaD.get(Integer.parseInt(comando[2]));
          S=S+1;
          pilhaD.add(temp);

          //System.out.println("param "+temp);
          System.out.println(comando[0]+" PARAM "+" S="+S+" sizeD= "+pilhaD.size());

        }

        if (comando[1].toString().equals("PUSHER")) {

          index=Integer.parseInt(comando[2]);
          Double temp=pilhaD.get(Integer.parseInt(comando[2]));
          S=S+1;
          pilhaD.add(temp);

          System.out.println("pusher "+index);
          System.out.println(comando[0]+" PUSHER "+" S="+S+" sizeD= "+pilhaD.size());

        }

        if (comando[1].toString().equals("CHPR")) {

          index=Integer.parseInt(comando[2]);
          //Double temp=pilhaD.get(Integer.parseInt(comando[2]));
          
          index=Integer.parseInt(comando[2]);  

          System.out.println(comando[0]+" CHPR "+index);

        }

        if (comando[1].toString().equals("DESM")) {

          int temp=Integer.parseInt(comando[2]);  
          
          S=S-temp;

          System.out.println(comando[0]+" DESM "+" S="+S+" sizeD= "+pilhaD.size());

        }

       /* if (comando[1].toString().equals("RTPR")) {

          int temp=Integer.parseInt(comando[2]);  
          
          index=pilhaD.get(S).intValue();
          S=S-temp;

          System.out.println("");

        }*/







        

       









        /*
         * System.out.println(comando[1]+" linha 2");
         * try {
         * System.out.println(comando[2]+" linha 3");
         * } catch (Exception e) {
         * //TODO: handle exception
         * }
         */

      }

      br.close();

    }

    

  }

}
