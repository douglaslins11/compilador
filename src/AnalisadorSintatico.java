import java.io.FileReader;

public class AnalisadorSintatico {
    FileReader arquivo;
    Token lookhead;
    Scanner analisadorLexico;
    int bloco=0;
    TabelaSimbolos tabelaDeSimbolos;
    int nLabel = 0;
    int tLabel = 0;


    public AnalisadorSintatico(FileReader arquivo) throws Exception {
        this.arquivo = arquivo;
        this.analisadorLexico = new Scanner(arquivo);
        lookhead = analisadorLexico.Scan();
        this.tabelaDeSimbolos = new TabelaSimbolos();
    }

    public void pegarProximoToken() throws Exception {
        lookhead = analisadorLexico.Scan();
    }

    public void Parser() throws Exception {
        if(lookhead.getGramatica() != Valores.INT){
            throw new Exception("ERRO na linha "+analisadorLexico.linha+", coluna "+analisadorLexico.coluna+", ultimo token lido: "+lookhead.getLexema()+" :Int nao encontrado na declaracao da funcao principal");
        }
        pegarProximoToken();
        if(lookhead.getGramatica() != Valores.MAIN){
            throw new Exception("ERRO na linha "+analisadorLexico.linha+", coluna "+analisadorLexico.coluna+", ultimo token lido: "+lookhead.getLexema()+" :Main nao encontrado na declaracao da funcao principal");
        }
        pegarProximoToken();
        if(lookhead.getGramatica() != Valores.ABREPARENTESES){
            throw new Exception("ERRO na linha "+analisadorLexico.linha+", coluna "+analisadorLexico.coluna+", ultimo token lido: "+lookhead.getLexema()+" :'(' nao encontrado na declaracao da funcao principal");
        }
        pegarProximoToken();
        if (lookhead.getGramatica() != Valores.FECHAPARENTESES){
            throw new Exception("ERRO na linha "+analisadorLexico.linha+", coluna "+analisadorLexico.coluna+", ultimo token lido: "+lookhead.getLexema()+" :')' nao encontrado na declaracao da funcao principal");
        }
        pegarProximoToken();
        Bloco();
    }

    public void Bloco() throws Exception {
        if(lookhead.getGramatica() == Valores.ABRECHAVES){
            pegarProximoToken();
            this.bloco += 1;
            while(lookhead.getGramatica() == Valores.INT || lookhead.getGramatica() == Valores.FLOAT || lookhead.getGramatica() == Valores.CHAR){
                DeclaraVariavel();
            }
            while(lookhead.getGramatica() == Valores.ABRECHAVES || lookhead.getGramatica() == Valores.DO || lookhead.getGramatica() == Valores.WHILE || lookhead.getGramatica() == Valores.FOR || lookhead.getGramatica() == Valores.IF || lookhead.getGramatica() == Valores.ID){
                Comando();
            }
            //if(lookhead.getGramatica() == Valores.INT || lookhead.getGramatica() == Valores.FLOAT || lookhead.getGramatica() == Valores.CHAR){
              //  throw new Exception("ERRO na linha "+analisadorLexico.linha+", coluna "+analisadorLexico.coluna+", ultimo token lido: "+lookhead.getLexema()+" : só é possível declarar variavel no inicio do escopo");
            //}
            if(lookhead.getGramatica() != Valores.FECHACHAVES){
                throw new Exception("ERRO na linha "+analisadorLexico.linha+", coluna "+analisadorLexico.coluna+", ultimo token lido: "+lookhead.getLexema()+" :'}' nao encontrado no fechamento do bloco");
            }
            else{
                pegarProximoToken();
            }
        }
        else{
            throw new Exception("ERRO na linha "+analisadorLexico.linha+", coluna "+analisadorLexico.coluna+", ultimo token lido: "+lookhead.getLexema()+" :'{' nao encontrado na abertura do bloco");
        }

        tabelaDeSimbolos.destroiVariaveis(bloco);
        bloco-=1;
    }

