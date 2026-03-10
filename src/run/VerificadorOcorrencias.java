package run;

import java.time.LocalDate;
import java.util.List;

import dao.api.IOcorrenciaDAO;
import model.Ocorrencia;

//Esta classe é uma tarefa que roda "escondida" em uma Thread separada.
//O trabalho dela é, de tempos em tempos, verificar se alguma ocorrência passou do prazo.
//Por rodar em paralelo, ela não trava o menu principal enquanto o usuário o utiliza.

public class VerificadorOcorrencias implements Runnable {

	// Precisa do DAO para poder conversar com o banco de dados.
    private final IOcorrenciaDAO ocorrenciaDAO;

    // Recebe o DAO pronto ao ser criada (Injeção de Dependência).
    public VerificadorOcorrencias(IOcorrenciaDAO ocorrenciaDAO) {
        this.ocorrenciaDAO = ocorrenciaDAO;
    }

    
    // O que estiver dentro deste método 'run' é o que a Thread vai executar em loop.
    @Override
    public void run() {
        System.out.println("[THREAD] Monitor de ocorrências iniciado em background.");
        while (true) {
            try {
            	
                // Faz a Thread "dormir" por 1 minuto.
                Thread.sleep(60000); 
                
                System.out.println("\n[THREAD] Verificando ocorrências vencidas...");

                // Busca as ocorrências e verifica uma por uma.
                List<Ocorrencia> todas = ocorrenciaDAO.buscarTodos();
                for (Ocorrencia o : todas) {
                    if ("Aberta".equalsIgnoreCase(o.getStatusDefinitivo()) && o.getDataLimiteSolucao().isBefore(LocalDate.now())) {
                        System.out.printf("[THREAD ALERTA]: A ocorrência N° %d está VENCIDA!%n", o.getNumero());
                    }
                }

            } catch (InterruptedException e) {
                // Se a aplicação principal for fechada, a thread para de forma limpa.
                System.out.println("[THREAD] Monitor de ocorrências finalizado.");
                Thread.currentThread().interrupt(); // Restaura o status de interrupção
                break;
            } catch (Exception e) {
                System.err.println("[THREAD ERRO]: Falha ao verificar ocorrências: " + e.getMessage());
            }
        }
    }
}