package com.ruanbianca.redesocial;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

import javax.naming.SizeLimitExceededException;
import javax.swing.JOptionPane;

import static com.ruanbianca.redesocial.utils.MenuUtils.*;
import static com.ruanbianca.redesocial.utils.ConsoleColors.*;


public class App {

    //Todo: substituir por um enum
    final String INCLUIR_PERFIL = "1";
    final String INCLUIR_POSTAGEM = "2";
    final String CONSULTAR_PERFIL = "3";
    final String CONSULTAR_POSTAGEM = "4";
    
    final String EXIBIR_POST_PERFIL = "5";
    final String EXIBIR_POST_HASHTAG = "6";
    final String EXIBIR_POST_POPULARES = "7";
    final String EXIBIR_HASHTAGS_POPULARES = "8";

    final String ATUALIZAR_PERFIL = "9";
    final String REMOVER_PERFIL = "10";
    final String REMOVER_POSTAGEM = "11";
    final String SAIR = "0";

    Scanner input = new Scanner(System.in);

    public static void main( String[] args ){

        RedeSocial Rubi;
        IRepositorioDePerfis perfis;
        IRepositorioDePostagens postagens;

        final int ARRAY = 0;
        final int FILE = 1;

        String[] persistencias = { "Array", "Arquivos", "Sql" };
        int persistencia = JOptionPane.showOptionDialog(null, "Escolha o tipo de persistência desejado", "Bem vindo!", 0, JOptionPane.QUESTION_MESSAGE, null, persistencias, persistencias[0]);

        if(persistencia == ARRAY){
            perfis = new RepositorioDePerfisArray();
            postagens = new RepositorioDePostagensArray();
        }

        else if (persistencia == FILE){ 
            perfis = new RepositorioDePerfisFile();
            postagens = new RepositorioDePostagensFile();
        }

        else {
            perfis = new RepositorioDePerfisSql();
            postagens = new RepositorioDePostagensSql();
        }

        Rubi = new RedeSocial(perfis, postagens);
        App RubiApp = new App();
        RubiApp.executar(Rubi); 
    }


    public void pausar() {

        String enter = GREEN_BOLD_BRIGHT +"<Enter>"+RESET;
        System.out.println(YELLOW_BOLD_BRIGHT+ "\n\nPressione " + enter + YELLOW_BOLD_BRIGHT + " para continuar..."+ RESET);
        input.nextLine();
        limparConsole();
    }
    