    public void DeclaraVariavel() throws Exception {

        Token tipoVariavel = lookhead;
        pegarProximoToken();

        if(lookhead.getGramatica() == Valores.ID){
            Simbolo simbolo = new Simbolo(lookhead, bloco, tipoVariavel.getGramatica(), lookhead.getLexema());
            if(tabelaDeSimbolos.existe(simbolo)){
                throw new Exception("ERRO na linha "+analisadorLexico.linha+", coluna "+analisadorLexico.coluna+", ultimo token lido: "+lookhead.getLexema()+" : variavel já declarada no escopo atual");
            }
            else{
                tabelaDeSimbolos.adicionaSimbolo(simbolo);
            }
            pegarProximoToken();
            while(lookhead.getGramatica() == Valores.VIRGULA){
                pegarProximoToken();
                if (lookhead.getGramatica() != Valores.ID){
                    throw new Exception("ERRO na linha "+analisadorLexico.linha+", coluna "+analisadorLexico.coluna+", ultimo token lido: "+lookhead.getLexema()+" : Identificador nao encontrado na declaraco da variavel");
                }
                Simbolo simbolo2 = new Simbolo(lookhead, bloco, tipoVariavel.getGramatica(), lookhead.getLexema());
                if(tabelaDeSimbolos.existe(simbolo2)){
                    throw new Exception("ERRO na linha "+analisadorLexico.linha+", coluna "+analisadorLexico.coluna+", ultimo token lido: "+lookhead.getLexema()+" : variavel já declarada no escopo atual");
                }
                else{
                    tabelaDeSimbolos.adicionaSimbolo(simbolo2);
                }
                pegarProximoToken();
            }
            if(lookhead.getGramatica() != Valores.PONTOVIRGULA){
                throw new Exception("ERRO na linha "+analisadorLexico.linha+", coluna "+analisadorLexico.coluna+", ultimo token lido: "+lookhead.getLexema()+" :';' nao encontrado no fim da declaracao da variavel");
            }
            pegarProximoToken();
        }
        else{
            throw new Exception("ERRO na linha "+analisadorLexico.linha+", coluna "+analisadorLexico.coluna+", ultimo token lido: "+lookhead.getLexema()+" : Identificador nao encontrado na declaraco da variavel");
        }
    }

    public void Comando() throws Exception {
        if(lookhead.getGramatica() == Valores.ID || lookhead.getGramatica() == Valores.ABRECHAVES){
            ComandoBasico();
        }
        else if(lookhead.getGramatica() == Valores.DO || lookhead.getGramatica() == Valores.WHILE || lookhead.getGramatica() == Valores.FOR){
            Iteracao();
        }
        else if(lookhead.getGramatica() == Valores.IF){
            CasoIfElse();
        }
        else{
            throw new Exception("ERRO na linha "+analisadorLexico.linha+", coluna "+analisadorLexico.coluna+", ultimo token lido: "+lookhead.getLexema()+" : Comando nao encontrado");
        }
    }

    public void ComandoBasico() throws Exception {
        if(lookhead.getGramatica() == Valores.ID){
            Atribuicao();
        }
        else{
            Bloco();
        }
    }

    public void Iteracao() throws Exception {
        if(lookhead.getGramatica() == Valores.WHILE){ //caso while
            pegarProximoToken();
            if(lookhead.getGramatica() == Valores.ABREPARENTESES){
                pegarProximoToken();
                String l = criaLabel();
                System.out.println(l+":");
                Simbolo resultExpRel = ExpRelacional();
                String l2 = criaLabel();
                System.out.println("if "+resultExpRel.getLexema()+" == 0 goto "+l2);
                if(lookhead.getGramatica() == Valores.FECHAPARENTESES){
                    pegarProximoToken();
                    Comando();
                    System.out.println("goto "+l);
                    System.out.println(l2+":");
                }
                else{
                    throw new Exception("ERRO na linha "+analisadorLexico.linha+", coluna "+analisadorLexico.coluna+", ultimo token lido: "+lookhead.getLexema()+" : ')' nao encontrado");
                }
            }
            else{
                throw new Exception("ERRO na linha "+analisadorLexico.linha+", coluna "+analisadorLexico.coluna+", ultimo token lido: "+lookhead.getLexema()+" : '(' nao encontrado");
            }
        }
        else if(lookhead.getGramatica() == Valores.DO){
            pegarProximoToken();
            String l = criaLabel();
            System.out.println(l+":");
            Comando();
            if(lookhead.getGramatica() != Valores.WHILE){
                throw new Exception("ERRO na linha "+analisadorLexico.linha+", coluna "+analisadorLexico.coluna+", ultimo token lido: "+lookhead.getLexema()+" : while nao encontrado");
            }
            pegarProximoToken();
            if(lookhead.getGramatica() != Valores.ABREPARENTESES){
                throw new Exception("ERRO na linha "+analisadorLexico.linha+", coluna "+analisadorLexico.coluna+", ultimo token lido: "+lookhead.getLexema()+" :'(' nao encontrado");
            }
            pegarProximoToken();
            Simbolo resultExpRel = ExpRelacional();
            String l2 = criaLabel();
            System.out.println("if "+resultExpRel.getLexema()+" != 0 goto "+l2);
            System.out.println("goto "+l);
            System.out.println(l2+":");
            if(lookhead.getGramatica() != Valores.FECHAPARENTESES){
                throw new Exception("ERRO na linha "+analisadorLexico.linha+", coluna "+analisadorLexico.coluna+", ultimo token lido: "+lookhead.getLexema()+" :')' nao encontrado");
            }
            pegarProximoToken();
            if(lookhead.getGramatica() != Valores.PONTOVIRGULA){
                throw new Exception("ERRO na linha "+analisadorLexico.linha+", coluna "+analisadorLexico.coluna+", ultimo token lido: "+lookhead.getLexema()+" :';' nao encontrado");
            }
            pegarProximoToken();
        }
    }

