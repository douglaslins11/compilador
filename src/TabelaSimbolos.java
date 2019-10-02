import java.util.ArrayList;

public class TabelaSimbolos {

    private ArrayList<Simbolo> tabelaSimbolos;

    public TabelaSimbolos(){
        this.tabelaSimbolos = new ArrayList<>();
    }

    public boolean existe (Simbolo simbolo){
        for(Simbolo simboloAtual : tabelaSimbolos){
            if(simboloAtual.getToken().getLexema().equals(simbolo.getToken().getLexema()) && simboloAtual.getBlocoDeclaracao() == simbolo.getBlocoDeclaracao()){
                return true;
            }
        }
        return false;
    }

    public boolean contem (Token token){
       for(Simbolo simboloAtual : tabelaSimbolos){
           if(simboloAtual.getToken().getLexema().equals(token.getLexema()))
               return true;
       }
       return false;
    }

    public Simbolo retornaSimbolo (Token token, int bloco){
        for(Simbolo simboloAtual: tabelaSimbolos){
            if(simboloAtual.getToken().getLexema().equals(token.getLexema()) && simboloAtual.getBlocoDeclaracao() == bloco)
                return simboloAtual;
        }
        return tabelaSimbolos.get(0);//so pra nao dar erro de falta de retorno
    }

    public void destroiVariaveis (int bloco){
        int cont =0;
        for(Simbolo simboloAtual : tabelaSimbolos){
            if(simboloAtual.getBlocoDeclaracao() == bloco){
                //tabelaSimbolos.remove(simboloAtual);
                ++cont;
            }
        }
        while(cont>0){
            tabelaSimbolos.remove(tabelaSimbolos.size()-cont);
            --cont;
        }
    }

    public void adicionaSimbolo (Simbolo simbolo){
        tabelaSimbolos.add(simbolo);
    }
}
