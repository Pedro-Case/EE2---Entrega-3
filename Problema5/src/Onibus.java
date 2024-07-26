import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.Semaphore;

public class Onibus extends Thread{
    int passageiros_transportados;
    int passageiros_na_parada;

    ReentrantLock onibus_passageiro_chegando;
    ReentrantLock passageiro_embarcando;
    Semaphore capacidade_onibus;

    public Onibus() {
        this.passageiros_transportados = 0;
        this.passageiros_na_parada = 0;

        this.onibus_passageiro_chegando = new ReentrantLock();
        this.passageiro_embarcando = new ReentrantLock();
        this.capacidade_onibus = new Semaphore(0);
    }

    public void run(){
        while (passageiros_transportados < 100){
            onibus_passageiro_chegando.lock();
            System.out.println("O onibus chegou na parada e está esperando os passageiros embarcarem");
            capacidade_onibus.release(50);
            boolean esperando_embarcarem = true;
            while (esperando_embarcarem){
                passageiro_embarcando.lock();
                if (passageiros_na_parada == 0 || capacidade_onibus.availablePermits() == 0){
                    esperando_embarcarem = false;
                    capacidade_onibus.drainPermits();
                    if (passageiros_na_parada == 0){
                        System.out.println("Não há mais passageiros na parada");
                    } else{
                        System.out.println("Não há mais espaço no onibus, mas ainda ficaram " + passageiros_na_parada + " passageiros na parada");
                    }
                }
                passageiro_embarcando.unlock();
            }
            System.out.println("O onibus partiu");
            if (passageiros_transportados < 100){
                onibus_passageiro_chegando.unlock();
                try {
                    sleep(((passageiros_transportados%3)+1)*1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                System.out.println("Fim do Expediente!");
                onibus_passageiro_chegando.unlock();
            }
        }
    }
}