    public void CasoIfElse() throws Exception {
        boolean flagElse=false;
        pegarProximoToken();
        if(lookhead.getGramatica() == Valores.ABREPARENTESES){
            pegarProximoToken();
            Simbolo resultExpRel = ExpRelacional();
            String label = criaLabel();
            System.out.println("if "+resultExpRel.getLexema() + " == 0 goto "+label);
            if(lookhead.getGramatica() == Valores.FECHAPARENTESES){
                pegarProximoToken();
                Comando();
                String l2 = criaLabel();
                if(lookhead.getGramatica() == Valores.ELSE){
                    flagElse = true;
                    System.out.println("goto "+l2);
                    System.out.println(label+":");
                    pegarProximoToken();
                    Comando();
                }
                if(!flagElse){
                    System.out.println(label+":");
                }
                else{
                    System.out.println(l2+":");
                }
            }
            else{
                throw new Exception("ERRO na linha "+analisadorLexico.linha+", coluna "+analisadorLexico.coluna+", ultimo token lido: "+lookhead.getLexema()+" : ')' nao encontrado");
            }
        }
        else {
            throw new Exception("ERRO na linha " + analisadorLexico.linha + ", coluna " + analisadorLexico.coluna + ", ultimo token lido: " + lookhead.getLexema() + " : '(' nao encontrado");
        }
    }

    public void Atribuicao() throws Exception {
        if(tabelaDeSimbolos.contem(this.lookhead)) {
            Simbolo operador1 = tabelaDeSimbolos.retornaSimbolo(this.lookhead, bloco);
            pegarProximoToken();
            if (lookhead.getGramatica() == Valores.ATRIBUICAO) {
                pegarProximoToken();
                Simbolo operador2 = ExpAritm();
                if(!Simbolo.compativeis(operador1, operador2)){
                    throw new Exception("ERRO na linha " + analisadorLexico.linha + ", coluna " + analisadorLexico.coluna + ", ultimo token lido: " + lookhead.getLexema() + " : incompatibilidade de tipos");
                }
                else{
                    if((operador1.getTipo().equals(Valores.CHAR)&&(operador2.getTipo().equals(Valores.TIPOCHAR)||operador2.getTipo().equals(Valores.CHAR)))||(operador1.getTipo().equals(Valores.INT)&&(operador2.getTipo().equals(Valores.TIPOINT)||operador2.getTipo().equals(Valores.INT)))||(operador1.getTipo().equals(Valores.FLOAT)&&(operador2.getTipo().equals(Valores.TIPOFLOAT)||operador2.getTipo().equals(Valores.FLOAT)))){
                        System.out.println(operador1.getLexema()+" = "+operador2.getLexema());
                    }
                    else{
                        Simbolo simbolo = new Simbolo(operador2.getToken(), operador2.getBlocoDeclaracao(), operador2.getTipo(), operador2.getLexema());
                        String temporaria = criaVarTemp();
                        System.out.println(temporaria+" = ");
                        convertFloat(simbolo);
                        simbolo.setLexema(temporaria);
                        System.out.println(operador1.getLexema()+" = "+simbolo.getLexema());
                    }
                }
                if (lookhead.getGramatica() != Valores.PONTOVIRGULA) {
                    throw new Exception("ERRO na linha " + analisadorLexico.linha + ", coluna " + analisadorLexico.coluna + ", ultimo token lido: " + lookhead.getLexema() + " : ';' nao encontrado na atribuicao");
                } else {
                    pegarProximoToken();
                }
            } else {
                throw new Exception("ERRO na linha " + analisadorLexico.linha + ", coluna " + analisadorLexico.coluna + ", ultimo token lido: " + lookhead.getLexema() + " : '=' Sinal de atribuicao nao encontrado");
            }
        }
        else{
            throw new Exception("ERRO na linha " + analisadorLexico.linha + ", coluna " + analisadorLexico.coluna + ", ultimo token lido: " + lookhead.getLexema() + " : variavel nao encontrada na tabela de simbolos");
        }
    }