    public void executar(RedeSocial Rubi){
        
        String titulo = "RUBI";
        String opcoes = "Incluir Perfil,Incluir Postagem,Consultar Perfil,Consultar Postagem,Exibir Postagens por Perfil,Exibir Postagens por Hashtag,Exibir Postagens Populares,Exibir Hashtags Populares,Atualizar Perfil,Remover Perfil,Remover Postagem";
        String menu = gerarMenu(titulo,opcoes);
        String opcao = "";
        limparConsole();
        
        menuprincipal:do{

            System.out.print(menu);
            opcao = input.nextLine();

            switch (opcao) {

                case INCLUIR_PERFIL:
                     
                    String nome = lerEValidarAtributo("nome",input,80);
                    String username = lerValidarEBuscarAtributo("username",input,30,Rubi,0);
                    String email = lerValidarEBuscarAtributo("email",input,320,Rubi,1);
                    String biografia = lerEValidarAtributo("bio",input,100);
                    
                    Rubi.incluirPerfil(new Perfil(username, nome, email, biografia));
                                        
                    System.out.println(GREEN_BOLD_BRIGHT+"\nWelcome "+nome+"! :)\n"+RESET);
                    break;

                case INCLUIR_POSTAGEM:

                    Postagem novaPostagem;
                    String usernamePost;
                    Optional <Perfil> perfilUser;
                    do {
                        input.nextLine();
                        usernamePost= lerString("Digite seu username: ",input);
                        perfilUser = Rubi.consultarPerfilPorUsername(usernamePost);
                        if(perfilUser.isEmpty()){
                            if(lerString("Usuário não encontrado! Tentar novamente? ",input).equals("nao")){
                                limparConsole();
                                continue menuprincipal;
                            }
                        }else 
                            break;
                    }while(true);

                    String texto = lerString("Digite o conteúdo do texto: ",input);
                    novaPostagem = new Postagem(perfilUser.get().getId(),texto);
                    //Todo: <Wanrning> se der Enter, o programa quebra bem aqui
                    if(lerHashtag("Deseja por hashtags? (0-Enter, 1-Sim)",input).equals("1")){

                        ArrayList<String> hashsProConstrutor = new ArrayList<>();
                        String hashtag = lerString("Digite uma hashtag: ",input);
                        hashsProConstrutor.add(hashtag);

                        boolean querBotarMaisUmaHashtag;//Todo: colocar um nome melhor...

                        do{
                            if(lerHashtag("Deseja por mais uma hashtag? (0-Enter, 1-Sim)",input).equals("1")){
                                String hashDoLoop = lerHashtag("Digite outra hashtag: ",input);
                                hashsProConstrutor.add(hashDoLoop);
                                querBotarMaisUmaHashtag = true;
                            }else{
                                querBotarMaisUmaHashtag = false;
                            }
                        }while(querBotarMaisUmaHashtag);

                        novaPostagem = new PostagemAvancada(perfilUser.get().getId(),texto,hashsProConstrutor);
                   
                    }else{
                        try{
                        novaPostagem = new Postagem(perfilUser.get().getId(),texto);
                        }catch(com.ruanbianca.redesocial.NullAtributesException e){
                            System.out.println("Você deixou algum atributo nulo!!!");
                        }
                    }Rubi.incluirPostagem(novaPostagem);

                    break;

                case CONSULTAR_PERFIL:
                    
                    username = lerString("Digite o username do perfil buscado: ", input);
                    Optional<Perfil> perfilBuscado = Rubi.consultarPerfilPorUsername(username);
                    if(perfilBuscado.isPresent()){
                        System.out.println(RedeSocial.exibirPerfil(perfilBuscado.get()));
                        
                    }else{
                        System.out.println(RED_BOLD_BRIGHT+"Perfil não encontrado!"+RESET);
                    }
                    break;
                    
                
                case CONSULTAR_POSTAGEM:
                
                    texto = lerString("Digite o texto da postagem buscada: ", input);
                    username = lerString("Digite o username do perfil buscado: ", input);
                    String hashtagParaOConsultarPostagem = null;
                    if(lerString("Deseja buscar por hashtag tambem? (Enter - Nao, 1 - Sim)", input).equals("1")){
                        hashtagParaOConsultarPostagem = lerString("Digite uma hashtag: ",input);
                    }
                    perfilBuscado = Rubi.consultarPerfilPorUsername(username);
                    ArrayList<Postagem> postagemBuscada = Rubi.consultarPostagens(texto, perfilBuscado.get(), hashtagParaOConsultarPostagem);
                    if(Optional.ofNullable(postagemBuscada).isPresent()){
                        for(int i = 0; i < postagemBuscada.size(); i++){
                            System.out.println(Rubi.exibirPostagem(postagemBuscada.get(i)));
                        }
                    }else{
                        System.out.println(RED_BOLD_BRIGHT+"Postagem não encontrada!"+RESET);
                    }
                    break;


                case EXIBIR_POST_PERFIL:

                    ArrayList<Postagem> postagensEncontradas;
                    username = JOptionPane.showInputDialog(null,"Digite o username do perfil buscado: ");
                    postagensEncontradas = Rubi.exibirPostagensPorPerfil(username);
                    exibirFeed(postagensEncontradas, Rubi);
                    break;


                case EXIBIR_POST_HASHTAG:
                    ArrayList<PostagemAvancada> postagensAvancadasEncontradas;
                    String hashtagBuscada = lerString("Digite a hashtag buscada: ", input);
                    postagensAvancadasEncontradas = Rubi.exibirPostagensPorHashtag(hashtagBuscada);
                    if(Optional.ofNullable(postagensAvancadasEncontradas).isPresent() && postagensAvancadasEncontradas.size()>0){
                        for(int i  = 0; i < postagensAvancadasEncontradas.size(); i++){
                            System.out.println(Rubi.exibirPostagem(postagensAvancadasEncontradas.get(i)));
                        }
                    }else{

                        System.out.println(RED_BOLD_BRIGHT+"Nenhuma postagem encontrada para essa hashtag!"+RESET);
                    }
                    break;


                case EXIBIR_POST_POPULARES:
                    ArrayList<Postagem> postagensPopulares;
                    postagensPopulares = Rubi.exibirPostagensPopulares();
                    if(Optional.ofNullable(postagensPopulares).isPresent() && postagensPopulares.size()>0){
                        for(int i  = 0; i < postagensPopulares.size(); i++){
                            System.out.println(Rubi.exibirPostagem(postagensPopulares.get(i))); 
                        }
                    }else{
                        System.out.println(RED_BOLD_BRIGHT+"Nenhuma postagem encontrada!"+RESET);
                    }
                    break;


                case EXIBIR_HASHTAGS_POPULARES:

                    ArrayList<Hashtag> hashtagsPopulares;
                    hashtagsPopulares = Rubi.exibirHashtagsPopulares();
                    if(Optional.of(hashtagsPopulares).isPresent()){
                        for(Hashtag hash : hashtagsPopulares){
                            System.out.println(hash.getHashtag());
                        }
                    }else
                        System.out.println(RED_BOLD_BRIGHT+"Nenhuma hashtag encontrada!"+RESET);
                    
                    break;


                case ATUALIZAR_PERFIL:
                    username = lerString("Digite o username do perfil buscado: ",input);
                    perfilBuscado = Rubi.consultarPerfilPorUsername(username);
                    if(perfilBuscado.isPresent()){
                        do{//:O
                            String atributo = lerString("Digite o atributo que deseja atualizar: ",input);
                            switch(atributo){
                                case "nome":
                                    String nomeAtualizado = lerString("Digite o novo nome: ",input);
                                    perfilBuscado.get().setNome(nomeAtualizado);
                                    break;
                                case "email":
                                    String emailAtualizado;
                                    while (true) {
                                        emailAtualizado = lerString("Digite o novo email: ", input);
                                        if (Rubi.usuarioJaExite(null, null, emailAtualizado)) {
                                            System.out.println("Email já está em uso!");
                                            if (lerString("Deseja tentar outro? ", input).equals("sim")) {
                                                continue;
                                            } else {
                                                break; 
                                            }
                                        } else {
                                            perfilBuscado.get().setUsername(emailAtualizado);
                                            break; 
                                        }
                                    }
                                    
                                    break;
                                case "biografia":
                                    String biografiaAtualizada = lerString("Digite a nova biografia: ",input);
                                    perfilBuscado.get().setBiografia(biografiaAtualizada);
                                    break;
                                case "username":
                                    String usernameAtualizado;
                                    while (true) {
                                        usernameAtualizado = lerString("Digite o novo username: ", input);
                                        if (Rubi.usuarioJaExite(null, usernameAtualizado, null)) {
                                            System.out.println("Username já está em uso!");
                                            if (lerString("Deseja tentar outro? ", input).equals("sim")) {
                                                continue;
                                            } else {
                                                break; 
                                            }
                                        } else {
                                            perfilBuscado.get().setUsername(usernameAtualizado);
                                            break; 
                                        }
                                    }
                                    break;
                                
                                default:
                                    System.out.println(RED_BOLD_BRIGHT+"Atributo inválido"+RESET);
                                    break;
                            }

                        }while(lerString("Deseja atualizar outro atributo? ",input).equals("sim"));
                    }else{
                        System.out.println(RED_BOLD_BRIGHT+"Perfil não encontrado!"+RESET);
                    }
                    break;


                case REMOVER_PERFIL:
                    username = lerString("Digite o username do perfil buscado: ",input);
                    try{Rubi.removerPerfil(username);}
                    catch(UserNotFoundException e){
                        System.out.println("Usuário não encontrado!");
                    }
                    System.out.println("Perfil removido com sucesso!");
                    break;


                // case REMOVER_POSTAGEM:

                //     username = lerString("Digite o username do perfil buscado: ", input);
                //     perfilBuscado = Rubi.consultarPerfilPorUsername(username);
                //     String hashtagParaORemoverPostagem =  null;
                //     if(perfilBuscado.isPresent()){
                //         texto = lerString("Digite o texto da postagem buscada: ", input);
                //         if(lerString("Deseja buscar por hashtag tambem? (Enter - Nao, 1 - Sim)", input).equals("1")){
                //             hashtagParaORemoverPostagem = lerString("Digite uma hashtag: ",input);
                //         }
                //         Rubi.removerPostagem(texto,perfilBuscado.get(), hashtagParaORemoverPostagem);
                //         System.out.println("Postagem removida com sucesso!");
                //     }else{
                //         System.out.println(RED_BOLD_BRIGHT+"Perfil não encontrado!"+RESET);
                //     }
                //     break;


                case SAIR:
                    break;


                default:
                    System.out.println("Opção inválida!");
                    break;

            }
            if(!opcao.equals(SAIR))
                pausar();
           
            //Rubi.salvarPerfis();
            //Rubi.salvarPostagens();
        }while(!opcao.equals(SAIR));
        
    }

