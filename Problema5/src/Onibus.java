import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.Semaphore;

public class Onibus extends Thread{
    int passageiros_transportados;
    int passageiros_na_parada;

    ReentrantLock onibus_passageiro_chegando;
    ReentrantLock passageiro_embarcando;
    Semaphore capacidade_onibus;

    public Onibus() {
        this.passageiros_transportados = 0;// contador de passageiros que conseguiram entra no onibus
        this.passageiros_na_parada = 0;// contador de passageiiros na parada

        this.onibus_passageiro_chegando = new ReentrantLock(); //para controlar a chegada dos passageiros
        this.passageiro_embarcando = new ReentrantLock();//para controlar o embarque dos passageiros
        this.capacidade_onibus = new Semaphore(0); // semaforo para controlar a capacidade do ônibus
    }

    public void run(){
        //loop principal do onibus, continua enquanto menos de 100 passageiros forem transportados
        while (passageiros_transportados < 100){
            onibus_passageiro_chegando.lock();// para sincronizar a chegada do onibus
            System.out.println("O onibus chegou na parada e está esperando os passageiros embarcarem");
            capacidade_onibus.release(50);
            boolean esperando_embarcarem = true;// usado para sincronizar o processo de embarque
            while (esperando_embarcarem){
                passageiro_embarcando.lock();
                if (passageiros_na_parada == 0 || capacidade_onibus.availablePermits() == 0){// quando nao tem mais passageiros na parada ou quando não há mais espaço no onibus
                    esperando_embarcarem = false;//
                    capacidade_onibus.drainPermits(); // esvazia o semaforo, no qual não é mais possível entrar no onibus
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
                onibus_passageiro_chegando.unlock(); // unlock para permitir que outros passageiros possam chegar enquanto o onibus está fora
                try {
                    sleep(((passageiros_transportados%3)+1)*1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                System.out.println("Fim do Expediente!");// quando todos os passageiros forem transportados
                onibus_passageiro_chegando.unlock();
            }
        }
    }
}