    public Simbolo ExpAritm () throws Exception {
        Simbolo operador1 = Termo();
        Token operador = this.lookhead;
        //pegarProximoToken();
        Simbolo operador2 = ExpAritm_2();

        if(operador2 != null){
            if((operador1.getTipo().equals(Valores.CHAR)||operador1.getTipo().equals(Valores.TIPOCHAR)) && (operador2.getTipo().equals(Valores.CHAR)||operador2.getTipo().equals(Valores.TIPOCHAR))){
                String temporaria = criaVarTemp();
                System.out.println(temporaria+" = "+operador1.getLexema()+operador.getLexema()+operador2.getLexema());
                operador2.setLexema(temporaria);
                return operador2;
            }
            else if((operador1.getTipo().equals(Valores.CHAR)||operador1.getTipo().equals(Valores.TIPOCHAR)) || (operador2.getTipo().equals(Valores.CHAR)||operador2.getTipo().equals(Valores.TIPOCHAR))){
                throw new Exception("ERRO na linha " + analisadorLexico.linha + ", coluna " + analisadorLexico.coluna + ", ultimo token lido: " + lookhead.getLexema() + " : incompatibilidade de tipos");
            }
            else if((operador1.getTipo().equals(Valores.FLOAT)||operador1.getTipo().equals(Valores.TIPOFLOAT)) || (operador2.getTipo().equals(Valores.FLOAT)||operador2.getTipo().equals(Valores.TIPOFLOAT))){
                if((operador1.getTipo().equals(Valores.FLOAT)||operador1.getTipo().equals(Valores.TIPOFLOAT)) && (operador2.getTipo().equals(Valores.FLOAT)||operador2.getTipo().equals(Valores.TIPOFLOAT))){
                    String temporaria = criaVarTemp();
                    System.out.println(temporaria+" = "+operador1.getLexema()+operador.getLexema()+operador2.getLexema());
                    operador2.setLexema(temporaria);
                    return operador2;
                }
                else if(operador1.getTipo().equals(Valores.FLOAT)||operador1.getTipo().equals(Valores.TIPOFLOAT)){
                    Simbolo simbolo = new Simbolo(operador2.getToken(), operador2.getBlocoDeclaracao(), operador2.getTipo(), operador2.getLexema());
                    String temporaria = criaVarTemp();
                    System.out.println(temporaria+" = ");
                    convertFloat(simbolo);
                    simbolo.setLexema(temporaria);
                    String temporiaria2 = criaVarTemp();
                    System.out.println(temporiaria2+" = "+simbolo.getLexema()+operador.getLexema()+operador1.getLexema());
                    simbolo.setLexema(temporiaria2);
                    return  simbolo;
                }
                else{
                    Simbolo simbolo = new Simbolo(operador2.getToken(), operador1.getBlocoDeclaracao(), operador1.getTipo(), operador1.getLexema());
                    String temporaria = criaVarTemp();
                    System.out.println(temporaria+" = ");
                    convertFloat(simbolo);
                    simbolo.setLexema(temporaria);
                    String temporiaria2 = criaVarTemp();
                    System.out.println(temporiaria2+" = "+simbolo.getLexema()+operador.getLexema()+operador2.getLexema());
                    simbolo.setLexema(temporiaria2);
                    return  simbolo;
                }
            }
            else{
                String temporaria = criaVarTemp();
                System.out.println(temporaria+" = "+operador1.getLexema()+operador.getLexema()+operador2.getLexema());
                operador2.setLexema(temporaria);
                return operador2;
            }
        }
        return operador1;
    }

