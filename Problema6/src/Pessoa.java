import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.Semaphore;

public class Pessoa extends Thread {
    static ReentrantLock entrado_saindo = new ReentrantLock(); // Usado para indicar que uma nova pessoa acabou de chegar e quer usar o banheiro
    static Semaphore banheiro = new Semaphore(3); // Usado para indicar o número de pessoas que podem estar no banheiro simultânea

    static boolean banheiro_feminino = false; // Essa variável booleana vai ficar verdadeira se em qualquer momento houver uma mulher no banheiro, então nenhum homem poderá entrar no banheiro
    static boolean banheiro_masculino = false; // Essa variável booleana vai ficar verdadeira se em qualquer momento houver um homem no banheiro, então nenhuma mulher poderá entrar no banheiro

    int nome; // Um número inteiro que identifica a thread
    boolean homem; // Define qual será o gênero da pessoa que chegou. Valor de true indica que é um homem, false indica que é uma mulher
    boolean usou_banheiro; // Essa variável fica falsa até que a pessoa consiga entrar e usar o banheiro, usamos ela para fazer um laço de repetição em que cada pessoa tenta entrar no banheiro assim que for possível
    public Pessoa(int nome, boolean homem) {
        this.nome = nome;
        this.homem = homem;
        this.usou_banheiro = false;
    }
    private void entrando_banheiro() throws InterruptedException { //Essa função representa a pessao tentando entrar no banheiro
        entrado_saindo.lock(); // Essa trava permite realizar todas as alterações referentes a uma pessoa entrar no banheiro, como ver quantas cabines estão disponíveis e se for o primeiro a entrar, definir se o banheiro se tornou exclusivo para homens ou mulheres
        int disponivel = banheiro.availablePermits();
        if (disponivel > 0 && ((homem && banheiro_masculino) || (!homem && banheiro_feminino))) { // Se houver alguma cabine disponível e a pessoa for de um gênero permitido ela consegue entrar no banheiro
            if (homem){
                System.out.println(nome + " (Homem) entrou no banheiro");
            } else {
                System.out.println(nome + " (Mulher) entrou no banheiro");
            }
            banheiro.acquire(1); // Ocupa uma cabine do banheiro
            entrado_saindo.unlock();
            saindo_banheiro();
        } else if (!banheiro_feminino && !banheiro_masculino){ // Se o banheiro não tiver nenhum gênero definido, significa que não tem ninguém no banheiro, então a pessoa consegue entrar, então deve deixar o banheiro exclusivo para um gênero
            if (homem){
                System.out.println(nome + " (Homem) entrou no banheiro, e agora nenhuma mulher poderá entrar");
                banheiro_masculino = true;
            } else {
                System.out.println(nome + " (Mulher) entrou no banheiro, e agora nenhum homem poderá entrar");
                banheiro_feminino = true;
            }
            banheiro.acquire(1); // Ocupa uma cabine do banheiro
            entrado_saindo.unlock();
            saindo_banheiro();
        } else {
            entrado_saindo.unlock(); // Não consegue entrar, então permite que outra pessoa consiga tentar entrar
        }

    }
    private void saindo_banheiro() throws InterruptedException { // Essa função representa a pessoa usar o banheiro e sair dele
         sleep(((nome % 3) + 1)); // Representa o tempo em que a pessoa passa para usar o banheiro (deixei o tempo de sleep baixo para permitir que aconteça casos mais interessantes)
         entrado_saindo.lock(); // Depois do tempo, a pessoa consegue sair do banheiro
         usou_banheiro = true;
        if (homem){
            System.out.println(nome + " (Homem) saiu do banheiro");
        } else {
            System.out.println(nome + " (Mulher) saiu do banheiro");
        }
         banheiro.release(1); // Libera uma cabine
         if (banheiro.availablePermits() == 3) { // Se o número de cabines disponíveis do banheiro for 3 após essa pessoa sair, significa que o banheiro está vazio, então não deve mais ser exclusivo a nenhum gênero
             if (homem){
                 System.out.println(nome + " deixou o banheiro vazio, agora qualquer um pode entrar");
                 banheiro_masculino = false;
             } else {
                 System.out.println(nome + " deixou o banheiro vazio, agora qualquer um pode entrar");
                 banheiro_feminino = false;
             }
         }
         entrado_saindo.unlock(); // A pessoa termina de sair
    }
    public void run() {
        if (homem) {
            System.out.println(nome + " (Homem) deseja usar o banheiro");
        } else {
            System.out.println(nome + " (Mulher) deseja usar o banheiro");
        }
        while (!usou_banheiro) { // Enquanto ainda não usou o banheiro, a pessoa constantemente vai tentar entrar no banheiro para usá-lo
            try {
                entrando_banheiro();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
