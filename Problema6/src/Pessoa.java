import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.Semaphore;

public class Pessoa extends Thread {
    static ReentrantLock entrado_saindo = new ReentrantLock(); // Usado para indicar que uma nova pessoa acabou de chegar e quer usar o banheiro
    static Semaphore banheiro = new Semaphore(3); // Usado para indicar o número de pessoas que podem estar no banheiro ao mesmo tempo

    static boolean banheiro_feminino = false; // Impede que homens entrem no banheiro
    static boolean banheiro_masculino = false; // Impede que mulheres entrem no banheiro

    int nome;
    boolean homem; // Define qual será o gênero da pessoa que chegou. Valor de true indica que é um homem, false indica que é uma mulher
    boolean usou_banheiro;
    public Pessoa(int nome, boolean homem) {
        this.nome = nome;
        this.homem = homem;
        this.usou_banheiro = false;
    }
    private void entrando_banheiro() throws InterruptedException {
        entrado_saindo.lock();
        int disponivel = banheiro.availablePermits();
        if (disponivel > 0 && ((homem && banheiro_masculino) || (!homem && banheiro_feminino))) {
            System.out.println(nome + " entrou no banheiro");
            banheiro.acquire(1);
            entrado_saindo.unlock();
            saindo_banheiro();
        } else if (!banheiro_feminino && !banheiro_masculino){
            if (homem){
                System.out.println(nome + " entrou no banheiro, e por ser homem, nenhuma mulher poderá entrar");
                banheiro_masculino = true;
            } else {
                System.out.println(nome + " entrou no banheiro, e por ser mulher, nenhum homem poderá entrar");
                banheiro_feminino = true;
            }
            banheiro.acquire(1);
            entrado_saindo.unlock();
            saindo_banheiro();
        } else {
            entrado_saindo.unlock();
        }

    }
    private void saindo_banheiro() throws InterruptedException {
         sleep(((nome % 3) + 1)*10);
         entrado_saindo.lock();
         usou_banheiro = true;
         System.out.println(nome + " está saindo do banheiro");
         banheiro.release(1);
         if (banheiro.availablePermits() == 3) {
             if (homem){
                 System.out.println(nome + " foi o ultimo homem a sair do banheiro");
                 banheiro_masculino = false;
             } else {
                 System.out.println(nome + " foi a ultima mulher a sair do banheiro");
                 banheiro_feminino = false;
             }
         }
         entrado_saindo.unlock();
    }
    public void run() {
        if (homem) {
            System.out.println("Homem " + nome + " deseja usar o banheiro");
        } else {
            System.out.println("Mulher " + nome + " deseja usar o banheiro");
        }
        while (!usou_banheiro) {
            try {
                entrando_banheiro();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