    public Simbolo Termo() throws Exception {
        Simbolo operador1 = Fator();
        while(lookhead.getGramatica() == Valores.DIVISAO || lookhead.getGramatica() == Valores.MULTIPLICACAO){
            Token operador = lookhead;
            pegarProximoToken();
            Simbolo operador2 = Fator();
            if((operador1.getTipo().equals(Valores.CHAR)||operador1.getTipo().equals(Valores.TIPOCHAR)) && (operador2.getTipo().equals(Valores.CHAR)||operador2.getTipo().equals(Valores.TIPOCHAR))){
                String temporaria = criaVarTemp();
                System.out.println(temporaria+" = "+operador1.getLexema()+operador.getLexema()+operador2.getLexema());
                operador1.setLexema(temporaria);
                //return operador1;
            }
            else if((operador1.getTipo().equals(Valores.CHAR)||operador1.getTipo().equals(Valores.TIPOCHAR)) || (operador2.getTipo().equals(Valores.CHAR)||operador2.getTipo().equals(Valores.TIPOCHAR))){
                throw new Exception("ERRO na linha " + analisadorLexico.linha + ", coluna " + analisadorLexico.coluna + ", ultimo token lido: " + lookhead.getLexema() + " : incompatibilidade de tipos");
            }
            else if((operador1.getTipo().equals(Valores.FLOAT)||operador1.getTipo().equals(Valores.TIPOFLOAT)) || (operador2.getTipo().equals(Valores.FLOAT)||operador2.getTipo().equals(Valores.TIPOFLOAT))){
                if((operador1.getTipo().equals(Valores.FLOAT)||operador1.getTipo().equals(Valores.TIPOFLOAT)) && (operador2.getTipo().equals(Valores.FLOAT)||operador2.getTipo().equals(Valores.TIPOFLOAT))){
                    String temporaria = criaVarTemp();
                    System.out.println(temporaria+" = "+operador1.getLexema()+operador.getLexema()+operador2.getLexema());
                    operador1.setLexema(temporaria);
                    //return operador1;
                }
                else if(operador1.getTipo().equals(Valores.FLOAT)||operador1.getTipo().equals(Valores.TIPOFLOAT)){
                    Simbolo simbolo = new Simbolo(operador2.getToken(), operador2.getBlocoDeclaracao(), operador2.getTipo(), operador2.getLexema());
                    String temporaria = criaVarTemp();
                    System.out.println(temporaria+" = ");
                    convertFloat(simbolo);
                    simbolo.setLexema(temporaria);
                    String temporiaria2 = criaVarTemp();
                    System.out.println(temporiaria2+" = "+simbolo.getLexema()+operador.getLexema()+operador1.getLexema());
                    simbolo.setLexema(temporiaria2);
                    operador1 = simbolo;
                    //return  simbolo;
                }
                else{
                    Simbolo simbolo = new Simbolo(operador2.getToken(), operador1.getBlocoDeclaracao(), operador1.getTipo(), operador1.getLexema());
                    String temporaria = criaVarTemp();
                    System.out.println(temporaria+" = ");
                    convertFloat(simbolo);
                    simbolo.setLexema(temporaria);
                    String temporiaria2 = criaVarTemp();
                    System.out.println(temporiaria2+" = "+simbolo.getLexema()+operador.getLexema()+operador2.getLexema());
                    simbolo.setLexema(temporiaria2);
                    operador1 = simbolo;
                    //return  simbolo;
                }
            }
            else if(operador.getGramatica().equals(Valores.DIVISAO)){
                String temporaria = criaVarTemp();
                System.out.println(temporaria+" = "+operador1.getLexema()+operador.getLexema()+operador2.getLexema());
                operador1.setLexema(temporaria);
                operador1.setTipo(Valores.TIPOFLOAT);
                //return operador1;
            }
            else{
                String temporaria = criaVarTemp();
                System.out.println(temporaria+" = "+operador1.getLexema()+operador.getLexema()+operador2.getLexema());
                operador1.setLexema(temporaria);
                //return operador1;
            }
        }
        return operador1;
        //throw new Exception("ERRO na linha " + analisadorLexico.linha + ", coluna " + analisadorLexico.coluna + ", ultimo token lido: " + lookhead.getLexema() + " : falta um '*' ou '/'");
    }

