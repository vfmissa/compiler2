import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Lexico {

  private char[] programa;
  private int estado;
  private int posicao_texto;

  public Lexico(String arquivo) {
    try {
      byte[] bDados = Files.readAllBytes(Paths.get(arquivo));
      String dados = new String(bDados);
      programa = dados.toCharArray();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private boolean isLetra(char c) {
    return ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'));
  }

  private boolean isDigito(char c) {
    return ((c >= '0' && c <= '9'));
  }

  private boolean isEspaco(char c) {
    return (c == ' ' || c == '\n' || c == '\t'|| c== '\r');
  }

  private boolean isExpression(char c){
    return (   c==':'|| c=='='  ||  c=='>'|| c=='<'|| c=='*'|| c=='/' || c=='+'|| c=='-');
  }

  private boolean isEOF() {
    return posicao_texto >= programa.length;
  }

  private char proxChar() {
    if (isEOF()) {
      return 0;
    }
    return programa[posicao_texto++];
  }

  private void Retorno() {
    posicao_texto--;
  }

  public Token proxToken() {
    if (isEOF()) {
      return null;
    }

    estado = 0;
    char c;
    String tipo = "";
    while (true) {
      if (isEOF()) {
        posicao_texto = programa.length + 1;
      }
      c = proxChar();
        switch (estado) {
        case 0:
          /*if(c=='{'){
            estado=5;
          }
          else */if (isEspaco(c)) {
            estado = 0;
          } else if (isDigito(c)) {
            estado = 1;
            tipo += c;
          } else if (isLetra(c)) {
            estado = 3;
            tipo += c;
          }else if(isExpression(c)){
            estado=4;
            tipo+=c;
          } else {
            if (c == 0) {
              return null;
            }
            tipo += c;
            return new Token(Token.SIMBOLO, tipo);
          }
          break;
        case 1:
          if (isDigito(c)) {
            estado = 1;
            tipo += c;
          } else if (c == '.') {
            estado = 2;
            tipo += c;
          } else {
            Retorno();
            return new Token(Token.INTEIRO, tipo);
          }
          break;
        case 2:
          if (isDigito(c)) {
            estado = 2;
            tipo += c;
          } else {
            Retorno();
            return new Token(Token.REAL, tipo);
          }
          break;
        case 3:
          if (isLetra(c) || isDigito(c)) {
            estado = 3;
            tipo += c;
          } else {
            Retorno();
            return new Token(Token.INDENTIFICADOR, tipo);
          }
          break;
        case 4:
          if(isExpression(c)){
            tipo+=c;
          }else{
            Retorno();
            return new Token(Token.OPERADOR, tipo);
          }
          break;
        /*case 5 :            NÃO CONSEGUI RESOLVER OS ERROS QUE DAVAM AO TENTAR LER OS COMENTARIOS
          if(c!='}'){
            c=proxChar();  
          }else{
            System.out.println("---- "+ c);
            return new Token(Token.INDENTIFICADOR, tipo);
          }
          break;   */
          
      }
    }
  }
}
