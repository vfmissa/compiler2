import java.util.HashMap;
import java.util.Map;

public class Sintatico {

  private Lexico lexico;
  private Token simbolo;
  private Map<String, Simbolo> tabelaSimbolo = new HashMap<>();
  private int tipo;

  public Sintatico(String arq) {
    lexico = new Lexico(arq);
  }

  private void obtemSimbolo() {
    simbolo = lexico.proxToken();
  
  }

  private boolean verificaSimbolo(String termo) {
    return (simbolo != null && simbolo.getValor().equals(termo));
  }

  private void DV() {
    System.out.println("DV");
    if (simbolo != null) {
      D();
      MV();
    }
  }

  private void MV() {
    System.out.println("MV");
    if (verificaSimbolo(";")) {
      obtemSimbolo();
      DV();
    }
  }

  private void D() {
    System.out.println("D");
    T();
    L();
  }

  private void T() {
    System.out.println("T");
    if (!verificaSimbolo("int") && !verificaSimbolo("float")) {
      throw new RuntimeException("Erro sintático esperado int ou float encontrado: " + simbolo.getValor());
    }
    if (simbolo.getValor().equals("int")) {
      this.tipo = Token.INTEIRO;
    } else {
      this.tipo = Token.REAL;
    }
    obtemSimbolo();
  }

  private void L() {
    System.out.println("L");
    if (simbolo == null || simbolo.getTipo() != Token.INDENTIFICADOR) {
      throw new RuntimeException("Erro sintático esperado identificador encontrado: " + simbolo.getValor());
    }

    if (tabelaSimbolo.containsKey(simbolo.getValor())) {
      throw new RuntimeException("Erro semântico identificador já encontrado: " + simbolo.getValor());
    } else {
      tabelaSimbolo.put(simbolo.getValor(), new Simbolo(this.tipo, simbolo.getValor()));
    }

    obtemSimbolo();
    Llinha();
  }

  private void Llinha() {
    System.out.println("L'");
    if (verificaSimbolo(",")) {
      obtemSimbolo();
      L();
    }
  }

  public void Analisador() {
    obtemSimbolo();
    DV();
    if (simbolo == null) {
      System.out.println("Tudo Certo!");
    } else {
      throw new RuntimeException("Erro sintático esperado fim de cadeia encontrado: " + simbolo.getValor());
    }
  }
}