    public Simbolo ExpAritm_2() throws Exception {
        Simbolo operador1 = Termo();
        if(lookhead.getGramatica() == Valores.SOMA || lookhead.getGramatica() == Valores.SUBTRACAO){
            Token operador = this.lookhead;
            pegarProximoToken();
            //Simbolo operador1 = Termo();
            Simbolo operador2 = ExpAritm_2();

            if(operador2 != null){
                if((operador1.getTipo().equals(Valores.CHAR)||operador1.getTipo().equals(Valores.TIPOCHAR)) && (operador2.getTipo().equals(Valores.CHAR)||operador2.getTipo().equals(Valores.TIPOCHAR))){
                    String temporaria = criaVarTemp();
                    System.out.println(temporaria+" = "+operador1.getLexema()+operador.getLexema()+operador2.getLexema());
                    operador2.setLexema(temporaria);
                    return operador2;
                }
                else if((operador1.getTipo().equals(Valores.CHAR)||operador1.getTipo().equals(Valores.TIPOCHAR)) || (operador2.getTipo().equals(Valores.CHAR)||operador2.getTipo().equals(Valores.TIPOCHAR))){
                    throw new Exception("ERRO na linha " + analisadorLexico.linha + ", coluna " + analisadorLexico.coluna + ", ultimo token lido: " + lookhead.getLexema() + " : incompatibilidade de tipos");
                }
                else if((operador1.getTipo().equals(Valores.FLOAT)||operador1.getTipo().equals(Valores.TIPOFLOAT)) || (operador2.getTipo().equals(Valores.FLOAT)||operador2.getTipo().equals(Valores.TIPOFLOAT))){
                    if((operador1.getTipo().equals(Valores.FLOAT)||operador1.getTipo().equals(Valores.TIPOFLOAT)) && (operador2.getTipo().equals(Valores.FLOAT)||operador2.getTipo().equals(Valores.TIPOFLOAT))){
                        String temporaria = criaVarTemp();
                        System.out.println(temporaria+" = "+operador1.getLexema()+operador.getLexema()+operador2.getLexema());
                        operador2.setLexema(temporaria);
                        return operador2;
                    }
                    else if(operador1.getTipo().equals(Valores.FLOAT)||operador1.getTipo().equals(Valores.TIPOFLOAT)){
                        Simbolo simbolo = new Simbolo(operador2.getToken(), operador2.getBlocoDeclaracao(), operador2.getTipo(), operador2.getLexema());
                        String temporaria = criaVarTemp();
                        System.out.println(temporaria+" = ");
                        convertFloat(simbolo);
                        simbolo.setLexema(temporaria);
                        String temporiaria2 = criaVarTemp();
                        System.out.println(temporiaria2+" = "+simbolo.getLexema()+operador.getLexema()+operador1.getLexema());
                        simbolo.setLexema(temporiaria2);
                        return  simbolo;
                    }
                    else{
                        Simbolo simbolo = new Simbolo(operador2.getToken(), operador1.getBlocoDeclaracao(), operador1.getTipo(), operador1.getLexema());
                        String temporaria = criaVarTemp();
                        System.out.println(temporaria+" = ");
                        convertFloat(simbolo);
                        simbolo.setLexema(temporaria);
                        String temporiaria2 = criaVarTemp();
                        System.out.println(temporiaria2+" = "+simbolo.getLexema()+operador.getLexema()+operador2.getLexema());
                        simbolo.setLexema(temporiaria2);
                        return  simbolo;
                    }
                }
                else{
                    String temporaria = criaVarTemp();
                    System.out.println(temporaria+" = "+operador1.getLexema()+operador.getLexema()+operador2.getLexema());
                    operador2.setLexema(temporaria);
                    return operador2;
                }
            }
            return operador1;
        }
        else{
            return operador1;
        }
    }