    public static void exibirFeed(ArrayList<Postagem> postagens, RedeSocial rede){

        final String CURTIR_POSTAGEM = "1";
        final String DESCURTIR_POSTAGEM = "2";
        String feedAtualizado = "";
        String resposta;
        Scanner input = new Scanner(System.in);
        Postagem postAtual;
        if(Optional.ofNullable(postagens).isPresent() && postagens.size()>0){
            for(int i  = 0; i < postagens.size(); i++) {   
                limparConsole();
                postAtual =  postagens.get(i);
                System.out.println(feedAtualizado + rede.exibirPostagem(postAtual));
                System.out.print("Interagir?\nEnter - Não, 1 - Curtir, 2 - Descurtir)\n>>> ");
                resposta = input.nextLine();
                if(resposta.equals(CURTIR_POSTAGEM)){
                    postAtual.curtir();
                }else if(resposta.equals(DESCURTIR_POSTAGEM)){
                    postAtual.descurtir();
                }
                if(postAtual instanceof PostagemAvancada){

                    ((PostagemAvancada)postAtual).incrementarVisualizacoes();
                }
                feedAtualizado += rede.exibirPostagem(postAtual);

                if(i+1==postagens.size()){
                    limparConsole();
                    System.out.println(feedAtualizado);
                }
            }
        }else
            System.out.println(RED_BOLD_BRIGHT+"Nenhuma postagem encontrada!"+RESET);
        
                
    }

    public static String lerValidarEBuscarAtributo(String nomeAtributo,Scanner input, int lenMax, RedeSocial rede, int tipoAtributo){

        final int USERNAME = 0;
        final int EMAIL = 1;
        String atributo;
        while(true){

            atributo = lerEValidarAtributo(nomeAtributo, input, lenMax);
            
            if((tipoAtributo == USERNAME && rede.usuarioJaExite(null, atributo, null))
            || (tipoAtributo == EMAIL && rede.usuarioJaExite(null, null, atributo))){
                    System.out.println(nomeAtributo+" já está em uso, por favor tente novamente!");
                    continue;
            }
            break;
        }
        return atributo;
    }

    public static String lerEValidarAtributo(String nomeAtributo,Scanner input, int lenMax){
       
        String atributo;

        while(true){
            
            atributo = lerString("Type your "+nomeAtributo+": ",input);

            if(Optional.ofNullable(atributo).isEmpty() ||  atributo.isEmpty() || atributo.isBlank()){
                System.out.println("Don't be so shy!");
                continue;
            }

            if(lenMax != 0 && atributo.length() > lenMax){//O 0 aqui serve apenas para indicar q não se deseja usar o len
                System.out.printf("O limite de caracteres para %s é %d!\n",nomeAtributo,lenMax);
                continue;
            }

            break;
        }
        return atributo;
    }

    


    public static String validarString(String entrada) throws NullAtributesException{
        
        if(Optional.ofNullable(entrada).isEmpty() ||  entrada.trim().isBlank())
            throw new NullAtributesException();

        String saida = entrada.trim();
        return saida;
    }
    public static double validarValor(String entrada) throws NullAtributesException, NumberFormatException, SizeLimitExceededException{
        
        //validarString(entrada,0);        
        return Double.parseDouble(entrada);
    }
}