    public Simbolo ExpRelacional() throws Exception {
        Simbolo operador1 = ExpAritm();
        if(lookhead.getGramatica() == Valores.IGUALDADE || lookhead.getGramatica() == Valores.DIFERENTE || lookhead.getGramatica() == Valores.MAIOR
        || lookhead.getGramatica() == Valores.MENOR || lookhead.getGramatica() == Valores.MAIORIGUAL || lookhead.getGramatica() == Valores.MENORIGUAL){
            Token operadorRelacional = this.lookhead;
            pegarProximoToken();
            Simbolo operador2 = ExpAritm();
            if(!Simbolo.compativeisExpRel(operador1, operador2)){
                throw new Exception("ERRO na linha " + analisadorLexico.linha + ", coluna " + analisadorLexico.coluna + ", ultimo token lido: " + lookhead.getLexema() + " : incompatibilidade de tipos");
            }
            else{
                String temporaria = criaVarTemp();
                System.out.println(temporaria+" = "+operador1.getLexema() + operadorRelacional.getLexema() + operador2.getLexema());
                operador1.setLexema(temporaria);
                return operador1;
            }
        }
        else{
            throw new Exception("ERRO na linha "+analisadorLexico.linha+", coluna "+analisadorLexico.coluna+", ultimo token lido: "+lookhead.getLexema()+" : Falta algum operador relacional");
        }
    }

    public Simbolo Fator() throws Exception {
        if(lookhead.getGramatica().equals(Valores.SOMA)||lookhead.getGramatica().equals(Valores.SUBTRACAO)||lookhead.getGramatica().equals(Valores.MULTIPLICACAO))
        pegarProximoToken();
        if (lookhead.getGramatica() == Valores.TIPOINT || lookhead.getGramatica() == Valores.TIPOFLOAT || lookhead.getGramatica() == Valores.TIPOCHAR || lookhead.getGramatica() == Valores.ID) {
            if (lookhead.getGramatica() == Valores.TIPOINT) {
                Simbolo simbolo = new Simbolo(lookhead, this.bloco, Valores.TIPOINT, lookhead.getLexema());
                pegarProximoToken();
                return simbolo;
            } else if (lookhead.getGramatica() == Valores.TIPOFLOAT) {
                Simbolo simbolo = new Simbolo(lookhead, this.bloco, Valores.TIPOFLOAT, lookhead.getLexema());
                pegarProximoToken();
                return simbolo;
            } else if (lookhead.getGramatica() == Valores.TIPOCHAR) {
                Simbolo simbolo = new Simbolo(lookhead, this.bloco, Valores.TIPOCHAR, lookhead.getLexema());
                pegarProximoToken();
                return simbolo;
            } else {
                if (!tabelaDeSimbolos.contem(lookhead)) {
                    throw new Exception("ERRO na linha " + analisadorLexico.linha + ", coluna " + analisadorLexico.coluna + ", ultimo token lido: " + lookhead.getLexema() + " : variavel nao encontrada na tabela de simbolos");
                }
                Simbolo s = tabelaDeSimbolos.retornaSimbolo(lookhead, bloco);
                Simbolo simbolo = new Simbolo(lookhead, s.getBlocoDeclaracao(), s.getTipo(), lookhead.getLexema());
                pegarProximoToken();
                return simbolo;
            }
        } else {
            if (lookhead.getGramatica() == Valores.ABREPARENTESES) {
                pegarProximoToken();
                Simbolo simbolo = ExpAritm();
                if (lookhead.getGramatica() == Valores.FECHAPARENTESES) {
                    pegarProximoToken();
                    return simbolo;
                } else {
                    throw new Exception("ERRO na linha " + analisadorLexico.linha + ", coluna " + analisadorLexico.coluna + ", ultimo token lido: " + lookhead.getLexema() + " : ')' nao encontrado");
                }
            }
        }
        return null;
    }

    public void convertFloat (Simbolo simbolo){
        System.out.println("(FLOAT) "+simbolo.getLexema());
        simbolo.setTipo(Valores.TIPOFLOAT);
    }

    public String criaLabel (){
        String l = "L" + this.nLabel;
        this.nLabel+=1;
        return l;
    }

    public String criaVarTemp (){
        String t = "T" + this.tLabel;
        this.tLabel+=1;
        return t;
    }
}
